package SimpleChatApp.Client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private String hostname = "localhost";
    private int port = 5555;
    private String userName;

    public ChatClient() {}

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
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
