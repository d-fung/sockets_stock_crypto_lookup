import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

public class Server {
    ServerSocket serverSocket;
    Socket client;
    DataInputStream br;
    DataOutputStream dos;
    int port = 3500;
    static Dotenv dotenv = Dotenv.load();

    // Change this to your own RapidAPI-key, I removed my original key for personal security
    // You might also have to subscribe to the Twelve Data API on RapidAPI for it to work
    private final static String APIKEY = dotenv.get("API_KEY");
    private Map<String, String> userCredentials;



    public static void main(String argv[]) throws Exception {
        new Server("production");
    }

    public Server(String environmentType) throws Exception{
        userCredentials = new HashMap<>();

        // Unit tests were hanging due to creating a new socket for each test case too fast
        // Unit tests were also hanging due to the while loop waiting for clients
        // Solution is separate this block of code from production and deployment
        if (environmentType.equals("production")){        
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);
            while (true) {

                Socket client = serverSocket.accept();
                InetAddress clientAddress = client.getInetAddress();
                int clientPort = client.getPort();
                System.out.println("Accepted connection from: " + clientAddress.getHostAddress() + ":" + clientPort);
                new MyThread(client).start();

            }
            
        } else if (environmentType.equals("testing")){
            System.out.println("Currently in test mode");
        }

    }

    // Adds user to the userCredentials hashmap to store
    public void addUser(String username, String password){
        userCredentials.put(username, password);
        System.out.println("Succesfully added user: " + username);
    }

    // Checks if the username and password entered correctly matches an entry in the hashmap
    public boolean checkUser(String username, String password){
        System.out.println(username +", "+ password);
        return (userCredentials.containsKey(username) && userCredentials.get(username).equals(password));

    }

    // Sends an http request using Twelve Data's API and returns a real-time price quote for a stock
    public static String getStockPrice(String ticker){
        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=" + ticker + "&format=json&outputsize=30"))
		.header("X-RapidAPI-Key", APIKEY)
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

    // Sends an http request using Twelve Data's API and returns a real-time price quote for cryptocurrency
    public static String getCryptoPrice(String ticker){
        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=" + ticker + "%2FUSD&format=json&outputsize=30"))
		.header("X-RapidAPI-Key", APIKEY)
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

    // Parses the JSON object to a decimal
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

    // Thread logic
    private class MyThread extends Thread {
        private Socket clientSocket;

        // New thread gets created for every client
        public MyThread(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        public void run(){
            try {
                br = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
                
                // While the client is connected to the socket, keep listening for a request
                while(!clientSocket.isClosed()){

                    String request = br.readUTF();

                    // Handles the request using a switch statement to call the correct function
                    switch (request){
                        // Gets username and password and stores into the hashmap
                        case "signUpRequest": {
                            System.out.println("Sign up requested");
                            String username = br.readUTF();
                            String password = br.readUTF();
                            System.out.println("Received username: " + username + " and password: " + password);
                            addUser(username, password);
                            dos.writeUTF("Sign up successful");
                            break;
                        }
                        // Gets username and password and checks the hashmap for an existing entry
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

                        // Gets the stock price of requested ticker
                        case "stockPriceRequest": {
                            String ticker = br.readUTF();
                            System.out.println("Stock price requested: " + ticker);
                            String price = getStockPrice(ticker);
                            dos.writeUTF(price);
                            dos.flush();
                            break;
                        }

                        // Gets the cryptocurrency price of requested ticker
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
                // Close the client socket when client exists
                try {
                    clientSocket.close();

                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            
            
        }
    }
    
}