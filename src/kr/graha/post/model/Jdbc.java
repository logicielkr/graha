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
import java.sql.Connection;
import kr.graha.post.model.utility.TextParser;
import kr.graha.helper.DB;

/**
 * querys/query/header/jdbc
 * querys/header/jdbc
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Jdbc {
	private static final String nodeName = "jdbc";
	private Jdbc() {
	}
	
	private String driverClassName = null;
	private String url = null;
	private String username = null;
	private String password = null;
	private String sqlListTemplate = null;
	private String sqlCntTemplate = null;
	private String sqlSequenceTemplate = null;
	private String protectedUrl = null;
	private String getDriverClassName() {
		return this.driverClassName;
	}
	private void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	private String getUrl() {
		return this.url;
	}
	private void setUrl(String url) {
		this.url = url;
	}
	private String getUsername() {
		return this.username;
	}
	private void setUsername(String username) {
		this.username = username;
	}
	private String getPassword() {
		return this.password;
	}
	private void setPassword(String password) {
		this.password = password;
	}
	public String getSqlListTemplate() {
		return this.sqlListTemplate;
	}
	private void setSqlListTemplate(String sqlListTemplate) {
		this.sqlListTemplate = sqlListTemplate;
	}
	public String getSqlCntTemplate() {
		return this.sqlCntTemplate;
	}
	private void setSqlCntTemplate(String sqlCntTemplate) {
		this.sqlCntTemplate = sqlCntTemplate;
	}
	public String getSqlSequenceTemplate() {
		return this.sqlSequenceTemplate;
	}
	private void setSqlSequenceTemplate(String sqlSequenceTemplate) {
		this.sqlSequenceTemplate = sqlSequenceTemplate;
	}
	public String getProtectedUrl() {
		return this.protectedUrl;
	}
	private void setProtectedUrl(String protectedUrl) {
		this.protectedUrl = protectedUrl;
	}
	protected static String nodeName() {
		return Jdbc.nodeName;
	}
	protected static Jdbc load(Node element) {
		Jdbc jdbc = new Jdbc();
		if(element != null) {
			jdbc.loadAttr(element);
			return jdbc;
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
						if(STR.compareIgnoreCase(node.getNodeName(), "driverClassName")) {
							this.setDriverClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "url")) {
							this.setUrl(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "username")) {
							this.setUsername(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "password")) {
							this.setPassword(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_list_template")) {
							this.setSqlListTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_cnt_template")) {
							this.setSqlCntTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_sequence_template")) {
							this.setSqlSequenceTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "protectedUrl")) {
							this.setProtectedUrl(node.getNodeValue());
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
		XmlElement element = new XmlElement(Jdbc.nodeName());
		element.setAttribute("driverClassName", this.getDriverClassName());
		element.setAttribute("url", this.getUrl());
		element.setAttribute("username", this.getUsername());
		element.setAttribute("password", this.getPassword());
		element.setAttribute("sql_list_template", this.getSqlListTemplate());
		element.setAttribute("sql_cnt_template", this.getSqlCntTemplate());
		element.setAttribute("sql_sequence_template", this.getSqlSequenceTemplate());
		element.setAttribute("protectedUrl", this.getProtectedUrl());
		return element;
	}
	public Connection getConnection(Record params) {
		if(STR.valid(this.getDriverClassName())) {
			String url = this.getUrl();
			if(STR.valid(this.getProtectedUrl())) {
				url = TextParser.parse(this.getProtectedUrl(), params);
			}
			if(STR.valid(url)) {
				return DB.getConnection(
					this.getDriverClassName(),
					url,
					this.getUsername(),
					this.getPassword()
				);
			}
		}
		return null;
	}
}
