package pl.edu.pw.elka.tin.spy.server;

public class SpyServer {
    public static void main(String... args) {
        SpyServer spyServer = new SpyServer();
        System.out.println(spyServer.startServer());
    }

    public String startServer() {
        return "Server started";
    }
}
