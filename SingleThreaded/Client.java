package SingleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        System.out.println("Connecting to server at " + host + ":" + port);

        try (
            Socket socket = new Socket(host, port);
            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            System.out.println("Connected to Server.");
            toSocket.println("Hello from the Client!");
            String response = fromSocket.readLine();
            System.out.println("Response from the Server: " + response);
        } catch (IOException ex) {
            System.err.println("Error during client communication: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8010;
        try {
            Client client = new Client(host, port);
            client.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
