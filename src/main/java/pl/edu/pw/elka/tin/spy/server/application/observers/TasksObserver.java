package pl.edu.pw.elka.tin.spy.server.application.observers;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.infrastructure.H2SpyRepository;
import pl.edu.pw.elka.tin.spy.server.infrastructure.SpyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
    private ConcurrentHashMap<Integer, Task> activeTasks;
    private Boolean logging = false;
    private String taskDirectory = System.getProperty("user.dir") + File.separator + "tasks";

    private TasksObserver() {
        log.debug("Starting task observer");

        this.interval = 5000;
        this.tasksQueue = new ConcurrentHashMap<>();
        this.activeTasks = new ConcurrentHashMap<>();
        createTaskResultDirectory(taskDirectory);
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
            checkForNewTasks();
            SpyUtils.sleep(interval);
        }
    }

    private void checkForNewTasks() {
        if (logging) {
            log.info("Updating task list");
        }

        List<Task> newTasks = repository.taskList(lastUpdateDT);
        newTasks.forEach( t -> {
            int userID = t.getUserID();
            Queue<Task> taskQueue = tasksQueue.get(userID);
            if (taskQueue == null) {
                ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();
                queue.add(t);
                tasksQueue.put(userID, queue);
            } else {
                taskQueue.add(t);
            }
        });
        if (logging) tasksQueue.entrySet().stream().map(Object::toString).forEach(log::info);
        lastUpdateDT = LocalDateTime.now();
    }

    public Task fetchTask(int userID) {
        ConcurrentLinkedQueue<Task> queue =  tasksQueue.get(userID);

        if (hasTasks(queue) && noActiveTask(userID)) {
            Task newTask = queue.poll();
            activeTasks.put(userID, newTask);
            return newTask;
        }

        return null;
    }

    private boolean hasTasks(ConcurrentLinkedQueue<Task> queue) {
        return queue != null && queue.size() > 0;
    }

    private boolean noActiveTask(int userID) {
        return activeTasks.getOrDefault(userID, null) == null;
    }

    public void taskDone(int userID, byte[] data) {
        Task task = activeTasks.get(userID);
        String filepath = taskDirectory + File.separator + task.getId() + ".jpg";
        try {
            saveImage(filepath, data);
            task.setFileURL(filepath);
            repository.markTaskDone(task);
            activeTasks.remove(userID);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image");
        }
    }

    private void saveImage(String filepath, byte[] image) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
        File outputFile = new File(filepath);
        ImageIO.write(img, "jpg", outputFile);
        log.info("Saved photo to: " + filepath);
    }

    private void createTaskResultDirectory(String path) {
        new File(path).mkdirs();
    }
}
