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

import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import kr.graha.post.lib.Record;

/**
 * Graha(그라하) TextParser
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class TextParser {
	private TextParser() {
	}
	public static String parse(String text, Record param) {
		Record result = new Record();
		TextParser.parse(text, param, result);
		return result.getString(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "filepath"));
	}
	public static void parse(String text, Record param, Record result) {
		String path = new String(text);
		Buffer sb = new Buffer();
		while(true) {
			if(path.indexOf("${") >= 0) {
				sb.append(path.substring(0, path.indexOf("${")));
				path = path.substring(path.indexOf("${") + 2);
				if(path.indexOf("}") > 0) {
					String val = path.substring(0, path.indexOf("}"));
					if(STR.compareIgnoreCase(val, "system.uuid")) {
						sb.append(java.util.UUID.randomUUID().toString());
					} else if(STR.compareIgnoreCase(val, "system.uuid2")) {
						sb.append(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
					} else if(param.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, val + ".0"))) {
						sb.append(param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, val + ".0")));
						result.put(Record.key(Record.PREFIX_TYPE_UNKNOWN, val), param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, val + ".0")));
					} else if(param.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, val))) {
						sb.append(param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, val)));
						result.put(Record.key(Record.PREFIX_TYPE_UNKNOWN, val), param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, val)));
					} else {
					}
					path = path.substring(path.indexOf("}") + 1);
				} else {
					sb.append(path);
					break;
				}
			} else {
				sb.append(path);
				break;
			}
		}
		result.puts(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "filepath"), sb.toString());
	}
	public static Buffer parseForXSL(String text, Record params, boolean rdf) {
		return TextParser.parseForXSL(text, params, rdf, true);
	}
	public static Buffer parseForXSL(String text, Record params, boolean rdf, boolean full) {
		Buffer sb = new Buffer();
		if(text == null) {
			return sb;
		}
		String title = new String(text);
		while(true) {
			if(title.indexOf("${") >= 0) {
				if(STR.nonempty(title.substring(0, title.indexOf("${")))) {
					sb.append("<xsl:text>" + title.substring(0, title.indexOf("${")) + "</xsl:text>");
				}
				title = title.substring(title.indexOf("${") + 2);
				if(title.indexOf("}") > 0) {
					String val = title.substring(0, title.indexOf("}"));
					if(STR.compareIgnoreCase(val, "system.uuid")) {
						sb.append("<xsl:text>" + java.util.UUID.randomUUID().toString() + "</xsl:text>");
					} else if(STR.compareIgnoreCase(val, "system.uuid2")) {
						sb.append("<xsl:text>" + java.util.UUID.randomUUID().toString().replaceAll("-", "") + "</xsl:text>");
					} else if(
						!STR.startsWithIgnoreCase(val, "query.") &&
						params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, val))
					) {
						sb.append(params.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, val)));
					} else {
						String v = XPathUtility.valueExpr(val, rdf, full);
						sb.append("<xsl:value-of select=\"" + v + "\" />");
					}
					title = title.substring(title.indexOf("}") + 1);
				} else {
					if(STR.nonempty(title)) {
						sb.append("<xsl:text>" + title + "</xsl:text>");
					}
					break;
				}
			} else {
				if(STR.nonempty(title)) {
					sb.append("<xsl:text>" + title + "</xsl:text>");
				}
				break;
			}
		}
		return sb;
	}
}
