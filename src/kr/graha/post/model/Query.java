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

import java.util.ArrayList;
import java.util.List;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.element.XmlElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Graha(그라하) Query 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Query {
	private static final String nodeName = "query";
	
	protected static final int QUERY_FUNC_TYPE_UNKNOWN = 0;
	protected static final int QUERY_FUNC_TYPE_LIST = 1;
	protected static final int QUERY_FUNC_TYPE_LISTALL = 2;
	protected static final int QUERY_FUNC_TYPE_INSERT = 3;
	protected static final int QUERY_FUNC_TYPE_DETAIL = 4;
	protected static final int QUERY_FUNC_TYPE_DELETE = 5;
	public static final int QUERY_FUNC_TYPE_USER = 6;
	protected static final int QUERY_FUNC_TYPE_QUERY = 7;
	protected static final int QUERY_FUNC_TYPE_REPORT = 8;
/**
 * querys/query/header 
 */
	protected Header header = null;
/**
 * querys/query[@extends]
 * 동일한 querys 내의 다른 query 의 id 속성값
 * 이 속성값이 있고, layout 혹은 commands 혹은 table 요소가 없는 경우 이 속성값으로 지정된 다른 query id 의 그것을 사용한다.
 */
	private String extend = null;
	private String id = null;
	private String funcType = null;
	private String auth = null;
	private String htmltype = null;
	private String output = null;
	private String uc = null;
	
	private String label = null;
	private String xLabel = null;
	private String desc = null;
	private String xDesc = null;
	private String author = null;
	private String xAuthor = null;
	private String keyword = null;
	private String xKeyword = null;
	
	private String className = null;
	private String displayType	 = null;
	private String xsl = null;
	private String contentType = null;
	
	private String allow = null;
	private String allowblank = null;
	
	private List<String> append = null;
	private List<Command> command = null;
	protected List<Processor> processor = null;
	protected List<CalculatorParam> calculator = null;
	protected List<Table> table = null;
	
	protected List<Validation> validation = null;
	protected List<Redirect> redirect = null;
	protected Files files = null;
	private Layout layout = null;

	protected String getExtend() {
		return this.extend;
	}
	private void setExtend(String extend) {
		this.extend = extend;
	}
	protected String getId() {
		return this.id;
	}
	private void setId(String id) {
		this.id = id;
	}
	protected String getFuncType() {
		return this.funcType;
	}
	private void setFuncType(String funcType) {
		this.funcType = funcType;
	}
	protected String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	protected String getAuth() {
		return this.auth;
	}
	private void setAuth(String auth) {
		this.auth = auth;
	}
	protected String getHtmltype() {
		return this.htmltype;
	}
	private void setHtmltype(String htmltype) {
		this.htmltype = htmltype;
	}
	protected String getOutput() {
		return this.output;
	}
	private void setOutput(String output) {
		this.output = output;
	}
	protected String getUc() {
		return this.uc;
	}
	private void setUc(String uc) {
		this.uc = uc;
	}
	protected String getXLabel() {
		return this.xLabel;
	}
	private void setXLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	protected String getDesc() {
		return this.desc;
	}
	private void setDesc(String desc) {
		this.desc = desc;
	}
	protected String getXDesc() {
		return this.xDesc;
	}
	private void setXDesc(String xDesc) {
		this.xDesc = xDesc;
	}
	protected String getAuthor() {
		return this.author;
	}
	private void setAuthor(String author) {
		this.author = author;
	}
	protected String getXAuthor() {
		return this.xAuthor;
	}
	private void setXAuthor(String xAuthor) {
		this.xAuthor = xAuthor;
	}
	protected String getKeyword() {
		return this.keyword;
	}
	private void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	protected String getXKeyword() {
		return this.xKeyword;
	}
	private void setXKeyword(String xKeyword) {
		this.xKeyword = xKeyword;
	}
	protected String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className = className;
	}
	private String getDisplayType() {
		return this.displayType;
	}
	private void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	protected String getXsl() {
		return this.xsl;
	}
	private void setXsl(String xsl) {
		this.xsl = xsl;
	}
	public String getContentType() {
		return this.contentType;
	}
	private void setContentType(String contentType) {
		this.contentType = contentType;
	}
	protected String getAllow() {
		return this.allow;
	}
	private void setAllow(String allow) {
		this.allow = allow;
	}
	protected String getAllowblank() {
		return this.allowblank;
	}
	private void setAllowblank(String allowblank) {
		this.allowblank = allowblank;
	}
	private void setLayout(Layout layout) {
		this.layout = layout;
	}

	private void add(String append) {
		if(this.append == null) {
			this.append = new ArrayList<String>();
		}
		this.append.add(append);
	}
	private void add(Command command) {
		if(this.command == null) {
			this.command = new ArrayList<Command>();
		}
		this.command.add(command);
	}
	private void add(Processor processor) {
		if(this.processor == null) {
			this.processor = new ArrayList<Processor>();
		}
		this.processor.add(processor);
	}
	private void add(CalculatorParam calculator) {
		if(this.calculator == null) {
			this.calculator = new ArrayList<CalculatorParam>();
		}
		this.calculator.add(calculator);
	}
	private void add(Table table) {
		if(this.table == null) {
			this.table = new ArrayList<Table>();
		}
		this.table.add(table);
	}
	private void add(Validation validation) {
		if(this.validation == null) {
			this.validation = new ArrayList<Validation>();
		}
		this.validation.add(validation);
	}
	private void add(Redirect redirect) {
		if(this.redirect == null) {
			this.redirect = new ArrayList<Redirect>();
		}
		this.redirect.add(redirect);
	}
	private void setFiles(Files files) {
		this.files = files;
	}
	protected static String nodeName() {
		return Query.nodeName;
	}
	private void setHeader(Header header) {
		this.header = header;
	}
	protected static String nodePath(Object parent) {
		if(parent instanceof Querys) {
			return ((Querys)parent).nodePath() + "/" + Query.nodeName();
		} else {
			return Query.nodeName();
		}
	}
	protected static QueryXMLImpl load(Element element, Header rootHeader, Header extendHeader) {
		QueryXMLImpl query = new QueryXMLImpl(rootHeader, extendHeader);
		if(element != null) {
			query.loadAttr(element);
			query.loadElement(element);
			return query;
		}
		return null;
	}
	protected void loadAttr(Element element) {
		NamedNodeMap nnm = element.getAttributes();
		if(nnm != null && nnm.getLength() > 0) {
			for(int i = 0; i < nnm.getLength(); i++) {
				Node node = nnm.item(i);
				if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
					if(
						STR.valid(node.getNodeName()) &&
						STR.valid(node.getNodeValue())
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "extends")) {
							this.setExtend(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "id")) {
							this.setId(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "funcType")) {
							this.setFuncType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "auth")) {
							this.setAuth(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "htmltype")) {
							this.setHtmltype(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "output")) {
							this.setOutput(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "uc")) {
							this.setUc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xLabel")) {
							this.setXLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "desc")) {
							this.setDesc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xDesc")) {
							this.setXDesc(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "author")) {
							this.setAuthor(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xAuthor")) {
							this.setXAuthor(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "keyword")) {
							this.setKeyword(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xKeyword")) {
							this.setXKeyword(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "displayType")) {
							this.setDisplayType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xsl")) {
							this.setXsl(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "contentType")) {
							this.setContentType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "allow")) {
							this.setAllow(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "allowblank")) {
							this.setAllowblank(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xml:base")) {
						} else {
							LOG.warning("invalid attrName(" + node.getNodeName() + ")");
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
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
		if(STR.compareIgnoreCase(node.getNodeName(), "header")) {
			this.setHeader(Header.load((Element)node, this.header));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "command")) {
			this.add(Command.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "layout")) {
			this.setLayout(Layout.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "processor")) {
			this.add(Processor.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "append")) {
			this.add(node.getTextContent());
		} else if(STR.compareIgnoreCase(node.getNodeName(), "validation")) {
			this.add(Validation.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "table")) {
			this.add(Table.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "files")) {
			this.setFiles(Files.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "redirect")) {
			this.add(Redirect.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(CalculatorParam.load((Element)node));
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
		}
	}
	protected void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "header")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "commands")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "layout")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "processors")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "append")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "validation")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "tables")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "files")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "redirect")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "calculator")) {
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
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		element.setAttribute("extends", this.getExtend());
		element.setAttribute("id", this.getId());
		element.setAttribute("funcType", this.getFuncType());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("auth", this.getAuth());
		element.setAttribute("htmltype", this.getHtmltype());
		element.setAttribute("output", this.getOutput());
		element.setAttribute("uc", this.getUc());
		element.setAttribute("xLabel", this.getXLabel());
		element.setAttribute("desc", this.getDesc());
		element.setAttribute("xDesc", this.getXDesc());
		element.setAttribute("author", this.getAuthor());
		element.setAttribute("xAuthor", this.getXAuthor());
		element.setAttribute("keyword", this.getKeyword());
		element.setAttribute("xKeyword", this.getXKeyword());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("displayType", this.getDisplayType());
		element.setAttribute("xsl", this.getXsl());
		element.setAttribute("contentType", this.getContentType());
		element.setAttribute("allow", this.getAllow());
		element.setAttribute("allowblank", this.getAllowblank());
		if(this.header != null) {
			element.appendChild(this.header.element());
		}
		if(this.command != null && this.command.size() > 0) {
			XmlElement child = element.createElement("commands");
			for(int i = 0; i < this.command.size(); i++) {
				child.appendChild(((Command)this.command.get(i)).element());
			}
		}
		if(this.table != null && this.table.size() > 0) {
			for(int i = 0; i < this.table.size(); i++) {
				element.appendChild(((Table)this.table.get(i)).element());
			}
		}
		if(this.processor != null && this.processor.size() > 0) {
			XmlElement child = element.createElement("processors");
			for(int i = 0; i < this.processor.size(); i++) {
				child.appendChild(((Processor)this.processor.get(i)).element());
			}
		}
		if(this.calculator != null && this.calculator.size() > 0) {
			XmlElement child = element.createElement("calculator");
			for(int i = 0; i < this.calculator.size(); i++) {
				child.appendChild(((CalculatorParam)this.calculator.get(i)).element());
			}
		}
		if(this.validation != null && this.validation.size() > 0) {
			for(int i = 0; i < this.validation.size(); i++) {
				element.appendChild(((Validation)this.validation.get(i)).element());
			}
		}
		if(this.redirect != null && this.redirect.size() > 0) {
			for(int i = 0; i < this.redirect.size(); i++) {
				element.appendChild(((Redirect)this.redirect.get(i)).element());
			}
		}
		if(this.files != null) {
			element.appendChild(this.files.element());
		}
		if(this.layout != null) {
			element.appendChild(this.layout.element());
		}
		return element;
	}
	protected List<String> getAppend() {
		return this.append;
	}
	protected List<Command> getCommand() {
		return this.command;
	}
	protected Layout getLayout() {
		return this.layout;
	}
}
