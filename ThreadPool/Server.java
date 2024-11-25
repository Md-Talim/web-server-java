package ThreadPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket) {
        try (PrintWriter toSocket =
                 new PrintWriter(clientSocket.getOutputStream(), true)) {
            String clientInfo =
                clientSocket.getInetAddress() + ":" + clientSocket.getPort();
            System.out.println("Connected to client: " + clientInfo);
            toSocket.println("Hello from server " + clientInfo);
        } catch (IOException ex) {
            System.err.println("Error handling client: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " +
                                   clientSocket.getInetAddress());
            } catch (IOException ex) {
                System.err.println("Failed to close client socket: " +
                                   ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 10;
        Server server = new Server(poolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(700000);
            System.out.println("Server is listening on port: " + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Use the thread pool to handle the client
                    server.threadPool.execute(
                        () -> server.handleClient(clientSocket));
                } catch (IOException ex) {
                    System.err.println("Error accepting client connections: " +
                                       ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("Server failed to start: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down servers...");
            // Shutdown the thread pool when the server exits
            server.shutdownThreadPool();
        }
    }

    private void shutdownThreadPool() {
        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Forcing shutdown of thread pool...");
            }
        } catch (InterruptedException ex) {
            System.out.println("Thread pool shutdown interrupted: " +
                               ex.getMessage());
            threadPool.shutdownNow();
        }

        System.out.println("Server has shut down.");
    }
}
