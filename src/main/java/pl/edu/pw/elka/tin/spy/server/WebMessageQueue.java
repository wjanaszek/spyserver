package pl.edu.pw.elka.tin.spy.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebMessageQueue extends Thread {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String SELECT_SQL = "SELECT * FROM TASK";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";

    private long interval;
    private List<Task> tasks;

    public void run() {
        while (true) {
            try {
                checkForNewTasks();
                System.out.println("MQ");
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                System.out.println("mq interrupted");
                e.printStackTrace();
            }
        }
    }

    WebMessageQueue() {
        this.interval = 500;
        this.tasks = new ArrayList<Task>();
    }

    private void checkForNewTasks() {
        Connection conn = null;
        PreparedStatement stat;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            stat = conn.prepareStatement(SELECT_SQL);
            ResultSet rs = stat.executeQuery();
            tasks.clear();
            while (rs.next()) {
                System.out.println(rs.getInt("ID"));
                System.out.println(rs.getString("NAME"));
                tasks.add(
                    new Task(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        Task.Status.fromString(rs.getString("STATUS")),
                        rs.getString("TIMESTAMP")
                    )
                );
            }
            rs.close();
            stat.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public synchronized List<Task> getTasks() {
        return tasks;
    }
}
