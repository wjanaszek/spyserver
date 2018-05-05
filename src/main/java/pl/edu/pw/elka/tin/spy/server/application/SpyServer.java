package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SpyServer extends Thread {

    private NewConnectionListener listener;

    public static void main(String... args) {
        try {
            new SpyServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SpyServer() throws IOException {
        this.listener = new NewConnectionListener();
    }

    public void run() {
        listener.run();
    }
}