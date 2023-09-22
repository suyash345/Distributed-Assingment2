import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException; 
import java.io.File;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.JsonSyntaxException;
//whenever we rec or send a message we need to increment the lamport clock by one.

public class ClientHandler implements Runnable {
    private Socket socket;
    private static int my_time_Lamport = 0;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String request = find_request();  // the first line is the type of request PUT/GET, also manages time
        while (socket.isConnected() && !socket.isClosed()) {
            try {
                if (request.equals("GET")) {
                    getMethod(bufferedWriter);
                    return;
                } else if (request.equals("PUT")) {
                    //System.out.println("Put Method is being run");
                    putMethod(bufferedReader, bufferedWriter);
                    return;
                } else {
                    close(socket, bufferedReader, bufferedWriter);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                close(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String find_request() { // this now manages the time also
        String request = "";
        try { // used for first line
            String first_line = bufferedReader.readLine();
            String[] first_line_list = first_line.split(" ");
            request = first_line_list[0];
            int time_from_server = Integer.parseInt(first_line_list[1]);
            synchronized (ClientHandler.class) {
                manageLamportTime(time_from_server);
                System.out.println("Time is currently" + my_time_Lamport);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    public void getMethod(BufferedWriter bufferedWriter) // for get method, no need to send back with lamport time
    { //   need to send back most recent data->recvied highest lamport time
        try {
            synchronized (ClientHandler.class) {
                incrementLamportTime();
                System.out.println("Time is currently" + my_time_Lamport);
            }
            File reader = new File("dataServer.json");
            Scanner myReader = new Scanner(reader);
            String data = myReader.nextLine();
            bufferedWriter.write(data); // whatever that is sent is  here
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return;
        }

    }

    public void putMethod(BufferedReader bufferedReader, BufferedWriter bufferedWriter) // send back with lamport time
    { //  need to make a stamp with each lamport time.
        try {
            synchronized (ClientHandler.class) {
                incrementLamportTime();
                System.out.println("Time is currently" + my_time_Lamport);
            } // increment when receving

            String data = bufferedReader.readLine(); // for the data that is to be put on server.
            String ID = getIdOfJsonString(data);
            if (checkIDInServer(ID)) {                      // if id exists, want to delete the before id and  add new.
                                                            // if it does not just add.
                                                            // if true, then delete then add
                                                            // if false just add.
                deleteFromServer(ID);
                addID(data);
            } else {
                addID(data);
            }

            synchronized (ClientHandler.class) {
                incrementLamportTime();
                System.out.println("Time is currently" + my_time_Lamport);
            } // increment when sending
            bufferedWriter.write("Got put request and sucessfully inputted into the file");  // whatever is sent is here
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return;
        }
    }

    public void manageLamportTime(int time_received_from_server) {
        if (time_received_from_server > my_time_Lamport) {
            my_time_Lamport = time_received_from_server + 1;
        } else {
            incrementLamportTime();
        }
    }

    public void incrementLamportTime() {
        my_time_Lamport = my_time_Lamport + 1;
    }


    public boolean checkIDInServer(String id) { // also want to read and check if id is in server.

        try {                                 // if false just add.
            File reader = new File("dataServer.json");
            Scanner myReader = new Scanner(reader);

            while (myReader.hasNextLine()) {
                if (myReader.nextLine().contains(id)) {
                    return true;
                } else {
                    continue;
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    public String getIdOfJsonString(String Json_data) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(Json_data).getAsJsonObject();
        String idValue = jsonObject.get("id").getAsString();
        return idValue;
    }

    public void deleteFromServer(String id) { // this deletes all insatnces of id.
        String idValue = "";
        StringBuilder tempContent = new StringBuilder();
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get("dataServer.json")));
            String[] lines = fileContent.split("\\r?\\n");

            for (int i = 0; i < lines.length; i++) {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(lines[i]).getAsJsonObject();
                idValue = jsonObject.get("id").getAsString();
                System.out.println(idValue);
                if (!idValue.equals(id)) {
                    System.out.println("line appended");
                    tempContent.append(lines[i]).append("\n");
                } else {
                    System.out.println("executed");
                }
            }
            //System.out.print(tempContent);
            try (FileWriter writer = new FileWriter("dataServer.json")) {
                writer.write(tempContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.out.println("Error parsing JSON.");
            e.printStackTrace();
        }
    }

    public void addID(String data) {
        try {
            FileWriter writer = new FileWriter("dataServer.json", true);
            writer.write(data + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}