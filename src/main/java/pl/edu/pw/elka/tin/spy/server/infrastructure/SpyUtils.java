package pl.edu.pw.elka.tin.spy.server.infrastructure;

public class SpyUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) { }
    }
}
