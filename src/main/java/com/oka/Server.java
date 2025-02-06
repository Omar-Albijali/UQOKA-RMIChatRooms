package com.oka;

import org.apache.commons.cli.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Server {

    public static final String DEFAULT_IP = "127.0.0.1";
    public static final int DEFAULT_PORT = 1099;
    interface Api extends Remote {
        List<String> getChatRoomNames() throws RemoteException;
    }

    private Registry registry;

    private final ArrayList<ChatRoom> chatRooms = new ArrayList<>();

    private final Api api = () -> chatRooms.stream()
            .map(ChatRoom::getName)
            .collect(Collectors.toList());


    private void startOn(String ip, int port) {
        try {
            System.setProperty("java.rmi.server.hostname", ip);
            registry = LocateRegistry.createRegistry(port);
            UnicastRemoteObject.exportObject(api, 0);
            registry.rebind("api", api);
            System.out.println("Server running on "+ ip+":"+port);
        } catch (RemoteException e) {
            throw new RuntimeException("Error could not start the Server.");
        }
    }
    public void exportChatRooms(String[] rooms){

        for (String room: rooms) {
            try {
                ChatRoom chatRoom = new ChatRoom(room);
                registry.rebind(room, UnicastRemoteObject.exportObject(chatRoom, 0));
                chatRooms.add(chatRoom);
                System.out.println("Room "+room+" has exported.");
            } catch (Exception e) {
                System.out.println("Failed to export room "+room);
            }
        }
    }

    private static void waitForEnterToClose(){
        System.out.print("\nPress enter to end the server...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.exit(0);
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption("h", "help", false, "Show help");
        options.addOption("i", "ip", true, "Server ip default is 127.0.0.1");
        options.addOption("p", "port", true, "Server port default is 1099");

        Option roomsOption = new Option(
                "r", "rooms", true,
                "Server rooms default is R1, R2, R3"
        );

        roomsOption.setArgs(Option.UNLIMITED_VALUES);
        roomsOption.setValueSeparator(',');

        options.addOption(roomsOption);
        return options;
    }


    public static void main(String[] args)  {

        CommandLineParser parser = new DefaultParser();

        Options options = getOptions();

        String ip = DEFAULT_IP;
        int port = DEFAULT_PORT;
        String[] rooms = {"R1", "R2", "R3"};

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar Server.jar [options]", options);
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

            if (cmd.hasOption("r")) {
                rooms = cmd.getOptionValues("r");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing command-line arguments.");
            return;
        }

        Server server = new Server();
        server.startOn(ip, port);
        server.exportChatRooms(rooms);

        waitForEnterToClose();
    }


}
