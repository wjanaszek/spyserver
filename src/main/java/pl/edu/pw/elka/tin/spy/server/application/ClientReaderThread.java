package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

@Slf4j
public class ClientReaderThread implements Runnable {

    private Socket clientSocket;
    private TasksObserver tasksObserver;
    private int clientID;

    public ClientReaderThread(Socket socket, int clientID) {
        this.clientSocket = socket;
        this.clientID = clientID;
        tasksObserver = TasksObserver.observer();
    }

    @Override
    public void run() {
        while (true) {
            try {
                readImage(clientSocket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Failed to get input stream");
            }
        }
    }

    //TODO: ugly as hell
    private void readImage(InputStream is) throws IOException {
        DataInputStream dIn = new DataInputStream(is);
        int length = dIn.readInt();                    // read length of incoming message
        if(length>0) {
            byte[] message = new byte[length];
            dIn.readFully(message, 0, message.length); // read the message
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(message));
            File outputFile = new File("saved.jpg");
            ImageIO.write(img, "jpg", outputFile);
            log.info("Saved photo");
            tasksObserver.taskDone(clientID);
        }
    }
}
