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
 * Graha(그라하) 오라클을 위한 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class DBUtilOracleImpl extends DBUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Hashtable<String, Integer> map = null;
	protected DBUtilOracleImpl() throws IOException {
		LOG.setLogLevel(logger);
	}
	protected void loadProp(Connection con, String def, String mapping) throws IOException, SQLException {
		super.loadProp(con, def, mapping);
		this.loadMap(con);
	}
	protected String getToday() {
		return "sysdate";
	}
	/*
	select to_char(sysdate, 'yyyy-mm-dd') as now from dual
	
	select to_char(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') as now,
sysdate,
systimestamp,
CURRENT_TIMESTAMP,
CURRENT_DATE
from dual

select dbtimezone from dual
	*/
	protected String getNextval(Connection con, String tableName, String columnName, String schemaName, String defaultSchema) {
		String prefix = "";
		if(defaultSchema != null && schemaName != null && !schemaName.equals(defaultSchema)) {
			prefix = schemaName + ".";
		}
		String sequence = getSequence(con, tableName + "$" + columnName);
		if(sequence == null) {
			return "&quot;" + prefix + tableName + "$" + columnName + "&quot;.nextval";
		} else {
			return "&quot;" + prefix + sequence + "&quot;.nextval";
		}
	}
	protected String getSequence(Connection con, String sequenceName) {
		String sql = "SELECT SEQUENCE_NAME FROM user_sequences where lower(SEQUENCE_NAME) = lower(?)";
		return super.getSequence(con, sql, sequenceName);
	}
	protected void updateTableRemarks(Connection con, String schemaName, String tableName, String remarks) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = null;
		try {
			sql = "comment on table " + schemaName + "." + tableName + " is '" + remarks + "'";
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
/*
select * from user_tab_comments a  where exists (
select * from user_tables b where a.table_name = b.table_name
) or exists(
select * from user_views c where a.table_name = c.view_name
)
*/
		Table table = null;
		if(tables != null && tables.size() == 1) {
			table = tables.get(0);
		}
		List<Table> l = new ArrayList<Table>();
		DatabaseMetaData m = con.getMetaData();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		if(table == null || table.name == null) {
			sql = "select TABLE_NAME, TABLE_TYPE, comments as REMARKS from user_tab_comments a where exists (select * from user_tables b where a.table_name = b.table_name) or exists (select * from user_views c where a.table_name = c.view_name)";
		} else {
			sql = "select TABLE_NAME, TABLE_TYPE, comments as REMARKS from user_tab_comments a where a.table_name = ? and (exists (select * from user_tables b where a.table_name = b.table_name) or exists (select * from user_views c where a.table_name = c.view_name))";
		}
		try {
			pstmt = con.prepareStatement(sql);
			if(table == null || table.name == null) {
			} else {
				pstmt.setString(1, table.name.toUpperCase());
			} 
			rs = pstmt.executeQuery();
			while(rs.next()) {
				if(tables != null && tables.size() > 1) {
					boolean exists = false;
					for(Table tab : tables) {
						if(tab.compareWithSchemaAndTableName(m.getUserName(), rs.getString(1))) {
							exists = true;
						}
					}
					if(!exists) {
						continue;
					}
				}
				Table t = new Table(m.getUserName(), rs.getString(1));
				t.type = rs.getString(2);
				t.remarks = rs.getString(3);
				if(columns) {
					t.cols = this.getColumns(con, t.schema, t.name);
				}
				l.add(t);
			}
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			throw e;
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
		return l;
	}
	private int getDataType(String typeName) {
		if(this.map.containsKey(typeName)) {
			return this.map.get(typeName).intValue();
		} else {
			if(typeName.indexOf("(") > 0 && this.map.containsKey(typeName.substring(0, typeName.indexOf("(")))) {
				return this.map.get(typeName.substring(0, typeName.indexOf("("))).intValue();
			}
		}
		return 0;
	}
	protected List<Column> getColumns(Connection con, String schemaName, String tableName) throws SQLException {
/*
select 
	a.table_name, 
	a.column_name,
	a.nullable,
	b.comments, 
	(
		select position from user_cons_columns, user_constraints
		where user_constraints.CONSTRAINT_TYPE = 'P'
			and user_constraints.CONSTRAINT_NAME = user_cons_columns.CONSTRAINT_NAME
			and user_constraints.TABLE_NAME = a.table_name
			and user_cons_columns.TABLE_NAME = a.table_name
			and user_cons_columns.column_name  = a.column_name
	) as pk
from user_tab_columns a, 
	user_col_comments b 
where a.table_name = b.table_name 
	and a.column_name = b.column_name
*/
		List<Column> l = new ArrayList<Column>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DatabaseMetaData m = con.getMetaData();
			pstmt = con.prepareStatement("select a.column_name, a.data_type, a.nullable, b.comments from user_tab_columns a, user_col_comments b where a.table_name = b.table_name and a.column_name = b.column_name and a.table_name = ? order by a.column_id");
			pstmt.setString(1, tableName);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Column c = new Column();
				c.name = rs.getString(1);
				c.typeName = rs.getString(2);

				c.dataType = getDataType(rs.getString(2));
				c.isPk = Boolean.toString(containsKey(con, schemaName, tableName, rs.getString(1)));
				c.isNullable = rs.getString(3);
				c.isAutoincrement = "";
				c.remarks = rs.getString(4);
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
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
		return l;
	}
	private void loadMap(Connection con) throws SQLException {
		if(this.map == null) {
			this.map = new Hashtable<String, Integer>();
			ResultSet rs = null;
			try {
				DatabaseMetaData m = con.getMetaData();
				rs = m.getTypeInfo();
				while(rs.next()) {
					this.map.put(rs.getString(1), Integer.valueOf(rs.getInt(2)));
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
						if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
					}
				}
			}
		}
	}
}