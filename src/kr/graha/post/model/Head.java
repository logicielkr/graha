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
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.element.XmlElement;

/**
 * querys/query/header/head
 * querys/header/head
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Head {
	protected static final int HEAD_TYPE_HEAD = 1;
	protected static final int HEAD_TYPE_TOP = 2;
	protected static final int HEAD_TYPE_BOTTOM = 3;
	
	protected static final int HEAD_POSITION_NONE = 10;
	
	protected static final int HEAD_POSITION_TOP = 11;	// top
	protected static final int HEAD_POSITION_UNDER_THE_TITLE = 12;	// title
	protected static final int HEAD_POSITION_ABOVE_THE_MULTITAB = 13;	//multitab
	protected static final int HEAD_POSITION_ABOVE_THE_TABLE = 14;	//above
	
	protected static final int HEAD_POSITION_UNDER_THE_TABLE = 15;	//under
	protected static final int HEAD_POSITION_BOTTOM = 16; //bottom

	private Head() {
	}
	
	private String name = null;
	private String override = null;
	private String cond = null;
	private String textContent = null;
	private Integer position = null;
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	protected String getOverride() {
		return this.override;
	}
	private void setOverride(String override) {
		this.override = override;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	protected int getPosition(int headType) {
		if(this.position == null) {
			if(headType == Head.HEAD_TYPE_TOP) {
				return Head.HEAD_POSITION_TOP;
			} else if(headType == Head.HEAD_TYPE_BOTTOM) {
				return Head.HEAD_POSITION_BOTTOM;
			} else if(headType == Head.HEAD_TYPE_HEAD) {
				return Head.HEAD_POSITION_NONE;
			}
			return -1;
		}
		return this.position.intValue();
	}
	private String getPosition() {
		if(this.position == null) {
			return null;
		}
		if(this.position == Head.HEAD_POSITION_TOP) {
			return "top";
		}
		if(this.position == Head.HEAD_POSITION_UNDER_THE_TITLE) {
			return "title";
		}
		if(this.position == Head.HEAD_POSITION_ABOVE_THE_MULTITAB) {
			return "multitab";
		}
		if(this.position == Head.HEAD_POSITION_ABOVE_THE_TABLE) {
			return "above";
		}
		if(this.position == Head.HEAD_POSITION_UNDER_THE_TABLE) {
			return "under";
		}
		if(this.position == Head.HEAD_POSITION_BOTTOM) {
			return "bottom";
		}
		return null;
	}
	private void setPosition(String position) {
		if(STR.compareIgnoreCase(position, "top")) {
			this.position = Head.HEAD_POSITION_TOP;
		} else if(STR.compareIgnoreCase(position, "title")) {
			this.position = Head.HEAD_POSITION_UNDER_THE_TITLE;
		} else if(STR.compareIgnoreCase(position, "multitab")) {
			this.position = Head.HEAD_POSITION_ABOVE_THE_MULTITAB;
		} else if(STR.compareIgnoreCase(position, "above")) {
			this.position = Head.HEAD_POSITION_ABOVE_THE_TABLE;
		} else if(STR.compareIgnoreCase(position, "under")) {
			this.position = Head.HEAD_POSITION_UNDER_THE_TABLE;
		} else if(STR.compareIgnoreCase(position, "bottom")) {
			this.position = Head.HEAD_POSITION_BOTTOM;
		} else {
			LOG.warning("invalid position(" + position + ")");
		}
	}
	public String getTextContent() {
		return this.textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	protected static String nodeName(int headType) {
		if(headType == Head.HEAD_TYPE_HEAD) {
			return "head";
		} else if(headType == Head.HEAD_TYPE_TOP) {
			return "top";
		} else if(headType == Head.HEAD_TYPE_BOTTOM) {
			return "bottom";
		}
		return "unknown";
	}
	protected static Head load(Node element) {
		Head head = new Head();
		if(element != null) {
			head.loadAttr(element);
			head.setTextContent(element.getTextContent());
			return head;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "override")) {
							this.setOverride(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "position")) {
							this.setPosition(node.getNodeValue());
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
	protected XmlElement element(int headType) {
		XmlElement element = new XmlElement(Head.nodeName(headType));
		element.setAttribute("name", this.getName());
		element.setAttribute("override", this.getOverride());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("position", this.getPosition());
		element.setTextContent(this.getTextContent());
		return element;
	}
	protected Buffer toXSL(Record param, boolean rdf) {
		int indent = 0;
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
		Buffer xsl = new Buffer();
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
		}
		xsl.appendL(indent + 1, this.getTextContent());
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
}
