//ChatClient is responsible for connection to the server
//and sending/receving messages
package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    Socket socket;// Represents connection to server
    PrintWriter writer;//USed to send the message to server
    BufferedReader reader;// Used to receive the message from the server

    public ChatClient(String username) {

        try {
            //Connects to server at localhost on port 6000
            socket = new Socket("localhost", 6000);
            //Output Stream = Sending messages to server
            writer = new PrintWriter(socket.getOutputStream(), true);
            // Input stream → receiving messages from server
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            // Send username as first message (protocol)
            // Server expects first message to be username printed on terminal of server
            writer.println(username);// Sends username to the server

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
    // Method used by GUI to send message to server
    public void sendMessage(String msg) {

        writer.println(msg);

    }
    // Method used by GUI to receive messages from server
    public BufferedReader getReader() {

        return reader;

    }
}