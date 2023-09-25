import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientReader {
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private StringBuilder stringBuilder;
    private final Socket socket;

    public ClientReader(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.stringBuilder = new StringBuilder();
        this.socket = socket;
    }

    public void close() throws IOException {
        this.reader.close();
    }

    public String read(int maxLength) {
        try {
            this.stringBuilder.setLength(0);
            String recharge = "RECHARGING";
            String power = "FULL POWER";

            int c;
            while ((c = this.reader.read()) != -1 || this.stringBuilder.length() <= maxLength && !recharge.contains(this.stringBuilder.substring(0, this.stringBuilder.length())) && !power.contains(this.stringBuilder.substring(0, this.stringBuilder.length()))) {
                this.stringBuilder.append((char) c);
                if (this.stringBuilder.toString().endsWith("\u0007\b")) {
                    return this.stringBuilder.substring(0, this.stringBuilder.length() - 2);
                }

                if (this.stringBuilder.length() == maxLength && !recharge.contains(this.stringBuilder.substring(0, this.stringBuilder.length())) && !power.contains(this.stringBuilder.substring(0, this.stringBuilder.length()))) {
                    return "max";
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading message: " + e.getMessage());
        }

        return null;
    }

    String rechargingRead(int maxLength) throws IOException {
        String message = this.read(maxLength);
        if (message != null && message.equals("RECHARGING")) {
            this.socket.setSoTimeout(5000);
            long startTime = System.currentTimeMillis();
            message = this.read(12);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (elapsedTime >= 5000) {
                System.out.println("Error: Out of time. Closing communication.");
                socket.close();
            }
            if (message != null && !message.equals("FULL POWER")) {
                System.out.println("Error: 302 LOGIC ERROR.");
                this.writer.write(ServerMessages.SERVER_LOGIC_ERROR);
                this.writer.flush();
                socket.close();
            }

            this.socket.setSoTimeout(1000);
            message = this.read(maxLength);
        }

        return message;
    }
}