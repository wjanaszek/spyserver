package pl.edu.pw.elka.tin.spy.server.infrastructure;


import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.task.TaskStatus;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;
import pl.edu.pw.elka.tin.spy.server.domain.user.UserStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.toIntExact;

@Slf4j
public class H2SpyRepository implements SpyRepository {
    private final static H2SpyRepository instance = new H2SpyRepository();
    private final static DBManager dbManager = DBManager.manager();

    public static H2SpyRepository getInstance() {
        return instance;
    }

    @Override
    public List<Task> taskList(LocalDateTime lastUpdateDT) {
        String selectTaskSQL = "SELECT * FROM TASKS WHERE UPPER(STATUS) = ? and CREATION_TIMESTAMP > ?";
        List<Task> tasks = new LinkedList<>();

        Connection connection = dbManager.getConnection();
        PreparedStatement stat;
        try {
            stat = connection.prepareStatement(selectTaskSQL);
            stat.setString(1, TaskStatus.NEW.getText());
            stat.setTimestamp(2, toTimestamp(lastUpdateDT));
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                tasks.add(
                        new Task(
                                rs.getInt("ID"),
                                rs.getTimestamp("CREATION_TIMESTAMP").toLocalDateTime(),
                                rs.getInt("USER_ID"),
                                rs.getString("NAME"),
                                TaskStatus.fromString(rs.getString("STATUS")),
                                null,
                                null
                                )
                );
            }
            rs.close();
            stat.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return tasks;
    }

    private Timestamp toTimestamp(LocalDateTime dt) {
        return  dt != null ? Timestamp.valueOf(dt) : new Timestamp(0);
    }

    @Override
    public User addUser(String name, String password) {
        String sql = "INSERT INTO USERS(NAME, PASSWORD) VALUES (?, ?)";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, name);
            stat.setString(2, password);

            int affectedRows = stat.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Creating user failed");
            }

            long userID;
            ResultSet generatedKeys = stat.getGeneratedKeys();
            if (generatedKeys.next()) {
                userID = generatedKeys.getLong(1);
            } else {
                throw new RuntimeException("Failed to get generated primary key");
            }

            return new User(toIntExact(userID), name, password, UserStatus.LOGOUT);
        } catch (SQLException e) {
           throw new IllegalArgumentException("User name already taken");
        }
    }

    @Override
    public User updateUserStatus(User user, UserStatus newStatus) {
        String sql = "UPDATE USERS SET STATUS = ? WHERE USER_ID = ?";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, newStatus.getText());
            stat.setInt(2, user.getID());

            int affectedRows = stat.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Updating user status failed");
            }

            user.setStatus(newStatus);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to update user");
    }

    @Override
    public User authenticateUser(int userID, String password) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ? AND PASSWORD = ?";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setInt(1, userID);
            stat.setString(2, password);

            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                return new User(
                        userID,
                        rs.getString("NAME"),
                        null,
                        UserStatus.fromString(rs.getString("STATUS"))
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to authenticate user");
    }

    @Override
    public Task markTaskDone(Task task) {
        String sql = "UPDATE TASKS SET STATUS = ?, FILE_URL = ?, LAST_UPDATE_TIMESTAMP = ? WHERE ID = ?";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, TaskStatus.DONE.getText());
            stat.setString(2, task.getFileURL());
            stat.setTimestamp(3, toTimestamp(LocalDateTime.now()));
            stat.setInt(4, task.getId());

            int affectedRows = stat.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Updating task failed");
            }

            task.setTaskStatus(TaskStatus.DONE);
            return task;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to update task");
    }

    @Override
    public void resetUsersStatuses() {
        String sql = "UPDATE USERS SET STATUS = ?";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, UserStatus.LOGOUT.getText());
            stat.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to reset users statuses");
        }
    }
}
