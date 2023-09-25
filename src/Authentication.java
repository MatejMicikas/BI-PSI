import java.io.*;
import java.net.Socket;

public class Authentication {
    private final Socket clientSocket;
    private final ClientReader reader;
    private final BufferedWriter writer;

    // Key ID - Server Key mapping
    private static final int[] serverKeys = {23019, 32037, 18789, 16443, 18189};

    // Key ID - Client Key mapping
    private static final int[] clientKeys = {32037, 29295, 13603, 29533, 21952};

    public Authentication(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new ClientReader(clientSocket);
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public boolean authenticate() throws IOException {
        String clientUsername = readMessage(20);

        if (!validateUsername(clientUsername)) {
            System.out.println("Error: Wrong username format.");
            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
            return false;
        }

        int keyId = requestKeyId();

        if (keyId == -1) {
            System.out.println("Error: Key has the wrong format.");
            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
            return false;
        } else if (!validateKeyId(keyId)) {
            System.out.println("Error: Key is out of range.");
            sendMessage(ServerMessages.SERVER_KEY_OUT_OF_RANGE_ERROR);
            return false;
        }

        int serverKey = serverKeys[keyId];
        int clientKey = clientKeys[keyId];

        int usernameHash = calculateUsernameHash(clientUsername);
        int serverConfirmation = (usernameHash + serverKey) % 65536;

        sendConfirmationMessage(serverConfirmation);

        int clientConfirmation = readConfirmationMessage();

        if (clientConfirmation == -1) {
            clientSocket.close();
        }

        int expectedClientConfirmation = (usernameHash + clientKey) % 65536;

        if (clientConfirmation == expectedClientConfirmation) {
            sendMessage(ServerMessages.SERVER_OK);
            return true;
        } else {
            sendLoginErrorMessage();
            return false;
        }
    }

    private String readMessage(int maxLength) throws IOException {
        return reader.rechargingRead(maxLength);
    }

    private void sendMessage(String message) throws IOException {
        writer.write(message);
        writer.flush();
    }

    private int requestKeyId() throws IOException {
        sendMessage(ServerMessages.SERVER_KEY_REQUEST);
        String keyIdMessage = readMessage(5);
        try {
            return Integer.parseInt(keyIdMessage);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void sendConfirmationMessage(int serverConfirmation) throws IOException {
        sendMessage(serverConfirmation + "\u0007\u0008");
    }

    private int readConfirmationMessage() throws IOException {
        String confirmationMessage = readMessage(7);
        if (confirmationMessage != null && (confirmationMessage.contains(" ") || confirmationMessage.equals("max"))) {
            System.out.println("Error: Confirmation message has the wrong format.");
            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
        }
        try {
            if (confirmationMessage != null) {
                return Integer.parseInt(confirmationMessage);
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return -1;
    }

    private void sendLoginErrorMessage() throws IOException {
        System.out.println("Error: Login failed.");
        sendMessage(ServerMessages.SERVER_LOGIN_FAILED);
    }

    private boolean validateUsername(String username) {
        return username.length() <= 18 && !username.equals("max");
    }

    private boolean validateKeyId(int keyId) {
        return keyId >= 0 && keyId < serverKeys.length;
    }

    private int calculateUsernameHash(String username) {
        int sum = 0;
        for (char c : username.toCharArray()) {
            sum += c;
        }
        return (sum * 1000) % 65536;
    }
}