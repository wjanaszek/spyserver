package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Header;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NewConnectionListener {

    private final int serverPort = 9999;
    private final int backlog = 50;
    private final static String SEPARATOR = ":";
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
                if (registerOrAuthenticateUser(clientSocket.getInputStream())) {
                    log.info("User authenticated or registred");
                    ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();
                    poolExecutor.submit(new ClientReaderThread(queue, clientSocket));
                    poolExecutor.submit(new ClientWriterThread(queue, clientSocket));
                } else {
                    log.info("User not recognised");
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeInt(3);
                    dos.write(Header.UNRECOGNISED.toString().getBytes(StandardCharsets.UTF_8));
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Socket accept exception");
            }
        }

    }

    /**
     * @param inputStream
     * @return true, if registering or authenticating will be successful
     * @throws IOException
     */
    private boolean registerOrAuthenticateUser(InputStream inputStream) throws IOException {
        DataInputStream dIn = new DataInputStream(inputStream);
        int length = dIn.readInt();
        byte[] result = new byte[length];
        dIn.readFully(result, 0, length);
        String messages[] = new String(result, StandardCharsets.UTF_8).split(SEPARATOR);
        String header = messages[0];
        if (header.equals(Header.REGISTER.getValue())) {
            log.info("Register request");
            UsersObserver.observer().registerUser(messages[1]);
            return true;
        } else if (header.equals(Header.AUTHENTICATE.getValue())) {
            log.info("Authenticate request");
//            UsersObserver.observer().logInUser();
            return true;
        } else {
            return false;
        }
    }
}
