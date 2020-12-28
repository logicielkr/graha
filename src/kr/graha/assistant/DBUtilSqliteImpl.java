/*
 *
 * Copyright (C) HeonJik, KIM
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package kr.graha.assistant;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import kr.graha.lib.LogHelper;
import java.util.Hashtable;
import java.util.Properties;
import java.io.IOException;

/**
 * Graha(그라하) Sqlite를 위한 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DBUtilSqliteImpl extends DBUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Hashtable<String, Integer> map = null;
	private boolean existsTableCommentTable = false;
	private boolean existsColumnCommentTable = false;
	public DBUtilSqliteImpl() throws IOException {
		LogHelper.setLogLevel(logger);
	}
	public String getToday() {
		return "current_timestamp";
	}
	public String getNextval(String tableName, String columnName) {
		return null;
	}
	public boolean existCommentTable(Connection con, boolean table) throws SQLException {
		if(table && this.existsTableCommentTable) {
			return true;
		} else if(!table && this.existsColumnCommentTable) {
			return true;
		}
		Table tab = null;
		if(table) {
			tab = super.getTable(con, con.getSchema(), "GRAHA_TAB_COMMENTS");
		} else {
			tab = super.getTable(con, con.getSchema(), "GRAHA_COL_COMMENTS");
		}
		if(tab == null) {
			return false;
		} else {
			return true;
		}
	}
	public void createCommentTable(Connection con, boolean table) throws SQLException {
		if(existCommentTable(con, table)) {
			if(table) {
				this.existsTableCommentTable = true;;
			} else {
				this.existsColumnCommentTable = true;
			}
			return;
		}
		PreparedStatement pstmt = null;
		String sql = null;
		if(table) {
			sql = "create table GRAHA_TAB_COMMENTS (\n";
			sql += "TABLE_NAME varchar(50),\n";
			sql += "COMMENTS varchar(50),\n";
			sql += "PRIMARY KEY (TABLE_NAME)\n";
			sql += ")";
		} else {
			sql = "create table GRAHA_COL_COMMENTS (\n";
			sql += "TABLE_NAME varchar(50),\n";
			sql += "COLUMN_NAME varchar(50),\n";
			sql += "COMMENTS varchar(50),\n";
			sql += "PRIMARY KEY (TABLE_NAME, COLUMN_NAME)\n";
			sql += ")";
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			if(table) {
				this.existsTableCommentTable = true;;
			} else {
				this.existsColumnCommentTable = true;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
	}
	public Hashtable<String, String> getTableComments(Connection con, String schemaName, String tableName) {
		return getComments(con, schemaName, tableName, true);
	}
	public Hashtable<String, String> getColumnComments(Connection con, String schemaName, String tableName) {
		return getComments(con, schemaName, tableName, false);
	}
	public Hashtable<String, String> getComments(Connection con, String schemaName, String tableName, boolean table) {
		Hashtable<String, String> comment = new Hashtable<String, String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		try {
			if(table) {
				sql = "select TABLE_NAME, COMMENTS from GRAHA_TAB_COMMENTS";
			} else {
				sql = "select TABLE_NAME, COLUMN_NAME, COMMENTS from GRAHA_COL_COMMENTS";
			}
			if(tableName != null) {
				sql += " where table_name = ?";
			}
			pstmt = con.prepareStatement(sql);
			if(tableName != null) {
				pstmt.setString(1, tableName);
			} 
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(table) {
					comment.put(rs.getString(1), rs.getString(2));
				} else {
					comment.put(rs.getString(1) + "." + rs.getString(2), rs.getString(3));
				}
			}
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			if(logger.isLoggable(Level.INFO)) {
				logger.info(LogHelper.toString(e));
			}
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		return comment;
	}
	public void updateTableRemarks(Connection con, String schemaName, String tableName, String remarks) throws SQLException {
		updateRemarks(con, schemaName, tableName, null, remarks);
	}
	public void updateColumnRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
		updateRemarks(con, schemaName, tableName, columnName, remarks);
	}
	public void updateRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
		if(columnName == null) {
			createCommentTable(con, true);
		} else {
			createCommentTable(con, false);
		}
		PreparedStatement pstmt = null;
		String sql = null;
		if(columnName == null) {
			sql = "update GRAHA_TAB_COMMENTS set COMMENTS = ? where TABLE_NAME = ?";
		} else {
			sql = "update GRAHA_COL_COMMENTS set COMMENTS = ? where TABLE_NAME = ? and COLUMN_NAME = ?";
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, remarks);
			pstmt.setString(2, tableName);
			if(columnName != null) {
				pstmt.setString(3, columnName);
			}
			pstmt.executeUpdate();
			int result = pstmt.getUpdateCount();
			pstmt.close();
			pstmt = null;
			if(result == 0) {
				if(columnName == null) {
					sql = "insert into GRAHA_TAB_COMMENTS (COMMENTS, TABLE_NAME) values (?, ?)";
				} else {
					sql = "insert into GRAHA_COL_COMMENTS (COMMENTS, TABLE_NAME, COLUMN_NAME) values (?, ?, ?)";
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, remarks);
				pstmt.setString(2, tableName);
				if(columnName != null) {
					pstmt.setString(3, columnName);
				}
				pstmt.executeUpdate();
				pstmt.getUpdateCount();
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
	}

	public Table getTable(Connection con, String schemaName, String tableName) throws SQLException {
		List<Table> l = getTables(con, schemaName, tableName);
		if(l.isEmpty()) {
			return null;
		} else {
			return l.get(0);
		}
	}
	public boolean supportSequence() {
		return false;
	}
	public List<Table> getTables(Connection con) throws SQLException {
		return getTables(con, null, null);
	}
	
	public List<Table> getTables(Connection con, String schemaName, String tableName) throws SQLException {
		List<Table> tabs = super.getTables(con, schemaName, tableName);
		Hashtable<String, String> comment = getTableComments(con, schemaName, tableName);
		for(Table tab : tabs){
			if(comment != null || !comment.isEmpty()) {
				if(comment.containsKey(tab.name)) {
					tab.remarks = comment.get(tab.name);
				}
			}
		}
		return tabs;
	}
	public List<Column> getColumns(Connection con, String schemaName, String tableName) throws SQLException {
		List<Column> cols = super.getColumns(con, schemaName, tableName);
		Hashtable<String, String> comment = getColumnComments(con, schemaName, tableName);
		for(Column col : cols){
			if(comment != null || !comment.isEmpty()) {
				if(comment.containsKey(tableName + "." + col.name)) {
					col.remarks = comment.get(tableName + "." + col.name);
				}
			}
		}
		return cols;
	}
}
