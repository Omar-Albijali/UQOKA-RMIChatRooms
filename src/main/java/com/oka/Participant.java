package com.oka;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Participant implements IParticipant {

    private final String name;
    private final IChatRoom room;
    private final ChatWindow window;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
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
    public void onMessage(long date, String name, String message) throws RemoteException {
        if(name != null){
            window.print("["+timeFormated(date)+"] "+name+": "+message);
        }else {
            window.print("~------------ "+timeFormated(date)+" "+message+"--------------~");
        }
    }
    @Override
    public String getName() {
        return name;
    }


    private String timeFormated(long date){
        return dateFormat.format(new Date(date));
    }
}
