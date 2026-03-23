// ChatServer is the main server class responsible for:
// 1. Accepting client connections
// 2. Creating a ClientHandler for each client
// 3. Maintaining a list of all connected clients

package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    // This static list stores all active clients (ClientHandler objects)
    // It is shared across all ClientHandler instances
    public static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {

        try {

            // Create a server socket on port 6000
            // This acts as an entry point for all client connections
            ServerSocket serverSocket = new ServerSocket(6000);

            System.out.println("Server started...");
            System.out.println("Waiting for clients...");

            // Infinite loop to continuously accept new clients
            while (true) {

                // accept() waits until a client connects
                // Once a client connects, it returns a Socket object
                Socket socket = serverSocket.accept();

                // Create a new ClientHandler for the connected client
                // Each client gets its own thread
                ClientHandler handler = new ClientHandler(socket);

                // Start the thread (calls run() method internally)
                handler.start();
            }

        } catch (Exception e) {

            // If any error occurs, print it for debugging
            e.printStackTrace();
        }
    }
}