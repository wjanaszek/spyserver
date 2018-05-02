package pl.edu.pw.elka.tin.spy.server.domain;

import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;
import pl.edu.pw.elka.tin.spy.server.domain.user.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface SpyRepository {

    List<Task> taskList(LocalDateTime lastUpdateDT);
    User addUser(String name);
    User updateUserStatus(User user, UserStatus newStatus);
}
