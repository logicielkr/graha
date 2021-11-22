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

package kr.graha.servlet;

import java.io.File;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
import org.xml.sax.SAXException;
import java.security.NoSuchProviderException;

import kr.graha.lib.FileHelper;
import kr.graha.helper.LOG;
import kr.graha.helper.DB;
import kr.graha.lib.Record;
import kr.graha.lib.AuthParser;
import kr.graha.lib.XMLGenerator;
import kr.graha.lib.Encryptor;
import kr.graha.lib.DBHelper;
import kr.graha.lib.Buffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 * Graha(그라하) 사용자 정의 Property 처리기
 * @author HeonJik, KIM
 * @version 0.5.0.3
 * @since 0.5.0.3
 */

public class PropAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static final int Before_Connection = 1;
	public static final int Before_Before_Processor = 2;
	public static final int After_Before_Processor = 3;
	public static final int Before_After_Processor = 4;
	public static final int After_After_Processor = 5;
	
	
	
	
	protected PropAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, File config, Element query, Record params, int mode) {
		try {
			this.execute(request, config, query, params, null, null, mode);
		} catch (SQLException | NoSuchProviderException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LOG.toString(e));
			}
		}
	}
	protected void execute(
			HttpServletRequest request, 
			File config, 
			Element query, 
			Record params, 
			Connection con, 
			XMLGenerator g,
			int mode
		) 
		throws SQLException,
			NoSuchProviderException
	{
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			XPathExpression expr = xpath.compile("header");
			Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
			
			expr = xpath.compile("header/prop");
			NodeList[] nodes = new NodeList[3];
			nodes[0] = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
			nodes[1] = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			nodes[2] = null;
			if(nodes[0].getLength() == 0 && query.hasAttribute("extends")) {
				expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/prop");
				nodes[0] = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			}

			Document doc = null;
			if(header.hasAttribute("extends")) {
					File parent = new File(config.getParent() + File.separator + header.getAttribute("extends"));
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					dbf.setXIncludeAware(true);
					doc = dbf.newDocumentBuilder().parse(parent);
					doc.getDocumentElement().normalize();
					expr = xpath.compile("/querys/header/prop");
					nodes[2] = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			}
			
			for(int x = nodes.length - 1; x >= 0 ; x--) {
				if(nodes[x] == null || nodes[x].getLength() == 0) {
					continue;
				}
				for(int i = 0; i < nodes[x].getLength(); i++) {
					Element node = (Element)nodes[x].item(i);
					if(node.hasAttribute("name")) {
						expr = xpath.compile("header/prop[@name='" + node.getAttribute("name") + "' and override='true']");
						NodeList p = null;
						if(x == 1) {
							p = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(x == 2 || x == 1) {
							p = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(!node.hasAttribute("time") && mode >= PropAdapter.After_Before_Processor) {
							continue;
						}
						if(node.hasAttribute("time")) {
							int time = getMode((String)node.getAttribute("time"));
							if(time == 0 && mode >= PropAdapter.After_Before_Processor) {
								continue;
							}
							if(time > 0 && mode != time) {
								continue;
							}
						}
						if(!node.hasAttribute("cond") || AuthParser.auth(node.getAttribute("cond"), params)) {
							if(node.hasAttribute("value")) {
								Record result = FileHelper.parse(node.getAttribute("value"), params);
								if(result != null && result.get("_system.filepath") != null) {
									params.puts("prop." + node.getAttribute("name"), result.get("_system.filepath"));
									if(node.hasAttribute("public") && node.getAttribute("public") != null && node.getAttribute("public").equals("true")) {
										params.puts("prop." + node.getAttribute("name") + ".public", "true");
									}
								}
							} else if(con != null && g != null) {
								execute(params, node, xpath, con, g, mode);
							}
						}
					}
				}
			}
		} catch (XPathExpressionException | DOMException | ParserConfigurationException | SAXException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LOG.toString(e));
			}
		}
	}
	private int getMode(String time) {
		if(time == null) {
			return 0;
		} else if(time.equals("first")) {
			return PropAdapter.Before_Connection;
		} else if(time.equals("before")) {
			return PropAdapter.Before_Before_Processor;
		} else if(time.equals("after-before")) {
			return PropAdapter.After_Before_Processor;
		} else if(time.equals("before-after")) {
			return PropAdapter.Before_After_Processor;
		} else if(time.equals("after")) {
			return PropAdapter.After_After_Processor;
		} else {
			return 0;
		}
	}
	protected void execute(
			Record params, 
			Element node, 
			XPath xpath, 
			Connection con, 
			XMLGenerator g,
			int mode
		) 
		throws XPathExpressionException, 
			SQLException,
			NoSuchProviderException
	{
		XPathExpression expr = xpath.compile("sql");
		Element sql = (Element)expr.evaluate(node, XPathConstants.NODE);
		String s = DBHelper.getSql(sql, params);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		java.util.Map<String, Encryptor> encryptor = g.getEncryptor(node);
		try {
			stmt = g.prepareStatement(s);
			expr = xpath.compile("params/param");
			NodeList param = (NodeList)expr.evaluate(node, XPathConstants.NODESET);
			int index = 1;
			for(int x = 0; x < param.getLength(); x++) {
				Element p = (Element)param.item(x);
				if(!p.hasAttribute("cond") || AuthParser.auth(p.getAttribute("cond"), params)) {
					g.bind(
						stmt, 
						p.getAttribute("datatype"), 
						index, 
						new String[] {p.getAttribute("value")}, 
						-1, 
						p.getAttribute("default"), 
						p.getAttribute("pattern"), 
						null, 
						null, 
						encryptor, 
						p.getAttribute("encrypt"), 
						(Buffer)null
					);
					index++;
				}
			}
			rs = stmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			if(rs.next()) {
				for(int x = 1; x <= rsmd.getColumnCount(); x++) {
					if(rs.getString(x) != null) {
						params.puts("prop." + node.getAttribute("name") + "." + rsmd.getColumnName(x), rs.getString(x));
						if(node.hasAttribute("public") && node.getAttribute("public") != null && node.getAttribute("public").equals("true")) {
							params.puts("prop." + node.getAttribute("name") + "." + rsmd.getColumnName(x) + ".public", "true");
						}
					}
				}
			}
			DB.close(rs);
			DB.close(stmt);
		} catch (SQLException | NoSuchProviderException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LOG.toString(e));
			}
		} finally {
			DB.close(rs);
			DB.close(stmt);
		}
	}
}