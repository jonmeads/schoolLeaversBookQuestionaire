package org.jpm.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jpm.config.AppConstants;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class LeaverDataSource {

    private static LeaverDataSource datasource;
    private ComboPooledDataSource cpds;

    private LeaverDataSource() throws PropertyVetoException {
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://" + AppConstants.DB_NAME +"/leavers");
        cpds.setUser(AppConstants.DB_USER);
        cpds.setPassword(AppConstants.DB_PASS);

        cpds.setMinPoolSize(4);
        cpds.setAcquireIncrement(2);
        cpds.setMaxPoolSize(10);
        cpds.setMaxStatements(200);

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
        return this.cpds.getConnection();
    }

}
