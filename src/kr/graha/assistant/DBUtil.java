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
import java.io.IOException;
import java.util.Properties;

/**
 * Graha(그라하) 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DBUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public String[] TYPES = {"TABLE", "VIEW"};
	public Hashtable<String, Hashtable> pk = null;
	private Properties prop = null;
	public Hashtable<Integer, String> sqlTypes = null;
	public Properties def = null;
	public DBUtil() throws IOException {
		LogHelper.setLogLevel(logger);
	}
	public static DBUtil getDBUtil(Connection con, String def, String mapping) throws SQLException, IOException {
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
			if(Logger.getLogger(DBUtil.class.getName()).isLoggable(Level.CONFIG)) {
				Logger.getLogger(DBUtil.class.getName()).config(m.getDatabaseProductName());
			}
			db = new DBUtil();
		}
		db.loadProp(con, def, mapping);
		return db;
	}
	public boolean supportSequence() {
		return true;
	}
	public String prefixSchema(Connection con, String schemaName) throws SQLException {
		DatabaseMetaData m = con.getMetaData();
		return "";
	}
	public String getNextval(Connection con, String tableName, String columnName) {
/*
		select sequencename from pg_sequences
*/
		return "nextval('" + tableName + "$" + columnName + "')";
	}
	public void loadProp(Connection con, String def, String mapping) throws IOException, SQLException {
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
					if(logger.isLoggable(Level.WARNING)) {
						logger.warning(LogHelper.toString(e));
					}
				}
			}
		}
	}
	public String getToday() {
		return "now()";
	}
	public String getDef(String columnName, String prefix) {
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
	public boolean isDef(String columnName) {
		if(this.def.containsKey(columnName + ".value")) {
			return true;
		} else {
			return false;
		}
	}
	public String getDefOnly(String columnName) {
		if(this.def.containsKey(columnName + ".only")) {
			return this.def.getProperty(columnName + ".only");
		} else {
			return "";
		}
	}
	public boolean isDefOnly(String columnName) {
		if(this.def.containsKey(columnName + ".only")) {
			return true;
		} else {
			return false;
		}
	}
	public String getGrahaDataType(int dataType) {
		if(this.sqlTypes.containsKey(Integer.valueOf(dataType))) {
			if(this.prop.containsKey(this.sqlTypes.get(Integer.valueOf(dataType)))) {
				return this.prop.getProperty(this.sqlTypes.get(Integer.valueOf(dataType)));
			} else {
				logger.severe(this.sqlTypes.get(Integer.valueOf(dataType)) + " is not defined");
				return this.prop.getProperty("default");
			}
		} else {
			logger.severe(dataType + " is not defined");
			return this.prop.getProperty("default");
		}
	}
	public void updateTableRemarks(Connection con, String schemaName, String tableName, String remarks) throws SQLException {
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
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
	}
	public void updateColumnRemarks(Connection con, String schemaName, String tableName, String columnName, String remarks) throws SQLException {
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
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
	}
	public String getTabRemarks(Connection con, String schemaName, String tableName) throws SQLException {
		Table t = getTable(con, schemaName, tableName);
		if(t == null) {
			return tableName;
		} else {
			return t.getRemarksOrName();
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
	public List<Table> getTables(Connection con) throws SQLException {
		return getTables(con, null, null);
	}
	public List<Table> getTables(Connection con, String schemaName, String tableName) throws SQLException {
		List<Table> l = new ArrayList<Table>();
		DatabaseMetaData m = con.getMetaData();
		ResultSet rs = null;
		try {
			if(tableName == null) {
				rs = m.getTables(con.getCatalog(), null, "%", TYPES);
			} else {
				rs = m.getTables(con.getCatalog(), schemaName, tableName, TYPES);
			}
			while(rs.next()) {
				Table t = new Table();
				t.schema = rs.getString("TABLE_SCHEM");
				t.name = rs.getString("TABLE_NAME");
				t.type = rs.getString("TABLE_TYPE");
				t.remarks = rs.getString("REMARKS");
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
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		return l;
	}
	public Hashtable<String, Integer> getPKColumns(Connection con, String schemaName, String tableName) throws SQLException {
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
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		return pk;
	}
	public boolean containsKey(Connection con, String schemaName, String tableName, String columnName) throws SQLException {
		if(this.pk == null) {
			this.pk = new Hashtable<String, Hashtable>();
		}
		if(!this.pk.containsKey(schemaName + "." + tableName)) {
			this.pk.put(schemaName + "." + tableName, getPKColumns(con, schemaName, tableName));
		}
		return this.pk.get(schemaName + "." + tableName).containsKey(columnName);
	}
	public List<Column> getColumns(Connection con, String schemaName, String tableName) throws SQLException {
		List<Column> l = new ArrayList<Column>();
		ResultSet rs = null;
		try {
			DatabaseMetaData m = con.getMetaData();
			rs = m.getColumns(con.getCatalog(), schemaName, tableName, "%");
			while(rs.next()) {
				Column c = new Column();
				c.name = rs.getString("COLUMN_NAME");
				if(rs.getInt("DATA_TYPE") == java.sql.Types.BIT && rs.getString("TYPE_NAME").equals("bool")) {
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
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		return l;
	}
}
