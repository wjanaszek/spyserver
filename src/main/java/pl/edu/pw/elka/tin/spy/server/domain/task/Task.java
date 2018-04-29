package pl.edu.pw.elka.tin.spy.server.domain.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Task {

    private int id;
    private String name;
    private TaskStatus taskStatus;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", taskStatus=" + taskStatus +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
