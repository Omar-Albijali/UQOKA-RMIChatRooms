package com.oka;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IParticipant extends Remote {

    void onMessage(long date, String name, String message) throws RemoteException;

    String getName() throws RemoteException;

}
