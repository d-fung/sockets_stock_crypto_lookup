import java.io.*;
import java.net.*;

public class Client {

    private static DataInputStream br;
    private static DataOutputStream dos;
    private static Socket socket;


    public static void main(String argv[]) throws Exception{
        Socket socket;

        int port = 3500;


        socket = new Socket("localhost", port);
        br = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        LoginPage login = new LoginPage(br, dos, socket);
    }


}
