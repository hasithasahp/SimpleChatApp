package SimpleChatApp.Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<String> userNames = new HashSet<>();
    private static Set<UserThread> userThreads = new HashSet<>();

    public static void main(String[] args) {
        int port = 5555;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, userNames, userThreads);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
