package Client;

import java.io.*;   // For input-output streams
import java.net.*;  // For socket communication

// Client class responsible for connecting to server
public class ChatClient {

    private Socket socket;            // Socket connection to server
    private BufferedReader reader;    // To read messages from server
    private PrintWriter writer;       // To send messages to server

    // Constructor: connects to server and initializes streams
    public ChatClient(String username) {

        try {
            // Connect to server running on localhost at port 8000
            socket = new Socket("localhost", 8000);

            // Input stream (reading messages from server)
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Output stream (sending messages to server)
            writer = new PrintWriter(
                    socket.getOutputStream(),
                    true // AUTO FLUSH ensures message is sent immediately
            );

            // Send username to server as first message
            writer.println(username);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to send message to server
    public void sendMessage(String message) {

        // Debug print (can be removed later)
        System.out.println("SENDING: " + message);

        // Send message to server
        writer.println(message);
    }

    // Getter method to access reader (used by GUI to receive messages)
    public BufferedReader getReader() {
        return reader;
    }
}