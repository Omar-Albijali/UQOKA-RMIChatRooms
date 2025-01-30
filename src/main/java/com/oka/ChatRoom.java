package com.oka;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements IChatRoom {

    String name;

    public ChatRoom(String name) {
        this.name = name;
    }

    static class ChatMessage{
        String message;
        String pctName;

        public ChatMessage(String message, String pctName) {
            this.message = message;
            this.pctName = pctName;
        }
    }
    ArrayList<IParticipant> participants = new ArrayList<>();
    ArrayList<ChatMessage> chat = new ArrayList<>();

    @Override
    public void join(IParticipant pt) throws RemoteException {
        participants.add(pt);
        for (ChatMessage cm : chat) {
            pt.onNewMessage(cm.pctName, cm.message);
        }
    }

    @Override
    public void leave(IParticipant pt) throws RemoteException {
        participants.remove(pt);
    }


    @Override
    public void say(IParticipant pt, String message) throws RemoteException {
        if(participants.contains(pt)){
            ChatMessage cm = new ChatMessage(message, pt.getName());
            for (IParticipant pat : participants){
                pat.onNewMessage(cm.pctName, message);
            }
            chat.add(cm);
        }
    }

    @Override
    public List<String> who() throws RemoteException {
        ArrayList<String> names = new ArrayList<>();
        for (IParticipant pt : participants) {
            names.add(pt.getName());
        }
        return names;
    }
}
