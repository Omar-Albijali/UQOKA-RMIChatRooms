package com.oka;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Participant implements IParticipant {

    private final String name;
    private final IChatRoom room;
    private final ChatWindow window;
    Participant(String name, IChatRoom room, String roomName){
        this.name = name;
        this.room = room;
        this.window =  new ChatWindow(name+"-"+roomName);
    }

    public void joinAndStart() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            room.join(this);
            window.start();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Server Error While trying to Join");
        }
    }

    public void leaveAndClose(){
        try {
            room.leave(this);
            window.close();
        } catch (RemoteException e) {
            System.out.println("Server Error");
        }
    }

    public void sendMessage(String message){
        try {
            room.say(this, message);
        } catch (RemoteException e) {
            System.out.println("Server Error");
        }
    }

    @Override
    public void onNewMessage(String pctName, String message) throws RemoteException {
        window.print(pctName+": "+message);
    }

    @Override
    public String getName() {
        return name;
    }

}
