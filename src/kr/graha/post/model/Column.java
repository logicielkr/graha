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

import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;
import java.util.Map;
import kr.graha.post.interfaces.Encryptor;
import java.security.NoSuchProviderException;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.interfaces.ConnectionFactory;
import java.sql.SQLException;
import java.util.List;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) column 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Column extends Param {
	private static final String nodeName = "column";
	protected Column() {
	}
	private String primary = null;
	private String foreign = null;
	private String insert = null;
	private String only = null;
	private String expr = null;
	private String follow = null;
	private String constraint = null;
	private String select = null;
	private Boolean valid = null;
	
	public String getName() {
		return super.getName();
	}
	protected void setName(String name) {
		super.setName(name);
	}
	public String getValue() {
		return super.getValue();
	}
	protected void setValue(String value) {
		super.setValue(value);
	}
	protected String getPrimary() {
		return this.primary;
	}
	private void setPrimary(String primary) {
		this.primary = primary;
	}
	protected String getForeign() {
		return this.foreign;
	}
	private void setForeign(String foreign) {
		this.foreign = foreign;
	}
	protected String getDatatype() {
		return super.getDatatype();
	}
	protected void setDatatype(String datatype) {
		super.setDatatype(datatype);
	}
	protected String getDefaultValue() {
		return super.getDefaultValue();
	}
	protected void setDefaultValue(String defaultValue) {
		super.setDefaultValue(defaultValue);
	}
	protected String getPattern() {
		return super.getPattern();
	}
	protected void setPattern(String pattern) {
		super.setPattern(pattern);
	}
	protected String getEncrypt() {
		return super.getEncrypt();
	}
	protected void setEncrypt(String encrypt) {
		super.setEncrypt(encrypt);
	}
	protected String getInsert() {
		return this.insert;
	}
	private void setInsert(String insert) {
		this.insert = insert;
	}
	protected String getOnly() {
		return this.only;
	}
	private void setOnly(String only) {
		this.only = only;
	}
	protected String getExpr() {
		return this.expr;
	}
	private void setExpr(String expr) {
		this.expr = expr;
	}
	protected String getFollow() {
		return this.follow;
	}
	private void setFollow(String follow) {
		this.follow = follow;
	}
	protected String getConstraint() {
		return this.constraint;
	}
	private void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	protected String getSelect() {
		return this.select;
	}
	private void setSelect(String select) {
		this.select = select;
	}
	protected boolean valid(Record params) {
		if(this.valid == null) {
			this.valid = true;
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
//			LOG.debug(this.getCond());
//			AuthUtility.debug(tabAuthInfo);
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, params)) {
				if(!AuthUtility.auth(tabAuthInfo, params)) {
					this.valid = false;
				}
			}
		}
		return this.valid.booleanValue();
	}
	protected static String nodeName() {
		return Column.nodeName;
	}
	protected static Column load(Node element) {
		Column column = new Column();
		if(element != null) {
			column.loadAttr(element);
			return column;
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
						node.getNodeValue() != null
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "primary")) {
							this.setPrimary(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "foreign")) {
							this.setForeign(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "datatype")) {
							this.setDatatype(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "default")) {
							if(node.getNodeValue() == null) {
								this.setDefaultValue("");
							} else {
								this.setDefaultValue(node.getNodeValue());
							}
						} else if(STR.compareIgnoreCase(node.getNodeName(), "pattern")) {
							this.setPattern(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "insert")) {
							this.setInsert(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "only")) {
							this.setOnly(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "expr")) {
							this.setExpr(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "follow")) {
							this.setFollow(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "constraint")) {
							this.setConstraint(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "select")) {
							this.setSelect(node.getNodeValue());
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
		element.setAttribute("value", this.getValue());
		element.setAttribute("primary", this.getPrimary());
		element.setAttribute("foreign", this.getForeign());
		element.setAttribute("datatype", this.getDatatype());
		element.setAttribute("default", this.getDefaultValue());
		element.setAttribute("pattern", this.getPattern());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("insert", this.getInsert());
		element.setAttribute("only", this.getOnly());
		element.setAttribute("expr", this.getExpr());
		element.setAttribute("follow", this.getFollow());
		element.setAttribute("constraint", this.getConstraint());
		element.setAttribute("select", this.getSelect());
		element.setAttribute("cond", this.getCond());
		return element;
	}
	protected SQLParameter getValue(
		Record params,
		int idx,
		Map<String, Encryptor> encryptor,
		List<Table> tables,
		ConnectionFactory connectionFactory,
		int sqlType
	) throws NoSuchProviderException, SQLException {
		Object value = null;
		if(STR.valid(this.getValue())) {
			String key = this.getValue();
			if(idx >= 0 && STR.startsWithIgnoreCase(this.getValue(), "param.") && !STR.trueValue(this.getForeign())) {
				key = this.getValue() + "." + idx;
			}
			if(params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, key))) {
				value = super.getValue(params, Record.key(Record.PREFIX_TYPE_UNKNOWN, key), encryptor);
				return new SQLParameter(value, this.getDataType());
			} else if(
				STR.startsWithIgnoreCase(this.getInsert(), "system.uuid") ||
				STR.startsWithIgnoreCase(this.getInsert(), "system.uuid2")
			) {
				if(sqlType == Table.SQL_TYPE_INSERT) {
					if(!params.hasKey(Record.key(Record.PREFIX_TYPE_UUID, key))) {
						String uuid = null;
						if(STR.startsWithIgnoreCase(this.getInsert(), "system.uuid")) {
							uuid = java.util.UUID.randomUUID().toString();
						} else {
							uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
						}
							params.put(Record.key(Record.PREFIX_TYPE_UUID, key), uuid);
					}
					value = super.getValue(params, Record.key(Record.PREFIX_TYPE_UUID, key), encryptor);
					return new SQLParameter(value, this.getDataType());
				}
			} else if(STR.startsWithIgnoreCase(this.getInsert(), "sequence.")) {
				if(sqlType == Table.SQL_TYPE_INSERT) {
					if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SEQUENCE, key))) {
						if(this.getDataType() == Param.DATA_TYPE_INT) {
							params.put(Record.key(Record.PREFIX_TYPE_SEQUENCE, key), SQLExecutor.getNextSequenceIntegerValue(this.getInsert(), connectionFactory));
						} else {
							params.put(Record.key(Record.PREFIX_TYPE_SEQUENCE, key), SQLExecutor.getNextSequenceLongValue(this.getInsert(), connectionFactory));
						}
					}
					value = super.getValue(params, Record.key(Record.PREFIX_TYPE_SEQUENCE, key), encryptor);
					return new SQLParameter(value, this.getDataType());
				}
			} else if(STR.trueValue(this.getForeign()) && STR.valid(tables)) {
				if(sqlType == Table.SQL_TYPE_INSERT) {
					if(params.hasKey(Record.key(Record.PREFIX_TYPE_SEQUENCE, this.getValue()))) {
						value = super.getValue(params, Record.key(Record.PREFIX_TYPE_SEQUENCE, this.getValue()), encryptor);
						return new SQLParameter(value, this.getDataType());
					} else {
						Table table = null;
						Column column = null;
						for(int i = 0; i < tables.size(); i++) {
							table = (Table)tables.get(i);
							column = table.getPrimaryColumnByValue(this.getValue(), params);
							break;
						}
						if(column != null) {
							if(
								STR.startsWithIgnoreCase(column.getInsert(), "system.uuid") ||
								STR.startsWithIgnoreCase(column.getInsert(), "system.uuid2")
							) {
								if(!params.hasKey(Record.key(Record.PREFIX_TYPE_UUID, column.getValue()))) {
									String uuid = null;
									if(STR.startsWithIgnoreCase(column.getInsert(), "system.uuid")) {
										uuid = java.util.UUID.randomUUID().toString();
									} else {
										uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
									}
										params.put(Record.key(Record.PREFIX_TYPE_UUID, column.getValue()), uuid);
								}
								value = super.getValue(params, Record.key(Record.PREFIX_TYPE_UUID, column.getValue()), encryptor);
								return new SQLParameter(value, this.getDataType());
							} else if(STR.startsWithIgnoreCase(column.getInsert(), "sequence.")) {
								if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SEQUENCE, column.getValue()))) {
									if(this.getDataType() == Param.DATA_TYPE_INT) {
										params.put(Record.key(Record.PREFIX_TYPE_SEQUENCE, column.getValue()), SQLExecutor.getNextSequenceIntegerValue(column.getInsert(), connectionFactory));
									} else {
										params.put(Record.key(Record.PREFIX_TYPE_SEQUENCE, column.getValue()), SQLExecutor.getNextSequenceLongValue(column.getInsert(), connectionFactory));
									}
								}
								value = super.getValue(params, Record.key(Record.PREFIX_TYPE_SEQUENCE, column.getValue()), encryptor);
								return new SQLParameter(value, this.getDataType());
							} else if(STR.compareIgnoreCase(column.getInsert(), "generate")) {
								value = super.getValue(params, Record.key(Record.PREFIX_TYPE_GENERATE, column.getValue()), encryptor);
								return new SQLParameter(value, this.getDataType());
							}
						}
					}
				}
			}
		}
		if(
			this.getDefaultValue() != null &&
			!STR.compareIgnoreCase(this.getDefaultValue(), "null") &&
			!STR.compareIgnoreCase(this.getDefaultValue(), "nil")
		) {
			if(STR.startsWithIgnoreCase(this.getDefaultValue(), "prop.")) {
				value = this.getValue(params, Record.key(Record.PREFIX_TYPE_PROP, this.getDefaultValue().substring(5)), encryptor);
			} else if(STR.startsWithIgnoreCase(this.getDefaultValue(), "param.")) {
				value = this.getValue(params, Record.key(Record.PREFIX_TYPE_PARAM, this.getDefaultValue().substring(6)), encryptor);
			} else if(STR.startsWithIgnoreCase(this.getDefaultValue(), "code.")) {
				value = this.getValue(params, Record.key(Record.PREFIX_TYPE_CODE, this.getDefaultValue().substring(5)), encryptor);
			} else if(STR.startsWithIgnoreCase(this.getDefaultValue(), "query.")) {
				value = this.getValue(params, Record.key(Record.PREFIX_TYPE_QUERY, this.getDefaultValue().substring(6)), encryptor);
			} else {
				value = super.getDefault(params, encryptor);
			}
		}
		return new SQLParameter(value, this.getDataType());
	}
}
