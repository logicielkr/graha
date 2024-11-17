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

import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.TextParser;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.element.XmlElement;

/**
 * Graha(그라하) label 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Label {
	protected static final int LABEL_TYPE_LABEL = 1;
	protected static final int LABEL_TYPE_DESC = 2;
	protected static final int LABEL_TYPE_AUTHOR = 3;
	protected static final int LABEL_TYPE_KEYWORD = 4;
	
	protected static final int LABEL_POSITION_HEAD = 5;
	protected static final int LABEL_POSITION_BODY = 6;
	
	private Label() {
	}
	
	private String cond = null;
	private String text = null;
	private String xText = null;
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private String getText() {
		return this.text;
	}
	private void setText(String text) {
		this.text = text;
	}
	private String getXText() {
		return this.xText;
	}
	private void setXText(String xText) {
		this.xText = xText;
	}
	protected static String nodeName(int labelType) {
		if(labelType == Label.LABEL_TYPE_LABEL) {
			return "label";
		} else if(labelType == Label.LABEL_TYPE_DESC) {
			return "desc";
		} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
			return "author";
		} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
			return "keyword";
		}
		return "unknown";
	}
	protected static Label load(Node element) {
		Label label = new Label();
		if(element != null) {
			label.loadAttr(element);
			return label;
		}
		return null;
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
						if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "text")) {
							this.setText(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xText")) {
							this.setXText(node.getNodeValue());
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
	protected XmlElement element(int labelType) {
		XmlElement element = new XmlElement(Label.nodeName(labelType));
		element.setAttribute("cond", this.getCond());
		element.setAttribute("text", this.getText());
		element.setAttribute("xText", this.getXText());
		return element;
	}
	protected Buffer toXSL(int labelType, int labelPosition, Record param, int indent, boolean rdf) {
		AuthInfo authInfo = null;
		if(STR.valid(this.getCond())) {
			authInfo = AuthUtility.parse(this.getCond());
		}
		if(authInfo != null && AuthUtility.testInServer(authInfo, param)) {
			if(!AuthUtility.auth(authInfo, param)) {
				return null;
			} else {
				authInfo = null;
			}
		}
		Buffer xsl = new Buffer();
		if(authInfo != null) {
			xsl.appendL(indent, "<xsl:when test=\"" + AuthUtility.testExpr(authInfo, param, rdf) + "\">");
		} else {
			xsl.appendL(indent, "<xsl:when test=\"1\">");
		}
		if(labelPosition == Label.LABEL_POSITION_HEAD) {
			if(labelType == Label.LABEL_TYPE_LABEL) {
				xsl.append(indent + 1, "<title>");
			} else {
				if(labelType == Label.LABEL_TYPE_DESC) {
					xsl.appendL(indent + 1, "<meta>");
					xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">description</xsl:attribute>");
				} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
					xsl.appendL(indent + 1, "<meta>");
					xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">author</xsl:attribute>");
				} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
					xsl.appendL(indent + 1, "<meta>");
					xsl.appendL(indent + 2, "<xsl:attribute name=\"name\">keywords</xsl:attribute>");
				}
				xsl.append(indent + 2, "<xsl:attribute name=\"content\">");
			}
		} else {
			if(labelType == Label.LABEL_TYPE_LABEL) {
				xsl.append(indent + 1, "<h2 class=\"title\">");
			} else if(labelType == Label.LABEL_TYPE_DESC) {
				xsl.append(indent + 1, "<div class=\"description\">");
			} else if(labelType == Label.LABEL_TYPE_AUTHOR) {
				xsl.append(indent + 1, "<div class=\"author\">");
			} else if(labelType == Label.LABEL_TYPE_KEYWORD) {
				xsl.append(indent + 1, "<div class=\"keyword\">");
			}
		}
		if(rdf && STR.valid(this.getXText())) {
			xsl.append(TextParser.parseForXSL(this.getXText(), param, rdf));
		} else if(STR.valid(this.getText())) {
			xsl.append(TextParser.parseForXSL(this.getText(), param, rdf));
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
				xsl.appendL(indent + 1, "</meta>");
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
		xsl.appendL(indent, "</xsl:when>");
		return xsl;
	}
}
