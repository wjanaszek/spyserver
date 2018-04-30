package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpyServer extends Thread {

    private TasksObserver tasksObserver;

    public static void main(String... args) {
        new SpyServer().start();
    }

    SpyServer() {
        this.tasksObserver = new TasksObserver();
        this.tasksObserver.start();
    }

    public void run() {
        while (true) {
            try {
                log.info("ST - tasks:");
                tasksObserver.getTasks()
                        .entrySet()
                        .forEach(t -> log.info(t.toString()));

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("ST interrupted");
                e.printStackTrace();
            }
        }
    }

    public String startServer() {
        TasksObserver mq = new TasksObserver();
        mq.start();
        return "Server started";
    }
}