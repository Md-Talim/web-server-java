package MultiThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public Runnable getRunnable(String host, int port) {
        return () -> {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 5000); // 5-second connection timeout
                System.out.println("Connected to server: " + socket.getRemoteSocketAddress());

                try (
                    PrintWriter toServer = new PrintWriter(socket.getOutputStream());
                    BufferedReader fromServer = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                ) {
                    // Send message to the server
                    toServer.println("Hello from the Client " + socket.getLocalSocketAddress());

                    // Read and print the server's response
                    String response = fromServer.readLine();
                    System.out.println("Response from the server: " + response);
                }
            } catch (IOException e) {
                System.err.println("Error in client: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8010;

        Client client = new Client();

        for (int i = 0; i < 100; i++) {
            new Thread(client.getRunnable(host, port)).start();
        }
    }
}
