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
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import java.util.Iterator;
import java.nio.file.Paths;
import java.net.URI;
import java.net.URISyntaxException;
import kr.graha.post.xml.GDocument;

/**
 * Graha(그라하) files 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Files {
	private static final String nodeName = "files";
	private Files() {
	}
	
	private String maxMemorySize = null;
	private String tempDirectory = null;
	private String maxRequestSize = null;
/**
 * 파일 업로드/다운로드의 인증은 2중화 되어 있다.
 
 */
	private String auth = null;
	private List<File> file = null;
/**
 * 파일 다운로드 할 때만 사용한다.
 *
 * 상세보기나 수정/삭제 화면에서는 데이타베이스에서 가져온 것이 없다면, 파일목록도 표시하지 않는다.
 *
 * 파일 다운로드 할 때는 상세보기나 수정/삭제 화면의 데이타베이스 질의를 수행하는 대신, 이것을 사용한다.
 */
	private Auth auths = null;
	public String getMaxMemorySize() {
		return this.maxMemorySize;
	}
	private void setMaxMemorySize(String maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}
	public String getTempDirectory() {
		return this.tempDirectory;
	}
	private void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}
	public String getMaxRequestSize() {
		return this.maxRequestSize;
	}
	private void setMaxRequestSize(String maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}
	private String getAuth() {
		return this.auth;
	}
	private void setAuth(String auth) {
		this.auth = auth;
	}
	private void add(File file) {
		if(this.file == null) {
			this.file = new ArrayList<File>();
		}
		this.file.add(file);
	}
	private void setAuths(Auth auths) {
		this.auths = auths;
	}
	protected static String nodeName() {
		return Files.nodeName;
	}
	protected static Files load(Node element) {
		Files files = new Files();
		if(element != null) {
			files.loadAttr(element);
			files.loadElement(element);
			return files;
		}
		return null;
	}
	private void load(Node node, String parentNodeName) {
		if(STR.compareIgnoreCase(node.getNodeName(), "file")) {
			this.add(File.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "auth")) {
			this.setAuths(Auth.load((Element)node));
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
						if(STR.compareIgnoreCase(node.getNodeName(), "file")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "auth")) {
							this.load(node, null);
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
						if(STR.compareIgnoreCase(node.getNodeName(), "maxMemorySize")) {
							this.setMaxMemorySize(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "tempDirectory")) {
							this.setTempDirectory(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "maxRequestSize")) {
							this.setMaxRequestSize(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "auth")) {
							this.setAuth(node.getNodeValue());
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
		element.setAttribute("maxMemorySize", this.getMaxMemorySize());
		element.setAttribute("tempDirectory", this.getTempDirectory());
		element.setAttribute("maxRequestSize", this.getMaxRequestSize());
		element.setAttribute("auth", this.getAuth());
		if(this.file != null && this.file.size() > 0) {
			for(int i = 0; i < this.file.size(); i++) {
				element.appendChild(((File)this.file.get(i)).element());
			}
		}
		if(this.auths != null) {
			element.appendChild(this.auths.element());
		}
		return element;
	}
	protected Buffer beforeLi(Record param, int indent, String tabName, boolean rdf) {
		return this.li(param, indent, tabName, null, rdf);
	}
	protected Buffer afterLi(Record param, int indent, String tabName, boolean rdf) {
		return this.li(param, indent, null, tabName, rdf);
	}
	protected Buffer li(Record param, int indent, boolean rdf) {
		return this.li(param, indent, null, null, rdf);
	}
	protected Buffer before(Record param, int indent, String tabName, boolean rdf, String queryId, int queryFuncType, boolean title) {
		return this.file(param, indent, tabName, null, rdf, queryId, queryFuncType, title);
	}
	protected Buffer after(Record param, int indent, String tabName, boolean rdf, String queryId, int queryFuncType, boolean title) {
		return this.file(param, indent, null, tabName, rdf, queryId, queryFuncType, title);
	}
	protected Buffer file(Record param, int indent, boolean rdf, String queryId, int queryFuncType, boolean title) {
		return this.file(param, indent, null, null, rdf, queryId, queryFuncType, title);
	}
	private Buffer li(Record param, int indent, String before, String after, boolean rdf) {
		if(!STR.valid(this.getAuth()) || AuthUtility.auth(this.getAuth(), param)) {
			if(this.file != null && this.file.size() > 0) {
				Buffer xsl = new Buffer();
				for(int i = 0; i < this.file.size(); i++) {
					xsl.append(((File)this.file.get(i)).li(param, indent, before, after, rdf));
				}
				return xsl;
			}
		}
		return null;
	}
	private Buffer file(Record param, int indent, String before, String after, boolean rdf, String queryId, int queryFuncType, boolean title) {
		if(this.fileAllow(param)) {
			if(this.file != null && this.file.size() > 0) {
				Buffer xsl = new Buffer();
				for(int i = 0; i < this.file.size(); i++) {
					xsl.append(((File)this.file.get(i)).file(param, indent, before, after, rdf, queryId, queryFuncType, title));
				}
				return xsl;
			}
		}
		return null;
	}
	protected boolean fileAllow(Record param) {
		if(!STR.valid(this.getAuth()) || AuthUtility.auth(this.getAuth(), param)) {
			return true;
		}
		return false;
	}
	protected boolean downloadable(Record params, ConnectionFactory connectionFactory) throws NoSuchProviderException, SQLException {
		if(this.fileAllow(params)) {
			if(auths == null) {
				return true;
			} else {
				return auths.check(params, connectionFactory);
			}
		} else {
			return false;
		}
	}
	protected void download(HttpServletRequest request, HttpServletResponse response, Record params) throws IOException {
/*
파라미터로 들어온 것들을 param. 을 붙이지 않고 처리하는 것.
즉 파라미터로 query. 으로 시작하는 것들이 들어오면,
param. 이 아니라, query. 으로 처리됨.
*/
		Enumeration<String> p = request.getParameterNames();
		while (p.hasMoreElements()) {
			String key = p.nextElement();
			params.puts(Record.key(Record.PREFIX_TYPE_UNKNOWN, key + ".0"), params.getString(Record.key(Record.PREFIX_TYPE_PARAM, key)));
		}
		String filePath = request.getPathInfo().trim();
		if(filePath.indexOf(".xml/download/") > 0) {
			filePath = filePath.substring(filePath.indexOf(".xml/download/") + ".xml/download/".length());
		} else if(filePath.indexOf(".html/download/") > 0) {
			filePath = filePath.substring(filePath.indexOf(".html/download/") + ".html/download/".length());
		}
		if(
			request.getServletContext().getMajorVersion() < 3
			|| (
				request.getServletContext().getMajorVersion() == 3
				&& request.getServletContext().getMinorVersion() == 0
			)
		) {
			filePath = new String(filePath.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		}
		if(
			filePath.indexOf("/") < 0 ||
			filePath.substring(0, filePath.indexOf("/")).startsWith("..") ||
			filePath.substring(0, filePath.indexOf("/")).startsWith("/") ||
			filePath.substring(0, filePath.indexOf("/")).startsWith("%2F") ||
			filePath.substring(filePath.indexOf("/") + 1).indexOf("/") >= 0 ||
			filePath.substring(filePath.indexOf("/") + 1).indexOf("%2F") >= 0
		) {
			LOG.warning("[SC_INVALID_REQUEST]File Path = " + filePath);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String fileName = filePath.substring(0, filePath.indexOf("/"));
		if(STR.valid(this.file)) {
			for(int i = 0; i < this.file.size(); i++) {
				File f = this.file.get(i);
				if(STR.compareIgnoreCase(fileName, f.getName())) {
					f.download(filePath, request, response, params);
					return;
				}
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	protected void upload(List<FileItem> fields, Record params) throws IOException, URISyntaxException {
		this.deleteFile(fields, params);
		this.saveFile(fields, params);
	}
	private File getFile(String fileName) {
		if(STR.valid(this.file)) {
			for(int i = 0; i < this.file.size(); i++) {
				File f = this.file.get(i);
				if(STR.compareIgnoreCase(fileName, f.getName())) {
					return f;
				}
			}
		}
		return null;
	}
	private void saveFile(List<FileItem> fields, Record params) throws IOException, URISyntaxException {
		Iterator<FileItem> it = fields.iterator();
		int idx = 0;
		while (it.hasNext()) {
			FileItem fileItem = it.next();
			boolean isFormField = fileItem.isFormField();
			if(!isFormField) {
				LOG.finer(fileItem.getFieldName());
				if(fileItem.getSize() == 0) {
					continue;
				}
				String fileName = fileItem.getFieldName();
				if(fileName.lastIndexOf(".") >= 0) {
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
				}
				File file = this.getFile(fileName);
				URI uri = file.saveFile(fileItem, params);
				if(uri != null) {
					params.put(Record.key(Record.PREFIX_TYPE_NONE, "uploaded.file.path." + idx), Paths.get(uri).toString());
					idx++;
				}
			}
		}
	}
	private void deleteFile(List<FileItem> fields, Record params) throws IOException, URISyntaxException {
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem fileItem = it.next();
			boolean isFormField = fileItem.isFormField();
			if(isFormField) {
				if(fileItem.getFieldName().startsWith("_deletefile_.")) {
					String fileName = fileItem.getFieldName().substring("_deletefile_.".length());
					if(fileName.lastIndexOf(".") >= 0) {
						fileName = fileName.substring(0, fileName.lastIndexOf("."));
					}
					File file = this.getFile(fileName);
					file.deleteFile(fileItem, params);
				}
			}
		}
	}
	protected void delete(GDocument document, Record params) {
		if(STR.valid(this.file)) {
			for(int i = 0; i < this.file.size(); i++) {
				File f = this.file.get(i);
				f.delete(document, params);
			}
		}
	}
	protected void list(GDocument document, Record params) throws IOException {
		if(STR.valid(this.file)) {
			for(int i = 0; i < this.file.size(); i++) {
				File f = this.file.get(i);
				f.list(document, params);
			}
		}
	}
}
