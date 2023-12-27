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

import java.util.List;
import java.util.ArrayList;
import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;

/**
 * GPages
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GPages {
	private String name;
	private List<GPage> pages = null;
	private GPages() {
	}
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private void add(GPage page) {
		if(this.pages == null) {
			this.pages = new ArrayList<GPage>();
		}
		this.pages.add(page);
	}
	public static GPages load(String name, int currentPage, int totalCount, int pageSize, int pageGroupSize) {
		GPages pages = new GPages();
		pages.setName(name);
		int totalPage = (int)(Math.ceil((double)totalCount/(double)pageSize));
		if(totalPage > 0) {
			if(((Math.floor(((double)currentPage - 1)/(double)pageGroupSize) * (double)pageGroupSize) + 1) >= pageGroupSize) {
				pages.add(new GPage((int)((Math.floor(((double)currentPage-1) / (double)pageGroupSize) * (double)pageGroupSize) + 1) - 1, "◀"));
			}
			for(int i = (int)((Math.floor(((double)currentPage - 1)/(double)pageGroupSize) * (double)pageGroupSize) + 1); i <= Math.min(totalPage, ((Math.floor((currentPage - 1)/pageGroupSize) * pageGroupSize) + pageGroupSize)); i++) {
				pages.add(new GPage(i));
			}
			if(totalPage >= ((Math.floor((currentPage - 1)/pageGroupSize) * pageGroupSize) + pageGroupSize + 1)) {
				pages.add(new GPage((int)((Math.floor((currentPage - 1)/pageGroupSize) * pageGroupSize) + pageGroupSize + 1), "▶"));
			}
		}
		return pages;
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(this.pages == null) {
			return;
		}
		if(this.pages.size() == 0) {
			return;
		}
		if(rdf) {
			if(STR.valid(this.getName())) {
				xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:pages:" + this.getName() + "\">");
			} else {
				xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:pages\">");
			}
		} else {
			if(STR.valid(this.getName())) {
				xml.appendL(1, "<pages id=\"" + this.getName() + "\">");
			} else {
				xml.appendL(1, "<pages>");
			}
		}
		for(int i = 0; i < this.pages.size(); i++) {
			GPage page = (GPage)this.pages.get(i);
			page.toXML(xml, rdf);
		}
		if(rdf) {
			xml.appendL(1, "</RDF:Seq>");
		} else {
			xml.appendL(1, "</pages>");
		}
	}
	public static String nodePath(boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:pages']/RDF:li";
		} else {
			return "/document/pages/page";
		}
	}
	public static String childNodeName(String childNodeName, boolean rdf) {
		if(rdf) {
			return "RDF:item/uc:" + childNodeName;
		} else {
			return childNodeName;
		}
	}
}
