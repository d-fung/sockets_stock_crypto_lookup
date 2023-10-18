import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.*;
import java.text.DecimalFormat;


public class test {
    
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
            else return new String("Unable to find");
    }

    public static void main(String argv[]) throws Exception {
        System.out.println(getStockPrice("asda")); // should return unable to find
        System.out.println(getCryptoPrice("asdsa")); // should return unable to find

        System.out.println(getStockPrice("AAPL")); // should return correct price
        System.out.println(getCryptoPrice("btc")); // should return correct price
    }
}
