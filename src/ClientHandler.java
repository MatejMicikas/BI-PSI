import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ) {
            ClientReader reader = new ClientReader(clientSocket);
            Authentication authentication = new Authentication(clientSocket);
            if (!authentication.authenticate()) {
                clientSocket.close();
            }

            ClientMovementHandler clientMovementHandler = new ClientMovementHandler(clientSocket);
            clientMovementHandler.findTarget(writer, reader);
            clientMovementHandler.pickUp(writer, reader);
        } catch (IOException e) {
            System.out.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
