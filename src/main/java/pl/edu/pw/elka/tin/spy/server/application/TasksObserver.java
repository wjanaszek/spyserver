package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.infrastructure.H2SpyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class TasksObserver extends Thread {

    private SpyRepository repository = H2SpyRepository.getInstance();
    private long interval;
    private LocalDateTime lastUpdateDT;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Task>> tasks;

    public void run() {
        while (true) {
            try {
                checkForNewTasks();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                log.info("interrupted");
                e.printStackTrace();
            }
        }
    }

    TasksObserver() {
        this.interval = 5000;
        this.tasks = new ConcurrentHashMap<>();
    }

    private void checkForNewTasks() {
        //TODO: trzeba ogarnąć jak GC będzie sobie radził
        log.info("Updating task list");

        List<Task> newTasks = repository.taskList(lastUpdateDT);
        newTasks.forEach( t -> {
            int clientID = t.getClientID();
            Queue<Task> taskQueue = tasks.get(clientID);
            if (taskQueue == null) {
                ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();
                queue.add(t);
                tasks.put(clientID, queue);
            } else {
                taskQueue.add(t);
            }
        });
        tasks.entrySet().stream().map(Object::toString).forEach(log::info);
        lastUpdateDT = LocalDateTime.now();
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Task>>  getTasks() {
        return tasks;
    }
}
