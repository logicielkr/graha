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
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * querys/query/header/script
 * querys/header/script
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Script {
	private static final String nodeName = "script";
	private Script() {
	}
	
	private String name = null;
	private String src = null;
	private String override = null;
	private String charset = null;
	private String cond = null;
	private String textContent = null;
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getSrc() {
		return this.src;
	}
	private void setSrc(String src) {
		this.src = src;
	}
	protected String getOverride() {
		return this.override;
	}
	private void setOverride(String override) {
		this.override = override;
	}
	private String getCharset() {
		return this.charset;
	}
	private void setCharset(String charset) {
		this.charset = charset;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	public String getTextContent() {
		return this.textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	protected static String nodeName() {
		return Script.nodeName;
	}
	protected static Script load(Node element) {
		Script script = new Script();
		if(element != null) {
			script.loadAttr(element);
			script.setTextContent(element.getTextContent());
			return script;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "src")) {
							this.setSrc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "override")) {
							this.setOverride(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "charset")) {
							this.setCharset(node.getNodeValue());
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
		element.setAttribute("src", this.getSrc());
		element.setAttribute("override", this.getOverride());
		element.setAttribute("charset", this.getCharset());
		element.setAttribute("cond", this.getCond());
		element.setTextContent(this.getTextContent());
		return element;
	}
	protected Buffer toXSL(Record param, boolean rdf) {
		int indent = 0;
		int internalIndent = indent;
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
			if(!AuthUtility.auth(authInfo, param)) {
				return null;
			}
		}
		Buffer xsl = new Buffer();
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:if test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
			internalIndent++;
		}
		if(STR.valid(this.getSrc())) {
			if(STR.valid(this.getCharset())) {
				xsl.appendL(internalIndent, "<script src=\"" + this.getSrc() + "\" charset=\"" + this.getCharset() + "\" />");
			} else {
				xsl.appendL(internalIndent, "<script src=\"" + this.getSrc() + "\" />");
			}
		} else if(STR.valid(this.getTextContent())){
			xsl.appendL(internalIndent, "<script>");
			xsl.appendL(this.getTextContent().trim());
			xsl.appendL(internalIndent, "</script>");
		}
		if(authInfo != null) {
			xsl.appendL(indent, "</xsl:if>");
		}
		return xsl;
	}
}
