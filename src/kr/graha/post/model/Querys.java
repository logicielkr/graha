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


package kr.graha.post.model;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import kr.graha.helper.LOG;
import java.io.IOException;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import javax.servlet.http.HttpServletRequest;
import kr.graha.post.element.XmlElement;

/**
 * Graha(그라하) Querys 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Querys {
	private static final String nodeName = "querys";
	private Header header = null;
	private Header extend = null;
	private List<QueryXMLImpl> query = null;
	private Document doc = null;
	private boolean headerLoaded = false;
	private File file = null;
	protected Querys() {
	}
	protected static String nodeName() {
		return Querys.nodeName;
	}
	protected static String nodePath() {
		return Querys.nodeName;
	}
	private void loadHeader() {
		if(this.headerLoaded) {
			return;
		}
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(Header.nodePath(this));
			NodeList nl = (NodeList)expr.evaluate(this.doc, XPathConstants.NODESET);
			if(nl != null) {
				for(int i = 0; i < nl.getLength(); i++) {
					Element element = (Element)nl.item(i);
					this.setHeader(Header.load(element, this.header));
				}
			}
			this.headerLoaded = true;
			if(this.getHeader() != null && STR.valid(this.getHeader().getExtend())) {
				File parentQuerysFile = null;
				if(this.getHeader().getExtend().toLowerCase().endsWith(".xml")) {
					parentQuerysFile = new File(this.file.getParentFile(), this.getHeader().getExtend());
				} else {
					parentQuerysFile = new File(this.file.getParentFile(), this.getHeader().getExtend() + ".xml");
				}
				if(parentQuerysFile.exists()) {
					Querys parentQuerys = Querys.load(parentQuerysFile);
					this.extend = parentQuerys.getHeader();
				}
			}
		} catch (XPathExpressionException e) {
			LOG.severe(e);
		}
	}
	private void setHeader(Header header) {
		this.header = header;
	}
	protected Header getHeader() {
		if(this.header == null) {
			loadHeader();
		}
		return this.header;
	}
	private void loads(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					load(node);
				}
			}
		}
	}
	private void load(Node node) {
		if(node == null) {
			return;
		}
		if(STR.compareIgnoreCase(node.getNodeName(), "header")) {
			if(!this.headerLoaded) {
				this.setHeader(Header.load((Element)node, this.header));
			}
		} else if(STR.compareIgnoreCase(node.getNodeName(), "query")) {
			if(this.query == null) {
				this.query = new ArrayList<QueryXMLImpl>();
			}
			this.loadHeader();
			QueryXMLImpl q = Query.load((Element)node, this.header, this.extend);
			this.query.add(q);
			if(STR.valid(q.getExtend())) {
				QueryXMLImpl extendQuery = this.getQuery(q.getExtend());
				q.setExtendQuery(extendQuery);
			}
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
		}
	}
	public void loadAll() {
		NodeList nl = this.doc.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "querys")) {
							this.loads(node);
						} else {
							LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
		this.headerLoaded = true;
	}
	public QueryXMLImpl getQuery(String id) {
		int index = this.exists(id);
		if(index == -1) {
			loadQuery(id);
			index = this.exists(id);
		}
		if(index >= 0) {
			return this.query.get(index);
		} else {
			return null;
		}
	}
	private int exists(String id) {
		if(this.query == null) {
			return -1;
		}
		for(int i = 0; i < this.query.size(); i++) {
			if(STR.compareIgnoreCase(id, ((QueryXMLImpl)this.query.get(i)).getId())) {
				return i;
			}
		}
		return -1;
	}
	public Buffer toXSL(String id, Record param, HttpServletRequest request) {
		QueryXMLImpl q = this.getQuery(id);
		if(q != null) {
			return ((QueryXSLImpl)q).toXSL(param, request, 0);
		}
		return null;
	}
	public static String parseId(String pathInfo) {
		if(pathInfo.indexOf(".xml/download/") > 0) {
			return pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.indexOf(".xml/download/"));
		} else if(pathInfo.indexOf(".html/download/") > 0) {
			return pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.indexOf(".html/download/"));
		} else {
			return pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.lastIndexOf("."));
		}
	}
	private void loadQuery(String id) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(Query.nodePath(this) + "[@id = '" + id + "']");
			Element element = (Element)expr.evaluate(this.doc, XPathConstants.NODE);
			if(element == null) {
				return;
			}
			this.load(element);
		} catch (XPathExpressionException e) {
			LOG.severe(e);
		}
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		if(this.header != null) {
			element.appendChild(this.header.element());
		}
		if(this.query != null && this.query.size() > 0) {
			for(int i = 0; i < this.query.size(); i++) {
				element.appendChild(((Query)this.query.get(i)).element());
			}
		}
		return element;
	}
	public Buffer toConfig() {
		Buffer config = new Buffer();
		this.element().print(config);
		return config;
	}
	public Document toDocument() {
		Document document = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setXIncludeAware(true);
			document = dbf.newDocumentBuilder().newDocument();
			document.appendChild(this.element().element(document));
		} catch (ParserConfigurationException e) {
			LOG.severe(e);
		}
		return document;
	}
	public static QueryXMLImpl load(HttpServletRequest request, Record params) {
		Querys querys = new Querys();
		String pathInfo = request.getPathInfo();
		if(!STR.valid(pathInfo)) {
			LOG.info("[SC_NOT_FOUND]request.getPathInfo() is null");
			return null;
		}
		pathInfo = pathInfo.trim();
		if(pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}
		if(pathInfo.indexOf("/") < 1) {
			LOG.info("[SC_NOT_FOUND]invalid path info, request.getPathInfo() = " + pathInfo);
			return null;
		}
		String configFileName = pathInfo.substring(0, pathInfo.indexOf("/"));
		int requestType = 0;
		String queryId = null;
		if(pathInfo.indexOf(".xml/download/") > 0) {
			requestType = QueryImpl.REQUEST_TYPE_XML_DOWNLOAD;
			queryId = pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.indexOf(".xml/download/"));
		} else if(pathInfo.indexOf(".html/download/") > 0) {
			requestType = QueryImpl.REQUEST_TYPE_HTML_DOWNLOAD;
			queryId = pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.indexOf(".html/download/"));
		} else if(pathInfo.endsWith(".xml")) {
			requestType = QueryImpl.REQUEST_TYPE_XML;
		} else if(pathInfo.endsWith(".xsl")) {
			requestType = QueryImpl.REQUEST_TYPE_XSL;
		} else if(pathInfo.endsWith(".html")) {
			requestType = QueryImpl.REQUEST_TYPE_HTML;
		} else {
			LOG.info("[SC_NOT_FOUND]invalid path info, request.getPathInfo() = " + pathInfo);
			return null;
		}
		if(queryId == null) {
			queryId = pathInfo.substring(pathInfo.indexOf("/") + 1, pathInfo.lastIndexOf("."));
		}
		if(
			STR.invalid(configFileName) ||
			STR.invalid(queryId)
		) {
			LOG.info("[SC_NOT_FOUND]config file name or query id is empty, request.getPathInfo() = " + pathInfo);
			return null;
		}
		LOG.config(
			"ContextPath = " + request.getContextPath(),
			"request.getPathInfo() = " + request.getPathInfo(),
			"Config File = " + configFileName,
			"query id = " + queryId,
			"Context Root Path = " + request.getServletContext().getRealPath("/")
		);
		File config = new File(request.getServletContext().getRealPath("/WEB-INF") + "/graha/" + configFileName + ".xml");
		if(!config.exists()) {
			LOG.info("[SC_NOT_FOUND]config file is not exists, config file = " + config.getPath());
			return null;
		}
		try {
			querys.loadFile(config);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			return null;
		}
		QueryXMLImpl query = querys.getQuery(queryId);
		if(query == null) {
			LOG.warning("[SC_NOT_FOUND]query is null, request.getPathInfo() = " + pathInfo);
			return null;
		}
		query.setRequestType(requestType);
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "config.file.name"), configFileName);
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "config.query.id"), queryId);
		return query;
	}
	public void loadFile(File file) throws SAXException, IOException, ParserConfigurationException {
		this.file = file;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setXIncludeAware(true);
			this.doc = dbf.newDocumentBuilder().parse(file);
			this.doc.getDocumentElement().normalize();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOG.severe(e);
			throw e;
		}
	}
	public static Querys load(File file) {
		Querys querys = new Querys();
		try {
			querys.loadFile(file);
			return querys;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOG.severe(e);
		}
		return null;
	}
}
