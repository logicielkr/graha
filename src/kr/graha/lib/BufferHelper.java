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
import kr.graha.helper.XML;
import kr.graha.helper.LOG;

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
	protected static void addRecord(String key, Record params, Buffer sb, XMLTag tag) {
		if(!key.startsWith("prop.") && params.isArray(key)) {
			java.util.List<String> items = params.getArray(key);
			for(String item : items) {
				if(item != null) {
					sb.append(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, true));
					sb.append("<![CDATA[");
					if(key.startsWith("error.") && item.startsWith("message.") && params.hasKey(item)) {
						sb.append(params.get(item));
					} else {
						if(item instanceof String) {
							sb.append(XML.fix(item));
						} else {
							sb.append(item);
						}
					}
					sb.append("]]>");
					sb.appendL(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, false));
				}
			}
		} else {
			if(params.get(key) != null) {
				sb.append(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, true));
				sb.append("<![CDATA[");
				if(key.startsWith("error.") && ((String)params.get(key)).startsWith("message.") && params.hasKey((String)params.get(key))) {
					sb.append(params.get((String)params.getString(key)));
				} else {
					if(params.get(key) instanceof String) {
						sb.append(XML.fix((String)params.getString(key)));
					} else {
						sb.append(params.getString(key));
					}
				}
				sb.append("]]>");
				sb.appendL(tag.tag(key.substring(0, key.indexOf(".")), key.substring(key.indexOf(".") + 1), null, false));
			}
		}
	}
	protected static Document getExtendsDocument(
		XPath xpath,
		XPathExpression expr,
		File config,
		Element query
	) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		expr = xpath.compile("header");
		Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
		Document doc = null;
		if(header.hasAttribute("extends")) {
			File parent = new File(config.getParent() + File.separator + header.getAttribute("extends"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setXIncludeAware(true);
			doc = dbf.newDocumentBuilder().parse(parent);
			doc.getDocumentElement().normalize();
		}
		return doc;
	}
	protected static Buffer header(
		XPath xpath,
		XPathExpression expr,
		File config,
		Element query,
		String headerName
	) {
		Buffer sb = new Buffer();
		Document doc = null;
		NodeList nodes = null;
		NodeList parentNodes = null;
		NodeList extendNodes = null;
		try {
			expr = xpath.compile("header");
			Element header = (Element)expr.evaluate(query.getParentNode(), XPathConstants.NODE);
			expr = xpath.compile("header/" + headerName);

			nodes = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
			parentNodes = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			extendNodes = null;
			if(nodes.getLength() == 0 && query.hasAttribute("extends")) {
				expr = xpath.compile("query[@id='" + query.getAttribute("extends") + "']/header/" + headerName);
				nodes = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
			}
			if(header.hasAttribute("extends")) {
				if(doc == null) {
					doc = BufferHelper.getExtendsDocument(
						xpath,
						expr,
						config,
						query
					);
				}
				expr = xpath.compile("/querys/header/" + headerName);
				extendNodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			}
			if(extendNodes != null && extendNodes.getLength() >= 0) {
				sb.append(BufferHelper.appendHeader(
					xpath,
					expr,
					query,
					headerName,
					extendNodes,
					BufferHelper.NodesPositionExtend
				));
			}
			if(parentNodes != null && parentNodes.getLength() >= 0) {
				sb.append(BufferHelper.appendHeader(
					xpath,
					expr,
					query,
					headerName,
					parentNodes,
					BufferHelper.NodesPositionParent
				));
			}
			if(nodes != null && nodes.getLength() >= 0) {
				sb.append(BufferHelper.appendHeader(
					xpath,
					expr,
					query,
					headerName,
					nodes,
					BufferHelper.NodesPositionCurrent
				));
			}
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | DOMException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		return sb;
	}
	private static int NodesPositionCurrent = 0;
	private static int NodesPositionParent = 1;
	private static int NodesPositionExtend = 2;
	protected static Buffer appendHeader(
		XPath xpath,
		XPathExpression expr,
		Element query,
		String headerName,
		NodeList nodes,
		int nodesPosition
	) throws XPathExpressionException {
		Buffer sb = new Buffer();
		for(int i = 0; i < nodes.getLength(); i++) {
			Element node = (Element)nodes.item(i);
			expr = xpath.compile("header/" + headerName + "[@name='" + node.getAttribute("name") + "' and @override='true']");
			NodeList p = null;
			if(nodesPosition == BufferHelper.NodesPositionExtend || nodesPosition == BufferHelper.NodesPositionParent) {
				p = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
				if(p.getLength() > 0) {
					continue;
				}
			}
			if(nodesPosition == BufferHelper.NodesPositionExtend) {
				p = (NodeList)expr.evaluate(query.getParentNode(), XPathConstants.NODESET);
				if(p.getLength() > 0) {
					continue;
				}
			}
			if(
				headerName.equals("top") ||
				headerName.equals("bottom") ||
				headerName.equals("head")
			) {
			} else if(!node.hasAttribute("src")) {
				if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
					sb.appendL("\n<xsl:comment>[if IE]>");
					sb.appendL("&lt;" + headerName + ">");
				} else {
					sb.appendL("<" + headerName + ">");
				}
			} else if(headerName.equals("script")) {
				if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
					sb.appendL("\n<xsl:comment>[if IE]>");
					if(node.hasAttribute("charset")) {
						sb.appendL("&lt;" + headerName + " src=\"" + node.getAttribute("src") + "\" charset=\"" + node.getAttribute("charset") + "\" />");
					} else {
						sb.appendL("&lt;" + headerName + " src=\"" + node.getAttribute("src") + "\" />");
					}
					sb.appendL("&lt;![endif]</xsl:comment>\n");
				} else {
					if(node.hasAttribute("charset")) {
						sb.appendL("<" + headerName + " src=\"" + node.getAttribute("src") + "\" charset=\"" + node.getAttribute("charset") + "\" />");
					} else {
						sb.appendL("<" + headerName + " src=\"" + node.getAttribute("src") + "\" />");
					}
				}
			} else if(headerName.equals("style")) {
				if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
					sb.appendL("\n<xsl:comment>[if IE]>");
					sb.appendL("&lt;link rel=\"stylesheet\" href=\"" + node.getAttribute("src") + "\" type=\"text/css\" media=\"all\" />");
					sb.appendL("&lt;![endif]</xsl:comment>\n");
					
				} else {
					sb.appendL("<link rel=\"stylesheet\" href=\"" + node.getAttribute("src") + "\" type=\"text/css\" media=\"all\" />");
				}
			}
			if(
				headerName.equals("top") ||
				headerName.equals("bottom") ||
				headerName.equals("head")
			) {
				sb.append(node.getTextContent());
				sb.append("");
			} else if(!node.hasAttribute("src")) {
				sb.append(node.getTextContent());
				sb.append("");
				if(node.hasAttribute("only") && node.getAttribute("only").equals("ie")) {
					sb.appendL("&lt;/" + headerName + ">");
					sb.appendL("&lt;![endif]</xsl:comment>\n");
				} else {
					sb.appendL("</" + headerName + ">");
				}
			}
		}
		return sb;
	}
	private static Buffer parseLabelOrDescOrAuthorOrKeywords(String text) {
		String title = new String(text);
		Buffer sb = new Buffer();
		while(true) {
			if(title.indexOf("${") >= 0) {
				sb.append(title.substring(0, title.indexOf("${")));
				title = title.substring(title.indexOf("${") + 2);
				if(title.indexOf("}") > 0) {
					String val = title.substring(0, title.indexOf("}"));
					sb.append("<xsl:value-of select=\"" + val + "\" />");
					title = title.substring(title.indexOf("}") + 1);
				} else {
					sb.append(title);
					break;
				}
			} else {
				sb.append(title);
				break;
			}
		}
		return sb;
	}
	
	protected static Buffer getTitle(
		XPath xpath,
		XPathExpression expr,
		Element query,
		Record params,
		XMLTag tag
	) {
		return BufferHelper.getLabelOrDescOrAuthorOrKeywords(xpath, expr, query, params, tag, "label");
	}
	protected static Buffer getDesc(
		XPath xpath,
		XPathExpression expr,
		Element query,
		Record params,
		XMLTag tag
	) {
		return BufferHelper.getLabelOrDescOrAuthorOrKeywords(xpath, expr, query, params, tag, "desc");
	}
	protected static Buffer getAuthor(
		XPath xpath,
		XPathExpression expr,
		Element query,
		Record params,
		XMLTag tag
	) {
		return BufferHelper.getLabelOrDescOrAuthorOrKeywords(xpath, expr, query, params, tag, "author");
	}
	protected static Buffer getKeyword(
		XPath xpath,
		XPathExpression expr,
		Element query,
		Record params,
		XMLTag tag
	) {
		return BufferHelper.getLabelOrDescOrAuthorOrKeywords(xpath, expr, query, params, tag, "keyword");
	}
	private static Buffer getLabelOrDescOrAuthorOrKeywords(
		XPath xpath,
		XPathExpression expr,
		Element query,
		Record params,
		XMLTag tag,
		String name
	) {
		Buffer sb = new Buffer();
		if(name == null) {
			return null;
		}

		try {
			expr = xpath.compile("header/" + name + "s/" + name);
			NodeList labels = (NodeList)expr.evaluate(query, XPathConstants.NODESET);
			if(labels.getLength() > 0) {
				for(int i = 0; i < labels.getLength(); i++) {
					Element label = (Element)labels.item(i);
					if(!label.hasAttribute("cond") || AuthParser.auth(label.getAttribute("cond"), params)) {
						if(tag.isRDF && label.hasAttribute("xText")) {
							sb.append(BufferHelper.parseLabelOrDescOrAuthorOrKeywords(label.getAttribute("xText")));
						} else {
							sb.append(BufferHelper.parseLabelOrDescOrAuthorOrKeywords(label.getAttribute("text")));
						}
						break;
					}
				}
			}
		} catch (XPathExpressionException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
		}
		
		String x = "x" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		
		if(sb.length() == 0 && (XML.validAttrValue(query, name) || XML.validAttrValue(query, x))) {
			if(tag.isRDF && XML.validAttrValue(query, x)) {
				sb.append(BufferHelper.parseLabelOrDescOrAuthorOrKeywords(query.getAttribute(x)));
			} else {
				sb.append(BufferHelper.parseLabelOrDescOrAuthorOrKeywords(query.getAttribute(name)));
			}
		}
		return sb;
	}
}