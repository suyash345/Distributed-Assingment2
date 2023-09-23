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
public class testing {
    private static int my_time_Lamport = 0;
    private static Socket socket = null;
    private static BufferedReader bufferedReader = null;
    private static BufferedWriter bufferedWriter = null;
    public static void main(String[] args) {

        try{ // clears the data stored on the dataserver.
            FileWriter writer = new FileWriter("dataServer.json");
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        // Testing Put/Get Functions with numerous clients and contentServers
        System.out.println("NEW TEST Put Function\n\n");
        putFunction(0);
        System.out.println("NEW TEST Get Function\n\n");
        getFunction();
        System.out.println("NEW TEST Adding more data with Put Function\n\n");
        putFunction(1);
        System.out.println("NEW TEST Get function with more data\n\n");
        getFunction();

        System.out.println("\nTESTING LAMPORT NOW-----------------------------------------------------------------\n");
        //Lamport Testing
        my_time_Lamport = 0;
        System.out.println("Lamport time is set to 0");
        putFunction(0);

        my_time_Lamport = 10;
        System.out.println("\n\nLamport time is set to 10");
        putFunction(0);

        my_time_Lamport = 20;
        System.out.println("\n\nLamport time is set to 20");
        putFunction(0);


        my_time_Lamport = 5;
        System.out.println("\n\nLamport time is set to 5");
        putFunction(0);

    }

    public static void getFunction(){
        try {
            // connect to server and make the input and output obejcts.
            socket = new Socket("localhost", 4567);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send get request to Server
            String getRequest = makeGetRequest();
            String firstLine = getRequest.split("\n")[0];
            System.out.println("The first line of the get request is: " + firstLine)    ;  // This will print "Line 1

            bufferedWriter.write(getRequest);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // response from server
            String header = "";
            while((header = bufferedReader.readLine()) !=null) { // this reads until there is only JSON to be READ.
                if (header.isEmpty()) {
                    break;
                }
            }
            System.out.println("Success GET OPERATION. Response Body is here");
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


//    Put function here



    public static void putFunction(int test_num) {

        convertToJson(test_num);
        try {
            socket = new Socket("localhost", 4567);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String dataToSend = jsonObjectToString(test_num);
            String Put_Request = makePutRequest(dataToSend);
            String firstLine = Put_Request.split("\n")[0];
            System.out.println("The first line of request is: " + firstLine);  // This will print "Line 1
            // for the request
            incrementLamportTime(); //  should only have to increment once, since the PUT and body are in the same request
            System.out.println("Lamport time before sending is: " + Integer.toString(my_time_Lamport));
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

                //bufferedReader.readLine();
            }
            System.out.println("The response from the server is: "+response+"\n\n");
            String[] parts = response.split(" ");
            String lastLetter = parts[parts.length - 1];
            try{
                int time_received_from_server = Integer.parseInt(lastLetter);
                System.out.print("Time Received from Server is: "+ Integer.toString(time_received_from_server) + "\n");
                manageLamportTime(time_received_from_server);
                System.out.println("Lamport time after receiving is: " + Integer.toString(my_time_Lamport));

            } // have to find in response;
            catch (NumberFormatException e) {
                System.out.println("The string is not a valid integer.");
            }

        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
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

    public static void convertToJson(int number)
    {
        String fileContent = "";
        File reader;
        FileWriter writer;
        try {
            if(number ==0){
                reader = new File("inputfile.txt");
                writer = new FileWriter("input.json");}
            else{
                reader = new File("new_Input.txt");
                writer = new FileWriter("new_Input.json");}

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
    public static String jsonObjectToString(int num)
    {
        try{
            FileReader fileReader;
            if(num == 0){
                fileReader = new FileReader("input.json");}
            else {
                fileReader = new FileReader("new_Input.json");}

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