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
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.interfaces.Reporter;
import kr.graha.post.lib.Key;
import kr.graha.post.lib.Record;
import kr.graha.post.xml.GDocument;
import javax.servlet.ServletConfig;
import org.apache.commons.fileupload.FileItem;
import java.net.URISyntaxException;
import java.io.ByteArrayInputStream;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import java.io.StringReader;

/**
 * Graha(그라하) Query 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class QueryXMLImpl extends QueryXSLImpl {
	protected QueryXMLImpl(Header rootHeader, Header extendHeader) {
		super(rootHeader, extendHeader);
	}
	private void executeProcessor(
		GDocument document, 
		Record param, 
		HttpServletRequest request, 
		HttpServletResponse response, 
		boolean before
	) throws NoSuchProviderException, SQLException {
		if(param.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return;
		}
		if(STR.valid(super.getProcessor())) {
			for(int i = 0; i < super.getProcessor().size(); i++) {
				((Processor)super.getProcessor().get(i)).execute(document, param, request, response, super.getConnectionFactory(param), before);
			}
		}
	}
	private boolean command(Record param, int queryFuncType) {
		if(
			queryFuncType == Query.QUERY_FUNC_TYPE_LIST || 
			queryFuncType == Query.QUERY_FUNC_TYPE_LISTALL ||
			queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL ||
			queryFuncType == Query.QUERY_FUNC_TYPE_USER
		) {
			return true;
		}
		if(
			queryFuncType == Query.QUERY_FUNC_TYPE_QUERY ||
			queryFuncType == Query.QUERY_FUNC_TYPE_REPORT
		) {
			if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
				return true;
			}
			if(
				param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET") &&
				STR.compareIgnoreCase(super.getAllow(), "get")
			) {
				return true;
			}
		}
		return false;
	}
	private int executeCommand(
		GDocument document, 
		Record param, 
		HttpServletRequest request, 
		HttpServletResponse response, 
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		if(param.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return 0;
		}
		int totalFetchCount = 0;
		if(this.command(param, queryFuncType)) {
			if(STR.valid(super.getCommand())) {
				for(int i = 0; i < super.getCommand().size(); i++) {
					totalFetchCount += ((Command)super.getCommand().get(i)).execute(document, param, request, response, super.getCommand(), super.getTab(), super.getConnectionFactory(param), queryFuncType);
				}
				if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY) {
					param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_affected_count"), totalFetchCount);
				} else {
					param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count"), totalFetchCount);
				}
			}
		}
		return totalFetchCount;
	}
	private int executeTable(
		GDocument document, 
		Record param, 
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		if(param.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return 0;
		}
		int totalFetchCount = 0;
		boolean isNew = true;
		if(queryFuncType == Query.QUERY_FUNC_TYPE_DELETE || queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
			if(STR.valid(super.getTable())) {
				for(int i = 0; i < super.getTable().size(); i++) {
					if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
						totalFetchCount += ((Table)super.getTable().get(i)).POST(document, param, super.getTable(), super.getTab(), super.getConnectionFactory(param), queryFuncType);
					} else if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET")) {
						int result = ((Table)super.getTable().get(i)).GET(document, param, super.getTable(), super.getTab(), super.getConnectionFactory(param), queryFuncType);
						if(result >= 0) {
							totalFetchCount += result;
							isNew = false;
						}
					}
				}
				if(param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
					if(queryFuncType == Query.QUERY_FUNC_TYPE_DELETE) {
						param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_delete_count"), totalFetchCount);
					} else {
						param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count"), totalFetchCount);
					}
				} else if(!isNew && param.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET")) {
					param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count"), totalFetchCount);
				}
			}
		}
		return totalFetchCount;
	}
	private void executeFile(
		GDocument document,
		List<FileItem> fields,
		Record params,
		int queryFuncType
	) throws IOException, URISyntaxException {
		if(params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return;
		}
		if(super.getFiles() != null) {
			try {
				if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
					if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
						this.uploadFile(fields, params, queryFuncType);
					} else {
						this.listFile(document, params);
					}
				} else if(queryFuncType == Query.QUERY_FUNC_TYPE_DELETE) {
					if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
						this.deleteFile(document, params);
					}
				} else if(queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL) {
					this.listFile(document, params);
				}
			} catch (IOException | URISyntaxException e) {
				super.abort();
				throw e;
			}
		}
	}
	private void listFile(GDocument document, Record params) throws IOException {
		if(super.getFiles() != null) {
			if(super.getFiles().fileAllow(params)) {
				if(
						!params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) ||
						params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) > 0
				) {
					super.getFiles().list(document, params);
				} else {
					LOG.warning("hasKey : " + params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")));
					LOG.warning("getInt : " + params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")));
				}
			}
		}
	}
	private void deleteFile(GDocument document, Record params) {
		if(super.getFiles() != null) {
			if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_delete_count")) &&
					params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_delete_count")) > 0 &&
					super.getFiles().fileAllow(params)
			) {
				super.getFiles().delete(document, params);
			}
		}
	}
	private boolean validate(Record params) throws NoSuchProviderException, SQLException {
		if(STR.valid(super.getValidation())) {
			try {
				List<String> msgs = new ArrayList<String>();
				for(int i = 0; i < super.getValidation().size(); i++) {
					Validation v = super.getValidation().get(i);
					v.validate(params, super.getConnectionFactory(params), msgs);
				}
				if(msgs.isEmpty()) {
					return true;
				} else {
					for(int i = 0; i < msgs.size(); i++) {
						String msg = (String)msgs.get(i);
						params.puts(Record.key(Record.PREFIX_TYPE_ERROR, "error"), msg);
					}
					msgs.clear();
					msgs = null;
					return false;
				}
			} catch (NoSuchProviderException | SQLException e) {
				super.abort();
				throw e;
			}
		}
		return true;
	}
	private void executeMessage(GDocument document, Record params) {
		if(super.getExtendHeader() != null) {
			super.getExtendHeader().executeMessage(document, params, super.getRootHeader(), super.getHeader());
		}
		if(super.getRootHeader() != null) {
			super.getRootHeader().executeMessage(document, params, super.getHeader());
		}
		if(super.getHeader() != null) {
			super.getHeader().executeMessage(document, params);
		}
	}
	private void executeCode(GDocument document, Record params) throws NoSuchProviderException, SQLException {
		if(params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			return;
		}
		try {
			if(super.getExtendHeader() != null) {
				super.getExtendHeader().executeCode(document, params, super.getConnectionFactory(params), super.getRootHeader(), super.getHeader());
			}
			if(super.getRootHeader() != null) {
				super.getRootHeader().executeCode(document, params, super.getConnectionFactory(params), super.getHeader());
			}
			if(super.getHeader() != null) {
				super.getHeader().executeCode(document, params, super.getConnectionFactory(params));
			}
		} catch (NoSuchProviderException | SQLException e) {
			super.abort();
			throw e;
		}
	}
	private boolean post(Record params, int queryFuncType) {
		if(params.getBoolean(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "resultset"))) {
			return false;
		}
		if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
			return true;
		}
		if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET")) {
			if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY || queryFuncType == Query.QUERY_FUNC_TYPE_REPORT) {
				if(STR.compareIgnoreCase(super.getAllow(), "GET")) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean href(int queryFuncType) {
		if(
			queryFuncType == Query.QUERY_FUNC_TYPE_LIST ||
			queryFuncType == Query.QUERY_FUNC_TYPE_LISTALL ||
			queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL
		) {
			if(super.getLayout() != null && STR.valid(super.getLayout().getHref())) {
				return true;
			}
		}
		return false;
	}
	private void setXslNameAndParam(GDocument document, Record params, int queryFuncType) {
		document.setXslName(super.getId().substring(super.getId().lastIndexOf("/") + 1));
		if(params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			document.setXslParam("method=error");
		} else if(this.post(params, queryFuncType)) {
			document.setXslParam("method=post");
		} else {
			if(this.href(queryFuncType)) {
				document.setXslPath(super.getLayout().getHref());
			} else {
				if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
					if(params.getBoolean(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "resultset"))) {
						document.setXslParam("method=query");
					} else {
						document.setXslParam("method=post");
					}
				}
				if(params != null && !params.isEmpty()) {
					StringBuffer xslParam = new StringBuffer();
					Iterator<Key> it = params.keySet().iterator();
					int index = 0;
					if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST")) {
						index = 1;
					}
					while(it.hasNext()) {
						Key key = (Key)it.next();
						if(key != null && key.getPrefix() == Record.PREFIX_TYPE_PARAM && !key.equals(Record.key(Record.PREFIX_TYPE_PARAM, "page"))) {
							if(params.isArray(key)) {
								java.util.List<String> items = params.getArray(key);
								for(String item : items) {
									if(index > 0) {
										xslParam.append("&amp;");
									}
									try {
										xslParam.append(key.getKey() + "=" + java.net.URLEncoder.encode(item, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										xslParam.append(key.getKey() + "=" + item);
										LOG.severe(e);
									}
									index++;
								}
							} else {
								if(index > 0) {
									xslParam.append("&amp;");
								}
								try {
									xslParam.append(key.getKey() + "=" + java.net.URLEncoder.encode(params.getString(key), "UTF-8"));
								} catch (UnsupportedEncodingException e) {
									xslParam.append(key.getKey() + "=" + params.getString(key));
									LOG.severe(e);
								}
								index++;
							}
						}
					}
					document.appendXslParam(xslParam);
				}
			}
		}
	}
	private boolean downloadable(Record params) throws NoSuchProviderException, SQLException {
		try {
			if(super.getFiles() != null && super.getFiles().downloadable(params, super.getConnectionFactory(params))) {
				return true;
			}
			return false;
		} catch (NoSuchProviderException | SQLException e) {
			super.abort();
			throw e;
		} catch (Exception e) {
			super.abort();
			throw e;
		}
	}
	public int download(HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig, Record params) throws IOException, NoSuchProviderException, SQLException {
		super.prepare(request, servletConfig, params);
		if(!super.auth(params) || !this.downloadable(params)) {
			super.clear();
			return HttpServletResponse.SC_FORBIDDEN;
		}
		try {
			super.closeConnectionFactory();
			if(super.getFiles() != null) {
				super.getFiles().download(request, response, params);
			}
			super.clear();
			return HttpServletResponse.SC_OK;
		} catch (IOException e) {
			super.abort();
			throw e;
		} catch (Exception e) {
			super.abort();
			throw e;
		}
	}
	private void uploadFile(
		List<FileItem> fields,
		Record params,
		int queryFuncType
	) throws IOException, URISyntaxException {
		if(queryFuncType != Query.QUERY_FUNC_TYPE_INSERT) {
			return;
		}
		if(fields != null) {
			if(super.getFiles() != null) {
				if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")) &&
					params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")) > 0 &&
					super.getFiles().fileAllow(params)
				) {
					try {
						super.getFiles().upload(fields, params);
					} catch (IOException | URISyntaxException e) {
						super.abort();
						throw e;
					}
				} else {
					LOG.warning(
						"[SC_NOT_FOUND]hasKey : " + params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")),
						"[SC_NOT_FOUND]getInt : " + params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")),
						"[SC_NOT_FOUND]isAllow : " + super.getFiles().fileAllow(params)
					);
				}
			}
		}
	}
	public void executeReport(
		HttpServletRequest request,
		HttpServletResponse response,
		Record params,
		GDocument document,
		int queryFuncType
	) {
		if(queryFuncType != Query.QUERY_FUNC_TYPE_REPORT) {
			return;
		}
		if(!params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			try {
				Reporter reporter = (Reporter)Class.forName(super.getClassName()).getConstructor().newInstance();
				reporter.execute(request, response, params, document.toXML(), super.getConnectionFactory(params).getConnection());
			} catch (IOException | InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				super.abort();
				LOG.severe(e);
			} catch (SQLException e) {
				super.abort();
				LOG.severe(e);
			}
			document.clear();
		}
	}
	private GDocument getDocument(HttpServletRequest request) {
		return new GDocument(
			super.getOutput(),
			super.getUc(),
			request
		);
	}
	private boolean empty(Record params, int queryFuncType) {
		boolean result = false;
		if(
			params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) &&
			params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) == 0 &&
			!STR.trueValue(super.getAllowblank())
		) {
			if(
				queryFuncType == Query.QUERY_FUNC_TYPE_LISTALL ||
				queryFuncType == Query.QUERY_FUNC_TYPE_DETAIL
			) {
				result = true;
			} else if(
				queryFuncType == Query.QUERY_FUNC_TYPE_LIST &&
				params.getInt(Record.key(Record.PREFIX_TYPE_PARAM, "page")) > 1
			) {
				result = true;
			} else if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET")) {
					result = true;
				}
			}
			if(result) {
				LOG.warning(
					"[SC_NOT_FOUND]hasKey : " + params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")),
					"[SC_NOT_FOUND]getInt : " + params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")),
					"[SC_NOT_FOUND]allowblank : " + STR.trueValue(super.getAllowblank())
				);
			}
		} else if(
			params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")) &&
			params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")) == 0
		) {
			LOG.warning(
				"[SC_NOT_FOUND]hasKey : " + params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count")),
				"[SC_NOT_FOUND]getInt : " + params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_update_count"))
			);
			result = true;
		}
		return result;
	}
	public GDocument document(
		HttpServletRequest request,
		HttpServletResponse response,
		List<FileItem> fields,
		Record params,
		int queryFuncType
	) throws NoSuchProviderException, SQLException, IOException, URISyntaxException {
		boolean rdf = super.rdf();
		if(rdf) {
			params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "output"), "rdf");
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_SYSTEM, "output"), "xml");
		}
		GDocument document = this.getDocument(request);
		params.setGDocument(document);
		int totalFetchCount = 0;
		try {
			this.executeMessage(document, params);
			this.executeCode(document, params);
			super.executeProp(params, Prop.Before_Before_Processor);
			this.executeProcessor(document, params, request, response, true);
			super.executeProp(params, Prop.After_Before_Processor);
			totalFetchCount += this.executeCommand(document, params, request, response, queryFuncType);
			totalFetchCount += this.executeTable(document, params, queryFuncType);
			this.executeFile(document, fields, params, queryFuncType);
			if(empty(params, queryFuncType)) {
				super.clear();
				return null;
			}
			super.executeProp(params, Prop.Before_After_Processor);
			this.executeProcessor(document, params, request, response, false);
			super.executeProp(params, Prop.After_After_Processor);
			this.executeReport(request, response, params, document, queryFuncType);
			this.setXslNameAndParam(document, params, queryFuncType);
			document.add(params);
			super.clear();
		} catch (NoSuchProviderException | SQLException | IOException | URISyntaxException e) {
			super.abort();
			throw e;
		} catch (Exception e) {
			super.abort();
			throw e;
		}
		return document;
	}
	private void sendUser(HttpServletRequest request, HttpServletResponse response, GDocument document) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(super.getContentType());
		try {
			StreamSource style = new StreamSource(new java.io.File(request.getServletContext().getRealPath("/WEB-INF/graha/" + super.getXsl())));
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(style);
			Source text = new StreamSource(new ByteArrayInputStream(document.toXML().toString().getBytes(StandardCharsets.UTF_8)));
			transformer.transform(text, new StreamResult(response.getWriter()));
		} catch (IOException | TransformerException e) {
			LOG.severe(e);
		}
	}
	private void sendHTML(HttpServletRequest request, HttpServletResponse response, Record params, GDocument document, int queryFuncType) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		if(params.getBoolean(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "resultset"))) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "QUERY");
		}
		if(params.containsKey(Record.key(Record.PREFIX_TYPE_ERROR, "error"))) {
			params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "ERROR");
		}
		if(params.equals(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "GET")) {
			if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY || queryFuncType == Query.QUERY_FUNC_TYPE_REPORT) {
				if(STR.compareIgnoreCase(super.getAllow(), "GET")) {
					params.put(Record.key(Record.PREFIX_TYPE_HEADER, "method"), "POST");
				}
			}
		}
		try {
			StringReader reader = new StringReader(super.toXSL(params, request, 0).toString());
			StreamSource style = new StreamSource(reader);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(style);
			Source text = new StreamSource(new ByteArrayInputStream(document.toXML().toString().getBytes(StandardCharsets.UTF_8)));
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.VERSION, "5.0");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "about:legacy-compat");
			transformer.transform(text, new StreamResult(response.getWriter()));
		} catch (IOException | TransformerException e) {
			LOG.severe(e);
		}
	}
	private void sendXML(HttpServletRequest request, HttpServletResponse response, GDocument document) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml; charset=UTF-8");
		try {
			response.getWriter().append(document.toXML().toStringBuffer());
		} catch (IOException e) {
			LOG.severe(e);
		}
	}
	public int execute(
		HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig, Record params
	) throws IOException, NoSuchProviderException, SQLException, URISyntaxException {
		try {
			List<FileItem> fields = super.prepare(request, servletConfig, params);
			if(!super.auth(params)) {
				super.clear();
				return HttpServletResponse.SC_FORBIDDEN;
			}
			int queryFuncType = super.getQueryFuncType();
			GDocument document = null;
			if(this.validate(params)) {
				document = this.document(request, response, fields, params, queryFuncType);
				if(document == null) {
					return HttpServletResponse.SC_NOT_FOUND;
				}
				if(queryFuncType == Query.QUERY_FUNC_TYPE_USER) {
					this.sendUser(request, response, document);
				} else if(super.getRequestType() == QueryImpl.REQUEST_TYPE_XML) {
					this.sendXML(request, response, document);
				} else if(super.getRequestType() == QueryImpl.REQUEST_TYPE_HTML) {
					this.sendHTML(request, response, params, document, queryFuncType);
				}
			} else {
				document = this.getDocument(request);
				this.executeMessage(document, params);
				this.setXslNameAndParam(document, params, queryFuncType);
				document.add(params);
				if(super.getRequestType() == QueryImpl.REQUEST_TYPE_XML) {
					this.sendXML(request, response, document);
				} else if(super.getRequestType() == QueryImpl.REQUEST_TYPE_HTML) {
					this.sendHTML(request, response, params, document, queryFuncType);
				}
			}
		} catch (Exception e) {
			this.abort();
			throw e;
		}
		return HttpServletResponse.SC_OK;
	}
}
