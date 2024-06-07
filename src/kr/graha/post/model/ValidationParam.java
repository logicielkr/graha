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
import kr.graha.post.lib.Key;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import java.util.List;

/**
 * Graha(그라하) param 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class ValidationParam {
	private static final String nodeName = "param";
	private ValidationParam() {
	}
	
	private String name = null;
	private String notNull = null;
	private String msg = null;
	private String maxLength = null;
	private String minLength = null;
	private String numberFormat = null;
	private String dateFormat = null;
	private String format = null;
	private String at = null;
	private String maxValue = null;
	private String minValue = null;
	private String cond = null;
	private String multi = null;
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getNotNull() {
		return this.notNull;
	}
	private void setNotNull(String notNull) {
		this.notNull = notNull;
	}
	private String getMsg() {
		return this.msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
	private String getMaxLength() {
		return this.maxLength;
	}
	private void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	private String getMinLength() {
		return this.minLength;
	}
	private void setMinLength(String minLength) {
		this.minLength = minLength;
	}
	private String getNumberFormat() {
		return this.numberFormat;
	}
	private void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}
	private String getDateFormat() {
		return this.dateFormat;
	}
	private void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	private String getFormat() {
		return this.format;
	}
	private void setFormat(String format) {
		this.format = format;
	}
	private String getAt() {
		return this.at;
	}
	private void setAt(String at) {
		this.at = at;
	}
	private String getMaxValue() {
		return this.maxValue;
	}
	private void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	private String getMinValue() {
		return this.minValue;
	}
	private void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getMulti() {
		return this.multi;
	}
	private void setMulti(String multi) {
		this.multi = multi;
	}
	protected static String nodeName() {
		return ValidationParam.nodeName;
	}
	protected static ValidationParam load(Node element) {
		ValidationParam param = new ValidationParam();
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "not-null")) {
							this.setNotNull(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.setMsg(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "max-length")) {
							this.setMaxLength(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "min-length")) {
							this.setMinLength(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "number-format")) {
							this.setNumberFormat(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "date-format")) {
							this.setDateFormat(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "format")) {
							this.setFormat(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "at")) {
							this.setAt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "max-value")) {
							this.setMaxValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "min-value")) {
							this.setMinValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "multi")) {
							this.setMulti(node.getNodeValue());
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
		element.setAttribute("not-null", this.getNotNull());
		element.setAttribute("msg", this.getMsg());
		element.setAttribute("max-length", this.getMaxLength());
		element.setAttribute("min-length", this.getMinLength());
		element.setAttribute("number-format", this.getNumberFormat());
		element.setAttribute("date-format", this.getDateFormat());
		element.setAttribute("format", this.getFormat());
		element.setAttribute("at", this.getAt());
		element.setAttribute("max-value", this.getMaxValue());
		element.setAttribute("min-value", this.getMinValue());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("multi", this.getMulti());
		return element;
	}
	private void toXSLBottomTile(Buffer xsl, int indent) {
		if(STR.trueValue(this.getMulti())) {
			xsl.appendL(indent + 4, "result = false;");
			xsl.appendL(indent + 3, "} else {");
			xsl.appendL(indent + 4, "alert(_getMessage(\"" + this.getMsg() + "\"));");
			xsl.appendL(indent + 4, "if(typeof(_focus) == \"function\") {_focus(form, \"" + this.getName() + "\" + \".\" + index);}");
			xsl.appendL(indent + 4, "return false;");
			xsl.appendL(indent + 3, "}");
			xsl.appendL(indent + 2, "}");
			xsl.appendL(indent + 2, "index++;");
			xsl.appendL(indent + 1, "} else {");
			xsl.appendL(indent + 2, "break;");
			xsl.appendL(indent + 1, "}");
		} else {
			xsl.appendL(indent + 2, "result = false;");
			xsl.appendL(indent + 1, "} else {");
			xsl.appendL(indent + 2, "alert(_getMessage(\"" + this.getMsg() + "\"));");
			xsl.appendL(indent + 2, "if(typeof(_focus) == \"function\") {_focus(form, \"" + this.getName() + "\");}");
			xsl.appendL(indent + 2, "return false;");
			xsl.appendL(indent + 1, "}");
			xsl.appendL(indent, "}");
		}
	}
	protected Buffer toXSL(Record param, int indent, boolean rdf) {
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
		if(STR.compareIgnoreCase(this.getAt(), "server")) {
			return null;
		}
		Buffer xsl = new Buffer();
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
		}
		if(STR.trueValue(this.getMulti())) {
			boolean exists = false;
			xsl.appendL(indent, "index = 1;");
			xsl.appendL(indent, "while(true) {");
			xsl.appendL(indent + 1, "if(form[\"" + this.getName() + "\" + \".\" + index]) {");
			if(STR.valid(this.getFormat())) {
				xsl.appendL(indent + 2, "if(!_format(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getFormat() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "format" + ":\"" + this.getFormat() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getDateFormat())) {
				xsl.appendL(indent + 2, "if(!_dateFormat(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getDateFormat() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "date_format" + ":\"" + this.getDateFormat() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getNotNull())) {
				xsl.appendL(indent + 2, "if(!_notNull(form, \"" + this.getName() + "\" + \".\" + index)) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "not_null" + ":true});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMaxLength())) {
				xsl.appendL(indent + 2, "if(!_maxLength(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getMaxLength() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "max_length" + ":\"" + this.getMaxLength() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMinLength())) {
				xsl.appendL(indent + 2, "if(!_minLength(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getMinLength() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "min_length" + ":\"" + this.getMinLength() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMaxValue())) {
				xsl.appendL(indent + 2, "if(!_maxValue(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getMaxValue() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "max_value" + ":\"" + this.getMaxValue() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMinValue())) {
				xsl.appendL(indent + 2, "if(!_minValue(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getMinValue() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "min_value" + ":\"" + this.getMinValue() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getNumberFormat())) {
				xsl.appendL(indent + 2, "if(!_numberFormat(form, \"" + this.getName() + "\" + \".\" + index, \"" + this.getNumberFormat() + "\")) {");
				xsl.appendL(indent + 3, "if(arguments.length > 1) {");
				xsl.appendL(indent + 4, "out.push({param:\"" + this.getName() + "\" + \".\" + index, msg:\"" + this.getMsg() + "\", " + "number_format" + ":\"" + this.getNumberFormat() + "\"});");
				exists = true;
				this.toXSLBottomTile(xsl, indent);
			}
			if(!exists) {
				xsl.appendL(indent + 1, "break;");
			}
			xsl.appendL(indent, "}");
		} else {
			if(STR.valid(this.getFormat())) {
				xsl.appendL(indent, "if(!_format(form, \"" + this.getName() + "\", \"" + this.getFormat() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "format" + ":\"" + this.getFormat() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getDateFormat())) {
				xsl.appendL(indent, "if(!_dateFormat(form, \"" + this.getName() + "\", \"" + this.getDateFormat() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "date_format" + ":\"" + this.getDateFormat() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getNotNull())) {
				xsl.appendL(indent, "if(!_notNull(form, \"" + this.getName() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "not_null" + ":true});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMaxLength())) {
				xsl.appendL(indent, "if(!_maxLength(form, \"" + this.getName() + "\", \"" + this.getMaxLength() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "max_length" + ":\"" + this.getMaxLength() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMinLength())) {
				xsl.appendL(indent, "if(!_minLength(form, \"" + this.getName() + "\", \"" + this.getMinLength() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "min_length" + ":\"" + this.getMinLength() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMaxValue())) {
				xsl.appendL(indent, "if(!_maxValue(form, \"" + this.getName() + "\", \"" + this.getMaxValue() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "max_value" + ":\"" + this.getMaxValue() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getMinValue())) {
				xsl.appendL(indent, "if(!_minValue(form, \"" + this.getName() + "\", \"" + this.getMinValue() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "min_value" + ":\"" + this.getMinValue() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
			if(STR.valid(this.getNumberFormat())) {
				xsl.appendL(indent, "if(!_numberFormat(form, \"" + this.getName() + "\", \"" + this.getNumberFormat() + "\")) {");
				xsl.appendL(indent + 1, "if(arguments.length > 1) {");
				xsl.appendL(indent + 2, "out.push({param:\"" + this.getName() + "\", msg:\"" + this.getMsg() + "\", " + "number_format" + ":\"" + this.getNumberFormat() + "\"});");
				this.toXSLBottomTile(xsl, indent);
			}
		}
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
	protected void validate(Record params, List<String> msgs) {
		if(STR.valid(this.getCond()) && !AuthUtility.auth(this.getCond(), params)) {
			return;
		}
		if(STR.compareIgnoreCase(this.getAt(), "client")) {
			return;
		}
		Key key = null;
		if(STR.trueValue(this.getMulti())) {
			int index = 1;
			while(true) {
				key = Record.key(Record.PREFIX_TYPE_PARAM, this.getName(), Integer.toString(index));
				if(params.containsKey(key)) {
					this.validate(key, params, msgs);
					index++;
				} else {
					break;
				}
			}
		} else {
			key = Record.key(Record.PREFIX_TYPE_PARAM, this.getName());
			this.validate(key, params, msgs);
		}
	}
	private void validate(Key key, Record params, List msgs) {
		if(STR.trueValue(this.getNotNull())) {
			if(!params.notNull(key)) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getMaxLength())) {
			if(!params.maxLength(key, this.getMaxLength())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getMinLength())) {
			if(!params.minLength(key, this.getMinLength())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getNumberFormat())) {
			if(!params.numberFormat(key, this.getNumberFormat())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getMaxValue())) {
			if(!params.maxValue(key, this.getMaxValue())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getMinValue())) {
			if(!params.minValue(key, this.getMinValue())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getDateFormat())) {
			if(!params.dateFormat(key, this.getDateFormat())) {
				msgs.add(this.getMsg());
			}
		}
		if(STR.valid(this.getFormat())) {
			if(!params.format(key, this.getFormat())) {
				msgs.add(this.getMsg());
			}
		}
	}
}
