package com.lmars.crawler.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtils {

	public Connection conn = null;
	public Statement stmt = null; 
	public ResultSet rs = null; 
	private static String propFileName = "connDB.properties"; // 指定资源文件保存的位置
	private static Properties prop = new Properties(); // 创建并实例化Properties对象的实例
	private static String dbClassName = "com.mysql.jdbc.Driver";
	private static String dbUrl= "jdbc:mysql://127.0.0.1:3306/data_gov?user=root&password=root&useUnicode=true";

	public JDBCUtils() { 
		try { 
			InputStream in = getClass().getResourceAsStream(propFileName);
			prop.load(in); // 通过输入流对象加载Properties文件
			dbClassName = prop.getProperty("DB_CLASS_NAME"); // 获取数据库驱动
			dbUrl = prop.getProperty("DB_URL", dbUrl);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	/**
	 * 功能：获取连接的语句
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try { 
			Class.forName(dbClassName).newInstance(); 
			conn = DriverManager.getConnection(dbUrl); 
		} catch (Exception ee) {
			ee.printStackTrace(); 
		}
		if (conn == null) {
			System.err
					.println("警告: DbConnectionManager.getConnection() 获得数据库链接失败.\r\n\r\n链接类型:"
							+ dbClassName + "\r\n链接位置:" + dbUrl); // 在控制台上输出提示信息
		}
		return conn; // 返回数据库连接对象
	}
	/*
	 * 功能：执行查询语句
	 */
	public ResultSet executeQuery(String sql) {
		try { 
			conn = getConnection(); 
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		return rs; // 返回结果集对象
	}

	/*
	 * 功能:执行更新操作
	 */
	public int executeUpdate(String sql) {
		int result = 0; 
		try { 
			conn = getConnection(); 
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			result = stmt.executeUpdate(sql); // 执行更新操作
		} catch (SQLException ex) {
			result = 0; // 将保存返回值的变量赋值为0
		}
		return result; // 返回保存返回值的变量
	}
	/*
	 * 功能:关闭数据库的连接
	 */
	public static void release(ResultSet rs, Statement stmt, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}
}
