package com.lmars.crawler.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * URL工具类
 * @author REN
 * @date 2016年6月10日 下午1:34:47
 */
public class URLUtil {

	/**
	 * 从数据库中读取数据记录
	 * 
	 * @param startId 数据表中起始读取Id
	 * @param nums 一次读取的数量
	 * @param tbName 要读的数据库名
	 * @return 结果resultSet
	 */
	public static ResultSet getImageUrlSet(int startId, int nums, String tbName) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DBCPUtil.getConnection();
			statement = connection.prepareStatement("SELECT * FROM `"+tbName+"` WHERE id >= ? LIMIT 0,?");
			statement.setInt(1, startId);
			statement.setInt(2, nums);
			resultSet = statement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
}
