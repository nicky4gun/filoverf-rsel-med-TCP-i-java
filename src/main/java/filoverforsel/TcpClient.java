package filoverforsel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public Socket connectToServer() {
        try {
            Socket socket = new Socket(HOST, PORT);
            socket.setSoTimeout(30000);
            System.out.println("FileClient forbinder til " + HOST + ":" + PORT);
            return socket;
        } catch (IOException e) {
            System.err.println("Kunne ikke forbinde til serveren på " + HOST + ":" + PORT + ": " + e.getMessage());
            return null;
        }
    }

    public void sendRequest(String fileName) {
        Socket socket = connectToServer();
        if (socket == null) {
            return;
        }

        try (Socket ignored = socket;
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             InputStream inputStream = socket.getInputStream()) {

            String request = "GET|" + fileName;
            writer.println(request);
            System.out.println("FileClient sender request: " + request);

            String response = readLine(inputStream);
            System.out.println("FileClient modtog respons: " + response);

            if (response == null) {
                return;
            }

            String[] parts = response.split("\\|", 2);
            if (parts.length == 2 && "OK".equalsIgnoreCase(parts[0])) {
                saveReceivedFile(fileName, inputStream);
            } else if (parts.length == 2 && "ERROR".equalsIgnoreCase(parts[0])) {
                System.out.println("Serverfejl: " + parts[1]);
            } else {
                System.out.println("Ugyldigt svar fra serveren");
            }
        } catch (IOException e) {
            System.err.println("Fejl ved sending af request: " + e.getMessage());
        }
    }

    private void saveReceivedFile(String fileName, InputStream inputStream) throws IOException {
        File downloadsDir = new File("downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        File outputFile = new File(downloadsDir, fileName);
        try (OutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
        }

        System.out.println("Fil gemt lokalt: " + outputFile.getAbsolutePath());
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            if (nextByte == '\n') {
                break;
            }
            if (nextByte != '\r') {
                buffer.write(nextByte);
            }
        }

        if (buffer.size() == 0 && nextByte == -1) {
            return null;
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        client.sendRequest("test.txt");
    }
}
