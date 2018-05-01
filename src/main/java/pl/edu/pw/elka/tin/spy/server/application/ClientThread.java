package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class ClientThread implements Runnable {

    private Socket clientSocket;

    public ClientThread(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            readImage(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get input stream");
        }
    }

    //TODO: ugly as hell
    private void readImage(InputStream is) throws IOException {
        BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(is));
        File outputFile = new File("saved.jpg");
        try {
            ImageIO.write(img, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to save photo");
        }
        log.info("ST - saved photo");
    }
}
