package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.infrastructure.H2SpyRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebMessageQueue extends Thread {

    private SpyRepository repository = H2SpyRepository.getInstance();
    private long interval;
    private List<Task> tasks;

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

    WebMessageQueue() {
        this.interval = 5000;
        this.tasks = new ArrayList<>();
    }

    private void checkForNewTasks() {
        //TODO: trzeba ogarnąć jak GC będzie sobie radził
        log.info("Updating task list");

        tasks = repository.taskList();
        tasks.stream().map(Object::toString).forEach(log::info);
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public synchronized List<Task> getTasks() {
        return tasks;
    }
}
