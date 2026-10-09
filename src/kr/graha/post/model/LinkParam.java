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
import kr.graha.post.xml.GRedirect;
import kr.graha.post.xml.GRedirectParam;
import kr.graha.post.xml.GDocument;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

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
	private String cond = null;
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
	protected String getCond() {
		return this.cond;
	}
	protected void setCond(String cond) {
		this.cond = cond;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
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
		element.setAttribute("cond", this.getCond());
		return element;
	}
	protected static Buffer param(String tabName, List<LinkParam> params, Record record, int indent, boolean rdf, boolean full) {
		return LinkParam.param(null, null, tabName, params, record, indent, rdf, full);
	}
	protected static Buffer param(List<Table> tables, List<Command> commands, String tabName, List<LinkParam> params, Record record, int indent, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		if(STR.valid(params)) {
			xsl.append(indent, "<xsl:variable name=\"hrefparam\">");
			List paramList = new ArrayList();
			String expr = null;
			boolean isContainsConst = false;
			for(int i = 0; i < params.size(); i++) {
				LinkParam param = (LinkParam)params.get(i);
				AuthInfo authInfo = null;
				if(STR.valid(param.getCond())) {
					authInfo = AuthUtility.parse(param.getCond());
				}
				if(authInfo != null && AuthUtility.testInServer(authInfo, record)) {
					if(!AuthUtility.auth(authInfo, record)) {
						return null;
					} else {
						authInfo = null;
					}
				}
				if(authInfo != null) {
					xsl.append("<xsl:if test=\"" + AuthUtility.testExpr(authInfo, record, rdf) + "\">");
				}
				String type = param.getType();
				String value = param.getValue();
				if(!STR.valid(type)) {
					if(STR.valid(value)) {
						if(STR.startsWithIgnoreCase(value, "query.")) {
							value = value.substring(6);
							type = "query";
						} else if(STR.startsWithIgnoreCase(value, "prop.")) {
							value = value.substring(5);
							type = "prop";
						} else if(STR.startsWithIgnoreCase(value, "param.")) {
							value = value.substring(6);
							type = "param";
						}
					} else {
						value = param.getName();
						type = "param";
					}
				}
				
				if(STR.compareIgnoreCase(type, "query")) {
					if(STR.valid(value) && value.indexOf(".") > 0) {
						expr = kr.graha.post.xml.GRow.childNodePath(
							value.substring(0, value.indexOf(".")),
							value.substring(value.indexOf(".") + 1),
							rdf
						);
					} else if(tabName == null || STR.valid(param.getRef())) {
						expr = kr.graha.post.xml.GRow.childNodePath(param.getRef(), value, rdf);
					} else {
						if(full) {
							expr = kr.graha.post.xml.GRow.childNodePath(tabName, value, rdf);
						} else {
							expr = kr.graha.post.xml.GRow.childNodeName(value, rdf);
						}
					}
					paramList.add(expr);
				} else if(STR.compareIgnoreCase(type, "param")) {
					expr = kr.graha.post.xml.GParam.childNodePath("linkparam", value, rdf);
					paramList.add(expr);
				} else if(STR.vexistsIgnoreCase(type, "prop", "result")) {
					expr = kr.graha.post.xml.GParam.childNodePath(type, value, rdf);
					paramList.add(expr);
				} else if(!STR.valid(type)) {
					expr = kr.graha.post.xml.GParam.childNodePath("linkparam", value, rdf);
					paramList.add(expr);
				} else if(STR.vexistsIgnoreCase(type, "default", "const")) {
				}
				if(STR.vexistsIgnoreCase(type, "query", "param", "prop", "result")) {
					xsl.append("<xsl:if test=\"");
					if(
						(
							!STR.valid(type) ||
							STR.compareIgnoreCase(type, "param")
						) &&
						STR.compareIgnoreCase(value, "page")
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
					int paramListSize = (paramList.size() - 1);
					if(STR.vexistsIgnoreCase(type, "default", "const")) {
						paramListSize = paramList.size();
					}
					if(paramList != null && paramListSize > 0) {
						xsl.append("<xsl:if test=\"");
						for(int x = 0; x < paramListSize; x++) {
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
				if(!STR.valid(type) || STR.vexistsIgnoreCase(type, "query", "param", "prop", "result")) {
					xsl.append(param.getName() + "=");
					xsl.append("<xsl:value-of select=\"" + expr + "\"/>");
					xsl.append("</xsl:if>");
				} else if(STR.vexistsIgnoreCase(type, "default", "const")) {
					xsl.append(param.getName() + "=");
					xsl.append(value);
					isContainsConst = true;
				}
				if(authInfo != null) {
					xsl.append("</xsl:if>");
				}
			}
			xsl.appendL("</xsl:variable>");
		}
		return xsl;
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf) {
		return this.hidden(tables, commands, param, true, indent, rdf);
	}
	protected Buffer hidden(String tabName, Record param, int indent, boolean rdf, boolean full) {
		return this.hidden(null, null, tabName, param, true, indent, rdf, full);
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, Record param, boolean hideBlank, int indent, boolean rdf) {
		return this.hidden(tables, commands, null, param, hideBlank, indent, rdf, true);
	}
	protected Buffer hidden(List<Table> tables, List<Command> commands, String tabName, Record param, boolean hideBlank, int indent, boolean rdf, boolean full) {
		Buffer xsl = new Buffer();
		int internalIndent = indent;
		String expr = null;
		
		String type = this.getType();
		String value = this.getValue();
		if(!STR.valid(type)) {
			if(STR.valid(value)) {
				if(STR.startsWithIgnoreCase(value, "query.")) {
					value = value.substring(6);
					type = "query";
				} else if(STR.startsWithIgnoreCase(value, "prop.")) {
					value = value.substring(5);
					type = "prop";
				} else if(STR.startsWithIgnoreCase(value, "param.")) {
					value = value.substring(6);
					type = "param";
				}
			} else {
				value = this.getName();
				type = "param";
			}
		}
		if(STR.compareIgnoreCase(type, "query")) {
			if(STR.valid(value) && value.indexOf(".") > 0) {
				expr = kr.graha.post.xml.GRow.childNodePath(
					value.substring(0, value.indexOf(".")),
					value.substring(value.indexOf(".") + 1),
					rdf
				);
			} else if(STR.valid(this.getRef())) {
				expr = kr.graha.post.xml.GRow.childNodePath(this.getRef(), value, rdf);
			} else {
				if(full) {
					expr = kr.graha.post.xml.GRow.childNodePath(tabName, value, rdf);
				} else {
					expr = kr.graha.post.xml.GRow.childNodeName(value, rdf);
				}
			}
		} else if(STR.vexistsIgnoreCase(type, "param", "prop", "result", "error")) {
			expr = kr.graha.post.xml.GParam.childNodePath(type, value, rdf);
		} else if(!STR.valid(type)) {
			expr = kr.graha.post.xml.GParam.childNodePath("param", value, rdf);
		} else if(STR.vexistsIgnoreCase(type, "default", "const")) {
		} else {
			throw new GrahaParsingException("type is empty or query, param, prop, result, error, default, const");
		}
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
		if(!STR.valid(type) || STR.vexistsIgnoreCase(type, "query", "param", "prop", "result", "error")) {
			if(hideBlank) {
				if(
					(
						!STR.valid(type) ||
						STR.compareIgnoreCase(type, "param")
					) &&
					STR.compareIgnoreCase(value, "page")
				) {
					xsl.appendL(internalIndent, "<xsl:if test=\"" + expr + " and " + expr + " > 1\">");
				} else {
					xsl.appendL(internalIndent, "<xsl:if test=\"" + expr + " and " + expr + " != ''\">");
				}
			}
			xsl.appendL(internalIndent + 1, "<input>");
			if(STR.valid(this.getRef())) {
				xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"data-graha-ref\">" + this.getRef() + "</xsl:attribute>");
			}
			if(STR.valid(type)) {
				xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"data-graha-type\">" + type + "</xsl:attribute>");
			}
			if(STR.valid(value)) {
				xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"data-graha-value\">" + value + "</xsl:attribute>");
			}
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"type\">hidden</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"class\">graha " + this.getName() + "</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"value\"><xsl:value-of select=\"" + expr + "\" /></xsl:attribute>");
			xsl.appendL(internalIndent + 1, "</input>");
			if(hideBlank) {
				xsl.appendL(internalIndent, "</xsl:if>");
			}
		} else if(STR.vexistsIgnoreCase(type, "default", "const")) {
			xsl.appendL(internalIndent + 1, "<input>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"type\">hidden</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"class\">graha " + this.getName() + "</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"name\">" + this.getName() + "</xsl:attribute>");
			xsl.appendL(internalIndent + 2, "<xsl:attribute name=\"value\">" + value + "</xsl:attribute>");
			xsl.appendL(internalIndent + 1, "</input>");
		}
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
	protected void execute(GRedirect redirect, GDocument document, Record param) {
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo == null || AuthUtility.auth(authInfo, param)) {
			GRedirectParam redirectParam = new GRedirectParam(this.getName());
			String type = this.getType();
			String value = this.getValue();
			if(!STR.valid(type)) {
				if(STR.valid(value)) {
					if(STR.startsWithIgnoreCase(value, "query.")) {
						value = value.substring(6);
						type = "query";
					} else if(STR.startsWithIgnoreCase(value, "prop.")) {
						value = value.substring(5);
						type = "prop";
					} else if(STR.startsWithIgnoreCase(value, "param.")) {
						value = value.substring(6);
						type = "param";
					}
				} else {
					value = this.getName();
					type = "param";
				}
			}
			if(STR.compareIgnoreCase(type, "query")) {
				if(STR.valid(value) && value.indexOf(".") > 0) {
					redirectParam.setValue(kr.graha.post.xml.GRow.childNodeValue(
						value.substring(0, value.indexOf(".")),
						value.substring(value.indexOf(".") + 1),
						document
					));
				} else if(STR.valid(this.getRef())) {
					redirectParam.setValue(kr.graha.post.xml.GRow.childNodeValue(this.getRef(), value, document));
				} else {
					redirectParam.setValue(kr.graha.post.xml.GRow.childNodeValue(null, value, document));
				}
			} else if(STR.vexistsIgnoreCase(type, "param", "prop", "result", "error")) {
				redirectParam.setValue(kr.graha.post.xml.GParam.childNodeValue(type, value, param));
			} else if(!STR.valid(type)) {
				redirectParam.setValue(kr.graha.post.xml.GParam.childNodeValue(null, value, param));
			} else if(STR.vexistsIgnoreCase(type, "default", "const")) {
				redirectParam.setValue(value);
			} else {
				throw new GrahaParsingException("type is empty or query, param, prop, result, error, default, const");
			}
			if(STR.nonempty(redirectParam.getValue())) {
				if(
					(
						!STR.valid(type) ||
						STR.compareIgnoreCase(type, "param")
					) &&
					STR.compareIgnoreCase(value, "page")
				) {
					if(STR.intValue(redirectParam.getValue(), 0) > 1) {
						redirect.add(redirectParam);
					}
				} else {
					redirect.add(redirectParam);
				}
			}
		}
	}
}
