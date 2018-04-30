package pl.edu.pw.elka.tin.spy.server.domain;

import pl.edu.pw.elka.tin.spy.server.domain.task.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface SpyRepository {

    List<Task> taskList(LocalDateTime lastUpdateDT);
}
