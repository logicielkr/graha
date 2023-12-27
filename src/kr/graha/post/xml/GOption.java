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
 * GOption
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GOption {
	private String value;
	private String label;
	public GOption(String value, String label) {
		this.setValue(value);
		this.setLabel(label);
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(2, "<RDF:li><RDF:item uc:value=\"" + this.getValue() + "\" uc:label=\"" + this.getLabel() + "\" /></RDF:li>");
		} else {
			xml.appendL(2, "<option value=\"" + this.getValue() + "\" label=\"" + this.getLabel() + "\" />");
		}
	}
}
