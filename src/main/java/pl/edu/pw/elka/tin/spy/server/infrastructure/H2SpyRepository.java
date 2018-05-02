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
                                rs.getInt("CLIENT_ID"),
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
            //Handle errors for JDBC
            se.printStackTrace();
        }

        return tasks;
    }

    private Timestamp toTimestamp(LocalDateTime dt) {
        return  dt != null ? Timestamp.valueOf(dt) : new Timestamp(0);
    }

    @Override
    public User addUser(String name) {
        String sql = "INSERT INTO USERS(NAME) VALUES (?)";

        Connection connection = dbManager.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, name);

            int affectedRows = stat.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Creating user failed");
            }

            stat.getGeneratedKeys().next();
            int userID = stat.getGeneratedKeys().getInt(1);

            return new User(userID, name, UserStatus.LOGOUT);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to create new user");
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
}
