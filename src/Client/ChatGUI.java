// ChatGUI is the main user interface of the chat application.

// Responsibilities:
// 1. Display user list
// 2. Display chat messages
// 3. Display activity (join/leave)
// 4. Allow sending private and broadcast messages
// 5. Handle user selection
// 6. Continuously receive messages from server using a separate thread

// Key Components:
// chatArea → displays messages
// activityArea → shows join/leave
// userList → shows online users
// messageField → input box

// Communication:
// Uses ChatClient to send messages to server
// Uses a background thread to receive messages
package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatGUI {

    Frame frame;

    List userList;//Stores UserList
    TextArea chatArea;//message area
    TextArea activityArea;//user joined user left

    TextField messageField;//user type message here
    Button sendButton;
    Button broadcastButton;

    String username;
    String selectedUser = null;//stores selected user from list used to send private message

    ChatClient client;

    public ChatGUI(String username, ChatClient client) {

        this.username = username;
        this.client = client;// used later to send message and display the name

        frame = new Frame("Real-Time Chat - User: " + username);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());//Divides frame into regions north/south/east/west/center

        // LEFT PANEL (USER LIST)
        Panel leftPanel = new Panel(new BorderLayout());
        Label userLabel = new Label("Users");
        userList = new List();//List is a predefined class in Java AWT
        leftPanel.add(userLabel, BorderLayout.NORTH);
        leftPanel.add(userList, BorderLayout.CENTER);

        // RIGHT PANEL
        Panel rightPanel = new Panel(new BorderLayout());

        // CHAT AREA
        chatArea = new TextArea();
        chatArea.setEditable(false);//There is textarea but user cannot write inside it False=Cant write True= Can Write

        // ACTIVITY AREA
        activityArea = new TextArea(5, 20);//Shows only 5 Clients then Scroll And Upto 20 characters in a line
        activityArea.setEditable(false);

        Panel bottomPanel = new Panel(new BorderLayout());//Divide screen into fixed regions

        messageField = new TextField();//Creates a single-line input box
        messageField.setText("");//Sets initial text to empty
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));//use new keyword Because Font is a class, not a simple value

        Panel buttonPanel = new Panel(new FlowLayout());//Arrange items in a line (left → right)


        sendButton = new Button("Send");//Send Button Private message
        broadcastButton = new Button("Send to All");//Broadcast Button Send To all

        buttonPanel.add(sendButton); //add Methods adds the button to panel
        buttonPanel.add(broadcastButton);

        bottomPanel.add(messageField, BorderLayout.CENTER);//“Add messageField in CENTER of bottomPanel”
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        rightPanel.add(chatArea, BorderLayout.CENTER);
        rightPanel.add(activityArea, BorderLayout.SOUTH);
        rightPanel.add(bottomPanel, BorderLayout.NORTH);

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);//If We used East rightPanel becomes VERY SMALL ❌

        frame.setVisible(true);

        // SELECT USER
        userList.addItemListener(e -> {
            selectedUser = userList.getSelectedItem();
        });

        // SEND PRIVATE MESSAGE
        sendButton.addActionListener(e -> {

            String msg = messageField.getText().trim();

            if (msg.isEmpty() || selectedUser == null) return;

            client.sendMessage("PRIVATE:" + selectedUser + ":" + msg);

            chatArea.append("Me → " + selectedUser + ": " + msg + "\n");

            messageField.setText("");// After sending message → clear field
        });

        // SEND BROADCAST
        broadcastButton.addActionListener(e -> {

            String msg = messageField.getText().trim();

            if (msg.isEmpty()) return;

            client.sendMessage(msg);

            chatArea.append("Me (All): " + msg + "\n");

            messageField.setText("");// After sending message → clear field
        });

        // ENTER KEY
        messageField.addActionListener(e -> sendButton.dispatchEvent(
                new ActionEvent(sendButton, ActionEvent.ACTION_PERFORMED, "Send")
        ));

        // WINDOW CLOSE → LOGOUT
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.sendMessage("logout");
                frame.dispose();
            }
        });

        // 🔥 RECEIVE MESSAGES THREAD (IMPORTANT PART) “Background me continuously server se messages suno”
        new Thread(() -> {
            try {
                String message;
                while ((message = client.getReader().readLine()) != null) {//“Jab tak server messages bhej raha hai → read karte raho”

                    // ADD USER
                    if (message.startsWith("USERLIST:")) {
                        String user = message.substring(9);
                        userList.add(user);
                    }

                    // 🔥 REMOVE USER
                    else if (message.startsWith("REMOVEUSER:")) {
                        String user = message.substring(11);
                        userList.remove(user);

                        if (selectedUser != null && selectedUser.equals(user)) {
                            selectedUser = null;
                            chatArea.setText("");
                        }
                    }

                    // ACTIVITY
                    else if (message.contains("joined") || message.contains("left")) {
                        activityArea.append(message + "\n");
                    }

                    // CHAT MESSAGE
                    else {
                        chatArea.append(message + "\n");
                    }
                }

            } catch (Exception e) {//“If something goes wrong → don’t crash the program”
                e.printStackTrace();//Prints error details in console//“Show me what went wrong”
            }
        }).start();//Continuously listening to server messages
    }
}