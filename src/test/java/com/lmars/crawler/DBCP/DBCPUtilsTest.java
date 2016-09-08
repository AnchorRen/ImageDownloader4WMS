package com.lmars.crawler.DBCP;

import java.sql.Connection;
import java.sql.SQLException;

import com.lmars.crawler.utils.DBCPUtil;


public class DBCPUtilsTest {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DBCPUtil.getConnection();
		System.out.println(conn.getClass().getName());
		conn.close();
	}
}
