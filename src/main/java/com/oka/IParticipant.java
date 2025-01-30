package com.oka;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IParticipant extends Remote {

    void onNewMessage(String pctName, String message) throws RemoteException;

    String getName() throws RemoteException;

}
