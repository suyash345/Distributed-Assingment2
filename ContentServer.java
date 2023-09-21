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
//import com.google.gson.Gson;
public class ContentServer {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4567;
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {  
            socket = new Socket(SERVER_IP, SERVER_PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String dataToSend = readFile();

            // for the request
            bufferedWriter.write("PUT");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // for the content
            bufferedWriter.write(dataToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // to read if sucessfull or not?
            String line = bufferedReader.readLine();
            System.out.println(line);

        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
    }
        public static void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static String readFile(){
        String fileContent = "";
        try {
            File myObj = new File("inputfile.txt");
            
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                fileContent += data;
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
