package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.RawMessageParser;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.message.*;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;

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
    private UsersObserver usersObserver;
    private DataOutputStream outputStream;
    private ConcurrentLinkedQueue<byte[]> rawMessageQueue;
    private Queue<Message> outputMessageQueue;
    private Integer clientID;

    public ClientWriterThread(ConcurrentLinkedQueue<byte[]> queue, Socket socket) {
        clientSocket = socket;
        tasksObserver = TasksObserver.observer();
        usersObserver = UsersObserver.observer();
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

            if (clientID != null) {
                Task newTask = tasksObserver.fetchTask(1);
                if (newTask != null) {
                    log.debug("Fetched new task. Adding to message Queue");
                    outputMessageQueue.add(SimpleMessage.PhotoRequest);
                }
            }

            Message newMessage = outputMessageQueue.poll();

            if (newMessage != null) {
                handleMessage(newMessage);
            }
        }
    }

    private void handleMessage(Message message) {
        if (message instanceof PhotoMessage) {
            PhotoMessage photo = (PhotoMessage) message;
            try {
                saveImage(photo.getPhoto());
                tasksObserver.taskDone(clientID);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Failed to save photo");
            }
        } else if (message instanceof RegistrationRequest) {
            RegistrationRequest request = (RegistrationRequest)message;
            User user = usersObserver.registerUser(request.getName(), request.getPassword());
            log.info("Added new user: " + user.getName());
            sendMessage(new SuccessfullRegistrationMessage(user.getID()));
        } else if (message instanceof SendMessage) {
            sendMessage((SendMessage)message);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            outputStream.write(message.toByteArray());
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
