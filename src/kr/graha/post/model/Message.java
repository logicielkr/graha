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
import kr.graha.post.element.XmlElement;
import kr.graha.post.lib.Record;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * querys/query/header/message
 * querys/header/message
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Message {
	private static final String nodeName = "message";
	private Message() {
	}
	private String name = null;
	private String code = null;
	private String ref = null;
	private String isPublic = null;
	private String textContent = null;
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getCode() {
		return this.code;
	}
	private void setCode(String code) {
		this.code = code;
	}
	private String getRef() {
		return this.ref;
	}
	private void setRef(String ref) {
		this.ref = ref;
	}
	private String getIsPublic() {
		return this.isPublic;
	}
	private void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	public String getTextContent() {
		return this.textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	protected static String nodeName() {
		return Message.nodeName;
	}
	protected static Message load(Node element) {
		Message message = new Message();
		if(element != null) {
			message.loadAttr(element);
			message.setTextContent(element.getTextContent());
			return message;
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
						} else if(STR.compareIgnoreCase(node.getNodeName(), "code")) {
							this.setCode(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "ref")) {
							this.setRef(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "public")) {
							this.setIsPublic(node.getNodeValue());
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
		XmlElement element = new XmlElement(Message.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("code", this.getCode());
		element.setAttribute("ref", this.getRef());
		element.setAttribute("public", this.getIsPublic());
		element.setTextContent(this.getTextContent());
		return element;
	}
	protected void execute(Record params) {
		String tmp = null;
		String label = null;
		if(STR.valid(this.getRef()) && params.hasKey(Record.key(Record.PREFIX_TYPE_MESSAGE, this.getRef()))) {
			tmp = params.getString(Record.key(Record.PREFIX_TYPE_MESSAGE, this.getRef()));
			if(tmp != null) {
				if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_MESSAGES_CODE, this.getRef())) &&
					tmp.startsWith("[") &&
					tmp.indexOf("]") > 0
				) {
					if(STR.compareIgnoreCase(this.getCode(), "exclude")) {
						label = tmp.substring(tmp.indexOf("]") + 1);
					} else {
						label = "[" + this.getName() + "]" + tmp.substring(tmp.indexOf("]") + 1);
						params.put(Record.key(Record.PREFIX_TYPE_MESSAGES_CODE, this.getName()), true);
					}
				} else {
					if(STR.compareIgnoreCase(this.getCode(), "include")) {
						label = "[" + this.getName() + "]" + tmp;
						params.put(Record.key(Record.PREFIX_TYPE_MESSAGES_CODE, this.getName()), true);
					} else {
						label = tmp;
					}
				}
			}
		}
		if(label == null) {
			tmp = this.getTextContent();
			label = "";
			if(tmp != null) {
				java.util.StringTokenizer st = new java.util.StringTokenizer(tmp, "\t\n\r ");
				int index = 0;
				while(st.hasMoreTokens()) {
					if(index > 0) {
						label += " ";
					}
					label += st.nextToken();
					index++;
				}
				if(index == 0) {
					label = null;
				} else {
					if(STR.compareIgnoreCase(this.getCode(), "include")) {
						label = "[" + this.getName() + "]" + label;
						params.put(Record.key(Record.PREFIX_TYPE_MESSAGES_CODE, this.getName()), true);
					}
				}
			}
		}
		if(label != null) {
			params.put(Record.key(Record.PREFIX_TYPE_MESSAGE, this.getName()), label);
			if(STR.trueValue(this.getIsPublic())) {
				params.put(Record.key(Record.PREFIX_TYPE_MESSAGE, this.getName(), "public"), true);
			}
		}
	}
}
