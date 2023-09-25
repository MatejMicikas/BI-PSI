import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        int clientIndex = 1;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(1000);
                System.out.println("Client " + clientIndex + " connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
                clientIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error starting the server", e);
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error stopping the server", e);
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        Server server = new Server(port);
        server.startServer();
    }
}