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


package kr.graha.lib;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;                           
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import javax.naming.NamingException;
import java.sql.DriverManager;

/**
 * Graha(그라하) 데이타베이스(DB) 관련 유틸리티

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.2
 */

public final class DBHelper {
	private static Logger logger = Logger.getLogger("kr.graha.lib.DBHelper");
	private DBHelper() {
	}
	public static Record getConnectionInfo(Element query, File config) {
		Record info = new Record();
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
		
			XPathExpression expr = xpath.compile("header/jndi");
			Element node = (Element)expr.evaluate(query, XPathConstants.NODE);
			if(node == null) {
				expr = xpath.compile("header/jdbc");
				node = (Element)expr.evaluate(query, XPathConstants.NODE);
			}
			if(node == null) {
				if(query.hasAttribute("extends")) {
					expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/jndi");
					node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
					if(node == null) {
						expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/jdbc");
						node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
					}
				}
			}
			
			if(node == null) {
				expr = xpath.compile("header/jndi");
				node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
			}
			if(node == null) {
				expr = xpath.compile("header/jdbc");
				node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
			}
			if(node == null) {
				expr = xpath.compile("header");
				Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
				if(header == null) {
					return null;
				}
				if(!header.hasAttribute("extends")) {
					return null;
				}
				File parent = new File(config.getParent() + File.separator + header.getAttribute("extends"));
				
				if(!parent.exists()) {
					return null;
				}
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setXIncludeAware(true);
				Document doc = dbf.newDocumentBuilder().parse(parent);
				doc.getDocumentElement().normalize();
				expr = xpath.compile("/querys/header/jndi");
				node = (Element)expr.evaluate(doc, XPathConstants.NODE);
				if(node == null) {
					expr = xpath.compile("/querys/header/jdbc");
					node = (Element)expr.evaluate(doc, XPathConstants.NODE);
				}
			}
			if(node == null) {
				return null;
			}
			if(node.getNodeName().equals("jndi")) {
				info.put("type", "jndi");
				info.put("name", node.getAttribute("name"));
			} else if(node.getNodeName().equals("jdbc")) {
				info.put("type", "jdbc");
				info.put("driverClassName", node.getAttribute("driverClassName"));
				info.put("url", node.getAttribute("url"));
				info.put("username", node.getAttribute("username"));
				info.put("password", node.getAttribute("password"));
			} else {
				return null;
			}
			info.put("sql_list_template", node.getAttribute("sql_list_template"));
			info.put("sql_cnt_template", node.getAttribute("sql_cnt_template"));
			info.put("sql_sequence_template", node.getAttribute("sql_sequence_template"));
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			e.printStackTrace();
		}
		return info;
	}
	public static Connection getConnection(String jndi) {
		Connection con = null;
		try {
			javax.naming.InitialContext cxt = new javax.naming.InitialContext();
			String source = null;
			if(jndi.startsWith("java:")) {
				source = jndi;
			} else {
				source = "java:/comp/env/" + jndi;
			}
			javax.sql.DataSource ds = (javax.sql.DataSource)cxt.lookup(source);
			con = ds.getConnection();
		} catch (SQLException | NamingException e2) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e2));
			}
		}
		return con;
	}
	public static Connection getConnection(Record info) {
		Connection con = null;
		if(info.hasKey("type") && info.getString("type").equals("jndi")) {
			con = DBHelper.getConnection(info.getString("name"));
		} else if(info.hasKey("type") && info.getString("type").equals("jdbc") && info.hasKey("driverClassName") && info.hasKey("url")) {
			try {
				Class.forName(info.getString("driverClassName"));
				con = DriverManager.getConnection(info.getString("url"), info.getString("username"), info.getString("password"));
			} catch (ClassNotFoundException | SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(e));
				}
			}
		}
		return con;
	}
	public static int getNextSequenceValue(PreparedStatement pstmt) throws SQLException {
		/*
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if(trace != null) {
			for(int i = 0; i < trace.length; i++) {
				logger.info(trace[i].toString());
				
			}
		}
		*/
		ResultSet rs = null;
		int result = 0;
		try {
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			} else {
				throw new ParsingException();
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
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
		return result;
	}
	public static int getNextSequenceValue(PreparedStatement pstmt, String value, Record info, DatabaseMetaData dmd) throws SQLException {
		return DBHelper.getNextSequenceValue(pstmt.getConnection(), value, info, dmd);
	}
	public static int getNextSequenceValue(Connection con, String value, Record info, DatabaseMetaData dmd) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String name = null;
		int result = 0;
		if(value.startsWith("sequence.")) {
			name = value.substring(9);
		} else {
			name = value;
		}
		try {
			pstmt = con.prepareStatement(DBHelper.getSeqSql(name, info, dmd));
			result = getNextSequenceValue(pstmt);
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
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
		return result;
	}
	public static String getSql(Element node, Record params) {
		if(node == null) {
			return null;
		}
		String sql = new String();
		NodeList list = node.getChildNodes();
		for(int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(i);
			if(n.getNodeType() == org.w3c.dom.Node.TEXT_NODE || n.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE ) {
				sql += n.getNodeValue();
				sql += "\n";
				
			} else if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Element e = (Element)n;
				if(!e.hasAttribute("cond") || AuthParser.auth(e.getAttribute("cond"), params)) {
					sql += e.getFirstChild().getNodeValue();
					sql += "\n";
					
				}
			} else {
				if(logger.isLoggable(Level.WARNING)) {
					logger.warning("NodeName = " + n.getNodeName());
					logger.warning("NodeType = " + n.getNodeType());
	
					logger.warning("2	ATTRIBUTE_NODE");
					logger.warning("4	CDATA_SECTION_NODE");
					logger.warning("8	COMMENT_NODE");
					logger.warning("11	DOCUMENT_FRAGMENT_NODE");
					logger.warning("9	DOCUMENT_NODE");
					logger.warning("16	DOCUMENT_POSITION_CONTAINED_BY");
					logger.warning("8	DOCUMENT_POSITION_CONTAINS");
					logger.warning("1	DOCUMENT_POSITION_DISCONNECTED");
					logger.warning("4	DOCUMENT_POSITION_FOLLOWING");
					logger.warning("32	DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC");
					logger.warning("2	DOCUMENT_POSITION_PRECEDING");
					logger.warning("10	DOCUMENT_TYPE_NODE");
					logger.warning("1	ELEMENT_NODE");
					logger.warning("6	ENTITY_NODE");
					logger.warning("5	ENTITY_REFERENCE_NODE");
					logger.warning("12	NOTATION_NODE");
					logger.warning("7	PROCESSING_INSTRUCTION_NODE");
					logger.warning("3	TEXT_NODE");
				}
			}
		}
		return sql;
	}
	public static String getListSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException {
		if(info != null && info.hasKey("sql_list_template")) {
			Record p = new Record();
			p.put("sql", sql);
			Record result = FileHelper.getFilePath(info.getString("sql_list_template"), p);
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
			return "select * from (select a$.*, rownum as rnum$ from (" + sql + ")  a$ where rownum <= ?) where rnum$ >= ?";
		} else if(dmd.getDatabaseProductName().equalsIgnoreCase("Apache Derby")) {
//			return "select * from (select a_.*, ROW_NUMBER() OVER() as rnum_ from (" + sql + ")  as a_) as a__ where rnum_ <= ? and rnum_ >= ?";
			return (sql + " { limit ? offset ? }");
			
		} else {
			return (sql + " limit ? offset ?");
		}

	}
	public static String getCountSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException {
		if(info != null && info.hasKey("sql_cnt_template")) {
			Record p = new Record();
			p.put("sql", sql);
			Record result = FileHelper.getFilePath(info.getString("sql_cnt_template"), p);
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
			return ("select count(*) from (" + sql + ")");
		} else {
			return ("select count(*) from (" + sql + ") as _");
		}
	}
	public static String getSeqSql(String name, Record info, DatabaseMetaData dmd) throws SQLException {
		if(info != null && info.hasKey("sql_sequence_template")) {
			Record p = new Record();
			p.put("name", name);
			Record result = FileHelper.getFilePath(info.getString("sql_sequence_template"), p);
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
			return ("select " + name + " from dual");
		} else if(dmd.getDatabaseProductName().equalsIgnoreCase("Apache Derby")) {
			return ("select " + name + " from sysibm.sysdummy1");
		} else if(dmd.getDatabaseProductName().equalsIgnoreCase("HSQL Database Engine")) {
			return ("select " + name + " from (values(0))");
		} else {
			return ("select " + name);
		}
	}
}