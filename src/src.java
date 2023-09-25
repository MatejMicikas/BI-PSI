//import java.io.*;
//import java.net.Socket;
//
//public class Authentication {
//    private final Socket clientSocket;
//    private final ClientReader reader;
//    private final BufferedWriter writer;
//
//    // Key ID - Server Key mapping
//    private static final int[] serverKeys = {23019, 32037, 18789, 16443, 18189};
//
//    // Key ID - Client Key mapping
//    private static final int[] clientKeys = {32037, 29295, 13603, 29533, 21952};
//
//    public Authentication(Socket clientSocket) throws IOException {
//        this.clientSocket = clientSocket;
//        this.reader = new ClientReader(clientSocket);
//        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//    }
//
//    public boolean authenticate() throws IOException {
//        String clientUsername = readMessage(20);
//
//        if (!validateUsername(clientUsername)) {
//            System.out.println("Error: Wrong username format.");
//            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
//            return false;
//        }
//
//        int keyId = requestKeyId();
//
//        if (keyId == -1) {
//            System.out.println("Error: Key has the wrong format.");
//            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
//            return false;
//        } else if (!validateKeyId(keyId)) {
//            System.out.println("Error: Key is out of range.");
//            sendMessage(ServerMessages.SERVER_KEY_OUT_OF_RANGE_ERROR);
//            return false;
//        }
//
//        int serverKey = serverKeys[keyId];
//        int clientKey = clientKeys[keyId];
//
//        int usernameHash = calculateUsernameHash(clientUsername);
//        int serverConfirmation = (usernameHash + serverKey) % 65536;
//
//        sendConfirmationMessage(serverConfirmation);
//
//        int clientConfirmation = readConfirmationMessage();
//
//        if (clientConfirmation == -1) {
//            clientSocket.close();
//        }
//
//        int expectedClientConfirmation = (usernameHash + clientKey) % 65536;
//
//        if (clientConfirmation == expectedClientConfirmation) {
//            sendMessage(ServerMessages.SERVER_OK);
//            return true;
//        } else {
//            sendLoginErrorMessage();
//            return false;
//        }
//    }
//
//    private String readMessage(int maxLength) throws IOException {
//        return reader.rechargingRead(maxLength);
//    }
//
//    private void sendMessage(String message) throws IOException {
//        writer.write(message);
//        writer.flush();
//    }
//
//    private int requestKeyId() throws IOException {
//        sendMessage(ServerMessages.SERVER_KEY_REQUEST);
//        String keyIdMessage = readMessage(5);
//        try {
//            return Integer.parseInt(keyIdMessage);
//        } catch (NumberFormatException e) {
//            return -1;
//        }
//    }
//
//    private void sendConfirmationMessage(int serverConfirmation) throws IOException {
//        sendMessage(serverConfirmation + "\u0007\u0008");
//    }
//
//    private int readConfirmationMessage() throws IOException {
//        String confirmationMessage = readMessage(7);
//        if (confirmationMessage != null && (confirmationMessage.contains(" ") || confirmationMessage.equals("max"))) {
//            System.out.println("Error: Confirmation message has the wrong format.");
//            sendMessage(ServerMessages.SERVER_SYNTAX_ERROR);
//        }
//        try {
//            if (confirmationMessage != null) {
//                return Integer.parseInt(confirmationMessage);
//            }
//        } catch (NumberFormatException e) {
//            return -1;
//        }
//        return -1;
//    }
//
//    private void sendLoginErrorMessage() throws IOException {
//        System.out.println("Error: Login failed.");
//        sendMessage(ServerMessages.SERVER_LOGIN_FAILED);
//    }
//
//    private boolean validateUsername(String username) {
//        return username.length() <= 18 && !username.equals("max");
//    }
//
//    private boolean validateKeyId(int keyId) {
//        return keyId >= 0 && keyId < serverKeys.length;
//    }
//
//    private int calculateUsernameHash(String username) {
//        int sum = 0;
//        for (char c : username.toCharArray()) {
//            sum += c;
//        }
//        return (sum * 1000) % 65536;
//    }
//}import java.io.*;
//import java.net.Socket;
//
//public class ClientHandler implements Runnable {
//    private final Socket clientSocket;
//
//    public ClientHandler(Socket clientSocket) throws IOException {
//        this.clientSocket = clientSocket;
//    }
//
//    @Override
//    public void run() {
//        try (
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//        ) {
//            ClientReader reader = new ClientReader(clientSocket);
//            Authentication authentication = new Authentication(clientSocket);
//            if (!authentication.authenticate()) {
//                clientSocket.close();
//            }
//
//            ClientMovementHandler clientMovementHandler = new ClientMovementHandler(clientSocket);
//            clientMovementHandler.findTarget(writer, reader);
//            clientMovementHandler.pickUp(writer, reader);
//        } catch (IOException e) {
//            System.out.println("Error handling client request: " + e.getMessage());
//        } finally {
//            try {
//                clientSocket.close();
//            } catch (IOException e) {
//                System.out.println("Error closing client socket: " + e.getMessage());
//            }
//        }
//    }
//}
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Objects;
//
//public class ClientMovementHandler {
//    private int x;
//    private int y;
//    private int direction;
//    private final Socket socket;
//
//    public ClientMovementHandler(Socket socket) {
//        this.socket = socket;
//    }
//
//    public void setDirection(int x, int y) {
//        // nahoru
//        if (y == 1)
//            direction = 0;
//        // doprava
//        if (x == 1)
//            direction = 1;
//        // dolu
//        if (y == -1)
//            direction = 2;
//        // doleva
//        if (x == -1)
//            direction = 3;
//    }
//
//    public void readCoordinates(ClientReader reader, BufferedWriter writer) throws IOException {
//        String message = reader.rechargingRead(12);
//        if (Objects.equals(message, "max")) {
//            writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
//            writer.flush();
//            socket.close();
//        }
//
//        if (message != null) {
//            String[] messageParts = message.split(" ");
//            if (messageParts.length != 3 || (!messageParts[0].equals("OK") && !messageParts[0].equals("K") && !messageParts[0].equals("O")) || messageParts[1].contains(".") || messageParts[2].contains(".") || message.endsWith(" ")) {
//                System.out.println("Error: Wrong coordinate message format.");
//                writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
//                writer.flush();
//                socket.close();
//            }
//            try {
//                this.x = Integer.parseInt(messageParts[1]);
//                this.y = Integer.parseInt(messageParts[2]);
//            } catch (NumberFormatException e) {
//                System.out.println("Error: Floating point coordinates.");
//            }
//        } else {
//            this.x = 0;
//            this.y = 0;
//        }
//    }
//
//    public boolean move(BufferedWriter writer, ClientReader reader) throws IOException {
//        writer.write(ServerMessages.SERVER_MOVE);
//        writer.flush();
//
//        int oldX = this.x;
//        int oldY = this.y;
//        readCoordinates(reader, writer);
//
//        if (oldX != this.x || oldY != this.y) {
//            setDirection(this.x - oldX, this.y - oldY);
//            return true;
//        }
//        return false;
//    }
//
//    public void turnLeft(BufferedWriter writer, ClientReader reader) throws IOException {
//        writer.write(ServerMessages.SERVER_TURN_LEFT);
//        writer.flush();
//        readCoordinates(reader, writer);
//    }
//
//    public void turnRight(BufferedWriter writer, ClientReader reader) throws IOException {
//        writer.write(ServerMessages.SERVER_TURN_RIGHT);
//        writer.flush();
//        readCoordinates(reader, writer);
//    }
//
//    public void turn(int direction, BufferedWriter writer, ClientReader reader) throws IOException {
//        int directionChange = (direction - this.direction + 4) % 4;
//        if (directionChange == 1) {
//            turnRight(writer, reader);
//        }
//        if (directionChange == 2) {
//            turnRight(writer, reader);
//            turnRight(writer, reader);
//        }
//        if (directionChange == 3) {
//            turnLeft(writer, reader);
//        }
//    }
//
//    public void avoidObstacle(BufferedWriter writer, ClientReader reader) throws IOException {
//        if (!move(writer, reader)) {
//            turnLeft(writer, reader);
//            move(writer, reader);
//            turnRight(writer, reader);
//            move(writer, reader);
//        }
//    }
//
//    public void go(int direction, BufferedWriter writer, ClientReader reader) throws IOException {
//        turn(direction, writer, reader);
//        avoidObstacle(writer, reader);
//    }
//
//    int max(int y, int x) {
//        if (y > x) {
//            return 0;
//        } else {
//            return 1;
//        }
//    }
//
//    int shortestDirection(int y0, int x0, int y1, int x1) {
//        int coordinates1 = Math.max(y0, x0);
//        int dir01 = max(y0, x0);
//        int coordinates2 = Math.max(y1, x1);
//        int dir23 = max(y1, x1);
//        if (coordinates1 > coordinates2) {
//            return dir01;
//        } else {
//            return dir23 + 2;
//        }
//    }
//
//    public void findTarget(BufferedWriter writer, ClientReader reader) throws IOException {
//        int direction;
//        do {
//            direction = shortestDirection(
//                    Math.max(0, -(this.y)),
//                    Math.max(0, -(this.x)),
//                    Math.max(0, this.y),
//                    Math.max(0, this.x)
//            );
//            go(direction, writer, reader);
//        }
//        while (this.x != 0 || this.y != 0);
//    }
//
//    public void pickUp(BufferedWriter writer, ClientReader reader) throws IOException {
//        writer.write(ServerMessages.SERVER_PICK_UP);
//        writer.flush();
//        String message = reader.rechargingRead(100);
//        if (Objects.equals(message, "max")) {
//            System.out.println("Error: Max length of secret exceeded.");
//            writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
//            writer.flush();
//            socket.close();
//        } else {
//            System.out.println("Secret message picked up. Disconnecting.");
//            writer.write(ServerMessages.SERVER_LOGOUT);
//            writer.flush();
//        }
//    }
//}import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
//
//public class ClientReader {
//    private final BufferedReader reader;
//    private final BufferedWriter writer;
//    private StringBuilder stringBuilder;
//    private final Socket socket;
//
//    public ClientReader(Socket socket) throws IOException {
//        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        this.stringBuilder = new StringBuilder();
//        this.socket = socket;
//    }
//
//    public void close() throws IOException {
//        this.reader.close();
//    }
//
//    public String read(int maxLength) {
//        try {
//            this.stringBuilder.setLength(0);
//            String recharge = "RECHARGING";
//            String power = "FULL POWER";
//
//            int c;
//            while ((c = this.reader.read()) != -1 || this.stringBuilder.length() <= maxLength && !recharge.contains(this.stringBuilder.substring(0, this.stringBuilder.length())) && !power.contains(this.stringBuilder.substring(0, this.stringBuilder.length()))) {
//                this.stringBuilder.append((char) c);
//                if (this.stringBuilder.toString().endsWith("\u0007\b")) {
//                    return this.stringBuilder.substring(0, this.stringBuilder.length() - 2);
//                }
//
//                if (this.stringBuilder.length() == maxLength && !recharge.contains(this.stringBuilder.substring(0, this.stringBuilder.length())) && !power.contains(this.stringBuilder.substring(0, this.stringBuilder.length()))) {
//                    return "max";
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Error reading message: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    String rechargingRead(int maxLength) throws IOException {
//        String message = this.read(maxLength);
//        if (message != null && message.equals("RECHARGING")) {
//            this.socket.setSoTimeout(5000);
//            long startTime = System.currentTimeMillis();
//            message = this.read(12);
//            long endTime = System.currentTimeMillis();
//            long elapsedTime = endTime - startTime;
//            if (elapsedTime >= 5000) {
//                System.out.println("Error: Out of time. Closing communication.");
//                socket.close();
//            }
//            if (message != null && !message.equals("FULL POWER")) {
//                System.out.println("Error: 302 LOGIC ERROR.");
//                this.writer.write(ServerMessages.SERVER_LOGIC_ERROR);
//                this.writer.flush();
//                socket.close();
//            }
//
//            this.socket.setSoTimeout(1000);
//            message = this.read(maxLength);
//        }
//
//        return message;
//    }
//}import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Server {
//
//    private final int port;
//    private ServerSocket serverSocket;
//
//    public Server(int port) {
//        this.port = port;
//    }
//
//    public void startServer() {
//        int clientIndex = 1;
//        try {
//            serverSocket = new ServerSocket(port);
//            System.out.println("Server started on port " + port);
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                clientSocket.setSoTimeout(1000);
//                System.out.println("Client " + clientIndex + " connected: " + clientSocket.getInetAddress().getHostAddress());
//
//                ClientHandler clientHandler = new ClientHandler(clientSocket);
//                Thread clientThread = new Thread(clientHandler);
//                clientThread.start();
//                clientIndex++;
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error starting the server", e);
//        }
//    }
//
//    public void stopServer() {
//        try {
//            if (serverSocket != null) {
//                serverSocket.close();
//                System.out.println("Server stopped.");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error stopping the server", e);
//        }
//    }
//
//    public static void main(String[] args) {
//        int port = 1234;
//        Server server = new Server(port);
//        server.startServer();
//    }
//}public class ServerMessages {
//    public static final String SERVER_CONFIRMATION = "<16-bitové číslo v decimální notaci>\u0007\u0008";
//    public static final String SERVER_MOVE = "102 MOVE\u0007\u0008";
//    public static final String SERVER_TURN_LEFT = "103 TURN LEFT\u0007\u0008";
//    public static final String SERVER_TURN_RIGHT = "104 TURN RIGHT\u0007\u0008";
//    public static final String SERVER_PICK_UP = "105 GET MESSAGE\u0007\u0008";
//    public static final String SERVER_LOGOUT = "106 LOGOUT\u0007\u0008";
//    public static final String SERVER_KEY_REQUEST = "107 KEY REQUEST\u0007\u0008";
//    public static final String SERVER_OK = "200 OK\u0007\u0008";
//    public static final String SERVER_LOGIN_FAILED = "300 LOGIN FAILED\u0007\u0008";
//    public static final String SERVER_SYNTAX_ERROR = "301 SYNTAX ERROR\u0007\u0008";
//    public static final String SERVER_LOGIC_ERROR = "302 LOGIC ERROR\u0007\u0008";
//    public static final String SERVER_KEY_OUT_OF_RANGE_ERROR = "303 KEY OUT OF RANGE\u0007\u0008";
//
//    public ServerMessages() {
//    }
//}
