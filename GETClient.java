import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class GETClient {
    public static void main(String[] args)
    {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            // connect to server and make the input and output obejcts.
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter URL (enter localhost for the Aggregation Server)");
            String servername = scanner.nextLine();
            System.out.println("Please input the port number");
            int port_number = scanner.nextInt();
            String url = "http://" + servername + ":" + port_number;
            System.out.println("going to: "+url);
            socket = new Socket(servername, port_number);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send get request to Server
            String getRequest = makeGetRequest();
            bufferedWriter.write(getRequest);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // response from server
            String header = "";
            while((header = bufferedReader.readLine()) !=null) { // this reads until there is only JSON to be READ.
                if (header.isEmpty()) {
                    break;
                }
                if(header.contains("404")){
                    System.out.println(header);
                    return;
                }
            }
            String line = bufferedReader.readLine();
            String[] splitLine = line.split("\\}");
            for(int i =0;i<splitLine.length;i++){
                System.out.println("\n");
                splitLine[i] = splitLine[i]+"}";
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(splitLine[i]).getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    System.out.println(key + ": " + jsonObject.get(key).getAsString());
                }

            }
            System.out.println("Sucess the function has run till the end!");
        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
    }
        public static void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
        {
            try{
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
                if(bufferedWriter!=null){
                    bufferedWriter.close();
                }
                if(socket!=null){
                    socket.close();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        public static String makeGetRequest(){
            String getRequest = "";
            getRequest += "GET /weather.json HTTP/1.1 \n";
            getRequest += "User-Agent: ATOMClient/1/0 \n";
            getRequest += "Accept: application/json \n";
            getRequest += "\n";

            return getRequest;
    }

    }