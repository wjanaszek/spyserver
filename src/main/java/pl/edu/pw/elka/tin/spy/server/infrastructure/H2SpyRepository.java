package pl.edu.pw.elka.tin.spy.server.infrastructure;


import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.task.TaskStatus;

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
        String selectTaskSQL = "SELECT * FROM TASKS WHERE UPPER(STATUS) = ? and TIMESTAMP > ?";
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
                                rs.getTimestamp("TIMESTAMP").toLocalDateTime(),
                                rs.getInt("CLIENT_ID"),
                                rs.getString("NAME"),
                                TaskStatus.fromString(rs.getString("STATUS"))
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
}
