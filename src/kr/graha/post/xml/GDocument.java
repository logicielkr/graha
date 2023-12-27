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


package kr.graha.post.xml;

import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.post.lib.Key;
import kr.graha.helper.STR;
import kr.graha.helper.XML;
import kr.graha.helper.LOG;
import java.util.List;
import java.util.Iterator;

/**
 * GDocument
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GDocument {
/**
 * <query> 요소의 output 속성값
 * 기본값 : xml (이 값이 rdf 가 아니라면, xml 로 처리된다)
 */
	private String output;
/**
 * rdf 형식으로 출력되는 경우 xmlns:uc 의 값
 * <query> 요소의 uc 속성값을 사용하겠지만,
 * 이 값이 정의되어 있지 않은 경우,
 * HttpServletRequest 에서 자동으로 조합한다.
 */
	private String uc;
/**
 * <query> 요소의 uc 속성값이 없는 경우에, uc 값을 자동으로 만든다.
 */
	private HttpServletRequest request;
	private List<GCode> codes = null;
	private List<GPages> pages = null;
	private List<GParam> params = null;
	private List<GParam> props = null;
	private List<GParam> results = null;
	private List<GParam> errors = null;
	private List<GMessage> messages = null;
	private List<GFile> files = null;
	private List<GRows> rows = null;
/**
 * 확장자를 제외한 xsl 파일이름
 * 이 값에 .xsl 을 붙여서 xslPath 를 조합한다.
 */
	private String xslName = null;
/**
 * xsl 파일이름
 */
	private String xslPath = null;
