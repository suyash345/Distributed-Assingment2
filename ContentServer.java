import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner; 

public class ContentServer {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4567;
    public static void main(String[] args) {
        try{
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            PrintWriter out_to_send_aggregation_server = new PrintWriter(socket.getOutputStream(),true);
            String data_JSON = readFile();
            System.out.print(data_JSON);
            out_to_send_aggregation_server.println(data_JSON);
            out_to_send_aggregation_server.flush();

        }
        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();    
        }
    }
    public static String readFile(){
        String fileContent = "";
        try {
            File myObj = new File("inputfile.txt");
            
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                fileContent += data;
                System.out.println(data);
      }
        myReader.close();
        return fileContent;

    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    return "";
}

}
