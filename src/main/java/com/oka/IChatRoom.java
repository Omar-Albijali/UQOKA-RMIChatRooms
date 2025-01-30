package com.oka;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatRoom extends Remote {

    void join(IParticipant pt) throws RemoteException;

    void leave(IParticipant pt) throws RemoteException;

    void say(IParticipant pt, String message) throws RemoteException;

    List<String> who() throws RemoteException;

}
