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

import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) CalculatorParam 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class CalculatorParam {
	private static final String nodeName = "param";
	private CalculatorParam() {
	}
	
	private String name = null;
	private String expr = null;
	private String func = null;
	private String refer = null;
	private String event = null;
	private String form = null;
	private String cond = null;
	
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getExpr() {
		return this.expr;
	}
	private void setExpr(String expr) {
		this.expr = expr;
	}
	private String getFunc() {
		return this.func;
	}
	private void setFunc(String func) {
		this.func = func;
	}
	private String getRefer() {
		return this.refer;
	}
	private void setRefer(String refer) {
		this.refer = refer;
	}
	private String getEvent() {
		return this.event;
	}
	private void setEvent(String event) {
		this.event = event;
	}
	private String getForm() {
		return this.form;
	}
	private void setForm(String form) {
		this.form = form;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	protected static String nodeName() {
		return CalculatorParam.nodeName;
	}
	protected static CalculatorParam load(Node element) {
		CalculatorParam param = new CalculatorParam();
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "expr")) {
							this.setExpr(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "func")) {
							this.setFunc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "refer")) {
							this.setRefer(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "event")) {
							this.setEvent(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "form")) {
							this.setForm(node.getNodeValue());
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
		XmlElement element = new XmlElement(CalculatorParam.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("expr", this.getExpr());
		element.setAttribute("func", this.getFunc());
		element.setAttribute("refer", this.getRefer());
		element.setAttribute("event", this.getEvent());
		element.setAttribute("form", this.getForm());
		element.setAttribute("cond", this.getCond());
		return element;
	}
	protected Buffer toXSL(Record params, String queryId, int indent, boolean rdf) {
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, params)) {
			if(!AuthUtility.auth(authInfo, params)) {
				return null;
			} else {
				authInfo = null;
			}
		}
		Buffer xsl = new Buffer();
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, params, rdf) + "\">");
		}
		if(STR.valid(this.getExpr())) {
			xsl.appendL(indent, "expr:\"" + this.getExpr().replace("\"", "\\\"") + "\",");
		}
		if(STR.valid(this.getEvent())) {
			xsl.appendL(indent, "event:\"" + this.getEvent() + "\",");
		}
		if(STR.valid(this.getRefer())) {
			xsl.appendL(indent, "refer:\"" + this.getRefer() + "\",");
		}
		if(STR.valid(this.getFunc())) {
			xsl.appendL(indent, "func:" + this.getFunc() + ",");
		}
		if(STR.valid(this.getName())) {
			xsl.appendL(indent, "name:\"" + this.getName() + "\",");
		}
		if(STR.valid(this.getForm())) {
			xsl.appendL(indent, "formName:\"" + this.getForm() + "\"");
		} else {
			xsl.appendL(indent, "formName:\"" + queryId + "\"");
		}
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
}
