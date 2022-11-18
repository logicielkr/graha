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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import kr.graha.helper.LOG;
import kr.graha.helper.DB;
import kr.graha.helper.XML;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.StringTokenizer;

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
		LOG.setLogLevel(logger);
	}
	
	private void setCharacterEncoding(HttpServletRequest request) throws ServletException, IOException {
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
	private String value(String value) {
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
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found path : " + path); }
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
			&& !path.equals("/options")
			&& !path.equals("/gen_from_query")
		) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found path : " + path); }
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
		} else if(path != null && path.equals("/options")) {
			_options(request, response);
		} else if(path != null && path.equals("/gen_from_query")) {
			_gen_from_query(request, response);
		}
	}
	private void xsl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml; charset=UTF-8");
		java.io.InputStream in = null;
		ServletOutputStream out = null;
		
		String xslPath = this.getServletConfig().getInitParameter("xsl_path");
		try {
			if(xslPath != null && !xslPath.equals("")) {
				if(!xslPath.endsWith("/") && !xslPath.endsWith(java.io.File.separator)) {
					xslPath = xslPath + java.io.File.separator;
				}
				in = new java.io.FileInputStream(xslPath + value(request.getParameter("xsl")) + ".xsl");
			} else {
				in = this.getClass().getResourceAsStream("/kr/graha/assistant/xsl/" + value(request.getParameter("xsl")) + ".xsl");
			}
			if(in == null) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found xsl file : " + value(request.getParameter("xsl"))); }
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
	private void _list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
		sb.append("<document>");
		if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
			sb.append("<params><param type=\"r\">");
			sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
			sb.append("</param></params>");
		}
		sb.append("</document>");
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
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
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		StringBuffer sb = new StringBuffer();
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			con = cm.getConnection();
			DatabaseMetaData m = con.getMetaData();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
			java.util.List<Table> tabs = db.getTables(con);
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=list\" ?>");
			sb.append("<document>");
			if(cm.getJndiSize() > 1) {
				String[] jndis = cm.getJndis();
				if(jndis != null && jndis.length > 0) {
					sb.append("<rows id=\"jndi\">");
					for(int q = 0; q < jndis.length; q++) {
						sb.append("<row>");
						sb.append("<name>");
						sb.append(jndis[q]);
						sb.append("</name>");
						sb.append("</row>");
					}
					sb.append("</rows>");
				}
			}
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
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
			}
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			sb.append("</param></params>");
			sb.append(cm.getPropertyList("list"));
			sb.append("</document>");

			con.close();
			con = null;
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
	private void table(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
			DatabaseMetaData m = con.getMetaData();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=table\" ?>");
			sb.append("<document>");
			if(cm.getJndiSize() > 1) {
				String[] jndis = cm.getJndis();
				if(jndis != null && jndis.length > 0) {
					sb.append("<rows id=\"jndi\">");
					for(int q = 0; q < jndis.length; q++) {
						sb.append("<row>");
						sb.append("<name>");
						sb.append(jndis[q]);
						sb.append("</name>");
						sb.append("</row>");
					}
					sb.append("</rows>");
				}
			}
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
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
			}
			sb.append("</param></params>");
			sb.append(cm.getPropertyList("table"));
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
	private void _table(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Connection con = null;
		
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
		sb.append("<document>");
		sb.append("<params><param type=\"r\"><table>" + value(request.getParameter("table")) + "</table>");
		if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
			sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
		}
		sb.append("</param></params>");
		sb.append("</document>");
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
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
						if(logger.isLoggable(Level.SEVERE)) { logger.severe("parameter is not allow"); }
						
					}
				}
			}
			con.close();
			con = null;
		} catch (SQLException | NamingException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
	private void _options(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		request.setCharacterEncoding(charset);
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		StringBuffer sb = new StringBuffer();
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=options\" ?>");
			sb.append("<document>");
			sb.append("<columns>");
			int columnCount = 0;
			java.util.Enumeration<String> e = request.getParameterNames();
	
			while (e.hasMoreElements()) {
				String key = cm.value(e.nextElement());
				if(key != null && key.startsWith("column.")) {
/*
sb.append("<column key=\"" + key + "\">" + cm.value(request.getParameter(key)) + "</column>");
*/
						sb.append("<column key=\"" + key + "\">");
						sb.append("<name>" + cm.value(request.getParameter(key)) + "</name>");
						Set labels = db.getCommentByColumnName(con, cm.value(request.getParameter(key)));
//						System.out.println(labels);
						if(labels != null && !labels.isEmpty()) {
							sb.append("<labels>");
							java.util.Iterator<String> it = labels.iterator();
							while(it.hasNext()) {
								String label = (String)it.next();
								sb.append("<label>" + label + "</label>");
							}
							sb.append("</labels>");
						}
						sb.append("</column>");
						
						columnCount++;
				}
			}
			sb.append("</columns>");
			sb.append("<params>");
			if(columnCount > 0) {
				sb.append("<param name=\"column_count\">" + columnCount + "</param>");
			}
			e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String key = cm.value(e.nextElement());
				if(key != null && !key.startsWith("column.")) {
						sb.append("<param name=\"" + key + "\">" + cm.value(request.getParameter(key)) + "</param>");
				}
			}
			sb.append("</params>");
			sb.append(cm.getPropertyList("options"));
			sb.append("</document>");
			con.close();
			con = null;
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				}
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
//		response.setContentType("text/xml; charset=UTF-8");
//		response.getWriter().append(sb);
	}
	private String getColumnLebelFromColumnName(String columnName) {
		if(columnName == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		StringTokenizer st = new StringTokenizer(columnName, "()-_, ");
		int index = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token != null && token.length() > 0) {
				if(index > 0) {
					result.append(" ");
				}
				result.append(token.substring(0, 1).toUpperCase() + token.substring(1).toLowerCase());
				index++;
			}
		}
		return result.toString();
	}
	private void _gen_from_query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		request.setCharacterEncoding(charset);
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		Connection con = null;
		DatabaseMetaData dmd = null;
		boolean oracleType = false;
		try {
			con = cm.getConnection();
			dmd = con.getMetaData();
			if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle") || dmd.getDatabaseProductName().equalsIgnoreCase("Tibero")) {
				oracleType = true;
			}
			DB.close(con);
		} catch (SQLException | NamingException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			DB.close(con);
		}
		String id = cm.param("query_id");
		String label = cm.param("query_label");
		if(label.trim().equals("")) {
			label = getColumnLebelFromColumnName(id);
		}
		String fetched = cm.param("fetched");
		String updated = cm.param("updated");
		
		int fetchedCount = -1;
		int updatedCount = -1;
		if(!fetched.trim().equals("")) {
			try {
				fetchedCount = Integer.valueOf(fetched);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		if(!updated.trim().equals("")) {
			try {
				updatedCount = Integer.valueOf(updated);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		
		String funcType = cm.param("func_type");
		if(updatedCount >= 0) {
			funcType = "query";
		}

		String redirectPath = cm.param("redirect_path");
		String print = cm.param("print");
		String file = cm.param("file");
		String filePath = cm.param("file_path");
		String sql = cm.param("sql");
		int columnCount = 0;
		if(!cm.param("column_count").trim().equals("")) {
			try {
				columnCount = Integer.valueOf(cm.param("column_count"));
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		
		StringBuffer bw = new StringBuffer();
		bw.append("	<query id=\"" + id + "\" funcType=\"" + funcType + "\" label=\"" + label + "\">\n");
		bw.append("		<header>\n");
		bw.append("		</header>\n");
		bw.append("		<commands>\n");
		bw.append("			<command name=\"" + id + "\">\n");
		bw.append("				<sql>\n");
		bw.append(sql);
		bw.append("\n");
		bw.append("				</sql>\n");
		if(funcType.equals("list")) {
			bw.append("				<sql_cnt>\n");
			bw.append("select count(*) from (\n");
			bw.append(sql);
			bw.append("\n");
			if(oracleType) {
				bw.append(")\n");
			} else {
				bw.append(") as _\n");
			}
			bw.append("				</sql_cnt>\n");
		}
		bw.append("				<!--params>\n");
		bw.append("					<param default=\"null\" name=\"input column name!!!\" datatype=\"input data type!!!\" value=\"input value!!!\" />\n");
		bw.append("				</params-->\n");
		bw.append("			</command>\n");
		bw.append("		</commands>\n");
		if(fetchedCount >= 0) {
			bw.append("		<layout>\n");
			bw.append("			<top>\n");
			bw.append("				<left />\n");
			bw.append("				<center />\n");
			bw.append("				<right />\n");
			bw.append("			</top>\n");
			bw.append("			<middle>\n");
			bw.append("				<tab name=\"" + id + "\" label=\"" + label + "\">\n");
			for(int x = 1; x <= columnCount; x++) {
				if(funcType.equals("detail")) {
					bw.append("					<row>\n");
					bw.append("						<column label=\"" + cm.param("label_column." + x, getColumnLebelFromColumnName(cm.param("name_column." + x))) + "\" name=\"" + cm.param("name_column." + x).toLowerCase() + "\" />\n");
					bw.append("					</row>\n");
				} else if(funcType.equals("list") || funcType.equals("listAll")) {
					bw.append("					<column label=\"" + cm.param("label_column." + x, getColumnLebelFromColumnName(cm.param("name_column." + x))) + "\" name=\"" + cm.param("name_column." + x).toLowerCase() + "\" />\n");
				}
			}
			bw.append("				</tab>\n");
			bw.append("			</middle>\n");
			bw.append("			<bottom>\n");
			bw.append("				<left />\n");
			bw.append("				<center />\n");
			bw.append("				<right />\n");
			bw.append("			</bottom>\n");
			bw.append("		</layout>\n");
		} else if(updatedCount >= 0) {
			bw.append("		<redirect path=\"" + redirectPath + "\">\n");
			bw.append("			<!--param name=\"page\" type=\"param\" value=\"page\" /-->\n");
			bw.append("		</redirect>\n");
		}
		bw.append("	</query>\n");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=gen_from_query\" ?>");
		sb.append("<document>");
		sb.append("<rows id=\"gen\"><row>");
		sb.append("<gen><![CDATA[" + XML.fix(bw.toString()) + "]]></gen>");
		sb.append("</row></rows>");
		if(!cm.param("jndi").trim().equals("")) {
			sb.append("<params>");
			sb.append("<param name=\"jndi\">" + cm.param("jndi") + "</param>");
			sb.append("</params>");
		}
		try {
			sb.append(cm.getPropertyList("gen_from_query"));
			sb.append("</document>");
		} catch (IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}

		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	private void _select(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		request.setCharacterEncoding(charset);
		StringBuffer sb = new StringBuffer();
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		Connection con = null;
		java.util.List codes = null;
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
			java.util.List<Column> cols = null;
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=select\" ?>");
			sb.append("<document><params>");
			java.util.Enumeration<String> e = request.getParameterNames();
			java.util.List<Table> tables = new java.util.ArrayList<Table>();
			while (e.hasMoreElements()) {
				String key = cm.value(e.nextElement());
				if(key != null && key.startsWith("check.") && cm.value(request.getParameter(key)) != null && cm.value(request.getParameter(key)).equals("on")) {
					if(logger.isLoggable(Level.FINEST)) { logger.finest("" + key + " : " + cm.value(request.getParameter(key))); logger.finest("" + key.substring(6) + " : " + cm.value(request.getParameter(key))); }
					String schemaAndTableName = key.substring(6);
					tables.add(new Table(schemaAndTableName));
				}
			}
			tables = db.getTablesWithColumns(con, tables);
			
			for(Table table: tables) {
				for(Table tab: tables) {
					if(tab.compareWithSchemaAndTableName(table)) {
						continue;
					}
					int fkColumnCount = 0;
					int pkColumnCount = 0;
					for(Column col : table.cols) {
						if(col.isPk()) {
							continue;
						}
						pkColumnCount = 0;
						for(Column c : tab.cols) {
							if(c.isPk()) {
								if(c.name.equals(col.name)) {
									col.isFk = true;
									fkColumnCount++;
								}
								pkColumnCount++;
							}
						}
					}
					if(pkColumnCount > 0 && pkColumnCount == fkColumnCount) {
						table.isMaster = false;
						table.masterTableSchema = tab.schema;
						table.masterTableName = tab.name;
					}
				}
			}
			for(Table table: tables) {
				sb.append("<table name=\"" + table.getNameWithSchema() + "\" master=\"" + table.isMaster + "\" table_name=\"" + table.getLowerName() + "\" schema_name=\"" + table.getSchemaName() + "\" xml_file_name=\"" + XMLConfigGenerator.getXMLFileName(XMLConfigGenerator.getXMLFile(this.getServletContext().getRealPath("/WEB-INF/graha/"), table.name, false)) + "\">");
				for(Column col : table.cols) {
					sb.append("<column lower_name=\"" + col.getLowerName() + "\" name=\"" + col.name + "\" graha_data_type=\"" + db.getGrahaDataType(col.dataType) + "\" data_type_name=\"" + col.typeName + "\" def=\"" + db.isDef(col.getLowerName()) + "\" pk=\"" + col.isPk() + "\" fk=\"" + col.isFk() + "\">");
					if(col.remarks != null) {
						sb.append("<![CDATA[" + col.remarks + "]]>");
					}
					sb.append("</column>");
				}
				sb.append("</table>");
			}
			if(cm.getGrahaCommonCodeTableName() != null) {
				try {
					codes = kr.graha.helper.DB.fetch(con, java.util.HashMap.class, "select value, label from common.graha_common_code where parent_id is null", null);
				} catch (SQLException e1) {
					codes = null;
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e1)); }
				}
			}
			if(codes != null && codes.size() > 0) {
				sb.append("<codes>");
				for(int i = 0; i < codes.size(); i++) {
					java.util.HashMap data = (java.util.HashMap)codes.get(i);
					sb.append("<code value=\"" + data.get("value") + "\" label=\"" + data.get("label") + "\" />");
				}
				sb.append("</codes>");
			}
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<param><jndi>" + request.getParameter("jndi") + "</jndi></param>");
			}
			String ownerColumnByDef = db.getOwnerColumnByDef();
			if(ownerColumnByDef != null && !ownerColumnByDef.equals("")) {
				sb.append("<prop><owner_column>" + ownerColumnByDef + "</owner_column></prop>");
			}
			sb.append("</params>\n");
			sb.append(cm.getPropertyList("select"));
			sb.append("</document>");
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			try {
				if(con != null) {
					con.close();
				}
			} catch (SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			}
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	
	private void _gen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String schemaAndTableName = value(request.getParameter("table"));
		Table masterTable = null;
		if(schemaAndTableName != null) {
			masterTable = new Table(schemaAndTableName);
		}
		if(masterTable == null) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("table name is null or blank"); }
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		Connection con = null;
		StringBuffer sb = new StringBuffer();
		try {
			con = cm.getConnection();
			XMLConfigGenerator gen = new XMLConfigGenerator(
				masterTable,
				cm,
				con
			);
			String xmlName = gen.execute();
			con.close();
			con = null;
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=redirect\" ?>");
			sb.append("<document>");
			sb.append("<params><param type=\"s\"><path>list</path><auto_redirect>false</auto_redirect></param>");
			java.util.List grahaAppRootPath = cm.getGrahaAppRootPath();
			if(grahaAppRootPath != null && grahaAppRootPath.size() > 0) {
				for(int i = 0; i < grahaAppRootPath.size(); i++) {
					sb.append("<param type=\"a\"><path>" + grahaAppRootPath.get(i) + xmlName + "/list.xml" + "</path><auto_redirect>false</auto_redirect></param>");
				}
			}
			java.util.List files = gen.getFiles();
			if(files != null && files.size() > 0) {
				for(int i = 0; i < files.size(); i++) {
					sb.append("<param type=\"f\"><path>" + files.get(i) + "</path></param>");
				}
			}
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<param type=\"r\"><jndi>" + request.getParameter("jndi") + "</jndi></param>");
			}
			sb.append("</params>");
			sb.append(cm.getPropertyList("redirect"));
			sb.append("</document>");
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			try {
				if(con != null) {
					con.close();
				}
			} catch (SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			}
			
		}
		if(sb.length() > 0) {
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().append(sb);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}
	}
	
	private void data(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		try {
			con = cm.getConnection();
			DBUtil db = DBUtil.getDBUtil(con, cm.getDef(), cm.getMapping());
			DatabaseMetaData m = con.getMetaData();
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=data\" ?>");
			sb.append("<document>");
			if(cm.getJndiSize() > 1) {
				String[] jndis = cm.getJndis();
				if(jndis != null && jndis.length > 0) {
					sb.append("<rows id=\"jndi\">");
					for(int q = 0; q < jndis.length; q++) {
						sb.append("<row>");
						sb.append("<name>");
						sb.append(jndis[q]);
						sb.append("</name>");
						sb.append("</row>");
					}
					sb.append("</rows>");
				}
			}
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
				if(m.getDatabaseProductName().equalsIgnoreCase("Oracle") || m.getDatabaseProductName().equalsIgnoreCase("Tibero")) {
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
						sb.append(XML.fix(rs.getString(x)));
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
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
			}
			sb.append("</param></params>");
			sb.append(cm.getPropertyList("data"));
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (SQLException | NamingException | IOException e) {
			sb.setLength(0);
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		} finally {
			if(rs != null) {
				try {
					rs.close();
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
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
	/*
	private StringBuffer getGrahaConfig(String sql, java.util.List<String> columns, int fetchCount) throws SQLException {
		StringBuffer bw = new StringBuffer();
		if(columns != null) {
			if(fetchCount == 1) {
				bw.append("	<query id=\"input query id!!!\" funcType=\"detail\" label=\"input label!!!\">\n");
			} else {
				bw.append("	<query id=\"input query id!!!\" funcType=\"listAll\" label=\"input label!!!\">\n");
			}
		} else {
			bw.append("	<query id=\"input query id!!!\" funcType=\"query\" label=\"input label!!!\">\n");
		}
		bw.append("		<header>\n");
		bw.append("		</header>\n");
		bw.append("		<commands>\n");
		bw.append("			<command name=\"default\">\n");
		bw.append("				<sql>\n");
		bw.append(sql);
		bw.append("\n");
		bw.append("				</sql>\n");
		bw.append("				<!--params>\n");
		bw.append("					<param default=\"null\" name=\"input column name!!!\" datatype=\"input data type!!!\" value=\"input value!!!\" />\n");
		bw.append("				</params-->\n");
		bw.append("			</command>\n");
		bw.append("		</commands>\n");
		if(columns != null) {
			bw.append("		<layout>\n");
			bw.append("			<top>\n");
			bw.append("				<left />\n");
			bw.append("				<center />\n");
			bw.append("				<right />\n");
			bw.append("			</top>\n");
			bw.append("			<middle>\n");
			bw.append("				<tab name=\"default\" label=\"input label!!!\">\n");
			for(int x = 0; x < columns.size(); x++) {
				if(fetchCount == 1) {
					bw.append("					<row>\n");
					bw.append("						<column label=\"" + columns.get(x) + "\" name=\"" + columns.get(x) + "\" />\n");
					bw.append("					</row>\n");
				} else {
					bw.append("						<column label=\"" + columns.get(x) + "\" name=\"" + columns.get(x) + "\" />\n");
				}
			}
			bw.append("				</tab>\n");
			bw.append("			</middle>\n");
			bw.append("			<bottom>\n");
			bw.append("				<left />\n");
			bw.append("				<center />\n");
			bw.append("				<right />\n");
			bw.append("			</bottom>\n");
			bw.append("		</layout>\n");
		} else {
			bw.append("		<redirect path=\"list\">\n");
			bw.append("			<param name=\"page\" type=\"param\" value=\"page\" />\n");
			bw.append("		</redirect>\n");
		}
		bw.append("	</query>\n");
		return bw;
	}
	*/
	private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();
		String sql = value(request.getParameter("sql"));
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
//		StringBuffer bw = new StringBuffer();
		CManager cm = new CManager(this.getServletConfig(), request);
		if(!cm.valid()) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe("not found jndi config"); }
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		boolean isAppendRows = false;
		try {
			con = cm.getConnection();
			DatabaseMetaData m = con.getMetaData();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"xsl?xsl=query\" ?>");
			sb.append("<document>");
			if(cm.getJndiSize() > 1) {
				String[] jndis = cm.getJndis();
				if(jndis != null && jndis.length > 0) {
					sb.append("<rows id=\"jndi\">");
					for(int q = 0; q < jndis.length; q++) {
						sb.append("<row>");
						sb.append("<name>");
						sb.append(jndis[q]);
						sb.append("</name>");
						sb.append("</row>");
					}
					sb.append("</rows>");
				}
			}
			sb.append("<params><param>");
			if(sql != null && !sql.trim().equals("")) {
				sb.append("<sql><![CDATA[" + XML.fix(sql) + "]]></sql>");
			}
			sb.append("<name>" + m.getDatabaseProductName() + "</name>");
			sb.append("<version>" + m.getDatabaseProductVersion() + "</version>");
			if(m.getUserName() != null) {
				sb.append("<user>" + m.getUserName() + "</user>");
			}
			sb.append("<jdbcmajorversion>" + m.getJDBCMajorVersion() + "</jdbcmajorversion>"); 
			sb.append("<jdbcminorversion>" + m.getJDBCMinorVersion() + "</jdbcminorversion>"); 
			sb.append("<driver>" + m.getDriverName() + "</driver>");
			sb.append("<driver_version>" + m.getDriverVersion() + "</driver_version>");
			if(request.getParameter("jndi") != null && !request.getParameter("jndi").equals("")) {
				sb.append("<jndi>" + request.getParameter("jndi") + "</jndi>");
			}
			sb.append("</param></params>");
			if(sql != null && !sql.trim().equals("")) {
				pstmt = con.prepareStatement(sql);
				if(pstmt.execute()) {
					rs = pstmt.getResultSet();
					ResultSetMetaData rsmd = rs.getMetaData();
					sb.append("<rows id=\"meta\">");
					sb.append("<row>");
					for(int x = 1; x <= rsmd.getColumnCount(); x++) {
						sb.append("<c" + x + "><![CDATA[");
						sb.append(XML.fix(rsmd.getColumnName(x)));
						sb.append("]]></c" + x + ">\n");
					}
					sb.append("</row>");
					sb.append("</rows>");
					sb.append("<rows id=\"data\">");
					isAppendRows = true;
					int index = 0;
					java.util.List<String> columns = new java.util.ArrayList<String>();
					
					while(rs.next()) {
						sb.append("<row>");
						for(int x = 1; x <= rsmd.getColumnCount(); x++) {
							sb.append("<c" + x + "><![CDATA[");
							sb.append(XML.fix(rs.getString(x)));
							sb.append("]]></c" + x + ">\n");
							if(index == 0) {
									columns.add(rsmd.getColumnName(x));
							}
						}
						sb.append("</row>");
						index++;
					}
					sb.append("</rows>");
//					bw = getGrahaConfig(sql, columns, index);
					rs.close();
					rs = null;
				} else {
//					bw = getGrahaConfig(sql, null, pstmt.getUpdateCount());
					sb.append("<rows id=\"count\"><row>");
					sb.append("<count>" + pstmt.getUpdateCount() + "</count>");
					sb.append("</row></rows>");
				}
				pstmt.close();
				pstmt = null;
			}
			/*
			if(bw.length() > 0) {
				sb.append("<rows id=\"gen\"><row>");
				sb.append("<gen><![CDATA[" + XML.fix(bw.toString()) + "]]></gen>");
				sb.append("</row></rows>");
			}
			*/
			sb.append(cm.getPropertyList("query"));
			sb.append("</document>");
			
			con.close();
			con = null;
		} catch (NamingException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			sb.setLength(0);
		} catch (java.sql.SQLSyntaxErrorException e) {
			if(isAppendRows) {
				sb.append("</rows>");
			} else if(sb.length() == 0) {
				sb.append("<document>");
			}
			sb.append("<errors><error>");
			sb.append("<message><![CDATA[" + XML.fix(e.getMessage()) + "]]></message>");
			sb.append("<error_code>" + e.getErrorCode( ) + "</error_code>");
			sb.append("<sql_state>" + e.getSQLState( ) + "</sql_state>");
			sb.append("</error></errors>");
			try {
				sb.append(cm.getPropertyList("query"));
				sb.append("</document>");
			} catch (IOException ee) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				sb.setLength(0);
			}
			
			if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
		} catch (SQLException e) {
			if(isAppendRows) {
				sb.append("</rows>");
			} else if(sb.length() == 0) {
				sb.append("<document>");
			}
			sb.append("<errors><error>");
			sb.append("<message><![CDATA[" + XML.fix(e.getMessage()) + "]]></message>");
			sb.append("<error_code>" + e.getErrorCode( ) + "</error_code>");
			sb.append("<sql_state>" + e.getSQLState( ) + "</sql_state>");
			sb.append("</error></errors>");
			try {
				sb.append(cm.getPropertyList("query"));
				sb.append("</document>");
			} catch (IOException ee) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				sb.setLength(0);
			}
/*
			for(Throwable t: e){
				System.out.println(t.getMessage());
			
			}
*/
			
			if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
		} finally {
			if(rs != null) {
				try {
					rs.close();
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
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
