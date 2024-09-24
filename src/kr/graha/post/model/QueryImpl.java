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


import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.FilePart;
import kr.graha.post.model.utility.FileUploadByApacheCommons;
import kr.graha.post.model.utility.FileUploadByServlet30;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import kr.graha.post.interfaces.ServletAdapter;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * Graha(그라하) Query 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class QueryImpl extends Query {
	protected static int REQUEST_TYPE_UNKNOWN = 0;
	protected static int REQUEST_TYPE_XML = 1;
	public static int REQUEST_TYPE_XSL = 2;
	protected static int REQUEST_TYPE_HTML = 3;
	public static int REQUEST_TYPE_XML_DOWNLOAD = 4;
	public static int REQUEST_TYPE_HTML_DOWNLOAD = 5;
	
	
	private int requestType = QueryImpl.REQUEST_TYPE_UNKNOWN;
/**
 * extend 가 있는 경우, extend 가 id 인 query
 */
	private QueryImpl extendQuery = null;
	private Header rootHeader;
	private Header extendHeader;
	private ConnectionFactory connectionFactory = null;
	protected QueryImpl(Header rootHeader, Header extendHeader) {
		this.rootHeader = rootHeader;
		this.extendHeader = extendHeader;
	}
	protected void setRequestType(int requestType) {
		this.requestType = requestType;
	}
	public int getRequestType() {
		return this.requestType;
	}
	public boolean auth(Record params) {
		if(STR.valid(super.getAuth()) && !AuthUtility.auth(super.getAuth(), params)) {
			LOG.config("[SC_FORBIDDEN]auth = " + super.getAuth());
			this.abort();
			return false;
		}
		return true;
	}
	protected void setExtendQuery(QueryImpl extendQuery) {
		this.extendQuery = extendQuery;
	}
	protected String getUc(HttpServletRequest request) {
		if(this.rdf()) {
			if(!STR.valid(super.getUc())) {
				String id = request.getPathInfo().trim();
				if(id.startsWith("/")) {
					id = id.substring(1);
				}
				return (request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort() + "" + request.getContextPath() + request.getServletPath() + "/" + id.substring(0, id.indexOf("/") + 1));
			}
			return super.getUc();
		} else {
			return null;
		}
	}
/**
 *
 *
 */
	public int getQueryFuncType() {
		if(STR.compareIgnoreCase(super.getFuncType(), "list")) {
			return Query.QUERY_FUNC_TYPE_LIST;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "listAll")) {
			return Query.QUERY_FUNC_TYPE_LISTALL;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "insert")) {
			return Query.QUERY_FUNC_TYPE_INSERT;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "detail")) {
			return Query.QUERY_FUNC_TYPE_DETAIL;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "delete")) {
			return Query.QUERY_FUNC_TYPE_DELETE;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "user")) {
			return Query.QUERY_FUNC_TYPE_USER;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "query")) {
			return Query.QUERY_FUNC_TYPE_QUERY;
		} else if(STR.compareIgnoreCase(super.getFuncType(), "report")) {
			return Query.QUERY_FUNC_TYPE_REPORT;
		} else {
			return Query.QUERY_FUNC_TYPE_UNKNOWN;
		}
	}
/**
 * XML 출력형식이 rdf 인 경우 true 를 반환한다.
 * output 속성값 이 명시적으로 rdf 로 정의된 경우에만 true 를 반환하고,
 * output 속성값 이 정의되지 않았거나, rdf 가 아닌 다른 값으로 정의된 경우에는 fasle 를 반환한다.
 */
	protected boolean rdf() {
		if(STR.valid(this.getOutput()) && STR.compareIgnoreCase(this.getOutput(), "rdf")) {
			return true;
		} else {
			return false;
		}
	}
