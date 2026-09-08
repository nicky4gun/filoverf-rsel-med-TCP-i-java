package filoverforsel;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        FileClient client = new FileClient();
        client.sendRequest();
    }

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

    public void sendRequest() {
        Socket socket = connectToServer();

        if (socket == null) {
            return;
        }

        try (socket;
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            System.out.print("Indtast filnavn e.g. test.txt: ");
            String fileName = keyboard.readLine().trim();

            String request = "GET|" + fileName;
            dos.writeUTF(request);
            dos.flush();
            System.out.println("Sendt request: " + request);

            String response = dis.readUTF();
            System.out.println("Server: " + response);

            if (response == null) {
                return;
            }

            String[] parts = response.split("\\|", 2);
            if (parts.length == 2 && "OK".equalsIgnoreCase(parts[0])) {
                long fileSize = Long.parseLong(parts[1]);
                saveReceivedFile(fileName, socket.getInputStream(), fileSize);
            } else if (parts.length == 2 && "ERROR".equalsIgnoreCase(parts[0])) {
                System.out.println("Serverfejl: " + parts[1]);
            } else {
                System.out.println("Ugyldigt svar fra serveren");
            }
        } catch (IOException e) {
            System.err.println("Fejl ved sending af request: " + e.getMessage());
        }
    }

    private void saveReceivedFile(String fileName, InputStream inputStream, long fileSize) throws IOException {
        File downloadsDir = new File("downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        File outputFile = new File(downloadsDir, fileName);
        try (OutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            long remaining = fileSize;
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int bytesRead = inputStream.read(buffer, 0, toRead);
                if (bytesRead == -1) throw new EOFException("Uventet slut på strømmen");
                fileOutputStream.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            fileOutputStream.flush();
        }

        System.out.println("Fil gemt lokalt: " + outputFile.getAbsolutePath());
    }
}
