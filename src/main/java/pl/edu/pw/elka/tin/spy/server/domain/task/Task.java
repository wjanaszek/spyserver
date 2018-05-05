package pl.edu.pw.elka.tin.spy.server.domain.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
public class Task {

    private int id;
    private LocalDateTime creationTimestamp;
    private int userID;
    private String name;
    private TaskStatus taskStatus;
    private LocalDateTime lastUpdateTimestamp;
    private String fileURL;
}
