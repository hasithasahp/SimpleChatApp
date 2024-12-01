package SimpleChatApp.Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private Set<String> userNames;
    private Set<UserThread> userThreads;

    public UserThread(Socket socket, Set<String> userNames, Set<UserThread> userThreads) {
        this.socket = socket;
        this.userNames = userNames;
        this.userThreads = userThreads;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            userNames.add(userName);

            String serverMessage = "New user connected: " + userName;
            broadcast(serverMessage);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                broadcast(serverMessage);

            } while (!clientMessage.equals("bye"));

            removeUser(userName);
            socket.close();

            serverMessage = userName + " has quitted.";
            broadcast(serverMessage);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void printUsers() {
        if (userNames.isEmpty()) {
            writer.println("No other users connected");
        } else {
            writer.println("Connected users: " + userNames);
        }
    }

    void broadcast(String message) {
        for (UserThread aUser : userThreads) {
            if(aUser == this) continue;

            aUser.writer.println(message);
        }
    }

    void removeUser(String userName) {
        userNames.remove(userName);
        userThreads.remove(this);
    }
}
