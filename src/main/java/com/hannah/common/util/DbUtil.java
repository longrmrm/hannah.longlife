package com.hannah.common.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author longrm
 * @date 2012-6-6
 */
public class DbUtil {

	public static String getDbClassName(String dbType) {
		dbType = dbType.toLowerCase();
		if ("oracle".equals(dbType))
			return "oracle.jdbc.driver.OracleDriver";
		else if ("db2".equals(dbType)) // jdbc 2.0
			return "com.ibm.db2.jcc.DB2Driver";
		else if ("mysql".equals(dbType)) // mysql 5
			return "com.mysql.jdbc.Driver";
		else if ("sqlserver".equals(dbType))
			return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		else if ("sybase".equals(dbType)) // jdbc 3.0
			return "com.sybase.jdbc3.jdbc.SybDriver";
		else if ("informix".equals(dbType))
			return "com.infoxmix.jdbc.IfxDriver";
		else if ("postgresql".equals(dbType))
			return "org.postgresql.Driver";
		else
			throw new RuntimeException("Database " + dbType + " is not recognized!");
	}

	public static String getDbUrl(String dbType, String serverIp, int port, String sid) {
		dbType = dbType.toLowerCase();
		if ("oracle".equals(dbType))
			return "jdbc:oracle:thin:@" + serverIp + ":" + port + ":" + sid;
		else if ("db2".equals(dbType))
			return "jdbc:db2://" + serverIp + ":" + port + "/" + sid;
		else if ("mysql".equals(dbType))
			return "jdbc:mysql://" + serverIp + ":" + port + "/" + sid;
		else if ("sqlserver".equals(dbType))
			return "jdbc:microsoft:sqlserver://" + serverIp + ":" + port + ";DatabaseName=" + sid;
		else if ("sybase".equals(dbType)) // ?charset=cp850&jconnect_version=6
			return "jdbc:sybase:Tds:" + serverIp + ":" + port + "/" + sid;
		else if ("informix".equals(dbType))
			return "jdbc:infoxmix-sqli://" + serverIp + ":" + port + "/" + sid + ":INFORMIXSERVER=myserver";
		else if ("postgresql".equals(dbType))
			return "jdbc:postgresql://" + serverIp + ":" + port + "/" + sid;
		else
			throw new RuntimeException("Database " + dbType + " is not recognized!");
	}

	public static Connection getConnection(String dbType, String serverIp, int port, String sid, String user,
			String password) throws Exception {
		return getConnection(dbType, getDbUrl(dbType, serverIp, port, sid), user, password);
	}

	/**
	 * get database connection
	 * @param dbType
	 * @param dbUrl
	 * @param user
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection(String dbType, String dbUrl, String user, String password) throws Exception {
		Class.forName(getDbClassName(dbType)).newInstance();
		return DriverManager.getConnection(dbUrl, user, password);
	}

	public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (null != rs)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (null != stmt)
				stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (null != conn && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			if (null != conn && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setStatementParameters(PreparedStatement pstmt, Object params[]) throws SQLException {
	    for (int i = 0; i < params.length; i++) {
	        Object obj = params[i];
	        if (null == obj)
	          pstmt.setNull(i + 1, 1);
	        else if (obj instanceof Date)
	          pstmt.setDate(i + 1, (Date) obj);
	        else if (obj instanceof java.util.Date) {
	          java.util.Date uDate = (java.util.Date) obj;
	          pstmt.setDate(i + 1, new Date(uDate.getTime()));
	        } else if (obj instanceof Timestamp)
	          pstmt.setTimestamp(i + 1, (Timestamp) obj);
	        else
	          pstmt.setObject(i + 1, obj);
	      }
	}

	/**
	 * query table's ResultSetMetaData
	 * @param conn
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static List queryTableMetaData(Connection conn, String table) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "select * from " + table + " where 1=0";
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			return convertMetaDataToList(metaData);
		} catch (SQLException e) {
			throw e;
		} finally {
			closeConnection(null, stmt, rs);
		}
	}

	public static List convertMetaDataToList(ResultSetMetaData metaData) throws SQLException {
		List metaDatas = new ArrayList();
		for (int column = 1; column <= metaData.getColumnCount(); column++) {
			Map map = new HashMap();
			map.put("CATALOG_NAME", metaData.getCatalogName(column));
			map.put("COLUMN_CLASS_NAME", metaData.getColumnClassName(column));
			map.put("COLUMN_DISPLAY_SIZE", metaData.getColumnDisplaySize(column));
			map.put("COLUMN_LABEL", metaData.getColumnLabel(column));
			map.put("COLUMN_NAME", metaData.getColumnName(column));
			map.put("COLUMN_TYPE", metaData.getColumnType(column));
			map.put("COLUMN_TYPE_NAME", metaData.getColumnTypeName(column));
			map.put("PRECISION", metaData.getPrecision(column));
			map.put("SCALE", metaData.getScale(column));
			map.put("TABLE_NAME", metaData.getTableName(column));
			metaDatas.add(map);
		}
		return metaDatas;
	}

	/**
	 * query column comments
	 * @param conn
	 * @param dbType
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static List queryColumnComments(Connection conn, String dbType, String table) throws SQLException {
		String sql = null;
		if ("oracle".equals(dbType.toLowerCase())) {
			sql = "select COLUMN_NAME, COMMENTS from USER_COL_COMMENTS where TABLE_NAME = '" + table + "'";
		} else
			return null;

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return DbUtil.convertResultSetToList(rs);
		} catch (SQLException e) {
			throw e;
		} finally {
			closeConnection(null, stmt, rs);
		}
	}

	/**
	 * query table's PrimaryKeys
	 * @param conn
	 * @param catalog
	 * @param schema
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static List queryPrimaryKeys(Connection conn, String catalog, String schema, String table)
			throws SQLException {
		ResultSet rs = null;
		try {
			rs = conn.getMetaData().getPrimaryKeys(catalog, schema, table);
			return convertResultSetToList(rs);
		} catch (SQLException e) {
			throw e;
		} finally {
			closeConnection(null, null, rs);
		}
	}

	public static List convertResultSetToList(ResultSet rs) throws SQLException {
		List list = new ArrayList();
		ResultSetMetaData metaData = rs.getMetaData();
		while (rs.next()) {
			Map map = new HashMap();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
				map.put(metaData.getColumnName(i), rs.getObject(i));
			list.add(map);
		}
		return list;
	}

	/**
	 * select paging sql (default is mysql format)
	 * @param dbType
	 * @param selectSql
	 * @param startRow first row is 0
	 * @param count
	 * @return
	 */
	public static String getSelectPagingSql(String dbType, String selectSql, String startRow, String count) {
		String selectPagingSql = selectSql;
		if ("oracle".equals(dbType.toLowerCase())) {
			String endRow = "(" + startRow + " + " + count + ")";
			startRow = "(" + startRow + " + 1)";
			selectPagingSql = "select t2.* from (" + "select t1.*, rownum rn from (" + selectSql
					+ ") t1 where rownum <= " + endRow + ") t2 where rn >= " + startRow;
		} else if ("mysql".equals(dbType.toLowerCase()))
			selectPagingSql = "select pt.* from (" + selectSql + ") pt limit " + startRow + ", " + count;
		else {
			// TODO other database
		}
		return selectPagingSql;
	}

}
