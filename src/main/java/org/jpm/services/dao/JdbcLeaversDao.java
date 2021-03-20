package org.jpm.services.dao;

import org.jpm.models.Leaver;
import org.jpm.utils.LeaverDataSource;
import org.springframework.stereotype.Service;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class JdbcLeaversDao {

    private final static Logger LOGGER = Logger.getLogger(JdbcLeaversDao.class.getName());


    public JdbcLeaversDao() {
    }



    public void saveForm(String session, String name) {
        saveSession(session, name);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            LOGGER.info("Saving form information for session id: "+ session +", and name: " + name);

            conn = LeaverDataSource.getInstance().getConnection();
            statement = conn.prepareStatement("update leavers set form  = ? where session = ?");
            statement.setInt(1, 1);
            statement.setString(2, session);

            int recs = statement.executeUpdate();
            if(recs > 0) {
                LOGGER.severe("Successfully updated form session information: " + recs);
            }
        } catch (SQLException | PropertyVetoException e) {
            LOGGER.severe("Failed to save form session information " + e);
        } finally {

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {  }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {  }
            }
        }
    }

    public void saveBaby(String session, String name) {
        saveSession(session, name);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            LOGGER.info("Saving baby information for session id: "+ session +", and name: " + name);
            conn = LeaverDataSource.getInstance().getConnection();
            statement = conn.prepareStatement("update leavers set baby = ? where session = ?");
            statement.setInt(1, 1);
            statement.setString(2, session);

            int recs = statement.executeUpdate();
            if(recs > 0) {
                LOGGER.severe("Successfully updated baby session information: " + recs);
            }
        } catch (SQLException | PropertyVetoException e) {
            LOGGER.severe("Failed to save baby session information " + e);
        } finally {

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {  }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {  }
            }
        }
    }

    protected void saveSession(String session, String name) {

        if(!leaverExists(session)) {

            Connection conn = null;
            PreparedStatement statement = null;

            LOGGER.info("Creation session information for session id: "+ session +", and name: " + name);
            try {
                conn = LeaverDataSource.getInstance().getConnection();
                statement = conn.prepareStatement("insert into leavers (session, name) select ?,?");
                statement.setString(1, session);
                statement.setString(2, name);

                int recs = statement.executeUpdate();
                if(recs > 0) {
                    LOGGER.severe("Successfully created session information: " + recs);
                }

            } catch (SQLException | PropertyVetoException e) {
                LOGGER.severe("Failed to save session information " + e);
            } finally {

                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {  }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {  }
                }
            }
        }
    }


    public Leaver getLeaver(String session) {
        Leaver leaver = new Leaver(session);

        List<Leaver> leavers = getLeavers(session);

        if (leavers != null && !leavers.isEmpty()) {
            leaver = leavers.get(0);
        }
        return leaver;
    }

    protected boolean leaverExists(String session) {
        boolean result = false;

        List<Leaver> leavers = getLeavers(session);

        if (leavers != null && !leavers.isEmpty()) {
            result = true;
        }
        return result;
    }

    protected List<Leaver> getLeavers(String session) {
        List<Leaver> leavers = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            conn = LeaverDataSource.getInstance().getConnection();
            statement = conn.prepareStatement("select session, name, form, baby from leavers where session = ?");
            statement.setString(1, session);
            rs = statement.executeQuery();

            while (rs.next()) {
                String sess = rs.getString("session");
                String name = rs.getString("name");
                Integer form = rs.getInt("form");
                Integer baby = rs.getInt("baby");

                Leaver leaver = new Leaver(sess,name, form, baby);
                leavers.add(leaver);
            }
            rs.close();

        } catch (SQLException | PropertyVetoException e) {
            LOGGER.severe("Failed to save session information " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {  }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {  }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {  }
            }
        }

        return leavers;
    }

}
