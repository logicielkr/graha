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
import kr.graha.helper.LOG;
import java.io.IOException;
import java.sql.SQLException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

/**
 * Graha(그라하) XML Config 파일을 생성한다.

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.6
 */

public final class XMLConfigGenerator {
	private Table _masterTableByParameter;
	private Table _masterTable = null;
	private CManager _cm;
	private Connection _con;
	private DBUtil _db;
	private Properties _messges;
	
	private java.util.List<Table> tabs = null;
	
	private java.util.List<String> files = null;
	
	protected XMLConfigGenerator(
		Table masterTable,
		CManager cm,
		Connection con
	) throws SQLException, IOException {
		this._masterTableByParameter = masterTable;
		this._cm = cm;
		this._con = con;
		this.init();
	}
	private void init() throws SQLException, IOException {
		this._db = DBUtil.getDBUtil(this._con, this._cm.getDef(), this._cm.getMapping());
		this._messges = this._cm.getMessages();
	}
	private void addFile(String filePath) {
		if(this.files == null) {
			this.files = new java.util.ArrayList<String>();
		}
		this.files.add(filePath);
	}
	protected java.util.List<String> getFiles() {
		return this.files;
	}
	private String defaultSchema() throws SQLException {
		return this._con.getSchema();
	}
	private boolean notEqualsDefaultSchema(String schemaName) throws SQLException {
		if(defaultSchema() != null && schemaName != null && !schemaName.equals(defaultSchema())) {
			return true;
		} else {
			return false;
		}
	}
	protected static String getXMLFileName(File f) {
		return f.getName().substring(0, f.getName().length() - 4);
	}
	protected static File getXMLFile(String basePath, String xmlFileName, boolean mkdir) {
		return getUniqueFile(basePath, xmlFileName.toLowerCase(), "xml", "-", mkdir);
	}
	private static File getUniqueFile(String basePath, String fileName, String fileExtension, String separator, boolean mkdir) {
		File f = null;
		int index = 0;
		f = new File(basePath);
		if(mkdir && !f.exists()) {
			f.mkdir();
		}
		while(true) {
			if(index == 0) {
				if(fileExtension != null && !fileExtension.equals("")) {
					f = new File(basePath + File.separator + fileName + "." + fileExtension);
				} else {
					f = new File(basePath + File.separator + fileName);
				}
			} else {
				if(fileExtension != null && !fileExtension.equals("")) {
					f = new File(basePath + File.separator + fileName + separator + index + "." + fileExtension);
				} else {
					f = new File(basePath + File.separator + fileName + separator + index);
				}
			}
			if(!f.exists()) {
				break;
			}
			index++;
		}
		return f;
	}
	private boolean postgresql() throws SQLException {
		return this._con.getMetaData().getDatabaseProductName().equalsIgnoreCase("PostgreSQL");
	}
	private String getTName(Table table) throws SQLException {
		return getTName(table.schema, table.name);
	}
	private String getTName(String schemaName, String tableName) throws SQLException {
		if(this.notEqualsDefaultSchema(schemaName)) {
			return schemaName + "." + tableName;
		} else {
			return tableName;
		}
	}
	
