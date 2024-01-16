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
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import java.security.NoSuchProviderException;
import java.sql.SQLException;

/**
 * Graha(그라하) validation 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Validation {
	private static final String nodeName = "validation";
	private Validation() {
	}
	
	private String method = null;

	private List<ValidationParam> param = null;
	private List<ValidationCommand> command = null;

	private String getMethod() {
		return this.method;
	}
	private void setMethod(String method) {
		this.method = method;
	}
	private void add(ValidationParam param) {
		if(this.param == null) {
			this.param = new ArrayList<ValidationParam>();
		}
		this.param.add(param);
	}
	private void add(ValidationCommand command) {
		if(this.command == null) {
			this.command = new ArrayList<ValidationCommand>();
		}
		this.command.add(command);
	}
	protected static String nodeName() {
		return Validation.nodeName;
	}
	protected static Validation load(Element element) {
		Validation validation = new Validation();
		if(element != null) {
			validation.loadAttr(element);
			validation.loadElement(element);
			return validation;
		}
		return null;
	}
	private void load(Node node, String parentNodeName) {
		if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(ValidationParam.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "command")) {
			this.add(ValidationCommand.load((Element)node));
		} else {
			LOG.warning("invalid nodeName(" + node.getNodeName() + ")"); 
		}
	}
	private void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "command")) {
							this.load(node, null);
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
	private void loadAttr(Node element) {
		NamedNodeMap nnm = element.getAttributes();
		if(nnm != null && nnm.getLength() > 0) {
			for(int i = 0; i < nnm.getLength(); i++) {
				Node node = nnm.item(i);
				if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
					if(
						STR.valid(node.getNodeName()) &&
						STR.valid(node.getNodeValue())
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "method")) {
							this.setMethod(node.getNodeValue());
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
	protected XmlElement element() {
		XmlElement element = new XmlElement(this.nodeName());
		element.setAttribute("method", this.getMethod());
		if(STR.valid(this.param)) {
			for(int i = 0; i < this.param.size(); i++) {
				Object obj = this.param.get(i);
				if(obj instanceof ValidationParam) {
					element.appendChild(((ValidationParam)obj).element());
				} else if(obj instanceof ValidationCommand) {
					element.appendChild(((ValidationCommand)obj).element());
				}
			}
		}
		return element;
	}
	protected Buffer toXSL(Record param, int indent, boolean rdf) {
		Buffer xsl = new Buffer();
		if(STR.valid(this.param)) {
			for(int i = 0; i < this.param.size(); i++) {
				Object obj = this.param.get(i);
				if(obj instanceof ValidationParam) {
					xsl.append(((ValidationParam)obj).toXSL(param, indent, rdf));
				} else if(obj instanceof ValidationCommand) {
					xsl.append(((ValidationCommand)obj).toValidationXSL(indent));
				}
			}
		}
		return xsl;
	}
	protected void validate(Record params, ConnectionFactory connectionFactory, List<String> msgs) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.getMethod())) {
			if(!params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), this.getMethod())) {
				return;
			}
		}
		if(STR.valid(this.param)) {
			for(int i = 0; i < this.param.size(); i++) {
				ValidationParam v = (ValidationParam)this.param.get(i);
				v.validate(params, msgs);
			}
		}
		if(STR.valid(this.command)) {
			for(int i = 0; i < this.command.size(); i++) {
				ValidationCommand v = (ValidationCommand)this.command.get(i);
				v.validate(params, connectionFactory, msgs);
			}
		}
	}
}
