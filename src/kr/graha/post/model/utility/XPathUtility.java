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


package kr.graha.post.model.utility;

import kr.graha.post.xml.GParam;
import kr.graha.post.xml.GRow;
import kr.graha.post.xml.GMessage;
import kr.graha.helper.STR;

/**
 * Graha(그라하) XPathUtility
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class XPathUtility {
	private XPathUtility() {
	}
	public static String valueExpr(String val, boolean rdf) {
		return XPathUtility.valueExpr(val, rdf, true);
	}
	public static String valueExpr(String val, boolean rdf, boolean full) {
		if(
			val.startsWith("param.") ||
			val.startsWith("result.") ||
			val.startsWith("prop.") ||
			val.startsWith("error.")
		) {
			return GParam.childNodePath(
				val.substring(0, val.indexOf(".")),
				val.substring(val.indexOf(".") + 1),
				rdf
			);
		} else if(val.startsWith("query.")) {
			String name = val.substring(val.indexOf(".") + 1);
			if(STR.valid(name) && name.indexOf(".") > 0) {
				return GRow.childNodePath(
					name.substring(0, name.indexOf(".")),
					name.substring(name.indexOf(".") + 1),
					rdf
				);
				
			} else {
				if(full) {
					return GRow.childNodePath(
						null,
						val.substring(val.indexOf(".") + 1),
						rdf
					);
				} else {
					return GRow.childNodeName(
						val.substring(val.indexOf(".") + 1),
						rdf
					);
				}
			}
		} else if(val.startsWith("message.")) {
			return GMessage.labelPath(val.substring(val.indexOf(".") + 1), rdf); 
		} else {
			return val;
		}
	}
	
}
