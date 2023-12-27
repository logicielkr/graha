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
import kr.graha.helper.STR;
import kr.graha.post.lib.Key;

/**
 * GParam
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GParam {
	protected static int PARAM_TYPE_PARAMS = 1;
	protected static int PARAM_TYPE_PROPS = 2;
	protected static int PARAM_TYPE_RESULTS = 3;
	protected static int PARAM_TYPE_ERRORS = 4;
	
	private Key key;
	private String value;
	public GParam(Key key) {
		this.setKey(key);
	}
	public GParam(Key key, String value) {
		this.setKey(key);
		this.setValue(value);
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Key getKey() {
		return this.key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(2, "<uc:" + this.getKey().getKey() + "><![CDATA[" + this.getValue() + "]]></uc:" + this.getKey().getKey() + ">");
		} else {
			xml.appendL(2, "<" + this.getKey().getKey() + "><![CDATA[" + this.getValue() + "]]></" + this.getKey().getKey() + ">");
		}
	}
	public void toXMLForFileParam(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(3, "<uc:param>");
			xml.appendL(4, "<uc:key>" + this.getKey().toString() + "</uc:key>");
			xml.appendL(4, "<uc:value>" + this.getValue() + "</uc:value>");
			xml.appendL(3, "</uc:param>");
		} else {
			xml.appendL(2, "<param>");
			xml.appendL(3, "<key>" + this.getKey().toString() + "</key>");
			xml.appendL(3, "<value>" + this.getValue() + "</value>");
			xml.appendL(2, "</param>");
		}
	}
	public static String childNodePath(String paramPrefix, String childNodeName, boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Description/uc:" + paramPrefix + "s/uc:" + childNodeName;
		} else {
			return "/document/" + paramPrefix + "s/" + childNodeName;
		}
	}
	public static String nodePathForFileParam(String fileName, boolean rdf) {
		if(rdf) {
			if(STR.valid(fileName)) {
				return "/RDF:RDF/RDF:Description[@uc:for='urn:root:files:" + fileName + "']/uc:params/uc:param";
			} else {
				return "/RDF:RDF/RDF:Description[@uc:for='urn:root:files']/uc:params/uc:param";
			}
		} else {
			if(STR.valid(fileName)) {
				return "/document/params[@for='files." + fileName + "']/param";
			} else {
				return "/document/params[@for='files']/param";
			}
		}
	}
	public static String childNodeNameForFileParam(String childNodeName, boolean rdf) {
		if(rdf) {
			return "uc:" + childNodeName;
		} else {
			return childNodeName;
		}
	}
}
