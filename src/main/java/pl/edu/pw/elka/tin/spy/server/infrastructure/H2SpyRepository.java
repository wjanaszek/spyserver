package pl.edu.pw.elka.tin.spy.server.infrastructure;


import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.task.Task;
import pl.edu.pw.elka.tin.spy.server.domain.task.TaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<Task> taskList() {
        String selectTaskSQL = "SELECT * FROM TASK";
        List<Task> tasks = new LinkedList<>();

        Connection connection = dbManager.getConnection();
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
