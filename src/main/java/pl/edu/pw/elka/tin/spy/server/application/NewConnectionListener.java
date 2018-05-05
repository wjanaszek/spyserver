package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NewConnectionListener {
    private final int serverPort = 9999;
    private final int backlog = 50;
    private ServerSocket serverSocket;
    private ThreadPoolExecutor poolExecutor;

    public NewConnectionListener() throws IOException {
        serverSocket = new ServerSocket(serverPort, backlog);
        poolExecutor = new ThreadPoolExecutor(30, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
        poolExecutor.submit(TasksObserver.observer());
    }

    public void run() {
        log.info("SpyServer started listening on new connection on port: " + serverPort);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.debug("Get new connection from: " + clientSocket.getRemoteSocketAddress());
                handleNewConnection(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Socket accept exception");
            }
        }
    }

    private void handleNewConnection(Socket socket) {
        ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();
        ClientReaderThread reader = new ClientReaderThread(queue, socket);
        ClientWriterThread writer = new ClientWriterThread(queue, socket);
        reader.addObserver(writer);
        poolExecutor.submit(reader);
        poolExecutor.submit(writer);
    }
}
