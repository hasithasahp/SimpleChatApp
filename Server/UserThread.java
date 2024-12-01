package SimpleChatApp.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class UserThread extends Thread {
    private Socket socket;
    private PrintWriter writer;
    private final Set<String> userNames;
    private final Set<UserThread> userThreads;
    private final Logger logger;

    public UserThread(Socket socket, Set<String> userNames, Set<UserThread> userThreads, Logger logger) {
        this.socket = socket;
        this.userNames = userNames;
        this.userThreads = userThreads;
        this.logger = logger;
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
            logger.log(Level.SEVERE, "IO Error Occured", ex);
            System.out.println("IO Error Occured: " + ex.getMessage());
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
