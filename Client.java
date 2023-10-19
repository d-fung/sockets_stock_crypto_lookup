// David Fung 100767734
// October 19, 2023
// SOFE 4790U Assignment 1

import java.io.*;
import java.net.*;

public class Client {

    private static DataInputStream br;
    private static DataOutputStream dos;
    private static Socket socket;

    public static void main(String argv[]) throws Exception{
        
        // Connects to the server socket
        Socket socket;
        int port = 3500;
        socket = new Socket("localhost", port);
        br = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        // Opens the login GUI while passing in the br, dos, and socket information
        new LoginPage(br, dos, socket);
    }


}
