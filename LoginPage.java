import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class LoginPage implements ActionListener {
    private DataInputStream br;
    private DataOutputStream dos;
    private Socket socket;
    private JButton signUpButton;
    private JButton loginButton;
    private JPasswordField passwordText;
    private JTextField usernameText;
    private JLabel message;
    JFrame frame;

    public LoginPage(DataInputStream br, DataOutputStream dos, Socket socket){
        this.br = br;
        this.dos = dos;
        this.socket = socket;
        
        JPanel panel = new JPanel();
        frame = new JFrame();
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 20, 80, 25);
        panel.add(usernameLabel);

        usernameText = new JTextField(20);
        usernameText.setBounds(100, 20, 165, 25);
        panel.add(usernameText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);
        
        signUpButton = new JButton("Sign up");
        signUpButton.setBounds(100, 80, 80,25);
        signUpButton.addActionListener(this);
        panel.add(signUpButton);

        loginButton = new JButton("Login");
        loginButton.setBounds(185, 80, 80,25);
        loginButton.addActionListener(this);
        panel.add(loginButton);

        message = new JLabel("");
        message.setBounds(100, 110, 300, 25);
        panel.add(message);


        frame.setVisible(true);


    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    
        JButton actionSource = (JButton) actionEvent.getSource();

        // sign up and login common logic: check if empty and send username and password to server
        if(actionSource == signUpButton || actionSource == loginButton){
            String username = usernameText.getText();
            String password = passwordText.getText();

            System.out.println(username + ", " + password);

            if (username.isEmpty() || password.isEmpty()){
                message.setText("Username or password is empty");
                return;
            }

            // send the action request first and then send the username/password
            try {
                if(actionSource == signUpButton){
                    System.out.println("Sign up clicked");
                        dos.writeUTF("signUpRequest");
                        dos.flush();
                        
                
                } else if (actionSource == loginButton){
                    System.out.println("Login clicked");
                        dos.writeUTF("loginRequest");
                        dos.flush();
                }
                // send username and password to server
                dos.writeUTF(username);
                dos.flush();
                dos.writeUTF(password);
                dos.flush();
                // message gets sent back after login or signup
                String reply = br.readUTF();
                message.setText(reply);
                if (reply.equals("Login successful")){
                    try {
                        frame.dispose();
                        PriceLookupPage priceLookupPage = new PriceLookupPage(br, dos, socket);
                    } catch (Exception e) {
                        System.out.println("Error opening new GUI");
                    }
                }
                
                
            } catch (Exception e) {
                    message.setText("Error sending username or password");;
            }
        }
    }
}
