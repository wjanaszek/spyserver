package pl.edu.pw.elka.tin.spy.server.infrastructure;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {

    private static final String DB_URL = "jdbc:h2:tcp://localhost//mnt/ubuntuDrive/h2test";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "12345";
    private static final String DB_DRIVER_CLASSNAME = "org.h2.Driver";

    private static volatile DBManager dbManager;
    private BasicDataSource dataSource;

    private DBManager() {
        dataSource = initDataSource();
    }

    public static DBManager manager() {
        if (dbManager == null) {
            synchronized (DBManager.class) {
                if (dbManager == null) {
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }

    private static BasicDataSource initDataSource() {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName(DB_DRIVER_CLASSNAME);
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        ds.setMaxTotal(20);
        ds.setMaxWaitMillis(1000);

        return ds;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            dataSource.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
