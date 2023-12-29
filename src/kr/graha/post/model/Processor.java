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

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.interfaces.Encryptor;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.model.utility.SQLInfo;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.xml.GDocument;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Graha(그라하) processor 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Processor extends SQLExecutor {
	private static final String nodeName = "processor";
	private Processor() {
	}
	
	private String before = null;
	private String after = null;
	private String method = null;
	private String cond = null;
	private String type = null;
	private String className = null;
	private String encrypt = null;
	private Node sql = null;
	private List<Param> param = null;
	private List<Encrypt> encrypts = null;
	private String getBefore() {
		return this.before;
	}
	private void setBefore(String before) {
		this.before = before;
	}
	private String getAfter() {
		return this.after;
	}
	private void setAfter(String after) {
		this.after = after;
	}
	private String getMethod() {
		return this.method;
	}
	private void setMethod(String method) {
		this.method = method;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type = type;
	}
	private String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className = className;
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
	protected static String nodeName() {
		return Processor.nodeName;
	}
	protected static Processor load(Element element) {
		Processor processor = new Processor();
		if(element != null) {
			processor.loadAttr(element);
			processor.loadElement(element);
			return processor;
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
						if(STR.compareIgnoreCase(node.getNodeName(), "before")) {
							this.setBefore(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "after")) {
							this.setAfter(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "method")) {
							this.setMethod(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
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
		element.setAttribute("before", this.getBefore());
			element.setAttribute("after", this.getAfter());
			element.setAttribute("method", this.getMethod());
			element.setAttribute("cond", this.getCond());
			element.setAttribute("type", this.getType());
			element.setAttribute("class", this.getClassName());
			element.setAttribute("encrypt", this.getEncrypt());
			element.appendChild(this.getSql());

		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((Param)this.param.get(i)).element());
			}
			element.appendChild(child);
		}
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		return element;
	}
	protected void execute(
		GDocument document,
		Record params,
		HttpServletRequest request,
		HttpServletResponse response,
		ConnectionFactory connectionFactory,
		boolean before
	) throws NoSuchProviderException, SQLException {
		if(before) {
			if(!STR.trueValue(this.getBefore())) {
				return;
			}
		} else {
			if(!STR.trueValue(this.getAfter())) {
				return;
			}
		}
		if(STR.valid(this.getMethod())) {
			if(!STR.compareIgnoreCase(params.getString(Record.key(Record.PREFIX_TYPE_HEADER, "method")), this.getMethod())) {
				return;
			}
		}
		if(STR.valid(this.getCond()) && !AuthUtility.auth(this.getCond(), params)) {
			return;
		}
		if(STR.compareIgnoreCase(this.getType(), "native")) {
			try {
				kr.graha.post.interfaces.Processor processor = (kr.graha.post.interfaces.Processor)Class.forName(this.getClassName()).getConstructor().newInstance();
				processor.execute(request, response, params, connectionFactory.getConnection());
			} catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				LOG.severe(e);
			}
		} else {
			super.setConnectionFactory(connectionFactory);
			Buffer sql = super.parseSQL(this.sql, params);
			Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
			List<SQLParameter> parameters = new ArrayList();
			if(STR.valid(this.param)) {
				for(int x = 0; x < this.param.size(); x++) {
					Param p = (Param)this.param.get(x);
					if(STR.startsWithIgnoreCase(p.getValue(), "sequence.")) {
						SQLParameter parameter =  new SQLParameter(SQLExecutor.getNextSequenceIntegerValue(p.getValue(), super.getConnectionFactory()), p.getDataType());
						parameters.add(parameter);
					} else {
						SQLParameter parameter =  p.getValue(
							params,
							encryptor
						);
						if(parameter != null) {
							parameters.add(parameter);
						}
					}
				}
			}
			if(STR.compareIgnoreCase(this.getType(), "query")) {
				super.executeUpdate(new SQLInfo(sql, parameters));
			} else if(STR.compareIgnoreCase(this.getType(), "plsql")) {
				super.executeUpdate(new SQLInfo(sql, SQLInfo.TYPE_CSTMT, parameters));
			}
		}
	}
}
