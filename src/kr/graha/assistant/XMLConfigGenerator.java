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

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import kr.graha.helper.LOG;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;

/**
 * Graha(그라하) XML Config 파일을 생성한다.

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.6
 */

public final class XMLConfigGenerator {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private BufferedWriter _bw;
	private CManager _cm;
	private DBUtil _db;
	private String _schemaName;
	private String _tableName;
	private String _tableComments;
	private String _xmlName;
	private String _defaultSchema;
	private String[] _tables;
	private Properties _messges;
	private Connection _con;
	private DatabaseMetaData _m;
	private java.util.List<Column> _cols = null;
	private java.util.List<Table> _tabs = null;
	private int majorVersion;
	private int minorVersion;
	protected XMLConfigGenerator(
		BufferedWriter bw,
		CManager cm,
		String schemaName,
		String tableName,
		String xmlName,
		String[] tables,
		Properties messges,
		Connection con,
		int majorVersion,
		int minorVersion
	) throws SQLException, IOException {
		this._bw = bw;
		this._cm = cm;
		this._db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
		this._schemaName = schemaName;
		this._tableName = tableName;
		this._tableComments = this._db.getTabRemarks(con, schemaName, tableName);
		this._xmlName = xmlName;
		this._defaultSchema = con.getSchema();
		this._tables = tables;
		this._messges = messges;
		this._con = con;
		this._m = con.getMetaData();
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	private String value(String value) {
		if(value == null) {
			return "";
		} else if(
			this.majorVersion < 3
			|| (
				this.majorVersion == 3
				&& this.minorVersion == 0
			)
		) {
			return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		} else {
			return value;
		}
	}
	private String getProperty(Properties messges, String key) {
		String value = messges.getProperty(key);
		if(value != null) {
			try {
				return new String(value.getBytes("iso-8859-1"), "UTF-8");
			} catch (java.io.UnsupportedEncodingException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			}
		}
		return null;
	}
	protected void execute() throws IOException, SQLException {
		this._bw.write("<querys>\n");
		this._bw.write("	<header extends=\"_base.xml\">\n");
		this._bw.write("		<jndi name=\"" + this._cm.getJndi() + "\" />\n");
		this._bw.write("	</header>\n");
		this.list();
		this.insert();
		this.detail();
		this.delete();
		this._bw.write("</querys>\n");
	}
	private void list() throws IOException, SQLException {
		int index = 0;
		this._bw.write("	<query id=\"list\" funcType=\"list\" label=\"" + this._tableComments + "\">\n");
		this._bw.write("		<header>\n");
		this._bw.write("		</header>\n");
		this._bw.write("		<commands>\n");
		this._bw.write("			<command name=\"" + this._tableName.toLowerCase() + "\">\n");
		this._bw.write("				<sql pageSize=\"15\" pageGroupSize=\"10\">\n");
		this._bw.write("					select\n");
		index = 0;
		
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		for(Column col : this._cols){
			if(this._db.isDef(col.getLowerName())) {
				continue;
			}
			if(index > 0) {
				this._bw.write("						, " + col.name + "\n");
			} else {
				this._bw.write("						" + col.name + "\n");
			}
			index++;
		}
		if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
			this._bw.write("					from " + this._schemaName + "." + this._tableName + "\n");
		} else {
			this._bw.write("					from " + this._tableName + "\n");
		}
		this._bw.write("				</sql>\n");
		this._bw.write("				<sql_cnt>\n");
		if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
			this._bw.write("					select count(*) from " + this._schemaName + "." + this._tableName + "\n");
		} else {
			this._bw.write("					select count(*) from " + this._tableName + "\n");
		}
		if(this._m.getDatabaseProductName().equalsIgnoreCase("PostgreSQL")) {
			this._bw.write("/*\n");
			this._bw.write("					SELECT n_live_tup\n");
			this._bw.write("					FROM pg_stat_all_tables\n");
			if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
				this._bw.write("					WHERE relname = '" + this._tableName + "' and schemaname = '" + this._schemaName + "'\n");
			} else {
				this._bw.write("					WHERE relname = '" + this._tableName + "'\n");
			}
			this._bw.write("*/\n");
			this._bw.write("/*\n");
			this._bw.write("					SELECT reltuples\n");
			this._bw.write("					FROM pg_class\n");
			if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
				this._bw.write("					WHERE relname = '" + this._tableName + "' and relnamespace::regnamespace::text = '" + this._schemaName + "'\n");
			} else {
				this._bw.write("					WHERE relname = '" + this._tableName + "'\n");
			}
			this._bw.write("*/\n");
		}
		this._bw.write("				</sql_cnt>\n");
		this._bw.write("			</command>\n");
		this._bw.write("		</commands>\n");
		this._bw.write("		<layout>\n");
		this._bw.write("			<top>\n");
		this._bw.write("				<left>\n");
		this._bw.write("					<link name=\"insert\" label=\"" + this.getProperty(this._messges, "button.new.label") + "\" path=\"/" + this._xmlName + "/insert\" />\n");
		this._bw.write("				</left>\n");
		this._bw.write("			</top>\n");
		this._bw.write("			<middle>\n");
		this._bw.write("				<tab name=\"" + this._tableName.toLowerCase() + "\">\n");
		
		for(Column col : this._cols) {
			if(col.isPk()) {
				this._bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\">\n");
				this._bw.write("						<link path=\"/" + this._xmlName + "/detail\">\n");
				for(Column pcol : this._cols){
					if(pcol.isPk()) {
						this._bw.write("							<param name=\"" + pcol.getLowerName() + "\" type=\"query\" value=\"" + pcol.getLowerName() + "\" />\n");
					}
				}
				this._bw.write("						</link>\n");
				this._bw.write("					</column>\n");
			} else if(this._db.isDef(col.getLowerName())) {
				continue;
			} else {
				this._bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
			}
		}
		this._bw.write("				</tab>\n");
		this._bw.write("			</middle>\n");
		this._bw.write("			<bottom>\n");
		this._bw.write("			<center>page</center>\n");
		this._bw.write("			</bottom>\n");
		this._bw.write("		</layout>\n");
		this._bw.write("	</query>\n");
	}
	private void insert() throws IOException, SQLException {
		int index = 0;
		this._bw.write("	<query id=\"insert\" funcType=\"insert\" label=\"" + this._tableComments + "\">\n");
		this._bw.write("		<header>\n");
		this._bw.write("		</header>\n");
		this._bw.write("		<tables>\n");
		if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
			this._bw.write("			<table tableName=\"" + this._schemaName + "." + this._tableName + "\" name=\"" + this._tableName.toLowerCase() + "\" label=\"" + this._tableComments + "\">\n");
		} else {
			this._bw.write("			<table tableName=\"" + this._tableName + "\" name=\"" + this._tableName.toLowerCase() + "\" label=\"" + this._tableComments + "\">\n");
		}
		if(this._cols == null) {
			this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		}
		for(Column col : this._cols) {
			String dataType = this._db.getGrahaDataType(col.dataType);
			if(col.isPk()) {
				if(this._db.supportSequence()) {
					this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + this._db.getNextval(this._con, this._tableName, col.name, this._schemaName, this._defaultSchema) + "\" />\n");
				} else {
					this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
				}
			} else if(this._db.isDef(col.getLowerName()) && this._db.isDefOnly(col.getLowerName())) {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + this._db.getDefOnly(col.getLowerName()) + "\" value=\"" + this._db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
			} else if(this._db.isDef(col.getLowerName())) {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + this._db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
			} else {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
			}
		}
		this._bw.write("			</table>\n");
		if(this._tables != null && this._tables.length > 1) {
			for (String tab2 : this._tables) {
				String tab1 = value(tab2);
				String schema = tab1.substring(0, tab1.indexOf("."));
				String table = tab1.substring(tab1.indexOf(".") + 1);
				if(table.equals(this._tableName)) {
					continue;
				}
				String comments = this._db.getTabRemarks(this._con, schema, table);
				if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
					this._bw.write("			<table tableName=\"" + this._schemaName + "." + this._tableName + "\" name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"  multi=\"true\" append=\"3\">\n");
				} else {
					this._bw.write("			<table tableName=\"" + table + "\" name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"  multi=\"true\" append=\"3\">\n");
				}
				
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					String dataType = this._db.getGrahaDataType(col.dataType);
					if(col.isPk()) {
						if(this._db.supportSequence()) {
							this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + this._db.getNextval(this._con, table, col.name, this._schemaName, this._defaultSchema) + "\" />\n");
						} else {
							this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
						}
					} else if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  foreign=\"true\" />\n");
					} else if(this._db.isDef(col.getLowerName()) && this._db.isDefOnly(col.getLowerName())) {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + this._db.getDefOnly(col.getLowerName()) + "\" value=\"" + this._db.getDef(col.getLowerName(), "param." + table) + "\" datatype=\"" + dataType + "\" />\n");
					} else if(this._db.isDef(col.getLowerName())) {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + this._db.getDef(col.getLowerName(), "param." + table) + "\" datatype=\"" + dataType + "\" />\n");
					} else {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
					}
				}
				this._bw.write("			</table>\n");
			}
		}
		this._bw.write("		</tables>\n");
		this._bw.write("		<layout msg=\"" + this.getProperty(this._messges, "message.save.confirm.msg") + "\">\n");
		this._bw.write("			<top>\n");
		this._bw.write("				<left />\n");
		this._bw.write("				<center />\n");
		this._bw.write("				<right>\n");
		this._bw.write("					<link name=\"list\" label=\"" + this.getProperty(this._messges, "button.list.label") + "\" path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("					<link name=\"save\" label=\"" + this.getProperty(this._messges, "button.save.label") + "\" path=\"/" + this._xmlName + "/insert\" method=\"post\" type=\"submit\" full=\"true\">\n");
		this._bw.write("						<params>\n");
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		for(Column col : this._cols) {
			if(col.isPk()) {
				this._bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		this._bw.write("						</params>\n");
		this._bw.write("					</link>\n");
		this._bw.write("				</right>\n");
		this._bw.write("			</top>\n");
		this._bw.write("			<middle>\n");
		this._bw.write("				<tab name=\"" + this._tableName.toLowerCase() + "\" label=\"" + this._tableComments + "\">\n");
		
		for(Column col : this._cols) {
			if(!col.isPk() && !this._db.isDef(col.getLowerName())) {
				this._bw.write("					<row>\n");
				if(col.dataType == java.sql.Types.BOOLEAN) {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\" />\n");
				} else if(col.typeName != null && col.typeName.equals("text")) {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\" />\n");
				} else {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" />\n");
				}
				this._bw.write("					</row>\n");
			}
		}
		this._bw.write("				</tab>\n");
		
		if(this._tables != null && this._tables.length > 1) {
			for (String tab2 : this._tables) {
				String tab1 = value(tab2);
				String schema = tab1.substring(0, tab1.indexOf("."));
				String table = tab1.substring(tab1.indexOf(".") + 1);
				if(table != null && table.equals(this._tableName)) {
					continue;
				}
				String comments = this._db.getTabRemarks(this._con, schema, table);
				
				this._bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\">\n");
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write("						<column name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"hidden\" />\n");
					}
				}
				this._bw.write("					<row>\n");
				for(Column col : this._cols) {
					if(col.isPk()) {
						continue;
					} else if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						continue;
					} else if(this._db.isDef(col.getLowerName())) {
						continue;
					} else {
						if(col.dataType == java.sql.Types.BOOLEAN) {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\" />\n");
						} else if(col.typeName != null && col.typeName.equals("text")) {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\" />\n");
						} else {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" />\n");
						}
					}
				}
				this._bw.write("					</row>\n");
				this._bw.write("				</tab>\n");
			}
		}
		
		this._bw.write("			</middle>\n");
		this._bw.write("			<bottom>\n");
		this._bw.write("				<right>\n");
		this._bw.write("				</right>\n");
		this._bw.write("			</bottom>\n");
		this._bw.write("		</layout>\n");
		this._bw.write("		<redirect path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("	</query>\n");
	}
	private void detail() throws IOException, SQLException {
		int index = 0;
		this._bw.write("	<query id=\"detail\" funcType=\"detail\" label=\"" + this._tableComments + "(${/document/rows/row/title})\">\n");
		this._bw.write("		<header>\n");
		this._bw.write("		</header>\n");
		this._bw.write("		<commands>\n");
		this._bw.write("			<command name=\"" + this._tableName.toLowerCase() + "\">\n");
		this._bw.write("				<sql>\n");
		this._bw.write("					select\n");
		index = 0;
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		for(Column col : this._cols) {
				if(this._db.isDef(col.getLowerName())) {
					continue;
				}
				if(index > 0) {
					this._bw.write("						, " + col.name + "\n");
				} else {
					this._bw.write("						" + col.name + "\n");
				}
				index++;
		}
		if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
			this._bw.write("					from " + this._schemaName + "." + this._tableName + "\n");
		} else {
			this._bw.write("					from " + this._tableName + "\n");
		}
		index = 0;
		for(Column col : this._cols) {
			if(col.isPk()) {
				if(index > 0) {
					this._bw.write("						and " + col.name + " = ?\n");
				} else {
					this._bw.write("						where " + col.name + " = ?\n");
				}
				index++;
			}
		}
		this._bw.write("				</sql>\n");
		this._bw.write("				<params>\n");
		for(Column col : this._cols) {
			if(col.isPk()) {
				String dataType = this._db.getGrahaDataType(col.dataType);
				this._bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
			}
		}
		this._bw.write("				</params>\n");
		this._bw.write("			</command>\n");
		if(this._tables != null && this._tables.length > 1) {
			for (String tab2 : this._tables) {
				String tab1 = value(tab2);
				String schema = tab1.substring(0, tab1.indexOf("."));
				String table = tab1.substring(tab1.indexOf(".") + 1);
				if(table != null && table.equals(this._tableName)) {
					continue;
				}
				this._bw.write("			<command name=\"" + table.toLowerCase() + "\"  multi=\"true\">\n");
				this._bw.write("				<sql>\n");
				this._bw.write("					select\n");
				index = 0;
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
						if(this._db.isDef(col.getLowerName())) {
							continue;
						}
						if(index > 0) {
							this._bw.write("						, " + col.name + "\n");
						} else {
							this._bw.write("						" + col.name + "\n");
						}
						index++;
				}
				if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
					this._bw.write("					from " + this._schemaName + "." + this._tableName + "\n");
				} else {
					this._bw.write("					from " + table + "\n");
				}
				index = 0;
				for(Column col : this._cols) {
					if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						if(index > 0) {
							this._bw.write("						and " + col.name + " = ?\n");
						} else {
							this._bw.write("						where " + col.name + " = ?\n");
						}
						index++;
					}
				}
				this._bw.write("				</sql>\n");
				this._bw.write("				<params>\n");
				for(Column col : this._cols) {
					if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						String dataType = this._db.getGrahaDataType(col.dataType);
						this._bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
					}
				}
				this._bw.write("				</params>\n");
				this._bw.write("			</command>\n");
			}
		}
		this._bw.write("		</commands>\n");
		this._bw.write("		<layout>\n");
		this._bw.write("			<top>\n");
		this._bw.write("				<left />\n");
		this._bw.write("				<center />\n");
		this._bw.write("				<right>\n");
		this._bw.write("					<link name=\"list\" label=\"" + this.getProperty(this._messges, "button.list.label") + "\" path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("					<link name=\"update\" label=\"" + this.getProperty(this._messges, "button.update.label") + "\" path=\"/" + this._xmlName + "/insert\">\n");
		this._bw.write("						<params>\n");
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		for(Column col : this._cols) {
			if(col.isPk()) {
				this._bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		
		this._bw.write("						</params>\n");
		this._bw.write("					</link>\n");
		this._bw.write("				</right>\n");
		this._bw.write("			</top>\n");
		this._bw.write("			<middle>\n");
		this._bw.write("				<tab name=\"" + this._tableName.toLowerCase() + "\" label=\"" + this._tableComments + "\">\n");
		for(Column col : this._cols) {
			if(col.isPk()) {
				continue;
			} else if(this._db.isDef(col.getLowerName())) {
					continue;
			} else {
				this._bw.write("					<row>\n");
				this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
				this._bw.write("					</row>\n");
			}
		}
		
		this._bw.write("				</tab>\n");
		if(this._tables != null && this._tables.length > 1) {
			for (String tab2 : this._tables) {
				String tab1 = value(tab2);
				String schema = tab1.substring(0, tab1.indexOf("."));
				String table = tab1.substring(tab1.indexOf(".") + 1);
				if(table != null && table.equals(this._tableName)) {
					continue;
				}
				String comments = this._db.getTabRemarks(this._con, schema, table);
				this._bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\">\n");
				this._bw.write("					<row>\n");
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					if(col.isPk()) {
						continue;
					} else if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						continue;
					} else if(this._db.isDef(col.getLowerName())) {
						continue;
					} else {
						this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
					}
					
				}
				this._bw.write("					</row>\n");
				this._bw.write("				</tab>\n");
			}
		}
		this._bw.write("			</middle>\n");
		this._bw.write("			<bottom>\n");
		this._bw.write("				<left>\n");
		this._bw.write("					<link label=\"" + this.getProperty(this._messges, "button.del.label") + "\" path=\"/" + this._xmlName + "/delete\" method=\"post\" type=\"submit\" msg=\"" + this.getProperty(this._messges, "message.del.confirm.msg") + "\">\n");
		this._bw.write("						<params>\n");
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		for(Column col : this._cols) {
			if(col.isPk()) {
				this._bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		this._bw.write("						</params>\n");
		this._bw.write("					</link>\n");
		this._bw.write("				</left>\n");
		this._bw.write("			</bottom>\n");
		this._bw.write("			\n");
		this._bw.write("		</layout>\n");
		this._bw.write("	</query>\n");
	}
	private void delete() throws IOException, SQLException {
		this._bw.write("	<query id=\"delete\" funcType=\"delete\" label=\"" + this._tableComments + "\">\n");
		this._bw.write("		<tables>\n");
		if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
			this._bw.write("			<table tableName=\"" + this._schemaName + "." + this._tableName + "\" name=\"" + this._tableName.toLowerCase() + "\">\n");
		} else {
			this._bw.write("			<table tableName=\"" + this._tableName + "\" name=\"" + this._tableName.toLowerCase() + "\">\n");
		}
		if(this._cols == null) {
			this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		}
		for(Column col : this._cols) {
			String dataType = this._db.getGrahaDataType(col.dataType);
			if(col.isPk()) {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" primary=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
			}
		}
		this._bw.write("			</table>\n");
		if(this._tables != null && this._tables.length > 1) {
			for (String tab2 : this._tables) {
				String tab1 = value(tab2);
				String schema = tab1.substring(0, tab1.indexOf("."));
				String table = tab1.substring(tab1.indexOf(".") + 1);
				if(table != null && table.equals(this._tableName)) {
					continue;
				}
				if(this._defaultSchema != null && this._schemaName != null && !this._schemaName.equals(this._defaultSchema)) {
					this._bw.write("			<table tableName=\"" + this._schemaName + "." + this._tableName + "\" name=\"" + table.toLowerCase() + "\">\n");
				} else {
					this._bw.write("			<table tableName=\"" + table + "\" name=\"" + table.toLowerCase() + "\">\n");
				}
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					String dataType = this._db.getGrahaDataType(col.dataType);
					if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" foreign=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
					}
				}
				this._bw.write("			</table>\n");
			}
		}
		this._bw.write("		</tables>\n");
		this._bw.write("		<redirect path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("	</query>\n");
	}
}