/**
 * XSL 출력형식이 div 인 경우 true 를 반환한다.
 * htmlType 속성값 이 명시적으로 div 로 정의된 경우에만 true 를 반환하고,
 * htmlType 속성값 이 정의되지 않았거나, div 가 아닌 다른 값으로 정의된 경우에는 fasle 를 반환한다.
 */
	protected boolean div() {
		return QueryImpl.div(super.getHtmltype());
		/*
		if(STR.valid(super.getHtmltype()) && STR.compareIgnoreCase(super.getHtmltype(), "div")) {
			return true;
		} else {
			return false;
		}
		*/
	}
	protected static boolean div(String htmlType) {
		if(STR.valid(htmlType) && STR.compareIgnoreCase(htmlType, "div")) {
			return true;
		} else {
			return false;
		}
	}
	protected List<Tab> getTab() {
		if(this.getLayout() == null) {
			return null;
		}
		return this.getLayout().getTab();
	}
	protected Layout getLayout() {
		if(super.getLayout() == null && STR.valid(this.getExtend()) && this.extendQuery != null) {
			return this.extendQuery.getLayout();
		} else {
			return super.getLayout();
		}
	}
	protected List<Table> getTable() {
		if(this.table == null && STR.valid(this.getExtend()) && this.extendQuery != null) {
			return this.extendQuery.getTable();
		} else {
			return this.table;
		}
	}
	protected List<Command> getCommand() {
		if(super.getCommand() == null && STR.valid(this.getExtend()) && this.extendQuery != null) {
			return this.extendQuery.getCommand();
		} else {
			return super.getCommand();
		}
	}
	protected List<String> getAppend() {
		if(super.getAppend() == null && STR.valid(this.getExtend()) && this.extendQuery != null) {
			return this.extendQuery.getAppend();
		} else {
			return super.getAppend();
		}
	}
	protected Header getRootHeader() {
		return this.rootHeader;
	}
	protected Header getExtendHeader() {
		return this.extendHeader;
	}
	protected Header getHeader() {
		return super.header;
	}
	public Files getFiles() {
		return super.files;
	}
	protected List<CalculatorParam> getCalculator() {
		return super.calculator;
	}
	protected List<Validation> getValidation() {
		return super.validation;
	}
	protected List<Redirect> getRedirect() {
		return super.redirect;
	}
	protected List<Processor> getProcessor() {
		return super.processor;
	}
	protected void clear() {
		this.closeConnectionFactory();
	}
	public void abort() {
		if(this.connectionFactory != null) {
			this.connectionFactory.abort();
			this.connectionFactory = null;
		}
	}
	protected void closeConnectionFactory() {
		if(this.connectionFactory != null) {
			this.connectionFactory.close();
			this.connectionFactory = null;
		}
	}
	protected ConnectionFactory getConnectionFactory(Record params) {
		if(this.connectionFactory == null) {
			if(this.getHeader() != null) {
				this.connectionFactory = this.getHeader().getConnectionFactory(params, this.getRootHeader(), this.getExtendHeader());
			} else if(this.getRootHeader() != null) {
				this.connectionFactory = this.getRootHeader().getConnectionFactory(params, this.getExtendHeader());
			} else if(this.getExtendHeader() != null) {
				this.connectionFactory = this.getExtendHeader().getConnectionFactory(params);
			}
		}
		return this.connectionFactory;
	}
	protected List<FilePart> prepareUsingServletFileUpload(
		HttpServletRequest request,
		ServletConfig servletConfig,
		Record params
	) throws UnsupportedEncodingException, IOException, ServletException {
		this.system(request, params);
		this.header(request, params);
		this.session(request, params);
		this.attribute(request, params);
		if(System.getProperties().containsKey("catalina.home")) {
			this.userRole(request, params);
		}
		List<FilePart> fields = null;
		try {
			fields = this.parameterUsingServletFileUpload(request, params, this.getFiles(), servletConfig.getInitParameter("FileUploadLibrary"));
		} catch (UnsupportedEncodingException e) {
			this.abort();
			LOG.severe(e);
			throw e;
		}
		this.userServletAdapter(request, servletConfig, params);
		try {
			this.executeProp(params, Prop.Before_Connection);
		} catch (NoSuchProviderException | SQLException e) {
			this.abort();
			LOG.severe(e);
		}
		return fields;
	}
	private void userServletAdapter(HttpServletRequest request, ServletConfig servletConfig, Record params) {
		try {
			if(servletConfig.getInitParameter("UserServletAdapter") != null) {
				ServletAdapter adapter = (ServletAdapter)Class.forName(servletConfig.getInitParameter("UserServletAdapter")).getConstructor().newInstance();
				adapter.execute(request, params);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			LOG.severe(e);
		}
	}
	protected void executeProp(Record params, int time) throws NoSuchProviderException, SQLException {
		if(params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return;
		}
		if(this.getExtendHeader() != null) {
			this.getExtendHeader().executeProp(params, time, this.getConnectionFactory(params), this.getRootHeader(), this.getHeader());
		}
		if(this.getRootHeader() != null) {
			this.getRootHeader().executeProp(params, time, this.getConnectionFactory(params), this.getHeader());
		}
		if(this.getHeader() != null) {
			this.getHeader().executeProp(params, time, this.getConnectionFactory(params));
		}
	}
	private List<FilePart> parameterUsingServletFileUpload(HttpServletRequest request, Record params, Files files, String fileUploadLibrary)
		throws UnsupportedEncodingException, IOException, ServletException {
		ServletContext c = request.getServletContext();
		if(c != null) {
			Enumeration<String> p = c.getInitParameterNames();
			while (p.hasMoreElements()) {
				String key = p.nextElement();
				params.puts(Record.key(Record.PREFIX_TYPE_INIT_PARAM, key), c.getInitParameter(key));
			}
		}
		boolean legacyServletAPI = FilePart.legacyServletAPI(request);
		List<FilePart> fileParts = null;
		if(fileUploadLibrary != null && STR.compareIgnoreCase(fileUploadLibrary, "Servlet30FileUpload")) {
			fileParts = FileUploadByServlet30.getFileParts(request, legacyServletAPI);
		} else {
			fileParts = FileUploadByApacheCommons.getFileParts(request, params, files);
		}
		if(legacyServletAPI) {
			request.setCharacterEncoding("ISO-8859-1");
		} else {
			request.setCharacterEncoding("UTF-8");
		}
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String[] values = request.getParameterValues(key);
			if(values != null) {
				for (String value : values) {
					if(value == null) {continue;}
					if(legacyServletAPI) {
						params.puts(Record.key(Record.PREFIX_TYPE_PARAM, key), new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
					} else {
						params.puts(Record.key(Record.PREFIX_TYPE_PARAM, key), value);
					}
				}
			}
		}
		return fileParts;
	}
	private void userRole(HttpServletRequest request, Record params) {
		if(request.getUserPrincipal() instanceof org.apache.catalina.realm.GenericPrincipal) {
			String[] roles = ((org.apache.catalina.realm.GenericPrincipal)request.getUserPrincipal()).getRoles();
			if(roles != null) {
				params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_user_roles"), roles);
			}
		} else if(request.getUserPrincipal() instanceof org.apache.catalina.User) {
			Iterator<org.apache.catalina.Role> it = ((org.apache.catalina.User)request.getUserPrincipal()).getRoles();
			List<String> roles = new ArrayList<String>();
			while(it.hasNext()) {
				roles.add(((org.apache.catalina.Role)it.next()).getRolename());
			}
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_user_roles"), roles.toArray(new String[]{}));
		} else {
			if(request.getUserPrincipal() != null) {
				LOG.finer(request.getUserPrincipal().getClass().getName());
			}
		}
	}
	private void attribute(HttpServletRequest request, Record params) {
		Enumeration<String> e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			params.put(Record.key(Record.PREFIX_TYPE_ATT, key), request.getAttribute(key));
		}
	}
	private void session(HttpServletRequest request, Record params) {
		HttpSession session = request.getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			params.put(Record.key(Record.PREFIX_TYPE_SESSION, key), session.getAttribute(key));
		}
	}
	private void header(HttpServletRequest request, Record params) {
		Enumeration<String> e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			if(key == null) {continue;}
			if(
				key.equals("method") ||
				key.equals("remote_user") ||
				key.equals("remote_addr") ||
				key.equals("remote_host") ||
				key.equals("remote_user_roles") ||
				key.equals("scheme")
			) {
				continue;
			}
			params.puts(Record.key(Record.PREFIX_TYPE_HEADER, key), request.getHeader(key));
		}
		params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), request.getMethod());
		params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_user"), request.getRemoteUser());
		
		if(STR.valid(request.getHeader("X-Forwarded-For"))) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_addr"), request.getHeader("X-Forwarded-For"));
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_addr"), request.getRemoteAddr());
		}
		params.put(Record.key(Record.PREFIX_TYPE_HEADER, "remote_host"), request.getRemoteHost());
		params.put(Record.key(Record.PREFIX_TYPE_HEADER, "scheme"), request.getScheme());
	}
	private void system(HttpServletRequest request, Record params) {
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "prefix"), request.getContextPath() + request.getServletPath());
		if(request.getPathInfo().endsWith(".html")) {
			params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".html");
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "suffix"), ".xml");
		}
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "context.root.realpath"), request.getServletContext().getRealPath("/"));
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "context.root.path"), request.getServletContext().getRealPath("/"));
		params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "context.path"), request.getContextPath());
	}
}
