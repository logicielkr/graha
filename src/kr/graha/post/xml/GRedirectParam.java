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

/**
 * GRedirectParam
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GRedirectParam {
	private String name = null;
	private Object value = null;
	
	public GRedirectParam(String name) {
		this.setName(name);
	}
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	protected boolean validValue() {
		if(this.getValue() == null) {
			return false;
		} else if(this.getValue() instanceof String && STR.empty(this.getValue().toString())) {
			return false;
		} else {
			return true;
		}
	}
	public void toXML(Buffer xml, boolean rdf) {
		if(this.validValue()) {
			if(rdf) {
				xml.appendL(4, "<uc:param><uc:name>" + this.getName() + "</uc:name><uc:value>" + this.getValue() + "</uc:value></uc:param>");
			} else {
				xml.appendL(4, "<param><name>" + this.getName() + "</name><value>" + this.getValue() + "</value></param>");
			}
		}
	}
}
