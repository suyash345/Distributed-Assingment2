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
import java.util.Timer;
import java.util.TimerTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;


public class AggregationServer {
    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("what port would you like the server to be?");
            int port_number = scanner.nextInt();
            serverSocket = new ServerSocket(port_number);
            System.out.println("Server is now listening on port " + port_number);
            while(!serverSocket.isClosed()){
                // accept request
                Socket socket = serverSocket.accept(); // this is closed in ClientHandler

                ClientHandler clientHandler = new ClientHandler(socket);// start the clientHandler with a new thread.
                Thread thread = new Thread(clientHandler);
                thread.start();

        }
    }  catch (IOException e){
            e.printStackTrace();}
        finally {
            close(serverSocket);
        }

    }
    public static void close(ServerSocket serverSocket)
    {
        try{
            FileWriter writer = new FileWriter("dataServer.json");
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        try{
            if(serverSocket!=null){
                serverSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}