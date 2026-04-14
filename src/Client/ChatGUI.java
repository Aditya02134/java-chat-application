package Client;

import javax.swing.*;                    // Swing components (GUI)
import javax.swing.border.EmptyBorder; // For padding around components
import java.awt.*;                      // Layouts, colors, fonts
import java.awt.event.*;               // Event handling

public class ChatGUI {

    // Main window frame
    JFrame frame;

    // Model + UI list to display users
    DefaultListModel<String> userModel;
    JList<String> userList;

    // Panel where chat bubbles are added
    JPanel chatPanel;
    JScrollPane chatScroll;

    // Activity area for join/leave messages
    JTextArea activityArea;

    // Input components
    JTextField messageField;
    JButton sendButton, broadcastButton, clearButton;

    // Label to show typing indicator
    JLabel typingLabel;

    // Current user info
    String username;
    String selectedUser = null;

    // Client object to communicate with server
    ChatClient client;

    // Constructor
    public ChatGUI(String username, ChatClient client) {

        this.username = username;
        this.client = client;

        // ================= FRAME SETUP =================
        frame = new JFrame("Chat - " + username);
        frame.setSize(750, 550);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= LEFT USER LIST =================
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);

        // Set width of user list panel
        userList.setPreferredSize(new Dimension(120, 0));

        frame.add(new JScrollPane(userList), BorderLayout.WEST);

        // ================= CHAT PANEL =================
        chatPanel = new JPanel();

        // Vertical layout for stacking messages
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        chatScroll = new JScrollPane(chatPanel);
        frame.add(chatScroll, BorderLayout.CENTER);

        // ================= ACTIVITY AREA =================
        activityArea = new JTextArea(4, 20);
        activityArea.setEditable(false);

        // Light background for distinction
        activityArea.setBackground(new Color(245,245,245));

        JScrollPane activityScroll = new JScrollPane(activityArea);
        frame.add(activityScroll, BorderLayout.SOUTH);

        // ================= INPUT PANEL =================
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Message input field
        messageField = new JTextField();

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        sendButton = new JButton("Send");           // Private message
        broadcastButton = new JButton("Send All");  // Broadcast message
        clearButton = new JButton("Clear");         // Clear chat

        buttonPanel.add(sendButton);
        buttonPanel.add(broadcastButton);
        buttonPanel.add(clearButton);

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Typing indicator label
        typingLabel = new JLabel(" ");
        typingLabel.setForeground(Color.GRAY);

        bottomPanel.add(typingLabel, BorderLayout.SOUTH);

        // Add input panel at top
        frame.add(bottomPanel, BorderLayout.NORTH);

        frame.setVisible(true);

        // ================= EVENTS =================

        // When user selects another user from list
        userList.addListSelectionListener(e -> {
            selectedUser = userList.getSelectedValue();
        });

        // Send private message
        sendButton.addActionListener(e -> sendMessage());

        // Send broadcast message
        broadcastButton.addActionListener(e -> {
            String msg = messageField.getText().trim();

            if (!msg.isEmpty()) {
                client.sendMessage(msg);
                messageField.setText("");
            }
        });

        // Clear chat area
        clearButton.addActionListener(e -> {
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint();
        });

        // ================= TYPING INDICATOR =================

        // Timer to avoid sending typing message continuously
        Timer typingTimer = new Timer(500, e -> {
            client.sendMessage("TYPING:" + username);
        });
        typingTimer.setRepeats(false);

        // Detect typing in text field
        messageField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                typingTimer.restart();
            }
        });

        // ================= RECEIVE THREAD =================

        // Separate thread to receive messages from server
        new Thread(() -> {
            try {
                String message;

                while ((message = client.getReader().readLine()) != null) {

                    // USER LIST UPDATE
                    if (message.startsWith("USERLIST:")) {

                        String user = message.substring(9);

                        // Avoid duplicate entries
                        if (!userModel.contains(user)) {
                            userModel.addElement(user);
                        }
                    }

                    // REMOVE USER
                    else if (message.startsWith("REMOVEUSER:")) {
                        userModel.removeElement(message.substring(11));
                    }

                    // ================= ACTIVITY (JOIN/LEAVE) =================
                    else if (message.contains("joined") || message.contains("left")) {
                        activityArea.append(message + "\n");
                    }

                    // ================= TYPING INDICATOR =================
                    else if (message.startsWith("TYPING:")) {

                        String user = message.substring(7);

                        if (!user.equals(username)) {

                            typingLabel.setText(user + " is typing...");

                            // Clear after short delay
                            new Timer(800, e -> typingLabel.setText("")).start();
                        }
                    }

                    // ================= CHAT MESSAGE =================
                    else {
                        addBubble(message);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ================= SEND PRIVATE MESSAGE =================
    private void sendMessage() {

        String msg = messageField.getText().trim();

        // Validation: no empty message or no user selected
        if (msg.isEmpty() || selectedUser == null) return;

        // Send in PRIVATE format
        client.sendMessage("PRIVATE:" + selectedUser + ":" + msg);

        messageField.setText("");
    }

    // ================= CHAT BUBBLE =================
    private void addBubble(String message) {

        // Wrapper panel for alignment
        JPanel wrapper = new JPanel(new FlowLayout(
                message.startsWith(username) ? FlowLayout.RIGHT : FlowLayout.LEFT
        ));

        wrapper.setOpaque(false);

        // Label to display message
        JLabel label = new JLabel("<html>" + message + "</html>");
        label.setOpaque(true);

        // Padding inside bubble
        label.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Limit bubble width (important for UI)
        label.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));

        // Color based on sender
        if (message.startsWith(username)) {
            label.setBackground(new Color(180, 255, 180)); // green (your message)
        } else {
            label.setBackground(new Color(230, 230, 230)); // gray (others)
        }

        wrapper.add(label);

        chatPanel.add(wrapper);
        chatPanel.revalidate();

        autoScroll();
    }

    // ================= AUTO SCROLL =================
    private void autoScroll() {

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScroll.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}