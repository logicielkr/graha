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

package kr.graha.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import kr.graha.lib.AuthParser;
import kr.graha.lib.Buffer;
import kr.graha.lib.LogHelper;
import kr.graha.lib.Record;
import kr.graha.lib.XMLGenerator;
import kr.graha.lib.XSLGenerator;
import kr.graha.lib.FileHelper;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Element;

/**
 * Graha(그라하) 서블릿
 * HTTP 요청(GET/POST)을 처리한다.
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class GeneratorServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public void init() {
		javax.servlet.ServletConfig c = this.getServletConfig();
		if(c.getInitParameter("LogLevel") != null) {
			System.setProperty("kr.graha.LogLevel", c.getInitParameter("LogLevel"));
		}
		LogHelper.setLogLevel(logger);
	}
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo().trim();
		if(pathInfo != null && (pathInfo.equals("") || (!pathInfo.endsWith(".xml") && !pathInfo.endsWith(".xsl")))) {
			if(pathInfo.indexOf(".xml/download/") == -1) {
				if(logger.isLoggable(Level.INFO)) {
					logger.info("[SC_NOT_FOUND]pathInfo = " + pathInfo);
				}
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		String id = pathInfo;
		if(id.startsWith("/")) {
			id = id.substring(1);
		}
		if(id.indexOf("/") < 1) {
			if(logger.isLoggable(Level.INFO)) {
				logger.info("[SC_NOT_FOUND]pathInfo = " + pathInfo);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if(logger.isLoggable(Level.CONFIG)) {
			logger.config("ContextPath = " + request.getContextPath());
			logger.config("Request Path = " + id);
			logger.config("XML File = " + id.substring(0, id.indexOf("/")));
			logger.config("query id = " + id.substring(id.indexOf("/") + 1));
			logger.config("Context Root Path = " + request.getServletContext().getRealPath("/"));
		}
		
		if(id != null && (id.substring(0, id.indexOf("/")).equals("") || id.substring(id.indexOf("/") + 1).equals(""))) {
			if(logger.isLoggable(Level.INFO)) {
				logger.info("[SC_NOT_FOUND]pathInfo = " + pathInfo);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		File config = new File(request.getServletContext().getRealPath("/WEB-INF/graha/" + id.substring(0, id.indexOf("/")) + ".xml"));
		if(!config.exists()) {
			if(logger.isLoggable(Level.INFO)) {
				logger.info("[SC_NOT_FOUND]config file = " + config.getPath());
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ServletUtil util = new ServletUtil();
		Element query = util.getQuery(config, id);
		
		if(query == null) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe("[SC_NOT_FOUND]query is null, Request Path = " + id);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		Record params = new Record();
		
		params.put("system.prefix", request.getContextPath() + request.getServletPath());
		params.put("system.config.file.name", File.separator + id.substring(0, id.indexOf("/")) + File.separator);
		params.put("system.suffix", ".xml"); 
		
		new HeaderAdapter().execute(request, params);
		List<FileItem> fields = new ParameterAdapter().execute(request, params, FileHelper.getFileInfo(query));
		new SessionAdapter().execute(request, params);
		new AttributeAdapter().execute(request, params);
		if(System.getProperties().containsKey("catalina.home")) {
			new UserRoleAdapter().execute(request, params);
		}
		
		try {
			javax.servlet.ServletConfig c = this.getServletConfig();
			if(c.getInitParameter("UserServletAdapter") != null) {
				ServletAdapter adapter = (ServletAdapter)Class.forName(c.getInitParameter("UserServletAdapter")).newInstance();
				adapter.execute(request, params);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
		}
		if(query.hasAttribute("auth") && query.getAttribute("auth") != null && !query.getAttribute("auth").trim().equals("")) {
			if(!AuthParser.auth(query.getAttribute("auth"), params)) {
				if(logger.isLoggable(Level.CONFIG)) {
					logger.config("[SC_FORBIDDEN]auth = " + query.getAttribute("auth"));
				}
				
				response.sendError(403);
				return;
			}
		}
		Connection con = null;
		if(id.indexOf(".xml/download/") > 0) {
			boolean isDownloadable = true;
			if(FileHelper.isAllow(query, params)) {
				if(FileHelper.isRequireConnection(query, "files/auth")) {
					isDownloadable = false;
					try {
						Record info = XMLGenerator.getConnectionInfo(query, config);
						if(info == null) {
							if(logger.isLoggable(Level.SEVERE)) {
								logger.severe("[SC_INTERNAL_SERVER_ERROR]Not exists database connection info");
							}
							throw new ServletException();
						}
						con = util.getConnection(info);
						if(con == null) {
							if(logger.isLoggable(Level.SEVERE)) {
								logger.severe("[SC_INTERNAL_SERVER_ERROR]database connection is null");
							}
							throw new ServletException();
						}
						XMLGenerator g = new XMLGenerator(query, params, con, config, request, response, info);
						isDownloadable = g.isDownloadable("files/auth");
					} catch (Exception e) {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe(LogHelper.toString(e));
						}
						response.sendError(500);
					} finally {
						try {
							if(con != null) {
								con.close();
								con = null;
							}
						} catch (SQLException e) {
							if(logger.isLoggable(Level.SEVERE)) {
								logger.severe(LogHelper.toString(e));
							}
						}
					}
				}
			} else {
				isDownloadable = false;
			}
			if(isDownloadable) {
				new DownloadAdapter().execute(request, response, query, params);
				return;
			} else {
				if(logger.isLoggable(Level.FINE)) {
					logger.fine("isDownloadable is false");
				}
				response.sendError(403);
				return;
			}
		}
		
		try {
			response.setCharacterEncoding("UTF-8");
			if(query.getAttribute("funcType") != null && query.getAttribute("funcType").equals("user")) {
				response.setContentType(query.getAttribute("contentType"));
			} else {
				if(request.getPathInfo().endsWith(".xml")) {
					response.setContentType("text/xml; charset=UTF-8");
				} else if(request.getPathInfo().endsWith(".xsl")) {
					response.setContentType("text/xsl; charset=UTF-8");
//					response.setContentType("application/xslt+xml; charset=UTF-8");
				}
			}
			
			if(request.getPathInfo().endsWith(".xml")) {
				Record info = XMLGenerator.getConnectionInfo(query, config);
				if(info == null) {
					if(logger.isLoggable(Level.SEVERE)) {
						logger.severe("[SC_INTERNAL_SERVER_ERROR]Not exists database connection info");
					}
					throw new ServletException();
				}
				if(FileHelper.isRequireConnection(query, "validation/command")) {
					con = util.getConnection(info);
					if(con == null) {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe("[SC_INTERNAL_SERVER_ERROR]database connection is null");
						}
						throw new ServletException();
					}
					con.setAutoCommit(false);
				}
				XMLGenerator g = new XMLGenerator(query, params, con, config, request, response, info);
				Buffer sb = g.validate();
				if(sb != null) {
					con.close();
					con = null;
					if(util.acceptsGZipEncoding(request, super.getServletConfig().getInitParameter("gzip"))) {
						response.addHeader("Content-Encoding", "gzip");
						java.util.zip.GZIPOutputStream gzip = null;
						try {
							gzip = new java.util.zip.GZIPOutputStream(response.getOutputStream());
							gzip.write(sb.toByte());
							gzip.close();
							gzip = null;
						} catch (IOException e) {
							if(logger.isLoggable(Level.SEVERE)) {
								logger.severe(LogHelper.toString(e));
							}
						} finally {
							if(gzip != null) {
								try {
									gzip.close();
								} catch (IOException e) {
									if(logger.isLoggable(Level.SEVERE)) {
										logger.severe(LogHelper.toString(e));
									}
								}
							}
						}
					} else {
						response.getWriter().append(sb.toStringBuffer());
					}
					return;
				} else {
					sb = new Buffer();
				}
				if(con == null) {
					con = util.getConnection(info);
					if(con == null) {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe("[SC_INTERNAL_SERVER_ERROR]database connection is null");
						}
						throw new ServletException();
					}
					con.setAutoCommit(false);
					g.setConnection(con);
				}
				g.processor(true);
				sb.append(g.execute());
				
				if(fields != null) {
					if(params.hasKey("query.row.total_update_count") && params.getInt("query.row.total_update_count") > 0 && FileHelper.isAllow(query, params)) {
						new UploadAdapter().execute(request, fields, query, params);
					} else {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe("hasKey : " + params.hasKey("query.row.total_update_count"));
							logger.severe("getInt : " + params.getInt("query.row.total_update_count"));
							logger.severe("isAllow : " + FileHelper.isAllow(query, params));
						}
					}
				}
				g.processor(false);
				if(query.getAttribute("funcType") != null && query.getAttribute("funcType").equals("user")) {
					StreamSource style = new StreamSource(new File(request.getServletContext().getRealPath("/WEB-INF/graha/" + query.getAttribute("xsl"))));
					TransformerFactory factory = TransformerFactory.newInstance();
					Transformer transformer = factory.newTransformer(style);
					Source text = new StreamSource(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
					transformer.transform(text, new StreamResult(response.getWriter()));
				} else {
					if(util.acceptsGZipEncoding(request, super.getServletConfig().getInitParameter("gzip"))) {
						response.addHeader("Content-Encoding", "gzip");
						java.util.zip.GZIPOutputStream gzip = null;
						try {
							gzip = new java.util.zip.GZIPOutputStream(response.getOutputStream());
							gzip.write(sb.toByte());
							gzip.close();
							gzip = null;
						} catch (IOException e) {
							if(logger.isLoggable(Level.SEVERE)) {
								logger.severe(LogHelper.toString(e));
							}
						} finally {
							if(gzip != null) {
								try {
									gzip.close();
								} catch (IOException e) {
									if(logger.isLoggable(Level.SEVERE)) {
										logger.severe(LogHelper.toString(e));
									}
								}
							}
						}
					} else {
						response.getWriter().append(sb.toStringBuffer());
					}
				}
			} else if(request.getPathInfo().endsWith(".xsl")) {
				if(request.getParameter("method") != null && request.getParameter("method").equals("post")) {
					params.put("header.method", "POST");
				} else if(request.getParameter("method") != null && request.getParameter("method").equals("error")) {
					params.put("header.method", "ERROR");
				}
				XSLGenerator g = new XSLGenerator(query, params, null, config, request);
				if(util.acceptsGZipEncoding(request, super.getServletConfig().getInitParameter("gzip"))) {
					response.addHeader("Content-Encoding", "gzip");
					java.util.zip.GZIPOutputStream gzip = null;
					try {
						gzip = new java.util.zip.GZIPOutputStream(response.getOutputStream());
						gzip.write(g.execute().toByte());
						gzip.close();
						gzip = null;
					} catch (IOException e) {
						if(logger.isLoggable(Level.SEVERE)) {
							logger.severe(LogHelper.toString(e));
						}
					} finally {
						if(gzip != null) {
							try {
								gzip.close();
							} catch (IOException e) {
								if(logger.isLoggable(Level.SEVERE)) {
									logger.severe(LogHelper.toString(e));
								}
							}
						}
					}
				} else {
					response.getWriter().append(g.execute().toStringBuffer());
				}
			}
			params.dump(logger, Level.FINEST);
		} catch (Exception e) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LogHelper.toString(e));
			}
			try {
				if(con != null) {
					con.rollback();
				}
			} catch (SQLException error) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(error));
				}
			}
			response.sendError(500);
		} finally {
			try {
				if(con != null) {
					con.commit();
					con.close();
					con = null;
				}
			} catch (SQLException e) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LogHelper.toString(e));
				}
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
