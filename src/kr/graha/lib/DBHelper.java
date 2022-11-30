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
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import javax.naming.NamingException;
import java.sql.DriverManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.sql.Timestamp;
import kr.graha.helper.DB;
import kr.graha.helper.LOG;
import kr.graha.helper.XML;
import kr.graha.helper.STR;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.security.NoSuchProviderException;
import java.sql.ResultSetMetaData;

/**
 * Graha(그라하) 데이타베이스(DB) 관련 유틸리티

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.2
 */

public final class DBHelper {
	private static Logger logger = Logger.getLogger("kr.graha.lib.DBHelper");
	private static int FOUND_CONNECTION_INFO_QUERY = 1;
	private static int FOUND_CONNECTION_INFO_EXTENDS = 2;
	private static int FOUND_CONNECTION_INFO_QUERYS = 3;
	private static int FOUND_CONNECTION_INFO_PARENT = 4;
	private static int NOT_FOUND_CONNECTION_INFO = 0;
	private DBHelper() {
	}
	private static int found(Element node, int current) {
		if(node != null) {
			return current;
		} else {
			return DBHelper.NOT_FOUND_CONNECTION_INFO;
		}
	}
	public static Record getConnectionInfo(Element query, File config) {
		Record info = new Record();
		int foundDb = DBHelper.NOT_FOUND_CONNECTION_INFO;
		int foundNode = DBHelper.NOT_FOUND_CONNECTION_INFO;
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
		
			XPathExpression expr = xpath.compile("header/jndi");
			Element node = (Element)expr.evaluate(query, XPathConstants.NODE);
			expr = xpath.compile("header/connection");
			Element db = (Element)expr.evaluate(query, XPathConstants.NODE);
			if(node == null) {
				expr = xpath.compile("header/jdbc");
				node = (Element)expr.evaluate(query, XPathConstants.NODE);
			}
			foundDb = found(db, DBHelper.FOUND_CONNECTION_INFO_QUERY);
			foundNode = found(node, DBHelper.FOUND_CONNECTION_INFO_QUERY);
			if(node == null || db == null) {
				if(query.hasAttribute("extends")) {
					if(db == null) {
						expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/connection");
						db = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
						foundDb = found(db, DBHelper.FOUND_CONNECTION_INFO_EXTENDS);
					}
					if(node == null) {
						expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/jndi");
						node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
						if(node == null) {
							expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/jdbc");
							node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
						}
						foundNode = found(node, DBHelper.FOUND_CONNECTION_INFO_EXTENDS);
					}
				}
			}
			
			if(node == null || db == null) {
				if(db == null) {
					expr = xpath.compile("header/connection");
					db = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
					foundDb = found(db, DBHelper.FOUND_CONNECTION_INFO_QUERYS);
				}
				if(node == null) {
					expr = xpath.compile("header/jndi");
					node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
					if(node == null) {
						expr = xpath.compile("header/jdbc");
						node = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
					}
					foundNode = found(node, DBHelper.FOUND_CONNECTION_INFO_QUERYS);
				}
			}
			if(node == null || db == null) {
				expr = xpath.compile("header");
				Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
				if(node == null && db == null && header == null) {
					return null;
				}
				if(node == null && db == null && !header.hasAttribute("extends")) {
					return null;
				}
				File parent = new File(config.getParent() + File.separator + header.getAttribute("extends"));
				
				if(node == null && db == null && !parent.exists()) {
					return null;
				}
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setXIncludeAware(true);
				Document doc = dbf.newDocumentBuilder().parse(parent);
				doc.getDocumentElement().normalize();
				if(db == null) {
					expr = xpath.compile("/querys/header/connection");
					db = (Element)expr.evaluate(doc, XPathConstants.NODE);
					foundDb = found(db, DBHelper.FOUND_CONNECTION_INFO_PARENT);
				}
				if(node == null) {
					expr = xpath.compile("/querys/header/jndi");
					node = (Element)expr.evaluate(doc, XPathConstants.NODE);
					if(node == null) {
						expr = xpath.compile("/querys/header/jdbc");
						node = (Element)expr.evaluate(doc, XPathConstants.NODE);
					}
					foundNode = found(node, DBHelper.FOUND_CONNECTION_INFO_PARENT);
				}
			}
			if(node == null && db == null) {
				return null;
			}
			if(node != null && node.getNodeName().equals("jndi")) {
				info.put("type", "jndi");
				info.put("name", node.getAttribute("name"));
			} else if(node != null && node.getNodeName().equals("jdbc")) {
				info.put("type", "jdbc");
				info.put("driverClassName", node.getAttribute("driverClassName"));
				if(XML.validAttrValue(node, "protectedUrl")) {
					info.put("protectedUrl", node.getAttribute("protectedUrl"));
				}
				info.put("url", node.getAttribute("url"));
				info.put("username", node.getAttribute("username"));
				info.put("password", node.getAttribute("password"));
			} else if(db == null) {
				return null;
			}
			if(db != null) {
				info.put("factory", node.getAttribute("factory"));
				info.put("factory_found", foundDb);
			}
			if(node != null) {
				info.put("connection_found", foundNode);
				info.put("sql_list_template", node.getAttribute("sql_list_template"));
				info.put("sql_cnt_template", node.getAttribute("sql_cnt_template"));
				info.put("sql_sequence_template", node.getAttribute("sql_sequence_template"));
			}
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return info;
	}
	private static ConnectionFactory getConnectionFactory(Record info) {
		if(!info.containsKey("factory") || !info.containsKey("factory_found")) {
			return null;
		}
		String factoryClassName = (String)info.get("factory");
		if(factoryClassName == null || factoryClassName.trim().equals("")) {
			return null;
		}
		int foundDb = (int)info.get("factory_found");
		int foundNode = DBHelper.NOT_FOUND_CONNECTION_INFO;
		if(info.containsKey("connection_found")) {
			foundNode = (int)info.get("connection_found");
			if(foundNode < foundDb) {
				return null;
			}
		}
		ConnectionFactory factory = null;
		try {
			factory = (ConnectionFactory) Class.forName(factoryClassName).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
			factory = null;
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return factory;
	}
	public static Connection getConnection(Record info, Record params) {
		Connection con = null;
		ConnectionFactory factory = getConnectionFactory(info);
		if(factory != null) {
			try {
				con = factory.getConnection(info, params);
			} catch (SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				con = null;
			}
		} else if(info.hasKey("type") && info.getString("type").equals("jndi")) {
			con = DB.getConnection(info.getString("name"));
		} else if(
			info.hasKey("type") && info.getString("type").equals("jdbc") &&
			(
				info.hasKey("driverClassName") && info.hasKey("url") ||
				info.hasKey("driverClassName") && info.hasKey("protectedUrl")
			)
		) {
			String url = null;
			if(info.hasKey("protectedUrl")) {
				Record result  = FileHelper.parse(info.getString("protectedUrl"), params);
				if(result != null && result.get("_system.filepath") != null) {
					url = result.getString("_system.filepath");
				}
			}
			if(url == null) {
				url = info.getString("url");
			}
			try {
				Class.forName(info.getString("driverClassName"));
				con = DriverManager.getConnection(url, info.getString("username"), info.getString("password"));
			} catch (ClassNotFoundException | SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				con = null;
			}
		}
		return con;
	}
	protected static int getXMLStringFromResultSet(
		ResultSet rs,
		XMLTag tag,
		String commandName,
		java.util.Map<String, Encryptor> encryptor,
		java.util.Map<String, String> encrypted,
		Record params,
		java.util.Map<String, String> pattern,
		String funcType,
		String databaseProductName,
		boolean multi,
		boolean queryToParam,
		Buffer sb
	) throws SQLException {
//		sb.appendL(tag.tag("rows", commandName, true));
		int index = 0;
		ResultSetMetaData rsmd = rs.getMetaData();
		while(rs.next()) {
			sb.append(tag.tag("row", null, true));
			for(int x = 1; x <= rsmd.getColumnCount(); x++) {
				if(
					(
						databaseProductName.equalsIgnoreCase("Oracle")
						|| databaseProductName.equalsIgnoreCase("Tibero")
					)
					&& rsmd.getColumnName(x) != null
					&& rsmd.getColumnName(x).equals("RNUM$") 
				) {
					continue;
				}
				String value = null;
				if(
					databaseProductName.equalsIgnoreCase("SQLite") &&
					rsmd.getColumnType(x) == java.sql.Types.DATE
				) {
					if(
						rsmd.getColumnTypeName(x).equals("DATETIME") &&
						pattern != null &&
						pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
					) {
						value = DBHelper.getSQLiteTimestampOrDateValue(rs, x, pattern.get(rsmd.getColumnName(x).toLowerCase()), true);
					} else if(
						pattern != null &&
						pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
					) {
						value = DBHelper.getSQLiteTimestampOrDateValue(rs, x, pattern.get(rsmd.getColumnName(x).toLowerCase()), false);
					} else {
						value = rs.getString(x);
					}
				} else if(
					rsmd.getColumnType(x) == java.sql.Types.DATE &&
					rs.getDate(x) != null &&
					pattern != null &&
					pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
				) {
					value = STR.formatDate(rs.getDate(x), pattern.get(rsmd.getColumnName(x).toLowerCase()));
				} else if(rsmd.getColumnType(x) == java.sql.Types.TIMESTAMP &&
					rs.getTimestamp(x) != null &&
					pattern != null &&
					pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
				) {
					value = STR.formatDate(rs.getTimestamp(x), pattern.get(rsmd.getColumnName(x).toLowerCase()));
				} else {
					value = rs.getString(x);
				}
				if(value != null) {
					if(encrypted != null && encryptor != null && encrypted.containsKey(rsmd.getColumnName(x).toLowerCase())) {
						try {
							value = encryptor.get(encrypted.get(rsmd.getColumnName(x).toLowerCase())).decrypt(value);
//							sb.append(XML.fix(encryptor.get(encrypted.get(rsmd.getColumnName(x).toLowerCase())).decrypt(value)));
/*
							String tmp = encryptor.get(encrypted.get(rsmd.getColumnName(x).toLowerCase())).decrypt(value);
							if(tmp != null) {
								sb.append(XML.fix(tmp));
							}
*/
						} catch (NoSuchProviderException e) {
							if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
						}
					}
				}
				if(value != null) {

					if(
						!queryToParam && (
							funcType.equals("detail") ||
							funcType.equals("report") ||
							funcType.equals("user") ||
							funcType.equals("insert")
						)
					) {
						if(commandName != null && !commandName.equals("")) {
							if(multi) {
								params.put("query." + commandName + "." + rsmd.getColumnName(x).toLowerCase() + "." + index, value);
							} else {
								params.put("query." + commandName + "." + rsmd.getColumnName(x).toLowerCase(), value);
							}
						}
					}
					sb.append("<");
					sb.append(tag.tag("row", rsmd.getColumnName(x).toLowerCase(), null, true));
					
					if(rsmd.getColumnType(x) == java.sql.Types.VARCHAR) {
						sb.append("><![CDATA[");
					} else {
						sb.append(">");
					}
					if(rsmd.getColumnType(x) == java.sql.Types.VARCHAR) {
						sb.append(XML.fix(value));
					} else {
						sb.append(value);
					}
					if(rsmd.getColumnType(x) == java.sql.Types.VARCHAR) {
						sb.append("]]></");
					} else {
						sb.append("</");
					}
					sb.append(tag.tag("row", rsmd.getColumnName(x).toLowerCase(), null, false));
					sb.appendL(">");
				}
			}
			sb.appendL(tag.tag("row", null, false));
			index++;
		}
		return index;
	}
	protected static String getSQLiteTimestampOrDateValue(ResultSet rs, int index, String pattern, boolean isTimestamp) throws SQLException {
/*
Caused by: java.text.ParseException: Unparseable date: "2015-08-05 15:11:16" does not match (\p{Nd}++)\Q-\E(\p{Nd}++)\Q-\E(\p{Nd}++)\Q \E(\p{Nd}++)\Q:\E(\p{Nd}++)\Q:\E(\p{Nd}++)\Q.\E(\p{Nd}++)
*/
		Timestamp timestampValue = null;
		Date dateValue = null;
		try {
			if(isTimestamp) {
				timestampValue = rs.getTimestamp(index);
				if(pattern == null) {
					return timestampValue.toString();
				} else {
					return STR.formatDate(timestampValue, pattern);
				}
			} else {
				dateValue = rs.getTimestamp(index);
				if(pattern == null) {
					return dateValue.toString();
				} else {
					return STR.formatDate(dateValue, pattern);
				}
			}
		} catch (SQLException e) {
			if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
		}
		String str = null;
		try {
			str = rs.getString(index);
			if(str != null) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				if(isTimestamp) {
					if(pattern == null) {
						return (new Timestamp(df.parse(str).getTime())).toString();
					} else {
						return STR.formatDate(new Timestamp(df.parse(str).getTime()), pattern);
					}
				} else {
					if(pattern == null) {
						return df.parse(str).toString();
					} else {
						return STR.formatDate(df.parse(str), pattern);
					}
				}
			}
		} catch (SQLException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} catch (java.text.ParseException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return str;
	}
	public static int getNextSequenceValue(PreparedStatement pstmt) throws SQLException {
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
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
		return result;
	}
	protected static int getNextSequenceValue(PreparedStatement pstmt, String value, Record info, DatabaseMetaData dmd) throws SQLException {
		return DBHelper.getNextSequenceValue(pstmt.getConnection(), value, info, dmd);
	}
	protected static int getNextSequenceValue(Connection con, String value, Record info, DatabaseMetaData dmd) throws SQLException {
		PreparedStatement pstmt = null;
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
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
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
						if(n.getNodeName() != null && n.getNodeName().equals("entity")) {
/*
이 기능은 신중하게 사용하세요.
사용자가 입력한 파라미터를 테이블이름 등으로 대체하는 것은
원칙적으로 허용되지 않는 방식입니다.
*/
							Record result  = FileHelper.parse(e.getFirstChild().getNodeValue(), params);
							if(result != null && result.get("_system.filepath") != null) {
								sql += result.get("_system.filepath");
							}
						} else if(n.getNodeName() != null && n.getNodeName().equals("tile")) {
							sql += e.getFirstChild().getNodeValue();
							sql += "\n";
						}
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
	protected static String getListSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException {
		ConnectionFactory factory = getConnectionFactory(info);
		if(factory != null) {
			String listSQL = factory.getListSql(sql, info, dmd);
			if(listSQL != null && !listSQL.trim().equals("")) {
				return listSQL;
			}
		} else if(info != null && info.hasKey("sql_list_template")) {
			Record p = new Record();
			p.put("sql", sql);
			Record result = FileHelper.getFilePath(p, info.getString("sql_list_template"));
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle") || dmd.getDatabaseProductName().equalsIgnoreCase("Tibero")) {
			return "select * from (select a$.*, rownum as rnum$ from (" + sql + ")  a$ where rownum <= ?) where rnum$ >= ?";
		} else if(dmd.getDatabaseProductName().equalsIgnoreCase("Apache Derby")) {
			return (sql + " { limit ? offset ? }");
			
		} else {
			return (sql + " limit ? offset ?");
		}

	}
	protected static String getCountSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException {
		ConnectionFactory factory = getConnectionFactory(info);
		if(factory != null) {
			String countSQL = factory.getCountSql(sql, info, dmd);
			if(countSQL != null && !countSQL.trim().equals("")) {
				return countSQL;
			}
		} else if(info != null && info.hasKey("sql_cnt_template")) {
			Record p = new Record();
			p.put("sql", sql);
			Record result = FileHelper.getFilePath(p, info.getString("sql_cnt_template"));
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle") || dmd.getDatabaseProductName().equalsIgnoreCase("Tibero")) {
			return ("select count(*) from (" + sql + ")");
		} else {
			return ("select count(*) from (" + sql + ") as _");
		}
	}
	protected static String getSeqSql(String name, Record info, DatabaseMetaData dmd) throws SQLException {
		ConnectionFactory factory = getConnectionFactory(info);
		if(factory != null) {
			String seqSQL = factory.getSeqSql(name, info, dmd);
			if(seqSQL != null && !seqSQL.trim().equals("")) {
				return seqSQL;
			}
		} else if(info != null && info.hasKey("sql_sequence_template")) {
			Record p = new Record();
			p.put("name", name);
			Record result = FileHelper.getFilePath(p, info.getString("sql_sequence_template"));
			if(result != null && !result.isEmpty()) {
				String n = result.getString("_system.filepath");
				if(n != null) {
					return n;
				}
			}
		}
		if(dmd.getDatabaseProductName().equalsIgnoreCase("Oracle") || dmd.getDatabaseProductName().equalsIgnoreCase("Tibero")) {
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