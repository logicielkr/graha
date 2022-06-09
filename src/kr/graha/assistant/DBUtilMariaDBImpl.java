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
import kr.graha.helper.LOG;
import java.util.Hashtable;
import java.util.Properties;
import java.io.IOException;


/**
 * Graha(그라하) MariaDB를 위한 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DBUtilMariaDBImpl extends DBUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Hashtable<String, Integer> map = null;
	private boolean existsTableCommentTable = false;
	private boolean existsColumnCommentTable = false;
	protected DBUtilMariaDBImpl() throws IOException {
		LOG.setLogLevel(logger);
	}
	protected String dateFormat(String columnName, String format) {
/*
select DATE_FORMAT(now(), '%Y-%m-%d')
*/
		if(format.equals("date")) {
			return "DATE_FORMAT(" + columnName + ", '%Y-%m-%d') as " + columnName;
		} else if(format.equals("datetime")) {
			return "DATE_FORMAT(" + columnName + ", '%Y-%m-%d %T') as " + columnName;
		} else {
			return columnName;
		}
	}
	protected String getNextval(Connection con, String tableName, String columnName, String schemaName, String defaultSchema) {
/*
SELECT table_name FROM INFORMATION_SCHEMA.TABLES where TABLE_TYPE = 'SEQUENCE'
*/
		String catalogName = null;
		try {
			catalogName = con.getCatalog();
		} catch(SQLException e) {
			
		}
		String prefix = "";
		if(defaultSchema != null && schemaName != null && !schemaName.equals(defaultSchema)) {
			prefix = schemaName + ".";
		}
		String sequence = getSequence(con, catalogName, tableName + "$" + columnName);
		if(sequence == null) {
			return "NEXT VALUE FOR " + prefix + tableName + "$" + columnName + "";
		} else {
			return "NEXT VALUE FOR " + prefix + sequence;
		}
	}
	private String getSequence(Connection con, String catalogName, String sequenceName) {
		String sequence = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES where TABLE_TYPE = 'SEQUENCE' and lower(table_name) = lower(?) and TABLE_SCHEMA = ?";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, sequenceName);
			pstmt.setString(2, catalogName);
			

			rs = pstmt.executeQuery();
			if(rs.next()) {
				sequence = rs.getString(1);
			}
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			if(logger.isLoggable(Level.INFO)) { logger.info(LOG.toString(e)); }
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
		return sequence;
	}

	private boolean existCommentTable(Connection con, boolean table) throws SQLException {
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
	private void createCommentTable(Connection con, boolean table) throws SQLException {
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
			sql += "SCHEMA_NAME varchar(50),\n";
			sql += "TABLE_NAME varchar(50),\n";
			sql += "COMMENTS varchar(50),\n";
			sql += "PRIMARY KEY (SCHEMA_NAME, TABLE_NAME)\n";
			sql += ") CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'";
		} else {
			sql = "create table GRAHA_COL_COMMENTS (\n";
			sql += "SCHEMA_NAME varchar(50),\n";
			sql += "TABLE_NAME varchar(50),\n";
			sql += "COLUMN_NAME varchar(50),\n";
			sql += "COMMENTS varchar(50),\n";
			sql += "PRIMARY KEY (SCHEMA_NAME, TABLE_NAME, COLUMN_NAME)\n";
			sql += ") CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'";
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
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
	}
	protected Hashtable<String, String> getTableComments(Connection con, String schemaName, String tableName) {
		return getComments(con, schemaName, tableName, true);
	}
	protected Hashtable<String, String> getColumnComments(Connection con, String schemaName, String tableName) {
		return getComments(con, schemaName, tableName, false);
	}
	private Hashtable<String, String> getComments(Connection con, String schemaName, String tableName, boolean table) {
		Hashtable<String, String> comment = new Hashtable<String, String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		try {
			if(table) {
				sql = "select SCHEMA_NAME, TABLE_NAME, COMMENTS from GRAHA_TAB_COMMENTS where comments is not null and comments != ''";
			} else {
				sql = "select SCHEMA_NAME, TABLE_NAME, COLUMN_NAME, COMMENTS from GRAHA_COL_COMMENTS where comments is not null and comments != ''";
			}
			if(tableName != null) {
				sql += " and table_name = ?";
			}
			pstmt = con.prepareStatement(sql);
			if(tableName != null) {
				pstmt.setString(1, tableName);
			} 
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(table) {
					comment.put(rs.getString(1) + "." + rs.getString(2), rs.getString(3));
				} else {
					comment.put(rs.getString(1) + "." + rs.getString(2) + "." + rs.getString(3), rs.getString(4));
				}
			}
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			if(logger.isLoggable(Level.INFO)) { logger.info(LOG.toString(e)); }
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
		return comment;
	}
	protected void updateTableRemarks(Connection con, String schemaName, String tableName, String remarks) throws SQLException {
		updateRemarks(con, schemaName, tableName, null, remarks);
	}
	protected void updateColumnRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
		updateRemarks(con, schemaName, tableName, columnName, remarks);
	}
	private void updateRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
		if(columnName == null) {
			createCommentTable(con, true);
		} else {
			createCommentTable(con, false);
		}
		PreparedStatement pstmt = null;
		String sql = null;
		if(columnName == null) {
			sql = "update GRAHA_TAB_COMMENTS set COMMENTS = ? where  SCHEMA_NAME = ? and TABLE_NAME = ?";
		} else {
			sql = "update GRAHA_COL_COMMENTS set COMMENTS = ? where  SCHEMA_NAME = ? and TABLE_NAME = ? and COLUMN_NAME = ?";
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, remarks);
			pstmt.setString(2, schemaName);
			pstmt.setString(3, tableName);
			if(columnName != null) {
				pstmt.setString(4, columnName);
			}
			pstmt.executeUpdate();
			int result = pstmt.getUpdateCount();
			pstmt.close();
			pstmt = null;
			if(result == 0) {
				if(columnName == null) {
					sql = "insert into GRAHA_TAB_COMMENTS (COMMENTS, SCHEMA_NAME, TABLE_NAME) values (?, ?, ?)";
				} else {
					sql = "insert into GRAHA_COL_COMMENTS (COMMENTS, SCHEMA_NAME, TABLE_NAME, COLUMN_NAME) values (?, ?, ?, ?)";
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, remarks);
				pstmt.setString(2, schemaName);
				pstmt.setString(3, tableName);
				if(columnName != null) {
					pstmt.setString(4, columnName);
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
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
	}

	protected Table getTable(Connection con, String schemaName, String tableName) throws SQLException {
		List<Table> l = getTables(con, schemaName, tableName);
		if(l.isEmpty()) {
			return null;
		} else {
			return l.get(0);
		}
	}
	protected List<Table> getTables(Connection con) throws SQLException {
		return getTables(con, null, null);
	}
	
	protected List<Table> getTables(Connection con, String schemaName, String tableName) throws SQLException {
		return getTables(con, new Table(schemaName, tableName));
	}
	protected List<Table> getTables(Connection con, Table table) throws SQLException {
		List<Table> tables = new ArrayList<Table>();
		if(table != null && table.name != null) {
			tables.add(table);
		}
		return getTables(con, tables);
	}
	protected List<Table> getTables(Connection con, List<Table> tables) throws SQLException {
		return getTables(con, tables, false);
	}
	protected List<Table> getTablesWithColumns(Connection con, List<Table> tables) throws SQLException {
		return getTables(con, tables, true);
	}
	protected List<Table> getTables(Connection con, List<Table> tables, boolean columns) throws SQLException {
		List<Table> tabs = super.getTables(con, tables, columns);
		Table table = null;
		if(tables != null && tables.size() == 1) {
			table = tables.get(0);
		}
		Hashtable<String, String> comment = null;
		if(table == null) {
			comment = getTableComments(con, null, null);
		} else {
			comment = getTableComments(con, table.schema, table.name);
		}
		
		for(Table tab : tabs){
			tab.schema = con.getCatalog();
			if(comment != null && !comment.isEmpty()) {
				if(comment.containsKey(tab.schema + "." + tab.name)) {
					tab.remarks = comment.get(tab.schema + "." + tab.name);
				}
			}
		}
		return tabs;
	}
	protected List<Column> getColumns(Connection con, String schemaName, String tableName) throws SQLException {
		List<Column> cols = super.getColumns(con, schemaName, tableName);
		Hashtable<String, String> comment = getColumnComments(con, schemaName, tableName);
		
		for(Column col : cols){
			if(comment != null || !comment.isEmpty()) {
				if(comment.containsKey(schemaName + "." + tableName + "." + col.name)) {
					col.remarks = comment.get(schemaName + "." + tableName + "." + col.name);
				}
			}
		}
		return cols;
	}
}
