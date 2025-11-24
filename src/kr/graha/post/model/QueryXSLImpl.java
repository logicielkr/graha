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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Graha(그라하) Query 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class QueryXSLImpl extends QueryImpl {
	protected QueryXSLImpl(Header rootHeader, Header extendHeader) {
		super(rootHeader, extendHeader);
	}
	public int xsl(
		HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig, Record params
	) throws UnsupportedEncodingException, IOException, ServletException {
		super.prepareUsingServletFileUpload(request, servletConfig, params);
		if(!super.auth(params)) {
			return HttpServletResponse.SC_FORBIDDEN;
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xsl; charset=UTF-8");
//		response.setContentType("application/xslt+xml; charset=UTF-8");
		if(request.getParameter("method") != null && request.getParameter("method").equals("post")) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST");
		} else if(request.getParameter("method") != null && request.getParameter("method").equals("error")) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "ERROR");
		} else if(request.getParameter("method") != null && request.getParameter("method").equals("query")) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "QUERY");
		}
		try {
			Buffer buffer = this.toXSL(params, request, 0);
//			response.getWriter().append(buffer.toStringBuffer());
			response.getWriter().append(buffer.toCharSequence());
		} catch (IOException e) {
			LOG.severe(e);
		}
		return HttpServletResponse.SC_OK;
	}
	protected void params(Record param, boolean div, boolean rdf) {
		if(rdf) {
			param.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "output"), "rdf");
		} else {
			param.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "output"), "xml");
		}
		if(div) {
			param.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "htmltype"), "div");
		} else {
			param.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "htmltype"), "table");
		}
	}
	protected void params(Record param) {
		boolean div = super.div();
		boolean rdf = super.rdf();
		this.params(param, div, rdf);
	}
	protected Buffer toXSL(Record param, HttpServletRequest request, int indent) {
		boolean div = super.div();
		boolean rdf = super.rdf();
		this.params(param, div, rdf);
		Buffer xsl = new Buffer();
		if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "ERROR")) {
			this.error(param, request, indent, rdf, xsl);
		} else if(
			param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET") ||
			param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "QUERY")
		) {
			this.before(param, super.getId(), request, indent, rdf, xsl);
			if(super.getLayout() != null) {
				xsl.append(super.getLayout().toXSL(super.getRootHeader(), super.getExtendHeader(), super.getHeader(), super.getTable(), super.getCommand(), super.getFiles(), param, indent, rdf, div, super.getId(), super.getQueryFuncType()));
			}
			this.after(param, indent, rdf, xsl);
		} else if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
			this.post(param, request, indent, rdf, xsl);
		}
		return xsl;
	}
	private void before(Record param, String queryId, HttpServletRequest request, int indent, boolean rdf, Buffer xsl) {
		this.html(param, request, indent, rdf, xsl);
		this.head(param, indent, rdf, xsl);
		xsl.appendL(indent, "<xsl:if test=\"" + kr.graha.post.xml.GMessage.nodePath(rdf) + "\">");
		xsl.appendL(indent + 2, "<script>");
		xsl.appendL(indent + 3, "var _messages = new Array();");
		xsl.appendL(indent + 3, "<xsl:for-each select=\"" + kr.graha.post.xml.GMessage.nodePath(rdf) + "\">");
		xsl.appendL(indent + 4, "_messages.push({");
		xsl.appendL(indent + 5, "name:\"<xsl:value-of select=\"" + kr.graha.post.xml.GMessage.childNodeName("name", rdf) + "\" />\",");
		xsl.appendL(indent + 5, "label:\"<xsl:value-of select=\"" + kr.graha.post.xml.GMessage.childNodeName("label", rdf) + "\" />\"");
		xsl.appendL(indent + 4, "});");
		xsl.appendL(indent + 3, "</xsl:for-each>");
		xsl.appendL(indent + 2, "</script>");
		xsl.appendL(indent, "</xsl:if>");
		this.css(param, indent, rdf, xsl);
		this.script(param, indent, rdf, xsl);
		if(STR.valid(super.getCalculator())) {
			xsl.appendL("<script>");
			xsl.appendL("GrahaFormula.expr = [");
			for(int i = 0; i < super.getCalculator().size(); i++) {
				if(i > 0) {
					xsl.appendL(indent + 1, ",{");
				} else {
					xsl.appendL(indent + 1, "{");
				}
				xsl.append(((CalculatorParam)super.getCalculator().get(i)).toXSL(param, queryId, indent + 2, rdf));
				xsl.appendL(indent + 1, "}");
			}
			xsl.appendL("];");
			xsl.appendL("GrahaFormula.addEvent(document, GrahaFormula.ready, \"ready\");");
			xsl.appendL("</script>");
		}
		if(STR.valid(super.getValidation())) {
			xsl.appendL(indent, "<script>");
			xsl.appendL(indent, "function _check(form, out) {");
			xsl.appendL(indent + 1, "var result = true;");
			xsl.appendL(indent + 1, "var index;");
			for(int i = 0; i < super.getValidation().size(); i++) {
				xsl.append(((Validation)super.getValidation().get(i)).toXSL(param, indent + 1, rdf));
			}
			xsl.appendL(indent + 1, "return result;");
			xsl.appendL(indent, "}");
			xsl.appendL(indent, "</script>");
		}
		xsl.appendL("</head>");
		xsl.appendL("<body>");
		Header.headToXSL(
			super.getExtendHeader(),
			super.getRootHeader(),
			super.getHeader(),
			Head.HEAD_TYPE_TOP,
			Head.HEAD_POSITION_TOP,
			param,
			rdf,
			xsl
		);
		if(super.getHeader() != null) {
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_LABEL, Label.LABEL_POSITION_BODY, param, super.getLabel(), super.getXLabel(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_AUTHOR, Label.LABEL_POSITION_BODY, param, super.getAuthor(), super.getXAuthor(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_KEYWORD, Label.LABEL_POSITION_BODY, param, super.getKeyword(), super.getXKeyword(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_DESC, Label.LABEL_POSITION_BODY, param, super.getDesc(), super.getXDesc(), rdf));
		}
	}
	private void post(Record param, HttpServletRequest request, int indent, boolean rdf, Buffer xsl) {
		this.html(param, request, indent, rdf, xsl);
		this.head(param, indent, rdf, xsl);
		xsl.appendL(indent, "<style type=\"text/css\">");
		xsl.appendL(indent, "body {");
		xsl.appendL(indent + 1, "height:100%;");
		xsl.appendL(indent + 1, "text-align:center;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "form {");
		xsl.appendL(1, "position: absolute;");
		xsl.appendL(1, "top: 50%;");
		xsl.appendL(1, "transform: translateY(-50%);");
		xsl.appendL(1, "width:100%;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "form div.msg {");
		xsl.appendL(1, "margin-bottom:10px;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "noscript {");
		xsl.appendL(1, "width:100%;");
		xsl.appendL(1, "display:block;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "</style>");
		xsl.appendL(indent, "</head>");
		xsl.appendL(indent, "<body>");
		if(STR.valid(super.getRedirect())) {
			xsl.appendL(indent + 1, "<xsl:choose>");
			for(int i = 0; i < super.getRedirect().size(); i++) {
				xsl.append(((Redirect)super.getRedirect().get(i)).toXSL(super.getTable(), super.getCommand(), param, indent + 2, rdf));
			}
			xsl.appendL(indent + 1, "</xsl:choose>");
		}
		xsl.appendL(indent, "<script>");
		xsl.appendL(indent, "function _getMessage(msg) {");
		xsl.appendL(indent + 1, "if(typeof(_messages) != \"undefined\" &amp;&amp; msg.indexOf(\"message.\") == 0) {");
		xsl.appendL(indent + 2, "for(var i = 0; i &lt; _messages.length; i++) {");
		xsl.appendL(indent + 3, "if(\"message.\" + _messages[i].name == msg) {");
		xsl.appendL(indent + 4, "return _messages[i].label;");
		xsl.appendL(indent + 3, "}");
		xsl.appendL(indent + 2, "}");
		xsl.appendL(indent + 1, "}");
		xsl.appendL(indent + 1, "return msg;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "if(document.getElementById(\"_post\") &amp;&amp; document.getElementById(\"_post\").getElementsByClassName(\"autoredirect\").length == 0) {");
		xsl.appendL(indent + 1, "if(document.getElementsByClassName(\"msg\")) {");
		xsl.appendL(indent + 2, "for(var i = 0; i &lt; document.getElementsByClassName(\"msg\").length; i++) {");
		xsl.appendL(indent + 3, "if(document.getElementsByClassName(\"msg\")[i].innerText) {");
		xsl.appendL(indent + 4, "alert(_getMessage(document.getElementsByClassName(\"msg\")[i].innerText));");
		xsl.appendL(indent + 3, "} else {");
		xsl.appendL(indent + 4, "var msg = \"\";");
		xsl.appendL(indent + 4, "for(var x = 0; x &lt; document.getElementsByClassName(\"msg\")[i].childNodes.length; i++) {");
		xsl.appendL(indent + 5, "msg += _getMessage(document.getElementsByClassName(\"msg\")[i].childNodes[x].nodeValue);");
		xsl.appendL(indent + 4, "}");
		xsl.appendL(indent + 4, "alert(msg);");
		xsl.appendL(indent + 3, "}");
		xsl.appendL(indent + 2, "}");
		xsl.appendL(indent + 1, "}");
		xsl.appendL(indent + 1, "setTimeout(function() {");
		xsl.appendL(indent + 2, "document.getElementById(\"_post\").submit();");
		xsl.appendL(indent + 1, "}, 0);");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "</script>");
		xsl.appendL(indent, "</body>");
		xsl.appendL(indent, "</html>");
		xsl.appendL(indent, "</xsl:template>");
		xsl.appendL(indent, "</xsl:stylesheet>");
	}
	private void error(Record param, HttpServletRequest request, int indent, boolean rdf, Buffer xsl) {
		this.html(param, request, indent, rdf, xsl);
		this.head(param, indent, rdf, xsl);
		xsl.appendL(indent, "<script>");
		xsl.appendL(indent, "function back() {");
		xsl.appendL(indent + 1, "history.back();");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "</script>");
		xsl.appendL(indent, "<style type=\"text/css\">");
		xsl.appendL(indent, "body {");
		xsl.appendL(indent + 1, "height:100%;");
		xsl.appendL(indent + 1, "text-align:center;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "div {");
		xsl.appendL(indent + 1, "position: absolute;");
		xsl.appendL(indent + 1, "top: 50%;");
		xsl.appendL(indent + 1, "transform: translateY(-50%);");
		xsl.appendL(indent + 1, "width:550px;");
		xsl.appendL(indent + 1, "left:50%;");
		xsl.appendL(indent + 1, "margin-left:-275px;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "div ul {");
		xsl.appendL(indent + 1, "text-align:left;");
		xsl.appendL(indent, "}");
		xsl.appendL(indent, "</style>");
		xsl.appendL(indent, "</head>");
		xsl.appendL(indent, "<body>");
		xsl.appendL(indent, "<div>");
		xsl.appendL(indent + 1, "<ul>");
		xsl.appendL(indent + 2, "<xsl:for-each select=\"" + kr.graha.post.xml.GParam.childNodePath("error", "error", rdf) + "\">");
		xsl.appendL(indent + 3, "<li><xsl:value-of select=\".\" /></li>");
		xsl.appendL(indent + 2, "</xsl:for-each>");
//		xsl.appendL(indent + 2, "<li>이 메시지를 보고 있다면, 이전 화면의 Javascript 실행 과정에서 에러가 발생했거나 웹브라우저에서 Javascript를 사용하지 않도록 설정한 것입니다.</li>");
//		xsl.appendL(indent + 2, "<li>만약 웹브라우저에서 Javascript를 사용하지 않도록 설정했다면, 웹브라우저의 뒤로가기 기능을 이용하여 이전화면으로 돌아갑니다.</li>");
		xsl.appendL(indent + 1, "</ul>");
		xsl.appendL(indent + 1, "<input type=\"button\" value=\"Go back one page\" onclick=\"back()\" />");
		xsl.appendL(indent, "</div>");
		xsl.appendL(indent, "</body>");
		xsl.appendL(indent, "</html>");
		xsl.appendL(indent, "</xsl:template>");
		xsl.appendL(indent, "</xsl:stylesheet>");
	}
	private void html(Record param, HttpServletRequest request, int indent, boolean rdf, Buffer xsl) {
		xsl.appendL("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if(rdf) {
			xsl.appendL("<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:RDF=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:uc=\"" + super.getUc(request) + "\">");
		} else {
			xsl.appendL("<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		}
		xsl.appendL("<xsl:output method=\"html\" encoding=\"utf-8\" indent=\"yes\" version=\"5.0\" omit-xml-declaration=\"no\" />");
		xsl.appendL("<xsl:template match=\"/\">");
		if(param.equals(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html")) {
			xsl.appendL("<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;&#xa;</xsl:text>");
		} else { 
			xsl.appendL("<xsl:if test=\"system-property('xsl:vendor') = 'Microsoft'\">");
			xsl.appendL(1, "<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>");
			xsl.appendL("</xsl:if>");
		}
		xsl.appendL("<html>");
	}
	private void head(Record param, int indent, boolean rdf, Buffer xsl) {
		xsl.appendL("<head>");
		xsl.appendL("<meta charset=\"UTF-8\" />");
		xsl.appendL("<meta name=\"viewport\" content=\"user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, width=device-width\" />");
		if(super.getHeader() != null) {
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_LABEL, Label.LABEL_POSITION_HEAD, param, super.getLabel(), super.getXLabel(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_DESC, Label.LABEL_POSITION_HEAD, param, super.getDesc(), super.getXDesc(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_AUTHOR, Label.LABEL_POSITION_HEAD, param, super.getAuthor(), super.getXAuthor(), rdf));
			xsl.append(super.getHeader().labelToXSL(Label.LABEL_TYPE_KEYWORD, Label.LABEL_POSITION_HEAD, param, super.getKeyword(), super.getXKeyword(), rdf));
		}
		xsl.appendL("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />");
		xsl.appendL("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		Header.headToXSL(
			super.getExtendHeader(),
			super.getRootHeader(),
			super.getHeader(),
			Head.HEAD_TYPE_HEAD,
			Head.HEAD_POSITION_NONE,
			param,
			rdf,
			xsl
		);
	}
	private void css(Record param, int indent, boolean rdf, Buffer xsl) {
		if(super.getExtendHeader() != null) {
			xsl.append(super.getExtendHeader().styleToXSL(param, rdf, super.getRootHeader(), super.getHeader()));
		}
		if(super.getRootHeader() != null) {
			xsl.append(super.getRootHeader().styleToXSL(param, rdf, super.getHeader()));
		}
		if(super.getHeader() != null) {
			xsl.append(super.getHeader().styleToXSL(param, rdf));
		}
	}
	private void script(Record param, int indent, boolean rdf, Buffer xsl) {
		if(super.getExtendHeader() != null) {
			xsl.append(super.getExtendHeader().scriptToXSL(param, rdf, super.getRootHeader(), super.getHeader()));
		}
		if(super.getRootHeader() != null) {
			xsl.append(super.getRootHeader().scriptToXSL(param, rdf, super.getHeader()));
		}
		if(super.getHeader() != null) {
			xsl.append(super.getHeader().scriptToXSL(param, rdf));
		}
	}
	private void after(Record param, int indent, boolean rdf, Buffer xsl) {
		Header.headToXSL(
			super.getExtendHeader(),
			super.getRootHeader(),
			super.getHeader(),
			Head.HEAD_TYPE_BOTTOM,
			Head.HEAD_POSITION_BOTTOM,
			param,
			rdf,
			xsl
		);
		xsl.appendL("</body>");
		xsl.appendL("</html>");
		xsl.appendL("</xsl:template>");
		if(super.getLayout() != null && STR.compareIgnoreCase(super.getLayout().getTemplate(), "native")) {
			if(STR.valid(super.getAppend())) {
				for(int i = 0; i < super.getAppend().size(); i++) {
					xsl.append((String)super.getAppend().get(i));
				}
			}
		}
		xsl.appendL("</xsl:stylesheet>");
	}
}
