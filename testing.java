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
import java.net.SocketException;
import java.net.UnknownHostException;


public class testing {
    public static int my_time_Lamport = 0;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args)throws UnsupportedEncodingException {

        clearFile();

        System.out.println(ANSI_BLUE + "Testing HTTP Responses" + ANSI_RESET);
        getFunction("404"); // not found
        String wrongRequest = makeDelete(" ");
        Tester(wrongRequest,"400");
        String PutReq_IncorrectJson = makePutRequest("hello World");
        Tester(PutReq_IncorrectJson,"500");
        System.out.println(ANSI_CYAN+"Putting Data on the Aggregation Server to test for 200 response"+ANSI_RESET);
        putFunction(0,1);
        getFunction("200");

        System.out.println("\n" + ANSI_BLUE + "Testing Lamport Times" + ANSI_RESET);
        my_time_Lamport = 0;
        putFunction(0, 0);
        my_time_Lamport = 10;
        putFunction(0, 0);
        my_time_Lamport = 20;
        putFunction(0, 0);
        my_time_Lamport = 5;
        putFunction(0, 0);


        System.out.println("\n" + ANSI_BLUE + "Testing Multiple Content Servers" + ANSI_RESET);
        putFunction(0, 1);
        putFunction(0, 1);
        putFunction(0, 1);
        putFunction(0, 1);
        putFunction(0, 1);

        System.out.println("\n" + ANSI_BLUE + "Testing Multiple Clients" + ANSI_RESET);
        getFunction("200");
        getFunction("200");
        getFunction("200");
        getFunction("200");
        getFunction("200");


        System.out.println("\n" + ANSI_BLUE + "Testing Multiple Clients and ContentServers at once " + ANSI_RESET);
        int numberOfThreads = 5;

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if ((threadIndex % 2) == 0) {
                        getFunction("200");
                    } else {
                        putFunction(1, 1);
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        threads.clear();

        System.out.println(ANSI_GREEN + "Success! Every Thread has finished Execution! " + ANSI_RESET);

    }
    public static void Tester(String request,String Expected_response){
        try{
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        Socket socket = null;
        socket = new Socket("localhost", 4567);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        bufferedWriter.write(request);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        String header = "";
        header = bufferedReader.readLine();
        check(header,Expected_response);
        while((header = bufferedReader.readLine()) !=null) { // this reads until there is only JSON to be READ.
            if (header.isEmpty()) {
                break;
            }
        }
    }
    catch(IOException e){
            e.printStackTrace();
    }
    }

    public static void clearFile(){
        try { // clear file
            try (FileWriter fileWriter = new FileWriter("dataServer.json", false)) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getFunction(String request){
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        Socket socket = null;
        try {
            // connect to server and make the input and output obejcts.
            socket = new Socket("localhost", 4567);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send get request to Server
            String getRequest = makeGetRequest();
            String firstLine = getRequest.split("\n")[0];
            System.out.println(ANSI_YELLOW+"Running Get Function "+ANSI_RESET);

            bufferedWriter.write(getRequest);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String header = "";
            header = bufferedReader.readLine();
            check(header,request);
            while((header = bufferedReader.readLine()) !=null) { // this reads until there is only JSON to be READ.
                if (header.isEmpty()) {
                    break;
                }
            }
            String line = bufferedReader.readLine();
            String[] splitLine = line.split("\\}");

            if(!line.equals("")){
                for(int i =0;i<splitLine.length;i++){
                    splitLine[i] = splitLine[i]+"}";
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(splitLine[i]).getAsJsonObject();

            }

            }
            else {
                System.out.println(ANSI_GREEN+"Correct 404 Response! "+ANSI_RESET);
            }

        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }

    }



    public static void check(String line,String request){
        if(line.contains(request)){
            System.out.println(ANSI_GREEN+"Correct " +  request + " Response!"+ANSI_RESET);
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


    public static void putFunction(int test_num, int display_response) {
        convertToJson(test_num);
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        Socket socket = null;
        try {
            socket = new Socket("localhost", 4567);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String dataToSend = jsonObjectToString(test_num);
            String Put_Request = makePutRequest(dataToSend);
            String firstLine = Put_Request.split("\n")[0];
            // for the request
            incrementLamportTime(); //  should only have to increment once, since the PUT and body are in the same request

            bufferedWriter.write(Put_Request + "\n"+my_time_Lamport);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println(ANSI_YELLOW+"Running Put Function with lamport time "+Integer.toString(my_time_Lamport)+ANSI_RESET);
            // to read if the returned value from server is  sucessfull or not?
            String response ="";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                response += line;
                //System.out.println(line);
                //bufferedReader.readLine();
            }
            if(display_response==1){
                System.out.println("The response from the server is: "+response);
                }
            String[] parts = response.split(" ");
            String lastLetter = parts[parts.length - 1];

            try{
                int time_received_from_server = Integer.parseInt(lastLetter);
                manageLamportTime(time_received_from_server);
                if(display_response==0) {
                    System.out.print("Time Received from Server is: " + Integer.toString(time_received_from_server) + "\n");
                    System.out.println("Lamport time after receiving is: " + Integer.toString(my_time_Lamport));
                }
                if(time_received_from_server>=my_time_Lamport-1 && display_response==0){
                    System.out.println(ANSI_GREEN + "Success" +ANSI_RESET);
                }

            } // have to find in response;
            catch (NumberFormatException e) {
                System.out.println("The string is not a valid integer.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized void manageLamportTime(int time_received_from_server) // manage when receving
    {
        if (time_received_from_server > my_time_Lamport) {
            my_time_Lamport = time_received_from_server + 1;
        } else {
            incrementLamportTime();
        }
    }

    public static synchronized void incrementLamportTime() {
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


    public static String makeDelete(String data) throws UnsupportedEncodingException{
        String putRequest = "";
        putRequest += "Delete /weather.json HTTP/1.1 \n";
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