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

        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            String request = dis.readUTF();
            System.out.println("Modtaget request: " + request);

            String fileName = parseFileName(request);
            if (fileName == null || fileName.isBlank()) {
                sendError(dos, "Ugyldigt format. Brug GET|<filnavn>");
                return;
            }

            if (fileName.contains("../") || fileName.startsWith("/") || fileName.contains("\\")) {
                sendError(dos, "Ugyldigt filnavn");
                return;
            }

            File file = new File(fileName);
            if (!file.exists() || !file.isFile()) {
                sendError(dos, "Filen findes ikke");
                return;
            }

            long fileSize = sendFile(file, dos, clientSocket.getOutputStream());
            System.out.println("Fil sendt: " + fileName + " (" + fileSize + " bytes)");
        }
    }

    private long sendFile(File file, DataOutputStream dos, OutputStream outputStream) throws IOException {
        long fileSize = file.length();
        // send header reliably as UTF
        dos.writeUTF("OK|" + fileSize);
        dos.flush();

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

    private void sendError(DataOutputStream dos, String message) throws IOException {
        dos.writeUTF("ERROR|" + message);
        dos.flush();
    }

    // Backwards-compatible helper if PrintWriter was used elsewhere
    private void sendError(PrintWriter writer, String message) {
        writer.println("ERROR|" + message);
    }
}