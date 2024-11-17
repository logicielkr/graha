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


package kr.graha.post.element;

import java.util.List;
import java.util.ArrayList;
import kr.graha.helper.STR;
import kr.graha.post.lib.Buffer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * XslElement
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class XmlElement {
	private String prefixName;
	private String tagName;
	private List childNodes = null;
	private List<XmlAttr> attrs = null;
	public XmlElement(String prefixName, String tagName) {
		this.prefixName = prefixName;
		this.tagName = tagName;
	}
	public XmlElement(String tagName) {
		this.prefixName = null;
		this.tagName = tagName;
	}
	public void clear() {
		if(this.childNodes != null) {
			this.childNodes.clear();
		}
		if(this.attrs != null) {
			this.attrs.clear();
		}
		this.attrs = null;
		this.childNodes = null;
		this.prefixName = null;
		this.tagName = null;
	}
	public void add(XmlElement element) {
		if(element != null) {
			if(this.childNodes == null) {
				this.childNodes = new ArrayList();
			}
			this.childNodes.add(element);
		}
	}
	public void add(XmlElement... elements) {
		if(elements != null) {
			for(int i = 0; i < elements.length; i++) {
				this.add(elements[i]);
			}
		}
	}
	public void add(Node... elements) {
		if(elements != null) {
			for(int i = 0; i < elements.length; i++) {
				this.add(elements[i]);
			}
		}
	}
	public void add(Node element) {
		if(element != null) {
			if(element.getNodeType() == Node.ELEMENT_NODE) {
				if(this.childNodes == null) {
					this.childNodes = new ArrayList();
				}
				this.childNodes.add(element);
			}
		}
	}
	public void add(String element) {
		if(STR.valid(element)) {
			if(this.childNodes == null) {
				this.childNodes = new ArrayList();
			}
			this.childNodes.add(element);
		}
	}
	public void add(String... elements) {
		if(elements != null) {
			for(int i = 0; i < elements.length; i++) {
				this.add(elements[i]);
			}
		}
	}
	public void add(Buffer element) {
		if(element != null && element.valid()) {
			if(this.childNodes == null) {
				this.childNodes = new ArrayList();
			}
			this.childNodes.add(element);
		}
	}
	public void add(Buffer... elements) {
		if(elements != null) {
			for(int i = 0; i < elements.length; i++) {
				this.add(elements[i]);
			}
		}
	}
	public void add(XmlAttr attr) {
		if(attr != null && attr.valid()) {
			if(this.attrs == null) {
				this.attrs = new ArrayList<XmlAttr>();
			}
			this.attrs.add(attr);
		}
	}
	public void add(XmlAttr... attrs) {
		if(attrs != null) {
			for(int i = 0; i < attrs.length; i++) {
				this.add(attrs[i]);
			}
		}
	}
	public void addAttr(String name, Object... values) {
		XmlAttr attr = new XmlAttr(name, values);
		this.add(attr);
	}
	protected void addAttr(String name, boolean inline, Object... values) {
		XmlAttr attr = new XmlAttr(name, inline, values);
		this.add(attr);
	}
	public void setAttribute(String name, String value) {
		XmlAttr attr = new XmlAttr(name, (Object)value);
		this.add(attr);
	}
	public void setTextContent(String textContent) {
		this.add(textContent);
	}
	public void appendChild(XmlElement element) {
		this.add(element);
	}
	public void appendChild(Node element) {
		this.add(element);
	}
	public void appendChild(String element) {
		this.add(element);
	}
	public XmlElement createElement(String tagName) {
		XmlElement element = new XmlElement(tagName);
		this.add(element);
		return element;
	}
	public XmlElement createElement(String tagName, XmlAttr... attrs) {
		XmlElement element = new XmlElement(tagName);
		element.add(attrs);
		this.add(element);
		return element;
	}
	public Buffer println() {
		Buffer buffer = new Buffer();
		this.println(buffer);
		return buffer;
	}
	public Buffer println(int indent) {
		Buffer buffer = new Buffer();
		this.println(buffer, indent);
		return buffer;
	}
	public Buffer print() {
		Buffer buffer = new Buffer();
		this.print(buffer);
		return buffer;
	}
	public Buffer print(int indent) {
		Buffer buffer = new Buffer();
		this.print(buffer, indent);
		return buffer;
	}
	public void println(Buffer buffer) {
		this.println(buffer, 0);
	}
	public void println(Buffer buffer, int indent) {
		this.print(buffer, indent);
		buffer.appendL();
	}
	public void print(Buffer buffer) {
		this.print(buffer, 0);
	}
	public void print(Buffer buffer, int indent) {
		buffer.append(indent, "<");
		if(STR.valid(this.prefixName)) {
			buffer.append(this.prefixName);
			buffer.append(":");
		}
		buffer.append(this.tagName);
		boolean exists = false;
		if(this.attrs != null && this.attrs.size() > 0) {
			for(int i = 0; i < this.attrs.size(); i++) {
				XmlAttr attr = (XmlAttr)this.attrs.get(i);
				if(attr.valid()) {
					if(attr.getInline()) {
						attr.println(buffer);
					} else {
						exists = true;
					}
				}
			}
		}
		if(
			exists ||
			(this.childNodes != null && this.childNodes.size() > 0)
		) {
			buffer.appendL(">");
			if(this.attrs != null && this.attrs.size() > 0) {
				for(int i = 0; i < this.attrs.size(); i++) {
					XmlAttr attr = (XmlAttr)this.attrs.get(i);
					if(attr.valid()) {
						if(!attr.getInline()) {
							attr.println(buffer, indent + 1);
						}
					}
				}
			}
			if(this.childNodes != null && this.childNodes.size() > 0) {
				for(int i = 0; i < this.childNodes.size(); i++) {
					Object childNode = this.childNodes.get(i);
					if(childNode instanceof XmlElement) {
						((XmlElement)childNode).println(buffer, indent + 1);
					} else if(childNode instanceof Buffer) {
						buffer.appendL(indent + 1, (Buffer)childNode);
					} else if(childNode instanceof String) {
						buffer.appendL(indent + 1, (String)childNode);
					}
				}
			}
			buffer.append(indent, "</");
			if(STR.valid(this.prefixName)) {
				buffer.append(this.prefixName);
				buffer.append(":");
			}
			buffer.append(this.tagName);
			buffer.append(">");
		} else {
			buffer.append(" />");
		}
	}
	public Element element(Document document) {
		String nodeName = null;
		if(STR.valid(this.prefixName)) {
			nodeName = this.prefixName + ":" + this.tagName;
		} else {
			nodeName = this.tagName;
		}
		Element element = document.createElement(nodeName);
		if(this.attrs != null && this.attrs.size() > 0) {
			for(int i = 0; i < this.attrs.size(); i++) {
				XmlAttr attr = (XmlAttr)this.attrs.get(i);
				if(attr.valid()) {
					element.setAttribute(attr.getName(), attr.getValue());
				}
			}
		}
		if(this.childNodes != null && this.childNodes.size() > 0) {
			for(int i = 0; i < this.childNodes.size(); i++) {
				Object obj = this.childNodes.get(i);
				if(obj instanceof XmlElement) {
					XmlElement childNode = (XmlElement)obj;
					element.appendChild(childNode.element(document));
				} else if(obj instanceof Node) {
					Node node = (Node)obj;
					Node importedNode = document.importNode(node, true);
					element.appendChild(importedNode);
				} else if(obj instanceof Buffer) {
					Buffer buffer = (Buffer)obj;
					element.setTextContent(buffer.toString());
				} else if(obj instanceof String) {
					element.setTextContent((String)obj);
				}
			}
		}
		return element;
	}
}
