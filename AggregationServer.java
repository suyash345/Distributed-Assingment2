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

//import com.google.gson.Gson;


public class AggregationServer {
    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4567);
            while(!serverSocket.isClosed()){
                // accept request
                Socket socket = serverSocket.accept(); // this is closed in ClientHandler
                System.out.println("A new client has connected!");
                // start the clientHandler with a new thread.
                ClientHandler clientHandler = new ClientHandler(socket);
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
            if(serverSocket!=null){
                serverSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}