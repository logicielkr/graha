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

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchProviderException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.graha.helper.DB;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.element.XmlElement;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.interfaces.Encryptor;
import kr.graha.post.interfaces.Processor;
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.model.utility.SQLInfo;
import kr.graha.post.model.utility.SQLParameter;
import kr.graha.post.xml.GColumn;
import kr.graha.post.xml.GDocument;
import kr.graha.post.xml.GPages;
import kr.graha.post.xml.GRows;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) command 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Command extends SQLExecutor {
	private static final String nodeName = "command";
	private Command() {
	}
	
	private String encrypt = null;
	private String name = null;
	private String multi = null;
	
	private String type = null;
	private String className = null;
	private String cond = null;
	
	private Node sql = null;
	private Node sqlCnt = null;
	private List<Param> param = null;
	private List<Encrypt> encrypts = null;
	private List<DecryptColumn> decrypt = null;
	private List<PatternColumn> pattern = null;
	private Boolean valid = null;
	private String getEncrypt() {
		return this.encrypt;
	}
	private void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	protected String getMulti() {
		return this.multi;
	}
	private void setMulti(String multi) {
		this.multi = multi;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type  = type ;
	}
	private String getClassName() {
		return this.className;
	}
	private void setClassName(String className) {
		this.className  = className ;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond  = cond ;
	}
	
	private Node getSql() {
		return this.sql;
	}
	private void setSql(Node sql) {
		this.sql = sql;
	}
	private Node getSqlCnt() {
		return this.sqlCnt;
	}
	private void setSqlCnt(Node sqlCnt) {
		this.sqlCnt = sqlCnt;
	}
	protected boolean valid(Record params) {
		if(this.valid == null) {
			this.valid = true;
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, params)) {
				if(!AuthUtility.auth(tabAuthInfo, params)) {
					this.valid = false;
				}
			}
		}
		return this.valid.booleanValue();
	}
	private void add(Param param) {
		if(this.param == null) {
			this.param = new ArrayList<Param>();
		}
		this.param.add(param);
	}
	private void add(Encrypt encrypt) {
		if(this.encrypts == null) {
			this.encrypts = new ArrayList<Encrypt>();
		}
		this.encrypts.add(encrypt);
	}
	private void add(DecryptColumn decrypt) {
		if(this.decrypt == null) {
			this.decrypt = new ArrayList<DecryptColumn>();
		}
		this.decrypt.add(decrypt);
	}
	private void add(PatternColumn pattern) {
		if(this.pattern == null) {
			this.pattern = new ArrayList<PatternColumn>();
		}
		this.pattern.add(pattern);
	}
	protected static String nodeName() {
		return Command.nodeName;
	}
	protected static Command load(Element element) {
		Command command = new Command();
		if(element != null) {
			command.loadAttr(element);
			command.loadElement(element);
			return command;
		}
		return null;
	}
	private void loads(Node element, String parentNodeName) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					load(node, parentNodeName);
				}
			}
		}
	}
	private void load(Node node, String parentNodeName) {
		if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
			this.setSql(node);
		} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_cnt")) {
			this.setSqlCnt(node);
		} else if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
			this.add(Param.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
			this.add(Encrypt.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
			if(parentNodeName != null) {
				if(STR.compareIgnoreCase(parentNodeName, "decrypt")) {
					this.add(DecryptColumn.load(node));
				} else if(STR.compareIgnoreCase(parentNodeName, "pattern")) {
					this.add(PatternColumn.load(node));
				}
			}
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
						if(STR.compareIgnoreCase(node.getNodeName(), "sql")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "sql_cnt")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "params")) {
							this.loads(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.load(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypts")) {
							this.loads(node, null);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "decrypt")) {
							this.loads(node, "decrypt");
						} else if(STR.compareIgnoreCase(node.getNodeName(), "pattern")) {
							this.loads(node, "pattern");
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
						if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "multi")) {
							this.setMulti(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "type")) {
							this.setType(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "class")) {
							this.setClassName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
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
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("name", this.getName());
		element.setAttribute("multi", this.getMulti());
		element.setAttribute("type", this.getType());
		element.setAttribute("class", this.getClassName());
		element.setAttribute("cond", this.getCond());
		element.appendChild(this.getSql());
		element.appendChild(this.getSqlCnt());

		if(this.param != null && this.param.size() > 0) {
			XmlElement child = element.createElement("params");
			for(int i = 0; i < this.param.size(); i++) {
				child.appendChild(((Param)this.param.get(i)).element());
			}
		}
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		if(this.decrypt != null && this.decrypt.size() > 0) {
			XmlElement child = element.createElement("decrypt");
			for(int i = 0; i < this.decrypt.size(); i++) {
				child.appendChild(((DecryptColumn)this.decrypt.get(i)).element());
			}
		}
		if(this.pattern != null && this.pattern.size() > 0) {
			XmlElement child = element.createElement("pattern");
			for(int i = 0; i < this.pattern.size(); i++) {
				child.appendChild(((PatternColumn)this.pattern.get(i)).element());
			}
		}
		return element;
	}
	protected int execute(
		GDocument document,
		Record params,
		HttpServletRequest request,
		HttpServletResponse response,
		List<Command> commands,
		List<Tab> tabs,
		ConnectionFactory connectionFactory, 
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
//		if(STR.valid(this.getCond()) && !AuthUtility.auth(this.getCond(), params)) {
		if(!this.valid(params)) {
			return 0;
		}
		int result = 0;
		if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY) {
			if(STR.compareIgnoreCase(this.getType(), "native")) {
				try {
					Processor processor = (Processor)Class.forName(this.getClassName()).getConstructor().newInstance();
					processor.execute(request, response, params, connectionFactory.getConnection());
				} catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					LOG.severe(e);
				}
				return 0;
			}
		}
		super.setConnectionFactory(connectionFactory);
		Buffer sql = super.parseSQL(this.sql, params);
		Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
		List<SQLParameter> parameters = new ArrayList();
		if(STR.valid(this.param)) {
			for(int x = 0; x < this.param.size(); x++) {
				Param p = (Param)this.param.get(x);
				if(STR.startsWithIgnoreCase(p.getValue(), "sequence.")) {
					Integer nextSequenceValue = SQLExecutor.getNextSequenceIntegerValue(p.getValue(), super.getConnectionFactory());
					SQLParameter parameter =  new SQLParameter(nextSequenceValue, p.getDataType());
					parameters.add(parameter);
				} else {
					SQLParameter parameter =  p.getValue(
						params,
						encryptor
					);
					if(parameter != null) {
						parameters.add(parameter);
					}
				}
			}
		}
		SQLInfo sqlInfo = null;
		if(queryFuncType == Query.QUERY_FUNC_TYPE_LIST) {
			int count = 0;
			int pageSize = Integer.parseInt(((Element)this.sql).getAttribute("pageSize"));
			int pageGroupSize = Integer.parseInt(((Element)this.sql).getAttribute("pageGroupSize"));
			if(params.getInt(Record.key(Record.PREFIX_TYPE_PARAM, "page")) == 0) {
				params.put(Record.key(Record.PREFIX_TYPE_PARAM, "page"), 1);
			}
			int page = params.getInt(Record.key(Record.PREFIX_TYPE_PARAM, "page"));
			if(this.sqlCnt != null) {
				count = this.getCountForList(super.parseSQL(this.sqlCnt, params), parameters);
			} else {
				count = this.getCountForList(connectionFactory.sqlForCount(sql.toString()), parameters);
			}
			params.put(Record.key(Record.PREFIX_TYPE_QUERY, "total", "count"), count);
			if(commands.size() == 1) {
				document.add(GPages.load(null, page, count, pageSize, pageGroupSize));
			} else {
				document.add(GPages.load(this.getName(), page, count, pageSize, pageGroupSize));
			}
			if(count > 0) {
				parameters.add(new SQLParameter(pageSize, Param.DATA_TYPE_INT));
				parameters.add(new SQLParameter((page - 1) * pageSize, Param.DATA_TYPE_INT));
				sqlInfo = new SQLInfo(connectionFactory.sqlForList(sql.toString()), parameters);
			} else {
				
			}
		} else {
			sqlInfo = new SQLInfo(sql, parameters);
		}
		if(sqlInfo != null) {
			if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY && STR.compareIgnoreCase(this.getType(), "plsql")) {
				sqlInfo.setType(SQLInfo.TYPE_CSTMT);
				return super.executeUpdate(sqlInfo);
			}
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = super.prepareStatement(sqlInfo.getSql());
				super.bind(pstmt, parameters);
				if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY) {
					if(pstmt.execute()) {
						rs = pstmt.getResultSet();
					} else {
						result = pstmt.getUpdateCount();
					}
				} else {
					rs = pstmt.executeQuery();
				}
				if(rs == null) {
					if(STR.valid(this.getName())) {
						document.add(new GRows(this.getName(), new GColumn("rowcount", java.sql.Types.INTEGER, "int"), result));
						params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName(), "count"), result);
					}
				} else {
					Tab tab = Table.getTab(tabs, this.getName(), params);
					boolean columnAuto = false;
					if(tab != null) {
						if(STR.compareIgnoreCase(tab.getColumn(), "auto")) {
							columnAuto = true;
						}
					}
					java.util.Map<String, String> encrypted = null;
					if(encryptor != null) {
						encrypted = this.getEncrypted();
					}
					java.util.Map<String, String> pattern = this.getPattern();
					GRows grows = GRows.load(
						rs,
						this.getName(),
						encryptor,
						encrypted,
						pattern,
						super.getConnectionFactory(),
						STR.trueValue(this.getMulti()),
						columnAuto
					);
					DB.close(rs);
					rs = null;
					if(queryFuncType != Query.QUERY_FUNC_TYPE_QUERY) {
						result = grows.size();
					}
					if(grows.size() > 0) {
						document.add(grows);
					}
					if(STR.valid(this.getName())) {
						params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName(), "count"), grows.size());
					}
					if(queryFuncType == Query.QUERY_FUNC_TYPE_QUERY) {
						params.put(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "resultset"), true);
					}
				}
				DB.close(pstmt);
				pstmt = null;
			} catch (SQLException e) {
				LOG.severe(e);
				throw e;
			} finally {
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
			}
		}
		return result;
	}
	private int getCountForList(Buffer sql, List<SQLParameter> parameters) throws SQLException {
		return this.getCountForList(sql.toString(), parameters);
	}
	private int getCountForList(String sql, List<SQLParameter> parameters) throws SQLException {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = super.prepareStatement(sql);
			super.bind(pstmt, parameters);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
			DB.close(rs);
			rs = null;
			DB.close(pstmt);
			pstmt = null;
		} catch (SQLException e) {
			LOG.severe(e);
			throw e;
		} finally {
			DB.close(rs);
			rs = null;
			DB.close(pstmt);
			pstmt = null;
		}
		return result;
	}
	private Map<String, String> getEncrypted() {
		if(STR.valid(this.decrypt)) {
			Map<String, String> map = new java.util.Hashtable<String, String>();
			for(int i = 0; i < this.decrypt.size(); i++) {
				DecryptColumn c = this.decrypt.get(i);
				if(STR.valid(c.getEncrypt())) {
					map.put(c.getName(), c.getEncrypt());
				}
			}
			return map;
		}
		return null;
	}
	private Map<String, String> getPattern() {
		if(STR.valid(this.pattern)) {
			Map<String, String> map = new java.util.Hashtable<String, String>();
			for(int i = 0; i < this.pattern.size(); i++) {
				PatternColumn c = this.pattern.get(i);
				if(STR.valid(c.getPattern())) {
					map.put(c.getName(), c.getPattern());
				}
			}
			return map;
		}
		return null;
	}
}
