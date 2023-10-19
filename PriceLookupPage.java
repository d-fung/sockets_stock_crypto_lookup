// David Fung 100767734
// October 19, 2023
// SOFE 4790U Assignment 1

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class PriceLookupPage implements ActionListener{
    // Initializes GUI elements to be used later
    private DataInputStream br;
    private DataOutputStream dos;
    private Socket socket;

    private JLabel stockLabel;
    private JTextField stockTickerText;
    private JLabel cryptoLabel;
    private JTextField cryptoTickerText;
    private JLabel helperMessage;
    private JLabel message;
    private JButton getStockPrice;
    private JButton getCryptoPrice;


    public PriceLookupPage(DataInputStream br, DataOutputStream dos, Socket socket){
        this.br = br;
        this.dos = dos;
        this.socket = socket;

        // This block of code contains the dimensions and logic for presenting the GUI elements and components
        JPanel panel = new JPanel();
        JFrame frame = new JFrame();
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        panel.setLayout(null);

        helperMessage = new JLabel("Stock & Crypto Price Lookup App");
        helperMessage.setBounds(10, 10, 200, 25);
        panel.add(helperMessage);

        stockLabel = new JLabel("Enter stock ticker: ");
        stockLabel.setBounds(10, 50, 150, 25);
        panel.add(stockLabel);

        stockTickerText = new JTextField(10);
        stockTickerText.setBounds(140, 50, 50, 25);
        panel.add(stockTickerText);

        getStockPrice = new JButton("Get Stock Price");
        getStockPrice.setBounds(190, 50, 130, 25);
        getStockPrice.addActionListener(this);
        panel.add(getStockPrice);


        cryptoLabel = new JLabel("Enter crypto ticker: ");
        cryptoLabel.setBounds(10, 80, 150, 25);
        panel.add(cryptoLabel);

        cryptoTickerText = new JTextField(10);
        cryptoTickerText.setBounds(140, 80, 50, 25);
        panel.add(cryptoTickerText);

        getCryptoPrice = new JButton("Get Crypto Price");
        getCryptoPrice.setBounds(190, 80, 130, 25);
        getCryptoPrice.addActionListener(this);
        panel.add(getCryptoPrice);

        message = new JLabel("");
        message.setBounds(10, 130, 280, 25);
        panel.add(message);

        frame.setVisible(true);
    }

    // On-click listener for the two buttons to get stock and crypto prices
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton actionSource = (JButton) actionEvent.getSource();

        String stock = stockTickerText.getText();
        String crypto = cryptoTickerText.getText();

        try {
            
            // If the getStockPrice button was clicked, then send in the request as a UTF message
            // which will be handled by the server
            if (actionSource == getStockPrice){
                if (stock.isEmpty()){
                    message.setText("Please enter a stock ticker");
                    return;
                }

                dos.writeUTF("stockPriceRequest");
                dos.flush();
                dos.writeUTF(stock);
                dos.flush();
                String reply = br.readUTF();
                if (!reply.equals("Unable to find.")){
                    message.setText("The price of stock " + stock + " is: " + reply + " USD.");
                } else {
                    message.setText(reply);
                }
            }

            // If the getCryptoPrice button was clicked, then send in the request as a UTF message
            // which will be handled by the server
            if (actionSource == getCryptoPrice){
                if(crypto.isEmpty()){
                    message.setText("Please enter a crypto ticker");
                    return;
                }

                dos.writeUTF("cryptoPriceRequest");
                dos.flush();
                dos.writeUTF(crypto);
                dos.flush();
                String reply = br.readUTF();
                if (!reply.equals("Unable to find.")){
                    message.setText("The price of crypto " + crypto + " is: " + reply + " USD.");
                } else {
                    message.setText(reply);
                }

        }
        } catch (Exception e) {
            message.setText("Error requesting prices");
        }

    }
}
