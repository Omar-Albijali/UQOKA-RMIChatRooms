package com.oka;

import org.apache.commons.cli.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;

public class Client {

    private Registry registry;
    private Server.Api api;
    private Participant participant;
    private final String name;
    public Client(String name) {
        this.name = name;
    }

    public void connectTo(String host, int port) {
        try {
            registry = LocateRegistry.getRegistry(host, port);
            api = (Server.Api) registry.lookup("api");
        } catch (RemoteException e) {
            throw new RuntimeException("Error Server Unreachable.");
        } catch (NotBoundException e) {
            throw new RuntimeException("Error Api Not Found.");
        }
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
        while (true){
            System.out.print("---> ");
            String fullCommand = scanner.nextLine();
            String[] split = fullCommand.split(" ", 2);

            switch (split[0]){
                case "rooms": listChatRooms(); break;
                case "join" : joinChatRoom(split[1]); break;
                case "leave" : leaveChatRoom(); break;
                case "who" : listMembers(split[1]); break;
                case "send" : sendToChatRoom(split[1]); break;
                case "close" : leaveChatRoom();System.exit(0);break;
                default: System.out.println("Unknown command");
            }
        }
    }

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        options.addOption("h", "help", false, "Show help");
        options.addOption("i", "ip", true, "Server ip default is 127.0.0.1");
        options.addOption("p", "port", true, "Server port default is 1099");
        options.addOption("n", "name", true, "Client name");

        String ip = Server.DEFAULT_IP;
        int port = Server.DEFAULT_PORT;
        String name = "Client"+ LocalTime.now().getSecond();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar Client.jar [options]", options);
                return;
            }

            if (cmd.hasOption("i")) {
                ip = cmd.getOptionValue("i");
            }

            if (cmd.hasOption("p")) {
                String p = cmd.getOptionValue("p");
                try {
                    port= Integer.parseInt(p);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port.");
                }
            }

            if (cmd.hasOption("n")) {
                name = cmd.getOptionValue("n");
            }

        } catch (ParseException e) {
            System.out.println("Error parsing command-line arguments.");
        }


        Client client = new Client(name);
        client.connectTo(ip, port);
        client.startCommander();

    }

}