	private boolean equals(String key, String value) {
		if(
			key == null || 
			value == null || 
			this._cm.getParameter(key) == null ||
			key.equals("") ||
			value.equals("")
		) {
			return false;
		}
		if(value.equals(this._cm.getParameter(key))) {
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
	private boolean search() {
		return this.equals("search", "true");
	}
	private String codeColumn(String schemaName, String tableName, String columnName) {
		String value = this._cm.param("code_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName);
		if(value != null && !value.trim().equals("")) {
			return value;
		}
		return null;
	}
	private String codeColumn(Table table, String columnName) {
		return this.codeColumn(table.schema, table.name, columnName);
	}
	private String getCodeExprForList(Table table, String columnName) {
		return this.getCodeExprForList(table.schema, table.name, columnName);
	}
	private String getCodeExprForList(String schemaName, String tableName, String columnName) {
		String codeName = this.codeColumn(schemaName, tableName, columnName);
		if(codeName != null) {
			return " code=\"true\" for=\"" + codeName + "\"";
		} else {
			return "";
		}
	}
	private String getCodeExprForInsert(Table table, String columnName) {
		return this.getCodeExprForInsert(table.schema, table.name, columnName);
	}
	private String getCodeExprForInsert(String schemaName, String tableName, String columnName) {
		String codeName = this.codeColumn(schemaName, tableName, columnName);
		if(codeName != null) {
			return " type=\"select\" for=\"" + codeName + "\"";
		} else {
			return "";
		}
	}
	
	private boolean searchColumn(String schemaName, String tableName, String columnName) {
		return this.equals("list_search_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", columnName);
	}
	private boolean searchColumn(Table table, String columnName) {
		return this.searchColumn(table.schema, table.name, columnName);
	}
	private boolean page() {
		return this.equals("list_type", "page");
	}
	private String listFuncType() {
		if(this.page()) {
			return "list";
		} else {
			return "listAll";
		}
	}
	private String getAuthenticationColumnName(String schemaName, String tableName) {
		return this._cm.param("auth_column_" + Table.getNameWithSchema(schemaName, tableName));
	}
	private String getAuthenticationColumnName(Table table) {
		return this.getAuthenticationColumnName(table.schema, table.name);
	}
	private boolean view(String type, Table table, String columnName) {
		return this.view(type, table.schema, table.name, columnName);
	}
	private boolean view(String type, String schemaName, String tableName, String columnName) {
		return this.equals(type + "_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", columnName);
	}
	private String dateFormatForJava(String format) {
		if(format.equals("date")) {
			return "yyyy-MM-dd";
		} else if(format.equals("datetime")) {
			return "yyyy-MM-dd HH:mm:ss";
		} else {
			return null;
		}
	}
	private String getDateFormatForJava(Table table, String columnName, String grahaDataType) {
		return this.getDateFormatForJava(table.schema, table.name, columnName, grahaDataType);
	}
	private String getDateFormatForJava(String schemaName, String tableName, String columnName, String grahaDataType) {
		if(
				grahaDataType.equals("date") ||
				grahaDataType.equals("timestamp")
		) {
			if(
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "date") ||
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "datetime")
			) {
				return dateFormatForJava(this._cm.param("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + ""));
			}
		}
		return null;
	}
	private String getDateFormatExpression(Table table, String columnName, String grahaDataType) {
		return this.getDateFormatExpression(table.schema, table.name, columnName, grahaDataType);
	}
	private String getDateFormatExpression(String schemaName, String tableName, String columnName, String grahaDataType) {
		if(!this._db.supportBultinDateFormatFunction()) {
			return columnName;
		}
		if(
				grahaDataType.equals("date") ||
				grahaDataType.equals("timestamp")
		) {
			if(
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "date") ||
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "datetime")
			) {
				return this._db.dateFormat(columnName, this._cm.param("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + ""));
			}
		}
		return columnName;
	}
	private boolean supportDatabaseDateFormat() {
		return this._db.supportBultinDateFormatFunction();
	}
	private String getFormat(Table table, String columnName) {
		return this.getFormat(table.schema, table.name, columnName);
	}
	private String getFormat(String schemaName, String tableName, String columnName) {
		if(this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "ts")) {
			return " fmt=\"#,##0\"";
		} else {
			return "";
		}
	}
	private String getPattern(Table table, String columnName, String grahaDataType) {
		return getPattern(table.schema, table.name, columnName, grahaDataType);
	}
	private String getPattern(String schemaName, String tableName, String columnName, String grahaDataType) {
		if(
				grahaDataType.equals("date") ||
				grahaDataType.equals("timestamp")
		) {
			if(
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "date") ||
				this.equals("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", "datetime")
			) {
				return " pattern=\"" + dateFormatForJava(this._cm.param("format_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "")) + "\"";
			}
		}
		return "";
	}
	private String getReadonlyForInsert(Table table, String columnName) {
		return this.getReadonlyForInsert(table.schema, table.name, columnName);
	}
	private String getReadonlyForInsert(String schemaName, String tableName, String columnName) {
		if(this.readonly(schemaName, tableName, columnName)) {
			return " readonly=\"true\"";
		} else {
			return "";
		}
	}
	private boolean readonly(Table table, String columnName) {
		return this.readonly(table.schema, table.name, columnName);
	}
	private boolean readonly(String schemaName, String tableName, String columnName) {
		return this.equals("insert_readonly_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", columnName);
	}
	private String validation(Table table, String columnName, boolean isMasterTable) {
		return this.validation(table.schema, table.name, columnName, isMasterTable);
	}
	private String validation(String schemaName, String tableName, String columnName, boolean isMasterTable) {
		String result = null;
		String validation = this._cm.param("insert_validation_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "");
		if(validation == null) {
			return null;
		}
		String paramName = null;
		if(isMasterTable) {
			paramName = columnName;
		} else {
			paramName = tableName + "." + columnName;
		}
		if(
			validation.equals("int") ||
			validation.equals("long") ||
			validation.equals("float") ||
			validation.equals("double")
		) {
			result = "<param name=\"" + paramName.toLowerCase() + "\" number-format=\"" + validation + "\" msg=\"message." + paramName.toLowerCase() + "." + validation + "\" />";
		} else if(validation.equals("not-null")) {
			result = "<param name=\"" + paramName.toLowerCase() + "\" not-null=\"true\" msg=\"message." + paramName.toLowerCase() + "." + validation + "\" />";
		} else if(
			validation.equals("min-length") ||
			validation.equals("max-length")
		) {
			String info = this._cm.param("insert_validation_info_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "");
			result = "<param name=\"" + paramName.toLowerCase() + "\" " + validation + "=\"" + info + "\" msg=\"message." + paramName.toLowerCase() + "." + validation + "." + info + "\" />";
		}

		return result;
	}
	private String calculator(Table table, String columnName) {
		return this.calculator(table.schema, table.name, columnName);
	}
	private String calculator(String schemaName, String tableName, String columnName) {
		if(this.equals("insert_is_calculation_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "", columnName)) {
			return this._cm.param("insert_calculation_column_" + Table.getNameWithSchema(schemaName, tableName) + "___" + columnName + "");
		} else {
			return null;
		}
	}
	private boolean multi(Table table) {
		return this.multi(table.schema, table.name);
	}
	private boolean multi(String schemaName, String tableName) {
		return this.equals("relation_" + Table.getNameWithSchema(schemaName, tableName), "many");
	}
	private String getMultiForInsert(Table table) {
		return this.getMultiForInsert(table.schema, table.name);
	}
	private String getMultiForInsert(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName)) {
			return " multi=\"true\" " + this._cm.param("row_count_type_" + Table.getNameWithSchema(schemaName, tableName)) + "=\"" + this._cm.param("row_count_" + Table.getNameWithSchema(schemaName, tableName)) + "\"";
		} else {
			return "";
		}
	}
	private String getMultiForDetail(Table table) {
		return this.getMultiForDetail(table.schema, table.name);
	}
	private String getMultiForDetail(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName)) {
			return " multi=\"true\"";
		} else {
			return "";
		}
	}
	private String getSingle(Table table) {
		return this.getSingle(table.schema, table.name);
	}
	private String getSingle(String schemaName, String tableName) {
		if(this.multi(schemaName, tableName) && this.equals("header_position_" + Table.getNameWithSchema(schemaName, tableName), "top")) {
			return " single=\"true\"";
		} else {
			return "";
		}
	}
	private String getProperty(String key) {
		return this._cm.getProperty(this._messges, key);
	}
	private Table getMasterTable() throws SQLException {
		if(this._masterTable != null) {
			return this._masterTable;
		}
		java.util.List<Table> list = tables();
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				if(isMasterTable(list.get(i))) {
					this._masterTable = list.get(i);
					return list.get(i);
				}
			}
		}
		return this._masterTableByParameter;
	}
	private boolean isMasterTable(Table table) {
		return table.compareWithSchemaAndTableName(this._masterTableByParameter);
	}
	private java.util.List<Table> tables() throws SQLException {
		if(this.tabs == null) {
			this.tabs = new java.util.ArrayList<Table>();
			String[] tables = this._cm.getParameterValues("tables");
			if(tables != null) {
				for(int i = 0; i < tables.length; i++) {
					this.tabs.add(new Table(tables[i]));
				}
			}
			this.tabs = this._db.getTablesWithColumns(this._con, this.tabs);
		}
		return this.tabs;
	}
	private java.util.HashMap getCodeList() {
		java.util.HashMap codes = new java.util.HashMap();
		java.util.Enumeration paramNames = this._cm.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			if(paramName.startsWith("code_column_")) {
				String param = this._cm.param(paramName);
				if(param != null && !param.trim().equals("") && !codes.containsKey(param)) {
					codes.put(param, param);
				}
			}
		}
		return codes;
	}
	private java.util.List<String> getFileLst() throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader in = null;
		java.util.List<String> list = new java.util.ArrayList<String>();
		try {
			is = this.getClass().getResourceAsStream("/kr/graha/assistant/client_lib/file.lst");
			isr = new InputStreamReader(is, StandardCharsets.ISO_8859_1);
			in = new BufferedReader(isr);
			String s;
			while ((s = in.readLine()) != null) {
				if(s != null && !s.trim().equals("")) {
					if(s.startsWith("./")) {
						list.add(s.substring(2));
					} else {
						list.add(s);
					}
				}
			}
			in.close();
			in = null;
			isr.close();
			isr = null;
			is.close();
			is = null;
		} catch (IOException e) {
			list = null;
			LOG.severe(e);
			throw e;
		} finally {
			if(in != null) {
				try {in.close();} catch(IOException e) {
					LOG.severe(e);
				}
			}
			if(isr != null) {
				try {isr.close();} catch(IOException e) {
					LOG.severe(e);
				}
			}
			if(is != null) {
				try {is.close();} catch(IOException e) {
					LOG.severe(e);
				}
			}
		}
		return list;
	}
	private void copy(String from, File to) throws IOException {
		File parent = to.getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		java.io.InputStream in = null;
		java.io.OutputStream out = null;
		try {
			in = this.getClass().getResourceAsStream("/kr/graha/assistant/client_lib/" + from);
			if(in == null) {
				throw new IOException("Source File is not exists!!!(" + ("/kr/graha/assistant/client_lib/" + from) + ")");
			}
			out = new FileOutputStream(to);
			byte[] buffer = new byte[8192];
			int len = 0;
			while((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} finally {
			if(in != null) {
				in.close();
			}
			if(out != null) {
				out.close();
			}
		}
	}
	private void copy(String from, String to) throws IOException {
		this.copy(from, new File(to));
	}
	private String copy(java.util.List<String> fileLst, String fileName, File to) throws IOException {
		if(fileLst == null || fileLst.size() == 0) {
			throw new IOException("fileLst is null or size is 0");
		}
		for(int i = 0; i < fileLst.size(); i++) {
			if(fileLst.get(i).endsWith(fileName)) {
				copy(fileLst.get(i), to.getPath() + fileLst.get(i).substring(fileLst.get(i).indexOf("/")));
				return fileLst.get(i).substring(fileLst.get(i).indexOf("/"));
			}
		}
		throw new IOException("file is not exists (" + fileName + ")");
	}
	private void createCss(String xmlName, Hashtable<String, String> result) throws IOException, SQLException {
		File cssDir = getUniqueFile(this._cm.getRealPath("/graha_css"), xmlName, null, "_", true);
		if(!cssDir.exists()) {
			cssDir.mkdirs();
		}
		result.put("css", cssDir.getName());
		if(createCss(xmlName, "list", cssDir)) {
			result.put("list.css", "../../graha_css/" + cssDir.getName() + "/list.css");
			this.addFile("/graha_css/" + cssDir.getName() + "/list.css");
		}
		if(createCss(xmlName, "detail", cssDir)) {
			result.put("detail.css", "../../graha_css/" + cssDir.getName() + "/detail.css");
			this.addFile("/graha_css/" + cssDir.getName() + "/detail.css");
		}
		if(createCss(xmlName, "insert", cssDir)) {
			result.put("insert.css", "../../graha_css/" + cssDir.getName() + "/insert.css");
			this.addFile("/graha_css/" + cssDir.getName() + "/insert.css");
		}
	}
	private boolean createCss(String xmlName, String id, File dir) throws IOException, SQLException {
		File f = new File(dir.getPath() + File.separator + id + ".css");
		LOG.finest(dir.getPath() + File.separator + id + ".css");
		
		StringBuffer css = new StringBuffer();
		java.util.List<String> hideMobileColumn = new java.util.ArrayList<String>();
		for (Table tab2 : this.tables()) {
			if(!this.multi(tab2)) {
				if(id.equals("detail") || id.equals("insert")) {
					String width = this._cm.param("header_column_width");
					css.append("table#" + tab2.getLowerName() + " th {\n");
					try {
						css.append("	width:" + Integer.valueOf(width) + "px;\n");
					} catch (NumberFormatException e) {
						css.append("	width:" + width + ";\n");
					}
					css.append("}\n");
				}
			}
			boolean isMasterTable = isMasterTable(tab2);
			for(Column col : tab2.cols) {
				if(isMasterTable || !this.multi(tab2)) {
					if(
						id.equals("detail") &&
						col.typeName != null && col.typeName.equals("text") &&
						this.view("detail_view", tab2, col.name)
					) {
						css.append("table#" + tab2.getLowerName() + " td." + col.getLowerName() + " {\n");
						css.append("	white-space:pre-wrap;\n");
						css.append("	min-height:100px;\n");
						css.append("}\n");
					}
				}
					
				if(isMasterTable || this.multi(tab2)) {
					String width = null;
					String align = null;
					if(id.equals("list") && isMasterTable && this.view("list_view", tab2, col.name)) {
						width = this._cm.param("list_width_column_" + tab2.getNameWithSchema() + "___" + col.name + "");
						align = this._cm.param("list_align_" + tab2.getNameWithSchema() + "___" + col.name + "");
					} else if(id.equals("detail") && this.view("detail_view", tab2, col.name)) {
						width = this._cm.param("detail_width_column_" + tab2.getNameWithSchema() + "___" + col.name + "");
						align = this._cm.param("detail_align_" + tab2.getNameWithSchema() + "___" + col.name + "");
					} else if(id.equals("insert") && this.view("insert_view", tab2, col.name)) {
						width = this._cm.param("insert_width_column_" + tab2.getNameWithSchema() + "___" + col.name + "");
						align = this._cm.param("insert_align_" + tab2.getNameWithSchema() + "___" + col.name + "");
					}
					if(width != null && !width.trim().equals("")) {
						css.append("table#" + tab2.getLowerName() + " th." + col.getLowerName() + ",\n");
						css.append("table#" + tab2.getLowerName() + " td." + col.getLowerName() + " {\n");
						try {
							css.append("	width:" + Integer.valueOf(width) + "px;\n");
						} catch (NumberFormatException e) {
							css.append("	width:" + width + ";\n");
						}
						css.append("}\n");
					}
					if(align != null && !align.trim().equals("") && !align.trim().equals("left")) {
						if(id.equals("insert")) {
							css.append("table#" + tab2.getLowerName() + " td." + col.getLowerName() + " input." + tab2.getLowerName() + "\\." + col.getLowerName() + " {\n");
						} else {
							css.append("table#" + tab2.getLowerName() + " td." + col.getLowerName() + " {\n");
						}
						css.append("	text-align:" + align + ";\n");
						css.append("}\n");
					}
					if(
						(id.equals("list") && isMasterTable && this.equals("list_hide_mobile_column_" + tab2.getNameWithSchema() + "___" + col.name + "", col.name)) ||
						(id.equals("detail") && this.equals("detail_hide_mobile_column_" + tab2.getNameWithSchema() + "___" + col.name + "", col.name))
					) {
						hideMobileColumn.add("table#" + tab2.getLowerName() + " th." + col.getLowerName() + "");
						hideMobileColumn.add("table#" + tab2.getLowerName() + " td." + col.getLowerName() + "");
					}
				}
			}
		}
		if(hideMobileColumn.size() > 0) {
			css.append("@media only screen and (max-width: 600px) {\n");
			for(int i = 0; i < hideMobileColumn.size(); i++) {
				css.append("\t" + hideMobileColumn.get(i) + "");
				if(i + 1 == hideMobileColumn.size()) {
				} else {
					css.append(",");
				}
				css.append("\n");
			}
			css.append("	{\n");
			css.append("		display:none;\n");
			css.append("	}\n");
			css.append("}\n");
		}
		if(css.length() > 0) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new java.io.OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8));
				bw.write(css.toString());
				bw.close();
				bw = null;
				return true;
			} catch (IOException e) {
				LOG.severe(e);
				throw e;
			} finally {
				try {
					if(bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					LOG.severe(e);
				}
			}
		} else {
			return false;
		}
	}
	private void findBaseJs(Hashtable<String, String> result) {
		if(result.containsKey("GrahaFormula.js")) {
			return;
		}
		result.put("fn_check.js.remote", "//graha.kr/static-contents/client_lib/graha_base_library/lastest/fn_check.js");
		result.put("GrahaFormula.js.remote", "//graha.kr/static-contents/client_lib/formula/lastest/GrahaFormula.js");
		result.put("DateParser.js.remote", "//graha.kr/static-contents/client_lib/date_parser/lastest/DateParser.js");
		
		File f = new File(this._cm.getRealPath("/graha_js"));
		File[] dirs = f.listFiles(new java.io.FilenameFilter() { 
			public boolean accept(File dir, String name) { 
				return (name.startsWith("base") || name.startsWith("formula") || name.startsWith("date_parser")); 
			}
		});
		if(dirs != null) {
			for(int i = 0; i < dirs.length; i++) {
				File[] vers = dirs[i].listFiles();
				if(vers != null) {
					for(int x = 0; x < vers.length; x++) {
						File[] files = vers[x].listFiles(new java.io.FilenameFilter() { 
							public boolean accept(File dir, String name) { 
								return (name.equals("GrahaFormula.js") || name.equals("fn_check.js") || name.equals("DateParser.js")); 
							}
						});
						if(files != null) {
							for(int a = 0; a < files.length; a++) {
								result.put(files[a].getName(), "../../graha_js/" + dirs[i].getName() + "/" + vers[x].getName() + "/" + files[a].getName());
							}
						}
					}
				}
			}
		}
	}
	private Hashtable<String, String> createBase() throws IOException {
		File f = new File(this._cm.getRealPath("/WEB-INF/graha/") + File.separator + "_base.xml");
		Hashtable<String, String> result = new Hashtable<String, String>();
		if(!f.exists()) {
			File grahaCssBaseDirectory = getUniqueFile(this._cm.getRealPath("/graha_css"), "base", null, "_", true);
			File grahaJsBaseDirectory = getUniqueFile(this._cm.getRealPath("/graha_js"), "base", null, "_", true);
			File grahaFormulaJsDirectory = getUniqueFile(this._cm.getRealPath("/graha_js"), "formula", null, "_", true);
			File dateParserJsDirectory = getUniqueFile(this._cm.getRealPath("/graha_js"), "date_parser", null, "_", true);
			BufferedWriter bw = null;
			try {
				java.util.List fileLst = getFileLst();
				String defaultCss = copy(fileLst, "default.css", grahaCssBaseDirectory);
				addFile("/graha_css/" + grahaCssBaseDirectory.getName() + defaultCss);
				String defaultNavCss = copy(fileLst, "default.nav.css", grahaCssBaseDirectory);
				addFile("/graha_css/" + grahaCssBaseDirectory.getName() + defaultNavCss);
				String navSmallScreenCss = copy(fileLst, "nav.small.screen.css", grahaCssBaseDirectory);
				addFile("/graha_css/" + grahaCssBaseDirectory.getName() + navSmallScreenCss);
				String inputWidthCss = copy(fileLst, "input.width.css", grahaCssBaseDirectory);
				addFile("/graha_css/" + grahaCssBaseDirectory.getName() + inputWidthCss);
				
				String getMessageJs = copy(fileLst, "get_message.js", grahaJsBaseDirectory);
				addFile("/graha_js/" + grahaJsBaseDirectory.getName() + getMessageJs);
				String checkSubmitJs = copy(fileLst, "check_submit.js", grahaJsBaseDirectory);
				addFile("/graha_js/" + grahaJsBaseDirectory.getName() + checkSubmitJs);
				String fnCheckJs = copy(fileLst, "fn_check.js", grahaJsBaseDirectory);
				addFile("/graha_js/" + grahaJsBaseDirectory.getName() + fnCheckJs);
				String parseGrahaXmlDocumentJs = copy(fileLst, "parse_graha_xml_document.js", grahaJsBaseDirectory);
				addFile("/graha_js/" + grahaJsBaseDirectory.getName() + parseGrahaXmlDocumentJs);
				
				String grahaFormulaJs = copy(fileLst, "GrahaFormula.js", grahaFormulaJsDirectory);
				addFile("/graha_js/" + grahaFormulaJsDirectory.getName() + grahaFormulaJs);
				String dateParserJs = copy(fileLst, "DateParser.js", dateParserJsDirectory);
				addFile("/graha_js/" + dateParserJsDirectory.getName() + dateParserJs);
				
				bw = new BufferedWriter(new java.io.OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8));
				bw.write("<querys>\n");
				bw.write("	<header>\n");
				bw.write("		<style name=\"default.default\" src=\"../../graha_css/" + grahaCssBaseDirectory.getName() + defaultCss + "\" />\n");
				bw.write("		<style name=\"default.nav\" src=\"../../graha_css/" + grahaCssBaseDirectory.getName() + defaultNavCss + "\" />\n");
				bw.write("		<style name=\"nav.small.screen\" src=\"../../graha_css/" + grahaCssBaseDirectory.getName() + navSmallScreenCss + "\" />\n");
				bw.write("		<style name=\"input.width\" src=\"../../graha_css/" + grahaCssBaseDirectory.getName() + inputWidthCss + "\" />\n");
				bw.write("		<script name=\"get_message\" src=\"../../graha_js/" + grahaJsBaseDirectory.getName() + getMessageJs + "\" />\n");
				bw.write("		<script name=\"check_submit\" src=\"../../graha_js/" + grahaJsBaseDirectory.getName() + checkSubmitJs + "\" />\n");
				result.put("fn_check.js", "../../graha_js/" + grahaJsBaseDirectory.getName() + fnCheckJs);
				bw.write("		<script name=\"ajax_parse_graha_xml_document\" src=\"../../graha_js/" + grahaJsBaseDirectory.getName() + parseGrahaXmlDocumentJs + "\" />\n");
				result.put("GrahaFormula.js", "../../graha_js/" + grahaFormulaJsDirectory.getName() + grahaFormulaJs);
				result.put("DateParser.js", "../../graha_js/" + dateParserJsDirectory.getName() + dateParserJs);
				bw.write("<!--\n");
				bw.write("		<style name=\"default.default\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + defaultCss + "\" />\n");
				bw.write("		<style name=\"default.nav\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + defaultNavCss + "\" />\n");
				bw.write("		<style name=\"nav.small.screen\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + navSmallScreenCss + "\" />\n");
				bw.write("		<style name=\"input.width\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + inputWidthCss + "\" />\n");
				bw.write("		<script name=\"get_message\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + getMessageJs + "\" />\n");
				bw.write("		<script name=\"check_submit\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + checkSubmitJs + "\" />\n");
				result.put("fn_check.js.remote", "//graha.kr/static-contents/client_lib/graha_base_library" + fnCheckJs);
				bw.write("		<script name=\"ajax_parse_graha_xml_document\" src=\"//graha.kr/static-contents/client_lib/graha_base_library" + parseGrahaXmlDocumentJs + "\" />\n");
				
				result.put("GrahaFormula.js.remote", "//graha.kr/static-contents/client_lib/formula" + grahaFormulaJs);
				result.put("DateParser.js.remote", "//graha.kr/static-contents/client_lib/date_parser" + dateParserJs);
				
				bw.write("-->\n");
				bw.write("	</header>\n");
				bw.write("</querys>\n");
				bw.close();
				bw = null;
			} catch (IOException e) {
				LOG.severe(e);
				throw e;
			} finally {
				try {
					if(bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					LOG.severe(e);
				}
			}
		}
		return result;
	}
	
	protected String execute() throws IOException, SQLException {
		String xmlFileName = this._cm.param("xml_file_name");
		if(xmlFileName == null || xmlFileName.trim().equals("")) {
			xmlFileName = getMasterTable().getLowerName();
		} else if(xmlFileName.toLowerCase().endsWith(".xml")) {
			xmlFileName = xmlFileName.substring(0, xmlFileName.length() - 4).toLowerCase();
		} else {
			xmlFileName = xmlFileName.toLowerCase();
		}
		File f = getXMLFile(this._cm.getRealPath("/WEB-INF/graha/"), xmlFileName, true);
		String xmlName = getXMLFileName(f);
		BufferedWriter bw = null;
		try {
			Hashtable cssAndJs = createBase();
			findBaseJs(cssAndJs);
			createCss(xmlName, cssAndJs);
			bw = new BufferedWriter(new java.io.OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8));
			execute(bw, xmlName, cssAndJs);
			bw.close();
			bw = null;
			this.addFile(f.getPath().substring(this._cm.getRealPath("/").length() - 1));
		} catch (IOException | SQLException e) {
			throw e;
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				LOG.severe(e);
			}
		}
		return xmlName;
	}
	private void execute(BufferedWriter bw, String xmlName, Hashtable cssAndJs) throws IOException, SQLException {
		bw.write("<querys>\n");
		bw.write("	<header extends=\"_base.xml\">\n");
		bw.write("		<jndi name=\"" + this._cm.getJndi() + "\" />\n");
		if(this.authentication()) {
			bw.write("		<prop name=\"logined_user\" value=\"guest\" />\n");
			bw.write("		<prop name=\"logined_user\" value=\"${session.user_id}\" cond=\"${session.user_id} isNotEmpty\" />\n");
			bw.write("		<prop name=\"logined_user\" value=\"${header.remote_user}\" cond=\"${header.remote_user} isNotEmpty\" />\n");
		}
		if(this.fileUpload()) {
			bw.write("		<prop name=\"file.upload.directory\" value=\"${system.context.root.path}WEB-INF/file/upload\" />\n");
			bw.write("		<prop name=\"file.backup.directory\" value=\"${system.context.root.path}WEB-INF/file/backup\" />\n");
		}
		java.util.HashMap codes = this.getCodeList();
		if(!codes.isEmpty()) {
			java.util.Iterator<String> it = codes.keySet().iterator();
			int index = 0;
			while(it.hasNext()) {
				String key = (String)it.next();
				if(index == 0) {
					bw.write("		<codes>\n");
				}
				bw.write("			<code name=\"" + key + "\">\n");
				bw.write("				<sql>\n");
				bw.write("					select value, label from " + this._cm.getGrahaCommonCodeTableName() + "\n");
				bw.write("					where parent_id in (\n");
				bw.write("						select graha_common_code_id from " + this._cm.getGrahaCommonCodeTableName() + "\n");
				bw.write("						where (parent_id is null or parent_id = 0)\n");
				bw.write("							and value = '" + key + "'\n");
				bw.write("					)\n");
				bw.write("					order by order_number, value\n");
				bw.write("				</sql>\n");
				bw.write("			</code>\n");
				index++;
			}
			if(index > 0) {
				bw.write("		</codes>\n");
			}
		}
		bw.write("	</header>\n");
		this.list(bw, xmlName, cssAndJs);
		this.insert(bw, xmlName, cssAndJs);
		this.detail(bw, xmlName, cssAndJs);
		this.delete(bw, xmlName);
		bw.write("</querys>\n");
	}
	private void list(BufferedWriter bw, String xmlName, Hashtable cssAndJs) throws IOException, SQLException {
		Table masterTable = this.getMasterTable();
		int index = 0;
		bw.write("	<query id=\"list\" funcType=\"" + listFuncType() + "\" label=\"" + masterTable.getRemarksOrName() + "\">\n");
		bw.write("		<header>\n");
		if(cssAndJs.containsKey("list.css")) {
			bw.write("			<style src=\"" + cssAndJs.get("list.css") + "\" />\n");
		}
		bw.write("		</header>\n");
		bw.write("		<commands>\n");
		bw.write("			<command name=\"" + masterTable.getLowerName() + "\" query_to_param=\"no\">\n");
		if(!this._db.supportBultinDateFormatFunction()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(!col.isPk() && !this.view("list_view", masterTable, col.name)) {
					continue;
				}
				String pattern = getDateFormatForJava(masterTable, col.name, this._db.getGrahaDataType(col.dataType));
				if(pattern != null) {
					if(index == 0) {
						bw.write("				<pattern>\n");
					}
					bw.write("					<column name=\"" + col.getLowerName() + "\" pattern=\"" + pattern + "\" />\n");
					index++;
				}
			}
			if(index > 0) {
				bw.write("				</pattern>\n");
			}
		}
		if(this.page()) {
			bw.write("				<sql pageSize=\"15\" pageGroupSize=\"10\">\n");
		} else {
			bw.write("				<sql>\n");
		}
		bw.write("					select\n");
		index = 0;
		
		for(Column col : masterTable.cols) {
			if(!col.isPk() && !this.view("list_view", masterTable, col.name)) {
				continue;
			}
			bw.write("						");
			if(index > 0) {
				bw.write(", ");
			}
			if(this._db.supportBultinDateFormatFunction()) {
				bw.write(getDateFormatExpression(masterTable, col.name, this._db.getGrahaDataType(col.dataType)));
			} else {
				bw.write(col.name);
			}
			bw.write("\n");
			index++;
		}
		bw.write("					from " + this.getTName(masterTable) + "\n");
		if(this.authentication()) {
			bw.write("					where " + this.getAuthenticationColumnName(masterTable) + " = ?\n");
		}
		StringBuffer searchBuffer = new StringBuffer();
		if(this.search()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(
					this.searchColumn(masterTable, col.name) &&
					this.codeColumn(masterTable, col.name) != null
				) {
					if(index == 0 && !this.authentication()) {
						searchBuffer.append("					where ");
					} else {
						searchBuffer.append("						and ");
					}
					searchBuffer.append(col.name + " = ?\n");
				}
			}
			index = 0;
			for(Column col : masterTable.cols) {
				if(
					this.searchColumn(masterTable, col.name) &&
					this.codeColumn(masterTable, col.name) == null
				) {
					if(index == 0) {
						searchBuffer.append("<tile cond=\"${param.search} isNotEmpty\">\n");
						if(!this.authentication() && searchBuffer.length() == 0) {
							searchBuffer.append("					where (");
						} else {
							searchBuffer.append("						and (");
						}
					} else {
						searchBuffer.append(" or ");
					}
					searchBuffer.append(col.name + " like '%' || ? || '%'");
					index++;
				}
			}
			if(index > 0) {
				searchBuffer.append(")\n");
				searchBuffer.append("</tile>\n");
			}
		}
		if(searchBuffer.length() > 0) {
			bw.write(searchBuffer.toString());
		}
		bw.write("				</sql>\n");
		if(this.page()) {
			bw.write("				<sql_cnt>\n");
			bw.write("					select count(*) from " + this.getTName(masterTable) + "\n");
			if(this.authentication()) {
				bw.write("					where " + this.getAuthenticationColumnName(masterTable) + " = ?\n");
			}
			if(searchBuffer.length() > 0) {
				bw.write(searchBuffer.toString());
			}
			if(!this.search() && !this.authentication() && this.postgresql()) {
				bw.write("/*\n");
				bw.write("					SELECT n_live_tup\n");
				bw.write("					FROM pg_stat_all_tables\n");
				if(this.notEqualsDefaultSchema(masterTable.schema)) {
					bw.write("					WHERE relname = '" + masterTable.name + "' and schemaname = '" + masterTable.schema + "'\n");
				} else {
					bw.write("					WHERE relname = '" + masterTable.name + "'\n");
				}
				bw.write("*/\n");
				bw.write("/*\n");
				bw.write("					SELECT reltuples\n");
				bw.write("					FROM pg_class\n");
				if(this.notEqualsDefaultSchema(masterTable.schema)) {
					bw.write("					WHERE relname = '" + masterTable.name + "' and relnamespace::regnamespace::text = '" + masterTable.schema + "'\n");
				} else {
					bw.write("					WHERE relname = '" + masterTable.name + "'\n");
				}
				bw.write("*/\n");
			}
			bw.write("				</sql_cnt>\n");
		}
		if(this.authentication() || this.search()) {
			bw.write("				<params>\n");
		}
		if(this.authentication()) {
			bw.write("					<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
		}
		if(this.search()) {
			for(Column col : masterTable.cols) {
				if(
					this.searchColumn(masterTable, col.name) &&
					this.codeColumn(masterTable, col.name) != null
				) {
					String codeName = this.codeColumn(masterTable, col.name);
					bw.write("					<param name=\"" + col.getLowerName() + "\" datatype=\"varchar\" value=\"param." + col.getLowerName() + "\" default=\"code." + codeName + ".firstValue\" />\n");
				}
			}
			for(Column col : masterTable.cols) {
				if(
					this.searchColumn(masterTable, col.name) &&
					this.codeColumn(masterTable, col.name) == null
				) {
					bw.write("					<param name=\"search\" datatype=\"varchar\" value=\"param.search\" cond=\"${param.search} isNotEmpty\" />\n");
				}
			}
		}
		if(this.authentication() || this.search()) {
			bw.write("				</params>\n");
		}
		bw.write("			</command>\n");
		bw.write("		</commands>\n");
		bw.write("		<layout>\n");
		bw.write("			<top>\n");
		bw.write("				<left>\n");
		bw.write("					<link name=\"insert\" label=\"" + this.getProperty("gen.button.new.label") + "\" path=\"/" + xmlName + "/insert\" />\n");
		bw.write("				</left>\n");
		if(this.search()) {
			bw.write("				<center>\n");
			bw.write("					<search label=\"" + this.getProperty("gen.button.search.label") + "\" path=\"/" + xmlName + "/list\">\n");
			bw.write("						<params>\n");
			index = 0;
			for(Column col : masterTable.cols) {
				if(this.searchColumn(masterTable, col.name)) {
					String codeName = this.codeColumn(masterTable, col.name);
					if(codeName == null) {
						index++;
					} else {
						bw.write("							<param name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"select\" for=\"" + codeName + "\" />\n");
					}
				}
			}
			if(index > 0) {
				bw.write("							<param name=\"search\" value=\"search\" />\n");
			}
			bw.write("						</params>\n");
			bw.write("					</search>\n");
			bw.write("				</center>\n");
		}
		
		bw.write("			</top>\n");
		bw.write("			<middle>\n");
		bw.write("				<tab name=\"" + masterTable.getLowerName() + "\">\n");
		
		for(Column col : masterTable.cols) {
			if(!this.view("list_view", masterTable, col.name)) {
				continue;
			}
			if(this.view("list_link", masterTable, col.name)) {
				bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(masterTable, col.name) + getCodeExprForList(masterTable, col.name) + ">\n");
				bw.write("						<link path=\"/" + xmlName + "/detail\">\n");
				for(Column pcol : masterTable.cols) {
					if(pcol.isPk()) {
						bw.write("							<param name=\"" + pcol.getLowerName() + "\" type=\"query\" value=\"" + pcol.getLowerName() + "\" />\n");
					}
				}
				if(this.page()) {
					bw.write("							<param name=\"page\" type=\"param\" value=\"page\" />\n");
				}
				if(this.search()) {
					index = 0;
					for(Column pcol : masterTable.cols) {
						if(this.searchColumn(masterTable, pcol.name)) {
							String codeName = this.codeColumn(masterTable, pcol.name);
							if(codeName == null) {
								index++;
							} else {
								bw.write("							<param name=\"" + pcol.getLowerName() + "\" type=\"param\" value=\"" + pcol.getLowerName() + "\" />\n");
							}
						}
					}
					if(index > 0) {
						bw.write("							<param name=\"search\" type=\"param\" value=\"search\" />\n");
					}
				}
				bw.write("						</link>\n");
				bw.write("					</column>\n");
			} else {
				bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(masterTable, col.name) + getCodeExprForList(masterTable, col.name) + " />\n");
			}
		}
		bw.write("				</tab>\n");
		bw.write("			</middle>\n");
		bw.write("			<bottom>\n");
		if(this.page()) {
			bw.write("			<center>page</center>\n");
		}
		bw.write("			</bottom>\n");
		bw.write("		</layout>\n");
		bw.write("	</query>\n");
	}
	private void insert(BufferedWriter bw, String xmlName, Hashtable cssAndJs) throws IOException, SQLException {
		Table masterTable = this.getMasterTable();
		int index = 0;
		bw.write("	<query id=\"insert\" funcType=\"insert\" label=\"" + masterTable.getRemarksOrName() + "\">\n");
		StringBuffer validationBuffer = new StringBuffer();
		for (Table tab2 : this.tables()) {
			boolean isMasterTable = isMasterTable(tab2);
			for(Column col : tab2.cols) {
				String validation = this.validation(tab2, col.name, isMasterTable);
				if(validation != null && !validation.trim().equals("")) {
					if(index == 0) {
						validationBuffer.append("		<validation method=\"POST\">\n");
					}
					validationBuffer.append("			" + validation + "\n");
					index++;
				}
			}
		}
		if(index > 0) {
			validationBuffer.append("		</validation>\n");
		}
		StringBuffer calculatorBuffer = new StringBuffer();
		index = 0;
		for (Table tab2 : this.tables()) {
			boolean isMasterTable = isMasterTable(tab2);
			boolean multi = this.multi(tab2);
			for(Column col : tab2.cols) {
				String calculator = this.calculator(tab2, col.name);
				if(calculator != null && !calculator.trim().equals("")) {
					if(index == 0) {
						calculatorBuffer.append("		<calculator>\n");
					}
					String paramName = null;
					if(isMasterTable) {
						paramName = col.name;
					} else {
						paramName = tab2.name + "." + col.name;
					}
					if(multi) {
						paramName += ".{N}";
					}
					calculatorBuffer.append("			<param name=\"" + paramName.toLowerCase() + "\" form=\"insert\" event=\"blur submit\" expr=\"" + calculator + "\" />\n");
					index++;
				}
			}
		}
		if(index > 0) {
			calculatorBuffer.append("		</calculator>\n");
		}
		bw.write("		<header>\n");
		
		if(validationBuffer.length() > 0) {
			if(cssAndJs.containsKey("fn_check.js")) {
				bw.write("			<script name=\"fn_check\" src=\"" + cssAndJs.get("fn_check.js") + "\" override=\"true\" />\n");
				if(cssAndJs.containsKey("fn_check.js.remote")) {
					bw.write("			<!--script name=\"fn_check\" src=\"" + cssAndJs.get("fn_check.js.remote") + "\" override=\"true\" /-->\n");
				}
			} else if(cssAndJs.containsKey("fn_check.js.remote")) {
				bw.write("			<script name=\"fn_check\" src=\"" + cssAndJs.get("fn_check.js.remote") + "\" override=\"true\" />\n");
			}
		}
		if(cssAndJs.containsKey("DateParser.js")) {
			bw.write("			<script name=\"date.parser\" src=\"" + cssAndJs.get("DateParser.js") + "\" override=\"true\" />\n");
			if(cssAndJs.containsKey("DateParser.js.remote")) {
				bw.write("			<!--script name=\"date.parser\" src=\"" + cssAndJs.get("DateParser.js.remote") + "\" override=\"true\" /-->\n");
			}
		} else if(cssAndJs.containsKey("DateParser.js.remote")) {
			bw.write("			<script name=\"date.parser\" src=\"" + cssAndJs.get("DateParser.js.remote") + "\" override=\"true\" />\n");
		}
		if(calculatorBuffer.length() > 0) {
			if(cssAndJs.containsKey("GrahaFormula.js")) {
				bw.write("			<script name=\"graha.formula\" src=\"" + cssAndJs.get("GrahaFormula.js") + "\" override=\"true\" />\n");
				if(cssAndJs.containsKey("GrahaFormula.js.remote")) {
					bw.write("			<!--script name=\"graha.formula\" src=\"" + cssAndJs.get("GrahaFormula.js.remote") + "\" override=\"true\" /-->\n");
				}
			} else if(cssAndJs.containsKey("GrahaFormula.js.remote")) {
				bw.write("			<script name=\"graha.formula\" src=\"" + cssAndJs.get("GrahaFormula.js.remote") + "\" override=\"true\" />\n");
			}
		}
		if(cssAndJs.containsKey("insert.css")) {
			bw.write("			<style src=\"" + cssAndJs.get("insert.css") + "\" />\n");
		}
		bw.write("		</header>\n");
		if(validationBuffer.length() > 0) {
			bw.write(validationBuffer.toString());
		}
		if(calculatorBuffer.length() > 0) {
			bw.write(calculatorBuffer.toString());
		}
		
		bw.write("		<tables>\n");
		bw.write("			<table tableName=\"" + getTName(masterTable) + "\" name=\"" + masterTable.getLowerName() + "\" label=\"" + masterTable.getRemarksOrName() + "\"");
		if(this.fileUpload()) {
			bw.write(">\n");
		} else {
			bw.write(" query_to_param=\"no\">\n");
		}
		
		for(Column col : masterTable.cols) {
			String dataType = this._db.getGrahaDataType(col.dataType);
			if(col.isPk()) {
				if(this._db.supportSequence()) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + this._db.getNextval(this._con, masterTable, col.name, defaultSchema()) + "\" />\n");
				} else {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
				}
			} else if(this._db.isDef(col.getLowerName()) && this._db.isDefOnly(col.getLowerName())) {
				bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + this._db.getDefOnly(col.getLowerName()) + "\" value=\"" + this._db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
			} else if(this._db.isDef(col.getLowerName())) {
				bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + this._db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
			} else if(!this.view("insert_view", masterTable, col.name)) {
				continue;
			} else {
				bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"" + getPattern(masterTable, col.name, dataType) + " />\n");
			}
		}
		if(this.authentication()) {
			bw.write("				<where>\n");
			bw.write("					<sql>\n");
			bw.write("						" + this.getAuthenticationColumnName(masterTable) + " = ?\n");
			bw.write("					</sql>\n");
			bw.write("					<params>\n");
			bw.write("						<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			bw.write("					</params>\n");
			bw.write("				</where>\n");
		}
		bw.write("			</table>\n");
		for (Table tab2 : this.tables()) {
			if(isMasterTable(tab2)) {
				continue;
			}
			bw.write("			<table tableName=\"" + getTName(tab2) + "\" name=\"" + tab2.getLowerName() + "\" label=\"" + tab2.getRemarksOrName() + "\"" + getMultiForInsert(tab2) + " query_to_param=\"no\">\n");
			for(Column col : tab2.cols) {
				String dataType = this._db.getGrahaDataType(col.dataType);
				if(col.isPk()) {
					if(this._db.supportSequence()) {
						bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + tab2.getLowerName() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + this._db.getNextval(this._con, tab2, col.name, defaultSchema()) + "\" />\n");
					} else {
						bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + tab2.getLowerName() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
					}
				} else if(this._db.containsKey(this._con, masterTable, col.name)) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  foreign=\"true\" />\n");
				} else if(this._db.isDef(col.getLowerName()) && this._db.isDefOnly(col.getLowerName())) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + this._db.getDefOnly(col.getLowerName()) + "\" value=\"" + this._db.getDef(col.getLowerName(), "param." + tab2.getLowerName()) + "\" datatype=\"" + dataType + "\" />\n");
				} else if(this._db.isDef(col.getLowerName())) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + this._db.getDef(col.getLowerName(), "param." + tab2.getLowerName()) + "\" datatype=\"" + dataType + "\" />\n");
				} else if(!this.view("insert_view", tab2, col.name)) {
					continue;
				} else {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + tab2.getLowerName() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"" + getPattern(tab2, col.name, dataType) + " />\n");
				}
			}
			if(this.authentication()) {
				bw.write("				<where>\n");
				bw.write("					<sql>\n");
				bw.write("						" + this.getAuthenticationColumnName(tab2) + " = ?\n");
				bw.write("					</sql>\n");
				bw.write("					<params>\n");
				bw.write("						<param name=\"" + this.getAuthenticationColumnName(tab2) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				bw.write("					</params>\n");
				bw.write("				</where>\n");
			}
			bw.write("			</table>\n");
		}

		bw.write("		</tables>\n");
		
		if(this.fileUpload()) {
			bw.write("		<files>\n");
			bw.write("			<file\n");
			bw.write("				name=\"" + xmlName + ".file\"\n");
			bw.write("				path=\"${prop.file.upload.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${query." + masterTable.name + "." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("				append=\"3\"\n");
			bw.write("				backup=\"${prop.file.backup.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${query." + masterTable.name + "." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("			/>\n");
			if(this.authentication()) {
				bw.write("			<auth check=\"${result} > 0\">\n");
				bw.write("				<sql>select count(*) from " + getTName(masterTable) + " where " + this.getAuthenticationColumnName(masterTable) + " = ?");
				for(Column col : masterTable.cols) {
					if(col.isPk()) {
						bw.write(" and " + col.name + " = ?");
					}
				}
				bw.write("</sql>\n");
				bw.write("				<params>\n");
				bw.write("					<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				for(Column col : masterTable.cols) {
					if(col.isPk()) {
						bw.write("					<param name=\"" + col.name + "\" datatype=\"" + this._db.getGrahaDataType(col.dataType) + "\" value=\"param.query." + masterTable.name + "." + col.name + "\" />\n");
					}
				}
				bw.write("				</params>\n");
				bw.write("			</auth>\n");
			}
			bw.write("		</files>\n");
		}
		bw.write("		<layout msg=\"" + this.getProperty("gen.message.save.confirm.msg") + "\">\n");
		bw.write("			<top>\n");
		bw.write("				<left />\n");
		bw.write("				<center />\n");
		bw.write("				<right>\n");
		if(this.page() || this.search()) {
			bw.write("					<link name=\"list\" label=\"" + this.getProperty("gen.button.list.label") + "\" path=\"/" + xmlName + "/list\">\n");
			bw.write("						<params>\n");
			if(this.page()) {
				bw.write("							<param name=\"page\" type=\"param\" value=\"page\" />\n");
			}
			if(this.search()) {
				index = 0;
				for(Column col : masterTable.cols) {
					if(this.searchColumn(masterTable, col.name)) {
						String codeName = this.codeColumn(masterTable, col.name);
						if(codeName == null) {
							index++;
						} else {
							bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
						}
					}
				}
				if(index > 0) {
					bw.write("							<param name=\"search\" type=\"param\" value=\"search\" />\n");
				}
			}
			bw.write("						</params>\n");
			bw.write("					</link>\n");
		} else {
			bw.write("					<link name=\"list\" label=\"" + this.getProperty("gen.button.list.label") + "\" path=\"/" + xmlName + "/list\" />\n");
		}
		bw.write("					<link name=\"save\" label=\"" + this.getProperty("gen.button.save.label") + "\" path=\"/" + xmlName + "/insert\" method=\"post\" type=\"submit\" full=\"true\">\n");
		bw.write("						<params>\n");
		
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		if(this.page()) {
			bw.write("							<param name=\"page\" value=\"page\" />\n");
		}
		if(this.search()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(this.searchColumn(masterTable, col.name)) {
					String codeName = this.codeColumn(masterTable, col.name);
					if(codeName == null) {
						index++;
					} else {
						bw.write("							<param name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" />\n");
					}
				}
			}
			if(index > 0) {
				bw.write("							<param name=\"search\" value=\"search\" />\n");
			}
		}
		bw.write("						</params>\n");
		bw.write("					</link>\n");
		bw.write("				</right>\n");
		bw.write("			</top>\n");
		bw.write("			<middle>\n");
		bw.write("				<tab name=\"" + masterTable.getLowerName() + "\" label=\"" + masterTable.getRemarksOrName() + "\">\n");
		
		for(Column col : masterTable.cols) {
			if(!col.isPk() && !this._db.isDef(col.getLowerName()) && this.view("insert_view", masterTable, col.name)) {
				bw.write("					<row>\n");
				if(col.dataType == java.sql.Types.BOOLEAN) {
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\"" + getReadonlyForInsert(masterTable, col.name) + " />\n");
				} else if(col.typeName != null && col.typeName.equals("text")) {
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\"" + getReadonlyForInsert(masterTable, col.name) + " />\n");
				} else {
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\"" + getFormat(masterTable, col.name) + "" + getReadonlyForInsert(masterTable, col.name) + "" + getCodeExprForInsert(masterTable, col.name) + " />\n");
				}
				bw.write("					</row>\n");
			}
		}
		bw.write("				</tab>\n");
		
		for (Table tab2 : this.tables()) {
			if(isMasterTable(tab2)) {
				continue;
			}
			bw.write("				<tab name=\"" + tab2.getLowerName() + "\" label=\"" + tab2.getRemarksOrName() + "\"" + this.getSingle(tab2) + ">\n");
			for(Column col : tab2.cols) {
				if(col.isPk()) {
					bw.write("						<column name=\"" + tab2.getLowerName() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"hidden\" />\n");
				}
			}
			if(this.multi(tab2)) {
				bw.write("					<row>\n");
			}
			for(Column col : tab2.cols) {
				if(col.isPk()) {
					continue;
				} else if(this._db.containsKey(this._con, masterTable, col.name)) {
					continue;
				} else if(this._db.isDef(col.getLowerName())) {
					continue;
				} else if(!this.view("insert_view", tab2, col.name)) {
					continue;
				} else {
					if(!this.multi(tab2)) {
						bw.write("					<row>\n");
					}
					if(col.dataType == java.sql.Types.BOOLEAN) {
						bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + tab2.getLowerName() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"checkbox\" val=\"t\"" + getReadonlyForInsert(tab2, col.name) + " />\n");
					} else if(col.typeName != null && col.typeName.equals("text")) {
						bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + tab2.getLowerName() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"textarea\"" + getReadonlyForInsert(tab2, col.name) + " />\n");
					} else {
						bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + tab2.getLowerName() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\"" + getFormat(tab2, col.name) + "" + getReadonlyForInsert(tab2, col.name) + "" + getCodeExprForInsert(tab2, col.name) + " />\n");
					}
					if(!this.multi(tab2)) {
						bw.write("					</row>\n");
					}
				}
			}
			if(this.multi(tab2)) {
				bw.write("					</row>\n");
			}
			bw.write("				</tab>\n");
		}
		
		bw.write("			</middle>\n");
		bw.write("			<bottom>\n");
		bw.write("				<right>\n");
		bw.write("				</right>\n");
		bw.write("			</bottom>\n");
		bw.write("		</layout>\n");
		if(this.page() || this.search()) {
			bw.write("		<redirect path=\"/" + xmlName + "/list\">\n");
			if(this.search()) {
				index = 0;
				for(Column col : masterTable.cols) {
					if(this.searchColumn(masterTable, col.name)) {
						String codeName = this.codeColumn(masterTable, col.name);
						if(codeName == null) {
							index++;
						} else {
							bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
						}
					}
				}
				if(index > 0) {
					bw.write("			<param name=\"search\" type=\"param\" value=\"search\" />\n");
				}
			}
			if(this.page()) {
				bw.write("			<param name=\"page\" type=\"param\" value=\"page\" />\n");
			}
			bw.write("		</redirect>\n");
		} else {
			bw.write("		<redirect path=\"/" + xmlName + "/list\" />\n");
		}
		bw.write("		<!--redirect path=\"/" + xmlName + "/detail\">\n");
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				bw.write("			<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		if(this.page()) {
			bw.write("			<param name=\"page\" type=\"param\" value=\"page\" />\n");
		}
		if(this.search()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(this.searchColumn(masterTable, col.name)) {
					String codeName = this.codeColumn(masterTable, col.name);
					if(codeName == null) {
						index++;
					} else {
						bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
					}
				}
			}
			if(index > 0) {
				bw.write("			<param name=\"search\" type=\"param\" value=\"search\" />\n");
			}
		}
		bw.write("		</redirect-->\n");
		bw.write("	</query>\n");
	}
	private void detail(BufferedWriter bw, String xmlName, Hashtable cssAndJs) throws IOException, SQLException {
		Table masterTable = this.getMasterTable();
		int index = 0;
		bw.write("	<query id=\"detail\" funcType=\"detail\" label=\"" + masterTable.getRemarksOrName() + "(${/document/rows/row/title})\">\n");
		bw.write("		<header>\n");
		if(cssAndJs.containsKey("detail.css")) {
			bw.write("			<style src=\"" + cssAndJs.get("detail.css") + "\" />\n");
		}
		bw.write("		</header>\n");
		bw.write("		<commands>\n");
		bw.write("			<command name=\"" + masterTable.getLowerName() + "\"");
		if(this.fileUpload()) {
			bw.write(">\n");
		} else {
			bw.write(" query_to_param=\"no\">\n");
		}
		if(!this._db.supportBultinDateFormatFunction()) {
			index = 0;
			for(Column col : masterTable.cols) {
				String pattern = getDateFormatForJava(masterTable, col.name, this._db.getGrahaDataType(col.dataType));
				if(pattern != null) {
					if(index == 0) {
						bw.write("				<pattern>\n");
					}
					bw.write("					<column name=\"" + col.getLowerName() + "\" pattern=\"" + pattern + "\" />\n");
					index++;
				}
			}
			if(index > 0) {
				bw.write("				</pattern>\n");
			}
		}
		bw.write("				<sql>\n");
		bw.write("					select\n");
		index = 0;
		for(Column col : masterTable.cols) {
				if(!col.isPk() && !this.view("detail_view", masterTable, col.name)) {
					continue;
				}
				bw.write("						");
				if(index > 0) {
					bw.write(", ");
				}
				if(this._db.supportBultinDateFormatFunction()) {
					bw.write(getDateFormatExpression(masterTable, col.name, this._db.getGrahaDataType(col.dataType)));
				} else {
					bw.write(col.name);
				}
				bw.write("\n");
				index++;
		}
		bw.write("					from " + this.getTName(masterTable) + "\n");
		index = 0;
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				if(index > 0) {
					bw.write("						and " + col.name + " = ?\n");
				} else {
					bw.write("					where " + col.name + " = ?\n");
				}
				index++;
			}
		}
		if(this.authentication()) {
			if(index > 0) {
				bw.write("						and " + this.getAuthenticationColumnName(masterTable) + " = ?\n");
			} else {
				bw.write("					where " + this.getAuthenticationColumnName(masterTable) + " = ?\n");
			}
			index++;
		}
		bw.write("				</sql>\n");
		bw.write("				<params>\n");
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				String dataType = this._db.getGrahaDataType(col.dataType);
				bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
			}
		}
		if(this.authentication()) {
			bw.write("					<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
		}
		bw.write("				</params>\n");
		bw.write("			</command>\n");

		for (Table tab2 : this.tables()) {
			if(isMasterTable(tab2)) {
				continue;
			}
			bw.write("			<command name=\"" + tab2.getLowerName() + "\"" + getMultiForDetail(tab2) + " query_to_param=\"no\">\n");
			if(!this._db.supportBultinDateFormatFunction()) {
				index = 0;
				for(Column col : tab2.cols) {
					String pattern = getDateFormatForJava(tab2, col.name, this._db.getGrahaDataType(col.dataType));
					if(pattern != null) {
						if(index == 0) {
							bw.write("				<pattern>\n");
						}
						bw.write("					<column name=\"" + col.getLowerName() + "\" pattern=\"" + pattern + "\" />\n");
						index++;
					}
				}
				if(index > 0) {
					bw.write("				</pattern>\n");
				}
			}
			bw.write("				<sql>\n");
			bw.write("					select\n");
			index = 0;
			
			for(Column col : tab2.cols) {
				if(!col.isPk() && !this.view("detail_view", tab2, col.name)) {
					continue;
				}
				bw.write("						");
				if(index > 0) {
					bw.write(", ");
				}
				if(this._db.supportBultinDateFormatFunction()) {
					bw.write(getDateFormatExpression(tab2, col.name, this._db.getGrahaDataType(col.dataType)));
				} else {
					bw.write(col.name);
				}
				bw.write("\n");
				index++;
			}
			bw.write("					from " + this.getTName(tab2) + "\n");
			index = 0;
			for(Column col : tab2.cols) {
				if(this._db.containsKey(this._con, masterTable, col.name)) {
					if(index > 0) {
						bw.write("						and " + col.name + " = ?\n");
					} else {
						bw.write("					where " + col.name + " = ?\n");
					}
					index++;
				}
			}
			if(this.authentication()) {
				if(index > 0) {
					bw.write("						and " + this.getAuthenticationColumnName(tab2) + " = ?\n");
				} else {
					bw.write("					where " + this.getAuthenticationColumnName(tab2) + " = ?\n");
				}
				index++;
			}
			bw.write("				</sql>\n");
			bw.write("				<params>\n");
			for(Column col : tab2.cols) {
				if(this._db.containsKey(this._con, masterTable, col.name)) {
					String dataType = this._db.getGrahaDataType(col.dataType);
					bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
				}
			}
			if(this.authentication()) {
				bw.write("					<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			}
			bw.write("				</params>\n");
			bw.write("			</command>\n");
		}

		bw.write("		</commands>\n");
		if(this.fileUpload()) {
			bw.write("		<files>\n");
			bw.write("			<file\n");
			bw.write("				name=\"" + xmlName + ".file\"\n");
			bw.write("				path=\"${prop.file.upload.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${query." + masterTable.name + "." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("				backup=\"${prop.file.backup.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${query." + masterTable.name + "." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("			/>\n");
			if(this.authentication()) {
				bw.write("			<auth check=\"${result} > 0\">\n");
				bw.write("				<sql>select count(*) from " + getTName(masterTable) + " where " + this.getAuthenticationColumnName(masterTable) + " = ?");
				for(Column col : masterTable.cols) {
					if(col.isPk()) {
						bw.write(" and " + col.name + " = ?");
					}
				}
				bw.write("</sql>\n");
				bw.write("				<params>\n");
				bw.write("					<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				for(Column col : masterTable.cols) {
					if(col.isPk()) {
						bw.write("					<param name=\"" + col.name + "\" datatype=\"" + this._db.getGrahaDataType(col.dataType) + "\" value=\"param.query." + masterTable.name + "." + col.name + "\" />\n");
					}
				}
				bw.write("				</params>\n");
				bw.write("			</auth>\n");
			}
			bw.write("		</files>\n");
		}
		bw.write("		<layout>\n");
		bw.write("			<top>\n");
		bw.write("				<left />\n");
		bw.write("				<center />\n");
		bw.write("				<right>\n");
		if(this.page() || this.search()) {
			bw.write("					<link name=\"list\" label=\"" + this.getProperty("gen.button.list.label") + "\" path=\"/" + xmlName + "/list\">\n");
			bw.write("						<params>\n");
			if(this.page()) {
				bw.write("							<param name=\"page\" type=\"param\" value=\"page\" />\n");
			}
			if(this.search()) {
				index = 0;
				for(Column col : masterTable.cols) {
					if(this.searchColumn(masterTable, col.name)) {
						String codeName = this.codeColumn(masterTable, col.name);
						if(codeName == null) {
							index++;
						} else {
							bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
						}
					}
				}
				if(index > 0) {
					bw.write("							<param name=\"search\" type=\"param\" value=\"search\" />\n");
				}
			}
			bw.write("						</params>\n");
			bw.write("					</link>\n");
		} else {
			bw.write("					<link name=\"list\" label=\"" + this.getProperty("gen.button.list.label") + "\" path=\"/" + xmlName + "/list\" />\n");
		}
		bw.write("					<link name=\"update\" label=\"" + this.getProperty("gen.button.update.label") + "\" path=\"/" + xmlName + "/insert\">\n");
		bw.write("						<params>\n");
		
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		if(this.page()) {
			bw.write("							<param name=\"page\" type=\"param\" value=\"page\" />\n");
		}
		if(this.search()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(this.searchColumn(masterTable, col.name)) {
					String codeName = this.codeColumn(masterTable, col.name);
					if(codeName == null) {
						index++;
					} else {
						bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
					}
				}
			}
			if(index > 0) {
				bw.write("							<param name=\"search\" type=\"param\" value=\"search\" />\n");
			}
		}
		bw.write("						</params>\n");
		bw.write("					</link>\n");
		bw.write("				</right>\n");
		bw.write("			</top>\n");
		bw.write("			<middle>\n");
		bw.write("				<tab name=\"" + masterTable.getLowerName() + "\" label=\"" + masterTable.getRemarksOrName() + "\">\n");
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				continue;
			} else if(!this.view("detail_view", masterTable, col.name)) {
				continue;
			} else {
				bw.write("					<row>\n");
				bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(masterTable, col.name) + "" + getCodeExprForList(masterTable, col.name) + " />\n");
				bw.write("					</row>\n");
			}
		}
		
		bw.write("				</tab>\n");
		
		for (Table tab2 : this.tables()) {
			if(isMasterTable(tab2)) {
				continue;
			}
			bw.write("				<tab name=\"" + tab2.getLowerName() + "\" label=\"" + tab2.getRemarksOrName() + "\"" + this.getSingle(tab2) + ">\n");
			if(this.multi(tab2)) {
				bw.write("					<row>\n");
			}
			for(Column col : tab2.cols) {
				if(col.isPk()) {
					continue;
				} else if(this._db.containsKey(this._con, masterTable, col.name)) {
					continue;
				} else if(!this.view("detail_view", tab2, col.name)) {
					continue;
				} else {
					if(!this.multi(tab2)) {
						bw.write("					<row>\n");
					}
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\"" + getFormat(tab2, col.name) + "" + getCodeExprForList(tab2, col.name) + " />\n");
					if(!this.multi(tab2)) {
						bw.write("					</row>\n");
					}
				}
				
			}
			if(this.multi(tab2)) {
				bw.write("					</row>\n");
			}
			bw.write("				</tab>\n");
		}

		bw.write("			</middle>\n");
		bw.write("			<bottom>\n");
		bw.write("				<left>\n");
		bw.write("					<link label=\"" + this.getProperty("gen.button.del.label") + "\" path=\"/" + xmlName + "/delete\" method=\"post\" type=\"submit\" msg=\"" + this.getProperty("gen.message.del.confirm.msg") + "\">\n");
		bw.write("						<params>\n");
		
		for(Column col : masterTable.cols) {
			if(col.isPk()) {
				bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
			}
		}
		if(this.page()) {
			bw.write("							<param name=\"page\" type=\"param\" value=\"page\" />\n");
		}
		if(this.search()) {
			index = 0;
			for(Column col : masterTable.cols) {
				if(this.searchColumn(masterTable, col.name)) {
					String codeName = this.codeColumn(masterTable, col.name);
					if(codeName == null) {
						index++;
					} else {
						bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"param\" value=\"" + col.getLowerName() + "\" />\n");
					}
				}
			}
			if(index > 0) {
				bw.write("							<param name=\"search\" type=\"param\" value=\"search\" />\n");
			}
		}
		bw.write("						</params>\n");
		bw.write("					</link>\n");
		bw.write("				</left>\n");
		bw.write("			</bottom>\n");
		bw.write("			\n");
		bw.write("		</layout>\n");
		bw.write("	</query>\n");
	}
	private void delete(BufferedWriter bw, String xmlName) throws IOException, SQLException {
		Table masterTable = this.getMasterTable();
		bw.write("	<query id=\"delete\" funcType=\"delete\" label=\"" + masterTable.getRemarksOrName() + "\">\n");
		bw.write("		<tables>\n");
		bw.write("			<table tableName=\"" + getTName(masterTable) + "\" name=\"" + masterTable.getLowerName() + "\">\n");
		
		for(Column col : masterTable.cols) {
			String dataType = this._db.getGrahaDataType(col.dataType);
			if(col.isPk()) {
				bw.write("				<column name=\"" + col.getLowerName() + "\" primary=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
			}
		}
		if(this.authentication()) {
			bw.write("				<where>\n");
			bw.write("					<sql>\n");
			bw.write("						" + this.getAuthenticationColumnName(masterTable) + " = ?\n");
			bw.write("					</sql>\n");
			bw.write("					<params>\n");
			bw.write("						<param name=\"" + this.getAuthenticationColumnName(masterTable) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
			bw.write("					</params>\n");
			bw.write("				</where>\n");
		}
		bw.write("			</table>\n");
		
		for (Table tab2 : this.tables()) {
			if(isMasterTable(tab2)) {
				continue;
			}
			bw.write("			<table tableName=\"" + getTName(tab2) + "\" name=\"" + tab2.getLowerName() + "\">\n");
			
			for(Column col : tab2.cols) {
				String dataType = this._db.getGrahaDataType(col.dataType);
				if(this._db.containsKey(this._con, masterTable, col.name)) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" foreign=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
				}
			}
			if(this.authentication()) {
				bw.write("				<where>\n");
				bw.write("					<sql>\n");
				bw.write("						" + this.getAuthenticationColumnName(tab2) + " = ?\n");
				bw.write("					</sql>\n");
				bw.write("					<params>\n");
				bw.write("						<param name=\"" + this.getAuthenticationColumnName(tab2) + "\" datatype=\"varchar\" value=\"prop.logined_user\" />\n");
				bw.write("					</params>\n");
				bw.write("				</where>\n");
			}
			bw.write("			</table>\n");
		}

		bw.write("		</tables>\n");
		if(this.fileUpload()) {
			bw.write("		<files>\n");
			bw.write("			<file\n");
			bw.write("				name=\"" + xmlName + ".file\"\n");
			bw.write("				path=\"${prop.file.upload.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${param." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("				backup=\"${prop.file.backup.directory}/" + xmlName + "");
			for(Column col : masterTable.cols) {
				if(col.isPk()) {
					bw.write("/${param." + col.name + "}");
				}
			}
			bw.write("\"\n");
			bw.write("			/>\n");
			bw.write("		</files>\n");
		}
		bw.write("		<redirect path=\"/" + xmlName + "/list\" />\n");
		bw.write("	</query>\n");
	}
}

