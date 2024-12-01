package SimpleChatApp.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class ChatServer {
    private static final Set<String> userNames = new HashSet<>();
    private static final Set<UserThread> userThreads = new HashSet<>();
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private static void init_logger() {
        try {
            Logger baseLogger = Logger.getLogger("");
            Handler[] handlers = baseLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    baseLogger.removeHandler(handler);
                }
            }

            FileHandler fileHandler = new FileHandler("logs\\app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.info("File Handler added to logger");
        } catch (IOException e) {
            System.out.println("Error occured in FileHandler: " + e.getMessage());
            logger.log(Level.SEVERE, "Error occured in FileHandler.", e);
        }
    }

    public static void main(String[] args) {
        int port = 5555;

        init_logger();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            String serverMsg = "Server Started! listening on PORT:" + port;
            logger.info(serverMsg);
            System.out.println(serverMsg);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("["+ socket.getInetAddress().getHostAddress() + ":" + socket.getPort() +"] New user connected");

                UserThread newUser = new UserThread(socket, userNames, userThreads, logger);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            System.out.println("IO Error Occured: " + ex.getMessage());
            logger.log(Level.SEVERE, "IO Error Occured", ex);
        }
    }
}
