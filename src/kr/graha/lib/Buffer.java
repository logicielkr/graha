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


package kr.graha.lib;

import java.util.logging.Logger;

/**
 * Graha(그라하)에서 StringBuffer 객체 대신에 사용 
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Buffer {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private StringBuffer sb;
	public Buffer() {
		sb = new StringBuffer();
		LogHelper.setLogLevel(logger);
	}
	public void append(String text) {
		this.sb.append(text);
	}
	public void append(int text) {
		this.sb.append(text);
	}
	public void append(Object text) {
		this.sb.append(text);
	}
	public void append(Buffer text) {
		this.sb.append(text.toStringBuffer());
	}
	public void appendL(String text) {
		this.sb.append(text);
		this.sb.append("\n");
	}
	public void appendL(int text) {
		this.sb.append(text);
		this.sb.append("\n");
	}
	public void appendL(Object text) {
		this.sb.append(text);
		this.sb.append("\n");
	}
	public void appendL(Buffer text) {
		this.sb.append(text.toStringBuffer());
		this.sb.append("\n");
	}
	public int length() {
		return this.sb.length();
	}
	public String toString() {
		return this.sb.toString();
	}
	public StringBuffer toStringBuffer() {
		return this.sb;
	}
	public byte[] toByte() {
		return this.sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
	}
	public void clear() {
		this.sb.setLength(0);
		this.sb = null;
	}
	public void init() {
		if(this.sb == null) {
			this.sb = new StringBuffer();
		} else {
			this.sb.setLength(0);
		}
	}
}
