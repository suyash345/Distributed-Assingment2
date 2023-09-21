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
public class ContentServer {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4567;
    private static int my_time_Lamport = 0;

    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        convertToJson();
        while (true) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Scanner scanner = new Scanner(System.in);
                String dataToSend = jsonObjectToString();
                System.out.println(dataToSend);


                // for the request
                incrementLamportTime(); //  should only have to increment once, since the PUT and body are in the same request
                System.out.println(my_time_Lamport);
                bufferedWriter.write("PUT 0");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                // for the content
                bufferedWriter.write(dataToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                // to read if the returned value from server is  sucessfull or not?
                String line = bufferedReader.readLine();
                System.out.println(line);
                int time_received_from_server = 2; // have to find in response;
                manageLamportTime(time_received_from_server);

                //user input to continue with connections with server.
                System.out.println("Do you want to send again? (Y/N)");
                String send_again = scanner.nextLine();
                if (send_again.equals("Y") || send_again.toUpperCase().equals("Y")) {
                    continue;
                } else {
                    System.out.println("Exiting Content Server");
                    break;
                }

            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public static void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void manageLamportTime(int time_received_from_server) {
        if (time_received_from_server > my_time_Lamport) {
            my_time_Lamport = time_received_from_server + 1;
        } else {
            incrementLamportTime();
        }
    }

    public static void incrementLamportTime() {
        my_time_Lamport = my_time_Lamport + 1;
    }

    public static void convertToJson() {
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
    public static String jsonObjectToString(){
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

}