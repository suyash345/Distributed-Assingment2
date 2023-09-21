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
public class GETClient {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            // connect to server and make the input and output obejcts.
            socket = new Socket("localhost", 4567);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // send get request to Server
            Scanner scanner = new Scanner(System.in);
            bufferedWriter.write("GET");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // response from server
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

    }