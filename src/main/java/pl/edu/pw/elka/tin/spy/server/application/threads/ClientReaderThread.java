package pl.edu.pw.elka.tin.spy.server.application.threads;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.infrastructure.SpyUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ClientReaderThread extends Observable implements Runnable {

    private Socket clientSocket;
    private DataInputStream inputStream;
    private ConcurrentLinkedQueue<byte[]> messageQueue;

    public ClientReaderThread(ConcurrentLinkedQueue<byte[]> queue, Socket socket) {
        this.clientSocket = socket;
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
                log.debug("Lost connection with " + clientSocket.getRemoteSocketAddress());
                notifyListeners();
                SpyUtils.closeSocket(clientSocket);
                return;
            }
        }
    }

    private void notifyListeners() {
        setChanged();
        notifyObservers();
    }
}
