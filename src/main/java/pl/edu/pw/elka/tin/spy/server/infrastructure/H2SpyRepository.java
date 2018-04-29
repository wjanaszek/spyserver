package pl.edu.pw.elka.tin.spy.server.infrastructure;


import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.task.TaskStatus;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class H2SpyRepository implements SpyRepository {
    private final static H2SpyRepository instance = new H2SpyRepository();

    private static final String DB_URL = "jdbc:h2:tcp://localhost//mnt/ubuntuDrive/h2test";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "12345";


    private Connection connection;

    private H2SpyRepository() {
        openConnection();
    }

    public static H2SpyRepository getInstance() {
        return instance;
    }

    public void openConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);
            log.info("Established connection to " + DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to open database connection");
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    @Override
    public List<Task> taskList() {
        String selectTaskSQL = "SELECT * FROM TASK";
        List<Task> tasks = new LinkedList<>();

        PreparedStatement stat;
        try {
            stat = connection.prepareStatement(selectTaskSQL);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                tasks.add(
                        new Task(
                                rs.getInt("ID"),
                                rs.getString("NAME"),
                                TaskStatus.fromString(rs.getString("STATUS")),
                                rs.getTimestamp("TIMESTAMP").toLocalDateTime()
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
}
