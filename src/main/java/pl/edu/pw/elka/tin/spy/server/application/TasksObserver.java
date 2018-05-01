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
public class TasksObserver implements Runnable {

    private static volatile TasksObserver instance;
    private SpyRepository repository = H2SpyRepository.getInstance();
    private long interval;
    private LocalDateTime lastUpdateDT;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Task>> tasksQueue;
    private ConcurrentHashMap<Integer, Boolean> activeTasks;

    private TasksObserver() {
        log.debug("Starting task observer");

        this.interval = 5000;
        this.tasksQueue = new ConcurrentHashMap<>();
        this.activeTasks = new ConcurrentHashMap<>();
    }

    public static TasksObserver observer() {
        if (instance == null) {
            synchronized (TasksObserver.class) {
                if (instance == null) {
                    instance = new TasksObserver();
                }
            }
        }
        return instance;
    }

    @Override
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

    private void checkForNewTasks() {
        //TODO: trzeba ogarnąć jak GC będzie sobie radził
        log.info("Updating task list");

        List<Task> newTasks = repository.taskList(lastUpdateDT);
        newTasks.forEach( t -> {
            int clientID = t.getClientID();
            Queue<Task> taskQueue = tasksQueue.get(clientID);
            if (taskQueue == null) {
                ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();
                queue.add(t);
                tasksQueue.put(clientID, queue);
            } else {
                taskQueue.add(t);
            }
        });
        tasksQueue.entrySet().stream().map(Object::toString).forEach(log::info);
        lastUpdateDT = LocalDateTime.now();
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Task>> getTasksQueue() {
        return tasksQueue;
    }

    public Task fetchTask(int clientID) {
        ConcurrentLinkedQueue<Task> queue =  tasksQueue.get(clientID);
        if (queue != null && !activeTasks.getOrDefault(clientID, false)) {
            activeTasks.put(clientID, true);
            return queue.poll();
        } else {
            return null;
        }
    }
    public void taskDone(int clientID) {
        activeTasks.put(clientID, false);
    }
}
