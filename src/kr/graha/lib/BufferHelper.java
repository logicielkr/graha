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


package kr.graha.lib;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;                           
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

/**
 * Graha(그라하) (XML 출력관 관련된) Buffer 관련 유틸리티

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.2
 */


public final class BufferHelper {
	private static Logger logger = Logger.getLogger("kr.graha.lib.BufferHelper");
	private BufferHelper() {
	}
	public static void addRecord(String key, Record params, Buffer sb, XMLTag tag) {
		if(params.isArray(key)) {
			java.util.List<String> items = params.getArray(key);
			for(String item : items){
				sb.append(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, true));
				sb.append("<![CDATA[");
				if(item instanceof String) {
					sb.append(item.replace("]]>", "]]]]><![CDATA[>"));
				} else {
					sb.append(item);
				}
				sb.append("]]>");
				sb.appendL(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, false));
			}
		} else {
			sb.append(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, true));
			sb.append("<![CDATA[");
			if(params.get(key) instanceof String) {
				sb.append(((String)params.get(key)).replace("]]>", "]]]]><![CDATA[>"));
			} else {
				sb.append(params.get(key));
			}
			sb.append("]]>");
			sb.appendL(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, false));
		}
	}
	
}