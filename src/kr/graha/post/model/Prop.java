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
import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.model.utility.AuthUtility;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import kr.graha.post.model.utility.TextParser;
import kr.graha.helper.DB;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import kr.graha.post.interfaces.Encryptor;
import kr.graha.helper.LOG;
import kr.graha.post.model.utility.SQLParameter;
import java.sql.ResultSetMetaData;
import kr.graha.post.lib.GrahaParsingException;

/**
 * querys/query/header/prop
 * querys/header/prop
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Prop extends SQLExecutor {
	private static final String nodeName = "prop";
	
	public static final int Before_Connection = 1;
	public static final int Before_Before_Processor = 2;
	public static final int After_Before_Processor = 3;
	public static final int Before_After_Processor = 4;
	public static final int After_After_Processor = 5;
	
	private String name = null;
	private String value = null;
	private String cond = null;
	private String encrypt = null;
	private String isPublic = null;
	private String time = null;
	private String override = null;
	private Node sql = null;
	private List param = null;
	private List<Encrypt> encrypts = null;
	private Prop() {
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
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getEncrypt() {
		return this.encrypt;
	}
	private void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	private String getIsPublic() {
		return this.isPublic;
	}
	private void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	private String getTime() {
		return this.time;
	}
	private void setTime(String time) {
		this.time = time;
	}
	protected String getOverride() {
		return this.override;
	}
	private void setOverride(String override) {
		this.override = override;
	}
	private Node getSql() {
		return this.sql;
	}
	private void setSql(Node sql) {
		this.sql = sql;
	}
	private void add(Param param) {
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
	private void add(Encrypt encrypt) {
		if(this.encrypts == null) {
			this.encrypts = new ArrayList<Encrypt>();
		}
		this.encrypts.add(encrypt);
	}
	private int getParamSize(Record record) {
		return Tile.getParamSize(this.param, record);
	}
	private Param getParam(int index, Record record) {
		return Tile.getParam(this.param, index, record);
	}
	protected static String nodeName() {
		return Prop.nodeName;
	}
	protected static Prop load(Element element) {
		Prop prop = new Prop();
		if(element != null) {
			prop.loadAttr(element);
			prop.loadElement(element);
			return prop;
		}
		return null;
	}
	private void loadAttr(Element element) {
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "public")) {
							this.setIsPublic(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "time")) {
							this.setTime(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "override")) {
							this.setOverride(node.getNodeValue());
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
		} else if(STR.compareIgnoreCase(node.getNodeName(), "tile")) {
			this.add(Tile.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
			this.add(Encrypt.load(node));
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "envelop")) {
							this.loadElement(node);
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
	protected XmlElement element() {
		XmlElement element = new XmlElement(Prop.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("value", this.getValue());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("public", this.getIsPublic());
		element.setAttribute("time", this.getTime());
		element.setAttribute("override", this.getOverride());
		element.appendChild(this.getSql());
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
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		return element;
	}
	private int getMode() {
		if(this.getTime() == null) {
			return 0;
		} else if(STR.compareIgnoreCase(this.getTime(), "first")) {
			return Prop.Before_Connection;
		} else if(STR.compareIgnoreCase(this.getTime(), "before")) {
			return Prop.Before_Before_Processor;
		} else if(STR.compareIgnoreCase(this.getTime(), "after-before")) {
			return Prop.After_Before_Processor;
		} else if(STR.compareIgnoreCase(this.getTime(), "before-after")) {
			return Prop.Before_After_Processor;
		} else if(STR.compareIgnoreCase(this.getTime(), "after")) {
			return Prop.After_After_Processor;
		} else {
			return 0;
		}
	}
	protected void execute(Record params, int time, ConnectionFactory connectionFactory) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.getCond()) && !AuthUtility.auth(this.getCond(), params)) {
			return;
		}
		if(!STR.valid(this.getTime()) && time >= Prop.After_Before_Processor) {
			return;
		}
		if(STR.valid(this.getTime())) {
			int mode = this.getMode();
			if(mode == 0 && time >= Prop.After_Before_Processor) {
				return;
			}
			if(mode > 0 && time != mode) {
				return;
			}
		}
		if(STR.valid(this.getValue())) {
			if(!STR.valid(this.getTime()) && time >= Prop.Before_Before_Processor) {
				return;
			}
			String value = TextParser.parse(this.getValue(), params);
			if(STR.valid(value)) {
				params.puts(Record.key(Record.PREFIX_TYPE_PROP, this.getName()), value);
				if(STR.trueValue(this.getIsPublic())) {
					params.puts(Record.key(Record.PREFIX_TYPE_PROP, this.getName(), "public"), "true");
				}
			}
		} else if(time > Prop.Before_Connection) {
			super.setConnectionFactory(connectionFactory);
			Buffer sql = super.parseSQL(this.sql, params);
			if(sql == null) {
				LOG.severe("prop(" + this.getName() + ") must be defined value or sql");
				throw new GrahaParsingException("prop(" + this.getName() + ") must be defined value or sql");
			}
			Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				List<SQLParameter> parameters = new ArrayList();
				if(STR.valid(this.param)) {
					for(int i = 0; i < this.getParamSize(params); i++) {
						Param p = (Param)this.getParam(i, params);
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
				ResultSetMetaData rsmd = rs.getMetaData();
				if(rs.next()) {
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						if(rs.getString(i) != null) {
							params.puts(Record.key(Record.PREFIX_TYPE_PROP, this.getName() + "." + rsmd.getColumnName(i)), rs.getString(i));
							if(STR.trueValue(this.getIsPublic())) {
								params.puts(Record.key(Record.PREFIX_TYPE_PROP, this.getName() + "." + rsmd.getColumnName(i), "public"), true);
							}
						}
					}
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
		}
	}
}
