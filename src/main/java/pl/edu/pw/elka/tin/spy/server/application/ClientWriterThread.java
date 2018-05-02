package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.*;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.infrastructure.SpyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ClientWriterThread implements Runnable {

    private Socket clientSocket;
    private TasksObserver tasksObserver;
    private DataOutputStream outputStream;
    private ConcurrentLinkedQueue<byte[]> rawMessageQueue;
    private Queue<Message> outputMessageQueue;
    private int clientID = 1;

    public ClientWriterThread(ConcurrentLinkedQueue<byte[]> queue, Socket socket) {
        clientSocket = socket;
        tasksObserver = TasksObserver.observer();
        rawMessageQueue = queue;
        outputMessageQueue = new LinkedList<>();
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {

            if (rawMessageQueue.size() > 0) {
                List<byte[]> rawMessages = new LinkedList<>();
                while (rawMessageQueue.size() > 0) {
                    rawMessages.add(rawMessageQueue.poll());
                }
                rawMessages.stream()
                        .map(RawMessageParser::parse)
                        .forEach(outputMessageQueue::add);
            }

            Task newTask = tasksObserver.fetchTask(1);
            if (newTask != null) {
                log.debug("Fetched new task. Adding to message Queue");
                outputMessageQueue.add(SimpleMessage.PhotoRequest);
            }

            Message newMessage = outputMessageQueue.poll();

            if (newMessage != null) {
                if (newMessage instanceof PhotoMessage) {
                    PhotoMessage photo = (PhotoMessage) newMessage;
                    try {
                        saveImage(photo.getPhoto());
                        tasksObserver.taskDone(clientID);
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("Failed to save photo");
                    }
                } else if (newMessage instanceof SendMessage) {
                    sendMessage(((SendMessage)newMessage).toByteArray());
                }
            }
            SpyUtils.sleep(5000);
        }
    }

    private void sendMessage(byte[] byteArray) {
        try {
            outputStream.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to send message");
        }
    }

    private void saveImage(byte[] image) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
        File outputFile = new File("saved.jpg");
        ImageIO.write(img, "jpg", outputFile);
        log.info("Saved photo");
    }
}
