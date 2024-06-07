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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;
import kr.graha.helper.DB;
import kr.graha.helper.LOG;
import java.sql.SQLException;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.model.utility.AuthUtility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.interfaces.Encryptor;
import java.util.Map;
import kr.graha.post.lib.Buffer;
import kr.graha.post.interfaces.ConnectionFactory;
import java.security.NoSuchProviderException;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) auth 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Auth extends SQLExecutor {
	private static final String nodeName = "auth";
	protected Auth() {
	}
	
	private String check = null;
	private Node sql = null;
	private List param = null;
	private String encrypt = null;
	private List<Encrypt> encrypts = null;
	
	private String cond = null;
	private Boolean valid = null;
	
	private String getEncrypt() {
		return this.encrypt;
	}
	protected void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	
	private String getCheck() {
		return this.check;
	}
	protected void setCheck(String check) {
		this.check = check;
	}
	protected String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private Node getSql() {
		return this.sql;
	}
	protected void setSql(Node sql) {
		this.sql = sql;
	}
	protected void add(Param param) {
		if(this.param == null) {
			this.param = new ArrayList();
		}
		this.param.add(param);
	}
	private void add(Tile tile) {
		if(this.param == null) {
			this.param = new ArrayList();
		}
		this.param.add(tile);
	}
	protected void add(Encrypt encrypt) {
		if(this.encrypts == null) {
			this.encrypts = new ArrayList<Encrypt>();
		}
		this.encrypts.add(encrypt);
	}
	protected boolean valid(Record params) {
		if(this.valid == null) {
			this.valid = true;
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, params)) {
				if(!AuthUtility.auth(tabAuthInfo, params)) {
					this.valid = false;
				}
			}
		}
		return this.valid.booleanValue();
	}
	private int getParamSize(Record record) {
		return Tile.getParamSize(this.param, record);
	}
	private Param getParam(int index, Record record) {
		return Tile.getParam(this.param, index, record);
	}
	protected static String nodeName() {
		return Auth.nodeName;
	}
	protected static Auth load(Node element) {
		Auth auth = new Auth();
		if(element != null) {
			auth.loadAttr(element);
			auth.loadElement(element);
			return auth;
		}
		return null;
	}
	private void loads(Node element, String parentNodeName) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					load(node, parentNodeName);
				}
			}
		}
	}
	private void load(Node node, String parentNodeName) {
		if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
			this.setSql(node);
		} else if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(Param.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "tile")) {
			this.add(Tile.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
			this.add(Encrypt.load(node));
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
		}
	}
	protected void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "params")) {
							this.loads(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypts")) {
							this.loads(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "envelop")) {
							this.loadElement(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
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
						if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "check")) {
							this.setCheck(node.getNodeValue());
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
	protected void element(XmlElement element) {
		element.setAttribute("check", this.getCheck());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("cond", this.getCond());
		element.appendChild(this.getSql());
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				if(this.param.get(i) instanceof Param) {
					child.appendChild(((Param)this.param.get(i)).element());
				} else {
					child.appendChild(((Tile)this.param.get(i)).element());
				}
			}
		}
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		this.element(element);
		return element;
	}
	protected boolean check(Record params, ConnectionFactory connectionFactory) throws NoSuchProviderException, SQLException {
		if(!this.valid(params)) {
			return true;
		}
		boolean result = false;
		if(this.sql == null) {
			result = true;
		} else {
			String check = "${result} exists";
			if(STR.valid(this.getCheck())) {
				check = this.getCheck();
			}
			LOG.finer("check : " + check);
			if(STR.compareIgnoreCase(check, "exists")) {
				check = "${result} exists";
			} else if(STR.compare(check, "0")) {
				check = "${result} = '0'";
			} else if(STR.compare(check, ">0")) {
				check = "${result} > '0'";
			} else if(STR.compare(check, "<0")) {
			} else if(check != null && check.equals("<0")) {
				check = "${result} < '0'";
			}
			super.setConnectionFactory(connectionFactory);
			Buffer sql = super.parseSQL(this.sql, params);
			Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Record record = new Record();
			try {
				List<SQLParameter> parameters = new ArrayList();
				if(STR.valid(this.param)) {
					for(int i = 0; i < this.getParamSize(params); i++) {
						Param p = this.getParam(i, params);
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
				if(rs.next()) {
					record.put(Record.key(Record.PREFIX_TYPE_NONE, "result"), rs.getString(1));
				}
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
				result = AuthUtility.auth(check, record);
			} catch (SQLException | NoSuchProviderException e) {
				LOG.severe(e);
				throw e;
			} finally {
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
			}
		}
		return result;
	}
}
