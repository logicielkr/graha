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
import kr.graha.post.lib.Key;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import java.util.Map;
import kr.graha.post.interfaces.Encryptor;
import java.security.NoSuchProviderException;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.interfaces.ConnectionFactory;

/**
 * Graha(그라하) param 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Param {
	private static final String nodeName = "param";
	
	public static int DATA_TYPE_UNKNOWN = 0;
	
	public static int DATA_TYPE_INT = 1;
	public static int DATA_TYPE_LONG = 2;
	public static int DATA_TYPE_FLOAT = 3;
	public static int DATA_TYPE_DOUBLE = 4;
	
	public static int DATA_TYPE_BOOLEAN = 5;
	
	public static int DATA_TYPE_DATE = 6;
	public static int DATA_TYPE_TIMESTAMP = 7;
	
	public static int DATA_TYPE_VARCHAR = 8;
	public static int DATA_TYPE_CHAR = 9;
	
	private String name = null;
	private String datatype = null;
	private String value = null;
	private String defaultValue = null;
	private String pattern = null;
	private String encrypt = null;
	private String cond = null;
	protected Param() {
	}
	public int getDataType() {
		if(STR.compareIgnoreCase(this.getDatatype(), "varchar")) {
			return Param.DATA_TYPE_VARCHAR;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "char")) {
			return Param.DATA_TYPE_CHAR;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "boolean")) {
			return Param.DATA_TYPE_BOOLEAN;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "int")) {
			return Param.DATA_TYPE_INT;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "float")) {
			return Param.DATA_TYPE_FLOAT;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "double")) {
			return Param.DATA_TYPE_DOUBLE;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "long")) {
			return Param.DATA_TYPE_LONG;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "date")) {
			return Param.DATA_TYPE_DATE;
		} else if(STR.compareIgnoreCase(this.getDatatype(), "timestamp")) {
			return Param.DATA_TYPE_TIMESTAMP;
		}
		return Param.DATA_TYPE_UNKNOWN;
	}
	protected String getName() {
		return this.name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected String getDatatype() {
		return this.datatype;
	}
	protected void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getValue() {
		return this.value;
	}
	protected void setValue(String value) {
		this.value = value;
	}
	protected String getDefaultValue() {
		return this.defaultValue;
	}
	protected void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	protected String getPattern() {
		return this.pattern;
	}
	protected void setPattern(String pattern) {
		this.pattern = pattern;
	}
	protected String getEncrypt() {
		return this.encrypt;
	}
	protected void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	public String getCond() {            
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	protected static String nodeName() {
		return Param.nodeName;
	}
	protected static Param load(Node element) {
		Param param = new Param();
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "datatype")) {
							this.setDatatype(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "default")) {
							this.setDefaultValue(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "pattern")) {
							this.setPattern(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
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
		XmlElement element = new XmlElement(this.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("datatype", this.getDatatype());
		element.setAttribute("value", this.getValue());
		element.setAttribute("default", this.getDefaultValue());
		element.setAttribute("pattern", this.getPattern());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("cond", this.getCond());
		return element;
	}
	protected Object getValue(Record params, kr.graha.post.lib.Key key, Map<String, Encryptor> encryptor) throws NoSuchProviderException {
		if(this.getDataType() == Param.DATA_TYPE_INT) {
			return params.getIntObject(key);
		} else if(this.getDataType() == Param.DATA_TYPE_LONG) {
			return params.getLongObject(key);
		} else if(this.getDataType() == Param.DATA_TYPE_FLOAT) {
			return params.getFloatObject(key);
		} else if(this.getDataType() == Param.DATA_TYPE_DOUBLE) {
			return params.getDoubleObject(key);
		} else if(this.getDataType() == Param.DATA_TYPE_BOOLEAN) {
			return Boolean.valueOf(params.getBoolean(key));
		} else if(this.getDataType() == Param.DATA_TYPE_DATE) {
			if(STR.valid(this.getPattern())) {
				return params.getDate(key, this.getPattern());
			} else {
				return params.getDate(key);
			}
		} else if(this.getDataType() == Param.DATA_TYPE_TIMESTAMP) {
			if(STR.valid(this.getPattern())) {
				return params.getTimestamp(key, this.getPattern());
			} else {
				return params.getTimestamp(key);
			}
		} else if(
			this.getDataType() == Param.DATA_TYPE_VARCHAR ||
			this.getDataType() == Param.DATA_TYPE_CHAR
		) {
			if(encryptor != null && STR.valid(this.getEncrypt()) && encryptor.containsKey(this.getEncrypt())) {
				return encryptor.get(this.getEncrypt()).encrypt(params.getString(key));
			} else {
				return params.getString(key);
			}
		}
		return null;
	}
	protected Object getDefault(Record params, Map<String, Encryptor> encryptor) throws NoSuchProviderException {
		String defaultValue = this.getDefaultValue();
		Key key = Record.key(Record.PREFIX_TYPE_UNKNOWN, defaultValue);
		if(key.getPrefix() != Record.PREFIX_TYPE_NONE) {
			defaultValue = params.getString(key);
		}
		if(this.getDataType() == Param.DATA_TYPE_INT) {
			return Integer.valueOf(defaultValue);
		} else if(this.getDataType() == Param.DATA_TYPE_LONG) {
			return Long.valueOf(defaultValue);
		} else if(this.getDataType() == Param.DATA_TYPE_FLOAT) {
			return Float.valueOf(defaultValue);
		} else if(this.getDataType() == Param.DATA_TYPE_DOUBLE) {
			return Double.valueOf(defaultValue);
		} else if(this.getDataType() == Param.DATA_TYPE_BOOLEAN) {
			return Boolean.valueOf(STR.trueValue(defaultValue));
		} else if(this.getDataType() == Param.DATA_TYPE_DATE) {
			if(STR.compareIgnoreCase(this.getDefaultValue(), "system.today")) {
				if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SYSTEM, "today"))) {
					java.util.Date d = new java.util.Date();
					params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today"), d);
					return new java.sql.Date(d.getTime());
				} else {
					return params.getDate(Record.key(Record.PREFIX_TYPE_UNKNOWN, "today"));
				}
			} else {
				if(STR.valid(this.getPattern())) {
					java.sql.Date d = STR.parseSqlDate(defaultValue, this.getPattern());
					return d;
				} else {
					return null;
				}
			}
		} else if(this.getDataType() == Param.DATA_TYPE_TIMESTAMP) {
			if(STR.compareIgnoreCase(this.getDefaultValue(), "system.today")) {
				if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SYSTEM, "today"))) {
					java.util.Date d = new java.util.Date();
					params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today"), d);
					return new java.sql.Timestamp(d.getTime());
				} else {
					return params.getTimestamp(Record.key(Record.PREFIX_TYPE_UNKNOWN, "today"));
				}
			} else {
				if(STR.valid(this.getPattern())) {
					java.sql.Timestamp d = STR.parseTimestamp(defaultValue, this.getPattern());
					return d;
				} else {
					return null;
				}
			}
		} else if(
			this.getDataType() == Param.DATA_TYPE_VARCHAR ||
			this.getDataType() == Param.DATA_TYPE_CHAR
		) {
			String result = null;
			if(STR.compareIgnoreCase(this.getDefaultValue(), "system.today.yyyy")) {
				if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.yyyy"))) {
					int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
					params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.yyyy"), Integer.toString(year));
				}
				result = params.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, "today.yyyy"));
			} else if(STR.compareIgnoreCase(this.getDefaultValue(), "system.today.mm")) {
				if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.mm"))) {
					int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
					month++;
					if(month >= 10) {
						params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.mm"), Integer.toString(month));
					} else {
						params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.mm"), "0" + Integer.toString(month));
					}
				}
				result = params.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, "today.mm"));
			} else if(STR.compareIgnoreCase(this.getDefaultValue(), "system.today.dd")) {
				if(!params.hasKey(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.dd"))) {
					int day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
					if(day >= 10) {
						params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.dd"), Integer.toString(day));
					} else {
						params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "today.dd"), "0" + Integer.toString(day));
					}
				}
				result = params.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, "today.dd"));
			} else {
				result = defaultValue;
			}
			if(result != null && encryptor != null && STR.valid(this.getEncrypt()) && encryptor.containsKey(this.getEncrypt())) {
				return encryptor.get(this.getEncrypt()).encrypt(result);
			} else {
				return result;
			}
		}
		return null;
	}
	protected SQLParameter getValue(
		Record params, Map<String,
		Encryptor> encryptor
	) throws NoSuchProviderException {
		if(!STR.valid(this.getCond()) || AuthUtility.auth(this.getCond(), params)) {
			Object value = null;
			if(STR.valid(this.getValue()) && params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, this.getValue()))) {
				value = this.getValue(params, Record.key(Record.PREFIX_TYPE_UNKNOWN, this.getValue()), encryptor);
			} else if(
				STR.valid(this.getDefaultValue()) &&
				!STR.compareIgnoreCase(this.getDefaultValue(), "null")
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
					value = this.getDefault(params, encryptor);
				}
				if(value != null && STR.startsWithIgnoreCase(this.getValue(), "param.")) {
					if(value instanceof String) {
						if(!STR.compareIgnoreCase((String)value, "%")) {
							params.put(Record.key(Record.PREFIX_TYPE_PARAM, this.getValue().substring(6)), value);
						}
					} else {
						params.put(Record.key(Record.PREFIX_TYPE_PARAM, this.getValue().substring(6)), value);
					}
				}
			}
			return new SQLParameter(value, this.getDataType());
		}
		return null;
	}
}
