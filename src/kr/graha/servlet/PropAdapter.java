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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
import org.xml.sax.SAXException;

import kr.graha.lib.FileHelper;
import kr.graha.helper.LOG;
import kr.graha.lib.Record;
import kr.graha.lib.AuthParser;

/**
 * Graha(그라하) 사용자 정의 Property 처리기
 * @author HeonJik, KIM
 * @version 0.5.0.3
 * @since 0.5.0.3
 */

public class PropAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected PropAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, File config, Element query, Record params) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			XPathExpression expr = xpath.compile("header");
			Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
			
			expr = xpath.compile("header/prop");
			NodeList[] nodes = new NodeList[3];
			nodes[0] = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
			nodes[1] = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			nodes[2] = null;
			if(nodes[0].getLength() == 0 && query.hasAttribute("extends")) {
				expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/prop");
				nodes[0] = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			}

			Document doc = null;
			if(header.hasAttribute("extends")) {
					File parent = new File(config.getParent() + File.separator + header.getAttribute("extends"));
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					dbf.setXIncludeAware(true);
					doc = dbf.newDocumentBuilder().parse(parent);
					doc.getDocumentElement().normalize();
					expr = xpath.compile("/querys/header/prop");
					nodes[2] = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			}
			
			for(int x = nodes.length - 1; x >= 0 ; x--) {
				if(nodes[x] == null || nodes[x].getLength() == 0) {
					continue;
				}
				for(int i = 0; i < nodes[x].getLength(); i++) {
					Element node = (Element)nodes[x].item(i);
					if(node.hasAttribute("name") && node.hasAttribute("value")) {
						expr = xpath.compile("header/prop[@name='" + node.getAttribute("name") + "' and override='true']");
						NodeList p = null;
						if(x == 1) {
							p = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(x == 2 || x == 1) {
							p = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
							if(p.getLength() > 0) {
								continue;
							}
						}
						if(!node.hasAttribute("cond") || AuthParser.auth(node.getAttribute("cond"), params)) {
							Record result = FileHelper.parse(node.getAttribute("value"), params);
							if(result != null && result.get("_system.filepath") != null) {
								params.puts("prop." + node.getAttribute("name"), result.get("_system.filepath"));
							}
						}
					}
				}
			}
		} catch (XPathExpressionException | DOMException | ParserConfigurationException | SAXException | IOException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LOG.toString(e));
			}
		}
	}
}