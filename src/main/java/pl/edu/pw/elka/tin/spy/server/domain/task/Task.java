package pl.edu.pw.elka.tin.spy.server.domain.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Task {

    private int id;
    private LocalDateTime timestamp;
    private int clientID;
    private String name;
    private TaskStatus taskStatus;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                "clientID=" + clientID +
                ", name='" + name + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
