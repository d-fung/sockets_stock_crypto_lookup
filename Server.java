import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class Server {
    ServerSocket serverSocket;
    Socket client;
    DataInputStream br;
    DataOutputStream dos;
    int port = 3500;

    private Map<String, String> userCredentials;



    public static void main(String argv[]) throws Exception {
        new Server();
    }

    public Server() throws Exception{
        userCredentials = new HashMap<>();
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port: " + port);

        while (true) {

            Socket client = serverSocket.accept();
            InetAddress clientAddress = client.getInetAddress();
            int clientPort = client.getPort();
            System.out.println("Accepted connection from: " + clientAddress.getHostAddress() + ":" + clientPort);
            new MyThread(client).start();

        }

    }

    public void addUser(String username, String password){
        userCredentials.put(username, password);
        System.out.println("Succesfully added user: " + username);
    }

    public boolean checkUser(String username, String password){
        System.out.println(username +", "+ password);
        return (userCredentials.containsKey(username) && userCredentials.get(username).equals(password));

    }

    public static String getStockPrice(String ticker){
        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=" + ticker + "&format=json&outputsize=30"))
		.header("X-RapidAPI-Key", "8c10888507mshd2946d158701951p133944jsnab1ea55d7159")
		.header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
		.method("GET", HttpRequest.BodyPublishers.noBody())
		.build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return convertJSONtoDecimal(response);


        } catch (Exception e){
            e.printStackTrace();
            return new String("error");
        }
    }

    public static String getCryptoPrice(String ticker){
        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=" + ticker + "%2FUSD&format=json&outputsize=30"))
		.header("X-RapidAPI-Key", "8c10888507mshd2946d158701951p133944jsnab1ea55d7159")
		.header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
		.method("GET", HttpRequest.BodyPublishers.noBody())
		.build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return convertJSONtoDecimal(response);

        } catch (Exception e){
            e.printStackTrace();
            return new String("error");

        }

    }

    public static String convertJSONtoDecimal(HttpResponse<String> response){
            JSONObject jsonObject = new JSONObject(response.body());

            if (jsonObject.has("price")){
                
                String priceString = jsonObject.getString("price");
                double priceDouble = Double.parseDouble(priceString);
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String formattedPrice = decimalFormat.format(priceDouble);
                return formattedPrice;
            }
            else return new String("Unable to find.");
    }

    private class MyThread extends Thread {
        private Socket clientSocket;

        public MyThread(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        public void run(){
            try {
                br = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
                while(!clientSocket.isClosed()){

                    String request = br.readUTF();

                    switch (request){
                        case "signUpRequest": {
                            System.out.println("Sign up requested");
                            String username = br.readUTF();
                            String password = br.readUTF();
                            System.out.println("Received username: " + username + " and password: " + password);
                            addUser(username, password);
                            dos.writeUTF("Sign up successful");
                            break;
                        }
                        case "loginRequest": {
                            System.out.println("Login requested");
                            String username = br.readUTF();
                            String password = br.readUTF();
                            System.out.println("Received username: " + username + " and password: " + password);
                            if (checkUser(username, password)) {
                                dos.writeUTF("Login successful");
                            } else {
                                dos.writeUTF("Login unsuccessful");
                            }
                            break;
                        }

                        case "stockPriceRequest": {
                            String ticker = br.readUTF();
                            System.out.println("Stock price requested: " + ticker);
                            String price = getStockPrice(ticker);
                            dos.writeUTF(price);
                            dos.flush();
                            break;
                        }

                        case "cryptoPriceRequest": {
                            String ticker = br.readUTF();
                            System.out.println("Crypto price requested: " + ticker);
                            String price = getCryptoPrice(ticker);
                            dos.writeUTF(price);
                            dos.flush();
                            break;
                        }

                    }

                }

            } catch (IOException e){
                System.out.println("Connection closed");
                //e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();

                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            
            
        }
    }
    
}