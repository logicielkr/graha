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

package kr.graha.servlet;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import kr.graha.helper.LOG;
import kr.graha.lib.Record;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Graha(그라하) Servlet 관련 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class ServletUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected ServletUtil() {
		LOG.setLogLevel(logger);
	}
	protected Element getQuery(File config, String id) {
		Element query = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setXIncludeAware(true);
	
			Document doc = dbf.newDocumentBuilder().parse(config);
			doc.getDocumentElement().normalize();
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
	
			XPathExpression expr = null;
			if(id.indexOf(".xml/download/") > 0) {
				expr = xpath.compile("/querys/query[@id = '" + id.substring(id.indexOf("/") + 1, id.indexOf(".xml/download/")) + "']");
			} else if(id.indexOf(".html/download/") > 0) {
				expr = xpath.compile("/querys/query[@id = '" + id.substring(id.indexOf("/") + 1, id.indexOf(".html/download/")) + "']");
			} else {
				expr = xpath.compile("/querys/query[@id = '" + id.substring(id.indexOf("/") + 1, id.lastIndexOf(".")) + "']");
			}
			query = (Element)expr.evaluate(doc, XPathConstants.NODE);
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return query;
	}
	protected boolean acceptsGZipEncoding(HttpServletRequest request, String gzip) {
		boolean result = false;
		String acceptEncoding = request.getHeader("Accept-Encoding");
		if(
			gzip != null 
			&& (
				gzip.equalsIgnoreCase("y") ||
				gzip.equalsIgnoreCase("yes") || 
				gzip.equalsIgnoreCase("true")
			)
			&& acceptEncoding != null
			&& acceptEncoding.indexOf("gzip") >= 0
		) {
			result = true;
		}
		return result;
	}
	
}