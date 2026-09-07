package filoverforsel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        FileServer server = new FileServer();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FileServer lytter på port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Klientfejl: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server kunne ikke starte på port " + PORT + ": " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        System.out.println("Forbindelse modtaget fra " + clientSocket.getInetAddress());

        try (OutputStream outputStream = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer  = new PrintWriter(
                     new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

            String request = reader.readLine();
            System.out.println("Modtaget request: " + request);

            String fileName = parseFileName(request);
            if (fileName == null || fileName.isBlank()) {
                sendError(writer, "Ugyldigt format. Brug GET|<filnavn>");
                return;
            }

            if (fileName.contains("../") || fileName.startsWith("/") || fileName.contains("\\")) {
                sendError(writer, "Ugyldigt filnavn");
                return;
            }

            File file = new File(fileName);
            if (!file.exists() || !file.isFile()) {
                sendError(writer, "Filen findes ikke");
                return;
            }

            long fileSize = SendFile(file, writer, outputStream);
            System.out.println("Fil sendt: " + fileName + " (" + fileSize + " bytes)");
        }
    }

    private long SendFile(File file, PrintWriter writer, OutputStream outputStream) throws IOException {
        long fileSize = file.length();
        writer.println("OK|" + fileSize);

        try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
        return fileSize;
    }

    private String parseFileName(String request) {
        if (request == null || request.isBlank()) {
            return null;
        }
        String[] parts = request.split("\\|", 2);
        if (parts.length != 2 || !"GET".equalsIgnoreCase(parts[0])) {
            return null;
        }
        return parts[1].trim();
    }

    private void sendError(PrintWriter writer, String message) {
        writer.println("ERROR|" + message);
    }
}