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
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.element.XmlElement;
import kr.graha.post.xml.GCode;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import kr.graha.helper.DB;
import kr.graha.helper.LOG;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.interfaces.Encryptor;
import java.util.Map;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.lib.Buffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import kr.graha.post.model.utility.AuthUtility;

/**
 * querys/query/header/codes/code
 * querys/header/codes/code
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Code extends SQLExecutor {
	private static final String nodeName = "code";
	private Code() {
	}
	
	private String name = null;
	private String sqlAttr = null;
	private String encrypt = null;
	private List<Param> param = null;
	private List<Encrypt> encrypts = null;
	private List<Option> option = null;
	private Node sql = null;
	private String cond = null;
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getSqlAttr() {
		return this.sqlAttr;
	}
	private void setSqlAttr(String sqlAttr) {
		this.sqlAttr = sqlAttr;
	}
	private String getEncrypt() {
		return this.encrypt;
	}
	private void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	private Node getSql() {
		return this.sql;
	}
	private void setSql(Node sql) {
		this.sql = sql;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond  = cond ;
	}
	private void add(Param param) {
		if(this.param == null) {
			this.param = new ArrayList<Param>();
		}
		this.param.add(param);
	}
	private void add(Encrypt encrypt) {
		if(this.encrypts == null) {
			this.encrypts = new ArrayList<Encrypt>();
		}
		this.encrypts.add(encrypt);
	}
	private void add(Option option) {
		if(this.option == null) {
			this.option = new ArrayList<Option>();
		}
		this.option.add(option);
	}
	protected static String nodeName() {
		return Code.nodeName;
	}
	protected static Code load(Element element) {
		Code code = new Code();
		if(element != null) {
			code.loadAttr(element);
			code.loadElement(element);
			return code;
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
		if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
			this.setSql(node);
		} else if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(Param.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
			this.add(Encrypt.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "option")) {
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
						if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "params")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypts")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "option")) {
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
						if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
							this.setSqlAttr(node.getNodeValue());
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
		element.setAttribute("sql", this.getSqlAttr());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("cond", this.getCond());
		element.appendChild(this.getSql());
		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((Param)this.param.get(i)).element());
			}
		}
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		if(this.option != null && this.option.size() > 0) {
			for(int i = 0; i < this.option.size(); i++) {
				element.appendChild(((Option)this.option.get(i)).element());
			}
		}
		return element;
	}
	protected GCode execute(Record params, ConnectionFactory connectionFactory) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.getCond()) && !AuthUtility.auth(this.getCond(), params)) {
			return null;
		}
		GCode code = new GCode(this.getName());
		Buffer sql = new Buffer();
		if(STR.valid(this.getSqlAttr())) {
			sql.append(this.getSqlAttr());
		} else {
			sql = super.parseSQL(this.sql, params);
		}
		if(sql != null && sql.valid()) {
			super.setConnectionFactory(connectionFactory);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
				List<SQLParameter> parameters = new ArrayList();
				if(STR.valid(this.param)) {
					for(int i = 0; i < this.param.size(); i++) {
						Param p = (Param)this.param.get(i);
							SQLParameter parameter =  p.getValue(
							params,
							encryptor
						);
						if(parameter != null) {
							parameters.add(parameter);
						}
					}
				}
				pstmt = super.prepareStatement(sql);
				super.bind(pstmt, parameters);
				rs = pstmt.executeQuery();
				int index = 0;
				while(rs.next()) {
					code.add(rs.getString(1), rs.getString(2));
					if(index == 0) {
						params.put(Record.key(Record.PREFIX_TYPE_CODE, this.getName(), "firstValue"), rs.getString(1));
					}
					index++;
				}
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
			} catch (SQLException | NoSuchProviderException e) {
				LOG.severe(e);
				throw e;
			} finally {
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
			}
		} else {
			if(STR.valid(this.option)) {
				for(int i = 0; i < this.option.size(); i++) {
					Option o = this.option.get(i);
					code.add(o.getValue(), o.getLabel());
					if(i == 0) {
						params.put(Record.key(Record.PREFIX_TYPE_CODE, this.getName(), "firstValue"), o.getValue());
					}
				}
			}
		}
		return code;
	}
}
