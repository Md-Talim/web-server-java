package SingleThreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        System.out.println("Starting server on port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(10000);

            while (!serverSocket.isClosed()) {
                try {
                    System.out.println("Waiting for client connections...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection accepted from: " + clientSocket.getRemoteSocketAddress());
                    handleClient(clientSocket);
                } catch (SocketTimeoutException e) {
                    System.out.println("Server timed out waiting for a connection...");
                } catch (IOException ex) {
                    System.err.println("Error accepting connection: " + ex.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            System.out.println("Communicating with client...");
            toClient.println("Hello from the Server!");
            String clientMessage = fromClient.readLine();
            System.out.println("Message from client: " + clientMessage);
        } catch (IOException ex) {
            System.err.println("Error during client communication: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client connection closed.");
            } catch (IOException ex) {
                System.err.println("Error closing client socket: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8010);

        try {
            server.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
