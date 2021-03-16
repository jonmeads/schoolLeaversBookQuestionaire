package org.jpm.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jpm.config.AppConstants;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LeaverDataSource {

    private final static Logger LOGGER = Logger.getLogger(LeaverDataSource.class.getName());

    private static LeaverDataSource datasource;
    private ComboPooledDataSource cpds;

    private LeaverDataSource() throws PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://" + AppConstants.DB_NAME +"/leavers");
        cpds.setUser(AppConstants.DB_USER);
        cpds.setPassword(AppConstants.DB_PASS);

        cpds.setAcquireRetryAttempts(100);
        cpds.setMinPoolSize(4);
        cpds.setAcquireIncrement(2);
        cpds.setMaxPoolSize(10);
        cpds.setMaxStatements(200);
        cpds.setIdleConnectionTestPeriod(30);
        cpds.setTestConnectionOnCheckin(true);

    }

    public static LeaverDataSource getInstance() throws PropertyVetoException {
        if (datasource == null) {
            datasource = new LeaverDataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = this.cpds.getConnection();
        if(conn.isClosed()) {
            LOGGER.severe("bad connection, resetting pool");
            cpds.softResetAllUsers();
            conn = this.cpds.getConnection();
        }
        return conn;
    }

}
