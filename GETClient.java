import java.net.*;
import java.io.*;

public class GETClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4567;
    
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader input_from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            String serverResponse = input_from_server.readLine();
            System.out.println("Received from server: " + serverResponse);
            socket.close();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();

        }
        System.exit(0);
    }
}
