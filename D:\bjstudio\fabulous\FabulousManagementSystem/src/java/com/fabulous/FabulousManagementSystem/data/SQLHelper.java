/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.data;

import com.fabulous.FabulousManagementSystem.configuration.ConfigurationManager;
import java.sql.*;
import java.util.logging.*;

/**
 *
 * @author O-O
 */
public class SQLHelper {

    static {
        try {
            Class.forName(ConfigurationManager.getDbDriver());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 不允许实例化该类
     */
    private SQLHelper() {
    }

    /**
     * 获取一个数据库连接 通过设置类的 driver / url / user / password 这四个静态变量来 设置数据库连接属性
     *
     * @return 数据库连接
     * @throws java.sql.SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(ConfigurationManager.getDbUrl(), ConfigurationManager.getDbUser(), ConfigurationManager.getDbPassword());
    }

    /**
     * 获取一个 Statement 该 Statement 已经设置数据集 可以滚动,不可以更新
     *
     * @param conn 数据库连接
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     * @throws java.sql.SQLException
     */
    public static Statement getStatement(Connection conn) throws SQLException {
        return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * 获取一个带参数的 PreparedStatement 该 PreparedStatement 已经设置数据集 可以滚动,不可以更新
     *
     * @param conn 数据库连接
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return 如果获取失败将返回 null,调用时记得检查返回值
     * @throws java.sql.SQLException
     */
    public static PreparedStatement getPreparedStatement(Connection conn, String cmdText, Object... cmdParams) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(cmdText, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int i = 1;
        for (Object item : cmdParams) {
            pstmt.setObject(i, item);
            i++;
        }
        return pstmt;
    }

    /**
     * 执行 SQL 语句,返回结果为整型 主要用于执行非查询语句
     *
     * @param conn
     * @param cmdText SQL 语句
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     * @throws java.sql.SQLException
     */
    public static int ExecSql(Connection conn, String cmdText) throws SQLException {
        Statement stmt = getStatement(conn);
        return stmt.executeUpdate(cmdText);
    }

    /**
     * 执行 SQL 语句,返回结果为整型 主要用于执行非查询语句
     *
     * @param conn 数据库连接
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return 非负数:正常执行; -1:执行错误; -2:连接错误
     * @throws java.sql.SQLException
     */
    public static int ExecSql(Connection conn, String cmdText, Object... cmdParams) throws SQLException {
        PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);
        return pstmt.executeUpdate();
    }

    /**
     * 返回一个 ResultSet
     *
     * @param conn
     * @param cmdText SQL 语句
     * @return
     * @throws java.sql.SQLException
     */
    public static ResultSet getResultSet(Connection conn, String cmdText) throws SQLException {
        Statement stmt = getStatement(conn);
        return stmt.executeQuery(cmdText);
    }

    /**
     * 返回一个 ResultSet
     *
     * @param conn 数据库连接
     * @param cmdText 需要 ? 参数的 SQL 语句
     * @param cmdParams SQL 语句的参数表
     * @return
     * @throws java.sql.SQLException
     */
    public static ResultSet getResultSet(Connection conn, String cmdText, Object... cmdParams) throws SQLException {
        PreparedStatement pstmt = getPreparedStatement(conn, cmdText, cmdParams);
        return pstmt.executeQuery();
    }

    public static void close(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Statement) {
                ((Statement) obj).close();
            } else if (obj instanceof PreparedStatement) {
                ((PreparedStatement) obj).close();
            } else if (obj instanceof ResultSet) {
                ((ResultSet) obj).close();
            } else if (obj instanceof Connection) {
                ((Connection) obj).close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeConnection(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Statement) {
                ((Statement) obj).getConnection().close();
            } else if (obj instanceof PreparedStatement) {
                ((PreparedStatement) obj).getConnection().close();
            } else if (obj instanceof ResultSet) {
                ((ResultSet) obj).getStatement().getConnection().close();
            } else if (obj instanceof Connection) {
                ((Connection) obj).close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
