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

/**
 * HtmlAttr
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class HtmlAttr extends XmlAttr {
	public HtmlAttr(String name, Object... values) {
		super(name, false, values);
	}
	private HtmlAttr(String name, boolean inline, Object... values) {
		super(name, inline, values);
	}
	public void clear() {
		super.clear();
	}
	protected boolean valid() {
		return super.valid();
	}
	public static HtmlAttr child(String name, Object... values) {
		return (HtmlAttr)HtmlAttr.child(name, values);
	}
	public static HtmlAttr attr(String name, Object... values) {
		return (HtmlAttr)HtmlAttr.attr(name, values);
	}
	public static HtmlAttr inline(String name, Object... values) {
		return (HtmlAttr)HtmlAttr.inline(name, values);
	}
	protected boolean getInline() {
		return super.getInline();
	}
	public void println(Buffer buffer, int indent) {
		super.println(buffer, indent);
	}
	public void println(Buffer buffer) {
		super.println(buffer);
	}
	public void print(Buffer buffer) {
		super.print(buffer);
	}
	public void print(Buffer buffer, int indent) {
		super.print(buffer, indent);
	}
	protected String getName() {
		return super.getName();
	}
	protected String getValue() {
		return super.getValue();
	}
}