/**
 * xsl 경로 붙이는 파라미터
 */
	private String xslParam = null;
	public GDocument(String output, String uc, HttpServletRequest request) {
		this.setOutput(output);
		this.setUc(uc);
		this.request = request;
	}
	private String getOutput() {
		return this.output;
	}
	private void setOutput(String output) {
		this.output = output;
	}
	public boolean containsKey(Key key) {
		if(STR.valid(this.rows)) {
			if(key.getKey() != null && key.getKey().indexOf(".") > 0) {
				String rowName = key.getKey().substring(0, key.getKey().indexOf("."));
				String columnName = key.getKey().substring(key.getKey().indexOf(".") + 1);
				for(int i = 0; i < this.rows.size(); i++) {
					GRows row = (GRows)this.rows.get(i);
					if(STR.compareIgnoreCase(row.getName(), rowName)) {
						return row.containsKey(columnName);
					}
				}
			} else {
				GRows row = (GRows)this.rows.get(0);
				return row.containsKey(key.getKey());
			}
		}
		return false;
	}
	public Object get(Key key) {
		if(STR.valid(this.rows)) {
			if(key.getKey() != null && key.getKey().indexOf(".") > 0) {
				String rowName = key.getKey().substring(0, key.getKey().indexOf("."));
				String columnName = key.getKey().substring(key.getKey().indexOf(".") + 1);
				for(int i = 0; i < this.rows.size(); i++) {
					GRows row = (GRows)this.rows.get(i);
					if(STR.compareIgnoreCase(row.getName(), rowName)) {
						return row.get(columnName);
					}
				}
			} else {
				GRows row = (GRows)this.rows.get(0);
				return row.get(key.getKey());
			}
		}
		return null;
	}
	private String getUc() {
		if(this.rdf()) {
			if(!STR.valid(this.uc)) {
				String id = this.request.getPathInfo().trim();
				if(id.startsWith("/")) {
					id = id.substring(1);
				}
				return (this.request.getScheme() + "://"+ this.request.getServerName() + ":" + this.request.getServerPort() + "" + this.request.getContextPath() + this.request.getServletPath() + "/" + id.substring(0, id.indexOf("/") + 1));
			}
			return this.uc;
		} else {
			return null;
		}
	}
	private void setUc(String uc) {
		this.uc = uc;
	}
	public void setXslName(String xslName) {
		this.xslName = xslName;
		this.xslPath = xslName + ".xsl";
	}
	private String getXslParam() {
		return this.xslParam;
	}
	public void setXslParam(String xslParam) {
		this.xslParam = xslParam;
	}
	public void appendXslParam(StringBuffer xslParam) {
		if(this.xslParam == null) {
			this.xslParam = xslParam.toString();
		} else {
			this.xslParam = this.xslParam + xslParam.toString();
		}
	}
	private String getXslPath() {
		return this.xslPath;
	}
	public void setXslPath(String xslPath) {
		this.xslPath = xslPath;
	}
	private String getXslFullPath() {
		if(STR.valid(this.getXslParam())) {
			return this.getXslPath() + "?" + this.getXslParam();
		} else {
			return this.getXslPath();
		}
	}
	public List<GRows> getRows() {
		return this.rows;
	}
	private boolean rdf() {
		if(STR.valid(this.getOutput()) && STR.compareIgnoreCase(this.getOutput(), "rdf")) {
			return true;
		} else {
			return false;
		}
	}
	public void add(GRows row) {
		if(this.rows == null) {
			this.rows = new ArrayList<GRows>();
		}
		this.rows.add(row);
	}
	public void add(GPages page) {
		if(this.pages == null) {
			this.pages = new ArrayList<GPages>();
		}
		this.pages.add(page);
	}
	public void add(GCode code) {
		if(this.codes == null) {
			this.codes = new ArrayList<GCode>();
		}
		this.codes.add(code);
	}
	public void add(GFile file) {
		if(this.files == null) {
			this.files = new ArrayList<GFile>();
		}
		this.files.add(file);
	}
	public void clear() {
		if(this.files != null) {
			this.files.clear();
			this.files = null;
		}
		if(this.rows != null) {
			this.rows.clear();
			this.rows = null;
		}
		if(this.pages != null) {
			this.pages.clear();
			this.pages = null;
		}
	}
	public void addParam(GParam param) {
		if(this.params == null) {
			this.params = new ArrayList<GParam>();
		}
		this.params.add(param);
	}
	public void addProp(GParam param) {
		if(this.props == null) {
			this.props = new ArrayList<GParam>();
		}
		this.props.add(param);
	}
	public void addResult(GParam param) {
		if(this.results == null) {
			this.results = new ArrayList<GParam>();
		}
		this.results.add(param);
	}
	public void addError(GParam param) {
		if(this.errors == null) {
			this.errors = new ArrayList<GParam>();
		}
		this.errors.add(param);
	}
	public void add(GMessage message) {
		if(message == null) {
			return;
		}
		if(this.messages == null) {
			this.messages = new ArrayList<GMessage>();
		}
		this.messages.add(message);
	}
	private void paramToXML(Buffer xml, List<GParam> params, int type, boolean rdf) {
		String tag = null;
		if(type == GParam.PARAM_TYPE_PARAMS) {
			tag = "params";
		} else if(type == GParam.PARAM_TYPE_PROPS) {
			tag = "props";
		} else if(type == GParam.PARAM_TYPE_RESULTS) {
			tag = "results";
		} else if(type == GParam.PARAM_TYPE_ERRORS) {
			tag = "errors";
		}
		if(rdf) {
			xml.appendL(1, "<RDF:Description uc:for=\"urn:root:" + tag + "\"><uc:" + tag + ">");
		} else {
			if(type == GParam.PARAM_TYPE_PARAMS || type == GParam.PARAM_TYPE_ERRORS) {
				xml.appendL(1, "<" + tag + " for=\"data\">");
			} else {
				xml.appendL(1, "<" + tag + ">");
			}
		}
		for(int i = 0; i < params.size(); i++) {
			GParam param = (GParam)params.get(i);
			param.toXML(xml, rdf);
		}
		if(rdf) {
			xml.appendL(1, "</uc:" + tag + "></RDF:Description>");
		} else {
			xml.appendL(1, "</" + tag + ">");
		}
	}
	public void toXML(Buffer xml) throws IOException {
		boolean rdf = this.rdf();
		xml.appendL("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if(rdf) {
			xml.appendL("<?xml-stylesheet type=\"text/xsl\" href=\"" + this.getXslFullPath() + "\" ?>");
			xml.appendL("<RDF:RDF xmlns:RDF=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:uc=\"" + this.getUc() + "\">");
		} else {
			xml.appendL("<?xml-stylesheet type=\"text/xsl\" href=\"" + this.getXslFullPath() + "\" ?>");
			xml.appendL("<document>");
		}
		if(this.codes != null && this.codes.size() > 0) {
			for(int i = 0; i < this.codes.size(); i++) {
				GCode code = (GCode)this.codes.get(i);
				code.toXML(xml, rdf);
			}
		}
		if(this.files != null && this.files.size() > 0) {
			for(int i = 0; i < this.files.size(); i++) {
				GFile file = (GFile)this.files.get(i);
				file.toXML(xml, rdf);
			}
		}
		if(this.rows != null && this.rows.size() > 0) {
			for(int i = 0; i < this.rows.size(); i++) {
				GRows row = (GRows)this.rows.get(i);
				row.toXML(xml, rdf);
			}
		}
		if(this.params != null && this.params.size() > 0) {
			paramToXML(xml, params, GParam.PARAM_TYPE_PARAMS, rdf); 
		}
		if(this.props != null && this.props.size() > 0) {
			paramToXML(xml, props, GParam.PARAM_TYPE_PROPS, rdf); 
		}
		if(this.results != null && this.results.size() > 0) {
			paramToXML(xml, results, GParam.PARAM_TYPE_RESULTS, rdf); 
		}
		if(this.errors != null && this.errors.size() > 0) {
			paramToXML(xml, errors, GParam.PARAM_TYPE_ERRORS, rdf); 
		}
		if(this.messages != null && this.messages.size() > 0) {
			if(rdf) {
				xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:messages\">");
			} else {
				xml.appendL(1, "<messages>");
			}
			for(int i = 0; i < this.messages.size(); i++) {
				GMessage message = (GMessage)this.messages.get(i);
				message.toXML(xml, rdf);
			}
			if(rdf) {
				xml.appendL(1, "</RDF:Seq>");
			} else {
				xml.appendL(1, "</messages>");
			}
		}
		if(this.pages != null && this.pages.size() > 0) {
			for(int i = 0; i < this.pages.size(); i++) {
				GPages page = (GPages)this.pages.get(i);
				page.toXML(xml, rdf);
			}
		}
		if(rdf) {
			xml.appendL("</RDF:RDF>");
		} else {
			xml.appendL("</document>");
		}
	}
	public Buffer toXML() throws IOException {
		Buffer xml = new Buffer();
		toXML(xml);
		return xml;
	}
	public void add(Record params) {
		if(params != null && !params.isEmpty()) {
			Iterator<Key> it = params.keySet().iterator();
			while(it.hasNext()) {
				Key key = (Key)it.next();
				if(
					key.getPrefix() == Record.PREFIX_TYPE_PARAM ||
					key.getPrefix() == Record.PREFIX_TYPE_ERROR ||
					key.getPrefix() == Record.PREFIX_TYPE_RESULT ||
					(
						key.getPrefix() == Record.PREFIX_TYPE_PROP &&
						!key.endsWith(".public") &&
						params.getBoolean(Record.key(key, "public"))
					) ||
					(
						key.getPrefix() == Record.PREFIX_TYPE_MESSAGE &&
						!key.endsWith(".public") &&
						params.getBoolean(Record.key(key, "public"))
					)
				) {
					if(key.getPrefix() != Record.PREFIX_TYPE_PROP && params.isArray(key)) {
						List<String> items = params.getArray(key);
						for(String item : items) {
							if(item != null) {
								GParam param = new GParam(key);
								if(key.getPrefix() == Record.PREFIX_TYPE_ERROR && item.startsWith("message.") && params.hasKey(Record.key(item))) {
									param.setValue(params.getString(Record.key(item)));
								} else {
									param.setValue(XML.fix(item));
								}
								if(key.getPrefix() == Record.PREFIX_TYPE_PARAM) {
									this.addParam(param);
								} else if(key.getPrefix() == Record.PREFIX_TYPE_RESULT) {
									this.addResult(param);
								} else if(key.getPrefix() == Record.PREFIX_TYPE_ERROR) {
									this.addError(param);
								} else if(key.getPrefix() == Record.PREFIX_TYPE_PROP) {
									this.addProp(param);
								} else if(key.getPrefix() == Record.PREFIX_TYPE_MESSAGE) {
									this.add(new GMessage(key.getKey(), XML.fix(item)));
								}
							}
						}
					} else {
						if(params.getObject(key) != null) {
							GParam param = new GParam(key);
							if(key.getPrefix() == Record.PREFIX_TYPE_ERROR && (params.getString(key)).startsWith("message.") && params.hasKey(Record.key(params.getString(key)))) {
								param.setValue(params.getString(Record.key(params.getString(key))));
							} else {
								if(params.getObject(key) instanceof String) {
									param.setValue(XML.fix((String)params.getString(key)));
								} else {
									param.setValue(params.getString(key));
								}
							}
							if(key.getPrefix() == Record.PREFIX_TYPE_PARAM) {
								this.addParam(param);
							} else if(key.getPrefix() == Record.PREFIX_TYPE_RESULT) {
								this.addResult(param);
							} else if(key.getPrefix() == Record.PREFIX_TYPE_ERROR) {
								this.addError(param);
							} else if(key.getPrefix() == Record.PREFIX_TYPE_PROP) {
								this.addProp(param);
							} else if(key.getPrefix() == Record.PREFIX_TYPE_MESSAGE) {
								
								LOG.out(key.getKey());
								LOG.out(params.getString(key));
								
								if(params.getObject(key) instanceof String) {
									this.add(new GMessage(key.getKey(), XML.fix((String)params.getString(key))));
								} else {
									this.add(new GMessage(key.getKey(), params.getString(key)));
								}
							}
						}
					}
				}
			}
		}
	}
}
