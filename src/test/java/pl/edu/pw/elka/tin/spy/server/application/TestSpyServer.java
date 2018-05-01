package pl.edu.pw.elka.tin.spy.server.application;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestSpyServer {

    private String message = "Server started";
    private SpyServer spyServer;

    @Test
    public void testStartServer() {
        try {
            spyServer = new SpyServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(message, spyServer.startServer());
    }
}