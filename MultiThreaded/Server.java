package MultiThreaded;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private volatile boolean running;

    public Server(int port) {
        this.port = port;
        this.running = true;
    }

    public void run() {
        System.out.println("Starting server on port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                try {
                    System.out.println("Waiting for client connections...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Accepted connection from: " + clientSocket.getRemoteSocketAddress());

                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    if (!running) {
                        System.out.println("Server is shutting down...");
                        break;
                    }
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing server: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        System.out.println("Server is shutting down...");
    }

    public static void main(String[] args) {
        int port = 8010;
        Server server = new Server(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.run();
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream())) {
            System.out.println("Handling client: " + clientSocket.getRemoteSocketAddress());
            toClient.println("Hello from the Server!");
            System.out.println("Message sent to client: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Error communicating with client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Closed connection to client: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Error closing the client socket: " + e.getMessage());
            }
        }
    }
}
