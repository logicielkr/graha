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
import java.util.Iterator;
import kr.graha.post.element.XmlElement;
import kr.graha.post.lib.Key;

/**
 * Graha(그라하) layout 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Layout {
	private static final String nodeName = "layout";
	private Layout() {
	}
	
	private String template = null;
	private String href = null;
	private String msg = null;
	private String textContent = null;
	private List topLeft = null;
	private List topCenter = null;
	private List topRight = null;
	private List<Tab> _tab = null;
	private List bottomLeft = null;
	private List bottomCenter = null;
	private List bottomRight = null;
	private String htmltype = null;

	protected String getTemplate() {
		return this.template;
	}
	private void setTemplate(String template) {
		this.template = template;
	}
	protected String getHref() {
		return this.href;
	}
	private void setHref(String href) {
		this.href = href;
	}
	private String getMsg() {
		return this.msg;
	}
	private void setMsg(String msg) {
		this.msg = msg;
	}
	private String getTextContent() {
		return this.textContent;
	}
	private void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	private String getHtmltype() {
		return this.htmltype;
	}
	private void setHtmltype(String htmltype) {
		this.htmltype = htmltype;
	}
	private boolean div(boolean htmlType) {
		if(STR.valid(this.htmltype)) {
			return QueryImpl.div(this.getHtmltype());
		}
		return htmlType;
	}
	protected List<Tab> getTab() {
		return this._tab;
	}
	private int getTabSize(Record params) {
		int result = 0;
		if(this.getTab() != null) {
			for(int i = 0; i < this.getTab().size(); i++) {
				if(((Tab)this.getTab().get(i)).valid(params)) {
					result++;
				}
			}
		}
		return result;
	}
	private void addTopLeft(Object obj) {
		if(this.topLeft == null) {
			this.topLeft = new ArrayList();
		}
		this.topLeft.add(obj);
	}
	private void addTopCenter(Object obj) {
		if(this.topCenter == null) {
			this.topCenter = new ArrayList();
		}
		this.topCenter.add(obj);
	}
	private void addTopRight(Object obj) {
		if(this.topRight == null) {
			this.topRight = new ArrayList();
		}
		this.topRight.add(obj);
	}
	private void addBottomLeft(Object obj) {
		if(this.bottomLeft == null) {
			this.bottomLeft = new ArrayList();
		}
		this.bottomLeft.add(obj);
	}
	private void addBottomCenter(Object obj) {
		if(this.bottomCenter == null) {
			this.bottomCenter = new ArrayList();
		}
		this.bottomCenter.add(obj);
	}
	private void addBottomRight(Object obj) {
		if(this.bottomRight == null) {
			this.bottomRight = new ArrayList();
		}
		this.bottomRight.add(obj);
	}
	private void add(Tab tab) {
		if(this._tab == null) {
			this._tab = new ArrayList<Tab>();
		}
		this._tab.add(tab);
	}
	protected static String nodeName() {
		return Layout.nodeName;
	}
	protected static Layout load(Element element) {
		Layout layout = new Layout();
		if(element != null) {
			layout.loadAttr(element);
			if(STR.compareIgnoreCase(layout.getTemplate(), "native")) {
				layout.setTextContent(element.getTextContent());
			} else {
				layout.loadElement(element);
			}
			return layout;
		}
		return null;
	}
	private void loads(Node element, String parentNodeName) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(parentNodeName == null) {
						if(STR.compareIgnoreCase(node.getNodeName(), "tabs")) {
							loads(node, parentNodeName);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "tab")) {
							load(node, parentNodeName);
						} else {
							LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
						}
					} else {
						if(STR.vexistsIgnoreCase(node.getNodeName(), "left", "center", "right")) {
							loads(node, parentNodeName + "/" + node.getNodeName());
						} else {
							load(node, parentNodeName);
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
					if(STR.vexistsIgnoreCase(parentNodeName, "top/left", "top/center", "top/right", "bottom/left", "bottom/center", "bottom/right")) {
						if(STR.valid(node.getTextContent()) && STR.compareIgnoreCase(node.getTextContent().trim(), "page")) {
							if(STR.compareIgnoreCase(parentNodeName, "top/left")) {
								this.addTopLeft("page");
							} else if(STR.compareIgnoreCase(parentNodeName, "top/center")) {
								this.addTopCenter("page");
							} else if(STR.compareIgnoreCase(parentNodeName, "top/right")) {
								this.addTopRight("page");
							} else 	if(STR.compareIgnoreCase(parentNodeName, "bottom/left")) {
								this.addBottomLeft("page");
							} else if(STR.compareIgnoreCase(parentNodeName, "bottom/center")) {
								this.addBottomCenter("page");
							} else if(STR.compareIgnoreCase(parentNodeName, "bottom/right")) {
								this.addBottomRight("page");
							}
						}
					}
				}
			}
		}
	}
	private void load(Node node, String parentNodeName) {
		if(
			parentNodeName == null ||
			STR.compareIgnoreCase(node.getNodeName(), "tab")
		) {
			this.add(Tab.load((Element)node));
		} else {
			if(STR.compareIgnoreCase(node.getNodeName(), "search")) {
				if(STR.compareIgnoreCase(parentNodeName, "top/left")) {
					this.addTopLeft(Search.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "top/center")) {
					this.addTopCenter(Search.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "top/right")) {
					this.addTopRight(Search.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/left")) {
					this.addBottomLeft(Search.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/center")) {
					this.addBottomCenter(Search.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/right")) {
					this.addBottomRight(Search.load((Element)node));
				} else {
					LOG.warning("invalid parentNodeName(" + parentNodeName + ")");
				}
			} else if(STR.compareIgnoreCase(node.getNodeName(), "link")) {
				if(STR.compareIgnoreCase(parentNodeName, "top/left")) {
					this.addTopLeft(Link.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "top/center")) {
					this.addTopCenter(Link.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "top/right")) {
					this.addTopRight(Link.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/left")) {
					this.addBottomLeft(Link.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/center")) {
					this.addBottomCenter(Link.load((Element)node));
				} else if(STR.compareIgnoreCase(parentNodeName, "bottom/right")) {
					this.addBottomRight(Link.load((Element)node));
				} else {
					LOG.warning("invalid parentNodeName(" + parentNodeName + ")");
				}
			} else {
				LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
			}
		}
	}
	private void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "top")) {
							this.loads(node, "top");
						} else if(STR.compareIgnoreCase(node.getNodeName(), "middle")) {
							this.loads(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "bottom")) {
							this.loads(node, "bottom");
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
						if(STR.compareIgnoreCase(node.getNodeName(), "template")) {
							this.setTemplate(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "href")) {
							this.setHref(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "msg")) {
							this.setMsg(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "htmltype")) {
							this.setHtmltype(node.getNodeValue());
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
		XmlElement element = new XmlElement(Layout.nodeName());
		element.setAttribute("template", this.getTemplate());
		element.setAttribute("href", this.getHref());
		element.setAttribute("msg", this.getMsg());
		element.setAttribute("htmltype", this.getHtmltype());
		if(
			this.topLeft != null ||
			this.topCenter != null ||
			this.topRight != null
		) {
			XmlElement top = element.createElement("top");
			top.appendChild(this.element(this.topLeft, "left"));
			top.appendChild(this.element(this.topCenter, "center"));
			top.appendChild(this.element(this.topRight, "right"));
		}
		if(this.getTab() != null && this.getTab().size() > 0) {
			for(int i = 0; i < this.getTab().size(); i++) {
				element.appendChild(((Tab)this.getTab().get(i)).element());
			}
		}
		if(
			this.bottomLeft != null ||
			this.bottomCenter != null ||
			this.bottomRight != null
		) {
			XmlElement bottom = element.createElement("bottom");
			bottom.appendChild(this.element(this.bottomLeft, "left"));
			bottom.appendChild(this.element(this.bottomCenter, "center"));
			bottom.appendChild(this.element(this.bottomRight, "right"));
		}
		return element;
	}
	private XmlElement element(List list, String childName) {
		if(list != null && list.size() > 0) {
			XmlElement child = new XmlElement(childName);
			for(int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if(obj instanceof Search) {
					child.appendChild(((Search)obj).element());
				} else if(obj instanceof Link) {
					child.appendChild(((Link)obj).element());
				} else if(obj instanceof String) {
					child.appendChild((String)obj);
				}
			}
			return child;
		}
		return null;
	}
	private void multitab(Files files, Record param, int indent, boolean rdf, Buffer xsl) {
		if(this.getTab() != null && this.getTabSize(param) > 1) {
			xsl.appendL(indent, "<ul class=\"multitab\">");
			for(int i = 0; i < this.getTab().size(); i++) {
				if(((Tab)this.getTab().get(i)).valid(param)) {
/*
					if(files != null) {
						xsl.append(files.beforeLi(param, indent, ((Tab)this.getTab().get(i)).getName(), rdf));
					}
*/
					xsl.append(((Tab)this.getTab().get(i)).li(files, param, indent, rdf));
