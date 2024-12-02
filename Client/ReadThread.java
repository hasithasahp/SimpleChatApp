package SimpleChatApp.Client;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String response;
            while((response = reader.readLine()) != null) {
                System.out.println("\r"+response);
                System.out.print("R[ " + client.getUserName() + " ]: ");
            }
        } catch (IOException ex) {
            if(ex instanceof SocketException) {
                System.out.println("\nConnection Closed: " + ex.getMessage());
            } else {
                System.out.println("\nError reading from server: " + ex.getMessage());
            }
        }
    }
}
