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

import kr.graha.helper.STR;
import kr.graha.post.lib.Buffer;

/**
 * XmlAttr
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class XmlAttr {
	private String prefixName;
	private String name;
	private Object[] values;
	private boolean inline;
	public XmlAttr(String name, Object... values) {
		this.prefixName = null;
		this.name = name;
		this.values = values;
		this.inline = true;
	}
	protected XmlAttr(String name, boolean inline, Object... values) {
		this.prefixName = null;
		this.name = name;
		this.values = values;
		this.inline = inline;
	}
	protected XmlAttr(String prefixName, String name, boolean inline, Object... values) {
		this.prefixName = prefixName;
		this.name = name;
		this.values = values;
		this.inline = inline;
	}
	public void clear() {
		if(this.values != null) {
			for(int i = 0; i < this.values.length; i++) {
				if(this.values[i] instanceof XmlElement) {
					((XmlElement)this.values[i]).clear();
				} else {
					this.values[i] = null;
				}
			}
		}
		this.values = null;
		this.prefixName = null;
		this.name = null;
	}
	protected boolean valid() {
		if(this.values != null) {
			for(int i = 0; i < this.values.length; i++) {
				if(this.values[i] instanceof XmlElement) {
					return true;
				} else if(this.values[i] instanceof String) {
					if(this.values[i] != null) {
//					if(STR.valid((String)this.values[i])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static XmlAttr child(String name, Object... values) {
		XmlAttr attr = new XmlAttr(name, false, values);
		return attr;
	}
	public static XmlAttr attr(String name, Object... values) {
		XmlAttr attr = new XmlAttr(name, true, values);
		return attr;
	}
	public static XmlAttr inline(String name, Object... values) {
		XmlAttr attr = new XmlAttr(name, true, values);
		return attr;
	}
	public static XmlAttr child(String prefixName, String name, Object... values) {
		XmlAttr attr = new XmlAttr(prefixName, name, false, values);
		return attr;
	}
	public static XmlAttr attr(String prefixName, String name, Object... values) {
		XmlAttr attr = new XmlAttr(prefixName, name, true, values);
		return attr;
	}
	public static XmlAttr inline(String prefixName, String name, Object... values) {
		XmlAttr attr = new XmlAttr(prefixName, name, true, values);
		return attr;
	}
	protected boolean getInline() {
		return this.inline;
	}
	public void println(Buffer buffer, int indent) {
		this.print(buffer, indent);
	}
	public void println(Buffer buffer) {
		this.println(buffer, 0);
	}
	public void print(Buffer buffer) {
		this.print(buffer, 0);
	}
	public void print(Buffer buffer, int indent) {
		if(this.valid()) {
			if(this.inline) {
				buffer.append(" ");
				if(STR.valid(this.prefixName)) {
					buffer.append(this.prefixName);
					buffer.append(":");
				}
				buffer.append(this.name);
				buffer.append("=");
				buffer.append("\"");
				int index = 0;
				for(int i = 0; i < this.values.length; i++) {
					Object value = this.values[i];
					if(index > 0 && STR.compareIgnoreCase(this.name, "class")) {
						buffer.append(" ");
					}
					if(value instanceof String) {
						if(STR.valid((String)value)) {
							buffer.append(value);
							index++;
						}
					}
				}
				buffer.append("\"");
			} else {
				buffer.append(indent, "<xsl:attribute name=\"");
				if(STR.valid(this.prefixName)) {
					buffer.append(this.prefixName);
					buffer.append(":");
				}
				buffer.append(this.name);
				buffer.append("\">");
				int index = 0;
				for(int i = 0; i < this.values.length; i++) {
					Object value = this.values[i];
					if(i > 0 && STR.compareIgnoreCase(this.name, "class")) {
						buffer.append(" ");
					}
					if(value instanceof XmlElement) {
						((XmlElement)value).println(buffer, indent + 1);
						index++;
					} else if(value instanceof String) {
						if(STR.valid((String)value)) {
							buffer.appendL(indent + 1, value);
							index++;
						}
					}
				}
				buffer.append("</xsl:attribute>");
			}
		}
	}
	protected String getValue() {
		if(this.valid()) {
			Buffer buffer = new Buffer();
			int index = 0;
			for(int i = 0; i < this.values.length; i++) {
				Object value = this.values[i];
				if(index > 0 && STR.compareIgnoreCase(this.name, "class")) {
					buffer.append(" ");
				}
				if(value instanceof String) {
					if(STR.valid((String)value)) {
						buffer.append(value);
						index++;
					}
				}
			}
			return buffer.toString();
		}
		return null;
	}
	protected String getName() {
		if(STR.valid(this.prefixName)) {
			return this.prefixName + ":" + this.name;
		} else {
			return this.name;
		}
	}
}