/*
					if(files != null) {
						xsl.append(files.afterLi(param, indent, ((Tab)this.getTab().get(i)).getName(), rdf));
					}
*/
				}
			}
			if(files != null) {
				xsl.append(files.li(param, indent, rdf));
			}
			xsl.appendL(indent, "</ul>");
			xsl.appendL(indent, "<div class=\"space\" />");
		}
	}
	private void hidden(List<Table> tables, List<Command> commands, Record param, int indent, boolean rdf, Buffer xsl) {
		Link fullLink = this.findFullLink();
		if(fullLink != null) {
			List<LinkParam> params = fullLink.getParam();
			if(params != null) {
				for(int i = 0; i < params.size(); i++) {
					xsl.append(((LinkParam)params.get(i)).hidden(tables, commands, false, indent + 1, rdf));
				}
			}
		}
	}
	private void table(
		List<Table> tables,
		List<Command> commands,
		Files files,
		Record param, 
		int indent, 
		boolean rdf,
		boolean div,
		String queryId,
		int queryFuncType,
		Buffer xsl
	) {
		if(this.getTab() != null && this.getTabSize(param) > 0) {
			for(int i = 0; i < this.getTab().size(); i++) {
				if(((Tab)this.getTab().get(i)).valid(param)) {
/*
					if(files != null) {
						xsl.append(files.before(param, indent, ((Tab)this.getTab().get(i)).getName(), rdf, queryId, queryFuncType, (this.getTabSize(param) > 1)));
					}
*/
					Table table = null;
					Command command = null;
					if(
						queryFuncType == Query.QUERY_FUNC_TYPE_LIST ||
						queryFuncType == Query.QUERY_FUNC_TYPE_LISTALL ||
						queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL
					) {
						if(commands != null && commands.size() > 0) {
							for(int x = 0; x < commands.size(); x++) {
								if(((Command)commands.get(x)).valid(param) && STR.compareIgnoreCase(((Tab)this.getTab().get(i)).getName(), ((Command)commands.get(x)).getName())) {
									command = (Command)commands.get(x);
								}
							}
						}
					} else if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
						if(tables != null && tables.size() > 0) {
							for(int x = 0; x < tables.size(); x++) {
								if(((Table)tables.get(x)).valid(param) && STR.compareIgnoreCase(((Tab)this.getTab().get(i)).getName(), ((Table)tables.get(x)).getName())) {
									table = (Table)tables.get(x);
								}
							}
						}
					}
					xsl.append(this.getTab().get(i).toXSL(files, param, indent, rdf, div, queryId, queryFuncType, table, command, (this.getTabSize(param) > 1)));
/*
					if(files != null) {
						xsl.append(files.after(param, indent, ((Tab)this.getTab().get(i)).getName(), rdf, queryId, queryFuncType, (this.getTabSize(param) > 1)));
					}
*/
				}
			}
			if(files != null) {
				xsl.append(files.file(param, indent, rdf, queryId, queryFuncType, (this.getTabSize(param) > 1)));
			}
		}
	}
	private boolean fileAllow(Files files, Record param) {
		if(files != null) {
			return files.fileAllow(param);
		}
		return false;
	}
	private Link findFullLink(List list) {
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if(obj instanceof Link) {
					Link link = (Link)obj;
					if(STR.trueValue(link.getFull())) {
						return link;
					}
				}
			}
		}
		return null;
	}
	private Link findFullLink() {
		Link link = null;
		link = this.findFullLink(topLeft);
		if(link != null) {
			return link;
		}
		link = this.findFullLink(topCenter);
		if(link != null) {
			return link;
		}
		link = this.findFullLink(topRight);
		if(link != null) {
			return link;
		}
		link = this.findFullLink(bottomLeft);
		if(link != null) {
			return link;
		}
		link = this.findFullLink(bottomCenter);
		if(link != null) {
			return link;
		}
		link = this.findFullLink(bottomRight);
		if(link != null) {
			return link;
		}
		return null;
	}
	protected Buffer toXSL(
		Header root,
		Header extend,
		Header header,
		List<Table> tables,
		List<Command> commands,
		Files files,
		Record param,
		int indent,
		boolean rdf,
		boolean parentDiv,
		String queryId,
		int queryFuncType
	) {
		Buffer xsl = new Buffer();
		if(STR.compareIgnoreCase(this.getTemplate(), "native")) {
			xsl.append(this.getTextContent());
		} else 	if(this.getTab() != null && this.getTabSize(param) > 0) {
			Header.headToXSL(
				extend,
				root,
				header,
				Head.HEAD_TYPE_TOP,
				Head.HEAD_POSITION_UNDER_THE_TITLE,
				param,
				rdf,
				xsl
			);
			this.top(tables, commands, param, rdf, queryId, xsl);
			Header.headToXSL(
				extend,
				root,
				header,
				Head.HEAD_TYPE_TOP,
				Head.HEAD_POSITION_ABOVE_THE_MULTITAB,
				param,
				rdf,
				xsl
			);
			this.multitab(files, param, indent, rdf, xsl);
			Header.headToXSL(
				extend,
				root,
				header,
				Head.HEAD_TYPE_TOP,
				Head.HEAD_POSITION_ABOVE_THE_TABLE,
				param,
				rdf,
				xsl
			);
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				xsl.appendL("<form>");
				if(this.fileAllow(files, param)) {
					xsl.appendL(indent + 1, "<xsl:attribute name=\"enctype\">multipart/form-data</xsl:attribute>");
				}
				xsl.appendL(indent + 1, "<xsl:attribute name=\"name\">" + queryId + "</xsl:attribute>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"id\">" + queryId + "</xsl:attribute>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"action\">" + Link.getPath(queryId, param, rdf) + "</xsl:attribute>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"method\">post</xsl:attribute>");
				xsl.appendL(indent + 1, "<xsl:attribute name=\"onsubmit\">return (document.getElementById('" + queryId + "_submit').form == null) || check_submit(this, '" + this.getMsg() + "');</xsl:attribute>");
				this.hidden(tables, commands, param, indent, rdf, xsl);
			}
			this.table(tables, commands, files, param, indent, rdf, this.div(parentDiv), queryId, queryFuncType, xsl);
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				xsl.append("</form>");
			}
			Header.headToXSL(
				extend,
				root,
				header,
				Head.HEAD_TYPE_TOP,
				Head.HEAD_POSITION_UNDER_THE_TABLE,
				param,
				rdf,
				xsl
			);
			this.bottom(tables, commands, param, rdf, queryId, xsl);
		}
		return xsl;
	}
	private void top(List<Table> tables, List<Command> commands, Record param, boolean rdf, String queryId, Buffer xsl) {
		int indent = 0;
		xsl.appendL(indent, "<div class=\"nav top\">");
		if(STR.valid(this.topLeft)) {
			xsl.appendL(indent + 1, "<div class=\"box left\">");
			this.buttons(tables, commands, param, this.topLeft, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box left\" />");
		}
		if(STR.valid(this.topCenter)) {
			xsl.appendL(indent + 1, "<div class=\"box center\">");
			this.buttons(tables, commands, param, this.topCenter, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box center\" />");
		}
		if(STR.valid(this.topRight)) {
			xsl.appendL(indent + 1, "<div class=\"box right\">");
			this.buttons(tables, commands, param, this.topRight, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box right\" />");
		}
		xsl.appendL(indent, "</div>");
	}
	private void bottom(List<Table> tables, List<Command> commands, Record param, boolean rdf, String queryId, Buffer xsl) {
		int indent = 0;
		xsl.appendL(indent, "<div class=\"nav bottom\">");
		if(STR.valid(this.bottomLeft)) {
			xsl.appendL(indent + 1, "<div class=\"box left\">");
			this.buttons(tables, commands, param, this.bottomLeft, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box left\" />");
		}
		if(STR.valid(this.bottomCenter)) {
			xsl.appendL(indent + 1, "<div class=\"box center\">");
			this.buttons(tables, commands, param, this.bottomCenter, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box center\" />");
		}
		if(STR.valid(this.bottomRight)) {
			xsl.appendL(indent + 1, "<div class=\"box right\">");
			this.buttons(tables, commands, param, this.bottomRight, indent + 2, rdf, queryId, xsl);
			xsl.appendL(indent + 1, "</div>");
		} else {
			xsl.appendL(indent + 1, "<div class=\"box right\" />");
		}
		xsl.appendL(indent, "</div>");
	}
	private void buttons(List<Table> tables, List<Command> commands, Record param, List list, int indent, boolean rdf, String queryId, Buffer xsl) {
		if(STR.valid(list)) {
			for(int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if(obj instanceof Search) {
					xsl.append(((Search)obj).toXSL(tables, commands, param, indent, rdf));
				} else if(obj instanceof Link) {
					xsl.append(((Link)obj).button(tables, commands, param, indent, rdf, queryId, this.getMsg()));
				} else if(obj instanceof String) {
					if(STR.compareIgnoreCase(((String)obj).trim(), "page")) {
						this.page(param, indent, rdf, xsl);
					}
				}
			}
		}
	}
	private void page(Record param, int indent, boolean rdf, Buffer xsl) {
		xsl.appendL(indent, "<ul class=\"pages\">");
		xsl.appendL(indent + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GPages.nodePath(rdf) + "\">");
		xsl.appendL(indent + 2, "<xsl:choose>");
		xsl.appendL(indent + 3, "<xsl:when test=\"" + kr.graha.post.xml.GParam.childNodePath("param", "page", rdf) + " = " + kr.graha.post.xml.GPages.childNodeName("no", rdf) + "\">");
		xsl.append(indent + 4, "<li class=\"page selected\">");
		xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GPages.childNodeName("text", rdf) + "\" />");
		xsl.appendL("</li>");
		xsl.appendL(indent + 3, "</xsl:when>");
		xsl.appendL(indent + 3, "<xsl:otherwise>");
		xsl.appendL(indent + 4, "<li class=\"page\">");
		xsl.appendL(indent + 5, "<a>");
		xsl.append(indent + 6, "<xsl:attribute name=\"href\">?page=");
		xsl.append("<xsl:value-of select=\"" + kr.graha.post.xml.GPages.childNodeName("no", rdf) + "\" />");
		if(param != null && !param.isEmpty()) {
			Iterator<Key> it = param.keySet().iterator();
			while(it.hasNext()) {
				Key key = (Key)it.next();
				if(
					key.getPrefix() == Record.PREFIX_TYPE_PARAM &&
					!key.equals(Record.key(Record.PREFIX_TYPE_PARAM, "page"))
				) {
					xsl.append("&amp;" + key.getKey() + "=<xsl:value-of select=\"" + kr.graha.post.xml.GParam.childNodePath("linkparam", key.getKey(), rdf) + "\" />");
				}
			}
		}
		xsl.appendL("</xsl:attribute>");
		xsl.appendL(indent + 6, "<xsl:value-of select=\"" + kr.graha.post.xml.GPages.childNodeName("text", rdf) + "\" />");
		xsl.appendL(indent + 5, "</a>");
		xsl.appendL(indent + 4, "</li>");
		xsl.appendL(indent + 3, "</xsl:otherwise>");
		xsl.appendL(indent + 2, "</xsl:choose>");
		xsl.appendL(indent + 1, "</xsl:for-each>");
		xsl.appendL(indent, "</ul>");
	}
}
