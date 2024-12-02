package SimpleChatApp.Client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private String hostname = "localhost";
    private int port = 5555;
    private String userName;
    private boolean authenticated = false;

    public ChatClient() {}

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            // Create a writer to send authentication details
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Authenticate user
            Console console = System.console();
            String response;
            boolean authenticated = false;
            while(!authenticated) {
                System.out.println(reader.readLine()); // "Enter your username: "
                String userName = console.readLine().trim();
                writer.println(userName);

                System.out.println(reader.readLine()); // "Enter your password: "
                String password = console.readLine().trim();
                writer.println(password);

                response = reader.readLine();
                if ("Authentication failed".equals(response)) {
                    System.out.println("Authentication failed. Please try again.");
                    socket.close();
                    return;
                } else if ("Authentication successful".equals(response)) {
                    System.out.println("Authentication successful. Welcome!");
                    this.userName = userName;
                    authenticated = true;
                }
            }
            
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    boolean isAuthenticated() {
        return this.authenticated;
    }

    void setAuthenticated() {
        this.authenticated = true;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {
        ChatClient client;
        if (args.length < 2) {
            client = new ChatClient();
            client.execute();

            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        client = new ChatClient(hostname, port);
        client.execute();
    }
}
