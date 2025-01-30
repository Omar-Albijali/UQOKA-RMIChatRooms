package com.oka;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {

    public static final int PORT = 1099;
    interface Api extends Remote {
        List<String> getChatRoomNames() throws RemoteException;
    }

    private Registry registry;

    private final ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private final Api api = () -> chatRooms.stream().map(c -> c.name)
            .collect(Collectors.toList());

    private void startOn(int port) throws RemoteException{
        registry = LocateRegistry.createRegistry(port);
        UnicastRemoteObject.exportObject(api, 0);
        registry.rebind("api", api);
    }
    public void exportChatRooms(String[] rooms){

        for (String room: rooms) {
            try {
                ChatRoom chatRoom = new ChatRoom(room);
                registry.rebind(room, UnicastRemoteObject.exportObject(chatRoom, 0));
                chatRooms.add(chatRoom);
            } catch (Exception e) {
                System.out.println("Failed to export room "+room);
            }
        }
    }

    public static void main(String[] args) throws RemoteException {
        String host = "127.0.0.1";

        if(args.length > 0 && args[0].equals("lan")){
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                host = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                System.out.println("Error No local host.");
            }
        }

        System.setProperty("java.rmi.server.hostname", host);

        Server server = new Server();
        server.startOn(PORT);

        System.out.println("Server running on "+host+":"+PORT);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Add chat rooms : ");
        String rooms = scanner.nextLine();
        String[] roomList = rooms.split(",");

        server.exportChatRooms(roomList);

    }

}
