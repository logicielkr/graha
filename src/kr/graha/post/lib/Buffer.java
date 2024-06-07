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


package kr.graha.post.lib;

/**
 * Graha(그라하)에서 StringBuffer 객체 대신에 사용 
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Buffer {
//	private StringBuffer sb;
	private StringBuilder sb;
	public Buffer() {
//		sb = new StringBuffer();
		sb = new StringBuilder();
	}
	public void append(String text) {
		if(text != null) {
			this.sb.append(text);
		}
	}
	public void append(int text) {
		this.sb.append(text);
	}
	public void append(Object text) {
		if(text != null) {
			this.sb.append(text);
		}
	}
	public void append(Buffer text) {
		if(text != null) {
//			this.sb.append(text.toStringBuffer());
			this.sb.append(text.toStringBuilder());
		}
	}
	public void appendL(String text) {
		if(text != null) {
			this.sb.append(text);
			this.sb.append("\n");
		}
	}
	public void appendL(int text) {
		this.sb.append(text);
		this.sb.append("\n");
	}
	public void appendL(Object text) {
		if(text != null) {
			this.sb.append(text);
			this.sb.append("\n");
		}
	}
	public void appendL(Buffer text) {
		if(text != null) {
//			this.sb.append(text.toStringBuffer());
			this.sb.append(text.toStringBuilder());
			this.sb.append("\n");
		}
	}
	public void indent(int indent) {
		for(int i = 0; i < indent; i++) {
			this.sb.append("\t");
		}
	}
	public void append(int indent, String text) {
		if(text != null) {
			this.indent(indent);
			this.sb.append(text);
		}
	}
	public void append(int indent, int text) {
		this.indent(indent);
		this.sb.append(text);
	}
	public void append(int indent, Object text) {
		if(text != null) {
			this.indent(indent);
			this.sb.append(text);
		}
	}
	public void append(int indent, Buffer text) {
		if(text != null) {
			this.indent(indent);
//			this.sb.append(text.toStringBuffer());
			this.sb.append(text.toStringBuilder());
		}
	}
	public void appendL(int indent, String text) {
		if(text != null) {
			this.indent(indent);
			this.sb.append(text);
			this.sb.append("\n");
		}
	}
	public void appendL(int indent, int text) {
		this.indent(indent);
		this.sb.append(text);
		this.sb.append("\n");
	}
	public void appendL(int indent, Object text) {
		if(text != null) {
			this.indent(indent);
			this.sb.append(text);
			this.sb.append("\n");
		}
	}
	public void appendL(int indent, Buffer text) {
		if(text != null) {
			this.indent(indent);
//			this.sb.append(text.toStringBuffer());
			this.sb.append(text.toStringBuilder());
			this.sb.append("\n");
		}
	}
	public void appendL() {
		this.sb.append("\n");
	}
	public int length() {
		return this.sb.length();
	}
	public String toString() {
		return this.sb.toString();
	}
//	public StringBuffer toStringBuffer() {
//		return this.sb;
//	}
	public CharSequence toCharSequence() {
		return this.sb;
	}
	private StringBuilder toStringBuilder() {
		return this.sb;
	}
	public byte[] toByte() {
		return this.sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
	}
	public void clear() {
		this.sb.setLength(0);
		this.sb = null;
	}
	public boolean valid() {
		return (this.length() > 0);
	}
	public void init() {
		if(this.sb == null) {
//			this.sb = new StringBuffer();
			this.sb = new StringBuilder();
		} else {
			this.sb.setLength(0);
		}
	}
}
