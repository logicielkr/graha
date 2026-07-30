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

import kr.graha.post.lib.Buffer;
import org.w3c.dom.Node;

/**
 * XslElement
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class XslElement extends XmlElement {
	protected static String XslPrefixName = "xsl";
	public XslElement(String tagName) {
		super(XslElement.XslPrefixName, tagName);
	}
	public XslElement(String tagName, XmlAttr attr) {
		super(XslElement.XslPrefixName, tagName, attr);
	}
	public XslElement(String tagName, XmlAttr... attrs) {
		super(XslElement.XslPrefixName, tagName, attrs);
	}
	public void clear() {
		super.clear();
	}
	public void add(XmlElement element) {
		super.add(element);
	}
	public void add(HTMLElement... elements) {
		super.add(elements);
	}
	public void add(HTMLElement element) {
		super.add(element);
	}
	public void add(XmlElement... elements) {
		super.add(elements);
	}
	public void add(String element) {
		super.add(element);
	}
	public void add(String... elements) {
		super.add(elements);
	}
	public void add(Buffer element) {
		super.add(element);
	}
	public void add(Buffer... elements) {
		super.add(elements);
	}
	public void add(XmlAttr attr) {
		super.add(attr);
	}
	public void add(XmlAttr... attrs) {
		super.add(attrs);
	}
	public void addAttr(String name, Object... values) {
		super.addAttr(name, false, values);
	}
	protected void addAttr(String name, boolean inline, Object... values) {
		super.addAttr(name, inline, values);
	}
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
	}
	public void setTextContent(String textContent) {
		super.setTextContent(textContent);
	}
	public void appendChild(XslElement element) {
		super.appendChild(element);
	}
	public void appendChild(Node element) {
		super.appendChild(element);
	}
	public void appendChild(String element) {
		super.appendChild(element);
	}
	public XslElement createElement(String tagName) {
		return super.createXslElement(tagName);
	}
	public XslElement createElement(String tagName, XmlAttr... attrs) {
		return super.createXslElement(tagName, attrs);
	}
	public HTMLElement createHTMLElement(String tagName) {
		return super.createHTMLElement(tagName);
	}
	public HTMLElement createHTMLElement(String tagName, XmlAttr... attrs) {
		return super.createHTMLElement(tagName, attrs);
	}
	public void println(Buffer buffer) {
		super.println(buffer);
	}
	public void println(Buffer buffer, int indent) {
		super.println(buffer, indent);
	}
	public void print(Buffer buffer) {
		super.print(buffer);
	}
	public void print(Buffer buffer, int indent) {
		super.print(buffer, indent);
	}
}
