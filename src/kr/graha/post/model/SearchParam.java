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
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;

/**
 * Graha(그라하) SearchParam 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class SearchParam extends LinkParam {
	private static final String nodeName = "param";
	
	protected SearchParam() {
	}
	
	private String hidden = null;
	private String forName = null;
	private List<Option> option = null;
	
	private String getHidden() {
		return this.hidden;
	}
	private void setHidden(String hidden) {
		this.hidden = hidden;
	}
	protected String getType() {
		return super.getType();
	}
	protected void setType(String type) {
		super.setType(type);
	}
	protected String getRef() {
		return super.getRef();
	}
	protected void setRef(String ref) {
		super.setRef(ref);
	}
	protected String getValue() {
		return super.getValue();
	}
	protected void setValue(String value) {
		super.setValue(value);
	}
	protected String getName() {
		return super.getName();
	}
	protected void setName(String name) {
		super.setName(name);
	}
	private String getForName() {
		return this.forName;
	}
	private void setForName(String forName) {
		this.forName = forName;
	}
	private void add(Option option) {
		if(this.option == null) {
			this.option = new ArrayList<Option>();
		}
		this.option.add(option);
	}
	protected static String nodeName() {
		return SearchParam.nodeName;
	}
	protected static SearchParam load(Element element) {
		SearchParam param = new SearchParam();
		if(element != null) {
			param.loadAttr(element);
			param.loadElement(element);
			return param;
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
		if(STR.compareIgnoreCase(node.getNodeName(), "option")) {
			this.add(Option.load(node));
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
						if(STR.compareIgnoreCase(node.getNodeName(), "option")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "options")) {
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
						if(STR.compareIgnoreCase(node.getNodeName(), "hidden")) {
							this.setHidden(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "ref")) {
							this.setRef(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "for")) {
							this.setForName(node.getNodeValue());
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
		element.setAttribute("hidden", this.getHidden());
		element.setAttribute("type", this.getType());
		element.setAttribute("ref", this.getRef());
		element.setAttribute("value", this.getValue());
		element.setAttribute("name", this.getName());
		element.setAttribute("for", this.getForName());
		if(this.option != null && this.option.size() > 0) {
			for(int i = 0; i < this.option.size(); i++) {
				element.appendChild(((Option)this.option.get(i)).element());
			}
		}
		return element;
	}
	protected Buffer search(List<Table> tables, List<Command> commands, int indent, boolean rdf) {
		if(STR.trueValue(this.getHidden())) {
			return super.hidden(tables, commands, indent, rdf);
		} else {
			Buffer xsl = new Buffer();
			if(STR.compareIgnoreCase(this.getType(), "select")) {
				xsl.appendL(indent, "<select name=\"" + this.getName() + "\" class=\"" + this.getName() + "\" value=\"{" + kr.graha.post.xml.GParam.childNodePath("param", this.getValue(), rdf) + "}\">");
				if(STR.valid(this.getForName())) {
					xsl.appendL(indent + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GCode.optionNodePath(this.getForName(), rdf) + "\">");
					xsl.appendL(indent + 2, "<xsl:choose>");
					xsl.appendL(indent + 3, "<xsl:when test=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + kr.graha.post.xml.GParam.childNodePath("param", this.getValue(), rdf)  + "\">");
					xsl.appendL(indent + 4, "<option value=\"{@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + "}\"  selected=\"selected\"><xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("label", rdf) + "\" /></option>");
					xsl.appendL(indent + 3, "</xsl:when>");
					xsl.appendL(indent + 3, "<xsl:otherwise>");
					xsl.appendL(indent + 4, "<option value=\"{@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + "}\"><xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("label", rdf) + "\" /></option>");
					xsl.appendL(indent + 3, "</xsl:otherwise>");
					xsl.appendL(indent + 2, "</xsl:choose>");
					xsl.appendL(indent + 1, "</xsl:for-each>");
				} else {
					if(STR.valid(this.option)) {
						for(int i = 0; i < this.option.size(); i++) {
							Option obj = (Option)this.option.get(i);
							xsl.appendL(indent + 1, "<xsl:choose>");
							xsl.appendL(indent + 2, "<xsl:when test=\"" + kr.graha.post.xml.GParam.childNodePath("param", this.getValue(), rdf) + " = '" + obj.getValue() + "'\">");
							xsl.appendL(indent + 3, "<option value=\"" + obj.getValue() + "\" selected=\"selected\">" + obj.getLabel() + "</option>");
							xsl.appendL(indent + 2, "</xsl:when>");
							xsl.appendL(indent + 2, "<xsl:otherwise>");
							xsl.appendL(indent + 3, "<option value=\"" + obj.getValue() + "\">" + obj.getLabel() + "</option>");
							xsl.appendL(indent + 2, "</xsl:otherwise>");
							xsl.appendL(indent + 1, "</xsl:choose>");
						}
					}
				}
				xsl.appendL(indent, "</select>");
			} else {
				xsl.appendL(indent, "<input type=\"text\" class=\""+ this.getName() + "\" name=\""+ this.getName() + "\" value=\"{" + kr.graha.post.xml.GParam.childNodePath("param", this.getValue(), rdf) + "}\" />");
				
			}
			return xsl;
		}
	}
}
