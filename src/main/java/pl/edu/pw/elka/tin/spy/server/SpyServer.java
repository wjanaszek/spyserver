package pl.edu.pw.elka.tin.spy.server;

public class SpyServer extends Thread {

    private WebMessageQueue webMessageQueue;

    public static void main(String... args) {
        (new SpyServer()).start();
    }

    SpyServer() {
        this.webMessageQueue = new WebMessageQueue();
        this.webMessageQueue.start();
    }

    public void run() {
        while (true) {
            try {
                System.out.println("ST - tasks:");
                for (Task t : webMessageQueue.getTasks()) {
                    System.out.println(t.toString());
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ST interrupted");
                e.printStackTrace();
            }
        }
    }

    public String startServer() {
        WebMessageQueue mq = new WebMessageQueue();
        mq.start();
        return "Server started";
    }
}
