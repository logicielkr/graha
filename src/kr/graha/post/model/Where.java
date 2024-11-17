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
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;
import kr.graha.post.lib.Record;

/**
 * Graha(그라하) where 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Where {
	private static final String nodeName = "where";
	private Where() {
	}
	
	private String method = null;
	private Node sql = null;
	private List param = null;
	protected String getMethod() {
		return this.method;
	}
	private void setMethod(String method) {
		this.method = method;
	}
	protected Node getSql() {
		return this.sql;
	}
	private void setSql(Node sql) {
		this.sql = sql;
	}
	private List getParam() {
		return this.param;
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
	protected int getParamSize(Record record) {
		return Tile.getParamSize(this.getParam(), record);
	}
	protected Param getParam(int index, Record record) {
		return Tile.getParam(this.getParam(), index, record);
	}
	protected static String nodeName() {
		return Where.nodeName;
	}
	protected static Where load(Element element) {
		Where where = new Where();
		if(element != null) {
			where.loadAttr(element);
			where.loadElement(element);
			return where;
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
		} else if(STR.compareIgnoreCase(node.getNodeName(), "tile")) {
			this.add(Tile.load(node));
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
						if(STR.compareIgnoreCase(node.getNodeName(), "method")) {
							this.setMethod(node.getNodeValue());
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
		XmlElement element = new XmlElement(Where.nodeName());
		element.setAttribute("method", this.getMethod());
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
		return element;
	}
}
