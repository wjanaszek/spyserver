package pl.edu.pw.elka.tin.spy.server.domain;

import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;
import pl.edu.pw.elka.tin.spy.server.domain.user.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface SpyRepository {

    List<Task> taskList(LocalDateTime lastUpdateDT);
    Task markTaskDone(Task task);
    User addUser(String name, String password);
    User updateUserStatus(User user, UserStatus newStatus);
    User authenticateUser(int userID, String password);
    void resetUsersStatuses();
}
