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
 * Graha(그라하) search 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Search {
	private static final String nodeName = "search";
	private Search() {
	}
	
	private String path = null;
	private String label = null;
	private String icon = null;
	private String className = null;
	private String cond = null;
	private String name = null;
	private String method = null;
	private List<SearchParam> param = null;
	private String getPath() {
		return this.path;
	}
	private void setPath(String path) {
		this.path = path;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
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
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getMethod() {
		return this.method;
	}
	private void setMethod(String method) {
		this.method = method;
	}
	private void add(SearchParam param) {
		if(this.param == null) {
			this.param = new ArrayList<SearchParam>();
		}
		this.param.add(param);
	}
	protected static String nodeName() {
		return Search.nodeName;
	}
	protected static Search load(Element element) {
		Search search = new Search();
		if(element != null) {
			search.loadAttr(element);
			search.loadElement(element);
			return search;
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
			this.add(SearchParam.load((Element)node));
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
						if(STR.compareIgnoreCase(node.getNodeName(), "path")) {
							this.setPath(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "icon")) {
							this.setIcon(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
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
		element.setAttribute("path", this.getPath());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("icon", this.getIcon());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("name", this.getName());
		element.setAttribute("method", this.getMethod());
		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((SearchParam)this.param.get(i)).element());
			}
		}
		return element;
	}
	protected Buffer toXSL(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf) {
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
		xsl.appendL(internalIndent, "<form>");
		xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"action\">" + Link.getPath(this.getPath(), param, rdf) + "</xsl:attribute>");
		if(STR.valid(this.getName())) {
			xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
			xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"class\">" + this.getName() + "</xsl:attribute>");
		}
		if(STR.valid(this.getMethod())) {
			xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"method\">" + this.getMethod() + "</xsl:attribute>");
		}
		if(STR.valid(this.param)) {
			for(int i = 0; i < this.param.size(); i++) {
				xsl.append(((SearchParam)this.param.get(i)).search(tables, commands, internalIndent + 1, rdf));
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
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
}
