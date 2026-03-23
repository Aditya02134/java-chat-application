// LoginGUI is the entry screen of the chat application.

// Responsibilities:
// 1. Take username input from user
// 2. Validate input (non-empty)
// 3. Create ChatClient connection
// 4. Open ChatGUI window

// It acts as the starting point of the client-side application.
package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginGUI {

    Frame frame;
    TextField usernameField;
    Button loginButton;

    public LoginGUI() {

        frame = new Frame("Chat Application Login");
        frame.setSize(400,250);//Sets Window size
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);//opens window in the center

        // MAIN PANEL Gridlayout arranges things into rows and columns
        Panel mainPanel = new Panel(new GridLayout(5,1,10,10));//mainpanel ko 5 rows me divide kiya hai gridlayout ke help se
        mainPanel.setBackground(new Color(98, 136, 166));

        // TITLE
        Label title = new Label(" Chat Application", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // SUBTITLE
        Label subtitle = new Label("Enter your username", Label.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        // USERNAME FIELD variable declared to take username input but textfield here is the predefined (Java AWT class)
        usernameField = new TextField();
        usernameField.setBackground(new Color(0xB6B6C6));//Wrong ❌ → setBackground("blue") Correct ✅ → setBackground(new Color(...))
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));//It accepts font and color object not the name


        // LOGIN BUTTON
        loginButton = new Button("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0,150,136));
        loginButton.setForeground(Color.black);

        // FOOTER TEXT
        Label footer = new Label("Connect and Chat in Real-Time", Label.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));

        // ADD COMPONENTS
        mainPanel.add(title);
        mainPanel.add(subtitle);
        mainPanel.add(usernameField);
        mainPanel.add(loginButton);
        mainPanel.add(footer);

        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        // BUTTON ACTION
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText().trim();

                if(username.isEmpty()){

                    // simple validation
                    Dialog d = new Dialog(frame, "Error", true);
                    d.setLayout(new FlowLayout());
                    d.add(new Label("Username cannot be empty!"));
                    Button ok = new Button("OK");
                    ok.addActionListener(ev -> d.dispose());
                    d.add(ok);
                    d.setSize(250,100);
                    d.setLocationRelativeTo(frame);
                    d.setVisible(true);

                    return;
                }

                ChatClient client = new ChatClient(username);

                new ChatGUI(username, client);

                frame.dispose();

            }

        });

        // ENTER KEY SUPPORT
        usernameField.addActionListener(e -> loginButton.dispatchEvent(
                new ActionEvent(loginButton, ActionEvent.ACTION_PERFORMED, "Login")
        ));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}