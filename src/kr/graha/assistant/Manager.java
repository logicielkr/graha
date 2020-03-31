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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import kr.graha.lib.LogHelper;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Graha(그라하) XML 자동생성기

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Manager extends HttpServlet {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String[] TYPES = {"TABLE", "VIEW"};
	public Manager() {
		LogHelper.setLogLevel(logger);
	}
	
	protected void setCharacterEncoding(HttpServletRequest request) throws ServletException, IOException {
		if(
			getServletContext().getMajorVersion() < 3
			|| (
				getServletContext().getMajorVersion() == 3
				&& getServletContext().getMinorVersion() == 0
			)
		) {
			request.setCharacterEncoding("ISO-8859-1");
		} else {
			request.setCharacterEncoding("UTF-8");
		}
	}
	protected String value(String value) {
		if(value == null) {
			return "";
		} else if(
			getServletContext().getMajorVersion() < 3
			|| (
				getServletContext().getMajorVersion() == 3
				&& getServletContext().getMinorVersion() == 0
			)
		) {
			return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		} else {
			return value;
		}
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.setCharacterEncoding(request);
		String path = request.getPathInfo().trim();
		if(
			path != null 
			&& !path.equals("/list")
			&& !path.equals("/table")
			&& !path.equals("/data")
			&& !path.equals("/xsl")
			&& !path.equals("/query")
		) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found path : " + path);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if(path != null && path.equals("/list")) {
			list(request, response);
		} else if(path != null && path.equals("/table")) {
			table(request, response);
		} else if(path != null && path.equals("/data")) {
			data(request, response);
		} else if(path != null && path.equals("/xsl")) {
			xsl(request, response);
		} else if(path != null && path.equals("/query")) {
			query(request, response);
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.setCharacterEncoding(request);
		String path = request.getPathInfo().trim();
		if(
			path != null
			&& !path.equals("/list")
			&& !path.equals("/gen")
			&& !path.equals("/select")
			&& !path.equals("/table")
			&& !path.equals("/query")
		) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found path : " + path);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if(path != null && path.equals("/list")) {
			_list(request, response);
		} else if(path != null && path.equals("/gen")) {
			_gen(request, response);
		} else if(path != null && path.equals("/select")) {
			_select(request, response);
		} else if(path != null && path.equals("/table")) {
			_table(request, response);
		} else if(path != null && path.equals("/query")) {
			query(request, response);
		}
	}
	protected void xsl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml; charset=UTF-8");
		java.io.InputStream in = null;
		ServletOutputStream out = null;
		try {
			in = this.getClass().getResourceAsStream("/kr/graha/assistant/xsl/" + value(request.getParameter("xsl")) + ".xsl");
			if(in == null) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe("not found xsl file : " + value(request.getParameter("xsl")));
				}
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			out = response.getOutputStream();
			byte[] buffer = new byte[8192];
			int len = 0;
			while((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);
			}
		} finally {
			in.close();
			out.flush();
			out.close();
		}
		
	}
	protected void _list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
		sb.append("<document />");
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			java.util.Enumeration<String> e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				String remarks = value(request.getParameter(key));
				remarks = remarks.replace("'", "''");
				if(key != null && key.startsWith("remarks.")) {
					String name = key.substring(8);
					if(name.indexOf(".") > 0) {
						db.updateTableRemarks(con, name.substring(0, name.indexOf(".")), name.substring(name.indexOf(".") + 1), remarks);
					} else {
						db.updateTableRemarks(con, null, name, remarks);
					}
				}
			}
			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	protected void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		StringBuffer sb = new StringBuffer();
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DatabaseMetaData m = con.getMetaData();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			java.util.List<Table> tabs = db.getTables(con);
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=list\" ?>");
			sb.append("<document>");
			sb.append("<rows id=\"tables\">");
			for(Table tab : tabs){
				sb.append("<row>");
				if(tab.schema != null) {
					sb.append("<table_schem>");
					sb.append(tab.schema);
					sb.append("</table_schem>");
				}
				sb.append("<table_name>");
				sb.append(tab.name);
				sb.append("</table_name>");
				sb.append("<table_type>");
				sb.append(tab.type);
				sb.append("</table_type>");
				if(tab.isNotEmptyRemarks()) {
					sb.append("<remarks>");
					sb.append(tab.remarks);
					sb.append("</remarks>");
				}
				sb.append("</row>");
			}
			sb.append("</rows>");
			sb.append("<params><param>");
			sb.append("<name>" + m.getDatabaseProductName() + "</name>");
			sb.append("<version>" + m.getDatabaseProductVersion() + "</version>");
			if(m.getUserName() != null) {
				sb.append("<user>" + m.getUserName() + "</user>");
			}
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			sb.append("</param></params>");
			sb.append("</document>");

			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	protected void table(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String table = value(request.getParameter("table"));
		String schemaName = null;
		String tableName = null;
		if(table.indexOf(".") > 0) {
			schemaName = table.substring(0, table.indexOf("."));
			tableName = table.substring(table.indexOf(".") + 1);
		} else {
			tableName = table;
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			DatabaseMetaData m = con.getMetaData();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=table\" ?>");
			sb.append("<document>");
			
			java.util.List<Table> tabs = db.getTables(con);
			sb.append("<rows id=\"tables\">");
			for(Table tab : tabs){
				sb.append("<row>");
				if(tab.schema != null) {
					sb.append("<table_schem>");
					sb.append(tab.schema);
					sb.append("</table_schem>");
				}
				sb.append("<table_name>");
				sb.append(tab.name);
				sb.append("</table_name>");
				sb.append("<table_type>");
				sb.append(tab.type);
				sb.append("</table_type>");
				if(tab.isNotEmptyRemarks()) {
					sb.append("<remarks>");
					sb.append(tab.remarks);
					sb.append("</remarks>");
				}
				sb.append("</row>");
			}
			sb.append("</rows>");
			
			if(tableName != null && !tableName.trim().equals("")) {
				sb.append("<rows id=\"columns\">");
				java.util.List<Column> cols = db.getColumns(con, schemaName, tableName);
				for(Column col : cols){
					sb.append("<row>");
					sb.append("<column_name>");
					sb.append(col.name);
					sb.append("</column_name>");
					sb.append("<data_type>");
					sb.append(col.typeName);
					sb.append("</data_type>");
					sb.append("<is_pk>");
					sb.append(col.isPk);
					sb.append("</is_pk>");
					sb.append("<is_nullable>");
					sb.append(col.isNullable);
					sb.append("</is_nullable>");
					sb.append("<is_autoincrement>");
					sb.append(col.isAutoincrement);
					sb.append("</is_autoincrement>");
					if(col.isNotEmptyRemarks()) {
						sb.append("<remarks>");
						sb.append(col.remarks);
						sb.append("</remarks>");
					}
					sb.append("</row>");
				}
				sb.append("</rows>");
			}
			sb.append("<params><param>");
			if(tableName != null && !tableName.trim().equals("")) {
				sb.append("<table>" + tableName + "</table>");
			}
			if(schemaName != null && !schemaName.trim().equals("")) {
				sb.append("<table_schem>" + schemaName + "</table_schem>");
			}
			
			sb.append("<name>" + m.getDatabaseProductName() + "</name>");
			sb.append("<version>" + m.getDatabaseProductVersion() + "</version>");
			if(m.getUserName() != null) {
				sb.append("<user>" + m.getUserName() + "</user>");
			}
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			
			sb.append("</param></params>");
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	protected void _table(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection con = null;
		
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
		sb.append("<document>");
		sb.append("<params><param type=\"r\"><table>" + value(request.getParameter("table")) + "</table></param></params>");
		sb.append("</document>");
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			java.util.Enumeration<String> e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				String remarks = value(request.getParameter(key));
				remarks = remarks.replace("'", "''");
				if(key != null && key.startsWith("remarks.")) {
					String name = key.substring(8);
					if(name.indexOf(".") > 0) {
						if(name.lastIndexOf(".") > name.indexOf(".")) {
							db.updateColumnRemarks(
								con, 
								name.substring(0, name.indexOf(".")), 
								name.substring(name.indexOf(".") + 1, name.lastIndexOf(".")), 
								name.substring(name.lastIndexOf(".") + 1), 
								remarks
							);
						} else {
							db.updateColumnRemarks(
								con, 
								null, 
								name.substring(0, name.indexOf(".")), 
								name.substring(name.lastIndexOf(".") + 1), 
								remarks
							); 
						}
					} else {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe("parameter is not allow");
						}
						
					}
				}
			}
			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
			
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	public void _select(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		request.setCharacterEncoding(charset);
		java.util.Enumeration<String> e = request.getParameterNames();
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=select\" ?>");
		sb.append("<document><params>");
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			if(key != null && key.startsWith("check.") && value(request.getParameter(key)) != null && value(request.getParameter(key)).equals("on")) {
				if(logger.isLoggable(Level.FINEST)) {
					logger.finest("" + key + " : " + value(request.getParameter(key)));
					logger.finest("" + key.substring(6) + " : " + value(request.getParameter(key)));
				}
				sb.append("<table name=\"" + key.substring(6) + "\" />");
			}
		}
		sb.append("</params></document>");
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	public String getProperty(Properties messges, String key) {
		String value = messges.getProperty(key);
		if(value != null) {
			try {
				return new String(value.getBytes("iso-8859-1"), "UTF-8");
			} catch (java.io.UnsupportedEncodingException e) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(e));
				}
			}
		}
		return null;
	}
	public void _gen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tab = value(request.getParameter("table"));
		String schemaName = null;
		String tableName = null;
		if(tab.indexOf(".") > 0) {
			schemaName = tab.substring(0, tab.indexOf("."));
			tableName = tab.substring(tab.indexOf(".") + 1);
		} else {
			tableName = tab;
		}
		
		String[] tables = request.getParameterValues("tables");
		if(tableName == null || tableName.trim().equals("")) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("table name is null or blank");
			}
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		Properties messges = new Properties();
		if(c.getInitParameter("resource") == null) {
			messges.load(getClass().getResourceAsStream("resource/ko.properties"));
		} else {
			messges.load(getClass().getResourceAsStream(c.getInitParameter("resource")));
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
		sb.append("<document>");
		sb.append("<params><param type=\"s\"><path>list</path></param></params>");
		sb.append("</document>");
		
		java.io.File f = null;
		int index = 0;
		String filePath = this.getServletContext().getRealPath("/WEB-INF/graha/");
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(filePath);
		}
		String xmlName = null;
		while(true) {
			if(index == 0) {
				f = new java.io.File(filePath + java.io.File.separator + tableName.toLowerCase() + ".xml");
				xmlName = tableName.toLowerCase();
			} else {
				f = new java.io.File(filePath + java.io.File.separator + tableName.toLowerCase() + "-" + index + ".xml");
				xmlName = tableName.toLowerCase() + "-" + index;
			}
			if(!f.exists()) {
				break;
			}
			index++;
		}
		BufferedWriter bw = null;
		Connection con = null;

		String tableComments = null;
		java.util.List<Column> cols = null;
		java.util.List<Table> tabs = null;
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			String jndi = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
				jndi = source.substring("java:/comp/env/".length());
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
				jndi = c.getInitParameter("jndi");
			}
			if(logger.isLoggable(Level.FINEST)) {
				logger.finest(jndi);
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			DatabaseMetaData m = con.getMetaData();
			
			tableComments = db.getTabRemarks(con, schemaName, tableName);
			
			bw = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(f, true), java.nio.charset.StandardCharsets.UTF_8));

			bw.write("<querys>\n");
			bw.write("	<header extends=\"_base.xml\">\n");
			bw.write("		<jndi name=\"" + jndi + "\" />\n");
			bw.write("	</header>\n");
			bw.write("	<query id=\"list\" funcType=\"list\" label=\"" + tableComments + "\">\n");
			bw.write("		<header>\n");
			bw.write("		</header>\n");
			bw.write("		<commands>\n");
			bw.write("			<command name=\"" + tableName.toLowerCase() + "\">\n");
			bw.write("				<sql pageSize=\"15\" pageGroupSize=\"10\">\n");
			bw.write("					select\n");
			index = 0;
			
			cols = db.getColumns(con, schemaName, tableName);
			for(Column col : cols){
				if(db.isDef(col.getLowerName())) {
					continue;
				}
				if(index > 0) {
					bw.write("						, " + col.name + "\n");
				} else {
					bw.write("						" + col.name + "\n");
				}
				index++;
			}

			bw.write("					from " + tableName + "\n");
			bw.write("				</sql>\n");
			bw.write("				<sql_cnt>\n");
			bw.write("					select count(*) from " + tableName + "\n");
