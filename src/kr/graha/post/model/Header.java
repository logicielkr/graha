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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.helper.XML;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.GrahaConnectionFactoryImpl;
import kr.graha.post.model.utility.TextParser;
import kr.graha.post.xml.GDocument;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.security.NoSuchProviderException;
import java.sql.SQLException;

/**
 * querys/query/header
 * querys/header
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Header {
	private static final String nodeName = "header";

	private String extend = null;
	private Jndi jndi = null;
	private Jdbc jdbc = null;
	private List<Prop> prop = null;
	private List<Style> style = null;
	private List<Script> script = null;
	
	private List<Head> head = null;
	private List<Head> top = null;
	private List<Head> bottom = null;
	
	private List<Message> message = null;
	
	private List<Code> code = null;
	private List<Label> label = null;
	private List<Label> desc = null;
	private List<Label> author = null;
	private List<Label> keyword = null;
	private String connectionFactory = null;
	protected Header() {
	}
	protected static String nodeName() {
		return Header.nodeName;
	}
	protected static String nodePath(Object parent) {
		if(parent instanceof Querys) {
			return ((Querys)parent).nodePath() + "/" + Header.nodeName();
		} else {
			return Header.nodeName();
		}
	}
	private String getConnectionFactory() {
		return this.connectionFactory;
	}
	private void setConnectionFactory(Node node) {
		if(XML.validAttrValue((Element)node, "factory")) {
			this.setConnectionFactory(((Element)node).getAttribute("factory"));
		} else {
			LOG.warning("<connection> not exists @factory");
		}
	}
	private void setConnectionFactory(String connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	protected String getExtend() {
		return this.extend;
	}
	private void setExtend(String extend) {
		this.extend = extend;
	}
	private Jndi getJndi() {
		return this.jndi;
	}
	private void setJndi(Jndi jndi) {
		this.jndi = jndi;
	}
	private Jdbc getJdbc() {
		return this.jdbc;
	}
	private void setJdbc(Jdbc jdbc) {
		this.jdbc = jdbc;
	}
	private void add(Prop prop) {
		if(this.prop == null) {
			this.prop = new ArrayList<Prop>();
		}
		this.prop.add(prop);
	}
	private void add(Style style) {
		if(this.style == null) {
			this.style = new ArrayList<Style>();
		}
		this.style.add(style);
	}
	private void add(Script script) {
		if(this.script == null) {
			this.script = new ArrayList<Script>();
		}
		this.script.add(script);
	}
	private void addTop(Head top) {
		if(this.top == null) {
			this.top = new ArrayList<Head>();
		}
		this.top.add(top);
	}
	private void addBottom(Head bottom) {
		if(this.bottom == null) {
			this.bottom = new ArrayList<Head>();
		}
		this.bottom.add(bottom);
	}
	private void add(Message message) {
		if(this.message == null) {
			this.message = new ArrayList<Message>();
		}
		this.message.add(message);
	}
	private void addHead(Head head) {
		if(this.head == null) {
			this.head = new ArrayList<Head>();
		}
		this.head.add(head);
	}
	private void add(Code code) {
		if(this.code == null) {
			this.code = new ArrayList<Code>();
		}
		this.code.add(code);
	}
	private void addLabel(Label label) {
		if(this.label == null) {
			this.label = new ArrayList<Label>();
		}
		this.label.add(label);
	}
	private void addDesc(Label desc) {
		if(this.desc == null) {
			this.desc = new ArrayList<Label>();
		}
		this.desc.add(desc);
	}
	private void addAuthor(Label author) {
		if(this.author == null) {
			this.author = new ArrayList<Label>();
		}
		this.author.add(author);
	}
	private void addKeyword(Label keyword) {
		if(this.keyword == null) {
			this.keyword = new ArrayList<Label>();
		}
		this.keyword.add(keyword);
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
		if(STR.compareIgnoreCase(node.getNodeName(), "jndi")) {
			this.setJndi(Jndi.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "connection")) {
			this.setConnectionFactory(node);
		} else if(STR.compareIgnoreCase(node.getNodeName(), "prop")) {
			this.add(Prop.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "style")) {
			this.add(Style.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "script")) {
			this.add(Script.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "jdbc")) {
			this.setJdbc(Jdbc.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "top")) {
			this.addTop(Head.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "bottom")) {
			this.addBottom(Head.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "code")) {
			this.add(Code.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "message")) {
			this.add(Message.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "head")) {
			this.addHead(Head.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
			this.addLabel(Label.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "desc")) {
			this.addDesc(Label.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "author")) {
			this.addAuthor(Label.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "keyword")) {
			this.addKeyword(Label.load((Element)node));
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
		}
	}
	private void loadAttr(Element element) {
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
	private void loadElement(Element element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "jndi")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "connection")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "prop")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "props")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "style")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "styles")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "script")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "scripts")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "jdbc")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "top")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "tops")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "bottom")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "bottoms")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "codes")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "messages")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "head")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "heads")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "labels")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "desc")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "descs")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "author")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "authors")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "keyword")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "keywords")) {
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
	protected static Header load(Element element, Header header) {
		if(header == null) {
			header = new Header();
		}
		if(element != null) {
			header.loadAttr(element);
			header.loadElement(element);
			return header;
		}
		return header;
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		element.setAttribute("extends", this.getExtend());
		if(this.jndi != null) {
			element.appendChild(this.jndi.element());
		}
		if(this.prop != null && this.prop.size() > 0) {
			for(int i = 0; i < this.prop.size(); i++) {
				element.appendChild(((Prop)this.prop.get(i)).element());
			}
		}
		if(STR.valid(this.getConnectionFactory())) {
			XmlElement child = element.createElement("connection");
			child.setAttribute("factory", this.getConnectionFactory());
		}
		if(this.jdbc != null) {
			element.appendChild(this.jdbc.element());
		}
		if(this.style != null && this.style.size() > 0) {
			for(int i = 0; i < this.style.size(); i++) {
				element.appendChild(((Style)this.style.get(i)).element());
			}
		}
		if(this.script != null && this.script.size() > 0) {
			for(int i = 0; i < this.script.size(); i++) {
				element.appendChild(((Script)this.script.get(i)).element());
			}
		}
		if(this.top != null && this.top.size() > 0) {
			for(int i = 0; i < this.top.size(); i++) {
				element.appendChild(((Head)this.top.get(i)).element(Head.HEAD_TYPE_TOP));
			}
		}
		if(this.bottom != null && this.bottom.size() > 0) {
			for(int i = 0; i < this.bottom.size(); i++) {
				element.appendChild(((Head)this.bottom.get(i)).element(Head.HEAD_TYPE_BOTTOM));
			}
		}
		if(this.message != null && this.message.size() > 0) {
			XmlElement child = element.createElement("messages");
			for(int i = 0; i < this.message.size(); i++) {
				child.appendChild(((Message)this.message.get(i)).element());
			}
		}
		if(this.code != null && this.code.size() > 0) {
			XmlElement child = element.createElement("codes");
			for(int i = 0; i < this.code.size(); i++) {
				child.appendChild(((Code)this.code.get(i)).element());
			}
		}
		if(this.head != null && this.head.size() > 0) {
			for(int i = 0; i < this.head.size(); i++) {
				element.appendChild(((Head)this.head.get(i)).element(Head.HEAD_TYPE_HEAD));
			}
		}
		if(this.label != null && this.label.size() > 0) {
			XmlElement child = element.createElement("labels");
			for(int i = 0; i < this.label.size(); i++) {
				child.appendChild(((Label)this.label.get(i)).element(Label.LABEL_TYPE_LABEL));
			}
		}
		if(this.desc != null && this.desc.size() > 0) {
			XmlElement child = element.createElement("descs");
			for(int i = 0; i < this.desc.size(); i++) {
				child.appendChild(((Label)this.desc.get(i)).element(Label.LABEL_TYPE_DESC));
			}
		}
		if(this.author != null && this.author.size() > 0) {
			XmlElement child = element.createElement("authors");
			for(int i = 0; i < this.author.size(); i++) {
				child.appendChild(((Label)this.author.get(i)).element(Label.LABEL_TYPE_AUTHOR));
			}
		}
		if(this.keyword != null && this.keyword.size() > 0) {
			XmlElement child = element.createElement("keywords");
			for(int i = 0; i < this.keyword.size(); i++) {
				child.appendChild(((Label)this.keyword.get(i)).element(Label.LABEL_TYPE_KEYWORD));
			}
			element.appendChild(child);
		}
		return element;
	}
	private boolean contains(int headType, Head head, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Head> childHeads = null;
					if(headType == Head.HEAD_TYPE_HEAD) {
						childHeads = header.head;
					} else if(headType == Head.HEAD_TYPE_TOP) {
						childHeads = header.top;
					} else if(headType == Head.HEAD_TYPE_BOTTOM) {
						childHeads = header.bottom;
					}
					if(STR.valid(childHeads)) {
						for(int x = 0; x < childHeads.size(); x++) {
							Head childHead = (Head)childHeads.get(x);
							if(
								childHead != null &&
								STR.valid(childHead.getName()) &&
								STR.compareIgnoreCase(head.getName(), childHead.getName()) &&
								STR.trueValue(childHead.getOverride())
							) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	private boolean contains(Style style, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Style> childStyles = header.style;
					if(STR.valid(childStyles)) {
						for(int x = 0; x < childStyles.size(); x++) {
							Style childStyle = (Style)childStyles.get(x);
							if(
								childStyle != null &&
								STR.valid(childStyle.getName()) &&
								STR.compareIgnoreCase(style.getName(), childStyle.getName()) &&
								STR.trueValue(childStyle.getOverride())
							) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	private boolean contains(Script script, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Script> childScripts = header.script;
					if(STR.valid(childScripts)) {
						for(int x = 0; x < childScripts.size(); x++) {
							Script childScript = (Script)childScripts.get(x);
//							LOG.config(childScript.getName() + "\t" + childScript.getOverride() + "\t" + script.getName());
							if(
								childScript != null &&
								STR.valid(childScript.getName()) &&
								STR.compareIgnoreCase(script.getName(), childScript.getName()) &&
								STR.trueValue(childScript.getOverride())
							) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	protected Buffer styleToXSL(Record param, boolean rdf, Header... childHeaders) {
		if(STR.valid(this.style)) {
			Buffer xsl = new Buffer();
			for(int i = 0; i < this.style.size(); i++) {
				Style s = (Style)this.style.get(i);
				if(STR.valid(s.getName()) && this.contains(s, childHeaders)) {
//				if(STR.valid(s.getName()) && STR.trueValue(s.getOverride()) && this.contains(s, childHeaders)) {
				} else {
					xsl.append(s.toXSL(param, rdf));
				}
			}
			return xsl;
		}
		return null;
	}
	protected Buffer scriptToXSL(Record param, boolean rdf, Header... childHeaders) {
		if(STR.valid(this.script)) {
			Buffer xsl = new Buffer();
			for(int i = 0; i < this.script.size(); i++) {
				Script s = (Script)this.script.get(i);
				if(STR.valid(s.getName()) && this.contains(s, childHeaders)) {
//				if(STR.valid(s.getName()) && STR.trueValue(s.getOverride()) && this.contains(s, childHeaders)) {
				} else {
					xsl.append(s.toXSL(param, rdf));
				}
			}
			return xsl;
		}
		return null;
	}
	protected Buffer headToXSL(int headType, Record param, boolean rdf, Header... childHeaders) {
		List<Head> heads = null;
		if(headType == Head.HEAD_TYPE_HEAD) {
			heads = this.head;
		} else if(headType == Head.HEAD_TYPE_TOP) {
			heads = this.top;
		} else if(headType == Head.HEAD_TYPE_BOTTOM) {
			heads = this.bottom;
		}
		if(STR.valid(heads)) {
			Buffer xsl = new Buffer();
			for(int i = 0; i < heads.size(); i++) {
				Head head = (Head)heads.get(i);
				if(STR.valid(head.getName()) && this.contains(headType, head, childHeaders)) {
//				if(STR.valid(head.getName()) && STR.trueValue(head.getOverride()) && this.contains(headType, head, childHeaders)) {
				} else {
					xsl.append(head.toXSL(param, rdf));
				}
			}
			return xsl;
		}
		return null;
	}
	protected Buffer labelToXSL(int labelType, int labelPosition, Record param, String text, String xText, boolean rdf) {
		int indent = 0;
		int internalIndent = 0;
		List<Label> labels = null;
		if(labelType == Label.LABEL_TYPE_LABEL) {
			labels = this.label;
		} else if(labelType == Label.LABEL_TYPE_DESC) {
			labels = this.desc;
		} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
			labels = this.author;
		} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
			labels = this.keyword;
		}
		Buffer xsl = new Buffer();
		boolean exists = false;
		int index = 0;
		if(STR.valid(labels)) {
			for(int i = 0; i < labels.size(); i++) {
				Label label = (Label)labels.get(i);
				Buffer result = label.toXSL(labelType, labelPosition, param, indent + 1, rdf);
				if(result != null) {
					if(index == 0) {
						xsl.appendL(indent, "<xsl:choose>");
					}
					xsl.append(result);
					exists = true;
					index++;
				}
			}
		}
		if((rdf && STR.valid(xText)) || STR.valid(text)) {
			if(exists) {
				xsl.appendL(indent + 1, "<xsl:otherwise>");
				internalIndent = 2;
			}
			if(labelPosition == Label.LABEL_POSITION_HEAD) {
				if(labelType == Label.LABEL_TYPE_LABEL) {
					xsl.append(internalIndent, "<title>");
				} else {
					if(labelType == Label.LABEL_TYPE_DESC) {
						xsl.appendL(internalIndent, "<meta>");
						xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"name\">description</xsl:attribute>");
					} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
						xsl.appendL(internalIndent, "<meta>");
						xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"name\">author</xsl:attribute>");
					} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
						xsl.appendL(internalIndent, "<meta>");
						xsl.appendL(internalIndent + 1, "<xsl:attribute name=\"name\">keywords</xsl:attribute>");
					}
					xsl.append(internalIndent + 1, "<xsl:attribute name=\"content\">");
				}
			} else {
				if(labelType == Label.LABEL_TYPE_LABEL) {
					xsl.append(internalIndent, "<h2 class=\"title\">");
				} else if(labelType == Label.LABEL_TYPE_DESC) {
					xsl.append(internalIndent, "<div class=\"description\">");
				} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
					xsl.append(internalIndent, "<div class=\"author\">");
				} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
					xsl.append(internalIndent, "<div class=\"keyword\">");
				}
			}
			if(rdf && STR.valid(xText)) {
				xsl.append(TextParser.parseForXSL(xText, param, rdf));
			} if(STR.valid(text)) {
				xsl.append(TextParser.parseForXSL(text, param, rdf));
			}
			if(labelPosition == Label.LABEL_POSITION_HEAD) {
				if(labelType == Label.LABEL_TYPE_LABEL) {
					xsl.appendL("</title>");
				} else if(
					labelType == Label.LABEL_TYPE_DESC ||
					labelType == Label.LABEL_TYPE_AUTHOR ||
					labelType == Label.LABEL_TYPE_KEYWORD
				) {
					xsl.appendL("</xsl:attribute>");
					xsl.appendL(internalIndent, "</meta>");
				}
			} else {
				if(labelType == Label.LABEL_TYPE_LABEL) {
					xsl.appendL("</h2>");
				} else if(
					labelType == Label.LABEL_TYPE_DESC ||
					labelType == Label.LABEL_TYPE_AUTHOR ||
					labelType == Label.LABEL_TYPE_KEYWORD
				) {
					xsl.appendL("</div>");
				}
			}
			if(exists) {
				xsl.appendL(indent + 1, "</xsl:otherwise>");
			}
		}
		if(exists) {
			xsl.appendL(indent, "</xsl:choose>");
		}
		return xsl;
	}
	protected ConnectionFactory getConnectionFactory(Record params) {
		ConnectionFactory factory = null;
		if(STR.valid(this.getConnectionFactory())) {
			try {
				factory = (ConnectionFactory) Class.forName(this.getConnectionFactory()).getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				factory = null;
				LOG.severe(e);
			}
			if(factory != null) {
				if(this.jndi != null) {
					factory.setJndi(this.jndi);
				} else 	if(this.jdbc != null) {
					factory.setJdbc(this.jdbc, params);
				}
				return factory;
			}
		} else 	if(this.jndi != null) {
			return new GrahaConnectionFactoryImpl(this.jndi);
		} else 	if(this.jdbc != null) {
			return new GrahaConnectionFactoryImpl(this.jdbc, params);
		}
		return null;
	}
	protected ConnectionFactory getConnectionFactory(Record params, Header... parentHeaders) {
		ConnectionFactory factory = this.getConnectionFactory(params);
		if(factory != null) {
			return factory;
		}
		if(parentHeaders != null && parentHeaders.length > 0) {
			for(int i = 0; i < parentHeaders.length; i++) {
				if(parentHeaders[i] != null) {
					factory = parentHeaders[i].getConnectionFactory(params);
					if(factory != null) {
						return factory;
					}
				}
			}
		}
		return null;
	}
	private boolean contains(Message message, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Message> childMessages = header.message;
					if(STR.valid(childMessages)) {
						for(int x = 0; x < childMessages.size(); x++) {
							Message childMessage = (Message)childMessages.get(x);
							if(childMessage != null && STR.valid(childMessage.getName()) && STR.compareIgnoreCase(message.getName(), childMessage.getName())) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	protected void executeMessage(GDocument document, Record params, Header... childHeaders) {
		if(STR.valid(this.message)) {
			for(int i = 0; i < this.message.size(); i++) {
				Message m = this.message.get(i);
				if(STR.valid(m.getName())) {
					if(this.contains(m, childHeaders)) {
					} else {
						m.execute(params);
					}
				}
			}
		}
	}
	private boolean contains(Prop prop, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Prop> childProps = header.prop;
					if(STR.valid(childProps)) {
						for(int x = 0; x < childProps.size(); x++) {
							Prop childProp = (Prop)childProps.get(x);
							if(
								childProp != null &&
								STR.valid(childProp.getName()) &&
								STR.compareIgnoreCase(prop.getName(), childProp.getName()) &&
								STR.trueValue(childProp.getOverride())
							) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	protected void executeProp(Record params, int time, ConnectionFactory connectionFactory, Header... childHeaders) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.prop)) {
			for(int i = 0; i < this.prop.size(); i++) {
				Prop p = this.prop.get(i);
				if(STR.valid(p.getName())) {
					if(this.contains(p, childHeaders)) {
					} else {
						p.execute(params, time, connectionFactory);
					}
				}
			}
		}
	}
	private boolean contains(Code code, Header... childHeaders) {
		if(childHeaders != null && childHeaders.length > 0) {
			for(int i = 0; i < childHeaders.length; i++) {
				Header header = childHeaders[i];
				if(header != null) {
					List<Code> childCodes = header.code;
					if(STR.valid(childCodes)) {
						for(int x = 0; x < childCodes.size(); x++) {
							Code childCode = (Code)childCodes.get(x);
							if(childCode != null && STR.valid(childCode.getName()) && STR.compareIgnoreCase(code.getName(), childCode.getName())) {
								return true;
							}
						}
					}
				} else {
					LOG.config("header is null, index = " + i);
				}
			}
		}
		return false;
	}
	protected void executeCode(GDocument document, Record params, ConnectionFactory connectionFactory, Header... childHeaders) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.code)) {
			for(int i = 0; i < this.code.size(); i++) {
				Code c = this.code.get(i);
				if(STR.valid(c.getName())) {
					if(this.contains(c, childHeaders)) {
					} else {
						document.add(c.execute(params, connectionFactory));
					}
				}
			}
		}
	}
}
