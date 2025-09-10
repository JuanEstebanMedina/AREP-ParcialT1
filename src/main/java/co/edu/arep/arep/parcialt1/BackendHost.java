package co.edu.arep.arep.parcialt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author juan.medina-r
 */
public class BackendHost {

    private static final LinkedList<Double> NUMB_LIST = new LinkedList<>();

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

                if (inputLine.startsWith("/api")) {
                    outputLine = processRequest(inputLine);
                } else {
                    outputLine = notFound();
                }
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String processRequest(String inputLine) {
        String[] split = inputLine.split("\\?");
        String path = split[0].substring(4);
        System.out.println("path: " + path);
                
        switch (path) {
            case "/add" -> {
                String temp = split[1].split("=")[1];
                String[] parameters = temp.split(",");
                for (String s : parameters) {
                    NUMB_LIST.add(Double.valueOf(s));
                }
                return statusOk("{ \"status\" : \"OK\", \"added\": " + Arrays.toString(parameters) + ", \"count\": " + NUMB_LIST.size() + " }");
            }
            case "/list" -> {
                if (!NUMB_LIST.isEmpty()) {
                    return statusOk("{ \"status\" : \"OK\", \"values\": " + NUMB_LIST + " }");
                } else {
                    return errorStatus("409 conflict", "{ \"status\" : \"ERR\", \"error\": empty_list }");
                }
            }
            case "/clear" -> {
                NUMB_LIST.clear();
                return statusOk("{ \"status\" : \"OK\", \"message\": list_cleared }");
            }
            case "/stats" -> {
                if (!NUMB_LIST.isEmpty() && NUMB_LIST.size() > 1) {
                    // double stddev = NUMB_LIST.size() > 1? stddev(): null;
                    return statusOk("{ \"status\" : \"OK\", \"mean\": " + mean() + ", \"stddev\": " + stddev() + ", \"count\": " + NUMB_LIST.size() + " }");
                } else {
                    return errorStatus("409 conflict", "{ \"status\" : \"ERR\", \"error\": empty_list }");
                }
                
            }
            default -> {
                return notFound();
            }
        }
    }

    private static String statusOk(String response) {
        return """
               HTTP/1.1 200 OK\r
               Content-Type: application/json\r
               \r
               """ + response;
    }

    private static String errorStatus(String error, String response) {
        return "HTTP/1.1 " + error + "\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + response;
    }

    private static String notFound() {
        return """
               HTTP/1.1 404 Not Found\r
               Content-Type: application/json\r
               \r
               {
               "status": "ERR",
               "error": "resource not found"
               }""";
    }

    private static double mean() {
        double result = 0;
        for (double i : NUMB_LIST) {
            result += i;
        }
        return result / NUMB_LIST.size();
    }

    private static double stddev() {
        double mean = mean();
        double result = 0;
        for (double i : NUMB_LIST) {
            result += Math.pow((i - mean), 2);
        }
        return Math.sqrt(result * (1 / (NUMB_LIST.size() - 1)));
    }
}
