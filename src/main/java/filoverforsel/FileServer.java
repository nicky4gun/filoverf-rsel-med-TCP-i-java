package filoverforsel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

        try (DataInputStream input = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String request = input.readUTF();
            System.out.println("Modtaget request: " + request);

            String fileName = parseFileName(request);
            if (fileName == null || fileName.isBlank()) {
                sendError(output, "Ugyldigt format. Brug GET|<filnavn>");
                return;
            }

            if (fileName.contains("../") || fileName.startsWith("/") || fileName.contains("\\")) {
                sendError(output, "Ugyldigt filnavn");
                return;
            }

            File baseDir = new File(".").getCanonicalFile();
            File file = new File(baseDir, fileName).getCanonicalFile();
            if (!file.getPath().startsWith(baseDir.getPath() + File.separator)) {
                sendError(output, "Ugyldigt filnavn");
                return;
            }

            if (!file.exists() || !file.isFile()) {
                sendError(output, "Filen findes ikke");
                return;
            }

            long fileSize = SendFile(file, output);
            System.out.println("Fil sendt: " + fileName + " (" + fileSize + " bytes)");
        }
    }

    private long SendFile(File file, DataOutputStream output) throws IOException {
        long fileSize = file.length();

        output.writeUTF("OK|" + fileSize);
        output.flush();

        try (BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
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

    private void sendError(DataOutputStream output, String message) throws IOException {
        output.writeUTF("ERROR|" + message);
        output.flush();
    }
}