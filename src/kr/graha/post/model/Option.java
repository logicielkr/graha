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

import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;

/**
 * Graha(그라하) option 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Option {
	private static final String nodeName = "option";
	private Option() {
	}
	
	private String value = null;
	private String label = null;
	protected String getValue() {
		if(this.value == null) {
			return "";
		}
		return this.value;
	}
	private void setValue(String value) {
		this.value = value;
	}
	protected String getLabel() {
		if(this.label == null) {
			return "";
		}
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	protected static String nodeName() {
		return Option.nodeName;
	}
	protected static Option load(Node element) {
		Option option = new Option();
		if(element != null) {
			option.loadAttr(element);
			return option;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
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
		XmlElement element = new XmlElement(Option.nodeName());
		element.setAttribute("value", this.getValue());
		element.setAttribute("label", this.getLabel());
		return element;
	}
}
