package pl.edu.pw.elka.tin.spy.server.infrastructure;

import java.io.IOException;
import java.net.Socket;

public class SpyUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) { }
    }

    public static void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) { }
    }
}
