package co.edu.arep.arep.parcialt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author juan.medina-r
 */
public class BackendHost {

    private static final LinkedList<Double> NUMB_LIST = null;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String outputLine = "";
            inputLine = in.readLine();
            if (inputLine != null) {
                System.out.println("Recibi: " + inputLine);
                inputLine = inputLine.split(" ")[1];
                System.out.println("inputLine.startsWith(\"/api\") " + inputLine.startsWith("/api"));
                
                outputLine = processRequest(inputLine);
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String processRequest(String inputLine) {
        String path = inputLine.substring(4);
        switch (path){
            case "/add":
                Numbers.add();
        }
    }
    
    private static String notFount() {
        return """
               HTTP/1.1 404 Not Found\r
               Content-Type: application/json\r
               \r
               {
               "status": "ERR",
               "error": "resource not found"
               }""";
    }
}
