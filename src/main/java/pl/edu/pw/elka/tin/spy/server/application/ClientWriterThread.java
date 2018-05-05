package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.RawMessageParser;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.message.*;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;
import pl.edu.pw.elka.tin.spy.server.infrastructure.SecretGenerator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ClientWriterThread implements Runnable, Observer {

    private Socket clientSocket;
    private TasksObserver tasksObserver;
    private UsersObserver usersObserver;
    private DataOutputStream outputStream;
    private ConcurrentLinkedQueue<byte[]> rawMessageQueue;
    private Queue<Message> outputMessageQueue;
    private User activeUser;
    private String sessionSecret;
    private boolean isRunning = true;

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
        while (isRunning) {
            if (rawMessageQueue.size() > 0) {
                List<byte[]> rawMessages = new LinkedList<>();
                while (rawMessageQueue.size() > 0) {
                    rawMessages.add(rawMessageQueue.poll());
                }
                rawMessages.stream()
                        .map(RawMessageParser::parse)
                        .forEach(outputMessageQueue::add);
            }

            if (authenticatedUser()) {
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
        switch (message.header()) {
            case REGISTRATION_REQUEST: {
                RegistrationRequest request = (RegistrationRequest) message;
                try {
                    User user = usersObserver.registerUser(request.getName(), request.getPassword());
                    log.info("Added new user: " + user.getName());
                    sendMessage(new SuccessfulRegistrationMessage(user.getID()));
                } catch (IllegalArgumentException e) {
                    sendMessage(SimpleMessage.RegistrationFailed);
                }
                break;
            }
            case AUTHENTICATION_REQUEST: {
                AuthRequest request = (AuthRequest) message;
                try {
                    activeUser = usersObserver.authenticateUser(request.getUserID(), request.getPassword());
                    log.info("User " + activeUser.getName() + " is active");
                    sessionSecret = SecretGenerator.generate(32);
                    sendMessage(new SuccessfulAuthMessage(sessionSecret));
                } catch (IllegalArgumentException e) {
                    sendMessage(SimpleMessage.AuthFailed);
                }
                break;
            }
            case PHOTO_REQUEST: {
                if (authenticatedUser()) {
                    PhotoMessage photo = (PhotoMessage) message;
                    tasksObserver.taskDone(activeUser.getID(), photo.getPhoto());
                } else {
                    sendMessage(SimpleMessage.UnauthorizedRequest);
                }
                break;
            }
        }
    }

    private boolean authenticatedUser() {
        return activeUser != null;
    }

    @Override
    public void update(Observable observable, Object o) {
        isRunning = false;
    }

    private void sendMessage(SendMessage message) {
        try {
            outputStream.write(message.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to send message");
        }
    }
}
