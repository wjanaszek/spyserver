package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ClientReaderThread implements Runnable {

    private Socket clientSocket;
    private TasksObserver tasksObserver;
    private DataInputStream inputStream;
    private ConcurrentLinkedQueue<byte[]> messageQueue;


    public ClientReaderThread(ConcurrentLinkedQueue<byte[]> queue, Socket socket) {
        this.clientSocket = socket;
        this.tasksObserver = TasksObserver.observer();
        this.messageQueue = queue;

        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                int messageLength = inputStream.readInt();
                if(messageLength > 0) {
                    byte[] message = new byte[messageLength];
                    inputStream.readFully(message, 0, message.length);
                    messageQueue.add(message);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to get input stream");
            }
        }
    }

    //TODO: ugly as hell
    private void readImage(InputStream is) throws IOException {
//        DataInputStream dIn = new DataInputStream(is);
//        int length = dIn.readInt();                    // read length of incoming message
//        if(length>0) {
//            byte[] message = new byte[length];
//            dIn.readFully(message, 0, message.length); // read the message
//            BufferedImage img = ImageIO.read(new ByteArrayInputStream(message));
//            File outputFile = new File("saved.jpg");
//            ImageIO.write(img, "jpg", outputFile);
//            log.info("Saved photo");
//            tasksObserver.taskDone(clientID);
//        }
    }
}
