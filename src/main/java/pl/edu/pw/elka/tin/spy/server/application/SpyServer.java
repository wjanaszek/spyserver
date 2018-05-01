package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public class SpyServer extends Thread {

    static final int PORT = 8000;
    static final int SOCKET_TIMEOUT = 180000;

    private ServerSocket serverSocket;
    private Socket server;
    private TasksObserver tasksObserver;

    public static void main(String... args) {
        try {
            new SpyServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    SpyServer() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        this.serverSocket.setSoTimeout(SOCKET_TIMEOUT);
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

                // get photo from client
                try {
                    server = serverSocket.accept();
                    BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
                    File outputFile = new File("saved.jpg");
                    ImageIO.write(img, "jpg", outputFile);
                    log.info("ST - saved photo");
                }
                catch(SocketTimeoutException st) {
                    System.out.println("Socket timed out!");
                    break;
                }
                catch(IOException e) {
                    e.printStackTrace();
                    break;
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }

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