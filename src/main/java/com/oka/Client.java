package com.oka;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {

    Registry registry;
    Server.Api api;
    Participant participant;

    String name;

    public Client(String name) {
        this.name = name;
    }

    public void connectTo(String host, int port) throws RemoteException, NotBoundException {
        registry = LocateRegistry.getRegistry(host, port);
        api = (Server.Api) registry.lookup("api");
    }

    private void listChatRooms(){
        try {
            List<String> rooms = api.getChatRoomNames();
            rooms.forEach(System.out::println);
        } catch (RemoteException e) {
            System.out.println("Server Error");
        }
    }

    private void joinChatRoom(String roomName) {

        if (participant != null){
            System.out.println("Already joined to a chat room.");
        }else {
            try {
                IChatRoom chatRoom = (IChatRoom) registry.lookup(roomName);
                participant = new Participant(name, chatRoom, roomName);
                participant.joinAndStart();
            } catch (RemoteException e) {
                System.out.println("Server error.");
            } catch (NotBoundException e) {
                System.out.println("Chat room not found.");;
            }
        }


    }

    private void leaveChatRoom(){
        if(participant != null){
            participant.leaveAndClose();
            participant = null;
        }
    }

    private void listMembers(String roomName) {
        try {
            IChatRoom chatRoom = (IChatRoom) registry.lookup(roomName);
            List<String> names = chatRoom.who();
            names.forEach(System.out::println);
        } catch (RemoteException e) {
            System.out.println("Server error.");
        } catch (NotBoundException e) {
            System.out.println("Chat room not found.");;
        }
    }

    private void sendToChatRoom(String message){
        if(participant != null){
            participant.sendMessage(message);
        }else {
            System.out.println("Not joined to any chat room.");
        }
    }



    public void startCommander(){
        System.out.println(
                "\nCommands and pattern :\n"
                        + "\trooms               #prints available chat rooms\n"
                        + "\tjoin [room name]    #join room command\n"
                        + "\tleave               #leave the joined room command\n"
                        + "\twho [room name]     #prints chat room members\n"
                        + "\tsend [message]      #sends a message to the joined room\n"
                        + "\tclose               #close this client\n"
        );
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running){
            System.out.print("---> ");
            String fullCommand = scanner.nextLine();
            String[] split = fullCommand.split(" ", 2);

            switch (split[0]){
                case "rooms": listChatRooms(); break;
                case "join" : joinChatRoom(split[1]); break;
                case "leave" : leaveChatRoom(); break;
                case "who" : listMembers(split[1]); break;
                case "send" : sendToChatRoom(split[1]); break;
                case "close" : leaveChatRoom();running = false;break;
                default: System.out.println("Unknown command");
            }
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        Scanner scanner = new Scanner(System.in);
        String host = "127.0.0.1";

        if(args.length > 0 && args[0].equals("lan")){
            System.out.print("Enter Server ip: ");
            host = scanner.nextLine();
        }

        System.out.print("Enter name : ");
        String name = scanner.nextLine();

        Client client = new Client(name);
        client.connectTo(host, Server.PORT);
        client.startCommander();

    }

}
