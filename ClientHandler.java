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
            bufferedWriter.write("Got Your Message!"); // whatever that is sent is  here
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
            String data = bufferedReader.readLine(); // for the data that is sent.
            System.out.println(data);
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

}