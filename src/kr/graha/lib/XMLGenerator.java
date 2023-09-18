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
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.security.NoSuchProviderException;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

import kr.graha.helper.LOG;
import kr.graha.helper.XML;
import kr.graha.helper.DB;
import kr.graha.helper.STR;

import java.util.List;

/**
 * Graha(그라하) XML 생성기

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class XMLGenerator {
	
	Element _query;
	Record _params;
	Connection _con;
	File _config;
	
	XPathFactory _factory;
	XPath _xpath;
	XPathExpression _expr;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	HttpServletRequest request;
	HttpServletResponse response;
	
	XMLTag _tag;
	Record _info;
	boolean isError = false;
	boolean isQueryAndResultSet = false;
	DatabaseMetaData dmd = null;
	
	public XMLGenerator(
		Element query, 
		Record params, 
		Connection con, 
		File config, 
		HttpServletRequest request, 
		HttpServletResponse response, 
		Record info
	) throws SQLException {
		
		this._query = query;
		this._params = params;
		if(con != null) {
			this._con = con;
			this.dmd = this._con.getMetaData();
		}
		this._config = config;
		
		this.request = request;
		this.response = response;
		
		this._factory = XPathFactory.newInstance();
		this._xpath = this._factory.newXPath();

		this._expr = null;
		
		this._tag = new XMLTag(
			this._query.getAttribute("output"), 
			this._query.getAttribute("uc"), 
			request
		);
		
		this._info = info;
		LOG.setLogLevel(logger);
	}
	public void setConnection(Connection con) throws SQLException {
		if(con != null) {
			this._con = con;
			this.dmd = this._con.getMetaData();
		}
	}
	public Buffer execute() throws Exception {
		Buffer sb = new Buffer();
		sb.append(this.before());
		if(XML.existsIgnoreCaseAttrValue(this._query, "funcType", new String[]{"list", "listAll", "detail", "user"})) {
			sb.append(this.list());
		} else if(
			XML.equalsIgnoreCaseAttrValue(this._query, "funcType", "delete")
			&& this._params.equals("header.method", "POST")
		) {
			sb.append(this.delete());
		} else if(
			XML.equalsIgnoreCaseAttrValue(this._query, "funcType", "insert")
			&& this._params.equals("header.method", "GET")
		) {
			sb.append(this.update());
		} else if(
			XML.equalsIgnoreCaseAttrValue(this._query, "funcType", "insert")
			&& this._params.equals("header.method", "POST")
		) {
			sb.append(this.insert());
		} else if(
			XML.existsIgnoreCaseAttrValue(this._query, "funcType", new String[]{"query", "report"})
			&& (
				this._params.equals("header.method", "POST") ||
				(XML.equalsIgnoreCaseAttrValue(this._query, "allow", "get") && this._params.equals("header.method", "GET"))
			)
		) {
			Buffer sb_tmp = new Buffer();
			if(XML.equalsIgnoreCaseAttrValue(this._query, "funcType", "query")) {
				sb_tmp.append(this.query());
			} else if(XML.equalsIgnoreCaseAttrValue(this._query, "funcType", "report")) {
				Buffer sb_report_tmp = new Buffer();
				sb_report_tmp.append(sb);
				sb_report_tmp.append(this.list());
				sb_report_tmp.append(this.after());
				sb_tmp.append(this.report(sb_report_tmp));
			}
			if(this.isError() || this.isQueryAndResultSet) {
				sb.init();
				sb.append(this.before());
				sb.append(sb_tmp);
			} else {
				sb.append(sb_tmp);
			}
			sb_tmp.clear();
			sb_tmp = null;
		}
		return sb;
	}
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) { logger.finest(sql); }
		return this._con.prepareStatement(sql);
	}
	private CallableStatement prepareCall(String sql) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) { logger.finest(sql); }
		return this._con.prepareCall(sql);
	}
	public void processor(boolean before) throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, NoSuchProviderException, NoSuchMethodException, InvocationTargetException {
		if(before) {
			this._expr = this._xpath.compile("processors/processor[@before = 'true']");
		} else {
			this._expr = this._xpath.compile("processors/processor[@after = 'true']");
		}
		NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		
		for(int y = 0; y < commands.getLength(); y++) {
			Element command = (Element)commands.item(y);
			if(command.hasAttribute("method") && command.getAttribute("method") != null) {
				if(!(command.getAttribute("method")).equals(this._params.getString("header.method"))) {
					continue;
				}
			}
			if(XML.validAttrValue(command, "cond")) {
				if(!(AuthParser.auth(command.getAttribute("cond"), this._params))) {
					continue;
				}
			}
			if(XML.equalsIgnoreCaseAttrValue(command, "type", "native")) {
				Processor processor = (Processor) Class.forName(command.getAttribute("class")).getConstructor().newInstance();
				processor.execute(request, response, this._params, this._con);
			} else if(XML.existsIgnoreCaseAttrValue(command, "type", new String[]{"query", "plsql"})) {
				PreparedStatement stmt = null;
				CallableStatement cstmt = null;
				try {
						this._expr = this._xpath.compile("sql");
						
						Element sql = (Element)this._expr.evaluate(command, XPathConstants.NODE);
						String s = DBHelper.getSql(sql, this._params);
						if(command.getAttribute("type").equals("query")) {
							stmt = this.prepareStatement(s);
						} else {
							cstmt = this.prepareCall(s);
						}
						
						java.util.Map<String, Encryptor> encryptor = getEncryptor(command);
						
						this._expr = this._xpath.compile("params/param");
						NodeList param = (NodeList)this._expr.evaluate(command, XPathConstants.NODESET);
						int index = 1;
						for(int x = 0; x < param.getLength(); x++) {
							Element p = (Element)param.item(x);
							if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), this._params)) {
								if(command.getAttribute("type").equals("query")) {
									this.bind(stmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
								} else {
									this.bind(cstmt, p.getAttribute("datatype"),index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
								}
								index++;
							}
						}
						if(command.getAttribute("type").equals("query")) {
							stmt.executeUpdate();
							DB.close(stmt);
						} else {
							cstmt.executeUpdate();
							DB.close(cstmt);
						}
				} catch (XPathExpressionException | SQLException | NoSuchProviderException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
					throw e;
				} finally {
					DB.close(stmt);
					DB.close(cstmt);
				}
			}
		}
	}
	private Buffer report(Buffer xml) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Buffer sb = new Buffer();
		Reporter reporter = (Reporter) Class.forName(this._query.getAttribute("class")).getConstructor().newInstance();
		reporter.execute(request, response, this._params, xml, this._con);
		if(this._params.containsKey("error.error")) {
			this.isError = true;
		}
		return sb;
	}
	private Buffer query() throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, NoSuchProviderException, NoSuchMethodException, InvocationTargetException {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		int totalUpdateCount = 0;
		this._expr = this._xpath.compile("commands/command");
		NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		sb.append(this._tag.tag("rows", null, true));
		for(int q = 0; q < commands.getLength(); q++) {
			Element command = (Element)commands.item(q);
			if(XML.validAttrValue(command, "cond")) {
				if(!(AuthParser.auth(command.getAttribute("cond"), this._params))) {
					continue;
				}
			}
			if(command.hasAttribute("type") && command.getAttribute("type") != null && command.getAttribute("type").equals("native")) {
				Processor processor = (Processor) Class.forName(command.getAttribute("class")).getConstructor().newInstance();
				processor.execute(request, response, this._params, this._con);
			} else {
				try {
					this._expr = this._xpath.compile("sql");
					
					Element sql = (Element)this._expr.evaluate(command, XPathConstants.NODE);
					String s = DBHelper.getSql(sql, this._params);
					if(command.hasAttribute("type") && command.getAttribute("type") != null && command.getAttribute("type").equals("plsql")) {
						cstmt = this.prepareCall(s);
					} else {
						stmt = this.prepareStatement(s);
						
					}
					
					java.util.Map<String, Encryptor> encryptor = getEncryptor(command);
					
					this._expr = this._xpath.compile("params/param");
					NodeList param = (NodeList)this._expr.evaluate(command, XPathConstants.NODESET);
					int index = 1;
					for(int x = 0; x < param.getLength(); x++) {
						Element p = (Element)param.item(x);
						if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), this._params)) {
							if(command.hasAttribute("type") && command.getAttribute("type") != null && !command.getAttribute("type").equals("") && command.getAttribute("type").equals("plsql")) {
								this.bind(cstmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
							} else {
								this.bind(stmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
							}
							index++;
						}
					}
					int result = 0;
					boolean isResultSet = false;
					if(command.hasAttribute("type") && command.getAttribute("type") != null && command.getAttribute("type").equals("plsql")) {
						cstmt.executeUpdate();
						result = cstmt.getUpdateCount();
						DB.close(cstmt);
					} else {
						if(stmt.execute()) {
							isResultSet = true;
							this.isQueryAndResultSet = true;
							rs = stmt.getResultSet();
							java.util.Map<String, String> encrypted = null;
							if(encryptor != null) {
								encrypted = getEncrypted(command, "decrypt/column");
							}
							java.util.Map<String, String> pattern = getRattern(command, "pattern/column");
							sb.appendL(this._tag.tag("rows", command.getAttribute("name"), true));
							index = DBHelper.getXMLStringFromResultSet(
								rs,
								this._tag,
								command,
								this._query,
								encryptor,
								encrypted,
								this._params,
								pattern,
								this.dmd,
								this._xpath,
								this._expr,
								sb
							);
							if(XML.validAttrValue(command, "name")) {
								this._params.put("query.row." + command.getAttribute("name") + ".count", index);
							}
							sb.appendL(this._tag.tag("rows", command.getAttribute("name"), false));
							DB.close(rs);
						} else {
							result = stmt.getUpdateCount();
						}
						DB.close(stmt);
					}
					if(!isResultSet && XML.validAttrValue(command, "name")) {
						sb.append(this._tag.tag("row", command.getAttribute("name"), true));
						sb.append(this._tag.tag("row", "rowcount", null, true) + result + this._tag.tag("row", "rowcount", null, false));
						sb.append(this._tag.tag("row", command.getAttribute("name"), false));
						this._params.put("query.row." + command.getAttribute("name") + ".count", result);
					}
					totalUpdateCount += result;
				} catch (XPathExpressionException | SQLException | NoSuchProviderException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
					throw e;
				} finally {
					DB.close(rs);
					DB.close(stmt);
					DB.close(cstmt);
				}
			}
		}
		this._params.put("query.row.total_affected_count", totalUpdateCount);
		sb.append(this._tag.tag("rows", null, false));
		if(this._params.containsKey("error.error")) {
			this.isError = true;
		}
		return sb;
	}
	
	private Buffer list() throws XPathExpressionException, SQLException, NoSuchProviderException, IOException  {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmtCount = null;
		ResultSet rsCount = null;
		int totalFetchCount = 0;
		try {
			this._expr = this._xpath.compile("commands/command");
			NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(commands.getLength() == 0 && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/commands/command");
				commands = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			}
			
			for(int y = 0; y < commands.getLength(); y++) {
				if(
					y > 0 && 
					!this._query.getAttribute("funcType").equals("detail") && 
					!this._query.getAttribute("funcType").equals("report") &&
					!this._query.getAttribute("funcType").equals("user")
				) {
					break;
				}
				Element command = (Element)commands.item(y);
				
				int pageSize = 0;
				int pageGroupSize = 0;
				int page = 0;

				this._expr = this._xpath.compile("params/param");
				NodeList param = (NodeList)this._expr.evaluate(command, XPathConstants.NODESET);
				Element sql = null;
				Element sqlCnt = null;
				NodeList list = command.getChildNodes();
				for(int i = 0; i < list.getLength(); i++) {
					org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(i);
					if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						if((n.getNodeName()).equals("sql")) {
							sql = (Element)n;
						} else if((n.getNodeName()).equals("sql_cnt")) {
							sqlCnt = (Element)n;
						}
					}
				}
				
				String s = DBHelper.getSql(sql, this._params);

				if(this._query.getAttribute("funcType").equals("list")) {
					pageSize = Integer.parseInt(sql.getAttribute("pageSize"));
					pageGroupSize = Integer.parseInt(sql.getAttribute("pageGroupSize"));
					if(this._params.getInt("param.page") == 0) {
						this._params.put("param.page", 1);
					}
					page = this._params.getInt("param.page");
					String s1 = DBHelper.getSql(sqlCnt, this._params);
					if(sqlCnt == null) {
						stmtCount = this.prepareStatement(DBHelper.getCountSql(s, this._info, this.dmd));
					} else {
						stmtCount = this.prepareStatement(s1);
					}
				}
				
				if(this._query.getAttribute("funcType").equals("list")) {
					stmt = this.prepareStatement(DBHelper.getListSql(s, this._info, this.dmd));
				} else {
					stmt = this.prepareStatement(s);
				}
				java.util.Map<String, Encryptor> encryptor = getEncryptor(command);
				
				int index = 1;
				for(int i = 0; i < param.getLength(); i++) {
					Element p = (Element)param.item(i);
					if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), this._params)) {
						this.bind(stmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
						if(this._query.getAttribute("funcType").equals("list")) {
							this.bind(stmtCount, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);	
						}
						index++;
					}
				}
				if(this._query.getAttribute("funcType").equals("list")) {
					this.setInt(stmt, index + 0, pageSize);
					this.setInt(stmt, index + 1, (page - 1) * pageSize);
				}
				rs = stmt.executeQuery();
				
				java.util.Map<String, String> encrypted = null;
				if(encryptor != null) {
					encrypted = getEncrypted(command, "decrypt/column");
				}
				java.util.Map<String, String> pattern = getRattern(command, "pattern/column");

				sb.appendL(this._tag.tag("rows", command.getAttribute("name"), true));
				
				index = DBHelper.getXMLStringFromResultSet(
					rs,
					this._tag,
					command,
					this._query,
					encryptor,
					encrypted,
					this._params,
					pattern,
					this.dmd,
					this._xpath,
					this._expr,
					sb
				);
				totalFetchCount += index;
				if(XML.validAttrValue(command, "name")) {
					this._params.put("query.row." + command.getAttribute("name") + ".count", index);
				}
				DB.close(rs);
				DB.close(stmt);
				sb.appendL(this._tag.tag("rows", command.getAttribute("name"), false));
				
				if(this._query.getAttribute("funcType").equals("list")) {
					rsCount = stmtCount.executeQuery();
				
					if(rsCount.next()) {
						this._params.put("query.total.count", rsCount.getInt(1));
						if(commands.getLength() == 1) {
							sb.appendL(this._tag.tag("pages", null, true));
						} else {
							sb.appendL(this._tag.tag("pages", command.getAttribute("name"), true));
						}
						
						int totalPage = (int)(Math.ceil((double)rsCount.getInt(1)/(double)pageSize));
						if(totalPage > 0) {
							
							if(((Math.floor(((double)page-1)/(double)pageGroupSize) * (double)pageGroupSize) + 1) >= pageGroupSize) {
								sb.append(this._tag.tag("page", null, true));
								sb.append(this._tag.tag("page", "no", null, true));
								sb.append((int)((Math.floor(((double)page-1)/(double)pageGroupSize) * (double)pageGroupSize) + 1) - 1);
								sb.append(this._tag.tag("page", "no", null, false));
								sb.append(this._tag.tag("page", "text", null, true) + "◀" + this._tag.tag("page", "text", null, false));
								sb.appendL(this._tag.tag("page", null, false));
							}
							for(int i = (int)((Math.floor(((double)page-1)/(double)pageGroupSize) * (double)pageGroupSize) + 1); i <= Math.min(totalPage, ((Math.floor((page-1)/pageGroupSize) * pageGroupSize) + pageGroupSize)); i++) {
								sb.append(this._tag.tag("page", null, true));
								sb.append(this._tag.tag("page", "no", null, true));
								sb.append(i);
								sb.append(this._tag.tag("page", "no", null, false));
								sb.append(this._tag.tag("page", "text", null, true));
								sb.append(i);
								sb.append(this._tag.tag("page", "text", null, false));
								sb.appendL(this._tag.tag("page", null, false));
							}
							
							if(totalPage >= ((Math.floor((page-1)/pageGroupSize) * pageGroupSize) + pageGroupSize + 1)) {
								sb.append(this._tag.tag("page", null, true));
								sb.append(this._tag.tag("page", "no", null, true));
								sb.append((int)((Math.floor((page-1)/pageGroupSize) * pageGroupSize) + pageGroupSize + 1));
								sb.append(this._tag.tag("page", "no", null, false));
								sb.append(this._tag.tag("page", "text", null, true) + "▶" + this._tag.tag("page", "text", null, false));
								sb.appendL(this._tag.tag("page", null, false));
							}
							
						}
						DB.close(rsCount);
						sb.appendL(this._tag.tag("pages", null, false));
					}
					DB.close(stmtCount);
				}
			}
			this._params.put("query.row.total_fetch_count", totalFetchCount);
			this._expr = this._xpath.compile("files/file");
			NodeList files = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(
				files.getLength() > 0 
				&& totalFetchCount > 0
				&& this._query.getAttribute("funcType").equals("detail")
				&& FileHelper.isAllow(this._query, this._params)
			) {
				for(int y = 0; y < files.getLength(); y++) {
					Element file = (Element)files.item(y);
					if(file.hasAttribute("name")) {
						sb.append(this._tag.tag("files", file.getAttribute("name"), true));
					} else {
						sb.append(this._tag.tag("files", null, true));
					}
					Record result = FileHelper.getFilePath(this._params, file.getAttribute("path"));
					if(result != null && !result.isEmpty()) {
						
						String filePath = result.getString("_system.filepath");
						if(filePath != null) {
							if(Files.exists(Paths.get(filePath)) && Files.isDirectory(Paths.get(filePath))) {
								DirectoryStream<Path> stream = null;
								try {
									stream = Files.newDirectoryStream(Paths.get(filePath));
									for(Path path : stream) {
										if(Files.isRegularFile(path)) {
											sb.append(this._tag.tag("file", null, true));
											sb.append(this._tag.tag("file", "name", null, true) + "<![CDATA[" + FileHelper.decodeFileName(path.toUri()) + "]]>" + this._tag.tag("file", "name", null, false));
											sb.append(this._tag.tag("file", "name2", null, true) + "<![CDATA[" + FileHelper.escapeFileName(path.toUri()) + "]]>" + this._tag.tag("file", "name2", null, false));
											sb.append(this._tag.tag("file", "length", null, true) + Files.size(path) + this._tag.tag("file", "length", null, false));
											sb.append(this._tag.tag("file", "lastModified", null, true) + Files.getLastModifiedTime(path).toMillis() + this._tag.tag("file", "lastModified", null, false));
											sb.append(this._tag.tag("file", null, false));
										}
									}
									stream.close();
									stream = null;
								} catch(IOException ex) {
									if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(ex)); 	}
									throw ex;
								} finally {
									if(stream != null) {
										try {
											stream.close();
											stream = null;
										} catch(IOException e) {
										}
									}
								}
							}
						}
					
						sb.append(this._tag.tag("files", null, false));
						if(file.hasAttribute("name")) {
							sb.append(this._tag.tag("fileparams", file.getAttribute("name"), true));
						} else {
							sb.append(this._tag.tag("fileparams", null, true));
						}
							
						Iterator<String> it = result.keySet().iterator();
						while(it.hasNext()) {
							String key = (String)it.next();
							if(key != null && key.equals("_system.filepath")) {
								continue;
							}
							if(key != null && key.startsWith("prop.")) {
								if(key.endsWith(".public") || !this._params.equals(key + ".public", "true")) {
									continue;
								}
							}
							sb.append(this._tag.tag("param", null, true));
							sb.append(this._tag.tag("param", "key", null, true) + key + this._tag.tag("param", "key", null, false));
							sb.append(this._tag.tag("param", "value", null, true) + result.get(key) + this._tag.tag("param", "value", null, false));
							sb.append(this._tag.tag("param", null, false));
						}
						sb.append(this._tag.tag("fileparams", null, false));
					}
				}
			}
		} catch (XPathExpressionException | SQLException | NoSuchProviderException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} finally {
			DB.close(rs);
			DB.close(rsCount);
			DB.close(stmt);
			DB.close(stmtCount);
		}
		return sb;
	}
	
	private Buffer delete() throws XPathExpressionException, SQLException, NoSuchProviderException {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		
		String sql = "";
		int totalUpdateCount = 0;
		try {
			this._expr = this._xpath.compile("tables/table");
			NodeList table = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			
			sb.append(this._tag.tag("rows", null, true));
			for(int i = 0; i < table.getLength(); i++) {
				Element p = (Element)table.item(i);
				sql = "delete from " + p.getAttribute("tableName");
				
				this._expr = this._xpath.compile("column");
				NodeList column = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
				for(int x = 0; x < column.getLength(); x++) {
					Element c = (Element)column.item(x);
					if(!this._params.hasKey(c.getAttribute("value"))) {
						throw new ParsingException();
					}
					if(x > 0) {
						sql += " and ";
					} else {
						sql += " where ";
					}
					sql += c.getAttribute("name") + " = ? ";
				}
				this._expr = this._xpath.compile("where/sql");
				Element where = (Element)this._expr.evaluate(p, XPathConstants.NODE);
				if(where != null) {
					if(!((Element)where.getParentNode()).hasAttribute("method") || !this._params.hasKey("header.method") || (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))) {
						sql += " and ";
						sql += DBHelper.getSql(where, this._params);
					}
				}
				
				
				java.util.Map<String, Encryptor> encryptor = getEncryptor(p);
				
				stmt = this.prepareStatement(sql);
				int index = 1;
				for(int x = 0; x < column.getLength(); x++) {
					Element c = (Element)column.item(x);
					this.bind(stmt, c.getAttribute("datatype"), x + 1, new String[] {c.getAttribute("value")}, -1, c.getAttribute("default"), c.getAttribute("pattern"), null, null, encryptor, c.getAttribute("encrypt"), null);
					index++;
				}
				if(where != null) {
					if(!((Element)where.getParentNode()).hasAttribute("method") || !this._params.hasKey("header.method") || (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))) {
						this._expr = this._xpath.compile("where/params/param");
						NodeList param = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						for(int x = 0; x < param.getLength(); x++) {
							Element pp = (Element)param.item(x);
							if(!pp.hasAttribute("cond") || AuthParser.auth(pp.getAttribute("cond"), this._params)) {
								this.bind(stmt, pp.getAttribute("datatype"), index, new String[] {pp.getAttribute("value")}, -1, pp.getAttribute("default"), pp.getAttribute("pattern"), null, null, encryptor, pp.getAttribute("encrypt"), null);
								index++;
							}
						}
					}
				}
				stmt.executeUpdate();
				int result = stmt.getUpdateCount();
				totalUpdateCount += result;
				DB.close(stmt);
				if(XML.validAttrValue(p, "name")) {
					sb.append(this._tag.tag("row", p.getAttribute("tableName"), true));
					sb.append(this._tag.tag("row", "rowcount", null, true) + result + this._tag.tag("row", "rowcount", null, false));
					sb.append(this._tag.tag("row", p.getAttribute("tableName"), false));
					this._params.put("query.row." + p.getAttribute("tableName") + ".count", result);
				}
			}
			this._params.put("query.row.total_delete_count", totalUpdateCount);
			sb.append(this._tag.tag("rows", null, false));
			this._expr = this._xpath.compile("files/file");
			NodeList files = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(files.getLength() > 0 && totalUpdateCount > 0 && FileHelper.isAllow(this._query, this._params)) {
				for(int y = 0; y < files.getLength(); y++) {
					Element file = (Element)files.item(y);
					if(file.hasAttribute("name")) {
						sb.append(this._tag.tag("files", file.getAttribute("name"), true));
					} else {
						sb.append(this._tag.tag("files", null, true));
					}
					int index = 0;
					Record result = FileHelper.getFilePath(this._params, file);
					if(result != null && !result.isEmpty()) {
						if(result.isArray("_system.filepath")) {
							List paths = result.getArray("_system.filepath");
							if(paths != null) {
								for(int x = 0; x < paths.size(); x++) {
									Object path = paths.get(x);
									if(path != null && path instanceof String) {
										File dir = new File((String)path);
										if(dir.exists() && dir.isDirectory()) {
											File[] ff = dir.listFiles();
											if(ff != null) {
												for (File f : ff) {
													f.delete();
													if(logger.isLoggable(Level.FINER)) { logger.finer("delete file (" + f.getPath() + ")"); }
													if(x == 0) {
														index++;
													}
												}
											}
											dir.delete();
											if(logger.isLoggable(Level.FINER)) { logger.finer("delete directory (" + dir.getPath() + ")"); }
										} else {
											if(logger.isLoggable(Level.WARNING)) { logger.warning("file paths is not exists (path = " + path + "), (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
										}
									} else {
										if(logger.isLoggable(Level.WARNING)) { logger.warning("file paths is not string (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
									}
								}
							} else {
								if(logger.isLoggable(Level.WARNING)) { logger.warning("file paths is null (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
							}
						} else {
							String filePath = result.getString("_system.filepath");
							if(filePath != null) {
								File dir = new File(filePath);
								if(dir.exists() && dir.isDirectory()) {
									File[] ff = dir.listFiles();
									if(ff != null) {
										for (File f : ff) {
											f.delete();
											if(logger.isLoggable(Level.FINER)) { logger.finer("delete file (" + f.getPath() + ")"); }
											index++;
										}
									}
									dir.delete();
									if(logger.isLoggable(Level.FINER)) { logger.finer("delete directory (" + dir.getPath() + ")"); }
								} else {
									if(logger.isLoggable(Level.WARNING)) { logger.warning("file path is not exists (path = " + filePath + "), (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
								}
							} else {
								if(logger.isLoggable(Level.WARNING)) { logger.warning("file path is null (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
							}
						}
					} else {
						if(logger.isLoggable(Level.WARNING)) { logger.warning("fail file directory parsing (path = " + file.getAttribute("path") + "), (backup=" + file.getAttribute("backup") + ")"); }
					}
					sb.append(this._tag.tag("files", "rowcount", null, true) + index + this._tag.tag("files", "rowcount", null, false));
					sb.append(this._tag.tag("files", null, false));
				}
			}
		} catch (XPathExpressionException | SQLException | NoSuchProviderException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} finally {
			DB.close(stmt);
		}
		return sb;
	}
	
	private Buffer update() throws XPathExpressionException, SQLException, NoSuchProviderException, IOException {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int totalFetchCount = 0;
		boolean isNew = false;
		try {
			this._expr = this._xpath.compile("tables/table");
			NodeList table = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			
			for(int i = 0; i < table.getLength(); i++) {
				Element p = (Element)table.item(i);
				NodeList column = null;
				
				this._expr = this._xpath.compile("column[@foreign = 'true']");
				column = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
				if(column.getLength() > 0) {
					continue;
				}
				
				this._expr = this._xpath.compile("column[@primary = 'true']");
				column = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
				
				for(int x = 0; x < column.getLength(); x++) {
					Element c = (Element)column.item(x);
					if(!this._params.hasKey(c.getAttribute("value"))) {
						isNew = true;
					}
				}
			}
			int index = 0;
			for(int i = 0; i < table.getLength(); i++) {
				Element p = (Element)table.item(i);
				sb.appendL(this._tag.tag("rows", p.getAttribute("name"), true));
				if(!isNew ) {
					this._expr = this._xpath.compile("column");
					NodeList column = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
					
					String sql = "select ";
					index = 0;
					for(int x = 0; x < column.getLength(); x++) {
						Element c = (Element)column.item(x);
						if(c.getAttribute("value").startsWith("param.") || XML.trueAttrValue(c, "select")) {
							if(index > 0) {
								sql += ", ";
							}
							index++;
							sql += c.getAttribute("name");
						}
					}
					sql += " from " + p.getAttribute("tableName");
					index = 0;
					NodeList col = null;
						this._expr = this._xpath.compile("column[@foreign='true']");
						col = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						if(col.getLength() == 0) {
							this._expr = this._xpath.compile("column[@primary='true']");
							col = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						}
					for(int x = 0; x < col.getLength(); x++) {
						Element c = (Element)col.item(x);
						if(index == 0) {
							sql += " where ";
						} else {
							sql += " and ";
						}
						sql += c.getAttribute("name") + " = ?";
						index++;
					}
					
					this._expr = this._xpath.compile("where/sql");
					Element where = (Element)this._expr.evaluate(p, XPathConstants.NODE);
					if(where != null) {
						if(!((Element)where.getParentNode()).hasAttribute("method") || !this._params.hasKey("header.method") || (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))) {
							sql += " and ";
							sql += DBHelper.getSql(where, this._params);
						}
					}
					
					this._expr = this._xpath.compile("order");
					Element order = (Element)this._expr.evaluate(p, XPathConstants.NODE);
					if(order != null) {
						sql += " order by ";
						sql += order.getFirstChild().getNodeValue();
					} else {
						this._expr = this._xpath.compile("column[@primary='true']");
						NodeList pk = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						if(pk.getLength() > 0) {
							index = 0;
							sql += " order by ";
							for(int x = 0; x < pk.getLength(); x++) {
								Element c = (Element)pk.item(x);
								if(index > 0) {
									sql += ", ";
								}
								sql += c.getAttribute("name") + "";
								index++;
							}
						}
					}

					stmt = this.prepareStatement(sql);
					index = 1;
					java.util.Map<String, Encryptor> encryptor = getEncryptor(p);
					for(int x = 0; x < col.getLength(); x++) {
						Element c = (Element)col.item(x);
						this.bind(stmt, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, -1, c.getAttribute("default"), c.getAttribute("pattern"), null, null, encryptor, c.getAttribute("encrypt"), null);
						index++;
					}
					if(where != null) {
						if(!((Element)where.getParentNode()).hasAttribute("method") || !this._params.hasKey("header.method") || (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))) {
							this._expr = this._xpath.compile("where/params/param");
							NodeList param = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
							for(int x = 0; x < param.getLength(); x++) {
								Element pp = (Element)param.item(x);
								if(!pp.hasAttribute("cond") || AuthParser.auth(pp.getAttribute("cond"), this._params)) {
									this.bind(stmt, pp.getAttribute("datatype"), index, new String[] {pp.getAttribute("value")}, -1, pp.getAttribute("default"), pp.getAttribute("pattern"), null, null, encryptor, pp.getAttribute("encrypt"), null);
									index++;
								}
							}
						}
					}
					java.util.Map<String, String> encrypted = null;
					if(encryptor != null) {
						encrypted = getEncrypted(p, "column[@encrypt]");
					}
					java.util.Map<String, String> pattern = getRattern(p, "column[@pattern]");
					
					rs = stmt.executeQuery();
					index = DBHelper.getXMLStringFromResultSet(
						rs,
						this._tag,
						p,
						this._query,
						encryptor,
						encrypted,
						this._params,
						pattern,
						this.dmd,
						this._xpath,
						this._expr,
						sb
					);
					totalFetchCount += index;
					if(XML.validAttrValue(p, "name")) {
						this._params.put("query.row." + p.getAttribute("name") + ".count", index);
					}
					DB.close(rs);
					DB.close(stmt);
				}
				if(p.hasAttribute("total") && Integer.parseInt(p.getAttribute("total")) > 0) {
					for(int q = index; q < Integer.parseInt(p.getAttribute("total")); q++) {
						sb.append(this._tag.tag("row", null, true));
						sb.appendL(this._tag.tag("row", null, false));
					}
				} else if(p.hasAttribute("append") && Integer.parseInt(p.getAttribute("append")) > 0) {
					for(int q = 0; q < Integer.parseInt(p.getAttribute("append")); q++) {
						sb.append(this._tag.tag("row", null, true));
						sb.appendL(this._tag.tag("row", null, false));
					}
				}
				sb.appendL(this._tag.tag("rows", null, false));
			}
			if(!isNew ) {
				this._params.put("query.row.total_fetch_count", totalFetchCount);
			}
			this._expr = this._xpath.compile("files/file");
			NodeList files = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(files.getLength() > 0 && (totalFetchCount > 0 || isNew) && FileHelper.isAllow(this._query, this._params)) {
				for(int y = 0; y < files.getLength(); y++) {
					Element file = (Element)files.item(y);
					if(file.hasAttribute("name")) {
						sb.appendL(this._tag.tag("files", file.getAttribute("name"), true));
					} else {
						sb.appendL(this._tag.tag("files", null, true));
					}
					index = 0;
					Record result = FileHelper.getFilePath(this._params, file.getAttribute("path"));
					if(!isNew ) {
						if(result != null && !result.isEmpty()) {
							String filePath = result.getString("_system.filepath");
							if(filePath != null) {
								if(Files.exists(Paths.get(filePath)) && Files.isDirectory(Paths.get(filePath))) {
									DirectoryStream<Path> stream = null;
									try {
										stream = Files.newDirectoryStream(Paths.get(filePath));
										for(Path path : stream) {
											if(Files.isRegularFile(path)) {
												sb.append(this._tag.tag("file", null, true));
												sb.append(this._tag.tag("file", "name", null, true) + "<![CDATA[" + FileHelper.decodeFileName(path.toUri()) + "]]>" + this._tag.tag("file", "name", null, false));
												sb.append(this._tag.tag("file", "name2", null, true) + "<![CDATA[" + FileHelper.escapeFileName(path.toUri()) + "]]>" + this._tag.tag("file", "name2", null, false));
												sb.append(this._tag.tag("file", "length", null, true) + Files.size(path) + this._tag.tag("file", "length", null, false));
												sb.append(this._tag.tag("file", "lastModified", null, true) + Files.getLastModifiedTime(path).toMillis() + this._tag.tag("file", "lastModified", null, false));
												sb.append(this._tag.tag("file", null, false));
												index++;
											}
										}
										stream.close();
										stream = null;
									} catch(IOException ex) {
										if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(ex)); 	}
										throw ex;
									} finally {
										if(stream != null) {
											try {
												stream.close();
												stream = null;
											} catch(IOException e) {
											}
										}
									}
								}
							}
						}
					}
					if(file.hasAttribute("total") && Integer.parseInt(file.getAttribute("total")) > 0) {
						for(int q = index; q < Integer.parseInt(file.getAttribute("total")); q++) {
							sb.append(this._tag.tag("file", null, true));
							sb.appendL(this._tag.tag("file", null, false));
						}
					} else if(file.hasAttribute("append") && Integer.parseInt(file.getAttribute("append")) > 0) {
						for(int q = 0; q < Integer.parseInt(file.getAttribute("append")); q++) {
							sb.append(this._tag.tag("file", null, true));
							sb.appendL(this._tag.tag("file", null, false));
						}
					}
					sb.appendL(this._tag.tag("files", null, false));
					if(file.hasAttribute("name")) {
						sb.appendL(this._tag.tag("fileparams", file.getAttribute("name"), true));
					} else {
						sb.appendL(this._tag.tag("fileparams", null, true));
					}
					if(result != null && !result.isEmpty()) {
						Iterator<String> it = result.keySet().iterator();
						while(it.hasNext()) {
							String key = (String)it.next();
							if(key != null && key.equals("_system.filepath")) {
								continue;
							}
							if(key != null && key.startsWith("prop.")) {
								if(key.endsWith(".public") || !this._params.equals(key + ".public", "true")) {
									continue;
								}
							}
							sb.appendL(this._tag.tag("param", null, true));
							sb.appendL(this._tag.tag("param", "key", null, true) + key + this._tag.tag("param", "key", null, false));
							sb.appendL(this._tag.tag("param", "value", null, true) + result.get(key) + this._tag.tag("param", "value", null, false));
							sb.appendL(this._tag.tag("param", null, false));
						}
					}
					sb.appendL(this._tag.tag("fileparams", null, false));
				}
			}
			
		} catch (XPathExpressionException | SQLException | NoSuchProviderException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} finally {
			DB.close(rs);
			DB.close(stmt);
		}
		return sb;
	}
	

	private Buffer insert() throws XPathExpressionException, SQLException, NoSuchProviderException {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		PreparedStatement stmtInsert = null;
		PreparedStatement stmtUpdate = null;
		PreparedStatement stmtDelete = null;
		ResultSet rs = null;
		String sqlInsert = "";
		String sqlUpdate = "";
		String sqlDelete = "";
		int totalUpdateCount = 0;
		try {
			this._expr = this._xpath.compile("tables/table");
			NodeList table = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			for(int a = 0; a < 2; a++) {
				for(int i = 0; i < table.getLength(); i++) {
					Element p = (Element)table.item(i);
					this._expr = this._xpath.compile("column[@foreign='true']");
					NodeList col = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
					if(a == 0 && col.getLength() > 0) {
						continue;
					} else if(a == 1 && col.getLength() == 0) {
						continue;
					}
					if(table.getLength() > 0) {
						sb.append(this._tag.tag("rows", p.getAttribute("name"), true));
					} else {
						sb.append(this._tag.tag("rows", null, true));
						
					}
					
					sqlInsert = "insert into " + p.getAttribute("tableName");
					sqlUpdate = "update " + p.getAttribute("tableName") + "";
					sqlDelete = "delete from " + p.getAttribute("tableName") + "";

					this._expr = this._xpath.compile("column");
					NodeList column = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);

					int index = 0;
					for(int x = 0; x < column.getLength(); x++) {
						Element c = (Element)column.item(x);
						if(
							XML.trueAttrValue(c, "primary") &&
							c.hasAttribute("insert") && ((String)c.getAttribute("insert")).equals("generate")
						) {
							continue;
						}
						if(index > 0) {
							sqlInsert += ", ";
						} else {
							sqlInsert += " (";
						}
						sqlInsert += c.getAttribute("name");
						index++;
					}
					index = 0;
					for(int x = 0; x < column.getLength(); x++) {
						Element c = (Element)column.item(x);
						if(
							(
								((String)c.getAttribute("only")).equals("insert") || 
								XML.trueAttrValue(c, "primary")
							)
						) {
							continue;
						}
						if(index > 0) {
							sqlUpdate += ", ";
						} else {
							sqlUpdate += " set ";
						}
						if(
							((String)c.getAttribute("value")).startsWith("sql.")
						) {
							sqlUpdate += c.getAttribute("name") + " = " + ((String)c.getAttribute("value")).substring(4) + "";
						} else if(
							c.hasAttribute("default") 
							&& ((String)c.getAttribute("default")).startsWith("sql.")
							&& !this._params.hasKey((String)c.getAttribute("value"))
						) {
							sqlUpdate += c.getAttribute("name") + " = " + ((String)c.getAttribute("default")).substring(4) + "";
						} else {
							sqlUpdate += c.getAttribute("name") + " = ?";
						}
						index++;
					}
					index = 0;
					for(int x = 0; x < column.getLength(); x++) {
						Element c = (Element)column.item(x);
						if(
							XML.trueAttrValue(c, "primary") &&
							c.hasAttribute("insert") && ((String)c.getAttribute("insert")).equals("generate")
						) {
							continue;
						}
						if(index > 0) {
							sqlInsert += ", ";
						} else {
							sqlInsert += ") values (";
						}
						if(((String)c.getAttribute("value")).startsWith("sql.")) {
							sqlInsert += "" + ((String)c.getAttribute("value")).substring(4) + " ";
						} else if(
							c.hasAttribute("default") 
							&& ((String)c.getAttribute("default")).startsWith("sql.")
							&& !this._params.hasKey((String)c.getAttribute("value"))
						) {
							sqlInsert += "" + ((String)c.getAttribute("default")).substring(4) + " ";
						} else {
							sqlInsert += "?";
						}
						index++;
					}
					sqlInsert += ")";
					index = 0;
					for(int x = 0; x < column.getLength(); x++) {
						Element c = (Element)column.item(x);
						if(XML.trueAttrValue(c, "primary")) {
							if(index > 0) {
								sqlUpdate += " and ";
								sqlDelete += " and ";
							} else {
								sqlUpdate += " where ";
								sqlDelete += " where ";
							}
							sqlUpdate += c.getAttribute("name") + " = ?";
							sqlDelete += c.getAttribute("name") + " = ?";
							index++;
						}
					}
					this._expr = this._xpath.compile("where/sql");
					Element where = (Element)this._expr.evaluate(p, XPathConstants.NODE);
					if(where != null) {
						if(!((Element)where.getParentNode()).hasAttribute("method") || !this._params.hasKey("header.method") || (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))) {
							sqlUpdate += " and ";
							sqlUpdate += DBHelper.getSql(where, this._params);
							
							sqlDelete += " and ";
							sqlDelete += DBHelper.getSql(where, this._params);
						}
					}
					
					boolean isNew = false;
					if(XML.trueAttrValue(p, "multi")) {
						stmtInsert = this.prepareStatement(sqlInsert);
						stmtUpdate = this.prepareStatement(sqlUpdate);
						stmtDelete = this.prepareStatement(sqlDelete);
					} else {
						this._expr = this._xpath.compile("column[@primary='true']");
						NodeList coln = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						for(int x = 0; x < coln.getLength(); x++) {
							Element c = (Element)coln.item(x);
							if(!this._params.hasKey(c.getAttribute("value"))) {
								isNew = true;
							}
						}
						if(isNew) {
							stmt = this.prepareStatement(sqlInsert);
						} else {
							stmt = this.prepareStatement(sqlUpdate);
						}
					}
					
					boolean iscontinue = true;
					NodeList cc = null;
					NodeList ddd = null;
					if(XML.trueAttrValue(p, "multi")) {
						Element layout = BufferHelper.getLayoutElement(this._xpath, this._expr, this._query);
						this._expr = this._xpath.compile("middle/tab[@name = '" + p.getAttribute("name") + "']/*/column");
						cc = (NodeList)this._expr.evaluate(layout, XPathConstants.NODESET);
						if(cc == null || cc.getLength() <= 0) {
							this._expr = this._xpath.compile("column");
							ddd = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
						}
					}
					
					int idx = 1;
					while(iscontinue) {
						boolean isnext = false;
						if(XML.trueAttrValue(p, "multi")) {
							iscontinue = false;
							if(cc != null && cc.getLength() > 0) {
								for(int xx = 0; xx < cc.getLength(); xx++) {
									Element e = (Element)cc.item(xx);
									if(
										e.hasAttribute("type") && 
										(
											e.getAttribute("type").equals("hidden") || 
											e.getAttribute("type").equals("select") ||
											e.getAttribute("type").equals("radio")
										)
									) {
										continue;
									}
									if(this._params.containsKey("param." + e.getAttribute("name") + "." + idx)) {
										iscontinue = true;
									}
									if(this._params.hasKey("param." + e.getAttribute("name") + "." + idx)) {
										isnext = true;
									}
								}
							} else {
								for(int xx = 0; xx < ddd.getLength(); xx++) {
									Element e = (Element)ddd.item(xx);
									if(XML.trueAttrValue(e, "primary")) {
										continue;
									}
									if(XML.trueAttrValue(e, "foreign")) {
										continue;
									}
									if(!e.hasAttribute("value")) {
										continue;
									}
									if(e.hasAttribute("value") && !e.getAttribute("value").startsWith("param.")) {
										continue;
									}
									if(this._params.containsKey(e.getAttribute("value") + "." + idx)) {
										iscontinue = true;
									}
									if(this._params.hasKey(e.getAttribute("value") + "." + idx)) {
										isnext = true;
									}
								}
							}
						} else {
							isnext = true;
						}
						index = 1;
						boolean isDelete = false;
						if(XML.trueAttrValue(p, "multi")) {
							isNew = false;
							this._expr = this._xpath.compile("column[@primary='true']");
							NodeList coln = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
							
							for(int x = 0; x < coln.getLength(); x++) {
								Element c = (Element)coln.item(x);
								if(!this._params.hasKey(c.getAttribute("value") + "." + idx)) {
									isNew = true;
								}
							}
							if(!isNew) {
								isDelete = true;
								if(cc.getLength() > 0) {
									for(int xx = 0; xx < cc.getLength(); xx++) {
										Element e = (Element)cc.item(xx);
										if(logger.isLoggable(Level.FINER)) { logger.finer(e.getAttribute("name")); }
										if(
											e.hasAttribute("type") && 
											(
												e.getAttribute("type").equals("hidden") || 
												e.getAttribute("type").equals("select") ||
												e.getAttribute("type").equals("radio")
											)
										) {
											continue;
										}
										if(this._params.hasKey("param." + e.getAttribute("name") + "." + idx)) {
											isDelete = false;
										}
									}
								} else {
									for(int x = 0; x < ddd.getLength(); x++) {
										Element c = (Element)ddd.item(x);
										if(XML.trueAttrValue(c, "primary")) {
											continue;
										}
										if(XML.trueAttrValue(c, "foreign")) {
											continue;
										}
										if(!c.hasAttribute("value")) {
											continue;
										}
										if(c.hasAttribute("value") && !c.getAttribute("value").startsWith("param.")) {
											continue;
										}
										if(this._params.hasKey(c.getAttribute("value") + "." + idx)) {
											isDelete = false;
										}
									}
								}
							}
						}
						if(!iscontinue) {
							break;
						}
						if(!isnext && !isDelete) {
							idx++;
							continue;
						}
						java.util.Map<String, Encryptor> encryptor = getEncryptor(p);
						sb.append(this._tag.tag("row", p.getAttribute("name"), true));
						if(!isDelete) {
							for(int x = 0; x < column.getLength(); x++) {
								Element c = (Element)column.item(x);
								if(
									((String)c.getAttribute("value")).startsWith("sql.")
								) {
									continue;
								} else if(
									c.hasAttribute("default") 
									&& ((String)c.getAttribute("default")).startsWith("sql.")
									&& !this._params.hasKey((String)c.getAttribute("value"))
								) {
									continue;
								}
								if(
									!isNew 
									&& (
										((String)c.getAttribute("only")).equals("insert")
										|| XML.trueAttrValue(c, "primary")
									)
								) {
									continue;
								}
								if(
									isNew &&
									XML.trueAttrValue(c, "primary") &&
									c.hasAttribute("insert") && 
									((String)c.getAttribute("insert")).equals("generate")
								) {
									continue;
								}
								if(c.hasAttribute("insert") && ((String)c.getAttribute("insert")).startsWith("sequence.")) {
									if(XML.trueAttrValue(p, "multi")) {
										this._params.put((String)c.getAttribute("insert") + "." + idx, DBHelper.getNextSequenceValue(this._con, (String)c.getAttribute("insert"), this._info, this.dmd));
										if(isNew) {
											this.bind(stmtInsert, "int", index, new String[] {c.getAttribute("insert")}, idx, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
										} else {
/*
!!!이상한 코드!!!
Primary Key 가 아닌데도 불구하고, Sequence로 입력되는 경우가 있는 case 가 있는지 의구심이 있음.
*/
											this.bind(stmtUpdate, "int", index, new String[] {c.getAttribute("insert")}, idx, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
										}
									} else {
										this._params.put((String)c.getAttribute("insert"), DBHelper.getNextSequenceValue(this._con, (String)c.getAttribute("insert"), this._info, this.dmd));
										this.bind(stmt, "int", index, new String[] {c.getAttribute("insert")}, -1, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
									}
									index++;
									continue;
								} else if(
									XML.trueAttrValue(c, "foreign") &&
									!this._params.hasKey(c.getAttribute("value"))
								) {
									this._expr = this._xpath.compile("tables/table/column[@name = '" + c.getAttribute("name") + "' and @primary = 'true']");
									NodeList ccc = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
									if(ccc.getLength() > 0) {
										Element cccc = (Element)ccc.item(0);
										if(cccc.hasAttribute("insert") && (((String)cccc.getAttribute("insert")).startsWith("sequence.") || ((String)cccc.getAttribute("insert")).equals("generate"))) {
											String insert = cccc.getAttribute("insert");
											if(insert != null && insert.equals("generate")) {
												insert = cccc.getAttribute("insert") + "." + cccc.getAttribute("name");
											}
											if(XML.trueAttrValue(p, "multi")) {
												if(isNew) {
													this.bind(stmtInsert, "int", index, new String[] {insert}, idx, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
												} else {
/*
!!!이상한 코드!!!
위의 !this._params.hasKey(c.getAttribute("value")) 조건에 의해 insert 에서만 실행되는 것으로 생각됨.
프로그램적으로 Foreign Key 값이 바뀔 일도 없을 것으로 생각됨.
*/
													this.bind(stmtUpdate, "int", index, new String[] {insert}, idx, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
												}
											} else {
												this.bind(stmt, "int", index, new String[] {insert}, -1, null, null, p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
											}
											index++;
											continue;
										}
									}
								}
								String defaultValue = null;
								if(c.hasAttribute("default")) {
									if(c.getAttribute("default").startsWith("param.")) {
										defaultValue = this._params.getString(c.getAttribute("default").substring(6));
									} else {
										defaultValue = c.getAttribute("default");
									}
								}
								if(XML.trueAttrValue(p, "multi")) {
									if(isNew) {
										this.bind(stmtInsert, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, idx, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
									} else {
										this.bind(stmtUpdate, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, idx, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
									}
								} else {
									this.bind(stmt, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, -1, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
								}
								index++;
							}
						}
						if(!isNew) {
							for(int x = 0; x < column.getLength(); x++) {
								Element c = (Element)column.item(x);
								if(XML.trueAttrValue(c, "primary")) {
									String defaultValue = null;
									if(c.hasAttribute("default")) {
										if(c.getAttribute("default").startsWith("param.")) {
											defaultValue = this._params.getString(c.getAttribute("default").substring(6));
										} else {
											defaultValue = c.getAttribute("default");
										}
									}
									if(XML.trueAttrValue(p, "multi")) {
										if(isDelete) {
											this.bind(stmtDelete, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, idx, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
										} else {
											this.bind(stmtUpdate, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, idx, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
										}
									} else {
										this.bind(stmt, c.getAttribute("datatype"), index, new String[] {c.getAttribute("value")}, 0, defaultValue, c.getAttribute("pattern"), p.getAttribute("name"), c.getAttribute("name"), encryptor, c.getAttribute("encrypt"), sb);
									}
									index++;
								}
							}
							
							if(where != null) {
								if(
									!((Element)where.getParentNode()).hasAttribute("method") 
									|| !this._params.hasKey("header.method") 
									|| (this._params.getString("header.method")).equals(((Element)where.getParentNode()).getAttribute("method"))
								) {
									this._expr = this._xpath.compile("where/params/param");
									NodeList param = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
									
									for(int x = 0; x < param.getLength(); x++) {
										Element pp = (Element)param.item(x);
										String defaultValue = null;
										if(pp.hasAttribute("default")) {
											if(pp.getAttribute("default").startsWith("param.")) {
												defaultValue = this._params.getString(pp.getAttribute("default").substring(6));
											} else {
												defaultValue = pp.getAttribute("default");
											}
										}
										if(XML.trueAttrValue(p, "multi")) {
											if(isDelete) {
												if(!pp.hasAttribute("cond") || AuthParser.auth(pp.getAttribute("cond"), this._params)) {
													this.bind(stmtDelete, pp.getAttribute("datatype"), index, new String[] {pp.getAttribute("value")}, -1, defaultValue, pp.getAttribute("pattern"), null, null, encryptor, pp.getAttribute("encrypt"), null);
													index++;
												}
											} else {
												if(!pp.hasAttribute("cond") || AuthParser.auth(pp.getAttribute("cond"), this._params)) {
													this.bind(stmtUpdate, pp.getAttribute("datatype"), index, new String[] {pp.getAttribute("value")}, -1, defaultValue, pp.getAttribute("pattern"), null, null, encryptor, pp.getAttribute("encrypt"), null);
													index++;
												}
											}
										} else {
											if(!pp.hasAttribute("cond") || AuthParser.auth(pp.getAttribute("cond"), this._params)) {
												this.bind(stmt, pp.getAttribute("datatype"), index, new String[] {pp.getAttribute("value")}, -1, defaultValue, pp.getAttribute("pattern"), null, null, encryptor, pp.getAttribute("encrypt"), null);
												index++;
											}
										}
										
									}
								}
							}
						}
						int result = 0;
						if(XML.trueAttrValue(p, "multi")) {
							if(isNew) {
								stmtInsert.executeUpdate();
								result = stmtInsert.getUpdateCount();
								totalUpdateCount += result;
								this._expr = this._xpath.compile("column[@primary='true' and @insert='generate']");
								NodeList coln = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);

								if(coln.getLength() > 0) {
									String generated = "";
									for(int x = 0; x < coln.getLength(); x++) {
										Element c = (Element)coln.item(x);
										generated = c.getAttribute("insert") + "." + c.getAttribute("name");
									}
									rs = stmtInsert.getGeneratedKeys();
									if(rs.next()) {
										this._params.put(generated + "." + index, rs.getInt(1));
									}
									DB.close(rs);
								}
							} else if(isDelete) {
								stmtDelete.executeUpdate();
								result = stmtDelete.getUpdateCount();
								totalUpdateCount += result;
							} else {
								stmtUpdate.executeUpdate();
								result = stmtUpdate.getUpdateCount();
								totalUpdateCount += result;
							}
						} else {
							stmt.executeUpdate();
							result = stmt.getUpdateCount();
							totalUpdateCount += result;
							if(isNew) {
								this._expr = this._xpath.compile("column[@primary='true' and @insert='generate']");
								NodeList coln = (NodeList)this._expr.evaluate(p, XPathConstants.NODESET);
								if(coln.getLength() > 0) {
									String generated = "";
									for(int x = 0; x < coln.getLength(); x++) {
										Element c = (Element)coln.item(x);
										generated = c.getAttribute("insert") + "." + c.getAttribute("name");
									}
									rs = stmt.getGeneratedKeys();
									if(rs.next()) {
										this._params.put(generated, rs.getInt(1));
									}
									DB.close(rs);
								}
							}
						}
						sb.append(this._tag.tag("row", "rowcount", null, true) + result + this._tag.tag("row", "rowcount", null, false));
						sb.append(this._tag.tag("row", p.getAttribute("name"), false));
						if(table.getLength() > 0) {
							this._params.put("query.row." + p.getAttribute("name") + ".count", result);
						} else {
							this._params.put("query.row.count", result);
							
						}
						if(!XML.trueAttrValue(p, "multi")) {
							DB.close(stmt);
							iscontinue = false;
							break;
						}
						idx++;
					}
					if(XML.trueAttrValue(p, "multi")) {
						DB.close(stmtInsert);
						DB.close(stmtUpdate);
						DB.close(stmtDelete);
					}
					sb.append(this._tag.tag("rows", null, false));
				}
			}
			this._params.put("query.row.total_update_count", totalUpdateCount);
		} catch (XPathExpressionException | SQLException | NoSuchProviderException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} finally {
			DB.close(rs);
			DB.close(stmt);
			DB.close(stmtInsert);
			DB.close(stmtUpdate);
			DB.close(stmtDelete);
		}
		return sb;
	}
	
	private void setInt(
		PreparedStatement stmt, 
		int index, 
		int value
	) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) { logger.finest(index + " = " + value); }
		stmt.setInt(index, value);
	}
	
	public void bind(
		PreparedStatement stmt, 
		String datatype, 
		int index, 
		String[] value, 
		int idx, 
		String defaultValue, 
		String pattern, 
		String table, 
		String column, 
		java.util.Map<String, Encryptor> encryptor, 
		String encrypt, 
		Buffer sb
	) throws SQLException, NoSuchProviderException {
		DataBinder binder = null;
		if(STR.existsIgnoreCase(datatype, new String[]{"varchar", "char"})) {
			binder = new DataBinderStringTypeImpl();
		} else if(STR.existsIgnoreCase(datatype, new String[]{"boolean"})) {
			binder = new DataBinderBooleanTypeImpl();
		} else if(STR.existsIgnoreCase(datatype, new String[]{"int", "float", "double", "long"})) {
			binder = new DataBinderNumberTypeImpl();
		} else if(STR.existsIgnoreCase(datatype, new String[] {"date", "timestamp"})) {
			binder = new DataBinderDateTypeImpl();
		} else {
			throw new ParsingException();
		}
		if(binder != null) {
			binder.bind(
				stmt, 
				datatype,
				index, 
				value, 
				idx, 
				defaultValue, 
				pattern, 
				table, 
				column, 
				encryptor, 
				encrypt, 
				sb,
				this._params,
				this._tag,
				this._info,
				this.dmd
			);
		}
	}
	public boolean isError() {
		return this.isError;
	}
	public boolean isQueryAndResultSet() {
		return this.isQueryAndResultSet;
	}
	private Buffer before() throws SQLException, NoSuchProviderException, XPathExpressionException {
		Buffer sb = new Buffer();
		sb.appendL("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if(this.isError) {
			sb.appendL("<?xml-stylesheet type=\"text/xsl\" href=\"" + this._query.getAttribute("id").substring(this._query.getAttribute("id").lastIndexOf("/") + 1) + ".xsl?method=error\"?>");
		} else if(
			!this.isQueryAndResultSet &&
			(
				this._params.equals("header.method", "POST") ||
				(
					XML.existsIgnoreCaseAttrValue(this._query, "funcType", new String[]{"query", "report"}) &&
					XML.equalsIgnoreCaseAttrValue(this._query, "allow", "get") && this._params.equals("header.method", "GET")
				)
			)
		) {
			sb.appendL("<?xml-stylesheet type=\"text/xsl\" href=\"" + this._query.getAttribute("id").substring(this._query.getAttribute("id").lastIndexOf("/") + 1) + ".xsl?method=post\"?>");
		} else {
			Element layout = BufferHelper.getLayoutElement(this._xpath, this._expr, this._query);
			if(
				XML.existsIgnoreCaseAttrValue(this._query, "funcType", new String[]{"list", "listAll", "detail"}) &&
				XML.validAttrValue(layout, "href")
			) {
				sb.appendL("<?xml-stylesheet type=\"text/xsl\" href=\"" + layout.getAttribute("href") + "\"?>");
			} else {
				if(this._params.equals("header.method", "POST")) {
					if(this.isQueryAndResultSet) {
						sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"" + this._query.getAttribute("id").substring(this._query.getAttribute("id").lastIndexOf("/") + 1) + ".xsl?method=query");
					} else {
						sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"" + this._query.getAttribute("id").substring(this._query.getAttribute("id").lastIndexOf("/") + 1) + ".xsl?method=post");
					}
				} else {
					sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"" + this._query.getAttribute("id").substring(this._query.getAttribute("id").lastIndexOf("/") + 1) + ".xsl");
				}
				if(this._params != null && !this._params.isEmpty()) {
					Iterator<String> it = this._params.keySet().iterator();
					int index = 0;
					if(this._params.equals("header.method", "POST")) {
						index = 1;
					}
					while(it.hasNext()) {
						String key = (String)it.next();
						if(key != null && key.startsWith("param.") && !key.equals("param.page")) {
							if(this._params.isArray(key)) {
								java.util.List<String> items = this._params.getArray(key);
								for(String item : items){
									if(index > 0) {
										sb.append("&amp;");
									} else {
										sb.append("?");
									}
									try {
										sb.append("" + key.substring(6) + "=" + java.net.URLEncoder.encode(item, "UTF-8") + "");
									} catch (UnsupportedEncodingException e) {
										sb.append("" + key.substring(6) + "=" + item + "");
										if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
									}
									index++;
								}
							} else {
								if(index > 0) {
									sb.append("&amp;");
								} else {
									sb.append("?");
								}
								try {
									sb.append("" + key.substring(6) + "=" + java.net.URLEncoder.encode(this._params.getString(key), "UTF-8") + "");
								} catch (UnsupportedEncodingException e) {
									sb.append("" + key.substring(6) + "=" + this._params.getString(key) + "");
									if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
								}
								index++;
							}
						}
					}
					
				}
				sb.appendL("\" ?>");
			}
		}
		sb.appendL(this._tag.tag("document", null, true));
		try {
			Document doc = BufferHelper.getExtendsDocument(
				this._xpath,
				this._expr,
				this._config,
				this._query
			);
			parseMessage(sb, doc);
			if(
				this._params.hasKey("header.method") && 
				(
					!this._params.getString("header.method").equals("POST") ||
					this.isQueryAndResultSet
				) && 
				!this.isError
			) {
				this._expr = this._xpath.compile("header/codes/code");
				NodeList codes = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
				
				for(int i = 0; i < codes.getLength(); i++) {
					Element node = (Element)codes.item(i);
					if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
						continue;
					}
					if(node.hasAttribute("cond") && node.getAttribute("cond") != null) {
						if(!(AuthParser.auth(node.getAttribute("cond"), this._params))) {
							continue;
						}
					}
					this._expr = this._xpath.compile("header/codes/code[@name='" + node.getAttribute("name") + "']");
					NodeList p = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
					if(p.getLength() > 0) {
						continue;
					}
					if(doc != null) {
						this._expr = this._xpath.compile("header/codes/code[@name='" + node.getAttribute("name") + "']");
						NodeList b = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
						if(b.getLength() > 0) {
							continue;
						}
					}
					sb.append(code(node));
				}
				this._expr = this._xpath.compile("header/codes/code");
				codes = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
				
				for(int i = 0; i < codes.getLength(); i++) {
					Element node = (Element)codes.item(i);
					if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
						continue;
					}
					if(node.hasAttribute("cond") && node.getAttribute("cond") != null) {
						if(!(AuthParser.auth(node.getAttribute("cond"), this._params))) {
							continue;
						}
					}
					if(doc != null) {
						this._expr = this._xpath.compile("header/codes/code[@name='" + node.getAttribute("name") + "']");
						NodeList b = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
						if(b.getLength() > 0) {
							continue;
						}
					}
					sb.append(code(node));
				}
				if(doc != null) {
					this._expr = this._xpath.compile("/querys/header/codes/code");
					codes = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
					for(int i = 0; i < codes.getLength(); i++) {
						Element node = (Element)codes.item(i);
						if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
							continue;
						}
						if(node.hasAttribute("cond") && node.getAttribute("cond") != null) {
							if(!(AuthParser.auth(node.getAttribute("cond"), this._params))) {
								continue;
							}
						}
						sb.append(code(node));
					}
				}
			}
		} catch (XPathExpressionException | DOMException | NoSuchProviderException | ParserConfigurationException | SAXException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return sb;
	}
	
	private void parseMessage(Buffer sb, Document doc) throws XPathExpressionException {
		Buffer sb_message = null;
		if(sb != null) {
			sb_message = new Buffer();
		}
		NodeList messages;
		if(doc != null) {
			this._expr = this._xpath.compile("header/messages/message");
			messages = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
			for(int i = 0; i < messages.getLength(); i++) {
				Element node = (Element)messages.item(i);
				if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
						continue;
				}
				this._expr = this._xpath.compile("header/messages/message[@name='" + node.getAttribute("name") + "']");
				NodeList p = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
				if(p.getLength() > 0) {
					continue;
				}
				this._expr = this._xpath.compile("header/messages/message[@name='" + node.getAttribute("name") + "']");
				p = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
				if(p.getLength() > 0) {
					continue;
				}
				if(sb == null) {
					message(node, false);
				} else {
					sb_message.append(message(node, true));
				}
			}
		}
		this._expr = this._xpath.compile("header/messages/message");
		messages = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
		for(int i = 0; i < messages.getLength(); i++) {
			Element node = (Element)messages.item(i);
			if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
					continue;
			}
			this._expr = this._xpath.compile("header/messages/message[@name='" + node.getAttribute("name") + "']");
			NodeList b = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(b.getLength() > 0) {
				continue;
			}
			if(sb == null) {
				message(node, false);
			} else {
				sb_message.append(message(node, true));
			}
		}
		this._expr = this._xpath.compile("header/messages/message");
		messages = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		for(int i = 0; i < messages.getLength(); i++) {
			Element node = (Element)messages.item(i);
			if(!node.hasAttribute("name") || node.getAttribute("name") == null || node.getAttribute("name").equals("")) {
					continue;
			}
			if(sb == null) {
				message(node, false);
			} else {
				sb_message.append(message(node, true));
			}
		}
		if(sb != null && sb_message.length() > 0) {
			sb.appendL(this._tag.tag("messages", null, true));
			sb.appendL(sb_message);
			sb.appendL(this._tag.tag("messages", null, false));
			sb_message.clear();
			sb_message = null;
		}
	}
	private Buffer message(Element node, boolean isBuffer) {
		Buffer sb = new Buffer();
		String tmp = null;
		String label = null;
		if(node.hasAttribute("ref") && this._params.hasKey("message." + node.getAttribute("ref"))) {
			tmp = this._params.getString("message." + node.getAttribute("ref"));
			if(tmp != null) {
				if(
					this._params.hasKey("messages.code." + node.getAttribute("ref")) &&
					tmp.startsWith("[") &&
					tmp.indexOf("]") > 0
				) {
					if(XML.equalsIgnoreCaseAttrValue(node, "code", "exclude")) {
						label = tmp.substring(tmp.indexOf("]") + 1);
					} else {
						label = "[" + node.getAttribute("name") + "]" + tmp.substring(tmp.indexOf("]") + 1);
						this._params.put("messages.code." + node.getAttribute("name"), true);
					}
				} else {
					if(XML.equalsIgnoreCaseAttrValue(node, "code", "include")) {
						label = "[" + node.getAttribute("name") + "]" + tmp;
						this._params.put("messages.code." + node.getAttribute("name"), true);
					} else {
						label = tmp;
					}
				}
				this._params.put("message." + node.getAttribute("name"), label);
			}
		}
		if(label == null) {
			tmp = node.getTextContent();
			label = "";
			if(tmp != null) {
				java.util.StringTokenizer st = new java.util.StringTokenizer(tmp, "\t\n\r ");
				int index = 0;
				while(st.hasMoreTokens()) {
					if(index > 0) {
						label += " ";
					}
					label += st.nextToken();
					index++;
				}
				if(index == 0) {
					label = null;
				} else {
					if(XML.equalsIgnoreCaseAttrValue(node, "code", "include")) {
						label = "[" + node.getAttribute("name") + "]" + label;
						this._params.put("messages.code." + node.getAttribute("name"), true);
					}
					this._params.put("message." + node.getAttribute("name"), label);
				}
			}
		}
		if(isBuffer && XML.trueAttrValue(node, "public")) {
			if(
				this._params.hasKey("header.method") && 
				(
					!this._params.getString("header.method").equals("POST") ||
					!this.isQueryAndResultSet
				) && 
				!this.isError
			) {
				if(label != null) {
					sb.append(this._tag.tag("message", null, true));
					sb.append(this._tag.tag("message", "name", null, true));
					sb.append(node.getAttribute("name"));
					sb.append(this._tag.tag("message", "name", null, false));
					sb.append(this._tag.tag("message", "label", null, true));
					sb.append(label);
					sb.append(this._tag.tag("message", "label", null, false));
					sb.appendL(this._tag.tag("message", null, false));
				}
			}
		}
		return sb;
	}
	private Buffer code(Element node) throws SQLException, NoSuchProviderException {
		Buffer sb = new Buffer();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = null;
		try {
			if(node.hasAttribute("sql") && node.getAttribute("sql") != null && !node.getAttribute("sql").equals("")) {
				sql = node.getAttribute("sql");
			} else {
				this._expr = this._xpath.compile("sql");
				Element sqlElement = (Element)this._expr.evaluate(node, XPathConstants.NODE);
				sql = DBHelper.getSql(sqlElement, this._params);
			}
			if(sql != null) {
				stmt = this.prepareStatement(sql);
				this._expr = this._xpath.compile("params/param");
				NodeList param = (NodeList)this._expr.evaluate(node, XPathConstants.NODESET);
				int index = 1;
				java.util.Map<String, Encryptor> encryptor = getEncryptor(node);
				for(int x = 0; x < param.getLength(); x++) {
					Element p = (Element)param.item(x);
					if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), this._params)) {
						this.bind(stmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
						index++;
					}
				}
				rs = stmt.executeQuery();
				sb.appendL(this._tag.tag("code", node.getAttribute("name"), true));
				index = 0;
				while(rs.next()) {
					sb.appendL(this._tag.tag("code", "option", rs.getString(1), rs.getString(2)));
					if(index == 0) {
						this._params.put("code." + node.getAttribute("name") + ".firstValue", rs.getString(1));
					}
					index++;
				}
				sb.appendL(this._tag.tag("code", null, false));
				DB.close(rs);
				DB.close(stmt);
			} else {
				sb.appendL(this._tag.tag("code", node.getAttribute("name"), true));
				NodeList list = node.getChildNodes();
				int index = 0;
				for(int x = 0; x < list.getLength(); x++) {
					org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(x);
					if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && n.getNodeName().equals("option")) {
						Element e = (Element)n;
						sb.appendL(this._tag.tag("code", "option", e.getAttribute("value"), e.getAttribute("label")));
						if(index == 0) {
							this._params.put("code." + node.getAttribute("name") + ".firstValue", e.getAttribute("value"));
						}
						index++;
					}
				}
				sb.appendL(this._tag.tag("code", null, false));
			}
		} catch (SQLException | NoSuchProviderException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			throw e;
		} catch (XPathExpressionException | DOMException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			return null;
		} finally {
			DB.close(rs);
			DB.close(stmt);
		}
		return sb;
	}
	public Buffer after() {
		Buffer sb = new Buffer();
		if(this._params != null && !this._params.isEmpty()) {
			boolean existsResult = false;
			boolean existsProp = false;
			boolean existsError = false;
			Iterator<String> it = this._params.keySet().iterator();
			sb.appendL(this._tag.tag("params", null, true));
			while(it.hasNext()) {
				String key = (String)it.next();
				if(key.startsWith("param.")) {
					BufferHelper.addRecord(key, this._params, sb, this._tag);
				} else if(key.startsWith("result.")) {
					existsResult = true;
				} else if(key.startsWith("error.")) {
					existsError = true;
				} else if(key.startsWith("prop.") && key.endsWith(".public")) {
					existsProp = true;
				}
			}
			sb.appendL(this._tag.tag("params", null, false));
			if(existsError) {
				it = this._params.keySet().iterator();
				sb.appendL(this._tag.tag("errors", null, true));
				while(it.hasNext()) {
					String key = (String)it.next();
					if(key.startsWith("error.")) {
						BufferHelper.addRecord(key, this._params, sb, this._tag);
					}
				}
				sb.appendL(this._tag.tag("errors", null, false));
			}
			if(existsResult) {
				it = this._params.keySet().iterator();
				sb.appendL(this._tag.tag("results", null, true));
				while(it.hasNext()) {
					String key = (String)it.next();
					if(key.startsWith("result.")) {
						BufferHelper.addRecord(key, this._params, sb, this._tag);
					}
				}
				sb.appendL(this._tag.tag("results", null, false));
			}
			if(existsProp) {
				it = this._params.keySet().iterator();
				sb.appendL(this._tag.tag("props", null, true));
				while(it.hasNext()) {
					String key = (String)it.next();
					if(key.startsWith("prop.")) {
						if(!key.endsWith(".public") && this._params.equals(key + ".public", "true")) {
							BufferHelper.addRecord(key, this._params, sb, this._tag);
						}
					}
				}
				sb.appendL(this._tag.tag("props", null, false));
			}
		}
		sb.append(this._tag.tag("document", null, false));
		return sb;
	}
	private void validate(Element e, String key, java.util.ArrayList msgs) {
		if(
			e.hasAttribute("not-null") && 
			e.getAttribute("not-null") != null && 
			(
				((String)e.getAttribute("not-null")).equalsIgnoreCase("true") || 
				((String)e.getAttribute("not-null")).equalsIgnoreCase("y")
			)
		) {
			if(!this._params.notNull("param." + key)) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("max-length") && e.getAttribute("max-length") != null) {
			if(!this._params.maxLength("param." + key, (String)e.getAttribute("max-length"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("min-length") && e.getAttribute("min-length") != null) {
			if(!this._params.minLength("param." + key, (String)e.getAttribute("min-length"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("number-format") && e.getAttribute("number-format") != null) {
			if(!this._params.numberFormat("param." + key, (String)e.getAttribute("number-format"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("max-value") && e.getAttribute("max-value") != null) {
			if(!this._params.maxValue("param." + key, (String)e.getAttribute("max-value"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("min-value") && e.getAttribute("min-value") != null) {
			if(!this._params.minValue("param." + key, (String)e.getAttribute("min-value"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("date-format") && e.getAttribute("date-format") != null) {
			if(!this._params.dateFormat("param." + key, (String)e.getAttribute("date-format"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
		if(e.hasAttribute("format") && e.getAttribute("format") != null) {
			if(!this._params.format("param." + key, (String)e.getAttribute("format"))) {
				msgs.add((String)e.getAttribute("msg"));
			}
		}
	}
	public Buffer validate() throws SQLException, NoSuchProviderException {
		try {
			this._expr = this._xpath.compile("validation");
			Element node = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
			
			if(node == null) {
				return null;
			} else {
				if(node.hasAttribute("method") && node.getAttribute("method") != null) {
					if(!((String)node.getAttribute("method")).equals(this._params.getString("header.method"))) {
						return null;
					}
				}
				NodeList list = node.getChildNodes();
				java.util.ArrayList msgs = new java.util.ArrayList();
				for(int x = 0; x < list.getLength(); x++) {
					org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(x);
					if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						if(XML.validAttrValue((Element)n, "cond")) {
							if(!(AuthParser.auth(((Element)n).getAttribute("cond"), this._params))) {
								continue;
							}
						}
					
						if(n.getNodeName().equals("param")) {
							Element e = (Element)n;
							String key = (String)e.getAttribute("name");
							if(XML.trueAttrValue(e, "multi")) {
								int index = 1;
								while(true) {
									if(this._params.containsKey("param." + key + "." + index)) {
										this.validate(e, key + "." + index, msgs);
										index++;
									} else {
										break;
									}
								}
							} else {
								this.validate(e, key, msgs);
							}
						} else if(n.getNodeName().equals("command")) {
							Element e = (Element)n;
							if(XML.equalsIgnoreCaseAttrValue(e, "type", "native") && e.hasAttribute("class")) {
								try {
									Validator validator = (Validator)Class.forName(e.getAttribute("class")).getConstructor().newInstance();
									String msg = validator.execute(this._params, this._con);
									if(msg != null) {
										msgs.add(msg);
									}
								} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
									if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(ex)); 	}
								}
							} else {
								if(!this.checkFromSQL(e)) {
									msgs.add((String)e.getAttribute("msg"));
								}
							}
						}
					}
				}
				if(msgs.isEmpty()) {
					return null;
				} else {
					Buffer sb = new Buffer();
					this.isError = true;
					sb.append(before());
					for(int i = 0; i < msgs.size(); i++) {
						this._params.puts("error.error", msgs.get(i));
					}
					sb.append(after());
					msgs.clear();
					msgs = null;
					return sb;
				}
			}
		} catch (XPathExpressionException | DOMException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isDownloadable(String name) throws XPathExpressionException, SQLException, NoSuchProviderException {
		this._expr = this._xpath.compile(name);
		Element command = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		
		return this.checkFromSQL(command);
	}
	private boolean checkFromSQL(Element command) throws XPathExpressionException, SQLException, NoSuchProviderException {
		boolean result = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Record record = new Record();
		if(command == null) {
			result = true;
		} else {
			String check = "${result} exists";
			if(command.hasAttribute("check")) {
				check = command.getAttribute("check");
			}
			if(logger.isLoggable(Level.FINER)) { logger.finer("check : " + check); }
			if(check != null && check.equals("exists")) {
				check = "${result} exists";
			} else if(check != null && check.equals("0")) {
				check = "${result} = '0'";
			} else if(check != null && check.equals(">0")) {
				check = "${result} > '0'";
			} else if(check != null && check.equals("<0")) {
				check = "${result} < '0'";
			}
			try {
				this._expr = this._xpath.compile("sql");
				
				Element sql = (Element)this._expr.evaluate(command, XPathConstants.NODE);
				String s = DBHelper.getSql(sql, this._params);
				stmt = this.prepareStatement(s);
				
				java.util.Map<String, Encryptor> encryptor = getEncryptor(command);
				
				this._expr = this._xpath.compile("params/param");
				NodeList param = (NodeList)this._expr.evaluate(command, XPathConstants.NODESET);
				int index = 1;
				for(int x = 0; x < param.getLength(); x++) {
					Element p = (Element)param.item(x);
					if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), this._params)) {
						this.bind(stmt, p.getAttribute("datatype"), index, new String[] {p.getAttribute("value")}, -1, p.getAttribute("default"), p.getAttribute("pattern"), null, null, encryptor, p.getAttribute("encrypt"), null);
						index++;
					}
				}
				rs = stmt.executeQuery();
				if(rs.next()) {
					record.put("result", rs.getString(1));
				}
				DB.close(rs);
				DB.close(stmt);
				result = AuthParser.auth(check, record);
			} catch (XPathExpressionException | SQLException | NoSuchProviderException e) {
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
				throw e;
			} finally {
				DB.close(rs);
				DB.close(stmt);
			}
		}
		
		return result;
	}
	public java.util.Map<String, Encryptor> getEncryptor(Element node) throws XPathExpressionException {
		java.util.Map<String, Encryptor> encryptor = new java.util.Hashtable();
		if(node.hasAttribute("encrypt") && node.getAttribute("encrypt") != null && !node.getAttribute("encrypt").equals("")) {
			try {
				encryptor.put("true", (Encryptor) Class.forName(node.getAttribute("encrypt")).getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				encryptor = null;
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			}
		}
		this._expr = this._xpath.compile("encrypt");
		NodeList encryptList = (NodeList)this._expr.evaluate(node, XPathConstants.NODESET);
		for(int a = 0; a < encryptList.getLength(); a++) {
			Element encrypt = (Element)encryptList.item(a);
			try {
				encryptor.put(encrypt.getAttribute("key"), (Encryptor) Class.forName(encrypt.getAttribute("name")).getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				encryptor = null;
				if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			}
		}
		return encryptor;
	}
	public java.util.Map<String, String> getRattern(Element node, String xpath) throws XPathExpressionException {
		return getRatternOrEncrypted(node, xpath, "pattern");
	}
	public java.util.Map<String, String> getEncrypted(Element node, String xpath) throws XPathExpressionException {
		return getRatternOrEncrypted(node, xpath, "encrypt");
	}
	private java.util.Map<String, String> getRatternOrEncrypted(Element node, String xpath, String attrName) throws XPathExpressionException {
		this._expr = this._xpath.compile(xpath);
		NodeList columns = (NodeList)this._expr.evaluate(node, XPathConstants.NODESET);
		java.util.Map<String, String> map = new java.util.Hashtable();
		if(columns != null && columns.getLength() > 0) {
			for(int i = 0; i < columns.getLength(); i++) {
				map.put(((Element)columns.item(i)).getAttribute("name"), ((Element)columns.item(i)).getAttribute(attrName));
			}
		}
		return map;
	}
}
