package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;
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
}
