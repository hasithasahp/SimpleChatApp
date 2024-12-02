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
    private final Map<String, String> userCredentials;

    public UserThread(Socket socket, Set<String> userNames, Set<UserThread> userThreads, Logger logger, Map<String, String> userCredentials) {
        this.socket = socket;
        this.userNames = userNames;
        this.userThreads = userThreads;
        this.logger = logger;
        this.userCredentials = userCredentials;
    }

    @Override
    public void run() {
        String userName = "";
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            boolean authenticated = false;
            while(!authenticated) {
                writer.println("Enter your username: ");
                userName = reader.readLine().trim();
    
                writer.println("Enter your password: ");
                String password = reader.readLine().trim();
    
                if(!authenticate(userName, password)) {
                    writer.println("Authentication failed");
                } else {
                    writer.println("Authentication successful");
                    authenticated = true;
                }
            }

            synchronized (userName) {
                if (userNames.contains(userName)) {
                    writer.println("Username already taken. Connection closing.");
                    socket.close();

                    return;
                }

                userNames.add(userName);
            }

            printUsers();

            String serverMessage = "\nNew user connected: " + userName + "\n";
            broadcast(serverMessage);

            String clientMessage;
            do {
                clientMessage = reader.readLine();
                if(clientMessage == null) break; // client disconnected

                clientMessage = clientMessage.trim();
                if(!clientMessage.isEmpty()) {
                    serverMessage = "S[ " + userName + " ]: " + clientMessage;
                    broadcast(serverMessage);
                }
            } while (!clientMessage.equals("bye"));

            removeUser(userName);
        } catch (SocketException ex) {
            logger.log(Level.SEVERE, "Connection reset", ex);

            System.out.println("User "+ userName +" left");
            removeUser(userName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Error Occured", ex);
            System.out.println("IO Error Occured: " + ex.getMessage());
        } finally {
            if (!userName.isEmpty()) {
                removeUser(userName);
            }

            try {
                socket.close();
                System.out.println("Connection Closed to " + userName);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing socket", e);
            }
        }
    }

    private boolean authenticate(String userName, String password) {
        String storedPassword = userCredentials.get(userName);
        return storedPassword != null && storedPassword.equals(password);
    }

    private void printUsers() {
        if (userNames.isEmpty()) {
            writer.println("No other users connected");
        } else {
            writer.println("Connected users: " + userNames);
        }
    }

    private void broadcast(String message) {
        for (UserThread aUser : userThreads) {
            if(aUser == this) continue;

            aUser.writer.println(message);
        }
    }

    private void removeUser(String userName) {
        synchronized (userNames) {
            userNames.remove(userName);
        }
        userThreads.remove(this);
        broadcast(userName + " has left.");
    }
}
