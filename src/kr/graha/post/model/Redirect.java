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
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.xml.GDocument;
import kr.graha.post.xml.GRedirect;
import kr.graha.post.model.utility.TextParser;

/**
 * Graha(그라하) redirect 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Redirect {
	private static final String nodeName = "redirect";
	private Redirect() {
	}
	
	private String path = null;
	private String cond = null;
	private String autoredirect = null;
	private List<LinkParam> param = null;
	private List<Msg> msgs = null;
	private String msg = null;
	private String label = null;
	private String type = null;
	
	private String getPath() {
		return this.path;
	}
	private void setPath(String path) {
		this.path = path;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getAutoredirect() {
		return this.autoredirect;
	}
	private void setAutoredirect(String autoredirect) {
		this.autoredirect = autoredirect;
	}
	private String getMsg() {
		return this.msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type = type;
	}
	private void add(LinkParam param) {
		if(this.param == null) {
			this.param = new ArrayList<LinkParam>();
		}
		this.param.add(param);
	}
	private void add(Msg msg) {
		if(this.msgs == null) {
			this.msgs = new ArrayList<Msg>();
		}
		this.msgs.add(msg);
	}
	protected static String nodeName() {
		return Redirect.nodeName;
	}
	protected static Redirect load(Element element) {
		Redirect redirect = new Redirect();
		if(element != null) {
			redirect.loadAttr(element);
			redirect.loadElement(element);
			return redirect;
		}
		return null;
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
		if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(LinkParam.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
			this.add(Msg.load((Element)node));
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
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "params")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msgs")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "envelop")) {
							this.loadElement(node);
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
						if(STR.compareIgnoreCase(node.getNodeName(), "path")) {
							this.setPath(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "autoredirect")) {
							this.setAutoredirect(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.setMsg(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
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
		XmlElement element = new XmlElement(Redirect.nodeName());
		element.setAttribute("path", this.getPath());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("autoredirect", this.getAutoredirect());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("type", this.getType());
		element.setAttribute("msg", this.getMsg());
		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((LinkParam)this.param.get(i)).element());
			}
		}
		if(this.msgs != null && this.msgs.size() > 0) {
			for(int i = 0; i < this.msgs.size(); i++) {
				element.appendChild(((Msg)this.msgs.get(i)).element());
			}
		}
		return element;
	}
	protected void execute(GDocument document, Record param) {
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo == null || AuthUtility.auth(authInfo, param)) {
			String label = null;
			if(STR.valid(this.getLabel())) {
				label = TextParser.parse(this.getLabel(), param);
			}
			GRedirect redirect = new GRedirect(Link.getPath(this.getPath(), param), !STR.falseValue(this.getAutoredirect()), label, this.getType());
			if(STR.valid(this.param)) {
				for(int i = 0; i < this.param.size(); i++) {
					((LinkParam)this.param.get(i)).execute(redirect, document, param);
				}
			}
			if(STR.valid(this.getMsg())) {
				redirect.add(TextParser.parse(this.getMsg(), param));
			}
			if(STR.valid(this.msgs)) {
				for(int i = 0; i < this.msgs.size(); i++) {
					((Msg)this.msgs.get(i)).execute(redirect, param);
				}
			}
			document.add(redirect);
		}
	}
}
