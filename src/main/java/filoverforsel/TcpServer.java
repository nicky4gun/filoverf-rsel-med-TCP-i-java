package filoverforsel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpServer {
    private static final int PORT = 5000;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FileServer lytter på port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Fejl under håndtering af klient: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server kunne ikke starte på port " + PORT + ": " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        System.out.println("Forbindelse modtaget fra " + clientSocket.getInetAddress());

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            String request = readLine(inputStream);
            System.out.println("Modtaget request: " + request);

            if (request == null || request.isBlank()) {
                sendError(outputStream, "Tom forespørgsel");
                return;
            }

            String[] parts = request.split("\\|", 2);
            if (parts.length != 2 || !"GET".equalsIgnoreCase(parts[0])) {
                sendError(outputStream, "Ugyldigt format. Brug GET|filnavn");
                return;
            }

            String fileName = parts[1].trim();
            if (fileName.contains("../") || fileName.startsWith("/") || fileName.contains("\\")) {
                sendError(outputStream, "Ugyldigt filnavn");
                return;
            }

            File file = new File(fileName);
            if (!file.exists() || !file.isFile()) {
                sendError(outputStream, "Filen findes ikke");
                return;
            }

            long fileSize = file.length();
            writeLine(outputStream, "OK|" + fileSize);

            try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            System.out.println("Fil sendt: " + fileName + " (" + fileSize + " bytes)");
        }
    }

    private void sendError(OutputStream outputStream, String message) throws IOException {
        writeLine(outputStream, "ERROR|" + message);
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

    private void writeLine(OutputStream outputStream, String text) throws IOException {
        outputStream.write((text + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
