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
import javax.servlet.http.HttpServletRequest;

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
	HttpServletRequest _request;
	protected XMLConfigGenerator(
		BufferedWriter bw,
		CManager cm,
		String schemaName,
		String tableName,
		String xmlName,
		String[] tables,
		Properties messges,
		Connection con,
		HttpServletRequest request,
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
		this._request = request;
		this._m = con.getMetaData();
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	private String getTName() {
		return getTName(this._schemaName, this._tableName);
	}
	private String getTName(String schemaName, String tableName) {
		if(this._defaultSchema != null && schemaName != null && !schemaName.equals(this._defaultSchema)) {
			return schemaName + "." + tableName;
		} else {
			return tableName;
		}
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
	private String param(String key) {
		return this.value(this._request.getParameter(key));
	}
	private boolean equals(String key, String value) {
		if(
			key == null || 
			value == null || 
			this._request.getParameter(key) == null ||
			key.equals("") ||
			value.equals("")
		) {
			return false;
		}
		if(value.equals(this._request.getParameter(key))) {
			return true;
		}
		return false;
	}
	private boolean authentication() {
		return this.equals("authentication", "true");
	}
	private boolean fileUpload() {
		return this.equals("file_upload", "true");
	}
	private String getAuthenticationColumnName(String schemaName, String tableName) {
		return this.param("auth_column_" + schemaName + "." + tableName);
	}
	private String getAuthenticationColumnName() {
		return this.getAuthenticationColumnName(this._schemaName, this._tableName);
	}
	private boolean view(String type, String columnName) {
		return this.view(type, this._schemaName, this._tableName, columnName);
	}
	private boolean view(String type, String schemaName, String tableName, String columnName) {
		return this.equals(type + "_column_" + schemaName + "." + tableName + "___" + columnName + "", columnName);
		/*
		list_view_column_{$table_name}___{@name}
		list_link_column_{$table_name}___{@name}
		detail_view_column_{$table_name}___{@name}
		insert_view_column_{$table_name}___{@name}
		*/
	}
	private String getFormat(String columnName) {
		return this.getFormat(this._schemaName, this._tableName, columnName);
	}
	private String getFormat(String schemaName, String tableName, String columnName) {
		if(this.equals("format_column_" + schemaName + "." + tableName + "___" + columnName + "", "ts")) {
			return " fmt=\"#,##0\"";
		} else {
			return "";
		}
	}
	private boolean multi() {
		return this.multi(this._schemaName, this._tableName);
	}
	private boolean multi(String schemaName, String tableName) {
		return this.equals("relation_" + schemaName + "." + tableName, "many");
	}
	private String getMultiForInsert() {
		return this.getMultiForInsert(this._schemaName, this._tableName);
	}
	private String getMultiForInsert(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName)) {
			return " multi=\"true\" append=\"5\"";
		} else {
			return "";
		}
	}
	private String getMultiForDetail() {
		return this.getMultiForDetail(this._schemaName, this._tableName);
	}
	private String getMultiForDetail(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName)) {
			return " multi=\"true\"";
		} else {
			return "";
		}
	}
	private String getSingle(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName) && this.equals("header_position_" + schemaName + "." + tableName, "top")) {
			return " single=\"true\"";
		} else {
			return "";
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
		if(this.authentication()) {
			this._bw.write("		<prop name=\"logined_user\" value=\"guest\" />\n");
			this._bw.write("		<prop name=\"logined_user\" value=\"${session.user_id}\" cond=\"${session.user_id} isNotEmpty\" />\n");
			this._bw.write("		<prop name=\"logined_user\" value=\"${header.remote_user}\" cond=\"${header.remote_user} isNotEmpty\" />\n");
		}
		if(this.fileUpload()) {
			this._bw.write("		<prop name=\"file.upload.directory\" value=\"${system.context.root.path}WEB-INF/file/upload\" />\n");
			this._bw.write("		<prop name=\"file.backup.directory\" value=\"${system.context.root.path}WEB-INF/file/backup\" />\n");
		}
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
		for(Column col : this._cols) {
			if(!this.view("list_view", col.getLowerName())) {
				continue;
			}
			if(index > 0) {
				this._bw.write("						, " + col.name + "\n");
			} else {
				this._bw.write("						" + col.name + "\n");
			}
			index++;
		}
		this._bw.write("					from " + this.getTName() + "\n");
		if(this.authentication()) {
			this._bw.write("					where " + this.getAuthenticationColumnName() + " = ?\n");
		}
		this._bw.write("				</sql>\n");
		this._bw.write("				<sql_cnt>\n");
		this._bw.write("					select count(*) from " + this.getTName() + "\n");
		if(this.authentication()) {
			this._bw.write("					where " + this.getAuthenticationColumnName() + " = ?\n");
		}
		if(!this.authentication() && this._m.getDatabaseProductName().equalsIgnoreCase("PostgreSQL")) {
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
		if(this.authentication()) {
			this._bw.write("				<params>\n");
			this._bw.write("					<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			this._bw.write("				</params>\n");
		}
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
			if(!this.view("list_view", col.getLowerName())) {
				continue;
			}
			if(this.view("list_link", col.getLowerName())) {
				this._bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(col.getLowerName()) + ">\n");
				this._bw.write("						<link path=\"/" + this._xmlName + "/detail\">\n");
				for(Column pcol : this._cols) {
					if(pcol.isPk()) {
						this._bw.write("							<param name=\"" + pcol.getLowerName() + "\" type=\"query\" value=\"" + pcol.getLowerName() + "\" />\n");
					}
				}
				this._bw.write("						</link>\n");
				this._bw.write("					</column>\n");
			} else {
				this._bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(col.getLowerName()) + " />\n");
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
		this._bw.write("			<table tableName=\"" + this.getTName() + "\" name=\"" + this._tableName.toLowerCase() + "\" label=\"" + this._tableComments + "\">\n");
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
			} else if(!this.view("insert_view", col.getLowerName())) {
				continue;
			} else {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
			}
		}
		if(this.authentication()) {
			this._bw.write("				<where>\n");
			this._bw.write("					<sql>\n");
			this._bw.write("						" + this.getAuthenticationColumnName() + " = ?\n");
			this._bw.write("					</sql>\n");
			this._bw.write("					<params>\n");
			this._bw.write("						<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			this._bw.write("					</params>\n");
			this._bw.write("				</where>\n");
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
				this._bw.write("			<table tableName=\"" + this.getTName(schema, table) + "\" name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"" + getMultiForInsert(schema, table) + ">\n");
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
					} else if(!this.view("insert_view", schema, table, col.getLowerName())) {
						continue;
					} else {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
					}
				}
				if(this.authentication()) {
					this._bw.write("				<where>\n");
					this._bw.write("					<sql>\n");
					this._bw.write("						" + this.getAuthenticationColumnName(schema, table) + " = ?\n");
					this._bw.write("					</sql>\n");
					this._bw.write("					<params>\n");
					this._bw.write("						<param name=\"" + this.getAuthenticationColumnName(schema, table) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
					this._bw.write("					</params>\n");
					this._bw.write("				</where>\n");
				}
				this._bw.write("			</table>\n");
			}
		}
		this._bw.write("		</tables>\n");
		this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		if(this.fileUpload()) {
			this._bw.write("		<files>\n");
			this._bw.write("			<file\n");
			this._bw.write("				name=\"" + this._xmlName + ".file\"\n");
			this._bw.write("				path=\"${prop.file.upload.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${query." + this._tableName + "." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("				append=\"3\"\n");
			this._bw.write("				backup=\"${prop.file.backup.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${query." + this._tableName + "." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("			/>\n");
			if(this.authentication()) {
				this._bw.write("			<auth check=\"${result} > 0\">\n");
				this._bw.write("				<sql>select count(*) from " + getTName() + " where " + this.getAuthenticationColumnName() + " = ?");
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write(" and " + col.name + " = ?");
					}
				}
				this._bw.write("</sql>\n");
				this._bw.write("				<params>\n");
				this._bw.write("					<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write("					<param name=\"" + col.name + "\" datatype=\"" + this._db.getGrahaDataType(col.dataType) + "\" value=\"param.query." + this._tableName + "." + col.name + "\" />\n");
					}
				}
				this._bw.write("				</params>\n");
				this._bw.write("			</auth>\n");
			}
			this._bw.write("		</files>\n");
		}
		this._bw.write("		<layout msg=\"" + this.getProperty(this._messges, "message.save.confirm.msg") + "\">\n");
		this._bw.write("			<top>\n");
		this._bw.write("				<left />\n");
		this._bw.write("				<center />\n");
		this._bw.write("				<right>\n");
		this._bw.write("					<link name=\"list\" label=\"" + this.getProperty(this._messges, "button.list.label") + "\" path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("					<link name=\"save\" label=\"" + this.getProperty(this._messges, "button.save.label") + "\" path=\"/" + this._xmlName + "/insert\" method=\"post\" type=\"submit\" full=\"true\">\n");
		this._bw.write("						<params>\n");
		
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
			if(!col.isPk() && !this._db.isDef(col.getLowerName()) && this.view("insert_view", col.getLowerName())) {
				this._bw.write("					<row>\n");
				if(col.dataType == java.sql.Types.BOOLEAN) {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\" />\n");
				} else if(col.typeName != null && col.typeName.equals("text")) {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\" />\n");
				} else {
					this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\"" + getFormat(col.getLowerName()) + " />\n");
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
				
				this._bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"" + this.getSingle(schema, table) + ">\n");
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write("						<column name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"hidden\" />\n");
					}
				}
				if(this.multi(schema, table)) {
					this._bw.write("					<row>\n");
				}
				for(Column col : this._cols) {
					if(col.isPk()) {
						continue;
					} else if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						continue;
					} else if(this._db.isDef(col.getLowerName())) {
						continue;
					} else if(!this.view("insert_view", schema, table, col.getLowerName())) {
						continue;
					} else {
						if(!this.multi(schema, table)) {
							this._bw.write("					<row>\n");
						}
						if(col.dataType == java.sql.Types.BOOLEAN) {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\" />\n");
						} else if(col.typeName != null && col.typeName.equals("text")) {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\" />\n");
						} else {
							this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\"" + getFormat(schema, table, col.getLowerName()) + " />\n");
						}
						if(!this.multi(schema, table)) {
							this._bw.write("					</row>\n");
						}
					}
				}
				if(this.multi(schema, table)) {
					this._bw.write("					</row>\n");
				}
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
				if(!this.view("detail_view", col.getLowerName())) {
					continue;
				}
				if(index > 0) {
					this._bw.write("						, " + col.name + "\n");
				} else {
					this._bw.write("						" + col.name + "\n");
				}
				index++;
		}
		this._bw.write("					from " + this.getTName() + "\n");
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
		if(this.authentication()) {
			if(index > 0) {
				this._bw.write("					and " + this.getAuthenticationColumnName() + " = ?\n");
			} else {
				this._bw.write("					where " + this.getAuthenticationColumnName() + " = ?\n");
			}
			index++;
		}
		this._bw.write("				</sql>\n");
		this._bw.write("				<params>\n");
		for(Column col : this._cols) {
			if(col.isPk()) {
				String dataType = this._db.getGrahaDataType(col.dataType);
				this._bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
			}
		}
		if(this.authentication()) {
			this._bw.write("					<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
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
				this._bw.write("			<command name=\"" + table.toLowerCase() + "\"" + getMultiForDetail(schema, table) + ">\n");
				this._bw.write("				<sql>\n");
				this._bw.write("					select\n");
				index = 0;
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
						if(!this.view("detail_view", schema, table, col.getLowerName())) {
							continue;
						}
						if(index > 0) {
							this._bw.write("						, " + col.name + "\n");
						} else {
							this._bw.write("						" + col.name + "\n");
						}
						index++;
				}
				this._bw.write("					from " + this.getTName(schema, table) + "\n");
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
				if(this.authentication()) {
					if(index > 0) {
						this._bw.write("					and " + this.getAuthenticationColumnName(schema, table) + " = ?\n");
					} else {
						this._bw.write("					where " + this.getAuthenticationColumnName(schema, table) + " = ?\n");
					}
					index++;
				}
				this._bw.write("				</sql>\n");
				this._bw.write("				<params>\n");
				for(Column col : this._cols) {
					if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						String dataType = this._db.getGrahaDataType(col.dataType);
						this._bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
					}
				}
				if(this.authentication()) {
					this._bw.write("					<param name=\"" + this.getAuthenticationColumnName(schema, table) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				}
				this._bw.write("				</params>\n");
				this._bw.write("			</command>\n");
			}
		}
		this._bw.write("		</commands>\n");
		if(this.fileUpload()) {
			this._bw.write("		<files>\n");
			this._bw.write("			<file\n");
			this._bw.write("				name=\"" + this._xmlName + ".file\"\n");
			this._bw.write("				path=\"${prop.file.upload.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${query." + this._tableName + "." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("				backup=\"${prop.file.backup.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${query." + this._tableName + "." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("			/>\n");
			if(this.authentication()) {
				this._bw.write("			<auth check=\"${result} > 0\">\n");
				this._bw.write("				<sql>select count(*) from " + getTName() + " where " + this.getAuthenticationColumnName() + " = ?");
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write(" and " + col.name + " = ?");
					}
				}
				this._bw.write("</sql>\n");
				this._bw.write("				<params>\n");
				this._bw.write("					<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				for(Column col : this._cols) {
					if(col.isPk()) {
						this._bw.write("					<param name=\"" + col.name + "\" datatype=\"" + this._db.getGrahaDataType(col.dataType) + "\" value=\"param.query." + this._tableName + "." + col.name + "\" />\n");
					}
				}
				this._bw.write("				</params>\n");
				this._bw.write("			</auth>\n");
			}
			this._bw.write("		</files>\n");
		}
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
			} else if(!this.view("detail_view", col.getLowerName())) {
				continue;
			} else {
				this._bw.write("					<row>\n");
				this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(col.getLowerName()) + " />\n");
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
				this._bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"" + this.getSingle(schema, table) + ">\n");
				if(this.multi(schema, table)) {
					this._bw.write("					<row>\n");
				}
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					if(col.isPk()) {
						continue;
					} else if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						continue;
					} else if(!this.view("detail_view", schema, table, col.getLowerName())) {
						continue;
					} else {
						if(!this.multi(schema, table)) {
							this._bw.write("					<row>\n");
						}
						this._bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(schema, table, col.getLowerName()) + " />\n");
						if(!this.multi(schema, table)) {
							this._bw.write("					</row>\n");
						}
					}
					
				}
				if(this.multi(schema, table)) {
					this._bw.write("					</row>\n");
				}
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
		this._bw.write("			<table tableName=\"" + this.getTName() + "\" name=\"" + this._tableName.toLowerCase() + "\">\n");
		if(this._cols == null) {
			this._cols = this._db.getColumns(this._con, this._schemaName, this._tableName);
		}
		for(Column col : this._cols) {
			String dataType = this._db.getGrahaDataType(col.dataType);
			if(col.isPk()) {
				this._bw.write("				<column name=\"" + col.getLowerName() + "\" primary=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
			}
		}
		if(this.authentication()) {
			this._bw.write("				<where>\n");
			this._bw.write("					<sql>\n");
			this._bw.write("						" + this.getAuthenticationColumnName() + " = ?\n");
			this._bw.write("					</sql>\n");
			this._bw.write("					<params>\n");
			this._bw.write("						<param name=\"" + this.getAuthenticationColumnName() + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			this._bw.write("					</params>\n");
			this._bw.write("				</where>\n");
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
				this._bw.write("			<table tableName=\"" + this.getTName(schema, table) + "\" name=\"" + table.toLowerCase() + "\">\n");
				this._cols = this._db.getColumns(this._con, schema, table);
				for(Column col : this._cols) {
					String dataType = this._db.getGrahaDataType(col.dataType);
					if(this._db.containsKey(this._con, this._schemaName, this._tableName, col.name)) {
						this._bw.write("				<column name=\"" + col.getLowerName() + "\" foreign=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
					}
				}
				if(this.authentication()) {
					this._bw.write("				<where>\n");
					this._bw.write("					<sql>\n");
					this._bw.write("						" + this.getAuthenticationColumnName(schema, table) + " = ?\n");
					this._bw.write("					</sql>\n");
					this._bw.write("					<params>\n");
					this._bw.write("						<param name=\"" + this.getAuthenticationColumnName(schema, table) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
					this._bw.write("					</params>\n");
					this._bw.write("				</where>\n");
				}
				this._bw.write("			</table>\n");
			}
		}
		this._bw.write("		</tables>\n");
		if(this.fileUpload()) {
			this._bw.write("		<files>\n");
			this._bw.write("			<file\n");
			this._bw.write("				name=\"" + this._xmlName + ".file\"\n");
			this._bw.write("				path=\"${prop.file.upload.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${param." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("				backup=\"${prop.file.backup.directory}/" + this._xmlName + "");
			for(Column col : this._cols) {
				if(col.isPk()) {
					this._bw.write("/${param." + col.name + "}");
				}
			}
			this._bw.write("\"\n");
			this._bw.write("			/>\n");
			this._bw.write("		</files>\n");
		}
		this._bw.write("		<redirect path=\"/" + this._xmlName + "/list\" />\n");
		this._bw.write("	</query>\n");
	}
}

