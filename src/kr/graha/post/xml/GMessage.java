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
 * GMessage
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GMessage {
	private String name;
	private String label;
	public GMessage(String name, String label) {
		this.setName(name);
		this.setLabel(label);
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(2, "<RDF:li><RDF:item><uc:name>" + this.getName() + "</uc:name><uc:label>" + this.getLabel() + "</uc:label></RDF:item></RDF:li>");
		} else {
			xml.appendL(2, "<message><name>" + this.getName() + "</name><label>" + this.getLabel() + "</label></message>");
		}
	}
	public static String nodePath(boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:messages']/RDF:li";
		} else {
			return "/document/messages/message";
		}
	}
	public static String childNodeName(String childNodeName, boolean rdf) {
		if(rdf) {
			return "RDF:item/uc:" + childNodeName;
		} else {
			return childNodeName;
		}
	}
	public static String labelPath(String name, boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:messages']/RDF:li/RDF:item[uc:name='" + name + "']/uc:label";
		} else {
			return "/document/messages/message[name='" + name + "']/label";
		}
	}
}
