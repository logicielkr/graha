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
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.model.utility.TextParser;

/**
 * Graha(그라하) link 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Link {
	private static final String nodeName = "link";
	private Link() {
	}
	
	private String name = null;
	private String path = null;
	private String type = null;
	private String full = null;
	private String icon = null;
	private String className = null;
	private String label = null;
	private String cond = null;
	private String msg = null;
	private String value = null;
	private String method = null;
	private List<LinkParam> param = null;
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getPath() {
		return this.path;
	}
	private void setPath(String path) {
		this.path = path;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type = type;
	}
	protected String getFull() {
		return this.full;
	}
	private void setFull(String full) {
		this.full = full;
	}
	private String getIcon() {
		return this.icon;
	}
	private void setIcon(String icon) {
		this.icon = icon;
	}
	private String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className = className;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getMsg() {
		return this.msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
	private String getValue() {
		return this.value;
	}
	private void setValue(String value) {
		this.value = value;
	}
	private String getMethod() {
		return this.method;
	}
	private void setMethod(String method) {
		this.method = method;
	}
	protected List<LinkParam> getParam() {
		return this.param;
	}
	private void add(LinkParam param) {
		if(this.param == null) {
			this.param = new ArrayList<LinkParam>();
		}
		this.param.add(param);
	}
	protected static String nodeName() {
		return Link.nodeName;
	}
	protected static Link load(Element element) {
		Link link = new Link();
		if(element != null) {
			link.loadAttr(element);
			link.loadElement(element);
			return link;
		}
		return null;
	}
	private void loads(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					load(node);
				}
			}
		}
	}
	private void load(Node node) {
		if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(LinkParam.load((Element)node));
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
						if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "params")) {
							this.loads(node);
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "path")) {
							this.setPath(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "full")) {
							this.setFull(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "icon")) {
							this.setIcon(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.setMsg(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "method")) {
							this.setMethod(node.getNodeValue());
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
		element.setAttribute("path", this.getPath());
		element.setAttribute("type", this.getType());
		element.setAttribute("full", this.getFull());
		element.setAttribute("icon", this.getIcon());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("msg", this.getMsg());
		element.setAttribute("value", this.getValue());
		element.setAttribute("method", this.getMethod());

		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((LinkParam)this.param.get(i)).element());
			}
		}
		return element;
	}
	protected static String getPath(String href, Record param) {
		if(href.startsWith("/")) {
			if(href.endsWith("!")) {
				return param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "prefix")) + href.substring(0, href.length() - 1);
			} else {
				return param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "prefix")) + href + param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"));
			}
		} else if(href.startsWith("../")) {
			if(href.endsWith("!")) {
				return href.substring(0, href.length() - 1);
			} else {
				return href + param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"));
			}
		} else if(href == null || href.trim().equals("")) {
			return "";
		} else {
			if(href.endsWith("!")) {
				return param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "prefix")) + "/" + param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "config.file.name")) + "/" + href.substring(0, href.length() - 1);
			} else {
				return param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "prefix")) + "/" + param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "config.file.name")) + "/" + href + param.getString(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"));
			}
		}
	}
	protected Buffer link(Record param, String tabName, String label, int indent, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		xsl.appendL(indent, "<a>");
		if(STR.compareIgnoreCase(this.getType(), "query")) {
			xsl.append(indent + 1, "<xsl:attribute name=\"link\">");
			if(full) {
				xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodePath(tabName, this.getValue(), rdf) + "\" />");
			} else {
				xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodeName(this.getValue(), rdf) + "\" />");
			}
			xsl.appendL("</xsl:attribute>");
		}
		if(this.param != null && this.param.size() > 0) {
			xsl.append(LinkParam.param(tabName, this.param, indent + 1, rdf, full));
		}
		xsl.append(indent + 1, "<xsl:attribute name=\"href\">");
		if(STR.compareIgnoreCase(this.getType(), "query")) {
			if(full) {
				xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodePath(tabName, this.getValue(), rdf) + "\" />");
			} else {
				xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodeName(this.getValue(), rdf) + "\" />");
			}
		} else if(STR.compareIgnoreCase(this.getType(), "external")) {
			xsl.append(this.getPath());
		} else {
			xsl.append(Link.getPath(this.getPath(), param));
		}
		if(this.param != null && this.param.size() > 0) {
			xsl.append("<xsl:if test=\"$hrefparam\">?<xsl:value-of select=\"$hrefparam\" /></xsl:if>");
		}
		xsl.appendL("</xsl:attribute>");
		xsl.append(1, label);
		xsl.appendL(indent, "</a>");
		return xsl;
	}
	protected Buffer button(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf, String queryId, String layoutMsg) {
		Buffer xsl = new Buffer();
		int internalIndent = indent;
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
			if(!AuthUtility.auth(authInfo, param)) {
				return null;
			} else {
				authInfo = null;
			}
		}
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
			internalIndent++;
		}
		if(
			!STR.valid(this.getFull()) ||
			!STR.trueValue(this.getFull())
		) {
			if(STR.compareIgnoreCase(this.getType(), "submit")) {
				xsl.append(internalIndent, "<form action=\"" + Link.getPath(this.getPath(), param) + "\" method=\"post\" onsubmit=\"return check_submit(this, '" + this.getMsg() + "');\"");
			} else {
				xsl.append(internalIndent, "<form action=\"" + Link.getPath(this.getPath(), param) + "\"");
			}
			if(STR.valid(this.getName())) {
				xsl.append(" name=\"" + this.getName() + "\" class=\"" + this.getName() + "\"");
			}
			xsl.appendL(">");
			if(STR.valid(this.param)) {
				for(int i = 0; i < this.param.size(); i++) {
					xsl.append(((LinkParam)this.param.get(i)).hidden(tables, commands, indent + 1, rdf));
				}
			}
			if(STR.valid(this.getIcon())) {
				xsl.appendL(internalIndent + 1, "<button type=\"submit\">");
				if(STR.valid(this.getClassName())) {
					xsl.appendL(internalIndent + 2, "<i class=\"" + this.getClassName() + "\">" + this.getIcon() + "</i>");
				} else {
					xsl.appendL(internalIndent + 2, "<i>" + this.getIcon() + "</i>");
				}
				xsl.appendL(internalIndent + 2, "<span>" + TextParser.parseForXSL(this.getLabel(), param, rdf) + "</span>");
				xsl.appendL(internalIndent + 1, "</button>");
			} else {
				xsl.appendL(internalIndent + 1, "<input>");
				xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"type\">submit</xsl:attribute>");
				xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"value\">" + TextParser.parseForXSL(this.getLabel(), param, rdf) + "</xsl:attribute>");
				xsl.appendL(internalIndent + 1, "</input>");
			}
			xsl.appendL(internalIndent, "</form>");
		} else {
			if(STR.valid(this.getIcon())) {
				xsl.appendL(internalIndent, "<button type=\"submit\" form=\"" + queryId + "\" id=\"" + queryId + "_submit\">");
				if(!param.equals(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html")) {
					xsl.appendL(internalIndent, "<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
					internalIndent++;
				}
				xsl.append(internalIndent + 1, "<xsl:attribute name=\"onclick\">");
				if(param.equals(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html")) {
					xsl.append("if(this.form == null) {");
				}
				xsl.append("if(check_submit(document.getElementById(\"" + queryId + "\"), '" + layoutMsg + "')) {");
				xsl.append("document.getElementById(\"" + queryId + "\").submit();");
				xsl.append("}");
				if(param.equals(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html")) {
					xsl.append("}");
				}
				xsl.appendL("</xsl:attribute>");
				if(!param.equals(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html")) {
					internalIndent--;
					xsl.appendL(internalIndent, "</xsl:if>");
				}
				if(STR.valid(this.getClassName())) {
					xsl.appendL(internalIndent + 1, "<i class=\"" + this.getClassName() + "\">" + this.getIcon() + "</i>");
				} else {
					xsl.appendL(internalIndent + 1, "<i>" + this.getIcon() + "</i>");
				}
				xsl.appendL(internalIndent + 1, "<span>" + TextParser.parseForXSL(this.getLabel(), param, rdf) + "</span>");
				xsl.appendL(internalIndent, "</button>");
			} else {
				xsl.appendL(internalIndent, "<input>");
				xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"type\">submit</xsl:attribute>");
				xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"value\">" + TextParser.parseForXSL(this.getLabel(), param, rdf) + "</xsl:attribute>");
				xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"form\">" + queryId + "</xsl:attribute>");
				xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"id\">" + queryId + "_submit</xsl:attribute>");
				xsl.appendL(internalIndent, "</input>");
			}
		}
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
}
