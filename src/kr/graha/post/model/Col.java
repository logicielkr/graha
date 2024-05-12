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
 * querys/query/layout/tab/col
 * querys/query/layout/tab/row/col
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Col {
	private static final String nodeName = "column";
	private Col() {
	}
	
	private String label = null;
	private String name = null;
	private String value = null;
	private String islabel = null;
	private String labelWidth = null;
	private String height = null;
	private String width = null;
	private String colspan = null;
	private String rowspan = null;
	private String type = null;
	private String readonly = null;
	private String autocomplete = null;
	private String disabled = null;
	private String icon = null;
	private String val = null;
	private String fmt = null;
	private String className = null;
	private String forName = null;
	private String rcond = null;
	private String dcond = null;
	private String cond = null;
	private String placeholder = null;
	private String pattern = null;
	private String title = null;
	private String required = null;

	private String align = null;
	private String code = null;
	private String test = null;
	private String xTest = null;
	private String escape = null;
	
	private String defaultValue = null;
	private List<String[]> dataAttr = null;
	private List<Option> option = null;
	private List<Link> link = null;

	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getValue() {
		return this.value;
	}
	private void setValue(String value) {
		this.value = value;
	}
	private String getIslabel() {
		return this.islabel;
	}
	private void setIslabel(String islabel) {
		this.islabel = islabel;
	}
	private String getLabelWidth() {
		return this.labelWidth;
	}
	private void setLabelWidth(String labelWidth) {
		this.labelWidth = labelWidth;
	}
	private String getHeight() {
		return this.height;
	}
	private void setHeight(String height) {
		this.height = height;
	}
	private String getWidth() {
		return this.width;
	}
	private void setWidth(String width) {
		this.width = width;
	}
	private String getColspan() {
		return this.colspan;
	}
	private void setColspan(String colspan) {
		this.colspan = colspan;
	}
	private String getRowspan() {
		return this.rowspan;
	}
	private void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}
	protected String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type = type;
	}
	private String getReadonly() {
		return this.readonly;
	}
	private void setReadonly(String readonly) {
		this.readonly = readonly;
	}
	private String getAutocomplete() {
		return this.autocomplete;
	}
	private void setAutocomplete(String autocomplete) {
		this.autocomplete = autocomplete;
	}
	private String getDisabled() {
		return this.disabled;
	}
	private void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	private String getIcon() {
		return this.icon;
	}
	private void setIcon(String icon) {
		this.icon = icon;
	}
	private String getVal() {
		return this.val;
	}
	private void setVal(String val) {
		this.val = val;
	}
	private String getFmt() {
		return this.fmt;
	}
	private void setFmt(String fmt) {
		this.fmt = fmt;
	}
	private String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className = className;
	}
	private String getRcond() {
		return this.rcond;
	}
	private void setRcond(String rcond) {
		this.rcond = rcond;
	}
	private String getDcond() {
		return this.dcond;
	}
	private void setDcond(String dcond) {
		this.dcond = dcond;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getPlaceholder() {
		return this.placeholder;
	}
	private void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	private String getPattern() {
		return this.pattern;
	}
	private void setPattern(String pattern) {
		this.pattern = pattern;
	}
	private String getTitle() {
		return this.title;
	}
	private void setTitle(String title) {
		this.title = title;
	}
	private String getRequired() {
		return this.required;
	}
	private void setRequired(String required) {
		this.required = required;
	}
	private String getAlign() {
		return this.align;
	}
	private void setAlign(String align) {
		this.align = align;
	}

	private String getCode() {
		return this.code;
	}
	private void setCode(String code) {
		this.code = code;
	}
	private String getForName() {
		return this.forName;
	}
	private void setForName(String forName) {
		this.forName = forName;
	}
	private String getTest() {
		return this.test;
	}
	private void setTest(String test) {
		this.test = test;
	}
	private String getXTest() {
		return this.xTest;
	}
	private void setXTest(String xTest) {
		this.xTest = xTest;
	}
	private String getEscape() {
		return this.escape;
	}
	private void setEscape(String escape) {
		this.escape = escape;
	}
	private String getDefaultValue() {
		return this.defaultValue;
	}
	private void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	private void add(Option option) {
		if(this.option == null) {
			this.option = new ArrayList<Option>();
		}
		this.option.add(option);
	}
	private void add(Link link) {
		if(this.link == null) {
			this.link = new ArrayList<Link>();
		}
		this.link.add(link);
	}
	private void add(String attrName, String attrValue) {
		if(this.dataAttr == null) {
			this.dataAttr = new ArrayList<String[]>();
		}
		this.dataAttr.add(new String[]{attrName, attrValue});
	}
	protected static String nodeName() {
		return Col.nodeName;
	}
	protected static Col load(Element element) {
		Col col = new Col();
		if(element != null) {
			col.loadAttr(element);
			col.loadElement(element);
			return col;
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
		} else if(STR.compareIgnoreCase(node.getNodeName(), "link")) {
			this.add(Link.load((Element)node));
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "link")) {
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
						node.getNodeValue() != null
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "islabel")) {
							this.setIslabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "labelWidth")) {
							this.setLabelWidth(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "height")) {
							this.setHeight(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "width")) {
							this.setWidth(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "colspan")) {
							this.setColspan(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "rowspan")) {
							this.setRowspan(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "readonly")) {
							this.setReadonly(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "autocomplete")) {
							this.setAutocomplete(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "disabled")) {
							this.setDisabled(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "icon")) {
							this.setIcon(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "val")) {
							this.setVal(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "fmt")) {
							this.setFmt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "for")) {
							this.setForName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "rcond")) {
							this.setRcond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "dcond")) {
							this.setDcond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "placeholder")) {
							this.setPlaceholder(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "pattern")) {
							this.setPattern(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "title")) {
							this.setTitle(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "required")) {
							this.setRequired(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "align")) {
							this.setAlign(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "code")) {
							this.setCode(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "test")) {
							this.setTest(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xTest")) {
							this.setXTest(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "escape")) {
							this.setEscape(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "default")) {
							this.setDefaultValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xml:base")) {
						} else if(STR.valid(node.getNodeName()) && STR.startsWithIgnoreCase(node.getNodeName(), "data-")) {
							this.add(node.getNodeName(), node.getNodeValue());
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
		element.setAttribute("label", this.getLabel());
		element.setAttribute("name", this.getName());
		element.setAttribute("value", this.getValue());
		element.setAttribute("islabel", this.getIslabel());
		element.setAttribute("labelWidth", this.getLabelWidth());
		element.setAttribute("height", this.getHeight());
		element.setAttribute("width", this.getWidth());
		element.setAttribute("colspan", this.getColspan());
		element.setAttribute("rowspan", this.getRowspan());
		element.setAttribute("type", this.getType());
		element.setAttribute("readonly", this.getReadonly());
		element.setAttribute("autocomplete", this.getAutocomplete());
		element.setAttribute("disabled", this.getDisabled());
		element.setAttribute("icon", this.getIcon());
		element.setAttribute("val", this.getVal());
		element.setAttribute("fmt", this.getFmt());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("for", this.getForName());
		element.setAttribute("rcond", this.getRcond());
		element.setAttribute("dcond", this.getDcond());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("placeholder", this.getPlaceholder());
		element.setAttribute("pattern", this.getPattern());
		element.setAttribute("title", this.getTitle());
		element.setAttribute("required", this.getRequired());
		element.setAttribute("align", this.getAlign());
		element.setAttribute("code", this.getCode());
		element.setAttribute("test", this.getTest());
		element.setAttribute("xTest", this.getXTest());
		element.setAttribute("escape", this.getEscape());
		element.setAttribute("default", this.getDefaultValue());
		if(this.dataAttr != null && this.dataAttr.size() > 0) {
			for(int i = 0; i < this.dataAttr.size(); i++) {
				String[] v = (String[])this.dataAttr.get(i);
				element.setAttribute(v[0], v[1]);
			}
		}
		if(this.option != null && this.option.size() > 0) {
			for(int i = 0; i < this.option.size(); i++) {
				element.appendChild(((Option)this.option.get(i)).element());
			}
		}
		if(this.link != null && this.link.size() > 0) {
			for(int i = 0; i < this.link.size(); i++) {
				element.appendChild(((Link)this.link.get(i)).element());
			}
		}
		return element;
	}
	protected Buffer th(
		Record param,
		int indent,
		boolean rdf,
		boolean div,
		int queryFuncType,
		int viewType
	) {
		if(
			!STR.valid(this.getIslabel()) ||
			!STR.falseValue(this.getIslabel())
		) {
			return tag(param, indent, null, null, null,"th", rdf, div, false, queryFuncType, viewType);
		} {
			return null;
		}
	}
	protected Buffer td(
		Record param,
		int indent,
		Table table,
		List<Col> col,
		String tabName,
		boolean rdf,
		boolean div,
		boolean full,
		int queryFuncType
	) {
		return tag(param, indent, table, col, tabName, "td", rdf, div, full, queryFuncType, Tab.VIEW_TYPE_UNKNOWN);
	}
	private Buffer tag(
		Record param,
		int indent,
		Table table,
		List<Col> cols,
		String tabName,
		String tagName,
		boolean rdf,
		boolean div,
		boolean full,
		int queryFuncType,
		int viewType
	) {
		Buffer xsl = new Buffer();
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
			if(!AuthUtility.auth(authInfo, param)) {
				return xsl;
			} else {
				authInfo = null;
			}
		}
		if(authInfo != null) {
			xsl.appendL("<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
		}
		if(div) {
			xsl.appendL(indent, "<div>");
			if(STR.valid(this.getName())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha " + tagName + " " + this.getName() + "</xsl:attribute>");
			} else {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha " + tagName + "</xsl:attribute>");
			}
		} else {
			xsl.appendL(indent, "<" + tagName + ">");
			if(STR.valid(this.getName())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha " + this.getName() + "</xsl:attribute>");
			} else {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"class\">graha</xsl:attribute>");
			}
		}
		if(STR.compareIgnoreCase(tagName, "th") && STR.valid(this.getLabelWidth())) {
			xsl.appendL(indent + 1, "<xsl:attribute name=\"style\">width:" + this.getLabelWidth() + ";</xsl:attribute>");
		} else {
			if(STR.valid(this.getWidth()) || STR.valid(this.getHeight())) {
				xsl.append(indent + 1, "<xsl:attribute name=\"style\">");
				if(STR.valid(this.getWidth())) {
						xsl.append("width:" + this.getWidth() + ";");
				}
				if(STR.valid(this.getHeight())) {
						xsl.append("height:" + this.getHeight() + ";");
				}
				xsl.appendL("</xsl:attribute>");
			}
		}
		if(STR.compareIgnoreCase(tagName, "td") && STR.valid(this.getAlign())) {
			xsl.appendL(indent + 1, "<xsl:attribute name=\"align\">" + this.getAlign() + "</xsl:attribute>");
		}
		if(
			(viewType == Tab.VIEW_TYPE_LIST || STR.compareIgnoreCase(tagName, "td")) &&
			STR.valid(this.getColspan())
		) {
			xsl.appendL(indent + 1, "<xsl:attribute name=\"colspan\">" + this.getColspan() + "</xsl:attribute>");
		}
		if(
			(viewType == Tab.VIEW_TYPE_LIST || STR.compareIgnoreCase(tagName, "td")) &&
			STR.valid(this.getRowspan())
		) {
			xsl.appendL(indent + 1, "<xsl:attribute name=\"rowspan\">" + this.getRowspan() + "</xsl:attribute>");
		}
		if(STR.compareIgnoreCase(tagName, "th")) {
			xsl.appendL(indent + 1, TextParser.parseForXSL(this.getLabel(), param, rdf));
		} else {
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				if(cols != null) {
					for(int i = 0; i < cols.size(); i++) {
						Col col = (Col)cols.get(i);
						if(STR.compareIgnoreCase(col.getType(), "hidden")) {
							xsl.appendL(indent + 1, "<input>");
							xsl.appendL(indent + 2, "<xsl:attribute name=\"type\">hidden</xsl:attribute>");
							xsl.appendL(indent + 2, "<xsl:attribute name=\"class\">" + col.getName() + "</xsl:attribute>");
							if(full) {
								xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">" + col.getName() + "</xsl:attribute>");
								xsl.appendL(indent + 2, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodePath(tabName, col.getValue(), rdf) + "\" /></xsl:attribute>");
							} else {
								xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">" + col.getName() + ".<xsl:value-of select=\"position()\" /></xsl:attribute>");
								xsl.appendL(indent + 2, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + kr.graha.post.xml.GRow.childNodeName(col.getValue(), rdf) + "\" /></xsl:attribute>");
							}
							xsl.appendL(indent + 1, "</input>");
						}
					}
				}
				xsl.append(this.input(param, indent + 1, table, tabName, rdf, full));
			} else {
				String value = this.value(indent + 1, tabName, rdf, div, full).toString();
				if(this.link != null && this.link.size() > 0) {
					for(int i = 0; i < this.link.size(); i++) {
						xsl.append(((Link)this.link.get(i)).link(param, tabName, value, indent + 1, rdf, full));
					}
				} else {
					xsl.append(value);
				}
			}
		}
		if(div) {
			xsl.appendL(indent, "</div>");
		} else {
			xsl.appendL(indent, "</" + tagName + ">");
		}
		if(authInfo != null) {
			xsl.appendL("</xsl:if>");
		}
		return xsl;
	}
	private Buffer value(int indent, String tabName, boolean rdf, boolean div, boolean full) {
		Buffer xsl = new Buffer();
		String path = this.getPath(this.getName(), tabName, rdf, full);
		
		boolean escape = true;
		String escapeAttrValue = "";
		if(STR.falseValue(this.getEscape())) {
			escape = false;
			escapeAttrValue = " disable-output-escaping=\"yes\"";
		}
		if(STR.trueValue(this.getCode())) {
			if(STR.valid(this.getForName())) {
				xsl.appendL(indent, "<xsl:variable name=\"" + this.getName() + "\" select=\"" + path + "\" />");
				xsl.appendL(indent, "<xsl:value-of select=\"" + kr.graha.post.xml.GCode.optionNodePath(this.getForName(), rdf) + "[@" + kr.graha.post.xml.GCode.valueAttrName(rdf) + " = $" + this.getName() + "]/@" + kr.graha.post.xml.GCode.labelAttrName(rdf) + "\"" + escapeAttrValue + " />");
			} else {
				if(this.option != null && this.option.size() > 0) {
					xsl.appendL(indent, "<xsl:choose>");
					for(int i = 0; i < this.option.size(); i++) {
						Option obj = (Option)this.option.get(i);
						xsl.appendL(indent + 1, "<xsl:when test=\"" + path + " = '" + obj.getValue() + "'\">");
						xsl.appendL(indent + 2, "<xsl:text>" + obj.getLabel() + "</xsl:text>");
						xsl.appendL(indent + 1, "</xsl:when>");
					}
					xsl.appendL(indent + 1, "<xsl:otherwise>");
					xsl.appendL(indent + 2, "<xsl:value-of select=\"" + path + "\"" + escapeAttrValue + " />");
					xsl.appendL(indent + 1, "</xsl:otherwise>");
					xsl.appendL(indent, "</xsl:choose>");
				} else {
					xsl.appendL(indent, "<xsl:value-of select=\"" + path + "\"" + escapeAttrValue + " />");
				}
			}
		} else {
			if(STR.valid(this.getFmt())) {
				xsl.appendL(indent, "<xsl:choose>");
				xsl.appendL(indent + 1, "<xsl:when test=\"" + path + " != ''\">");
				xsl.appendL(indent + 2, "<xsl:value-of select=\"format-number(" + path + ", '" + this.getFmt() + "')\" />");
				xsl.appendL(indent + 1, "</xsl:when>");
				xsl.appendL(indent + 1, "<xsl:otherwise>");
				xsl.appendL(indent + 2, "<xsl:value-of select=\"" + path + "\"" + escapeAttrValue + " />");
				xsl.appendL(indent + 1, "</xsl:otherwise>");
				xsl.appendL(indent, "</xsl:choose>");
			} else {
				xsl.appendL(indent, "<xsl:value-of select=\"" + path + "\"" + escapeAttrValue + " />");
			}
		}
		return xsl;
	}
	private String getPath(String name, String tabName, boolean rdf, boolean full) {
		if(STR.valid(name)) {
			String path = null;
			if(STR.startsWithIgnoreCase(name, "param.")) {
				path = kr.graha.post.xml.GParam.childNodePath("param", name.substring(6), rdf);
			} else if(STR.startsWithIgnoreCase(name, "prop.")) {
				path = kr.graha.post.xml.GParam.childNodePath("prop", name.substring(5), rdf);
			} else if(STR.startsWithIgnoreCase(name, "result.")) {
				path = kr.graha.post.xml.GParam.childNodePath("result", name.substring(7), rdf);
			} else if(STR.startsWithIgnoreCase(name, "query.")) {
				if(full) {
					path = kr.graha.post.xml.GRow.childNodePath(tabName, name.substring(6), rdf);
				} else {
					path = kr.graha.post.xml.GRow.childNodeName(name.substring(6), rdf);
				}
			} else {
				if(full) {
					path = kr.graha.post.xml.GRow.childNodePath(tabName, name, rdf);
				} else {
					path = kr.graha.post.xml.GRow.childNodeName(name, rdf);
				}
			}
			return path;
		}
		return null;
	}
	private Buffer input(Record param, int indent, Table table, String tabName, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		String path = this.getPath(this.getValue(), tabName, rdf, full);
		String defaultValuePath = this.getPath(this.getDefaultValue(), tabName, rdf, full);
		if(STR.compareIgnoreCase(this.getType(), "radio")) {
			if(STR.valid(this.getForName())) {
				xsl.appendL(indent, "<xsl:for-each select=\"" + kr.graha.post.xml.GCode.optionNodePath(this.getForName(), rdf) + "\">");
				xsl.appendL(indent + 1, "<input>");
				xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
				xsl.appendL(indent + 2, "<xsl:attribute name=\"type\">radio</xsl:attribute>");
				if(this.dataAttr != null && this.dataAttr.size() > 0) {
					for(int x = 0; x < this.dataAttr.size(); x++) {
						String[] v = (String[])this.dataAttr.get(x);
						xsl.appendL(indent + 2, "<xsl:attribute name=\"" + v[0] + "\">" + v[1] + "</xsl:attribute>");
					}
				}
				xsl.append(indent + 2, "<xsl:attribute name=\"value\">");
				xsl.append("<xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + "\" />");
				xsl.appendL("</xsl:attribute>");
				if(STR.valid(this.getDefaultValue())) {
					xsl.appendL(indent + 2, "<xsl:choose>");
					xsl.appendL(indent + 3, "<xsl:when test=\"" + path + " and " + path + " != '' and @" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + path + "\">");
					xsl.appendL(indent + 4, "<xsl:attribute name=\"checked\">true</xsl:attribute>");
					xsl.appendL(indent + 3, "</xsl:when>");
					xsl.appendL(indent + 3, "<xsl:when test=\"(not(" + path + ") or " + path + " = '') and @" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + defaultValuePath + "\">");
					xsl.appendL(indent + 4, "<xsl:attribute name=\"checked\">true</xsl:attribute>");
					xsl.appendL(indent + 3, "</xsl:when>");
					xsl.appendL(indent + 2, "</xsl:choose>");
				} else {
					xsl.appendL(indent + 2, "<xsl:if test=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + path + "\">");
					xsl.appendL(indent + 3, "<xsl:attribute name=\"checked\">true</xsl:attribute>");
					xsl.appendL(indent + 2, "</xsl:if>");
				}
				xsl.appendL(indent + 1, "</input>");
				xsl.appendL(indent + 1, "<label><xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("label", rdf) + "\" /></label>");
				xsl.appendL(indent, "</xsl:for-each>");
			} else {
				if(this.option != null && this.option.size() > 0) {
					for(int i = 0; i < this.option.size(); i++) {
						Option obj = (Option)this.option.get(i);
						xsl.appendL(indent, "<input>");
						xsl.appendL(indent + 1, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
						xsl.appendL(indent + 1, "<xsl:attribute name=\"type\">radio</xsl:attribute>");
						if(this.dataAttr != null && this.dataAttr.size() > 0) {
							for(int x = 0; x < this.dataAttr.size(); x++) {
								String[] v = (String[])this.dataAttr.get(x);
								xsl.appendL(indent + 2, "<xsl:attribute name=\"" + v[0] + "\">" + v[1] + "</xsl:attribute>");
							}
						}
						xsl.appendL(indent + 1, "<xsl:attribute name=\"value\">" + obj.getValue() + "</xsl:attribute>");
						if(STR.valid(this.getDefaultValue())) {
							xsl.appendL(indent + 1,"<xsl:choose>");
							xsl.appendL(indent + 2,"<xsl:when test=\"" + path + " and " + path + " != '' and " + path + " = '" + obj.getValue() + "'\">");
							xsl.appendL(indent + 3,"<xsl:attribute name=\"checked\">true</xsl:attribute>");
							xsl.appendL(indent + 2,"</xsl:when>");
							xsl.appendL(indent + 2,"<xsl:when test=\"(not(" + path + ") or " + path + " = '') and " + defaultValuePath + " = '" + obj.getValue() + "'\">");
							xsl.appendL(indent + 3,"<xsl:attribute name=\"checked\">true</xsl:attribute>");
							xsl.appendL(indent + 2,"</xsl:when>");
							xsl.appendL(indent + 1, "</xsl:choose>");
						} else {
							xsl.appendL(indent + 1, "<xsl:if test=\"" + path + " = '" + obj.getValue() + "'\">");
							xsl.appendL(indent + 2,"<xsl:attribute name=\"checked\">true</xsl:attribute>");
							xsl.appendL(indent + 1, "</xsl:if>");
						}
						xsl.appendL(indent, "</input>");
						xsl.appendL(indent, "<label>" + obj.getLabel() + "</label>");
					}
				}
			}
		} else {
			if(STR.compareIgnoreCase(this.getType(), "textarea")) {
				xsl.appendL(indent, "<textarea>");
			} else if(STR.compareIgnoreCase(this.getType(), "select")) {
				xsl.appendL(indent, "<select>");
			} else if(STR.compareIgnoreCase(this.getType(), "button") && STR.valid(this.getIcon())) {
				xsl.appendL(indent, "<button>");
			} else {
				xsl.appendL(indent, "<input>");
			}
			xsl.append(indent + 1, "<xsl:attribute name=\"class\">");
			xsl.append(this.getName());
			if(STR.trueValue(this.getReadonly()) || STR.compareIgnoreCase(this.getReadonly(), "readonly")) {
				xsl.append(" readonly");
			}
			xsl.appendL("</xsl:attribute>");
			if(STR.vexistsIgnoreCase(this.getType(), "textarea", "select")) {
			} else {
				if(
					!STR.valid(this.getType()) ||
					STR.compareIgnoreCase(this.getType(), "datalist")
				) {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"type\">text</xsl:attribute>");
				} else {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"type\">" + this.getType() + "</xsl:attribute>");
				}
			}
			if(full) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
			} else {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"name\">" + this.getName() + ".<xsl:value-of select=\"position()\" /></xsl:attribute>");
			}
			if(STR.trueValue(this.getReadonly()) || STR.compareIgnoreCase(this.getReadonly(), "readonly")) {
				AuthInfo authInfo = null;
				if(STR.valid(this.getRcond())) {
					authInfo = AuthUtility.parse(this.getRcond());
				}
				if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
					if(AuthUtility.auth(authInfo, param)) {
						xsl.appendL(indent + 1, "<xsl:attribute name=\"readonly\">readonly</xsl:attribute>");
					}
				} else {
					if(authInfo != null) {
						xsl.appendL(indent + 1, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
						xsl.append(1, "");
					}
					xsl.appendL(indent + 1, "<xsl:attribute name=\"readonly\">readonly</xsl:attribute>");
					if(authInfo != null) {
						xsl.appendL(indent + 1, "</xsl:if>");
					}
				}
			}
			if(table != null) {
				if(STR.valid(this.getValue())) {
					Column column = null;
					column = table.getColumn(this.getValue(), param);
					if(column != null) {
						if(STR.valid(column.getExpr())) {
							xsl.appendL(indent + 1, "<xsl:attribute name=\"expr\">" + column.getExpr() + "</xsl:attribute>");
						}
						if(STR.valid(column.getFollow())) {
							xsl.appendL(indent + 1, "<xsl:attribute name=\"follow\">" + column.getFollow() + "</xsl:attribute>");
						}
						if(STR.valid(column.getDatatype())) {
							xsl.appendL(indent + 1, "<xsl:attribute name=\"datatype\">" + column.getDatatype() + "</xsl:attribute>");
						}
						if(STR.valid(column.getConstraint())) {
							xsl.appendL(indent + 1, "<xsl:attribute name=\"constraint\">" + column.getConstraint() + "</xsl:attribute>");
						}
					}
				}
			}
			if(STR.valid(this.getAutocomplete())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"autocomplete\">" + this.getAutocomplete() + "</xsl:attribute>");
			}
			if(STR.valid(this.getPlaceholder())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"placeholder\">" + this.getPlaceholder() + "</xsl:attribute>");
			}
			if(STR.valid(this.getPattern())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"pattern\">" + this.getPattern() + "</xsl:attribute>");
			}
			if(STR.valid(this.getTitle())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"title\">" + this.getTitle() + "</xsl:attribute>");
			}
			if(STR.valid(this.getRequired())) {
				xsl.appendL(indent + 1, "<xsl:attribute name=\"required\">" + this.getRequired() + "</xsl:attribute>");
			}
			if(this.dataAttr != null && this.dataAttr.size() > 0) {
				for(int x = 0; x < this.dataAttr.size(); x++) {
					String[] v = (String[])this.dataAttr.get(x);
					xsl.appendL(indent + 1, "<xsl:attribute name=\"" + v[0] + "\">" + v[1] + "</xsl:attribute>");
				}
			}
			if(STR.valid(this.getDisabled())) {
				AuthInfo authInfo = null;
				if(STR.valid(this.getDcond())) {
					authInfo = AuthUtility.parse(this.getDcond());
				}
				if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
					if(AuthUtility.auth(authInfo, param)) {
						xsl.appendL(indent + 1, "<xsl:attribute name=\"disabled\">" + this.getDisabled() + "</xsl:attribute>");
					}
				} else {
					if(authInfo != null) {
						xsl.appendL(indent + 1, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
						xsl.append(1, "");
					}
					xsl.appendL(indent + 1, "<xsl:attribute name=\"disabled\">" + this.getDisabled() + "</xsl:attribute>");
					if(authInfo != null) {
						xsl.appendL(indent + 1, "</xsl:if>");
					}
				}
			}
			if(STR.compareIgnoreCase(this.getType(), "textarea")) {
				if(STR.valid(this.getDefaultValue())) {
					xsl.appendL(indent + 1, "<xsl:choose>");
					xsl.appendL(indent + 2, "<xsl:when test=\"" + path + " and " + path + " != ''\">");
					xsl.appendL(indent + 3, "<xsl:value-of select=\"" + path + "\" />");
					xsl.appendL(indent + 2, "</xsl:when>");
					xsl.appendL(indent + 2, "<xsl:otherwise>");
					xsl.appendL(indent + 3, "<xsl:value-of select=\"" + defaultValuePath + "\" />");
					xsl.appendL(indent + 2, "</xsl:otherwise>");
					xsl.appendL(indent + 1, "</xsl:choose>");
				} else {
					xsl.appendL(indent + 1, "<xsl:value-of select=\"" + path + "\" />");
				}
			} else {
				if(STR.compareIgnoreCase(this.getType(), "button")) {
					if(!STR.valid(this.getIcon())) {
						xsl.appendL(indent + 1, "<xsl:attribute name=\"value\">" + this.getValue() + "</xsl:attribute>");
					}
				} else if(STR.valid(path)) {
					if(STR.compareIgnoreCase(this.getType(), "checkbox")) {
						if(STR.valid(this.getVal())) {
							if(STR.valid(this.getDefaultValue())) {
								xsl.appendL(indent + 1, "<xsl:choose>");
								xsl.appendL(indent + 2, "<xsl:when test=\"" + path + " and " + path + " != '' and " + path + " = '" + this.getVal() + "'\"><xsl:attribute name=\"checked\">checked</xsl:attribute></xsl:when>");
								xsl.appendL(indent + 2, "<xsl:when test=\"(not(" + path + ") or " + path + " = '') and " + defaultValuePath + " = '" + this.getVal() + "'\"><xsl:attribute name=\"checked\">checked</xsl:attribute></xsl:when>");
								xsl.appendL(indent + 1, "</xsl:choose>");
							} else {
								xsl.appendL(indent + 1, "<xsl:if test=\"" + path + " = '" + this.getVal() + "'\"><xsl:attribute name=\"checked\">checked</xsl:attribute></xsl:if>");
							}
						}
						xsl.appendL(indent + 1, "<xsl:attribute name=\"value\">" + this.getVal() + "</xsl:attribute>");
					} else if(STR.valid(this.getFmt())) {
						xsl.appendL(indent + 1, "<xsl:choose>");
						xsl.appendL(indent + 2, "<xsl:when test=\"" + path + " and " + path + " != ''\">");
						xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"format-number(" + path + ", '" + this.getFmt() + "')\"/></xsl:attribute>");
						xsl.appendL(indent + 2, "</xsl:when>");
						if(STR.valid(this.getDefaultValue())) {
							xsl.appendL(indent + 2, "<xsl:when test=\"" + defaultValuePath + " and " + defaultValuePath + " != ''\">");
							xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"format-number(" + defaultValuePath + ", '" + this.getFmt() + "')\"/></xsl:attribute>");
							xsl.appendL(indent + 2, "</xsl:when>");
						}
						xsl.appendL(indent + 2, "<xsl:otherwise>");
						xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + path + "\"/></xsl:attribute>");
						xsl.appendL(indent + 2, "</xsl:otherwise>");
						xsl.appendL(indent + 1, "</xsl:choose>");
					} else {
						if(STR.valid(this.getDefaultValue())) {
							xsl.appendL(indent + 1, "<xsl:choose>");
							xsl.appendL(indent + 2, "<xsl:when test=\"" + path + " and " + path + " != ''\">");
							xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + path + "\"/></xsl:attribute>");
							xsl.appendL(indent + 2, "</xsl:when>");
							xsl.appendL(indent + 2, "<xsl:otherwise>");
							xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + defaultValuePath + "\"/></xsl:attribute>");
							xsl.appendL(indent + 2, "</xsl:otherwise>");
							xsl.appendL(indent + 1, "</xsl:choose>");
						} else {
							xsl.appendL(indent + 1, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + path + "\"/></xsl:attribute>");
						}
					}
				}
			}
			if(STR.compareIgnoreCase(this.getType(), "select")) {
				if(STR.valid(this.getForName())) {
					if(STR.valid(this.getDefaultValue())) {
					} else {
						xsl.appendL(indent + 1, "<xsl:variable name=\"selected\"><xsl:value-of select=\"" + path + "\" /></xsl:variable>");
					}
					xsl.appendL(indent + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GCode.optionNodePath(this.getForName(), rdf) + "\">");
					xsl.appendL(indent + 2, "<option>");
					xsl.appendL(indent + 3, "<xsl:attribute name=\"value\"><xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + "\"/></xsl:attribute>");
					if(STR.valid(this.getDefaultValue())) {
						xsl.appendL(indent + 3, "<xsl:choose>");
						xsl.appendL(indent + 4, "<xsl:when test=\"" + path + " and " + path + " != '' and @" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + path + "\">");
						xsl.appendL(indent + 5, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
						xsl.appendL(indent + 4, "</xsl:when>");
						xsl.appendL(indent + 4, "<xsl:when test=\"(not(" + path + ") or " + path + " = '') and @" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = " + defaultValuePath + "\">");
						xsl.appendL(indent + 5, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
						xsl.appendL(indent + 4, "</xsl:when>");
						xsl.appendL(indent + 3, "</xsl:choose>");
					} else {
						xsl.appendL(indent + 3, "<xsl:if test=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + " = $selected\">");
						xsl.appendL(indent + 4, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
						xsl.appendL(indent + 3, "</xsl:if>");
					}
					xsl.appendL(indent + 3, "<xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("label", rdf) + "\" />");
					xsl.appendL(indent + 2, "</option>");
					xsl.appendL(indent + 1, "</xsl:for-each>");
				} else {
					if(this.option != null && this.option.size() > 0) {
						for(int i = 0; i < this.option.size(); i++) {
							Option obj = (Option)this.option.get(i);
							xsl.appendL(indent + 1, "<option>");
							xsl.appendL(indent + 2, "<xsl:attribute name=\"value\">" + obj.getValue() + "</xsl:attribute>");
							if(STR.valid(this.getDefaultValue())) {
								xsl.appendL(indent + 2, "<xsl:choose>");
								xsl.appendL(indent + 3, "<xsl:when test=\"" + path + " and " + path + " != '' and " + path + " = '" + obj.getValue() + "'\">");
								xsl.appendL(indent + 4, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
								xsl.appendL(indent + 3, "</xsl:when>");
								xsl.appendL(indent + 3, "<xsl:when test=\"(not(" + path + ") or " + path + " = '') and " + defaultValuePath + " = '" + obj.getValue() + "'\">");
								xsl.appendL(indent + 4, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
								xsl.appendL(indent + 3, "</xsl:when>");
								xsl.appendL(indent + 2, "</xsl:choose>");
							} else {
								xsl.appendL(indent + 2, "<xsl:if test=\"" + path + " = '" + obj.getValue() + "'\">");
								xsl.appendL(indent + 3, "<xsl:attribute name=\"selected\">selected</xsl:attribute>");
								xsl.appendL(indent + 2, "</xsl:if>");
							}
							xsl.appendL(indent + 2, "<xsl:text>" + obj.getLabel() + "</xsl:text>");
							xsl.appendL(indent + 1, "</option>");
						}
					}
				}
			}
			if(STR.compareIgnoreCase(this.getType(), "datalist")) {
				if(full) {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"list\">" + this.getName() + "</xsl:attribute>");
				} else {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"list\">" + this.getName() + ".<xsl:value-of select=\"position()\" /></xsl:attribute>");
				}
			}
			if(STR.compareIgnoreCase(this.getType(), "button") && STR.valid(this.getIcon())) {
				if(STR.valid(this.getClassName())) {
					xsl.appendL(indent + 1, "<i class=\"" + this.getClassName() + "\">" + this.getIcon() + "</i>");
				} else {
					xsl.appendL(indent + 1, "<i>" + this.getIcon() + "</i>");
				}
				xsl.appendL(indent + 1, "<span>" + this.getValue() + "</span>");
			}
			if(STR.compareIgnoreCase(this.getType(), "textarea")) {
				xsl.appendL(indent, "</textarea>");
			} else if(STR.compareIgnoreCase(this.getType(), "select")) {
				xsl.appendL(indent, "</select>");
			} else if(STR.compareIgnoreCase(this.getType(), "button") && STR.valid(this.getIcon())) {
				xsl.appendL(indent, "</button>");
			} else {
				xsl.appendL(indent, "</input>");
			}
			if(STR.compareIgnoreCase(this.getType(), "datalist")) {
				if(full) {
					xsl.appendL(indent, "<datalist id=\"" + this.getName() + "\">");
				} else {
					xsl.appendL(indent, "<datalist  id=\"" + this.getName() + ".{position()}\">");
				}
				if(STR.valid(this.getForName())) {
					xsl.appendL(indent + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GCode.optionNodePath(this.getForName(), rdf) + "\">");
					xsl.appendL(indent + 2, "<option value=\"{@" + kr.graha.post.xml.GCode.optionChildAttrName("value", rdf) + "}\"><xsl:value-of select=\"@" + kr.graha.post.xml.GCode.optionChildAttrName("label", rdf) + "\" /></option>");
					xsl.appendL(indent + 1, "</xsl:for-each>");
				} else {
					if(this.option != null && this.option.size() > 0) {
						for(int i = 0; i < this.option.size(); i++) {
							Option obj = (Option)this.option.get(i);
							xsl.appendL(indent + 1, "<option value=\"" + obj.getValue() + "\">" + obj.getLabel() + "</option>");
						}
					}
				}
				xsl.appendL(indent, "</datalist>");
			}
		}
		return xsl;
	}
}
