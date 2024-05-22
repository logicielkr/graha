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
import org.w3c.dom.Node;
import kr.graha.helper.STR;
import kr.graha.post.element.XmlElement;
import org.w3c.dom.NamedNodeMap;
import java.util.List;
import kr.graha.post.interfaces.ConnectionFactory;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import kr.graha.post.interfaces.Validator;
import kr.graha.helper.LOG;
import java.lang.reflect.InvocationTargetException;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) ValidationCommand 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class ValidationCommand extends Auth {
	private static final String nodeName = "command";
	private String name = null;
	private String func = null;
	private String type = null;
	private String className = null;
	private String msg = null;
	
	private ValidationCommand() {
	}
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type  = type ;
	}
	private String getFunc() {
		return this.func;
	}
	private void setFunc(String func) {
		this.func = func;
	}
	private String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className  = className ;
	}
	private String getMsg() {
		return this.msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
	protected static String nodeName() {
		return ValidationCommand.nodeName;
	}
	protected static ValidationCommand load(Node element) {
		ValidationCommand param = new ValidationCommand();
		if(element != null) {
			param.loadAttr(element);
			param.loadElement(element);
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
						if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							super.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "check")) {
							super.setCheck(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "func")) {
							this.setFunc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.setMsg(node.getNodeValue());
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
		super.element(element);
		element.setAttribute("name", this.getName());
		element.setAttribute("type", this.getType());
		element.setAttribute("func", this.getFunc());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("msg", this.getMsg());
		return element;
	}
	
	protected Buffer toValidationXSL(Record params, int indent, boolean rdf) {
		if(
			STR.compareIgnoreCase(this.getType(), "native") &&
			STR.valid(this.getFunc())
		) {
			AuthInfo authInfo = null;
			if(STR.valid(super.getCond())) {
				authInfo = AuthUtility.parse(super.getCond());
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
			xsl.appendL(indent, "var _msg = " + this.getFunc() + "(form, \"" + this.getName() + "\");");
			xsl.appendL(indent, "if(_msg != null) {");
			xsl.appendL(indent + 1, "if(arguments.length > 1) {");
			xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:_msg, not_null:true});");
			xsl.appendL(indent + 2, "result = false;");
			xsl.appendL(indent + 1, "} else {");
			xsl.appendL(indent + 2, "alert(_getMessage(_msg));");
			xsl.appendL(indent + 2, "if(typeof(_focus) == \"function\") {_focus(form, \"" + this.getName() + "\");}");
			xsl.appendL(indent + 2, "return false;");
			xsl.appendL(indent + 1, "}");
			xsl.appendL(indent, "}");
			if(authInfo != null) {
				xsl.appendL(indent, "</xsl:if>");
			}
			return xsl;
		}
		return null;
	}
	protected void validate(Record params, ConnectionFactory connectionFactory, List<String> msgs) throws NoSuchProviderException, SQLException {
		if(!super.valid(params)) {
			return;
		}
		if(
			STR.compareIgnoreCase(this.getType(), "native") &&
			STR.valid(this.getClassName())
		) {
			try {
				Validator validator = (Validator)Class.forName(this.getClassName()).getConstructor().newInstance();
				String msg = validator.execute(params, connectionFactory.getConnection());
				if(msg != null) {
					msgs.add(msg);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
				LOG.severe(ex); 
			}
		} else {
			if(!super.check(params, connectionFactory)) {
				msgs.add(this.getMsg());
			}
		}
	}
}
