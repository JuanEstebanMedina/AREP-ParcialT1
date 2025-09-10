package co.edu.arep.arep.parcialt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 *
 * @author juan.medina-r
 */
public class FacadeHost {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
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
                    try {
                        httpConnection(inputLine);
                    } catch (ConnectException e) {
                        outputLine = failedConnection();
                    }
                } else {
                    outputLine = webClient();
                }
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    public static void httpConnection(String path) throws IOException {
        
        URL obj = new URL(GET_URL + path);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        System.out.println("con? " + con);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
    }

    private static String failedConnection() {
        return """
               HTTP/1.1 502 Bad Gateway\r
               Content-Type: application/json\r
               \r
               {
               "status": "ERR",
               "error": "backend_unreachable"
               }""";
    }

    private static String webClient() {
        return """
               HTTP/1.1 200 OK\r
               Content-Type: text/html\r
               \r
               <!DOCTYPE html>
               <html>
               
               <head>
                   <title>Form Example</title>
                   <meta charset="UTF-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
               </head>
               
               <body>
                   <h1>Calculadora Web para estimar la media y la desviación estándar de un conjunto de números</h1>
                   <form action="/add">
                       <label for="add">Numeros a añadir</label><br>
                       <label>separados por coma (,)</label><br>
                       <input type="text" id="add" name="add" value="1,2,3,4"><br><br>
                       <input type="button" value="Submit" onclick="addRequest()">
                   </form><br>
                   <div id="addResponse"></div>
               
                   <script>
                       function addRequest() {
                           let addNumbers = document.getElementById("add").value;
                           const xhttp = new XMLHttpRequest();
                           xhttp.onload = function () {
                               document.getElementById("addResponse").innerHTML =
                                   this.responseText;
                           }
                           xhttp.open("GET", "/api/add?x=" + addNumbers);
                           xhttp.send();
                       }
                   </script>
               
                   <br><br>
                   <form action="/list">
                       <label for="list">Listar números</label><br>
                       <input type="button" value="Submit" onclick="listRequest()">
                   </form><br>
                   <div id="listResponse"></div>
               
                   <script>
                       function listRequest() {
                           const xhttp = new XMLHttpRequest();
                           xhttp.onload = function () {
                               document.getElementById("listResponse").innerHTML =
                                   this.responseText;
                           }
                           xhttp.open("GET", "/api/list");
                           xhttp.send();
                       }
                   </script>
               
                   <br><br>
                   <form action="/clear">
                       <label for="clear">Limpiar lista de números</label><br>
                       <input type="button" value="Submit" onclick="clearRequest()">
                   </form><br>
                   <div id="clearResponse"></div>
               
                   <script>
                       function clearRequest() {
                           const xhttp = new XMLHttpRequest();
                           xhttp.onload = function () {
                               document.getElementById("clearResponse").innerHTML =
                                   this.responseText;
                           }
                           xhttp.open("GET", "/api/clear");
                           xhttp.send();
                       }
                   </script>
               
                   <br><br>
                   <form action="/stats">
                       <label for="stats">Calcular media y desviación estándar</label><br>
                       <input type="button" value="Submit" onclick="statsRequest()">
                   </form><br>
                   <div id="statsResponse"></div>
               
                   <script>
                       function statsRequest() {
                           const xhttp = new XMLHttpRequest();
                           xhttp.onload = function () {
                               document.getElementById("statsResponse").innerHTML =
                                   this.responseText;
                           }
                           xhttp.open("GET", "/api/stats");
                           xhttp.send();
                       }
                   </script>
               
               
               </body>
               
               </html>""";
    }

}
