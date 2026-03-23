//// ClientHandler handles communication for a single client.
//// Each client is assigned a separate thread.
//
/// / Responsibilities:
/// / 1. Receive messages from client
/// / 2. Identify message type (private / broadcast / logout)
/// / 3. Send messages to appropriate clients
/// / 4. Maintain user list and notify others about join/leave
//
//// Uses:
//// Socket → connection
//// BufferedReader → input (client → server)
//// PrintWriter → output (server → client)
package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    Socket socket;// Client Connection “Kaunsa client connected hai”
    PrintWriter writer;// Server → client message bhejne ke liye usage (writer.println(message);)
    BufferedReader reader;// Client → server message receive karne ke liye usage(reader.readLine();)

    String username;//Stores Clients Name (broadcast(username + ": " + msg);sendPrivateMessage(username, ...))
//Client (ChatClient)
//   ↓ sends message
//Socket
//   ↓
//BufferedReader (server reads)
//   ↓
//ClientHandler processes
//   ↓
//PrintWriter (server sends back)
    public ClientHandler(Socket socket) {//This constructor runs separately for EACH client
        this.socket = socket;//“Store THIS client’s socket inside THIS object”without this ClientHandler will not know which client it is handling ❌
    }
//ChtServer Code Socket socket = serverSocket.accept();
//
//ClientHandler handler = new ClientHandler(socket);
//
//handler.start();
    public void run() {

        try {

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            writer = new PrintWriter(socket.getOutputStream(), true);

            // first message = username
            username = reader.readLine();//Conncected with Chatclient writer.println(username);

            System.out.println(username + " connected");

            // send existing users to new client
            for (ClientHandler client : ChatServer.clients) {//Chatserver.Clients=Array List of All active users (ClientHandler objects)
                writer.println("USERLIST:" + client.username);//USERLIST:U2
            }

            // add this client
            ChatServer.clients.add(this);//“Is new client ko list me add karo”,Chat Server=public static ArrayList<ClientHandler> clients

            // notify all users
            broadcast("USERLIST:" + username);
            broadcast(username + " joined the chat");

            String message;

            while ((message = reader.readLine()) != null) {

                // LOGOUT
                if (message.trim().equalsIgnoreCase("logout")) {

                    ChatServer.clients.remove(this);

                    // 🔥 REMOVE USER FROM LIST
                    broadcast("REMOVEUSER:" + username);

                    broadcast(username + " left the chat");
                    break;
                }

                // PRIVATE MESSAGE
                if (message.startsWith("PRIVATE:")) {

                    String parts[] = message.split(":", 3);

                    String targetUser = parts[1];
                    String msg = parts[2];

                    sendPrivateMessage(targetUser, username + " → you: " + msg);
                }

                // BROADCAST MESSAGE
                else {
                    broadcast(username + " (All): " + message);
                }
            }

        } catch (Exception e) {
            System.out.println(username + " disconnected");
        }
    }

    public void sendPrivateMessage(String targetUser, String message) {

        for (ClientHandler client : ChatServer.clients) {

            if (client.username.equals(targetUser)) {
                client.writer.println(message);
            }
        }
    }

    public void broadcast(String message) {

        for (ClientHandler client : ChatServer.clients) {
            client.writer.println(message);
        }
    }
}