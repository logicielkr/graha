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
import kr.graha.helper.LOG;
import java.util.Hashtable;
import java.io.IOException;
import java.util.Properties;
import kr.graha.helper.DB;
import java.util.HashSet;
import java.util.Set;

/**
 * Graha(그라하) 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DBUtil {
	private String[] TYPES = {"TABLE", "VIEW"};
	private Hashtable<String, Hashtable> pk = null;
	private Properties prop = null;
	private Hashtable<Integer, String> sqlTypes = null;
	private Properties def = null;
	protected DBUtil() throws IOException {
	}
	protected static DBUtil getDBUtil(Connection con, String def, String mapping) throws SQLException, IOException {
		DatabaseMetaData m = con.getMetaData();
		DBUtil db = null;
		if(
			m.getDatabaseProductName().equalsIgnoreCase("Oracle") || m.getDatabaseProductName().equalsIgnoreCase("Tibero")
		) {
			db = new DBUtilOracleImpl();
		} else if(m.getDatabaseProductName().equalsIgnoreCase("Apache Derby")) {
			db = new DBUtilDerbyImpl();
		} else if(m.getDatabaseProductName().equalsIgnoreCase("H2")) {
			db = new DBUtilH2Impl();
		} else if(m.getDatabaseProductName().equalsIgnoreCase("HSQL Database Engine")) {
			db = new DBUtilHSQLImpl();
		} else if(m.getDatabaseProductName().equalsIgnoreCase("SQLite")) {
			db = new DBUtilSqliteImpl();
		} else if(m.getDatabaseProductName().equalsIgnoreCase("MariaDB")) {
			db = new DBUtilMariaDBImpl();
		} else {
			LOG.config(m.getDatabaseProductName());
			db = new DBUtil();
		}
		db.loadProp(con, def, mapping);
		return db;
	}
	protected boolean supportSequence() {
		return true;
	}
	private String prefixSchema(Connection con, String schemaName) throws SQLException {
		DatabaseMetaData m = con.getMetaData();
		return "";
	}
	protected String getNextval(Connection con, Table table, String columnName, String defaultSchema) {
		return getNextval(con, table.name, columnName, table.schema, defaultSchema);
	}
	protected String getNextval(Connection con, String tableName, String columnName, String schemaName, String defaultSchema) {
		String prefix = "";
		if(defaultSchema != null && schemaName != null && !schemaName.equals(defaultSchema)) {
			prefix = schemaName + ".";
		}
		return "nextval('" + prefix + tableName + "$" + columnName + "')";
	}
	protected String getSequence(Connection con, String sequenceName) {
		String sql = "select SEQUENCE_NAME from INFORMATION_SCHEMA.SEQUENCES where lower(SEQUENCE_NAME) = lower(?)";
		return getSequence(con, sql, sequenceName);
	}
	protected String getSequence(Connection con, String sql, String sequenceName) {
		return getSequence(con, sql, null, sequenceName);
	}
	protected String getSequence(Connection con, String sql, String schemaName, String sequenceName) {
		String sequence = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = con.prepareStatement(sql);
			if(schemaName == null) {
				pstmt.setString(1, sequenceName);
			} else {
				pstmt.setString(1, schemaName);
				pstmt.setString(2, sequenceName);
			}

			rs = pstmt.executeQuery();
			if(rs.next()) {
				sequence = rs.getString(1);
			}
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			LOG.severe(e);
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
		return sequence;
	}
	protected Set getCommentByColumnNameFromGrahaColComments(Connection con, String columnName, boolean oracle) {
		Set set = new HashSet();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		if(oracle) {
			sql = "select distinct comments from user_col_comments where column_name = ?";
		} else {
			sql = "select distinct COMMENTS from GRAHA_COL_COMMENTS where COLUMN_NAME = ?";
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, columnName);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(rs.getString(1) != null && !rs.getString(1).trim().equals("")) {
					if(!set.contains(rs.getString(1))) {
						set.add(rs.getString(1));
					}
				}
			}
			DB.close(rs);
			DB.close(pstmt);
		} catch (SQLException e) {
			LOG.severe(e);
		} finally {
			DB.close(rs);
			DB.close(pstmt);
		}
		return set;
	}
	protected Set getCommentByColumnName(Connection con, String columnName) {
		Set set = new HashSet();
		ResultSet rs = null;
		try {
			DatabaseMetaData m = con.getMetaData();
			rs = m.getColumns(con.getCatalog(), "%", "%", columnName);
			while(rs.next()) {
				if(rs.getString("REMARKS") != null && !rs.getString("REMARKS").trim().equals("")) {
					if(rs.getString("TABLE_SCHEM") != null && rs.getString("TABLE_SCHEM").equalsIgnoreCase("INFORMATION_SCHEMA")) {
						continue;
					}
					if(!set.contains(rs.getString("REMARKS"))) {
						set.add(rs.getString("REMARKS"));
					}
				}
			}
			DB.close(rs);
		} catch (SQLException e) {
			LOG.severe(e);
		} finally {
			DB.close(rs);
		}
		return set;
	}
	protected boolean supportBultinDateFormatFunction() {
		return true;
	}
	protected String dateFormat(String columnName, String format) {
		if(format.equals("date")) {
			return "to_char(" + columnName + ", 'yyyy-mm-dd') as " + columnName;
		} else if(format.equals("datetime")) {
			return "to_char(" + columnName + ", 'YYYY-MM-DD HH24:MI:SS') as " + columnName;
		} else {
			return columnName;
		}
	}
	protected void loadProp(Connection con, String def, String mapping) throws IOException, SQLException {
		if(this.prop == null) {
			this.prop = new Properties();
			if(mapping == null) {
				this.prop.load(getClass().getResourceAsStream("mapping.properties"));
			} else {
				this.prop.load(getClass().getResourceAsStream(mapping));
			}
		}
		if(this.def == null) {
			this.def = new Properties();
			if(def == null) {
				this.def.load(getClass().getResourceAsStream("def.properties"));
			} else {
				this.def.load(getClass().getResourceAsStream(def));
			}
		}
		if(this.sqlTypes == null) {
			this.sqlTypes = new Hashtable<Integer, String>();
			for (java.lang.reflect.Field field : java.sql.Types.class.getFields()) {
				try {
					this.sqlTypes.put(Integer.valueOf(field.getInt(null)), field.getName());
				} catch (IllegalAccessException e) {
					LOG.severe(e);
				}
			}
		}
	}
	protected String getToday() {
		return "now()";
	}
	protected String getOwnerColumnByDef() {
		java.util.Enumeration e = this.def.propertyNames();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = this.def.getProperty(key);
			if(
				key.endsWith(".value") &&
				((value.equals("prop.logined_user") || value.equals("header.remote_user") || value.equals("session.member_id")))
			) {
				String only = this.def.getProperty(key.substring(0, key.length() - 6) + ".only");
				if(only != null && only.equals("insert")) {
					return key.substring(0, key.length() - 6);
				}
			}
		}
		return null;
	}
	protected String getDef(String columnName, String prefix) {
		if(this.def.containsKey(columnName + ".value")) {
			if(this.def.getProperty(columnName + ".value").equals("sql.today")) {
				return "sql." + getToday();
			} else {
				return this.def.getProperty(columnName + ".value");
			}
		} else {
			return prefix + columnName;
		}
	}
	protected boolean isDef(String columnName) {
		if(this.def.containsKey(columnName + ".value")) {
			return true;
		} else {
			return false;
		}
	}
	protected String getDefOnly(String columnName) {
		if(this.def.containsKey(columnName + ".only")) {
			return this.def.getProperty(columnName + ".only");
		} else {
			return "";
		}
	}
	protected boolean isDefOnly(String columnName) {
		if(this.def.containsKey(columnName + ".only")) {
			return true;
		} else {
			return false;
		}
	}
	protected String getGrahaDataType(int dataType) {
		if(this.sqlTypes.containsKey(Integer.valueOf(dataType))) {
			if(this.prop.containsKey(this.sqlTypes.get(Integer.valueOf(dataType)))) {
				return this.prop.getProperty(this.sqlTypes.get(Integer.valueOf(dataType)));
			} else {
				LOG.warning(this.sqlTypes.get(Integer.valueOf(dataType)) + " is not defined");
				return this.prop.getProperty("default");
			}
		} else {
			LOG.warning(dataType + " is not defined");
			return this.prop.getProperty("default");
		}
	}
	protected void updateTableRemarks(Connection con, String schemaName, String tableName, String remarks) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			Table t = getTable(con, schemaName, tableName);
			if(schemaName == null) {
				sql = "comment on " + t.type + " " + tableName + " is '" + remarks + "'";
			} else {
				sql = "comment on " + t.type + " " + schemaName + "." + tableName + " is '" + remarks + "'";
			}
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
	}
	protected void updateColumnRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			if(schemaName == null) {
				sql = "comment on column " + tableName + "." + columnName + " is '" + remarks + "'";
			} else {
				sql = "comment on column " + schemaName + "." + tableName + "." + columnName + " is '" + remarks + "'";
			}
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
	}
	protected String getTabRemarks(Connection con, String schemaName, String tableName) throws SQLException {
		Table t = getTable(con, schemaName, tableName);
		if(t == null) {
			return tableName;
		} else {
			return t.getRemarksOrName();
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
		Table table = null;
		if(tables != null && tables.size() == 1) {
			table = tables.get(0);
		}
		List<Table> l = new ArrayList<Table>();
		DatabaseMetaData m = con.getMetaData();
		ResultSet rs = null;
		try {
			if(table == null || table.name == null) {
				rs = m.getTables(con.getCatalog(), null, "%", TYPES);
			} else {
				rs = m.getTables(con.getCatalog(), table.schema, table.name, TYPES);
			}
			while(rs.next()) {
				if(tables != null && tables.size() > 1) {
					boolean exists = false;
					for(Table tab : tables) {
						if(tab.compareWithSchemaAndTableName(rs.getString("TABLE_SCHEM"), rs.getString("TABLE_NAME"))) {
							exists = true;
						}
					}
					if(!exists) {
						continue;
					}
				}
				Table t = new Table(rs.getString("TABLE_SCHEM"), rs.getString("TABLE_NAME"));
				t.type = rs.getString("TABLE_TYPE");
				t.remarks = rs.getString("REMARKS");
				if(columns) {
					t.cols = this.getColumns(con, t.schema, t.name);
				}
				l.add(t);
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
		return l;
	}
	private Hashtable<String, Integer> getPKColumns(Connection con, String schemaName, String tableName) throws SQLException {
		Hashtable<String, Integer> pk = new Hashtable<String, Integer>();
		DatabaseMetaData m = con.getMetaData();
		ResultSet rs = null;
		try {
			rs = m.getPrimaryKeys(con.getCatalog(), schemaName, tableName);
			while(rs.next()) {
				pk.put(rs.getString("COLUMN_NAME"), rs.getInt("KEY_SEQ"));
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
		return pk;
	}
	protected boolean containsKey(Connection con, Table table, String columnName) throws SQLException {
		return containsKey(con, table.schema, table.name, columnName);
	}
	protected boolean containsKey(Connection con, String schemaName, String tableName, String columnName) throws SQLException {
		if(this.pk == null) {
			this.pk = new Hashtable<String, Hashtable>();
		}
		if(!this.pk.containsKey(schemaName + "." + tableName)) {
			this.pk.put(schemaName + "." + tableName, getPKColumns(con, schemaName, tableName));
		}
		return this.pk.get(schemaName + "." + tableName).containsKey(columnName);
	}
	protected List<Column> getColumns(Connection con, String schemaName, String tableName) throws SQLException {
		List<Column> l = new ArrayList<Column>();
		ResultSet rs = null;
		try {
			DatabaseMetaData m = con.getMetaData();
			rs = m.getColumns(con.getCatalog(), schemaName, tableName, "%");
			while(rs.next()) {
				Column c = new Column();
				c.name = rs.getString("COLUMN_NAME");
/*
for postgresql
*/
				if(rs.getInt("DATA_TYPE") == java.sql.Types.BIT && rs.getString("TYPE_NAME").equals("bool")) {
					c.dataType = java.sql.Types.BOOLEAN;
/*
for sqlite
*/
				} else if(rs.getInt("DATA_TYPE") == java.sql.Types.VARCHAR && rs.getString("TYPE_NAME").equals("DATETIME")) {
					c.dataType = java.sql.Types.TIMESTAMP;
				} else if(rs.getInt("DATA_TYPE") == java.sql.Types.VARCHAR && rs.getString("TYPE_NAME").equals("DATE")) {
					c.dataType = java.sql.Types.DATE;
				} else if(rs.getInt("DATA_TYPE") == java.sql.Types.INTEGER && rs.getString("TYPE_NAME").equals("BIGINT")) {
					c.dataType = java.sql.Types.BIGINT;
				} else if(rs.getInt("DATA_TYPE") == java.sql.Types.FLOAT && rs.getString("TYPE_NAME").equals("DOUBLE")) {
					c.dataType = java.sql.Types.DOUBLE;
				} else if(rs.getInt("DATA_TYPE") == java.sql.Types.INTEGER && rs.getString("TYPE_NAME").equals("BOOLEAN")) {
					c.dataType = java.sql.Types.BOOLEAN;
				} else {
					c.dataType = rs.getInt("DATA_TYPE");
				}
				
				c.typeName = rs.getString("TYPE_NAME");
				c.isPk = Boolean.toString(containsKey(con, schemaName, tableName, rs.getString("COLUMN_NAME")));
				c.isNullable = rs.getString("IS_NULLABLE");
				c.isAutoincrement = rs.getString("IS_AUTOINCREMENT");
				c.remarks = rs.getString("REMARKS");
				l.add(c);
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					LOG.severe(e);
				}
			}
		}
		return l;
	}
}

