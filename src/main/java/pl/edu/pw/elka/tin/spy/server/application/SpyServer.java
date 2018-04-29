package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpyServer extends Thread {

    private WebMessageQueue webMessageQueue;

    public static void main(String... args) {
        new SpyServer().start();
    }

    SpyServer() {
        this.webMessageQueue = new WebMessageQueue();
        this.webMessageQueue.start();
    }

    public void run() {
        while (true) {
            try {
                log.info("ST - tasks:");
                webMessageQueue.getTasks().forEach(t -> log.info(t.toString()));

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