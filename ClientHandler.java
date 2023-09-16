import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader input_from_content_server;
    private PrintWriter output_from_server_to_send_Client;
    private String PUT_OR_GET_METHOD;

    public ClientHandler(Socket clientSocket, String method) {
        try {
            this.client = clientSocket;
            this.input_from_content_server = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            this.output_from_server_to_send_Client = new PrintWriter(this.client.getOutputStream(), true);
            this.PUT_OR_GET_METHOD = method;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


    }

public void readFile() {
    Scanner myReader = null;
    try {
        File myObj = new File("data_server.txt");
        myReader = new Scanner(myObj);
        StringBuilder weather_data_that_needs_to_be_sent = new StringBuilder();

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            weather_data_that_needs_to_be_sent.append(data);
        }

        String outputData = weather_data_that_needs_to_be_sent.toString();
        //System.out.print(outputData);  

        if (output_from_server_to_send_Client != null) {
            output_from_server_to_send_Client.print(outputData);
            output_from_server_to_send_Client.flush();  
            //System.out.println("output_from_server_to_send_Client is null.");
        }

    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (myReader != null) {
            myReader.close();
        }
    }
}

public void writeFile(String input) {
    try (FileWriter fileWriter = new FileWriter("data_server.txt", true)) {
        fileWriter.append(input).append(System.lineSeparator());
        System.out.println("Data appended: " + input);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

