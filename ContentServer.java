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
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;
import com.google.gson.JsonParser;
import java.io.UnsupportedEncodingException;
public class ContentServer {
    private static int my_time_Lamport = 0;

    public static void main(String[] args)
    {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        convertToJson();
            try {
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
                String dataToSend = jsonObjectToString();
                String Put_Request = makePutRequest(dataToSend);
                System.out.println(Put_Request);
                // for the request
                incrementLamportTime(); //  should only have to increment once, since the PUT and body are in the same request
                bufferedWriter.write(Put_Request + "\n"+my_time_Lamport);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                // to read if the returned value from server is  sucessfull or not?

                String response ="";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.isEmpty()) {
                        break;
                    }
                    response += line;
                    System.out.println(line);
                    //bufferedReader.readLine();
                }
                String lastLetter = response.substring(response.length() - 1);
                try{
                    int time_received_from_server = Integer.parseInt(lastLetter);
                    manageLamportTime(time_received_from_server);
                    } // have to find in response;
                catch (NumberFormatException e) {
                    System.out.println("The string is not a valid integer.");
                }



            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
            }
        }


    public static void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void manageLamportTime(int time_received_from_server) // manage when receving
    {
        if (time_received_from_server > my_time_Lamport) {
            my_time_Lamport = time_received_from_server + 1;
        } else {
            incrementLamportTime();
        }
    }

    public static void incrementLamportTime() {
        my_time_Lamport = my_time_Lamport + 1;
    }

    public static void convertToJson()
    {
        String fileContent = "";
        try {
            File reader = new File("inputfile.txt");
            FileWriter writer = new FileWriter("input.json");
            writer.write("{\n");
            Scanner myReader = new Scanner(reader);
            ArrayList<String> jsonLines = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] parts = data.split(":");
                jsonLines.add("\"" + parts[0].trim() + "\":\"" + parts[1].trim() + "\"");
            }
            writer.write(String.join(",\n", jsonLines));
            writer.write("\n}");
            myReader.close();
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String jsonObjectToString()
    {
        try{
            FileReader fileReader = new FileReader("input.json");
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(fileReader,JsonObject.class);
            return jsonObject.toString();
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
    public static String makePutRequest(String data) throws UnsupportedEncodingException{
        String putRequest = "";
        putRequest += "PUT /weather.json HTTP/1.1 \n";
        putRequest += "User-Agent: ATOMClient/1/0 \n";
        putRequest += "Content-Type: application/json \n";
        int content_length;
        byte[] bytes_data = data.getBytes("UTF-8");
        content_length = bytes_data.length;
        putRequest += "Content-Length:" + content_length +"\n";
        putRequest +=  "\n";
        putRequest += data;
        return putRequest;
    }


}