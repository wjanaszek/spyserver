package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.protocol.Headers;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.infrastructure.SpyUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ClientWriterThread implements Runnable {

    private Socket clientSocket;
    private TasksObserver tasksObserver;
    private DataOutputStream outputStream;

    public ClientWriterThread(Socket socket) {
        clientSocket = socket;
        tasksObserver = TasksObserver.observer();
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            Task newTask = tasksObserver.fetchTask(1);
            if (newTask != null) {
                log.debug("Fetched new task. Sending request");
                byte[] byteArray = Headers.PHOTO_REQUEST.getValue().getBytes(StandardCharsets.UTF_8);
                sendMessage(byteArray);
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
}
