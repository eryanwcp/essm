package com.eryansky.codegen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.eryansky.codegen.util.Resources;
import com.eryansky.codegen.vo.DbConfig;
import org.apache.commons.lang3.Validate;

/**
 * 数据库连接
 */
public class DbConnection {

    private DbConfig dbConfig;

    public DbConnection(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }


    /**
     * 获取连接
     *
     * @return
     */
    public Connection getConn() {
        Validate.notNull(dbConfig, "属性[dbConfig]不能为空.");
        Connection conn = null;
        try {
            Class.forName(dbConfig.getDriverClassName()).newInstance();
            conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
}