if(m.getDatabaseProductName().equalsIgnoreCase("PostgreSQL")) {
			bw.write("/*\n");
			bw.write("					SELECT n_live_tup\n");
			bw.write("					FROM pg_stat_all_tables\n");
			bw.write("					WHERE relname = '" + tableName + "'\n");
			bw.write("*/\n");
			bw.write("/*\n");
			bw.write("					SELECT reltuples\n");
			bw.write("					FROM pg_class\n");
			bw.write("					WHERE relname = '" + tableName + "'\n");
			bw.write("*/\n");
}
			bw.write("				</sql_cnt>\n");
			bw.write("			</command>\n");
			bw.write("		</commands>\n");
			bw.write("		<layout>\n");
			bw.write("			<top>\n");
			bw.write("				<left>\n");
			bw.write("					<link name=\"insert\" label=\"" + this.getProperty(messges, "button.new.label") + "\" path=\"/" + xmlName + "/insert\" />\n");
			bw.write("				</left>\n");
			bw.write("			</top>\n");
			bw.write("			<middle>\n");
			bw.write("				<tab name=\"" + tableName.toLowerCase() + "\">\n");
			
			for(Column col : cols){
				if(col.isPk()) {
					bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\">\n");
					bw.write("						<link path=\"/" + xmlName + "/detail\">\n");
					for(Column pcol : cols){
						if(pcol.isPk()) {
							bw.write("							<param name=\"" + pcol.getLowerName() + "\" type=\"query\" value=\"" + pcol.getLowerName() + "\" />\n");
						}
					}
					bw.write("						</link>\n");
					bw.write("					</column>\n");
				} else if(db.isDef(col.getLowerName())) {
					continue;
				} else {
					bw.write("					<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
				}
			}
			bw.write("				</tab>\n");
			bw.write("			</middle>\n");
			bw.write("			<bottom>\n");
			bw.write("			<center>page</center>\n");
			bw.write("			</bottom>\n");
			bw.write("		</layout>\n");
			bw.write("	</query>\n");

			bw.write("	<query id=\"insert\" funcType=\"insert\" label=\"" + tableComments + "\">\n");
			bw.write("		<header>\n");
			bw.write("		</header>\n");
			bw.write("		<tables>\n");
			bw.write("			<table tableName=\"" + tableName + "\" name=\"" + tableName.toLowerCase() + "\" label=\"" + tableComments + "\">\n");
			for(Column col : cols){
				String dataType = db.getGrahaDataType(col.dataType);
				if(col.isPk()) {
					if(db.supportSequence()) {
						bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + db.getNextval(con, tableName, col.name) + "\" />\n");
					} else {
						bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
					}
				} else if(db.isDef(col.getLowerName()) && db.isDefOnly(col.getLowerName())) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + db.getDefOnly(col.getLowerName()) + "\" value=\"" + db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
				} else if(db.isDef(col.getLowerName())) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + db.getDef(col.getLowerName(), "param.") + "\" datatype=\"" + dataType + "\" />\n");
				} else {
					bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
				}
			}
			bw.write("			</table>\n");
			if(tables != null && tables.length > 1) {
				for (String tab2 : tables) {
					String tab1 = value(tab2);
					String schema = tab1.substring(0, tab1.indexOf("."));
					String table = tab1.substring(tab1.indexOf(".") + 1);
					if(table.equals(tableName)) {
						continue;
					}
					String comments = db.getTabRemarks(con, schema, table);
					bw.write("			<table tableName=\"" + table + "\" name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\"  multi=\"true\" append=\"3\">\n");
					
					cols = db.getColumns(con, schema, table);
					for(Column col : cols){
						String dataType = db.getGrahaDataType(col.dataType);
						if(col.isPk()) {
							if(db.supportSequence()) {
								bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"sequence." + db.getNextval(con, table, col.name) + "\" />\n");
							} else {
								bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  primary=\"true\"  insert=\"generate\" />\n");
							}
						} else if(db.containsKey(con, schemaName, tableName, col.name)) {
							bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\"  foreign=\"true\" />\n");
						} else if(db.isDef(col.getLowerName()) && db.isDefOnly(col.getLowerName())) {
							bw.write("				<column name=\"" + col.getLowerName() + "\" only=\"" + db.getDefOnly(col.getLowerName()) + "\" value=\"" + db.getDef(col.getLowerName(), "param." + table) + "\" datatype=\"" + dataType + "\" />\n");
						} else if(db.isDef(col.getLowerName())) {
							bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"" + db.getDef(col.getLowerName(), "param." + table) + "\" datatype=\"" + dataType + "\" />\n");
						} else {
							bw.write("				<column name=\"" + col.getLowerName() + "\" value=\"param." + table.toLowerCase() + "." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
						}
					}
					bw.write("			</table>\n");
				}
			}
			bw.write("		</tables>\n");
			bw.write("		<layout msg=\"" + this.getProperty(messges, "message.save.confirm.msg") + "\">\n");
			bw.write("			<top>\n");
			bw.write("				<left />\n");
			bw.write("				<center />\n");
			bw.write("				<right>\n");
			bw.write("					<link name=\"list\" label=\"" + this.getProperty(messges, "button.list.label") + "\" path=\"/" + xmlName + "/list\" />\n");
			bw.write("					<link name=\"save\" label=\"" + this.getProperty(messges, "button.save.label") + "\" path=\"/" + xmlName + "/insert\" method=\"post\" type=\"submit\" full=\"true\">\n");
			bw.write("						<params>\n");
			cols = db.getColumns(con, schemaName, tableName);
			for(Column col : cols){
				if(col.isPk()) {
					bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
				}
			}
			bw.write("						</params>\n");
			bw.write("					</link>\n");
			bw.write("				</right>\n");
			bw.write("			</top>\n");
			bw.write("			<middle>\n");
			bw.write("				<tab name=\"" + tableName.toLowerCase() + "\" label=\"" + tableComments + "\">\n");
			
			for(Column col : cols){
				if(!col.isPk() && !db.isDef(col.getLowerName())) {
					bw.write("					<row>\n");
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" />\n");
					bw.write("					</row>\n");
				}
			}
			bw.write("				</tab>\n");
			
			if(tables != null && tables.length > 1) {
				for (String tab2 : tables) {
					String tab1 = value(tab2);
					String schema = tab1.substring(0, tab1.indexOf("."));
					String table = tab1.substring(tab1.indexOf(".") + 1);
					if(table != null && table.equals(tableName)) {
						continue;
					}
					String comments = db.getTabRemarks(con, schema, table);
					
					bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\">\n");
					cols = db.getColumns(con, schema, table);
					for(Column col : cols){
						if(col.isPk()) {
							bw.write("						<column name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" type=\"hidden\" />\n");
						}
					}
					bw.write("					<row>\n");
					for(Column col : cols){
						if(col.isPk()) {
							continue;
						} else if(db.containsKey(con, schemaName, tableName, col.name)) {
							continue;
						} else if(db.isDef(col.getLowerName())) {
							continue;
						} else {
							bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + table.toLowerCase() + "." +  col.getLowerName() + "\" value=\"" + col.getLowerName() + "\" />\n");
						}
					}
					bw.write("					</row>\n");
					bw.write("				</tab>\n");
				}
			}
			
			bw.write("			</middle>\n");
			bw.write("			<bottom>\n");
			bw.write("				<right>\n");
			bw.write("				</right>\n");
			bw.write("			</bottom>\n");
			bw.write("		</layout>\n");
			bw.write("		<redirect path=\"/" + xmlName + "/list\" />\n");
			bw.write("	</query>\n");

			bw.write("	<query id=\"detail\" funcType=\"detail\" label=\"" + tableComments + "(${/document/rows/row/title})\">\n");
			bw.write("		<header>\n");
			bw.write("		</header>\n");
			bw.write("		<commands>\n");
			bw.write("			<command name=\"" + tableName.toLowerCase() + "\">\n");
			bw.write("				<sql>\n");
			bw.write("					select\n");
			index = 0;
			cols = db.getColumns(con, schemaName, tableName);
			for(Column col : cols){
					if(db.isDef(col.getLowerName())) {
						continue;
					}
					if(index > 0) {
						bw.write("						, " + col.name + "\n");
					} else {
						bw.write("						" + col.name + "\n");
					}
					index++;
			}
			bw.write("					from " + tableName + "\n");
			index = 0;
			for(Column col : cols){
				if(col.isPk()) {
					if(index > 0) {
						bw.write("						and " + col.name + " = ?\n");
					} else {
						bw.write("						where " + col.name + " = ?\n");
					}
					index++;
				}
			}
			bw.write("				</sql>\n");
			bw.write("				<params>\n");
			for(Column col : cols){
				if(col.isPk()) {
					String dataType = db.getGrahaDataType(col.dataType);
					bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
				}
			}
			bw.write("				</params>\n");
			bw.write("			</command>\n");
			if(tables != null && tables.length > 1) {
				for (String tab2 : tables) {
					String tab1 = value(tab2);
					String schema = tab1.substring(0, tab1.indexOf("."));
					String table = tab1.substring(tab1.indexOf(".") + 1);
					if(table != null && table.equals(tableName)) {
						continue;
					}
					bw.write("			<command name=\"" + table.toLowerCase() + "\"  multi=\"true\">\n");
					bw.write("				<sql>\n");
					bw.write("					select\n");
					index = 0;
					cols = db.getColumns(con, schema, table);
					for(Column col : cols){
							if(db.isDef(col.getLowerName())) {
								continue;
							}
							if(index > 0) {
								bw.write("						, " + col.name + "\n");
							} else {
								bw.write("						" + col.name + "\n");
							}
							index++;
					}
					bw.write("					from " + table + "\n");
					index = 0;
					for(Column col : cols){
						if(db.containsKey(con, schemaName, tableName, col.name)) {
							if(index > 0) {
								bw.write("						and " + col.name + " = ?\n");
							} else {
								bw.write("						where " + col.name + " = ?\n");
							}
							index++;
						}
					}
					bw.write("				</sql>\n");
					bw.write("				<params>\n");
					for(Column col : cols){
						if(db.containsKey(con, schemaName, tableName, col.name)) {
							String dataType = db.getGrahaDataType(col.dataType);
							bw.write("					<param default=\"null\" name=\"" + col.getLowerName() + "\" datatype=\"" + dataType + "\" value=\"param." + col.getLowerName() + "\" />\n");
						}
					}
					bw.write("				</params>\n");
					bw.write("			</command>\n");
				}
			}
			bw.write("		</commands>\n");
			bw.write("		<layout>\n");
			bw.write("			<top>\n");
			bw.write("				<left />\n");
			bw.write("				<center />\n");
			bw.write("				<right>\n");
			bw.write("					<link name=\"list\" label=\"" + this.getProperty(messges, "button.list.label") + "\" path=\"/" + xmlName + "/list\" />\n");
			bw.write("					<link name=\"update\" label=\"" + this.getProperty(messges, "button.update.label") + "\" path=\"/" + xmlName + "/insert\">\n");
			bw.write("						<params>\n");
			cols = db.getColumns(con, schemaName, tableName);
			for(Column col : cols){
				if(col.isPk()) {
					bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
				}
			}
			
			bw.write("						</params>\n");
			bw.write("					</link>\n");
			bw.write("				</right>\n");
			bw.write("			</top>\n");
			bw.write("			<middle>\n");
			bw.write("				<tab name=\"" + tableName.toLowerCase() + "\" label=\"" + tableComments + "\">\n");
			for(Column col : cols){
				if(col.isPk()) {
					continue;
				} else if(db.isDef(col.getLowerName())) {
						continue;
				} else {
					bw.write("					<row>\n");
					bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
					bw.write("					</row>\n");
				}
			}
			
			bw.write("				</tab>\n");
			if(tables != null && tables.length > 1) {
				for (String tab2 : tables) {
					String tab1 = value(tab2);
					String schema = tab1.substring(0, tab1.indexOf("."));
					String table = tab1.substring(tab1.indexOf(".") + 1);
					if(table != null && table.equals(tableName)) {
						continue;
					}
					String comments = db.getTabRemarks(con, schema, table);
					bw.write("				<tab name=\"" + table.toLowerCase() + "\" label=\"" + comments + "\">\n");
					bw.write("					<row>\n");
					cols = db.getColumns(con, schema, table);
					for(Column col : cols){
						if(col.isPk()) {
							continue;
						} else if(db.containsKey(con, schemaName, tableName, col.name)) {
							continue;
						} else if(db.isDef(col.getLowerName())) {
							continue;
						} else {
							bw.write("						<column label=\"" + col.getRemarksOrName() + "\" name=\"" + col.getLowerName() + "\" />\n");
						}
						
					}
					bw.write("					</row>\n");
					bw.write("				</tab>\n");
				}
			}
			bw.write("			</middle>\n");
			bw.write("			<bottom>\n");
			bw.write("				<left>\n");
			bw.write("					<link label=\"" + this.getProperty(messges, "button.del.label") + "\" path=\"/" + xmlName + "/delete\" method=\"post\" type=\"submit\" msg=\"" + this.getProperty(messges, "message.del.confirm.msg") + "\">\n");
			bw.write("						<params>\n");
			cols = db.getColumns(con, schemaName, tableName);
			for(Column col : cols){
				if(col.isPk()) {
					bw.write("							<param name=\"" + col.getLowerName() + "\" type=\"query\" value=\"" + col.getLowerName() + "\" />\n");
				}
			}
			bw.write("						</params>\n");
			bw.write("					</link>\n");
			bw.write("				</left>\n");
			bw.write("			</bottom>\n");
			bw.write("			\n");
			bw.write("		</layout>\n");
			bw.write("	</query>\n");


			bw.write("	<query id=\"delete\" funcType=\"delete\" label=\"" + tableComments + "\">\n");
			bw.write("		<tables>\n");
			bw.write("			<table tableName=\"" + tableName + "\" name=\"" + tableName.toLowerCase() + "\">\n");
			for(Column col : cols){
				String dataType = db.getGrahaDataType(col.dataType);
				if(col.isPk()) {
					bw.write("				<column name=\"" + col.getLowerName() + "\" primary=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
				}
			}
			bw.write("			</table>\n");
			if(tables != null && tables.length > 1) {
				for (String tab2 : tables) {
					String tab1 = value(tab2);
					String schema = tab1.substring(0, tab1.indexOf("."));
					String table = tab1.substring(tab1.indexOf(".") + 1);
					if(table != null && table.equals(tableName)) {
						continue;
					}
					bw.write("			<table tableName=\"" + table + "\" name=\"" + table.toLowerCase() + "\">\n");
					cols = db.getColumns(con, schema, table);
					for(Column col : cols){
						String dataType = db.getGrahaDataType(col.dataType);
						if(db.containsKey(con, schemaName, tableName, col.name)) {
							bw.write("				<column name=\"" + col.getLowerName() + "\" foreign=\"true\" value=\"param." + col.getLowerName() + "\" datatype=\"" + dataType + "\" />\n");
						}
					}
					bw.write("			</table>\n");
				}
			}
			bw.write("		</tables>\n");
			bw.write("		<redirect path=\"/" + xmlName + "/list\" />\n");
			bw.write("	</query>\n");
			bw.write("</querys>\n");
			bw.flush();

			con.close();
			con = null;
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		} finally {
			try {
				if(con != null) {
					con.close();
				}
			} catch (SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(e));
				}
			}
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(e));
				}
			}
			
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	
	protected void data(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String table = value(request.getParameter("table"));
		String schemaName = null;
		String tableName = null;
		if(table.indexOf(".") > 0) {
			schemaName = table.substring(0, table.indexOf("."));
			tableName = table.substring(table.indexOf(".") + 1);
		} else {
			tableName = table;
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, c.getInitParameter("def"), c.getInitParameter("mapping"));
			DatabaseMetaData m = con.getMetaData();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=data\" ?>");
			sb.append("<document>");
			java.util.List<Table> tabs = db.getTables(con);
			sb.append("<rows id=\"tables\">");
			for(Table tab : tabs){
				sb.append("<row>");
				if(tab.schema != null) {
					sb.append("<table_schem>");
					sb.append(tab.schema);
					sb.append("</table_schem>");
				}
				sb.append("<table_name>");
				sb.append(tab.name);
				sb.append("</table_name>");
				sb.append("<table_type>");
				sb.append(tab.type);
				sb.append("</table_type>");
				if(tab.isNotEmptyRemarks()) {
					sb.append("<remarks>");
					sb.append(tab.remarks);
					sb.append("</remarks>");
				}
				sb.append("</row>");
			}
			sb.append("</rows>");
			
			if(tableName != null && !tableName.trim().equals("")) {
				String t = tableName;
				if(schemaName != null) {
					t = schemaName + "." + tableName;
				}
				if(m.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
					pstmt = con.prepareStatement("select * from " + t + " where rownum <= 30");
				} else if(m.getDatabaseProductName().equalsIgnoreCase("Apache Derby")) {
					pstmt = con.prepareStatement("select * from " + t + " { limit 30 }");
				} else {
					pstmt = con.prepareStatement("select * from " + t + " limit 30");
				}
				sb.append("<rows id=\"data\">");
				rs = pstmt.executeQuery();
				while(rs.next()) {
					sb.append("<row>");
					ResultSetMetaData rsmd = rs.getMetaData();
					for(int x = 1; x <= rsmd.getColumnCount(); x++) {
						sb.append("<" + rsmd.getColumnName(x) + "><![CDATA[");
						sb.append(rs.getString(x));
						sb.append("]]></" + rsmd.getColumnName(x) + ">\n");
					}
					sb.append("</row>");
				}
				sb.append("</rows>");
				rs.close();
				rs = null;
				pstmt.close();
				pstmt = null;
			}
			sb.append("<params><param>");
			if(tableName != null && !tableName.trim().equals("")) {
				sb.append("<table>" + tableName + "</table>");
			}
			if(schemaName != null && !schemaName.trim().equals("")) {
				sb.append("<table_schem>" + schemaName + "</table_schem>");
			}
			
			sb.append("<name>" + m.getDatabaseProductName() + "</name>");
			sb.append("<version>" + m.getDatabaseProductVersion() + "</version>");
			if(m.getUserName() != null) {
				sb.append("<user>" + m.getUserName() + "</user>");
			}
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			
			sb.append("</param></params>");
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	protected void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		String sql = value(request.getParameter("sql"));
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("jndi") == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("not found jndi config");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		boolean isAppendRows = false;
		try {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(c.getInitParameter("jndi").startsWith("java:")) {
				source = c.getInitParameter("jndi");
			} else {
				source = "java:/comp/env/" + c.getInitParameter("jndi");
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			con = ds.getConnection();
			DatabaseMetaData m = con.getMetaData();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=query\" ?>");
			sb.append("<document>");
			sb.append("<params><param>");
			if(sql != null && !sql.trim().equals("")) {
				sb.append("<sql><![CDATA[" + sql + "]]></sql>");
			}
			sb.append("<name>" + m.getDatabaseProductName() + "</name>");
			sb.append("<version>" + m.getDatabaseProductVersion() + "</version>");
			if(m.getUserName() != null) {
				sb.append("<user>" + m.getUserName() + "</user>");
			}
			sb.append("<jdbcmajorversion>" + m.getJDBCMajorVersion() + "</jdbcmajorversion>"); 
			sb.append("<jdbcminorversion>" + m.getJDBCMinorVersion() + "</jdbcminorversion>"); 
//			sb.append("<schema>" + con.getSchema() + "</schema>");
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			
			sb.append("</param></params>");
			if(sql != null && !sql.trim().equals("")) {
				pstmt = con.prepareStatement(sql);
				if(pstmt.execute()) {
					rs = pstmt.getResultSet();
					ResultSetMetaData rsmd = rs.getMetaData();
					sb.append("<rows id=\"data\">");
					isAppendRows = true;
					while(rs.next()) {
						sb.append("<row>");
						for(int x = 1; x <= rsmd.getColumnCount(); x++) {
							sb.append("<c" + x + " name=\"" + rsmd.getColumnName(x).replace("\"", "&quot;") + "\"><![CDATA[");
							sb.append(rs.getString(x));
							sb.append("]]></c" + x + ">\n");
						}
						sb.append("</row>");
					}
					sb.append("</rows>");
					rs.close();
					rs = null;
				} else {
					sb.append("<rows id=\"count\"><row>");
					sb.append("<count>" + pstmt.getUpdateCount() + "</count>");
					sb.append("</row></rows>");
				}
				pstmt.close();
				pstmt = null;
			}
			
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (NamingException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
			sb.setLength(0);
		} catch (java.sql.SQLSyntaxErrorException e) {
			if(isAppendRows) {
				sb.append("</rows>");
			}
			sb.append("<errors><error>");
			sb.append("<message><![CDATA[" + e.getMessage( ) + "]]></message>");
			sb.append("<error_code>" + e.getErrorCode( ) + "</error_code>");
			sb.append("<sql_state>" + e.getSQLState( ) + "</sql_state>");
			sb.append("</error></errors>");
			sb.append("</document>");
			
			if(logger.isLoggable(Level.WARNING)) {
				logger.warning(LogHelper.toString(e));
			}
		} catch (SQLException e) {
			if(isAppendRows) {
				sb.append("</rows>");
			}
			sb.append("<errors><error>");
			sb.append("<message><![CDATA[" + e.getMessage( ) + "]]></message>");
			sb.append("<error_code>" + e.getErrorCode( ) + "</error_code>");
			sb.append("<sql_state>" + e.getSQLState( ) + "</sql_state>");
			sb.append("</error></errors>");
			sb.append("</document>");
/*
for(Throwable t: e){
	System.out.println(t.getMessage());

}
*/
			
			if(logger.isLoggable(Level.WARNING)) {
				logger.warning(LogHelper.toString(e));
			}
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe(LogHelper.toString(e));
					}
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
}
