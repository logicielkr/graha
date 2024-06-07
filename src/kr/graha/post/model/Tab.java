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


package kr.graha.post.model;

import java.util.List;
import java.util.ArrayList;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.model.utility.TextParser;
import kr.graha.post.element.XmlElement;

/**
 * querys/query/layout/tab
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Tab {
	protected static int VIEW_TYPE_LIST = 1;
	protected static int VIEW_TYPE_DETAIL = 2;
	protected static int VIEW_TYPE_UNKNOWN = 3;
	private static final String nodeName = "tab";
	private Tab() {
	}
	
	private String name = null;
	private String label = null;
	private String single = null;
	private String column = null;
	private List<Row> row = null;
	private List<Col> col = null;
	private String cond = null;
	private String htmltype = null;
	
	private Boolean valid = null;
	
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getSingle() {
		return this.single;
	}
	private void setSingle(String single) {
		this.single = single;
	}
	protected String getColumn() {
		return this.column;
	}
	private void setColumn(String column) {
		this.column = column;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getHtmltype() {
		return this.htmltype;
	}
	private void setHtmltype(String htmltype) {
		this.htmltype = htmltype;
	}
	private boolean div(boolean htmlType) {
		if(STR.valid(this.htmltype)) {
			return QueryImpl.div(this.getHtmltype());
		}
		return htmlType;
	}
	protected List<Row> getRow() {
		return this.row;
	}
	protected List<Col> getCol() {
		return this.col;
	}
	private void add(Col col) {
		if(this.col == null) {
			this.col = new ArrayList<Col>();
		}
		this.col.add(col);
	}
	private void add(Row row) {
		if(this.row == null) {
			this.row = new ArrayList<Row>();
		}
		this.row.add(row);
	}
	protected boolean valid(Record params) {
		if(this.valid == null) {
			this.valid = true;
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, params)) {
				if(!AuthUtility.auth(tabAuthInfo, params)) {
					this.valid = false;
				} else {
					tabAuthInfo = null;
				}
			}
		}
		return this.valid.booleanValue();
	}
	protected static String nodeName() {
		return Tab.nodeName;
	}
	protected static Tab load(Element element) {
		Tab tab = new Tab();
		if(element != null) {
			tab.loadAttr(element);
			tab.loadElement(element);
			return tab;
		}
		return null;
	}
	private void load(Node node) {
		if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
			this.add(Col.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "row")) {
			this.add(Row.load((Element)node));
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")"); 
		}
	}
	private void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "row")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "envelop")) {
							this.loadElement(node);
						} else {
							LOG.warning("invalid nodeName(" + node.getNodeName() + ")"); 
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
	}
	private void loadAttr(Node element) {
		NamedNodeMap nnm = element.getAttributes();
		if(nnm != null && nnm.getLength() > 0) {
			for(int i = 0; i < nnm.getLength(); i++) {
				Node node = nnm.item(i);
				if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
					if(
						STR.valid(node.getNodeName()) &&
						STR.valid(node.getNodeValue())
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "single")) {
							this.setSingle(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
							this.setColumn(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "htmltype")) {
							this.setHtmltype(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xml:base")) {
						} else {
							LOG.warning("invalid attrName(" + node.getNodeName() + ")"); 
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("single", this.getSingle());
		element.setAttribute("column", this.getColumn());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("htmltype", this.getHtmltype());
		if(this.col != null && this.col.size() > 0) {
			for(int i = 0; i < this.col.size(); i++) {
				element.appendChild(((Col)this.col.get(i)).element());
			}
		}
		if(this.row != null && this.row.size() > 0) {
			for(int i = 0; i < this.row.size(); i++) {
				element.appendChild(((Row)this.row.get(i)).element());
			}
		}
		return element;
	}
	private void table(int indent, boolean div, Buffer xsl, boolean start) {
		if(start) {
			if(div) {
				xsl.appendL(indent, "<div>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha table</xsl:attribute>");
			} else {
				xsl.appendL(indent, "<table>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha</xsl:attribute>");
			}
			if(STR.valid(this.getName())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"id\">" + this.getName() + "</xsl:attribute>");
			}
		} else {
			if(div) {
				xsl.appendL(indent, "</div>");
			} else {
				xsl.appendL(indent, "</table>");
			}
		}
	}
	private void tag(int indent, String tagName, boolean div, Buffer xsl, boolean start) {
		if(start) {
			if(div) {
				xsl.appendL(indent, "<div>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha " + tagName + "</xsl:attribute>");
			} else {
				xsl.appendL(indent, "<" + tagName + ">");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha</xsl:attribute>");
			}
		} else {
			if(div) {
				xsl.appendL(indent, "</div>");
			} else {
				xsl.appendL(indent, "</" + tagName + ">");
			}
		}
	}
	private void thead(int indent, boolean div, Buffer xsl, boolean start) {
		this.tag(indent, "thead", div, xsl, start);
	}
	private void tbody(int indent, boolean div, Buffer xsl, boolean start) {
		this.tag(indent, "tbody", div, xsl, start);
	}
	private void tr(int indent, boolean div, Buffer xsl, boolean start) {
		this.tag(indent, "tr", div, xsl, start);
	}
	private void tag(
		int indent,
		String tagName,
		String className,
		boolean div,
		Buffer xsl,
		boolean start
	) {
		if(start) {
			if(div) {
				xsl.appendL(indent, "<div>");
				if(STR.valid(className)) {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">" + className + "</xsl:attribute>");
				} else {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha " + tagName + "</xsl:attribute>");
				}
			} else {
				xsl.appendL(indent, "<" + tagName + ">");
				if(STR.valid(className)) {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">" + className + "</xsl:attribute>");
				} else {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha</xsl:attribute>");
				}
			}
		} else {
			if(div) {
				xsl.appendL(indent, "</div>");
			} else {
				xsl.appendL(indent, "</" + tagName + ">");
			}
		}
	}
	private void th(int indent, String className, boolean div, Buffer xsl, boolean start) {
		this.tag(indent, "th", className, div, xsl, start);
	}
	private void td(int indent, String className, boolean div, Buffer xsl, boolean start) {
		this.tag(indent, "td", className, div, xsl, start);
	}
	protected Buffer toXSL(
		Files files,
		Record param,
		int indent,
		boolean rdf,
		boolean parentDiv,
		String queryId,
		int queryFuncType,
		Table table,
		Command command,
		boolean multiRow
	) {
		boolean multi = false;
		if(table != null) {
			multi = STR.trueValue(table.getMulti());
		} else if(command != null) {
			multi = STR.trueValue(command.getMulti());
		}
		Buffer xsl = new Buffer();
		boolean exists = false;
		AuthInfo tabAuthInfo = null;
		if(STR.valid(this.getCond())) {
			tabAuthInfo = AuthUtility.parse(this.getCond());
		}
		if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, param)) {
			if(!AuthUtility.auth(tabAuthInfo, param)) {
				return null;
			} else {
				tabAuthInfo = null;
			}
		}
		if(tabAuthInfo != null) {
			xsl.appendL("<xsl:if test=\"" + AuthUtility.testExpr(tabAuthInfo, param, rdf) + "\">");
		}
		if(files != null) {
			xsl.append(files.before(param, indent, this.getName(), rdf, queryId, queryFuncType, multiRow));
		}
		if(
			queryFuncType == Query.QUERY_FUNC_TYPE_LIST ||
			queryFuncType == Query.QUERY_FUNC_TYPE_LISTALL
		) {
			List<Row> rows = new ArrayList<Row>();
			rows.add(new Row(this.col));
			this.list(param, indent, rows, table, command, rdf, this.div(parentDiv), multiRow, queryFuncType, xsl);
			exists = true;
		} else if(
			queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL ||
			queryFuncType == Query.QUERY_FUNC_TYPE_INSERT
		) {
			if(this.row != null && this.row.size() > 0) {
				if(multi) {
					if(this.row.size() == 1) {
						this.list(param, indent, this.row, table, command, rdf, this.div(parentDiv), multiRow, queryFuncType, xsl);
						exists = true;
					} else if(STR.trueValue(this.getSingle())) {
						this.list(param, indent, this.row, table, command, rdf, this.div(parentDiv), multiRow, queryFuncType, xsl);
						exists = true;
					} else {
						this.detail(param, indent, this.row, table, command, multi, rdf, this.div(parentDiv), multiRow, queryFuncType, xsl);
						exists = true;
					}
				} else {
					this.detail(param, indent, this.row, table, command, multi, rdf, this.div(parentDiv), multiRow, queryFuncType, xsl);
					exists = true;
				}
			}
		}
		if(exists) {
			if(files != null) {
				xsl.append(files.after(param, indent, this.getName(), rdf, queryId, queryFuncType, multiRow));
			}
			if(tabAuthInfo != null) {
				xsl.appendL("</xsl:if>");
			}
			return xsl;
		} else {
			xsl.clear();
			xsl = null;
			return null;
		}
	}
	protected Buffer li(Files files, Record param, int indent, boolean rdf) {
		Buffer xsl = new Buffer();
		if(STR.valid(this.getLabel())) {
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, param)) {
				if(!AuthUtility.auth(tabAuthInfo, param)) {
					return null;
				} else {
					tabAuthInfo = null;
				}
			}
			if(tabAuthInfo != null) {
				xsl.appendL("<xsl:if test=\"" + AuthUtility.testExpr(tabAuthInfo, param, rdf) + "\">");
			}
			if(files != null) {
				xsl.append(files.beforeLi(param, indent, this.getName(), rdf));
			}
			xsl.appendL("<li class=\"" + this.getName()+ "\">" + TextParser.parseForXSL(this.getLabel(), param, rdf) + "</li>");
			if(files != null) {
				xsl.append(files.afterLi(param, indent, this.getName(), rdf));
			}
			if(tabAuthInfo != null) {
				xsl.appendL("</xsl:if>");
			}
		}
		return xsl;
	}
	private void h3(
		Record param,
		int indent,
		boolean rdf,
		Buffer xsl
	) {
		if(STR.valid(this.getLabel())) {
			if(STR.valid(this.getName())) {
				xsl.append(indent, "<h3 class=\"" + this.getName() + "\">");
			} else {
				xsl.append(indent, "<h3>");
			}
			xsl.append(TextParser.parseForXSL(this.getLabel(), param, rdf));
			xsl.appendL("</h3>");
		}
	}
	private void detail(
		Record param,
		int indent,
		List<Row> rows,
		Table table,
		Command command,
		boolean multi,
		boolean rdf,
		boolean div,
		boolean multiRow,
		int queryFuncType,
		Buffer xsl
	) {
		if(multiRow) {
			this.h3(param, indent, rdf, xsl);
		}
		this.table(indent, div, xsl, true);
		this.tbody(indent + 1, div, xsl, true);
		if(
			STR.compareIgnoreCase(this.getColumn(), "auto") &&
			queryFuncType != Query.QUERY_FUNC_TYPE_INSERT
		) {
			int indent2 = indent + 2;
			if(multi) {
				xsl.appendL(indent2, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.nodePath(this.getName(), rdf) + "\">");
				xsl.appendL(indent2 + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.firstNodeName(rdf) + "\">");
				indent2++;
			} else {
				xsl.appendL(indent2, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.firstNodePath(this.getName(), rdf) + "/*\">");
			}
			this.tr(indent2 + 1, div, xsl, true);
			
			this.th(indent2 + 2, "graha th <xsl:value-of select=\"local-name()\" />", div, xsl, true);
			xsl.appendL(indent2 + 3, "<xsl:value-of select =\"local-name()\"/>");
			this.th(indent2 + 2, null, div, xsl, false);
			
			this.td(indent2 + 2, "graha td <xsl:value-of select=\"local-name()\" />", div, xsl, true);
			xsl.appendL(indent2 + 3, "<xsl:attribute name=\"class\">graha td <xsl:value-of select=\"local-name()\" /></xsl:attribute>");
			xsl.appendL(indent2 + 3, "<xsl:value-of select=\".\" />");
			this.td(indent2 + 2, null, div, xsl, false);
			
			this.tr(indent2 + 1, div, xsl, false);
			xsl.appendL(indent2, "</xsl:for-each>");
			if(multi) {
				xsl.appendL(indent + 2, "</xsl:for-each>");
			}
		} else {
			if(rows != null && rows.size() > 0) {
				int indent2 = indent + 1;
				if(multi) {
					xsl.appendL(indent2, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.nodePath(this.getName(), rdf) + "\">");
					indent2++;
				}
				for(int x = 0; x < rows.size(); x++) {
					Row r = (Row)rows.get(x);
					List<Col> cols = r.getCol();
					if(cols != null && cols.size() > 0) {
						AuthInfo rowAuthInfo = null;
						if(STR.valid(r.getCond())) {
							rowAuthInfo = AuthUtility.parse(r.getCond());
						}
						if(rowAuthInfo != null && AuthUtility.testInServer(rowAuthInfo, param)) {
							if(!AuthUtility.auth(rowAuthInfo, param)) {
								continue;
							} else {
								rowAuthInfo = null;
							}
						}
						if(rowAuthInfo != null) {
							xsl.appendL("<xsl:if test=\"" + AuthUtility.testExpr(rowAuthInfo, param, rdf, !multi) + "\">");
						}
						this.tr(indent2 + 1, div, xsl, true);
						for(int i = 0; i < cols.size(); i++) {
							xsl.append(((Col)cols.get(i)).th(param, indent2 + 2, rdf, div, !multi, queryFuncType, Tab.VIEW_TYPE_DETAIL));
							if(
								(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT && x == 0 && i == 0) &&
								(
									(multi && multiRow) ||
									(table != null && table.existsForeignColumn(param))
								)
							) {
								xsl.append(((Col)cols.get(i)).td(param, indent2 + 2, table, this.col, this.getName(), rdf, div, !multi, queryFuncType));
							} else {
								xsl.append(((Col)cols.get(i)).td(param, indent2 + 2, table, null, this.getName(), rdf, div, !multi, queryFuncType));
							}
						}
						this.tr(indent2 + 1, div, xsl, false);
						if(rowAuthInfo != null) {
							xsl.appendL("</xsl:if>");
						}
					}
				}
				if(multi) {
					xsl.appendL(indent + 1, "</xsl:for-each>");
				}
			}
		}
		this.tbody(indent + 1, div, xsl, false);
		this.table(indent, div, xsl, false);
	}
	private void list(
		Record param,
		int indent,
		List<Row> rows,
		Table table,
		Command command,
		boolean rdf,
		boolean div,
		boolean multiRow,
		int queryFuncType,
		Buffer xsl
	) {
		if(multiRow) {
			this.h3(param, indent, rdf, xsl);
		}
		this.table(indent, div, xsl, true);
		this.thead(indent + 1, div, xsl, true);
		if(
			STR.compareIgnoreCase(this.getColumn(), "auto") &&
			queryFuncType != Query.QUERY_FUNC_TYPE_INSERT
		) {
			this.tr(indent + 2, div, xsl, true);
			xsl.appendL(indent + 3, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.firstNodePath(this.getName(), rdf) + "/*\">");
			this.th(indent + 4, "graha th <xsl:value-of select=\"local-name()\" />", div, xsl, true);
			xsl.appendL(indent + 5, "<xsl:value-of select =\"local-name()\"/>");
			this.th(indent + 4, null, div, xsl, false);
			xsl.appendL(indent + 3, "</xsl:for-each>");
			this.tr(indent + 2, div, xsl, false);
		} else {
			if(rows != null && rows.size() > 0) {
				for(int x = 0; x < rows.size(); x++) {
					List<Col> cols = ((Row)rows.get(x)).getCol();
					if(cols != null && cols.size() > 0) {
						this.tr(indent + 2, div, xsl, true);
						for(int i = 0; i < cols.size(); i++) {
							xsl.append(((Col)cols.get(i)).th(param, indent + 3, rdf, div, true, queryFuncType, Tab.VIEW_TYPE_LIST));
						}
						this.tr(indent + 2, div, xsl, false);
					}
				}
			}
		}
		this.thead(indent + 1, div, xsl, false);
		this.tbody(indent + 1, div, xsl, true);
		xsl.appendL(indent + 2, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.nodePath(this.getName(), rdf) + "\">");
		
		if(
			STR.compareIgnoreCase(this.getColumn(), "auto") &&
			queryFuncType != Query.QUERY_FUNC_TYPE_INSERT
		) {
			this.tr(indent + 3, div, xsl, true);
			xsl.appendL(indent + 4, "<xsl:for-each select=\"" + kr.graha.post.xml.GRow.firstNodeName(rdf) + "\">");
			this.td(indent + 5, "graha td <xsl:value-of select=\"local-name()\" />", div, xsl, true);
			xsl.appendL(indent + 6, "<xsl:attribute name=\"class\">graha td <xsl:value-of select=\"local-name()\" /></xsl:attribute>");
			xsl.appendL(indent + 6, "<xsl:value-of select=\".\" />");
			this.td(indent + 5, null, div, xsl, false);
			xsl.appendL(indent + 4, "</xsl:for-each>");
			this.tr(indent + 3, div, xsl, false);
		} else {
			if(rows != null && rows.size() > 0) {
				for(int x = 0; x < rows.size(); x++) {
					List<Col> cols = ((Row)rows.get(x)).getCol();
					if(cols != null && cols.size() > 0) {
						this.tr(indent + 3, div, xsl, true);
						for(int i = 0; i < cols.size(); i++) {
							if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT && x == 0 && i == 0) {
								xsl.append(((Col)cols.get(i)).td(param, indent + 4, table, this.col, this.getName(), rdf, div, false, queryFuncType));
							} else {
								xsl.append(((Col)cols.get(i)).td(param, indent + 4, table, null, this.getName(), rdf, div, false, queryFuncType));
							}
						}
						this.tr(indent + 3, div, xsl, false);
					}
				}
			}
		}
		
		xsl.appendL(indent + 2, "</xsl:for-each>");
		this.tbody(indent + 1, div, xsl, false);
		this.table(indent, div, xsl, false);
	}
}
