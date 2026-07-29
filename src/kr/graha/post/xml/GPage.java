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


package kr.graha.post.xml;

import kr.graha.post.lib.Buffer;

/**
 * GPage
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GPage {
	private int no;
	private String text = null;
	private int type;
	
	protected static int TYPE_OF_FIRST_PAGE = 81;
	protected static int TYPE_OF_PREV_GROUP = 82;
	protected static int TYPE_OF_PAGE = 83;
	protected static int TYPE_OF_NEXT_GROUP = 84;
	protected static int TYPE_OF_LAST_PAGE = 85;
	
	public GPage(int no, String text) {
		this.setNo(no);
		this.setText(text);
		this.setType(GPage.TYPE_OF_PAGE);
	}
	public GPage(int no, String text, int type) {
		this.setNo(no);
		this.setText(text);
		this.setType(type);
	}
	public GPage(int no) {
		this.setNo(no);
		this.setType(GPage.TYPE_OF_PAGE);
	}
	public int getNo() {
		return this.no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getText() {
		if(this.text == null) {
			return Integer.toString(this.no);
		} else {
			return this.text;
		}
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTypeClassName() {
		if(this.type == GPage.TYPE_OF_FIRST_PAGE) {
			return "page first_page";
		} else if(this.type == GPage.TYPE_OF_PREV_GROUP) {
			return "page prev_group";
		} else if(this.type == GPage.TYPE_OF_NEXT_GROUP) {
			return "page next_group";
		} else if(this.type == GPage.TYPE_OF_LAST_PAGE) {
			return "page last_page";
		} else {
			return "page";
		}
	}
	public void setType(int type) {
		this.type = type;
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(2, "<RDF:li><RDF:item><uc:no>" + this.getNo() + "</uc:no><uc:text>" + this.getText() + "</uc:text><uc:type>" + this.getTypeClassName() + "</uc:type></RDF:item></RDF:li>");
		} else {
			xml.appendL(2, "<page><no>" + this.getNo() + "</no><text>" + this.getText() + "</text><type>" + this.getTypeClassName() + "</type></page>");
		}
		
	}
}
