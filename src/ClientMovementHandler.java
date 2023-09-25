import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ClientMovementHandler {
    private int x;
    private int y;
    private int direction;
    private final Socket socket;

    public ClientMovementHandler(Socket socket) {
        this.socket = socket;
    }

    public void setDirection(int x, int y) {
        // nahoru
        if (y == 1)
            direction = 0;
        // doprava
        if (x == 1)
            direction = 1;
        // dolu
        if (y == -1)
            direction = 2;
        // doleva
        if (x == -1)
            direction = 3;
    }

    public void readCoordinates(ClientReader reader, BufferedWriter writer) throws IOException {
        String message = reader.rechargingRead(12);
        if (Objects.equals(message, "max")) {
            writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
            writer.flush();
            socket.close();
        }

        if (message != null) {
            String[] messageParts = message.split(" ");
            if (messageParts.length != 3 || (!messageParts[0].equals("OK") && !messageParts[0].equals("K") && !messageParts[0].equals("O")) || messageParts[1].contains(".") || messageParts[2].contains(".") || message.endsWith(" ")) {
                System.out.println("Error: Wrong coordinate message format.");
                writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
                writer.flush();
                socket.close();
            }
            try {
                this.x = Integer.parseInt(messageParts[1]);
                this.y = Integer.parseInt(messageParts[2]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Floating point coordinates.");
            }
        } else {
            this.x = 0;
            this.y = 0;
        }
    }

    public boolean move(BufferedWriter writer, ClientReader reader) throws IOException {
        writer.write(ServerMessages.SERVER_MOVE);
        writer.flush();

        int oldX = this.x;
        int oldY = this.y;
        readCoordinates(reader, writer);

        if (oldX != this.x || oldY != this.y) {
            setDirection(this.x - oldX, this.y - oldY);
            return true;
        }
        return false;
    }

    public void turnLeft(BufferedWriter writer, ClientReader reader) throws IOException {
        writer.write(ServerMessages.SERVER_TURN_LEFT);
        writer.flush();
        readCoordinates(reader, writer);
    }

    public void turnRight(BufferedWriter writer, ClientReader reader) throws IOException {
        writer.write(ServerMessages.SERVER_TURN_RIGHT);
        writer.flush();
        readCoordinates(reader, writer);
    }

    public void turn(int direction, BufferedWriter writer, ClientReader reader) throws IOException {
        int directionChange = (direction - this.direction + 4) % 4;
        if (directionChange == 1) {
            turnRight(writer, reader);
        }
        if (directionChange == 2) {
            turnRight(writer, reader);
            turnRight(writer, reader);
        }
        if (directionChange == 3) {
            turnLeft(writer, reader);
        }
    }

    public void avoidObstacle(BufferedWriter writer, ClientReader reader) throws IOException {
        if (!move(writer, reader)) {
            turnLeft(writer, reader);
            move(writer, reader);
            turnRight(writer, reader);
            move(writer, reader);
        }
    }

    public void go(int direction, BufferedWriter writer, ClientReader reader) throws IOException {
        turn(direction, writer, reader);
        avoidObstacle(writer, reader);
    }

    int max(int y, int x) {
        if (y > x) {
            return 0;
        } else {
            return 1;
        }
    }

    int shortestDirection(int y0, int x0, int y1, int x1) {
        int coordinates1 = Math.max(y0, x0);
        int dir01 = max(y0, x0);
        int coordinates2 = Math.max(y1, x1);
        int dir23 = max(y1, x1);
        if (coordinates1 > coordinates2) {
            return dir01;
        } else {
            return dir23 + 2;
        }
    }

    public void findTarget(BufferedWriter writer, ClientReader reader) throws IOException {
        int direction;
        do {
            direction = shortestDirection(
                    Math.max(0, -(this.y)),
                    Math.max(0, -(this.x)),
                    Math.max(0, this.y),
                    Math.max(0, this.x)
            );
            go(direction, writer, reader);
        }
        while (this.x != 0 || this.y != 0);
    }

    public void pickUp(BufferedWriter writer, ClientReader reader) throws IOException {
        writer.write(ServerMessages.SERVER_PICK_UP);
        writer.flush();
        String message = reader.rechargingRead(100);
        if (Objects.equals(message, "max")) {
            System.out.println("Error: Max length of secret exceeded.");
            writer.write(ServerMessages.SERVER_SYNTAX_ERROR);
            writer.flush();
            socket.close();
        } else {
            System.out.println("Secret message picked up. Disconnecting.");
            writer.write(ServerMessages.SERVER_LOGOUT);
            writer.flush();
        }
    }
}