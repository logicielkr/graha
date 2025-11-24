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
import kr.graha.post.element.XmlElement;
import kr.graha.post.lib.GrahaParsingException;

/**
 * Graha(그라하) LinkParam 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class LinkParam {
	private static final String nodeName = "param";
	protected LinkParam() {
	}
	
	private String name = null;
	private String value = null;
	private String type = null;
	private String ref = null;
	protected String getName() {
		return this.name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected String getValue() {
		return this.value;
	}
	protected void setValue(String value) {
		this.value = value;
	}
	protected String getType() {
		return this.type;
	}
	protected void setType(String type) {
		this.type = type;
	}
	protected String getRef() {
		return this.ref;
	}
	protected void setRef(String ref) {
		this.ref = ref;
	}
	protected static String nodeName() {
		return LinkParam.nodeName;
	}
	protected static LinkParam load(Element element) {
		LinkParam param = new LinkParam();
		if(element != null) {
			param.loadAttr(element);
			return param;
		}
		return null;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "ref")) {
							this.setRef(node.getNodeValue());
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
		XmlElement element = new XmlElement(LinkParam.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("value", this.getValue());
		element.setAttribute("type", this.getType());
		element.setAttribute("ref", this.getRef());
		return element;
	}
	protected static Buffer param(String tabName, List<LinkParam> params, int indent, boolean rdf, boolean full) {
		return LinkParam.param(null, null, tabName, params, indent, rdf, full);
	}
	protected static Buffer param(List<Table> tables, List<Command> commands, String tabName, List<LinkParam> params, int indent, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		if(STR.valid(params)) {
			xsl.append(indent, "<xsl:variable name=\"hrefparam\">");
			List paramList = new ArrayList();
			String expr = null;
			boolean isContainsConst = false;
			for(int i = 0; i < params.size(); i++) {
				LinkParam param = (LinkParam)params.get(i);
				if(STR.compareIgnoreCase(param.getType(), "query")) {
					if(STR.valid(param.getValue()) && param.getValue().indexOf(".") > 0) {
						expr = kr.graha.post.xml.GRow.childNodePath(
							param.getValue().substring(0, param.getValue().indexOf(".")),
							param.getValue().substring(param.getValue().indexOf(".") + 1),
							rdf
						);
					} else if(tabName == null || STR.valid(param.getRef())) {
						expr = kr.graha.post.xml.GRow.childNodePath(param.getRef(), param.getValue(), rdf);
						/*
						String ref = param.getRef();
						if(!STR.valid(ref)) {
							if(tables != null && tables.size() > 0) {
								ref = ((Table)tables.get(0)).getName();
							} else if(commands != null && commands.size() > 0) {
								ref = ((Command)commands.get(0)).getName();
							}
						}
						if(STR.valid(ref)) {
							expr = kr.graha.post.xml.GRow.childNodePath(ref, param.getValue(), rdf);
						} else {
							expr = kr.graha.post.xml.GRow.childNodeName(param.getValue(), rdf);
						}
						*/
					} else {
						if(full) {
							expr = kr.graha.post.xml.GRow.childNodePath(tabName, param.getValue(), rdf);
						} else {
							expr = kr.graha.post.xml.GRow.childNodeName(param.getValue(), rdf);
						}
					}
					paramList.add(expr);
				} else if(STR.compareIgnoreCase(param.getType(), "param")) {
					expr = kr.graha.post.xml.GParam.childNodePath("linkparam", param.getValue(), rdf);
					paramList.add(expr);
				} else if(STR.vexistsIgnoreCase(param.getType(), "prop", "result")) {
					expr = kr.graha.post.xml.GParam.childNodePath(param.getType(), param.getValue(), rdf);
					paramList.add(expr);
				} else if(!STR.valid(param.getType())) {
					expr = kr.graha.post.xml.GParam.childNodePath("linkparam", param.getValue(), rdf);
					paramList.add(expr);
				} else if(STR.vexistsIgnoreCase(param.getType(), "default", "const")) {
				}
				if(STR.vexistsIgnoreCase(param.getType(), "query", "param", "prop", "result")) {
					xsl.append("<xsl:if test=\"");
					if(
						(
							!STR.valid(param.getType()) ||
							STR.compareIgnoreCase(param.getType(), "param")
						) &&
						STR.compareIgnoreCase(param.getValue(), "page")
					) {
						xsl.append("(");
						xsl.append(expr);
						xsl.append(" and ");
						xsl.append(expr);
						xsl.append(" > 1");
						xsl.append(")");
					} else {
						xsl.append("(");
						xsl.append(expr);
						xsl.append(" and ");
						xsl.append(expr);
						xsl.append(" != ''");
						xsl.append(")");
					}
					xsl.append("\">");
				}
				if(isContainsConst) {
					xsl.append("&amp;");
				} else if(i > 0) {
					if(paramList != null && paramList.size() > 1) {
						xsl.append("<xsl:if test=\"");
						for(int x = 0; x < (paramList.size() - 1); x++) {
							if(x > 0) {
								xsl.append(" or ");
							}
							if(kr.graha.helper.STR.compareIgnoreCase((String)paramList.get(x), kr.graha.post.xml.GParam.childNodePath("param", "page", rdf))) {
								xsl.append("(");
								xsl.append(paramList.get(x));
								xsl.append(" and ");
								xsl.append(paramList.get(x));
								xsl.append(" > 1");
								xsl.append(")");
							} else {
								xsl.append("(");
								xsl.append(paramList.get(x));
								xsl.append(" and ");
								xsl.append(paramList.get(x));
								xsl.append(" != ''");
								xsl.append(")");
							}
						}
						xsl.append("\">&amp;</xsl:if>");
					}
				}
				if(!STR.valid(param.getType()) || STR.vexistsIgnoreCase(param.getType(), "query", "param", "prop", "result")) {
					xsl.append(param.getName() + "=");
					xsl.append("<xsl:value-of select=\"" + expr + "\"/>");
					xsl.append("</xsl:if>");
				} else if(STR.vexistsIgnoreCase(param.getType(), "default", "const")) {
					xsl.append(param.getName() + "=");
					xsl.append(param.getValue());
					isContainsConst = true;
				}
			}
			xsl.appendL("</xsl:variable>");
		}
		return xsl;
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, int indent, boolean rdf) {
		return this.hidden(tables, commands, true, indent, rdf);
	}
	protected Buffer hidden(String tabName, int indent, boolean rdf, boolean full) {
		return this.hidden(null, null, tabName, true, indent, rdf, full);
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, boolean hideBlank, int indent, boolean rdf) {
		return this.hidden(tables, commands, null, hideBlank, indent, rdf, true);
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, String tabName, boolean hideBlank, int indent, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		String expr = null;
		if(STR.compareIgnoreCase(this.getType(), "query")) {
			if(STR.valid(this.getValue()) && this.getValue().indexOf(".") > 0) {
				expr = kr.graha.post.xml.GRow.childNodePath(
					this.getValue().substring(0, this.getValue().indexOf(".")),
					this.getValue().substring(this.getValue().indexOf(".") + 1),
					rdf
				);
			} else if(tabName == null || STR.valid(this.getRef())) {
				expr = kr.graha.post.xml.GRow.childNodePath(this.getRef(), this.getValue(), rdf);
				/*
				String ref = this.getRef();
				if(!STR.valid(ref)) {
					if(tables != null && tables.size() > 0) {
						ref = ((Table)tables.get(0)).getName();
					} else if(commands != null && commands.size() > 0) {
						ref = ((Command)commands.get(0)).getName();
					}
				}
				if(STR.valid(ref)) {
					expr = kr.graha.post.xml.GRow.childNodePath(ref, this.getValue(), rdf);
				} else {
					expr = kr.graha.post.xml.GRow.childNodeName(this.getValue(), rdf);
				}
				*/
			} else {
				if(full) {
					expr = kr.graha.post.xml.GRow.childNodePath(tabName, this.getValue(), rdf);
				} else {
					expr = kr.graha.post.xml.GRow.childNodeName(this.getValue(), rdf);
				}
			}
		} else if(STR.vexistsIgnoreCase(this.getType(), "param", "prop", "result", "error")) {
			expr = kr.graha.post.xml.GParam.childNodePath(this.getType(), this.getValue(), rdf);
		} else if(!STR.valid(this.getType())) {
			expr = kr.graha.post.xml.GParam.childNodePath("param", this.getValue(), rdf);
		} else if(STR.vexistsIgnoreCase(this.getType(), "default", "const")) {
		} else {
			throw new GrahaParsingException("type is empty or query, param, prop, result, error, default, const");
		}
		if(!STR.valid(this.getType()) || STR.vexistsIgnoreCase(this.getType(), "query", "param", "prop", "result", "error")) {
			if(hideBlank) {
				if(
					(
						!STR.valid(this.getType()) ||
						STR.compareIgnoreCase(this.getType(), "param")
					) &&
					STR.compareIgnoreCase(this.getValue(), "page")
				) {
					xsl.appendL(indent, "<xsl:if test=\"" + expr + " and " + expr + " > 1\">");
				} else {
					xsl.appendL(indent, "<xsl:if test=\"" + expr + " and " + expr + " != ''\">");
				}
			}
			xsl.appendL(indent + 1, "<input type=\"hidden\" class=\"" + this.getName() + "\" name=\"" + this.getName() + "\" value=\"{" + expr + "}\" />");
			if(hideBlank) {
				xsl.appendL(indent, "</xsl:if>");
			}
		} else if(STR.vexistsIgnoreCase(this.getType(), "default", "const")) {
			xsl.appendL(indent, "<input type=\"hidden\" class=\"" + this.getName() + "\" name=\"" + this.getName() + "\" value=\"" + this.getValue() + "\" />");
		}
		return xsl;
	}
	
}
