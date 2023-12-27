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

/**
 * GCode
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GCode {
	private String name;
	private List<GOption> options = null;
	public GCode(String name) {
		this.setName(name);
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void add(GOption option) {
		if(this.options == null) {
			this.options = new ArrayList<GOption>();
		}
		this.options.add(option);
	}
	public void add(String value, String label) {
		this.add(new GOption(value, label));
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(this.options == null) {
			return;
		}
		if(this.options.size() == 0) {
			return;
		}
		if(rdf) {
			xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:code:" + this.getName() + "\">");
		} else {
			xml.appendL(1, "<code name=\"" + this.getName() + "\">");
		}
		for(int i = 0; i < this.options.size(); i++) {
			GOption option = (GOption)this.options.get(i);
			option.toXML(xml, rdf);
		}
		if(rdf) {
			xml.appendL(1, "</RDF:Seq>");
		} else {
			xml.appendL(1, "</code>");
		}
	}
	public static String valueAttrName(boolean rdf) {
		if(rdf) {
			return "uc:value";
		} else {
			return "value";
		}
	}
	public static String labelAttrName(boolean rdf) {
		if(rdf) {
			return "uc:label";
		} else {
			return "label";
		}
	}
	public static String optionNodePath(String gRowsName, boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:code:" + gRowsName + "']/RDF:li/RDF:item";
		} else {
			return "/document/code[@name='" + gRowsName + "']/option";
		}
	}
	public static String optionChildAttrName(String tagName, boolean rdf) {
		if(rdf) {
			return "uc:" + tagName;
		} else {
			return tagName;
		}
	}
}
