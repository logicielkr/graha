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

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import kr.graha.post.element.XmlElement;
import java.sql.Connection;
import kr.graha.helper.DB;

/**
 * querys/query/header/jndi
 * querys/header/jndi
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Jndi {
	private static final String nodeName = "jndi";
	
	private String name = null;
	private String sqlListTemplate = null;
	private String sqlCntTemplate = null;
	private String sqlSequenceTemplate = null;
	private Jndi() {
	}
	public String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
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
	protected static String nodeName() {
		return Jndi.nodeName;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_list_template")) {
							this.setSqlListTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_cnt_template")) {
							this.setSqlCntTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_sequence_template")) {
							this.setSqlSequenceTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xml:base")) {
						} else {
							LOG.warning("invalid attrName(" + node.getNodeName() + ")");
						}
					}
				}
			}
		}
	}
	protected static Jndi load(Node element) {
		Jndi jndi = new Jndi();
		jndi.loadAttr(element);
		return jndi;
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(Jndi.nodeName());
		element.setAttribute("sql_list_template", this.getSqlListTemplate());
		element.setAttribute("sql_cnt_template", this.getSqlCntTemplate());
		element.setAttribute("sql_sequence_template", this.getSqlSequenceTemplate());
		return element;
	}
	public Connection getConnection() {
		if(STR.valid(this.getName())) {
			return DB.getConnection(this.getName());
		}
		return null;
	}
}
