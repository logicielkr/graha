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
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

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
		element.setAttribute("path", this.getPath());
		element.setAttribute("cond", this.getCond());
		element.setAttribute("autoredirect", this.getAutoredirect());
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
	private void form(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf, Buffer xsl) {
		xsl.appendL(indent, "<form>");
		xsl.appendL(indent + 1, "<xsl:attribute name=\"method\">get</xsl:attribute>");
		xsl.appendL(indent + 1, "<xsl:attribute name=\"id\">_post</xsl:attribute>");
		xsl.appendL(indent + 1, "<xsl:attribute name=\"action\">" + Link.getPath(this.getPath(), param) + "</xsl:attribute>");
		if(STR.valid(this.param)) {
			for(int i = 0; i < this.param.size(); i++) {
				xsl.append(((LinkParam)this.param.get(i)).hidden(tables, commands, indent + 1, rdf));
			}
		}
		if(!STR.valid(this.getAutoredirect()) || !STR.falseValue(this.getAutoredirect())) {
			xsl.appendL(indent + 1, "<noscript>웹브라우저에서 Javascript 가 동작하지 않도록 설정되어 있습니다.  다음으로 이동하려면 확인 버튼을 클릭하시기 바랍니다.</noscript>");
		}
		if(STR.valid(this.msgs)) {
			for(int i = 0; i < this.msgs.size(); i++) {
				xsl.append(((Msg)this.msgs.get(i)).toXSL(indent + 1));
			}
		}
		xsl.appendL(indent + 1, "<input type=\"submit\" value=\"확인\" />");
		xsl.appendL(indent, "</form>");
	}
	protected Buffer toXSL(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf) {
		Buffer xsl = new Buffer();
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
			if(AuthUtility.auth(authInfo, param)) {
				xsl.appendL(indent, "<xsl:when test=\"1\">");
				this.form(tables, commands, param, indent + 1, rdf, xsl);
				xsl.appendL(indent, "</xsl:when>");
			}
		} else {
			if(authInfo != null) {
				xsl.appendL(indent, "<xsl:when test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
			} else {
				xsl.appendL(indent, "<xsl:when test=\"1\">");
			}
			this.form(tables, commands, param, indent + 1, rdf, xsl);
			xsl.appendL(indent, "</xsl:when>");
		}
		return xsl;
	}
}
