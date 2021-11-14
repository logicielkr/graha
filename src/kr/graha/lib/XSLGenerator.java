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
import java.sql.Connection;
import java.util.Iterator;
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
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;

import kr.graha.helper.LOG;


/**
 * Graha(그라하) XSL 생성기

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class XSLGenerator {
	Element _query;
	Record _params;
	Connection _con;
	XPathFactory _factory;
	XPath _xpath;
	XPathExpression _expr;
	File _config;
	
	XMLTag _tag;
	HTMLTag _html;
	String _defaultName = null;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public XSLGenerator(
		Element query, 
		Record params, 
		Connection con, 
		File config, 
		HttpServletRequest request
	) {
		this._query = query;
		this._params = params;
		if(con != null) {
			this._con = con;
		}
		this._config = config;
		
		this._factory = XPathFactory.newInstance();
		this._xpath = this._factory.newXPath();

		this._expr = null;
		
		this._tag = new XMLTag(this._query.getAttribute("output"), this._query.getAttribute("uc"), request);
		this._html = new HTMLTag(this._query.getAttribute("htmltype"));
		LOG.setLogLevel(logger);
	}
	public Buffer execute() throws Exception {
		Buffer sb = new Buffer();
		if(this._params.hasKey("header.method") && (this._params.getString("header.method")).equals("ERROR")) {
			sb.append(this.error());
		} else if(this._query.getAttribute("funcType").equals("list") || this._query.getAttribute("funcType").equals("listAll")) {
			this._expr = this._xpath.compile("commands/command");
			NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(commands.getLength() > 0) { 
				this._defaultName = ((Element)commands.item(0)).getAttribute("name");
			}
			sb.append(this.before());
			sb.append(this.list());
			sb.append(this.after());
			
		} else if(this._query.getAttribute("funcType").equals("detail")) {
			this._expr = this._xpath.compile("commands/command");
			NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(commands.getLength() > 0) { 
				this._defaultName = ((Element)commands.item(0)).getAttribute("name");
			}
			sb.append(this.before());
			sb.append(this.detail());
			sb.append(this.after());
		} else if(
			this._query.getAttribute("funcType").equals("insert") 
			&& this._params.hasKey("header.method") 
			&& (this._params.getString("header.method")).equals("GET")
		) {
			this._expr = this._xpath.compile("tables/table");
			NodeList commands = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(commands.getLength() > 0) { 
				this._defaultName = ((Element)commands.item(0)).getAttribute("name");
			}
			sb.append(this.before());
			sb.append(this.insert());
			sb.append(this.after());
		} else if(
			this._params.hasKey("header.method") 
			&& (this._params.getString("header.method")).equals("POST")
		) {
			sb.append(this.post());
		}

		return sb;
	}
	private Buffer list() throws XPathExpressionException {
		Buffer sb = new Buffer();
		this._expr = this._xpath.compile("layout");
		Element layout = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		if(layout == null && this._query.hasAttribute("extends")) {
			this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/layout");
			layout = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
		}
		if(layout.hasAttribute("template") && layout.getAttribute("template").equals("native")) {
			sb.append(layout.getTextContent());
			return sb;
		}
		sb.append(this.nav(layout, "top"));
		this._expr = this._xpath.compile("middle/tab");
		NodeList tabs = (NodeList)this._expr.evaluate(layout, XPathConstants.NODESET);
		
		for(int y = 0; y < tabs.getLength(); y++) {
			if(y > 0) {break;}
			Element tab = (Element)tabs.item(y);
			if(tab.hasAttribute("name")) {
				sb.appendL(this._html.table(tab.getAttribute("name")));
			} else {
				sb.appendL(this._html.table(null));
			}
			sb.appendL(this._html.thead());
			sb.appendL(this._html.tr());
			this._expr = this._xpath.compile("column");
			NodeList columns = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
			for(int i = 0; i < columns.getLength(); i++) {
				Element column = (Element)columns.item(i);
				if(column.hasAttribute("width")) {
					sb.appendL(this._html.th(column.getAttribute("name")) + " style=\"width:" + column.getAttribute("width") + "\">" + column.getAttribute("label") + this._html.thE());
				} else {
					sb.appendL(this._html.th(column.getAttribute("name")) + ">" + column.getAttribute("label") + this._html.thE());
				}
			}
			sb.appendL(this._html.trE());
			sb.appendL(this._html.theadE());
			sb.appendL(this._html.tbody());
			if(tabs.getLength() == 1) {
				
				sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", null) + "\">");
			} else {
				sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
			}
			sb.appendL(this._html.tr());
			for(int i = 0; i < columns.getLength(); i++) {
				Element column = (Element)columns.item(i);
					sb.appendL(this._html.td(column.getAttribute("name")));
					if(column.hasAttribute("align")) {
						sb.append(" align=\"" + column.getAttribute("align") + "\"");
					}
					if(column.hasAttribute("width")) {
						sb.append(" style=\"width:" + column.getAttribute("width") + "\"");
					}
					sb.append(">");
					sb.append(this.column(column, false, tab.getAttribute("name")));
					sb.appendL(this._html.tdE());
			}
			sb.appendL(this._html.trE());
			sb.appendL("</xsl:for-each>");
			sb.appendL(this._html.tbodyE());
			sb.appendL(this._html.tableE());
		}
		sb.append(this.nav(layout, "bottom"));
		return sb;
	}
	private Buffer column(
		Element column,
		boolean isFull,
		String tableName
	) throws XPathExpressionException {
		Buffer sb = new Buffer();
		if(column.getChildNodes().getLength() > 0) {
			this._expr = this._xpath.compile("link");
			Element link = (Element)this._expr.evaluate(column, XPathConstants.NODE);
			if(link != null) {
				sb.append("<a>");
				sb.append("<xsl:attribute name=\"href\">");
				if(link.hasAttribute("type") && link.getAttribute("type").equals("query")) {
					sb.append("<xsl:value-of select=\"" + this._tag.path("row", link.getAttribute("value"), tableName, isFull) + "\"/>");
				} else if(link.hasAttribute("type") && link.getAttribute("type").equals("external")) {
					sb.append(link.getAttribute("path"));
				} else {
					sb.append(this.getPath(link.getAttribute("path")));
				}
				this._expr = this._xpath.compile("param");
				NodeList param = (NodeList)this._expr.evaluate(link, XPathConstants.NODESET);
				for(int x = 0; x < param.getLength(); x++) {
					Element p = (Element)param.item(x);
					if(x > 0) {
						sb.append("&amp;");
					} else {
						sb.append("?");
					}
					sb.append(p.getAttribute("name") + "=");
					if(p.getAttribute("type").equals("query")) {
						sb.append("<xsl:value-of select=\"" + this._tag.path("row", p.getAttribute("value"), tableName, isFull) + "\"/>");
					} else if(p.getAttribute("type").equals("param")) {
						sb.append("<xsl:value-of select=\"" + this._tag.path("param", p.getAttribute("value"), null, true) + "\"/>");
					} else if(p.getAttribute("type").equals("prop")) {
						sb.append("<xsl:value-of select=\"" + this._tag.path("prop", p.getAttribute("value"), null, true) + "\"/>");
					} else if(p.getAttribute("type").equals("default") || p.getAttribute("type").equals("const")) {
						sb.append(p.getAttribute("value"));
					}
				}
				sb.append("</xsl:attribute>");
				sb.append(this.code(column, isFull, tableName));
				sb.append("</a>");
			} else {
				sb.append(this.code(column, isFull, tableName));
			}
		} else {
			sb.append(this.code(column, isFull, tableName));
		}
		return sb;
	}
	private Buffer code(
		Element column, 
		boolean isFull, 
		String tableName
	) throws XPathExpressionException {
		Buffer sb = new Buffer();
		if(column.hasAttribute("code") && column.getAttribute("code").equals("true")) {
			if(column.hasAttribute("for") && column.getAttribute("for") != null && !column.getAttribute("for").equals("")) {
				sb.appendL("<xsl:variable name=\"" + column.getAttribute("name") + "\" select=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + "\" />");
				sb.appendL("<xsl:value-of select=\"" + this._tag.path("code", "option", column.getAttribute("for"), isFull) + "[@" + this._tag.path("code", "value", null, isFull) + " = $" + column.getAttribute("name") + "]/@" + this._tag.path("code", "label", null, isFull) + "\" />");
			} else {
				this._expr = this._xpath.compile("option");
				NodeList param = (NodeList)this._expr.evaluate(column, XPathConstants.NODESET);
				if(param.getLength() > 0) {
					sb.appendL("<xsl:choose>");
					for(int x = 0; x < param.getLength(); x++) {
						Element p = (Element)param.item(x);
						sb.appendL("<xsl:when test=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + " = '" + p.getAttribute("value") + "'\">");
						sb.appendL(p.getAttribute("label"));
						sb.appendL("</xsl:when>");
					}
					sb.appendL("<xsl:otherwise>");
					sb.appendL("<xsl:value-of select=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + "\" />");
					sb.appendL("</xsl:otherwise>");
					sb.appendL("</xsl:choose>");
				} else {
					sb.appendL("<xsl:value-of select=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + "\" />");
				}
			}
		} else {
			if(column.hasAttribute("fmt")) {
				sb.appendL("<xsl:choose>");
				sb.appendL("<xsl:when test=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + " != ''\">");
				sb.appendL("<xsl:value-of select=\"format-number(" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + ", '" + column.getAttribute("fmt") + "')\" />");
				sb.appendL("</xsl:when>");
				sb.appendL("<xsl:otherwise>");
				sb.appendL("<xsl:value-of select=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + "\" />");
				sb.appendL("</xsl:otherwise>");
				sb.appendL("</xsl:choose>");
			} else {
				sb.appendL("<xsl:value-of select=\"" + this._tag.path("row", column.getAttribute("name"), tableName, isFull) + "\" />");
			}
		}
		return sb;
	}
	private Buffer detail() throws XPathExpressionException {
		Buffer sb = new Buffer();
		this._expr = this._xpath.compile("layout");
		Element layout = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		
		if(layout == null && this._query.hasAttribute("extends")) {
			this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/layout");
			layout = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
		}
		if(layout.hasAttribute("template") && layout.getAttribute("template").equals("native")) {
			sb.append(layout.getTextContent());
			return sb;
		}
		sb.append(this.nav(layout, "top"));
		this._expr = this._xpath.compile("middle/tab");
		NodeList tabs = (NodeList)this._expr.evaluate(layout, XPathConstants.NODESET);
		
		this._expr = this._xpath.compile("files/file");
		NodeList files = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		
		if(tabs.getLength() > 1) {
			sb.appendL("<ul class=\"multitab\">");
			for(int y = 0; y < tabs.getLength(); y++) {
				Element tab = (Element)tabs.item(y);
				fileLI(files, sb, tab.getAttribute("name"), null);
				if(tab.hasAttribute("label")) {
					sb.appendL("<li class=\"" + tab.getAttribute("name") + "\">" + tab.getAttribute("label") + "</li>");
				}
				fileLI(files, sb, null, tab.getAttribute("name"));
			}
			fileLI(files, sb, null, null);
			sb.appendL("</ul>");
			sb.appendL("<div class=\"space\" />");
		}

		for(int y = 0; y < tabs.getLength(); y++) {
			Element tab = (Element)tabs.item(y);
			file(files, sb, (tabs.getLength() > 1), tab.getAttribute("name"), null, true);
			if(tab.hasAttribute("label") && tabs.getLength() > 1) {
				sb.appendL("<h3 class=\"" + tab.getAttribute("name") + "\">" + tab.getAttribute("label") + "</h3>");
			}
			if(tab.hasAttribute("name")) {
				sb.appendL(this._html.table(tab.getAttribute("name")));
			} else {
				sb.appendL(this._html.table(null));
			}
			
			this._expr = this._xpath.compile("commands/command[@name='" + tab.getAttribute("name") + "']");
			Element table = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
			if(table == null && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/commands/command[@name='" + tab.getAttribute("name") + "']");
				table = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
			}
			this._expr = this._xpath.compile("row");
			NodeList rows = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
			if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() == 1) {
				sb.appendL(this._html.thead());
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					sb.appendL(this._html.tr());
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);
						sb.append(this.td(col, "th"));
						sb.appendL(col.getAttribute("label") + this._html.thE());
					}
					sb.appendL(this._html.trE());
				}
				sb.appendL(this._html.theadE());
				sb.appendL(this._html.tbody());
				
				sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					sb.appendL(this._html.tr());
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);
						
						sb.append(this.td(col, "td"));
						sb.append(this.column(col, false, tab.getAttribute("name")));
//						sb.append(this.code(col, false, tab.getAttribute("name")));
						sb.appendL(this._html.tdE());
						
					}
					sb.appendL(this._html.trE());
				}
				sb.appendL("</xsl:for-each>");
				sb.appendL(this._html.tbodyE());
			} else {
				if(tab.hasAttribute("single") && tab.getAttribute("single").equals("true")) {
					sb.appendL(this._html.thead());
					for(int i = 0; i < rows.getLength(); i++) {
						Element row = (Element)rows.item(i);
						this._expr = this._xpath.compile("column");
						NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
						sb.appendL(this._html.tr());
						for(int x = 0; x < cols.getLength(); x++) {
							Element col = (Element)cols.item(x);
							sb.append(this.td(col, "th"));
							sb.appendL(col.getAttribute("label") + this._html.thE());
						}
						sb.appendL(this._html.trE());
					}
					sb.appendL(this._html.theadE());
				}
				sb.appendL(this._html.tbody());
				if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
					sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
				}
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					sb.appendL(this._html.tr());
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);
						sb.appendL(this._html.thG(col.getAttribute("name")));
						if(!tab.hasAttribute("single") || !tab.getAttribute("single").equals("true")) {
							if(!col.hasAttribute("islabel") || !col.getAttribute("islabel").equals("false")) {
								if(col.hasAttribute("labelWidth")) {
									sb.appendL(this._html.th(col.getAttribute("name")) + " style=\"width:" + col.getAttribute("labelWidth") + ";\">" + col.getAttribute("label") + this._html.thE());
								} else {
									sb.appendL(this._html.th(col.getAttribute("name")) + ">" + col.getAttribute("label") + this._html.thE());
								}
							}
						}
						sb.append(this.td(col, "td"));
						if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
//							sb.append(this.code(col, false, tab.getAttribute("name")));
							sb.append(this.column(col, false, tab.getAttribute("name")));
						} else {
//							sb.append(this.code(col, true, tab.getAttribute("name")));
							sb.append(this.column(col, true, tab.getAttribute("name")));
						}
						sb.appendL(this._html.tdE());
						sb.appendL(this._html.tdGE());
					}
					sb.appendL(this._html.trE());
				}
				if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
					sb.appendL("</xsl:for-each>");
				}
				sb.appendL(this._html.tbodyE());
			}
			sb.appendL(this._html.tableE());
			file(files, sb, (tabs.getLength() > 1), null, tab.getAttribute("name"), true);
		}
		file(files, sb, (tabs.getLength() > 1), null, null, true);
		sb.append(this.nav(layout, "bottom"));
		return sb;
		
	}
	private void fileLI(NodeList files, Buffer sb, String before, String after) {
		if(files != null && files.getLength() > 0 && FileHelper.isAllow(this._query, this._params)) {
			for(int y = 0; y < files.getLength(); y++) {
				Element file = (Element)files.item(y);
				if(
					(
						before != null && 
						file.hasAttribute("before") && 
						file.getAttribute("before") != null &&
						before.equals(file.getAttribute("before"))
					) ||
					(
						after != null && 
						file.hasAttribute("after") && 
						file.getAttribute("after") != null &&
						after.equals(file.getAttribute("after"))
					) ||
					(
						before == null &&
						after == null &&
						!file.hasAttribute("before") &&
						!file.hasAttribute("after")
					)
				) {
					if(file.hasAttribute("label")) {
						sb.appendL("<li class=\"" + file.getAttribute("name") + "\">" + file.getAttribute("label") + "</li>");
					}
				}
			}
		}
	}
	private void file(NodeList files, Buffer sb, boolean isPrintH3, String before, String after, boolean isDetail) {
		if(files != null && files.getLength() > 0 && FileHelper.isAllow(this._query, this._params)) {
			for(int y = 0; y < files.getLength(); y++) {
				Element file = (Element)files.item(y);
				if(
					(
						before != null && 
						file.hasAttribute("before") && 
						file.getAttribute("before") != null &&
						before.equals(file.getAttribute("before"))
					) ||
					(
						after != null && 
						file.hasAttribute("after") && 
						file.getAttribute("after") != null &&
						after.equals(file.getAttribute("after"))
					) ||
					(
						before == null &&
						after == null &&
						!file.hasAttribute("before") &&
						!file.hasAttribute("after")
					)
				) {
					if(file.hasAttribute("label") && isPrintH3) {
						sb.appendL("<h3 class=\"" + file.getAttribute("name") + "\">" + file.getAttribute("label") + "</h3>");
					}
					if(file.hasAttribute("name")) {
						sb.appendL("<ul id=\"" + file.getAttribute("name") + "\">");
					} else {
						sb.appendL("<ul>");
					}
					sb.appendL("<xsl:for-each select=\"" + this._tag.path("file", file.getAttribute("name")) + "\">");
					sb.appendL("<xsl:sort select=\"node() = false()\"/>");
					sb.appendL("<xsl:sort select=\"" + this._tag.path("file", "name", null, false) + "\" />");
					if(isDetail) {
						sb.appendL("<xsl:variable name=\"downloadparam\">");
						sb.appendL("<xsl:for-each select=\"" + this._tag.path("fileparam", file.getAttribute("name")) + "\"><xsl:value-of select=\"" + this._tag.path("fileparam", "key", file.getAttribute("name"), false) + "\" /><xsl:text>=</xsl:text><xsl:value-of select=\"" + this._tag.path("fileparam", "value", file.getAttribute("name"), false) + "\" /><xsl:text>&amp;</xsl:text></xsl:for-each>");
						sb.appendL("</xsl:variable>");
						sb.appendL("<li length=\"{" + this._tag.path("file", "length", null, false) + "}\" lastModified=\"{" + this._tag.path("file", "lastModified", null, false) + "}\">");
						sb.appendL("<a href=\"" + this.getPath(this._query.getAttribute("id")) + "/download/" + file.getAttribute("name") + "/{" + this._tag.path("file", "name2", null, false) + "}?{$downloadparam}\">");
						
						sb.appendL("<xsl:value-of select=\"" + this._tag.path("file", "name", null, false) + "\" />");
						sb.appendL("</a>");
						sb.appendL("</li>");
					} else {
						sb.appendL("<xsl:choose>");
						sb.appendL("<xsl:when test=\"not(" + this._tag.path("file", "name", null, false) + ")\">");
						sb.appendL("<li><input type=\"file\" class=\"" + file.getAttribute("name") + "\" name=\"" + file.getAttribute("name") + ".{position()}\"  multiple=\"multiple\" /></li>");
						sb.appendL("</xsl:when>");
						sb.appendL("<xsl:otherwise>");
						sb.appendL("<xsl:variable name=\"downloadparam\">");
						sb.appendL("<xsl:for-each select=\"" + this._tag.path("fileparam", file.getAttribute("name")) + "\"><xsl:value-of select=\"" + this._tag.path("fileparam", "key", file.getAttribute("name"), false) + "\" /><xsl:text>=</xsl:text><xsl:value-of select=\"" + this._tag.path("fileparam", "value", file.getAttribute("name"), false) + "\" /><xsl:text>&amp;</xsl:text></xsl:for-each>");
						sb.appendL("</xsl:variable>");
						sb.appendL("<li length=\"{" + this._tag.path("file", "length", null, false) + "}\" lastModified=\"{" + this._tag.path("file", "lastModified", null, false) + "}\">");
						sb.appendL("<input type=\"checkbox\" class=\"_deletefile_." + file.getAttribute("name") + "\" name=\"_deletefile_." + file.getAttribute("name") + ".{position()}\" value=\"{" + this._tag.path("file", "name", null, false) + "}\" />");
						sb.appendL("<a href=\"" + this.getPath(this._query.getAttribute("id")) + "/download/" + file.getAttribute("name") + "/{" + this._tag.path("file", "name2", null, false) + "}?{$downloadparam}\">");
						
						sb.appendL("<xsl:value-of select=\"" + this._tag.path("file", "name", null, false) + "\" />");
						sb.appendL("</a>");
						sb.appendL("</li>");
						sb.appendL("</xsl:otherwise>");
						sb.appendL("</xsl:choose>");
					}
					sb.appendL("</xsl:for-each>");
					sb.appendL("</ul>");
				}
			}
		}
	}
	private String getPath(String href) {
		if(href.startsWith("/")) {
			if(href.endsWith("!")) {
				return this._params.getString("system.prefix") + href.substring(0, href.length() - 1);
			} else {
				return this._params.getString("system.prefix") + href + this._params.getString("system.suffix");
			}
		} else if(href == null || href.trim().equals("")) {
			return "";
		} else {
			if(href.endsWith("!")) {
				return this._params.getString("system.prefix") + this._params.getString("system.config.file.name") + href.substring(0, href.length() - 1);
			} else {
				return this._params.getString("system.prefix") + this._params.getString("system.config.file.name") + href + this._params.getString("system.suffix");
			}
		}
	}
	private Buffer insert() throws XPathExpressionException {
		Buffer sb = new Buffer();
		this._expr = this._xpath.compile("layout");
		Element layout = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		
		if(layout == null && this._query.hasAttribute("extends")) {
			this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/layout");
			layout = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
		}
		
		if(layout.hasAttribute("template") && layout.getAttribute("template").equals("native")) {
			sb.append(layout.getTextContent());
			return sb;
		}
		sb.append(this.nav(layout, "top"));
		
		this._expr = this._xpath.compile("files/file");
		NodeList files = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		if(files != null && files.getLength() > 0 && FileHelper.isAllow(this._query, this._params)) {
			sb.appendL("<form enctype=\"multipart/form-data\" name=\"" + this._query.getAttribute("id") + "\" id=\"" + this._query.getAttribute("id") + "\" action=\"" + this.getPath(this._query.getAttribute("id")) + "\" method=\"post\" onsubmit=\"return (document.getElementById('" + this._query.getAttribute("id") + "_submit').form == null) || check_submit(this, '" + layout.getAttribute("msg") + "');\">");
		} else {
			sb.appendL("<form name=\"" + this._query.getAttribute("id") + "\" id=\"" + this._query.getAttribute("id") + "\" action=\"" + this.getPath(this._query.getAttribute("id")) + "\" method=\"post\" onsubmit=\"return (document.getElementById('" + this._query.getAttribute("id") + "_submit').form == null) || check_submit(this, '" + layout.getAttribute("msg") + "');\">");
		}
		String[] position = {"top/left", "top/center", "top/right", "bottom/left", "bottom/center", "bottom/right"};
		
		for (String value : position) {
			this._expr = this._xpath.compile(value + "/link[@full='true']/params/param");
			NodeList inputs = (NodeList)this._expr.evaluate(layout, XPathConstants.NODESET);
			if(inputs.getLength() > 0) {
				for(int x = 0; x < inputs.getLength(); x++) {
					Element input = (Element)inputs.item(x);
					
					String type = input.getAttribute("type");
					if(type == null || type.equals("")) {
						type = "param";
					}
					String ref = input.getAttribute("ref");
					if(ref == null || ref.equals("")) {
						ref = this._defaultName;
					}
					
					sb.appendL("<input type=\"hidden\" class=\""+ input.getAttribute("name") + "\" name=\""+ input.getAttribute("name") + "\" value=\"{" + this._tag.path(type, input.getAttribute("value"), ref, true) + "}\" />");
				}
				break;
			}
		}
		
		this._expr = this._xpath.compile("middle/tab");
		NodeList tabs = (NodeList)this._expr.evaluate(layout, XPathConstants.NODESET);
		if(tabs.getLength() > 1) {
			sb.appendL("<ul class=\"multitab\">");
			for(int y = 0; y < tabs.getLength(); y++) {
				Element tab = (Element)tabs.item(y);
				fileLI(files, sb, tab.getAttribute("name"), null);
				if(tab.hasAttribute("label")) {
					sb.appendL("<li class=\"" + tab.getAttribute("name") + "\">" + tab.getAttribute("label") + "</li>");
				}
				fileLI(files, sb, null, tab.getAttribute("name"));
			}
			fileLI(files, sb, null, null);
			sb.appendL("</ul>");
			sb.appendL("<div class=\"space\" />");
		}
		for(int y = 0; y < tabs.getLength(); y++) {
			Element tab = (Element)tabs.item(y);
			file(files, sb, (tabs.getLength() > 1), tab.getAttribute("name"), null, false);
			if(tab.hasAttribute("label") && tabs.getLength() > 1) {
				sb.appendL("<h3 class=\"" + tab.getAttribute("name") + "\">" + tab.getAttribute("label") + "</h3>");
			}
			if(tab.hasAttribute("name")) {
				sb.appendL(this._html.table(tab.getAttribute("name")));
			} else {
				sb.appendL(this._html.table(null));
			}
			
			this._expr = this._xpath.compile("tables/table[@name='" + tab.getAttribute("name") + "']");
			Element table = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
			this._expr = this._xpath.compile("row");
			NodeList rows = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
			if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() == 1) {
				Element row = (Element)rows.item(0);
				this._expr = this._xpath.compile("column");
				NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
				sb.appendL(this._html.thead());
				sb.appendL(this._html.tr());
				for(int x = 0; x < cols.getLength(); x++) {
					Element col = (Element)cols.item(x);
					if(col.hasAttribute("width")) {
						sb.appendL(this._html.th(col.getAttribute("name")) + " style=\"width:" + col.getAttribute("width") + ";\">" + col.getAttribute("label") + this._html.thE());
					} else {
						sb.appendL(this._html.th(col.getAttribute("name")) + ">" + col.getAttribute("label") + this._html.thE());
					}
				}
				sb.appendL(this._html.trE());
				sb.appendL(this._html.theadE());
				sb.appendL(this._html.tbody());
				sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
				sb.appendL(this._html.tr());
				for(int x = 0; x < cols.getLength(); x++) {
					Element col = (Element)cols.item(x);
					if(col.hasAttribute("width")) {
						sb.appendL(this._html.td(col.getAttribute("name")) + " style=\"width:" + col.getAttribute("width") + ";\">");
					} else {
						sb.appendL(this._html.td(col.getAttribute("name")) + ">");
					}
					if(x == 0) {
						this._expr = this._xpath.compile("column[@type = 'hidden']");
						NodeList hidden = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
						for(int q = 0;q < hidden.getLength(); q++) {
							Element h = (Element)hidden.item(q);

							sb.append("<input type=\"hidden\" class=\"" + h.getAttribute("name") + "\" name=\"" + h.getAttribute("name") + ".{position()}\" value=\"{" + "" + this._tag.path("row", h.getAttribute("value"), tab.getAttribute("name"), false) + "}\" />");
							
						}
					}
					sb.append(this.input(col, false, tab.getAttribute("name")));
					sb.appendL(this._html.tdE());
				}
				sb.appendL(this._html.trE());
				sb.appendL("</xsl:for-each>");
				sb.appendL(this._html.tbodyE());
			} else if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && tab.hasAttribute("single") && tab.getAttribute("single").equals("true")) {
				sb.appendL(this._html.thead());
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					sb.appendL(this._html.tr());
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);
						sb.append(this.td(col, "th"));
						sb.appendL(col.getAttribute("label") + this._html.thE());
					}
					sb.appendL(this._html.trE());
				}
				sb.appendL(this._html.theadE());
				sb.appendL(this._html.tbody());
				sb.appendL("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					sb.appendL(this._html.tr());
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);

						sb.append(this.td(col, "td"));
						if(x == 0 && i == 0) {
							this._expr = this._xpath.compile("column[@type = 'hidden']");
							NodeList hidden = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
							for(int q = 0;q < hidden.getLength(); q++) {
								Element h = (Element)hidden.item(q);
								
								sb.append("<input type=\"hidden\" class=\"" + h.getAttribute("name") + "\" name=\"" + h.getAttribute("name") + ".{position()}\" value=\"{" + "" + this._tag.path("row", h.getAttribute("value"), tab.getAttribute("name"), false) + "}\" />");
								
							}
						}
						sb.append(this.input(col, false, tab.getAttribute("name")));
						sb.appendL(this._html.tdE());
					}
					sb.appendL(this._html.trE());
				}
				sb.append("</xsl:for-each>");
				sb.appendL(this._html.tbodyE());
			} else {
				sb.appendL(this._html.tbody());
				if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
					sb.append("<xsl:for-each select=\"" + this._tag.path("row", tab.getAttribute("name")) + "\">");
				}
				for(int i = 0; i < rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					if(row.hasAttribute("height")) {
						sb.append(this._html.trS() + " style=\"height:" + row.getAttribute("height") + ";\">");
					} else {
						sb.appendL(this._html.tr());
					}
					this._expr = this._xpath.compile("column");
					NodeList cols = (NodeList)this._expr.evaluate(row, XPathConstants.NODESET);
					for(int x = 0; x < cols.getLength(); x++) {
						Element col = (Element)cols.item(x);
						sb.appendL(this._html.thG(col.getAttribute("name")));
						if(!col.hasAttribute("islabel") || !col.getAttribute("islabel").equals("false")) {
							if(col.hasAttribute("labelWidth")) {
								sb.append(this._html.th(col.getAttribute("name")) + " style=\"width:" + col.getAttribute("labelWidth") + ";\">" + col.getAttribute("label") + this._html.thE());
							} else {
								sb.append(this._html.th(col.getAttribute("name")) + ">" + col.getAttribute("label") + this._html.thE());
							}
						}
						sb.append(this.td(col, "td"));
						
						if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
							if(i == 0 && x == 0) {
								this._expr = this._xpath.compile("column[@type = 'hidden']");
								NodeList hidden = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
								for(int q = 0;q < hidden.getLength(); q++) {
									Element h = (Element)hidden.item(q);
									sb.append("<input type=\"hidden\" class=\"" + h.getAttribute("name") + "\" name=\"" + h.getAttribute("name") + ".{position()}\" value=\"{" + "" + this._tag.path("row", h.getAttribute("value"), tab.getAttribute("name"), false) + "}\" />");
								}
							}
							sb.append(this.input(col, false, tab.getAttribute("name")));
						} else {
							if(i == 0 && x == 0) {
								this._expr = this._xpath.compile("column[@foreign='true']");
								NodeList foreign = (NodeList)this._expr.evaluate(table, XPathConstants.NODESET);
								if(foreign.getLength() > 0) {
									this._expr = this._xpath.compile("column[@type = 'hidden']");
									NodeList hidden = (NodeList)this._expr.evaluate(tab, XPathConstants.NODESET);
									for(int q = 0;q < hidden.getLength(); q++) {
										Element h = (Element)hidden.item(q);
										sb.append("<input type=\"hidden\" class=\"" + h.getAttribute("name") + "\" name=\"" + h.getAttribute("name") + "\" value=\"{" + "" + this._tag.path("row", h.getAttribute("value"), tab.getAttribute("name"), true) + "}\" />");
									}
								}
							}
							sb.append(this.input(col, true, tab.getAttribute("name")));
						}
						sb.append(this._html.tdE());
						sb.appendL(this._html.tdGE());
					}
					sb.append(this._html.trE());
				}
				if(table != null && table.hasAttribute("multi") && table.getAttribute("multi").equals("true") && rows.getLength() > 1) {
					sb.append("</xsl:for-each>");
				}
				sb.append(this._html.tbodyE());
			}
			sb.append(this._html.tableE());
			file(files, sb, (tabs.getLength() > 1), null, tab.getAttribute("name"), false);
		}
		file(files, sb, (tabs.getLength() > 1), null, null, false);
		sb.append("</form>");
		sb.append(this.nav(layout, "bottom"));
		return sb;

	}
	private Buffer td(Element col, String tagName) throws XPathExpressionException {
		Buffer sb = new Buffer();
		
		if(tagName != null && tagName.equals("td")) {
			sb.append(this._html.td(col.getAttribute("name")));
		} else if(tagName != null && tagName.equals("th")) {
			sb.append(this._html.th(col.getAttribute("name")));
		} else {
			throw new ParsingException();
		}
		if(col.hasAttribute("height") || col.hasAttribute("width")) {
			sb.append(" style=\"");
			if(col.hasAttribute("height")) {
				sb.append("height:" + col.getAttribute("height") + ";");
			}
			if(col.hasAttribute("width")) {
				sb.append("width:" + col.getAttribute("width") + ";");
			}
			sb.append("\"");
		}
		
		if(col.hasAttribute("colspan")) {
			sb.append(" colspan=\"" + col.getAttribute("colspan") + "\"");
		}
		if(col.hasAttribute("rowspan")) {
			sb.append(" rowspan=\"" + col.getAttribute("rowspan") + "\"");
		}
		sb.append(">");
		return sb;
	}
	private Buffer input(
		Element col, 
		boolean isFull, 
		String tableName
	) throws XPathExpressionException {
		Buffer sb = new Buffer();
		String name = col.getAttribute("name");
		String value = col.getAttribute("value");
		String upperName = "row";
		if(value.startsWith("param.")) {
			value = value.substring(6);
			upperName = "param";
			isFull = true;
		} else if(value.startsWith("prop.")) {
			value = value.substring(5);
			upperName = "prop";
			isFull = true;
		} else if(value.startsWith("query.")) {
			value = value.substring(6);
		}
		String readonly = "";
		String readonlyClass = "";
		if(this._params.in(col, "readonly", new String[]{"readonly", "true"})) {
			readonly = " readonly=\"readonly\"";
			readonlyClass = " readonly";
		}
		String expr = "";
		String follow = "";
		String datatype = "";
		String constraint = "";
		String autocomplete = "";
		String disabled = "";
		String valueExpr = null;
		String valueExpr2 = null;
		if(value == null || value.trim().equals("")) {
			valueExpr = "";
			valueExpr2 = "";
		} else {
			valueExpr = this._tag.path(upperName, value, tableName, isFull);
			valueExpr2 = "{" + valueExpr + "}";
		}
		this._expr = this._xpath.compile("tables/table[@name = '" + tableName + "']/column[@name = '" + value + "']");
		Element e = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		if(e != null) {
			datatype = " datatype = \"" + e.getAttribute("datatype") + "\"";
			if(e.hasAttribute("expr")) {
				expr = " expr = \"" + e.getAttribute("expr") + "\"";
			}
			if(e.hasAttribute("follow")) {
				follow = " follow = \"" + e.getAttribute("follow") + "\"";
			}
			if(e.hasAttribute("constraint")) {
				constraint = " constraint = \"" + e.getAttribute("constraint") + "\"";
			}
		}
		if(col.hasAttribute("autocomplete")) {
			autocomplete = " autocomplete = \"" + col.getAttribute("autocomplete") + "\"";
		}
		if(col.hasAttribute("disabled")) {
			disabled = " disabled = \"" + col.getAttribute("disabled") + "\"";
		}
		if(!col.hasAttribute("type") || this._params.in(col, "type", new String[]{"text", "password", "checkbox", "button"})) {
			if(this._params.equals(col, "type", "button") && this._params.isNotEmpty(col, "icon")) {
				sb.append("<button>");
			} else {
				sb.append("<input>");
			}
			sb.append("	<xsl:attribute name=\"class\">" + name + readonlyClass + "</xsl:attribute>");
			if(!col.hasAttribute("type")) {
				sb.append("	<xsl:attribute name=\"type\">text</xsl:attribute>");
			} else {
				sb.append("	<xsl:attribute name=\"type\">" + col.getAttribute("type") + "</xsl:attribute>");
			}
			if(!isFull) {
				sb.append("<xsl:attribute name=\"name\">" + name + ".<xsl:value-of select=\"position()\"/></xsl:attribute>");
			} else {
				sb.append("<xsl:attribute name=\"name\">" + name + "</xsl:attribute>");
			}
			if(valueExpr != null && !valueExpr.equals("")) {
				if(this._params.equals(col, "type", "checkbox")) {
					if(col.getAttribute("val") != null && !col.getAttribute("val").equals("")) {
						sb.append("<xsl:if test=\"" + valueExpr + " = '" + col.getAttribute("val") + "'\"><xsl:attribute name=\"checked\">checked</xsl:attribute></xsl:if>");
					}
					sb.append("	<xsl:attribute name=\"value\">" + col.getAttribute("val") + "</xsl:attribute>");
				} else if(this._params.equals(col, "type", "button") && !this._params.isNotEmpty(col, "icon")) {
					sb.append("	<xsl:attribute name=\"value\">" + value + "</xsl:attribute>");
				} else if(col.hasAttribute("fmt")) {
					sb.append("<xsl:choose>");
					sb.append("<xsl:when test=\"" + valueExpr + " != ''\">");
					sb.append("	<xsl:attribute name=\"value\"><xsl:value-of select=\"format-number(" + valueExpr + ", '" + col.getAttribute("fmt") + "')\"/></xsl:attribute>");
					sb.append("</xsl:when>");
					sb.append("<xsl:otherwise>");
					sb.append("	<xsl:attribute name=\"value\"><xsl:value-of select=\"" + valueExpr + "\"/></xsl:attribute>");
					sb.append("</xsl:otherwise>");
					sb.append("</xsl:choose>");
				} else {
						sb.append("	<xsl:attribute name=\"value\"><xsl:value-of select=\"" + valueExpr + "\"/></xsl:attribute>");
				}
			}
			if(this._params.in(col, "readonly", new String[]{"readonly", "true"})) {
				sb.append("	<xsl:attribute name=\"readonly\">readonly</xsl:attribute>");
			}
			if(e != null && e.hasAttribute("expr")) {
				sb.append("	<xsl:attribute name=\"expr\">" + e.getAttribute("expr") + "</xsl:attribute>");
			}
			if(e != null && e.hasAttribute("follow")) {
				sb.append("	<xsl:attribute name=\"follow\">" + e.getAttribute("follow") + "</xsl:attribute>");
			}
			if(e != null && e.hasAttribute("datatype")) {
				sb.append("	<xsl:attribute name=\"datatype\">" + e.getAttribute("datatype") + "</xsl:attribute>");
			}
			if(e != null && e.hasAttribute("constraint")) {
				sb.append("	<xsl:attribute name=\"constraint\">" + e.getAttribute("constraint") + "</xsl:attribute>");
			}
			if(col.hasAttribute("autocomplete")) {
				sb.append("	<xsl:attribute name=\"autocomplete\">" + col.getAttribute("autocomplete") + "</xsl:attribute>");
			}
			if(col.hasAttribute("disabled")) {
				sb.append("	<xsl:attribute name=\"disabled\">" + col.getAttribute("disabled") + "</xsl:attribute>");
			}
			if(this._params.equals(col, "type", "button") && this._params.isNotEmpty(col, "icon")) {
				if(this._params.isNotEmpty(col, "class")) {
					sb.append("<i class=\"" + col.getAttribute("class") + "\">" + col.getAttribute("icon") + "</i>");
				} else {
					sb.append("<i>" + col.getAttribute("icon") + "</i>");
				}
				sb.append("<span>" + value + "</span>");
				sb.append("</button>");
			} else {
				sb.append("</input>");
			}
		} else if(col.getAttribute("type").equals("textarea")) {
			if(!isFull) {
				sb.append("\n				<textarea class=\"" + name + " " + readonlyClass + "\" name=\"" + name + ".{position()}\" " + readonly + "><xsl:value-of select=\"" + valueExpr + "\" /></textarea>");
			} else {
				sb.append("\n				<textarea class=\"" + name + " " + readonlyClass + "\" name=\"" + name + "\" " + readonly + "><xsl:value-of select=\"" + valueExpr + "\" /></textarea>");
			}
		} else if(col.getAttribute("type").equals("radio")) {
			if(col.hasAttribute("for") && col.getAttribute("for") != null && !col.getAttribute("for").equals("")) {
				sb.append("<xsl:for-each select=\"" + this._tag.path("code", "option", col.getAttribute("for"), isFull) + "\">");
				sb.append("	<xsl:choose>");
				sb.append("	<xsl:when test=\"@" + this._tag.path("code", "value", null, isFull) + " = " + valueExpr + "\">");
				sb.append("		<input type=\"radio\" value=\"{@" + this._tag.path("code", "value", null, isFull) + "}\" checked=\"true\" />");
				sb.append("		<label><xsl:value-of select=\"@" + this._tag.path("code", "label", null, isFull) + "\" /></label>");
				sb.append("	</xsl:when>");
				sb.append("	<xsl:otherwise>");
				sb.append("		<input type=\"radio\" value=\"{@" + this._tag.path("code", "value", null, isFull) + "}\" />");
				sb.append("		<label><xsl:value-of select=\"@" + this._tag.path("code", "label", null, isFull) + "\" /></label>");
				sb.append("	</xsl:otherwise>");
				sb.append("	</xsl:choose>");
				sb.append("</xsl:for-each>");
				
			} else {
				this._expr = this._xpath.compile("option");
				NodeList options = (NodeList)this._expr.evaluate(col, XPathConstants.NODESET);
				for(int q = 0; q < options.getLength(); q++) {
					Element option = (Element)options.item(q);
					sb.append("<xsl:choose>");
					sb.append("<xsl:when test=\"" + valueExpr + " = '" + option.getAttribute("value") + "'\">");
					sb.append("		<input type=\"radio\" value=\"" + option.getAttribute("value") + "\" checked=\"true\" />");
					sb.append("		<label>" + option.getAttribute("label") + "</label>");
					sb.append("</xsl:when>");
					sb.append("<xsl:otherwise>");
					sb.append("		<input type=\"radio\" value=\"" + option.getAttribute("value") + "\" />");
					sb.append("		<label>" + option.getAttribute("label") + "</label>");
					sb.append("</xsl:otherwise>");
					sb.append("</xsl:choose>");
				}
			}
		} else if(col.getAttribute("type").equals("select")) {
			if(!isFull) {
				sb.append("<select class=\"" + name + " " + readonlyClass + "\" name=\"" + name + ".{position()}\" value=\"" + valueExpr2 + "\"" + readonly + expr + follow + datatype + constraint + disabled + ">");
			} else {
				sb.append("<select class=\"" + name + " " + readonlyClass + "\" name=\"" + name + "\" value=\"" + valueExpr2 + "\"" + readonly + expr + follow + datatype + constraint + disabled + ">");
			}
			if(col.hasAttribute("for") && col.getAttribute("for") != null && !col.getAttribute("for").equals("")) {
				sb.append("<xsl:variable name=\"selected\"><xsl:value-of select=\"" + valueExpr + "\" /></xsl:variable>");
				sb.append("<xsl:for-each select=\"" + this._tag.path("code", "option", col.getAttribute("for"), isFull) + "\">");
				sb.append("	<xsl:choose>");
				sb.append("	<xsl:when test=\"@" + this._tag.path("code", "value", null, isFull) + " = $selected\">");
				sb.append("		<option value=\"{@" + this._tag.path("code", "value", null, isFull) + "}\"  selected=\"selected\"><xsl:value-of select=\"@" + this._tag.path("code", "label", null, isFull) + "\" /></option>");
				sb.append("	</xsl:when>");
				sb.append("	<xsl:otherwise>");
				sb.append("		<option value=\"{@" + this._tag.path("code", "value", null, isFull) + "}\"><xsl:value-of select=\"@" + this._tag.path("code", "label", null, isFull) + "\" /></option>");
				sb.append("	</xsl:otherwise>");
				sb.append("	</xsl:choose>");
				sb.append("</xsl:for-each>");
				
			} else {
				this._expr = this._xpath.compile("option");
				NodeList options = (NodeList)this._expr.evaluate(col, XPathConstants.NODESET);
				for(int q = 0; q < options.getLength(); q++) {
					Element option = (Element)options.item(q);
					sb.append("<xsl:choose>");
					sb.append("<xsl:when test=\"" + valueExpr + " = '" + option.getAttribute("value") + "'\">");
					sb.append("<option value=\"" + option.getAttribute("value") + "\" selected=\"selected\">" + option.getAttribute("label") + "</option>");
					sb.append("</xsl:when>");
					sb.append("<xsl:otherwise>");
					sb.append("<option value=\"" + option.getAttribute("value") + "\">" + option.getAttribute("label") + "</option>");
					sb.append("</xsl:otherwise>");
					sb.append("</xsl:choose>");
				}
			}
			sb.append("</select>");
		} else if(col.getAttribute("type").equals("datalist")) {
			if(!isFull) {
				sb.append("<input class=\"" + name + " datalist " + readonlyClass + "\" type=\"text\" name=\"" + name + ".{position()}\" value=\"" + valueExpr2 + "\" list=\"" + name + ".{position()}\"" + readonly + expr + follow + datatype + constraint + autocomplete + disabled + " />");
				sb.append("<datalist  id=\"" + name + ".{position()}\">");
			} else {
				sb.append("<input class=\"" + name + " datalist " + readonlyClass + "\" type=\"text\" name=\"" + name + "\" value=\"" + valueExpr2 + "\" list=\"" + name + "\"" + readonly + expr + follow + datatype + constraint + autocomplete + disabled + " />");
				sb.append("<datalist id=\"" + name + "\">");
			}
			if(col.hasAttribute("for") && col.getAttribute("for") != null && !col.getAttribute("for").equals("")) {
				sb.append("<xsl:for-each select=\"" + this._tag.path("code", "option", col.getAttribute("for"), isFull) + "\">");
				sb.append("		<option value=\"{@" + this._tag.path("code", "value", null, isFull) + "}\"><xsl:value-of select=\"@" + this._tag.path("code", "label", null, isFull) + "\" /></option>");
				sb.append("</xsl:for-each>");
			} else {
				this._expr = this._xpath.compile("option");
				NodeList options = (NodeList)this._expr.evaluate(col, XPathConstants.NODESET);
				for(int q = 0; q < options.getLength(); q++) {
					Element option = (Element)options.item(q);
					sb.append("<option value=\"" + option.getAttribute("value") + "\">" + option.getAttribute("label") + "</option>");
				}
			}
			sb.append("</datalist>");
		} else if(col.getAttribute("type").equals("hidden")) {
			if(!isFull) {
				sb.append("<input type=\"hidden\" class=\"" + name + "\" name=\"" + name + ".{position()}\" value=\"" + valueExpr2 + "\" />");
			} else {
				sb.append("<input type=\"hidden\" class=\"" + name + "\" name=\"" + name + "\" value=\"" + valueExpr2 + "\" />");
			}
		} else {
			throw new ParsingException();
		}
		return sb;
	}
	private Buffer nav(Element layout, String position) throws XPathExpressionException {
		Buffer sb = new Buffer();
		sb.append("<div class=\"nav " + position + "\">");
		this._expr = this._xpath.compile(position + "/left");
		Element item = (Element)this._expr.evaluate(layout, XPathConstants.NODE);
		if(item == null || item.getChildNodes().getLength() == 0) {
			sb.append("<div class=\"box left\" />");
		} else {
			sb.append("<div class=\"box left\">");
			sb.append(this.inner(item, layout));
			sb.append("</div>");
		}
		this._expr = this._xpath.compile(position + "/center");
		item = (Element)this._expr.evaluate(layout, XPathConstants.NODE);
		if(item == null || item.getChildNodes().getLength() == 0) {
			sb.append("<div class=\"box center\" />");
		} else {
			sb.append("<div class=\"box center\">");
			sb.append(this.inner(item, layout));
			sb.append("</div>");
		}
		this._expr = this._xpath.compile(position + "/right");
		item = (Element)this._expr.evaluate(layout, XPathConstants.NODE);
		if(item == null || item.getChildNodes().getLength() == 0) {
			sb.append("<div class=\"box right\" />");
		} else {
			sb.append("<div class=\"box right\">");
			sb.append(this.inner(item, layout));
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb;
	}
	private Buffer inner(Element item, Element layout) throws XPathExpressionException {
		Buffer sb = new Buffer();
		if(item.getChildNodes().getLength() == 1 && (item.getTextContent()).equals("page")) {
			sb.append("<ul class=\"pages\">");
			sb.append("<xsl:for-each select=\"" + this._tag.path("page", null) + "\">");
			sb.append("<xsl:choose>");
			sb.append("<xsl:when test=\"" + this._tag.path("param", "page", null, true) + " = " + this._tag.path("page", "no", null, false) + "\">");
			
			sb.append("<li class=\"page selected\">");
			sb.append("<xsl:value-of select=\"" + this._tag.path("page", "text", null, false) + "\" />");
			sb.append("</li>");
			sb.append("</xsl:when>");
			sb.append("<xsl:otherwise>");
			sb.append("<li class=\"page\">");
			sb.append("<a href=\"?page={" + this._tag.path("page", "no", null, false) + "}");
			if(this._params != null && !this._params.isEmpty()) {
				Iterator<String> it = this._params.keySet().iterator();
				while(it.hasNext()) {
					String key = (String)it.next();
					if(key != null && key.startsWith("param.") && !key.equals("param.page")) {
						sb.append("&amp;" + key.substring(6) + "={" + this._tag.path("param", key.substring(6), null, true) + "}");
					}
				}
			}
			sb.append("\" style=\"text-decoration:none;\"><xsl:value-of select=\"" + this._tag.path("page", "text", null, false) + "\" /></a>");
			sb.append("</li>");
			sb.append("</xsl:otherwise>");
			sb.append("</xsl:choose>");
			sb.append("</xsl:for-each>");
			sb.append("</ul>");
		} else {
			this._expr = this._xpath.compile("link");
			NodeList links = (NodeList)this._expr.evaluate(item, XPathConstants.NODESET);
			for(int i = 0; i < links.getLength(); i++) {
				Element link = (Element)links.item(i);
				if(link.hasAttribute("cond") && link.getAttribute("cond") != null) {
					if(!(AuthParser.auth(link.getAttribute("cond"), this._params))) {
						continue;
					}
				}
				if(!link.hasAttribute("full") || !link.getAttribute("full").equals("true")) {
					String name = "";
					if(link.hasAttribute("name") && !link.getAttribute("name").equals("")) {
						name = " name=\"" + link.getAttribute("name") + "\" class=\"" + link.getAttribute("name") + "\"";
					}
					if(link.hasAttribute("type") && link.getAttribute("type").equals("submit")) {
						sb.append("<form action=\"" + this.getPath(link.getAttribute("path")) + "\" method=\"post\" onsubmit=\"return check_submit(this, '" + link.getAttribute("msg") + "');\"" + name + ">");
					} else {
						sb.append("<form action=\"" + this.getPath(link.getAttribute("path")) + "\"" + name + ">");
					}
				}
				if(!link.hasAttribute("full") || !link.getAttribute("full").equals("true")) {
					this._expr = this._xpath.compile("params/param");
					NodeList inputs = (NodeList)this._expr.evaluate(link, XPathConstants.NODESET);
					for(int x = 0; x < inputs.getLength(); x++) {
						Element input = (Element)inputs.item(x);
						String type = input.getAttribute("type");
						String value = "";
						if(type == null || type.equals("")) {
							type = "param";
						}
						if(type != null && (type.equals("const") || type.equals("default"))) {
							value = input.getAttribute("value");
						} else {
							String ref = input.getAttribute("ref");
							if(ref == null || ref.equals("")) {
								ref = this._defaultName;
							}
							value = "{" + this._tag.path(type, input.getAttribute("value"), ref, true) + "}";
						}
						sb.append("<input type=\"hidden\" class=\""+ input.getAttribute("name") + "\" name=\""+ input.getAttribute("name") + "\" value=\"" + value + "\" />");
					}
				}
				if(!link.hasAttribute("full") || !link.getAttribute("full").equals("true")) {
					if(this._params.isNotEmpty(link, "icon")) {
						sb.append("<button type=\"submit\">");
						if(this._params.isNotEmpty(link, "class")) {
							sb.append("<i class=\"" + link.getAttribute("class") + "\">" + link.getAttribute("icon") + "</i>");
						} else {
							sb.append("<i>" + link.getAttribute("icon") + "</i>");
						}
						sb.append("<span>" + link.getAttribute("label") + "</span>");
						sb.append("</button>");
					} else {
						sb.append("<input type=\"submit\" value=\"" + link.getAttribute("label") + "\" />");
					}
					sb.append("</form>");
				} else {
					if(!this._params.equals("system.suffix", ".html")) {
						sb.append("<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
					}
					sb.append("<script>");
					sb.append("window.onload = function () {");
					if(this._params.equals("system.suffix", ".html")) {
						sb.append("if(document.getElementById(\"" + this._query.getAttribute("id") + "_submit\").form == null) {");
					}
					sb.append("document.getElementById(\"" + this._query.getAttribute("id") + "_submit\").onclick = function() {");
					sb.append("if(check_submit(document.getElementById(\"" + this._query.getAttribute("id") + "\"), '" + layout.getAttribute("msg") + "')) {");
					sb.append("document.getElementById(\"" + this._query.getAttribute("id") + "\").submit();");
					sb.append("}");
					if(this._params.equals("system.suffix", ".html")) {
						sb.append("}");
					}
					sb.append("}");
					sb.append("}");
					sb.append("</script>");
					if(!this._params.equals("system.suffix", ".html")) {
						sb.append("</xsl:if>");
					}
					if(this._params.isNotEmpty(link, "icon")) {
						sb.append("<button type=\"submit\" form=\"" + this._query.getAttribute("id") + "\" id=\"" + this._query.getAttribute("id") + "_submit\">");
						if(this._params.isNotEmpty(link, "class")) {
							sb.append("<i class=\"" + link.getAttribute("class") + "\">" + link.getAttribute("icon") + "</i>");
						} else {
							sb.append("<i>" + link.getAttribute("icon") + "</i>");
						}
						sb.append("<span>" + link.getAttribute("label") + "</span>");
						sb.append("</button>");
					} else {
						sb.append("<input type=\"submit\" value=\"" + link.getAttribute("label") + "\" form=\"" + this._query.getAttribute("id") + "\" id=\"" + this._query.getAttribute("id") + "_submit\" />");
					}
				}
			}
			
			this._expr = this._xpath.compile("search");
			NodeList searches = (NodeList)this._expr.evaluate(item, XPathConstants.NODESET);
			for(int i = 0; i < searches.getLength(); i++) {
				Element search = (Element)searches.item(i);
				if(search.hasAttribute("cond") && search.getAttribute("cond") != null) {
					if(!(AuthParser.auth(search.getAttribute("cond"), this._params))) {
						continue;
					}
				}
				String name = "";
				if(search.hasAttribute("name") && !search.getAttribute("name").equals("")) {
					name = " name=\"" + search.getAttribute("name") + "\" class=\"" + search.getAttribute("name") + "\"";
				}
				String method = "";
				if(search.hasAttribute("method") && !search.getAttribute("method").equals("")) {
					method = " method=\"" + search.getAttribute("method") + "\"";
				}
				sb.append("<form action=\"" + this.getPath(search.getAttribute("path")) + "\"" + name + method + ">");
				this._expr = this._xpath.compile("params/param");
				NodeList inputs = (NodeList)this._expr.evaluate(search, XPathConstants.NODESET);
				for(int x = 0; x < inputs.getLength(); x++) {
					Element input = (Element)inputs.item(x);
					if(input.hasAttribute("hidden") && input.getAttribute("hidden").equals("true")) {
						String type = input.getAttribute("type");
						String value = "";
						if(type == null || type.equals("")) {
							type = "param";
						}
						if(type != null && (type.equals("const") || type.equals("default"))) {
							value = input.getAttribute("value");
						} else {
							String ref = input.getAttribute("ref");
							if(ref == null || ref.equals("")) {
								ref = this._defaultName;
							}
							value = "{" + this._tag.path(type, input.getAttribute("value"), ref, true) + "}";
						}
						sb.append("<input type=\"hidden\" class=\""+ input.getAttribute("name") + "\" name=\""+ input.getAttribute("name") + "\" value=\"" + value + "\" />");
					} else if(input.hasAttribute("type") && input.getAttribute("type").equals("select")) {
						sb.append("<select name=\"" + input.getAttribute("name") + "\" class=\"" + input.getAttribute("name") + "\" value=\"{" + this._tag.path("row", input.getAttribute("value"), null, true) + "}\">");
						if(input.hasAttribute("for") && input.getAttribute("for") != null && !input.getAttribute("for").equals("")) {
							sb.append("<xsl:for-each select=\"" + this._tag.path("code", "option", input.getAttribute("for"), true) + "\">");
							sb.append("	<xsl:choose>");
							sb.append("	<xsl:when test=\"@" + this._tag.path("code", "value", null, true) + " = " + this._tag.path("param", input.getAttribute("value"), null, true)  + "\">");
							sb.append("		<option value=\"{@" + this._tag.path("code", "value", null, true) + "}\"  selected=\"selected\"><xsl:value-of select=\"@" + this._tag.path("code", "label", null, true) + "\" /></option>");
							sb.append("	</xsl:when>");
							sb.append("	<xsl:otherwise>");
							sb.append("		<option value=\"{@" + this._tag.path("code", "value", null, true) + "}\"><xsl:value-of select=\"@" + this._tag.path("code", "label", null, true) + "\" /></option>");
							sb.append("	</xsl:otherwise>");
							sb.append("	</xsl:choose>");
							sb.append("</xsl:for-each>");
						} else {
							this._expr = this._xpath.compile("option");
							NodeList options = (NodeList)this._expr.evaluate(input, XPathConstants.NODESET);
							for(int q = 0; q < options.getLength(); q++) {
								Element option = (Element)options.item(q);
								sb.append("<xsl:choose>");
								sb.append("<xsl:when test=\"" + this._tag.path("param", input.getAttribute("value"), null, true) + " = '" + option.getAttribute("value") + "'\">");
								sb.append("<option value=\"" + option.getAttribute("value") + "\" selected=\"selected\">" + option.getAttribute("label") + "</option>");
								sb.append("</xsl:when>");
								sb.append("<xsl:otherwise>");
								sb.append("<option value=\"" + option.getAttribute("value") + "\">" + option.getAttribute("label") + "</option>");
								sb.append("</xsl:otherwise>");
								sb.append("</xsl:choose>");
							}
						}
						sb.append("</select>");
					} else {
						sb.append("<input type=\"text\" class=\""+ input.getAttribute("name") + "\" name=\""+ input.getAttribute("name") + "\" value=\"{" + this._tag.path("param", input.getAttribute("value"), null, true) + "}\" />");
					}
				}
				if(this._params.isNotEmpty(search, "icon")) {
					sb.append("<button type=\"submit\">");
					if(this._params.isNotEmpty(search, "class")) {
						sb.append("<i class=\"" + search.getAttribute("class") + "\">" + search.getAttribute("icon") + "</i>");
					} else {
						sb.append("<i>" + search.getAttribute("icon") + "</i>");
					}
					sb.append("<span>" + search.getAttribute("label") + "</span>");
					sb.append("</button>");
				} else {
					sb.append("<input type=\"submit\" value=\"" + search.getAttribute("label") + "\" />");
				}
				sb.append("</form>");
			}
		}
		return sb;
	}
	private Buffer post() throws XPathExpressionException {
		Buffer sb = new Buffer();
		Element redirect = null;
		this._expr = this._xpath.compile("redirect");
		NodeList list = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
		/*
		for(int y = 0; y < list.getLength(); y++) {
			Element n = (Element)list.item(y);
			if(!n.hasAttribute("cond") || AuthParser.auth(n.getAttribute("cond"), this._params)) {
				redirect = n;
				break;
			}
		}
		*/
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(this._tag.path("stylesheet", null));
		
		sb.append("<xsl:output method=\"html\" encoding=\"utf-8\" indent=\"yes\" version=\"5.0\" omit-xml-declaration=\"no\" />");
		sb.append("<xsl:template match=\"/\">");
		if(this._params.equals("system.suffix", ".html")) {
			sb.appendL("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;&#xa;</xsl:text>");
		} else { 
			sb.append("<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
			sb.append("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>");
			sb.append("</xsl:if>");
		}
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"UTF-8\" />");
		sb.append("<title></title>");
		sb.append("<meta http-equiv=\"Content-Language\" content=\"Korean\" />");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		sb.append("</head>");
		sb.append("<body>");
		/*
		sb.append("<form method=\"get\" action=\"" + this.getPath(redirect.getAttribute("path")) + "\" style=\"display:inline;\" id=\"_post\">");
		this._expr = this._xpath.compile("param");
		NodeList param = (NodeList)this._expr.evaluate(redirect, XPathConstants.NODESET);
		for(int x = 0; x < param.getLength(); x++) {
			Element p = (Element)param.item(x);
			if(p.getAttribute("type").equals("param")) {
				sb.append("<input type=\"hidden\" class=\"" + p.getAttribute("name") + "\" name=\"" + p.getAttribute("name") + "\" value=\"{" + this._tag.path("param", p.getAttribute("value"), null, true) + "}\" />");
			} else if(p.getAttribute("type").equals("query")) {
				sb.append("<input type=\"hidden\" class=\"" + p.getAttribute("name") + "\" name=\"" + p.getAttribute("name") + "\" value=\"{" + this._tag.path("row", p.getAttribute("value"), null, true) + "}\" />");
			} else {
				throw new ParsingException();
			}
		}
		*/
		sb.append("<xsl:choose>");
		for(int y = 0; y < list.getLength(); y++) {
			Element n = (Element)list.item(y);
			String test = null;
			if(!n.hasAttribute("cond")) {
				test = "1";
			} else {
				AuthInfo info = AuthParser.parse(n.getAttribute("cond"));
				String left = null;
				if(info == null) {
					test = "1";
				} else if(info.left == null) {
					test = "1";
				} else {
					if(info.left.startsWith("param.")) {
						left = this._tag.path("param", info.left.substring(info.left.indexOf(".") + 1), null, true);
					} else if(info.left.startsWith("result.")) {
						left = this._tag.path("result", info.left.substring(info.left.indexOf(".") + 1), null, true);
					} else if(info.left.startsWith("prop.")) {
						left = this._tag.path("prop", info.left.substring(info.left.indexOf(".") + 1), null, true);
					} else if(info.left.startsWith("query.")) {
						left = this._tag.path("row", info.left.substring(info.left.indexOf(".") + 1), null, true);
					} else {
						if(AuthParser.auth(info, this._params)) {
							test = "1";
						} else {
							test = "0";
						}
					}
					if(test == null) {
						if(info.op == AuthParser.IsEmpty) {
							test = "not(" + left + ") or " + left + " = ''"; 
						} else if(info.op == AuthParser.IsNotEmpty) {
							test = "" + left + " and " + left + " != ''"; 
						} else {
							if(info.right == null) {
								test = "0";
							}
						}
						if(info.op == AuthParser.In || info.op == AuthParser.NotIn) {
							throw new ParsingException();
						} else if(info.op == AuthParser.Equals) {
							test = "" + left + " = '" + info.right + "'";
						} else if(info.op == AuthParser.NotEquals) {
							test = "" + left + " != '" + info.right + "'";
						} else if(info.op == AuthParser.GreaterThan) {
							test = "" + left + " > " + info.right + "";
						} else if(info.op == AuthParser.GreaterThanOrEqualTo) {
							test = "" + left + " >= " + info.right + "";
						} else if(info.op == AuthParser.LessThan) {
							test = "" + left + " < " + info.right + "";
						} else if(info.op == AuthParser.LessThanOrEqualTo) {
							test = "" + left + " <= " + info.right + "";
						}
					}
				}
			}
			sb.append("<xsl:when test=\"" + test + "\">");
			sb.append("<form>");
			sb.append("<xsl:attribute name=\"method\">get</xsl:attribute>");
			sb.append("<xsl:attribute name=\"style\">display:inline;</xsl:attribute>");
			sb.append("<xsl:attribute name=\"id\">_post</xsl:attribute>");
			sb.append("<xsl:attribute name=\"action\">" + this.getPath(n.getAttribute("path")) + "</xsl:attribute>");
			this._expr = this._xpath.compile("param");
			NodeList param = (NodeList)this._expr.evaluate(n, XPathConstants.NODESET);
			for(int x = 0; x < param.getLength(); x++) {
				Element p = (Element)param.item(x);
				String value = null;
				if(p.getAttribute("type").equals("param")) {
					value = this._tag.path("param", p.getAttribute("value"), null, true);
				} else if(p.getAttribute("type").equals("query")) {
					if(p.hasAttribute("ref") && p.getAttribute("ref") != null && !p.getAttribute("ref").equals("")) {
						value = this._tag.path("query", p.getAttribute("value"), p.getAttribute("ref"), true);
					} else {
						value = this._tag.path("query", p.getAttribute("value"), null, true);
					}
				} else if(p.getAttribute("type").equals("result")) {
					value = this._tag.path("result", p.getAttribute("value"), null, true);
				} else if(p.getAttribute("type").equals("prop")) {
					value = this._tag.path("prop", p.getAttribute("value"), null, true);
				} else if(p.getAttribute("type").equals("const") || p.getAttribute("type").equals("default")) {
					
				} else {
					throw new ParsingException();
				}
				if(p.getAttribute("type").equals("const") || p.getAttribute("type").equals("default")) {
					sb.append("<input type=\"hidden\" class=\"" + p.getAttribute("name") + "\" name=\"" + p.getAttribute("name") + "\" value=\"" + p.getAttribute("value") + "\" />");
				} else {
					sb.append("<input type=\"hidden\" class=\"" + p.getAttribute("name") + "\" name=\"" + p.getAttribute("name") + "\" value=\"{" + value + "}\" />");
				}
			}
			sb.append("<input type=\"submit\" value=\"확인\" />");
			sb.append("</form>");
			sb.append("</xsl:when>");
		/*
			if(!n.hasAttribute("cond") || AuthParser.auth(n.getAttribute("cond"), this._params)) {
				
				redirect = n;
				break;
			}
			*/
		}
		sb.append("</xsl:choose>");
		sb.append("<script>");
		sb.append("if(document.getElementById(\"_post\")) document.getElementById(\"_post\").submit();");
		sb.append("</script>");
		sb.append("</body>");
		sb.append("</html>");
		sb.append("</xsl:template>");
		sb.append("</xsl:stylesheet>");
		return sb;
		
	}
	private Buffer error() throws XPathExpressionException {
		Buffer sb = new Buffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(this._tag.path("stylesheet", null));
		
		sb.append("<xsl:output method=\"html\" encoding=\"utf-8\" indent=\"yes\" version=\"5.0\" omit-xml-declaration=\"no\" />");
		sb.append("<xsl:template match=\"/\">");
		if(this._params.equals("system.suffix", ".html")) {
			sb.appendL("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;&#xa;</xsl:text>");
		} else { 
			sb.append("<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
			sb.append("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>");
			sb.append("</xsl:if>");
		}
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"UTF-8\" />");
		sb.append("<title></title>");
		sb.append("<meta http-equiv=\"Content-Language\" content=\"Korean\" />");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		sb.appendL("<script>");
		sb.appendL("function back() {");
		sb.appendL("history.back();");
		sb.appendL("}");
		sb.appendL("</script>");
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<ul>");
		sb.append("<xsl:for-each select=\"" + this._tag.path("error", null) + "\">");
		sb.append("<li><xsl:value-of select=\".\" /></li>");
		sb.append("</xsl:for-each>");
		sb.append("</ul>");
		sb.append("<input type=\"button\" value=\"돌아가기\" onclick=\"back()\" />");
		
		sb.append("</body>");
		sb.append("</html>");
		sb.append("</xsl:template>");
		sb.append("</xsl:stylesheet>");
		return sb;
		
	}
	private Buffer getTitle(String text) {
		String title = new String(text);
		Buffer sb = new Buffer();
		while(true) {
			if(title.indexOf("${") >= 0) {
				sb.append(title.substring(0, title.indexOf("${")));
				title = title.substring(title.indexOf("${") + 2);
				if(title.indexOf("}") > 0) {
					String val = title.substring(0, title.indexOf("}"));
					sb.append("<xsl:value-of select=\"" + val + "\" />");
					title = title.substring(title.indexOf("}") + 1);
				} else {
					sb.append(title);
					break;
				}
			} else {
				sb.append(title);
				break;
			}
		}
		return sb;
	}
	private Buffer getTitle() {
		Buffer sb = new Buffer();
		
		try {
			this._expr = this._xpath.compile("header/labels/label");
			NodeList labels = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(labels.getLength() > 0) {
				for(int i = 0; i < labels.getLength(); i++) {
					Element label = (Element)labels.item(i);
					if(!label.hasAttribute("cond") || AuthParser.auth(label.getAttribute("cond"), this._params)) {
						if(this._tag.isRDF && label.hasAttribute("xText")) {
							sb.append(this.getTitle(label.getAttribute("xText")));
						} else {
							sb.append(this.getTitle(label.getAttribute("text")));
						}
						break;
					}
				}
			}
		} catch (XPathExpressionException e) {
			logger.severe(LOG.toString(e));
		}
		if(sb.length() == 0 && this._query.hasAttribute("label") || this._query.hasAttribute("xLabel")) {
			if(this._tag.isRDF && this._query.hasAttribute("xLabel")) {
				sb.append(this.getTitle(this._query.getAttribute("xLabel")));
			} else {
				sb.append(this.getTitle(this._query.getAttribute("label")));
			}
		}
		return sb;
	}
	private Buffer before() {
		Buffer sb = new Buffer();
		Buffer title = this.getTitle();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(this._tag.path("stylesheet", null));
		sb.append("<xsl:output method=\"html\" encoding=\"utf-8\" indent=\"yes\" version=\"5.0\" omit-xml-declaration=\"no\" />");
		sb.append("<xsl:template match=\"/\">");
		if(this._params.equals("system.suffix", ".html")) {
			sb.appendL("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;&#xa;</xsl:text>");
		} else { 
			sb.append("<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
			sb.append("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>");
			sb.append("</xsl:if>");
		}
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"UTF-8\" />");
		sb.append("<meta name=\"viewport\" content=\"user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, width=device-width\" />");
		sb.append("<title>");
		sb.append(title);
		sb.append("</title>");
		sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		Document doc = null;
		
		try {
			this._expr = this._xpath.compile("header");
			Element header = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
			
			this._expr = this._xpath.compile("header/style");
			NodeList[] nodes = new NodeList[6];
			nodes[0] = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			nodes[1] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			nodes[2] = null;
			if(nodes[0].getLength() == 0 && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/header/style");
				nodes[0] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			}
			
			this._expr = this._xpath.compile("header/script");
			nodes[3] = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			nodes[4] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			nodes[5] = null;
			if(nodes[3].getLength() == 0 && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/header/script");
				nodes[3] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			}

			if(header.hasAttribute("extends")) {
				if(doc == null) {
					File parent = new File(this._config.getParent() + File.separator + header.getAttribute("extends"));
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					dbf.setXIncludeAware(true);
					doc = dbf.newDocumentBuilder().parse(parent);
					doc.getDocumentElement().normalize();
				}
				this._expr = this._xpath.compile("/querys/header/style");
				nodes[2] = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
				this._expr = this._xpath.compile("/querys/header/script");
				nodes[5] = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
			}
			
			for(int x = nodes.length - 1; x >= 0 ; x--) {
				if(nodes[x] == null || nodes[x].getLength() == 0) {
					continue;
				}
				for(int i = 0; i < nodes[x].getLength(); i++) {
					Element node = (Element)nodes[x].item(i);
					if(node.hasAttribute("name")) {
						if(x > 2) {
							this._expr = this._xpath.compile("header/script[@name='" + node.getAttribute("name") + "' and @override='true']");
						} else {
							this._expr = this._xpath.compile("header/style[@name='" + node.getAttribute("name") + "' and @override='true']");
						}
						NodeList p = null;
						if(x == 2 || x == 5 || x == 1 || x == 4) {
							p = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(x == 2 || x == 5) {
							p = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
					}
					if(x > 2) {
						if(!node.hasAttribute("src")) {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("\n<xsl:comment>[if IE]>");
								sb.appendL("&lt;script>");
							} else {
								sb.appendL("<script>");
							}
						} else {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("\n<xsl:comment>[if IE]>");
								if(node.hasAttribute("charset")) {
									sb.appendL("&lt;script src=\"" + node.getAttribute("src") + "\" charset=\"" + node.getAttribute("charset") + "\" />");
								} else {
									sb.appendL("&lt;script src=\"" + node.getAttribute("src") + "\" />");
								}
								sb.appendL("&lt;![endif]</xsl:comment>\n");
							} else {
								if(node.hasAttribute("charset")) {
									sb.appendL("<script src=\"" + node.getAttribute("src") + "\" charset=\"" + node.getAttribute("charset") + "\" />");
								} else {
									sb.appendL("<script src=\"" + node.getAttribute("src") + "\" />");
								}
							}
						}
					} else {
						if(!node.hasAttribute("src")) {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("\n<xsl:comment>[if IE]>");
								sb.appendL("&lt;style>");
							} else {
								sb.appendL("<style>");
							}
						} else {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("\n<xsl:comment>[if IE]>");
								sb.appendL("&lt;link rel=\"stylesheet\" href=\"" + node.getAttribute("src") + "\" type=\"text/css\" media=\"all\" />");
								sb.appendL("&lt;![endif]</xsl:comment>\n");
								
							} else {
								sb.appendL("<link rel=\"stylesheet\" href=\"" + node.getAttribute("src") + "\" type=\"text/css\" media=\"all\" />");
							}
						}
					}
					if(!node.hasAttribute("src")) {
						sb.append(node.getTextContent());
						sb.append("");
						if(x > 2) {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("&lt;/script>");
								sb.appendL("&lt;![endif]</xsl:comment>\n");
							} else {
								sb.appendL("</script>");
							}
						} else {
							if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
								sb.appendL("&lt;/style>");
								sb.appendL("&lt;![endif]</xsl:comment>\n");
							} else {
								sb.appendL("</style>");
							}
						}
					}
				}
			}
			
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			logger.severe(LOG.toString(e));
		}
		try {
			this._expr = this._xpath.compile("calculator/param");
			NodeList list = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(list != null && list.getLength() > 0) {
				sb.appendL("<script>");
				sb.appendL("GrahaFormula.expr = [");
				for(int x = 0; x < list.getLength(); x++) {
					Element node = (Element)list.item(x);
					if(x > 0) {
						sb.appendL("	,{");
					} else {
						sb.appendL("	{");
					}
					if(node.hasAttribute("expr") && node.getAttribute("expr") != null) {
						String expr = (String)node.getAttribute("expr");
						sb.appendL("		expr:\"" + expr.replace("\"", "\\\"") + "\",");
					}
					if(node.hasAttribute("form") && node.getAttribute("form") != null) {
						sb.appendL("		formName:\"" + node.getAttribute("form") + "\",");
					} else {
						sb.appendL("		formName:\"insert\",");
					}
					if(node.hasAttribute("event") && node.getAttribute("event") != null) {
						sb.appendL("		event:\"" + node.getAttribute("event") + "\",");
					}
					if(node.hasAttribute("refer") && node.getAttribute("refer") != null) {
						sb.appendL("		refer:\"" + node.getAttribute("refer") + "\",");
					}
					if(node.hasAttribute("func") && node.getAttribute("func") != null) {
						sb.appendL("		func:" + node.getAttribute("func") + ",");
					}
					if(node.hasAttribute("name") && node.getAttribute("name") != null) {
						sb.appendL("		name:\"" + node.getAttribute("name") + "\"");
					}
					sb.appendL("	}");
				}
				sb.appendL("];");
				sb.appendL("GrahaFormula.addEvent(document, GrahaFormula.ready, \"ready\");");
				sb.appendL("</script>");
			}
		} catch (XPathExpressionException | DOMException e) {
			e.printStackTrace();
		}
		try {
			this._expr = this._xpath.compile("validation");
			Element node = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
			
			if(node != null) {
				NodeList list = node.getChildNodes();
				if(list.getLength() > 0) {
					sb.appendL("<script>");
					sb.appendL("function _check(form, out) {");
					sb.appendL("var result = true;");
				}
				for(int x = 0; x < list.getLength(); x++) {
					org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(x);
					if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && n.getNodeName().equals("param")) {
						Element e = (Element)n;
						String key = (String)e.getAttribute("name");
						if(e.hasAttribute("not-null") && e.getAttribute("not-null") != null && (((String)e.getAttribute("not-null")).equalsIgnoreCase("true") || ((String)e.getAttribute("not-null")).equalsIgnoreCase("y"))) {
							sb.appendL("if(!_notNull(form, \"" + key + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", not_null:true});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
						}
						if(e.hasAttribute("max-length") && e.getAttribute("max-length") != null) {
							sb.appendL("if(!_maxLength(form, \"" + key + "\", \"" + (String)e.getAttribute("max-length") + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", max_length:" + (String)e.getAttribute("max-length") + "});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
						}
						if(e.hasAttribute("min-length") && e.getAttribute("min-length") != null) {
							sb.appendL("if(!_minLength(form, \"" + key + "\", \"" + (String)e.getAttribute("min-length") + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", min_length:" + (String)e.getAttribute("min-length") + "});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
						}
						if(e.hasAttribute("number-format") && e.getAttribute("number-format") != null) {
							sb.appendL("if(!_numberFormat(form, \"" + key + "\", \"" + (String)e.getAttribute("number-format") + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", number_format:\"" + (String)e.getAttribute("number-format") + "\"});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
							
						}
						if(e.hasAttribute("date-format") && e.getAttribute("date-format") != null) {
							sb.appendL("if(!_dateFormat(form, \"" + key + "\", \"" + (String)e.getAttribute("date-format") + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", date_format:\"" + (String)e.getAttribute("date-format") + "\"});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
						}
						if(e.hasAttribute("format") && e.getAttribute("format") != null) {
							sb.appendL("if(!_format(form, \"" + key + "\", \"" + (String)e.getAttribute("format") + "\")) {");
							sb.appendL("if(arguments.length > 1) {");
							sb.appendL("	out.push({param:\"" + key + "\", msg:\"" + (String)e.getAttribute("msg") + "\", format:\"" + (String)e.getAttribute("format") + "\"});");
							sb.appendL("	result = false;");
							sb.appendL("} else {");
							sb.appendL("	alert(\"" + (String)e.getAttribute("msg") + "\");");
							sb.appendL("	if(typeof(_focus) == \"function\") {_focus(form, \"" + key + "\");}");
							sb.appendL("	return false;");
							sb.appendL("}");
							sb.appendL("}");
						}
					}
				}
				if(list.getLength() > 0) {
					sb.appendL("return result;");
					sb.appendL("}");
					sb.appendL("</script>");
				}
			}
		} catch (XPathExpressionException | DOMException e) {
			e.printStackTrace();
		}

		sb.append("</head>"); 
		sb.append("<body>");
		try {
			this._expr = this._xpath.compile("header");
			Element header = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
			
			this._expr = this._xpath.compile("header/top");
			NodeList[] nodes = new NodeList[3];
			nodes[0] = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			nodes[1] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			nodes[2] = null;
			if(nodes[0].getLength() == 0 && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/header/top");
				nodes[0] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			}

			if(header.hasAttribute("extends")) {
				if(doc == null) {
					File parent = new File(this._config.getParent() + File.separator + header.getAttribute("extends"));
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					dbf.setXIncludeAware(true);
					doc = dbf.newDocumentBuilder().parse(parent);
					doc.getDocumentElement().normalize();
				}
				this._expr = this._xpath.compile("/querys/header/top");
				nodes[2] = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
			}
			
//			for(int x = 0; x < nodes.length; x++) {
			for(int x = nodes.length - 1; x >= 0 ; x--) {
				if(nodes[x] == null || nodes[x].getLength() == 0) {
					continue;
				}
				for(int i = 0; i < nodes[x].getLength(); i++) {
					Element node = (Element)nodes[x].item(i);
					if(node.hasAttribute("name")) {
						this._expr = this._xpath.compile("header/top[@name='" + node.getAttribute("name") + "' and override='true']");
						NodeList p = null;
						if(x == 1) {
							p = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(x == 2 || x == 1) {
							p = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
					}
					sb.append(node.getTextContent());
					sb.append("");
				}
			}
			
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			logger.severe(LOG.toString(e));
		}
		if(title.length() > 0) {
			sb.append("<h2 class=\"title\">");
			sb.append(title);
			sb.append("</h2>");
		}
		return sb;
	}
	private Buffer after() {
		Buffer sb = new Buffer();
		try {
			this._expr = this._xpath.compile("header");
			Element header = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
			
			this._expr = this._xpath.compile("header/bottom");
			NodeList[] nodes = new NodeList[3];
			nodes[0] = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			nodes[1] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			nodes[2] = null;
			if(nodes[0].getLength() == 0 && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/header/bottom");
				nodes[0] = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
			}

			Document doc = null;
			if(header.hasAttribute("extends")) {
					File parent = new File(this._config.getParent() + File.separator + header.getAttribute("extends"));
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					dbf.setXIncludeAware(true);
					doc = dbf.newDocumentBuilder().parse(parent);
					doc.getDocumentElement().normalize();
					this._expr = this._xpath.compile("/querys/header/bottom");
					nodes[2] = (NodeList)this._expr.evaluate(doc, XPathConstants.NODESET);
			}
			
			for(int x = nodes.length - 1; x >= 0 ; x--) {
//			for(int x = 0; x < nodes.length; x++) {
				if(nodes[x] == null || nodes[x].getLength() == 0) {
					continue;
				}
				for(int i = 0; i < nodes[x].getLength(); i++) {
					Element node = (Element)nodes[x].item(i);
					if(node.hasAttribute("name")) {
						this._expr = this._xpath.compile("header/bottom[@name='" + node.getAttribute("name") + "' and override='true']");
						NodeList p = null;
						if(x == 1) {
							p = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(x == 2 || x == 1) {
							p = (NodeList)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
					}
					sb.append(node.getTextContent());
					sb.append("");
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			logger.severe(LOG.toString(e));
		}
/*
		sb.append("Version:");
		sb.append("<xsl:value-of select=\"system-property('xsl:version')\" />");
		sb.append("<br />");
		
		sb.append("Vendor:");
		sb.append("<xsl:value-of select=\"system-property('xsl:vendor')\" />");
		sb.append("<br />");
		
		sb.append("Vendor URL:");
		sb.append("<xsl:value-of select=\"system-property('xsl:vendor-url')\" />");
*/
		sb.appendL("</body>");
		sb.appendL("</html>");
		sb.appendL("</xsl:template>");
		try {
		this._expr = this._xpath.compile("layout");
		Element layout = (Element)this._expr.evaluate(this._query, XPathConstants.NODE);
		if(layout == null && this._query.hasAttribute("extends")) {
			this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/layout");
			layout = (Element)this._expr.evaluate(this._query.getParentNode(), XPathConstants.NODE);
		}
		if(layout.hasAttribute("template") && layout.getAttribute("template").equals("native")) {
			this._expr = this._xpath.compile("append");
			NodeList append = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			if(append == null && layout == null && this._query.hasAttribute("extends")) {
				this._expr = this._xpath.compile("query[@id='" + this._query.getAttribute("extends") + "']/append");
				append = (NodeList)this._expr.evaluate(this._query, XPathConstants.NODESET);
			}
			if(append.getLength() > 0) {
				for(int i = 0; i < append.getLength(); i++) {
					sb.append(((Element)append.item(i)).getTextContent());
				}
			}
		}
		} catch (XPathExpressionException e) {
			logger.severe(LOG.toString(e));
		}

		sb.appendL("</xsl:stylesheet>");
		return sb;
	}
}
