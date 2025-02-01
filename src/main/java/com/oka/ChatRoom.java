package com.oka;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements IChatRoom {

    private final String name;
    private final Chat chat = new Chat();
    private final ArrayList<IParticipant> participants = new ArrayList<>();

    public ChatRoom(String name) {
        this.name = name;
        initEvent();
    }

    private void flushChatToParticipant(IParticipant participant) throws RemoteException {
        for (Chat.Message cm : chat) {
            participant.onMessage(cm.getDate(), cm.getName(), cm.getMessage());
        }
    }

    private void broadcastMessage(Chat.Message cm) throws RemoteException {
        for (IParticipant participant : participants) {
            participant.onMessage(cm.getDate(), cm.getName(), cm.getMessage());
        }
        chat.add(cm);
    }

    private void initEvent(){
        chat.add(new Chat.Message(System.currentTimeMillis(), null, name+" Chat Room Was Created."));
    }
    private void joinEvent(IParticipant participant) throws RemoteException {
        broadcastMessage(new Chat.Message(
            System.currentTimeMillis(), null,
        participant.getName() +" has joined the chat"
        ));
    }

    private void leaveEvent(IParticipant participant) throws RemoteException {
        broadcastMessage(new Chat.Message(
            System.currentTimeMillis(), null,
            participant.getName() +" has left the chat"
        ));
    }

    public String getName() {
        return name;
    }

    @Override
    public void join(IParticipant pt) throws RemoteException {
        participants.add(pt); flushChatToParticipant(pt); joinEvent(pt);
    }

    @Override
    public void leave(IParticipant pt) throws RemoteException {
        participants.remove(pt); leaveEvent(pt);
    }


    @Override
    public void say(IParticipant pt, String message) throws RemoteException {
        if(participants.contains(pt)){
            broadcastMessage(new Chat.Message(
                System.currentTimeMillis(), pt.getName(), message
            ));
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
