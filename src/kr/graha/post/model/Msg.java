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
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;

/**
 * Graha(그라하) Msg 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Msg {
	private static final String nodeName = "msg";
	private Msg() {
	}
	private String value = null;
	private String textContent = null;
	private String getValue() {
		return this.value;
	}
	private void setValue(String value) {
		this.value = value;
	}
	private String getTextContent() {
		return this.textContent;
	}
	private void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	private static String nodeName() {
		return Msg.nodeName;
	}
	protected static Msg load(Node element) {
		Msg msg = new Msg();
		if(element != null) {
			msg.loadAttr(element);
			msg.setTextContent(element.getTextContent());
			return msg;
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
						if(STR.compareIgnoreCase(node.getNodeName(), "value")) {
							this.setValue(node.getNodeValue());
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
		element.setAttribute("value", this.getValue());
		element.setTextContent(this.getTextContent());
		return element;
	}
	protected Buffer toXSL(int indent) {
		Buffer xsl = new Buffer();
		if(STR.valid(this.getValue())) {
			xsl.appendL(indent, "<div class=\"msg\">" + this.getValue() + "</div>");
		}
		if(STR.valid(this.getTextContent())) {
			xsl.appendL(indent, "<div class=\"msg\">" + this.getTextContent() + "</div>");
		}
		return xsl;
	}
}
