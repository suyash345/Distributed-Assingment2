import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class AggregationServer {
    private static ArrayList <ClientHandler> clients = new ArrayList<>(); // to store all the threads. 
    private static final int PORT = 4567;
    public static void main(String[] args) throws IOException{
    ServerSocket listener = new ServerSocket(PORT); // make the socket   
    while(true){           
        System.out.println("Waiting for a connection");
        Socket client = listener.accept(); // establish the connection
        System.out.println("A New Client has Connected!");
        PrintWriter out = new PrintWriter(client.getOutputStream(),true); // the object to send data
        ClientHandler clientThread = new ClientHandler(client,"GET"); 
        Thread thread = new Thread(clientThread);
        thread.start();
        clients.add(clientThread);  
    }
    }

}