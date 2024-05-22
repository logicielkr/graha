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
import kr.graha.post.lib.Record;
import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import kr.graha.post.element.XmlElement;
import kr.graha.post.xml.GDocument;
import kr.graha.post.xml.GRows;
import kr.graha.post.xml.GRow;
import kr.graha.post.xml.GColumn;
import kr.graha.post.interfaces.ConnectionFactory;
import java.sql.SQLException;
import kr.graha.post.model.utility.SQLExecutor;
import kr.graha.post.model.utility.SQLInfo;
import kr.graha.post.model.utility.SQLParameter;
import java.sql.PreparedStatement;
import kr.graha.helper.DB;
import kr.graha.helper.LOG;
import kr.graha.post.interfaces.Encryptor;
import java.util.Map;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import kr.graha.post.lib.ParsingException;
import kr.graha.post.model.utility.TextParser;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;

/**
 * Graha(그라하) table 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Table extends SQLExecutor {
	private static final String nodeName = "table";
	public static int SQL_TYPE_INSERT = 1;
	public static int SQL_TYPE_UPDATE = 2;
	public static int SQL_TYPE_DELETE = 3;
	public static int SQL_TYPE_WHERE = 4;
	private Table() {
	}
	
	private String name = null;
	private String _tableName = null;
	private String label = null;
	private String encrypt = null;
	private String multi = null;
	private String append = null;
	private String total = null;
	private String queryToParam = null;
	private List<Column> _column = null;
	private List<Encrypt> encrypts = null;
	private Node order = null;
	private List<Where> where = null;
	private String cond = null;
	private Boolean valid = null;
	private Column getPrimaryColumnForInsert(Record param) {
		if(this.getColumn() != null && this.getColumn().size() > 0) {
			for(int i = 0; i < this.getColumn().size(); i++) {
				if(
					((Column)this.getColumn().get(i)).valid(param) &&
					STR.trueValue(((Column)this.getColumn().get(i)).getPrimary()) &&
					STR.compareIgnoreCase("generate", ((Column)this.getColumn().get(i)).getInsert())
				) {
					return (Column)this.getColumn().get(i);
				}
			}
		}
		return null;
	}
	protected Column getPrimaryColumnByValue(String columnValue, Record param) {
		if(STR.valid(columnValue)) {
			if(this.getColumn() != null && this.getColumn().size() > 0) {
				for(int i = 0; i < this.getColumn().size(); i++) {
					if(
						((Column)this.getColumn().get(i)).valid(param) &&
						STR.trueValue(((Column)this.getColumn().get(i)).getPrimary()) &&
						STR.compareIgnoreCase(columnValue, ((Column)this.getColumn().get(i)).getValue())
					) {
						return (Column)this.getColumn().get(i);
					}
				}
			}
		}
		return null;
	}
	protected Column getColumn(String columnNameOrValue, Record param) {
		if(STR.valid(columnNameOrValue)) {
			if(this.getColumn() != null && this.getColumn().size() > 0) {
				for(int i = 0; i < this.getColumn().size(); i++) {
					if(!((Column)this.getColumn().get(i)).valid(param)) {
						continue;
					}
					if(STR.compareIgnoreCase(columnNameOrValue, ((Column)this.getColumn().get(i)).getName())) {
						return (Column)this.getColumn().get(i);
					} else if(
						STR.startsWithIgnoreCase(columnNameOrValue, "query.") &&
						STR.compareIgnoreCase(columnNameOrValue.substring(6), ((Column)this.getColumn().get(i)).getName())
					) {
						return (Column)this.getColumn().get(i);
					} else 	if(
						(
							STR.startsWithIgnoreCase(columnNameOrValue, "param.") ||
							STR.startsWithIgnoreCase(columnNameOrValue, "prop.") ||
							STR.startsWithIgnoreCase(columnNameOrValue, "result.")
						) &&
						STR.compareIgnoreCase(columnNameOrValue, ((Column)this.getColumn().get(i)).getValue())
					) {
						return (Column)this.getColumn().get(i);
					}
				}
			}
		}
		return null;
	}
	protected boolean existsForeignColumn(Record param) {
		if(this.getColumn() != null) {
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = (Column)this.getColumn().get(i);
				if(c.valid(param) && STR.trueValue(c.getForeign())) {
					return true;
				}
			}
		}
		return false;
	}
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getTableName(Record param) {
		if(param == null) {
			return this._tableName;
		} else {
			return TextParser.parse(this._tableName, param);
		}
	}
	private void setTableName(String tableName) {
		this._tableName = tableName;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getEncrypt() {
		return this.encrypt;
	}
	private void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	protected String getMulti() {
		return this.multi;
	}
	private void setMulti(String multi) {
		this.multi = multi;
	}
	private String getAppend() {
		return this.append;
	}
	private void setAppend(String append) {
		this.append = append;
	}
	private String getTotal() {
		return this.total;
	}
	private void setTotal(String total) {
		this.total = total;
	}
	private Node getOrder() {
		return this.order;
	}
	private void setOrder(Node order) {
		this.order = order;
	}
	private String getQueryToParam() {
		return this.queryToParam;
	}
	private void setQueryToParam(String queryToParam) {
		this.queryToParam = queryToParam;
	}
	private String getCond() {
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private List<Column> getColumn() {
		return this._column;
	}
	private void add(Encrypt encrypt) {
		if(this.encrypts == null) {
			this.encrypts = new ArrayList<Encrypt>();
		}
		this.encrypts.add(encrypt);
	}
	private void add(Column column) {
		if(this._column == null) {
			this._column = new ArrayList<Column>();
		}
		this._column.add(column);
	}
	private void add(Where where) {
		if(this.where == null) {
			this.where = new ArrayList<Where>();
		}
		this.where.add(where);
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
	protected static String nodeName() {
		return Table.nodeName;
	}
	protected static Table load(Element element) {
		Table table = new Table();
		if(element != null) {
			table.loadAttr(element);
			table.loadElement(element);
			return table;
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
		if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
			this.add(Column.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "where")) {
			this.add(Where.load((Element)node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
			this.add(Encrypt.load(node));
		} else if(STR.compareIgnoreCase(node.getNodeName(), "order")) {
			this.setOrder(node);
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
						if(STR.compareIgnoreCase(node.getNodeName(), "column")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "where")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.load(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypts")) {
							this.loads(node);
						} else if(STR.compareIgnoreCase(node.getNodeName(), "order")) {
							this.load(node);
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
						if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "tableName")) {
							this.setTableName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "encrypt")) {
							this.setEncrypt(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "multi")) {
							this.setMulti(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "append")) {
							this.setAppend(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "total")) {
							this.setTotal(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "query_to_param")) {
							this.setQueryToParam(node.getNodeValue());
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
		element.setAttribute("name", this.getName());
		element.setAttribute("tableName", this.getTableName(null));
		element.setAttribute("label", this.getLabel());
		element.setAttribute("encrypt", this.getEncrypt());
		element.setAttribute("multi", this.getMulti());
		element.setAttribute("append", this.getAppend());
		element.setAttribute("total", this.getTotal());
		element.setAttribute("query_to_param", this.getQueryToParam());
		element.setAttribute("cond", this.getCond());
		if(this.encrypts != null && this.encrypts.size() > 0) {
			for(int i = 0; i < this.encrypts.size(); i++) {
				element.appendChild(((Encrypt)this.encrypts.get(i)).element());
			}
		}
		if(this.getColumn() != null && this.getColumn().size() > 0) {
			for(int i = 0; i < this.getColumn().size(); i++) {
				element.appendChild(((Column)this.getColumn().get(i)).element());
			}
		}
		if(this.where != null && this.where.size() > 0) {
			for(int i = 0; i < this.where.size(); i++) {
				element.appendChild(((Where)this.where.get(i)).element());
			}
		}
		element.appendChild(this.getOrder());
		return element;
	}
	protected int GET(
		GDocument document,
		Record param,
		List<Table> tables,
		List<Tab> tabs,
		ConnectionFactory connectionFactory,
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		if(!this.valid(param)) {
			return 0;
		}
		boolean exists = false;
		super.setConnectionFactory(connectionFactory);
		if(this.getColumn() != null && this.getColumn().size() > 0) {
			for(int i = 0; i < this.getColumn().size(); i++) {
				if(
					((Column)this.getColumn().get(i)).valid(param) &&
					(
						STR.trueValue(((Column)this.getColumn().get(i)).getPrimary()) ||
						STR.trueValue(((Column)this.getColumn().get(i)).getForeign())
					)
				) {
					if(param.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, ((Column)this.getColumn().get(i)).getValue()))) {
						exists = true;
					}
				}
			}
			GRows rows = null;
			if(exists) {
				List<SQLParameter> parameters = new ArrayList();
				Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
				Buffer sqlForSelect = new Buffer();
				sqlForSelect.appendL("select");
				int index = 0;
				for(int i = 0; i < this.getColumn().size(); i++) {
					Column c = this.getColumn().get(i);
					if(
						c.valid(param) &&
						(
							STR.startsWithIgnoreCase(c.getValue(), "param.") ||
							STR.trueValue(c.getSelect())
						)
					) {
/*
						LOG.debug(
							Boolean.toString(c.valid(param)),
							c.getName()
						);
*/
						if(index > 0) {
							sqlForSelect.append(", ");
						}
						sqlForSelect.appendL(c.getName());
						index++;
					}
				}
				sqlForSelect.appendL("from " + this.getTableName(param));
				boolean findForeignColumn = this.existsForeignColumn(param);
				sqlForSelect.append(this.sqlForWhere(param, encryptor, null, -1, parameters, findForeignColumn, Query.QUERY_FUNC_TYPE_INSERT));
				if(this.getOrder() != null) {
					sqlForSelect.append("order by ");
					sqlForSelect.appendL(super.parseSQL(this.getOrder(), param));
				} else {
					index = 0;
					for(int i = 0; i < this.getColumn().size(); i++) {
						Column c = this.getColumn().get(i);
						if(c.valid(param) && STR.trueValue(c.getPrimary())) {
							if(index == 0) {
								sqlForSelect.append("order by ");
							} else {
								sqlForSelect.append(1, ", ");
							}
							sqlForSelect.appendL(c.getName());
							index++;
						}
					}
				}
				Tab tab = Table.getTab(tabs, this.getName(), param);
				boolean columnAuto = false;
				if(tab != null) {
					if(STR.compareIgnoreCase(tab.getColumn(), "auto")) {
						columnAuto = true;
					}
				}
				java.util.Map<String, String> encrypted = null;
				if(encryptor != null) {
					encrypted = this.getEncrypted(param);
				}
				java.util.Map<String, String> pattern = this.getPattern(param);
				rows = super.executeQuery(
					new SQLInfo(sqlForSelect, parameters),
					this.getName(),
					encryptor,
					encrypted,
					pattern,
					STR.trueValue(this.getMulti()),
					columnAuto
				);
				if(STR.valid(this.getName())) {
					param.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName(), "count"), rows.size());
				}
			}
			if(rows == null) {
				rows = new GRows(this.getName());
			}
			if(STR.valid(this.getTotal()) && Integer.parseInt(this.getTotal()) > 0) {
				rows.setTotal(Integer.parseInt(this.getTotal()));
			} else if(STR.valid(this.getAppend()) && Integer.parseInt(this.getAppend()) > 0) {
				rows.setAppend(Integer.parseInt(this.getAppend()));
			}
			document.add(rows);
			if(exists) {
				return rows.size();
			} else {
				return -1;
			}
		}
		return 0;
	}
	private Map<String, String> getEncrypted(Record param) {
		if(STR.valid(this.getColumn())) {
			Map<String, String> map = new java.util.Hashtable<String, String>();
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(c.valid(param) && STR.valid(c.getEncrypt())) {
					map.put(c.getName(), c.getEncrypt());
				}
			}
			return map;
		}
		return null;
	}
	private Map<String, String> getPattern(Record param) {
		if(STR.valid(this.getColumn())) {
			Map<String, String> map = new java.util.Hashtable<String, String>();
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(c.valid(param) && STR.valid(c.getPattern())) {
					map.put(c.getName(), c.getPattern());
				}
			}
			return map;
		}
		return null;
	}
	protected int POST(
		GDocument document,
		Record params,
		List<Table> tables,
		List<Tab> tabs,
		ConnectionFactory connectionFactory,
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		if(!this.valid(params)) {
			return 0;
		}
		super.setConnectionFactory(connectionFactory);
		GRows grows = new GRows(this.getName());
		Map<String, Encryptor> encryptor = super.getEncryptor(this.encrypt, this.encrypts);
		int updateCount = 0;
		if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
			if(STR.trueValue(this.getMulti())) {
				updateCount += multi(params, encryptor, grows, tables, tabs);
			} else {
				updateCount += single(params, encryptor, grows, tables);
			}
		} else if(queryFuncType == Query.QUERY_FUNC_TYPE_DELETE) {
			updateCount += delete(params, encryptor, grows, -1, tables, queryFuncType);
		}
		if(updateCount > 0) {
			document.add(grows);
		}
		return updateCount;
	}
	private void contains(
		List<Col> cols, 
		Record params, 
		int idx, 
		ContainsResult cr
	) {
		if(STR.valid(cols)) {
			for(int x = 0; x < cols.size(); x++) {
				Col col = cols.get(x);
				if(STR.vexistsIgnoreCase(col.getType(), "hidden", "select", "radio")) {
				} else {
					if(params.hasKey(Record.key(Record.PREFIX_TYPE_PARAM, col.getName() + "." + idx))) {
						cr.has = true;
					}
					if(params.containsKey(Record.key(Record.PREFIX_TYPE_PARAM, col.getName() + "." + idx))) {
						cr.contains = true;
					}
				}
			}
		}
	}
	protected static Tab getTab(List<Tab> tabs, String tableOrCommandName, Record params) {
		Tab tab = null;
		if(STR.valid(tabs)) {
			for(int i = 0; i < tabs.size(); i++) {
				tab = tabs.get(i);
				if(tab.valid(params) && STR.compareIgnoreCase(tableOrCommandName, tab.getName())) {
					break;
				} else {
					tab = null;
				}
			}
		}
		return tab;
	}
/**
 * 여러 개의 row 를 처리한다.
 * 입력된 데이타에 따라 insert, update, delete 한다.
 * Primary Key 컬럼에 해당하는 값이 1개라도 없는 경우 : insert
 * 모든 컬럼의 값이 없는 경우 : delete (사용자가 화면에서 입력된 값을 모두 지운 경우)
 *
 * 컬럼의 값이 있는지 여부를 판단하는 방법은 다음과 같다.
 * 
 * layout 아래의 tab 요소 중 name 속성 값이 같은 것의 col 을 가져와서 처리하고,
 * (hidden, select, radio 와 같이 사용자가 화면에서 값을 지울수 없는 것들은 제외한다) 
 * 그것이 없는 부득이한 경우에만 컬럼 정보로 처리한다.
 */
	private int multi(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRows grows, 
		List<Table> tables, 
		List<Tab> tabs
	) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.getColumn())) {
			int sqlType = Table.SQL_TYPE_UPDATE;
			boolean next = false;
			Tab tab = Table.getTab(tabs, this.getName(), params);
			int idx = 1;
			int updateCount = 0;
			while(true) {
				ContainsResult cr = new ContainsResult();
				cr.has = false;
				cr.contains = false;
				cr.hasPk = true;
				if(tab != null) {
					List<Col> cols = tab.getCol();
					this.contains(cols, params, idx, cr);
					List<Row> rows = tab.getRow();
					if(STR.valid(rows)) {
						for(int i = 0; i < rows.size(); i++) {
							Row row = (Row)rows.get(i);
							if(row != null) {
								cols = row.getCol();
								this.contains(cols, params, idx, cr);
							}
						}
					}
				} else {
					for(int i = 0; i < this.getColumn().size(); i++) {
						Column c = this.getColumn().get(i);
						if(!c.valid(params)) {
							continue;
						}
						if(
							STR.trueValue(c.getPrimary()) ||
							STR.trueValue(c.getForeign()) ||
							!STR.valid(c.getValue()) ||
							!STR.startsWithIgnoreCase(c.getValue(), "param.")
						) {
						} else if(params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, c.getValue() + "." + idx))) {
							cr.has = true;
						} else if(params.containsKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, c.getValue() + "." + idx))) {
							cr.contains = true;
						}
					}
				}
				if(!cr.contains) {
					break;
				}
				for(int i = 0; i < this.getColumn().size(); i++) {
					Column c = this.getColumn().get(i);
					if(STR.trueValue(c.getPrimary()) && c.valid(params)) {
						if(!params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, c.getValue() + "." + idx))) {
							cr.hasPk = false;
						}
					}
				}
				if(!cr.hasPk && !cr.has) {
					idx++;
					continue;
				}
				if(cr.hasPk) {
					if(cr.has) {
						updateCount += update(params, encryptor, grows, idx, tables);
					} else {
						updateCount += delete(params, encryptor, grows, idx, tables, Query.QUERY_FUNC_TYPE_INSERT);
					}
				} else {
					updateCount += insert(params, encryptor, grows, idx, tables);
				}
				idx++;
			}
			return updateCount;
		}
		return 0;
	}
	private int single(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRows grows, 
		List<Table> tables
	) throws NoSuchProviderException, SQLException {
		if(STR.valid(this.getColumn())) {
			int sqlType = Table.SQL_TYPE_UPDATE;
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(
					(
						STR.trueValue(c.getPrimary()) &&
						!params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN,c.getValue()))
					)  && c.valid(params)
				) {
					sqlType = Table.SQL_TYPE_INSERT;
				}
			}
			if(sqlType == Table.SQL_TYPE_INSERT) {
				return insert(params, encryptor, grows, -1, tables);
			} else {
				return update(params, encryptor, grows, -1, tables);
			}
		}
		return 0;
	}
	private int insert(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRows grows, 
		int idx, 
		List<Table> tables
	) throws NoSuchProviderException, SQLException {
		int updateCount = 0;
		GRow grow = new GRow();
		SQLInfo info = sqlForInsert(params, encryptor, grow, idx, tables);
		Column column = this.getPrimaryColumnForInsert(params);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = super.prepareStatement(info.getSql());
			super.bind(pstmt, info.getParameters());
			pstmt.executeUpdate();
			updateCount = pstmt.getUpdateCount();
			if(column != null) {
				rs = pstmt.getGeneratedKeys();
				if(rs.next()) {
					params.put(Record.key(Record.PREFIX_TYPE_GENERATE, column.getValue()), rs.getInt(1));
				}
				DB.close(rs);
				rs = null;
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
		grow.add(new GColumn("rowcount", java.sql.Types.INTEGER, "int"), updateCount);
		grows.add(grow);
		if(tables != null && tables.size() > 1 && STR.valid(this.getName())) {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName() + ".count"), updateCount);
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "count"), updateCount);
		}
		return updateCount;
	}
	private int delete(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRows grows, 
		int idx, 
		List<Table> tables, 
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		GRow grow = new GRow();
		SQLInfo info = sqlForDelete(params, encryptor, grow, idx, tables, queryFuncType);
		int updateCount = super.executeUpdate(info);
		grow.add(new GColumn("rowcount", java.sql.Types.INTEGER, "int"), updateCount);
		grows.add(grow);
		if(tables != null && tables.size() > 1 && STR.valid(this.getName())) {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName() + ".count"), updateCount);
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "count"), updateCount);
		}
		return updateCount;
	}
	private int update(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRows grows, 
		int idx, 
		List<Table> tables
	) throws NoSuchProviderException, SQLException {
		GRow grow = new GRow();
		SQLInfo info = sqlForUpdate(params, encryptor, grow, idx, tables);
		int updateCount = super.executeUpdate(info);
		grow.add(new GColumn("rowcount", java.sql.Types.INTEGER, "int"), updateCount);
		grows.add(grow);
		if(tables != null && tables.size() > 1 && STR.valid(this.getName())) {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, this.getName() + ".count"), updateCount);
		} else {
			params.put(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "count"), updateCount);
		}
		return updateCount;
	}
	private void appendParam(
		Record params, 
		SQLParameter parameter, 
		Column column, 
		int idx
	) {
		if(STR.valid(this.getName()) && column != null && STR.valid(column.getName())) {
			String key = this.getName() + "." + column.getName();
			if(idx >= 0) {
				key = this.getName() + "." + column.getName() + "." + idx;
			}
			if(parameter.getDataType() == Param.DATA_TYPE_TIMESTAMP && parameter.getValue() instanceof java.sql.Timestamp) {
				params.put(Record.key(Record.PREFIX_TYPE_QUERY, key), STR.formatDate((java.sql.Timestamp)parameter.getValue(), column.getPattern()));
			} else if(parameter.getDataType() == Param.DATA_TYPE_DATE && parameter.getValue() instanceof java.sql.Date) {
				params.put(Record.key(Record.PREFIX_TYPE_QUERY, key), STR.formatDate((java.sql.Date)parameter.getValue(), column.getPattern()));
			} else if(parameter.getValue() instanceof java.util.Date) {
				params.put(Record.key(Record.PREFIX_TYPE_QUERY, key), STR.formatDate((java.util.Date)parameter.getValue(), column.getPattern()));
			} else {
				params.put(Record.key(Record.PREFIX_TYPE_QUERY, key), parameter.getValue());
			}
		}
	}
	private void appendRow(
		GRow grow, 
		SQLParameter parameter, 
		Column column
	) {
		if(column != null && STR.valid(column.getName())) {
			if(parameter.getValue() != null) {
				grow.add(new GColumn(column.getName(), parameter.getSqlType(), parameter.getSqlTypeName()), parameter.getValue());
			}
		}
	}
	private SQLInfo sqlForInsert(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRow grow, 
		int idx, 
		List<Table> tables
	) throws NoSuchProviderException, SQLException {
		Buffer sqlForInsert = new Buffer();
		sqlForInsert.append("insert into " + this.getTableName(params));
		List<SQLParameter> parameters = new ArrayList();
		if(STR.valid(this.getColumn())) {
			int index = 0;
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(STR.trueValue(c.getPrimary()) && STR.compareIgnoreCase(c.getInsert(), "generate")) {
					continue;
				}
				if(!c.valid(params)) {
					continue;
				}
				if(index > 0) {
					sqlForInsert.append(1, ", ");
				} else {
					sqlForInsert.append(" (");
				}
				sqlForInsert.appendL(c.getName());;
				index++;
			}
			index = 0;
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(STR.trueValue(c.getPrimary()) && STR.compareIgnoreCase(c.getInsert(), "generate")) {
					continue;
				}
				if(!c.valid(params)) {
					continue;
				}
				if(index > 0) {
					sqlForInsert.append(1, ", ");
				} else {
					sqlForInsert.appendL(") values (");
				}
				String key = c.getValue();
				if(idx >= 0) {
					key += "." + idx;
				}
				if(STR.startsWithIgnoreCase(c.getValue(), "sql.")) {
					sqlForInsert.appendL(c.getValue().substring(4));
				} else if(
					STR.startsWithIgnoreCase(c.getDefaultValue(), "sql.") &&
					!params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, key))
				) {
					sqlForInsert.appendL(c.getDefaultValue().substring(4));
				} else {
					SQLParameter parameter =  c.getValue(
						params,
						idx,
						encryptor,
						tables,
						super.getConnectionFactory(),
						Table.SQL_TYPE_INSERT
					);
					if(parameter != null) {
						sqlForInsert.appendL("?");
						this.appendParam(params, parameter, c, idx);
						this.appendRow(grow, parameter, c);
						parameters.add(parameter);
					}
				}
				index++;
			}
			sqlForInsert.appendL(")");
		}
		return new SQLInfo(sqlForInsert, parameters);
	}
	private SQLInfo sqlForUpdate(
		Record params, 
		Map<String, Encryptor> encryptor, 
		GRow grow, 
		int idx, 
		List<Table> tables
	) throws NoSuchProviderException, SQLException {
		Buffer sqlForUpdate = new Buffer();
		sqlForUpdate.appendL("update " + this.getTableName(params));
		List<SQLParameter> parameters = new ArrayList();
		if(STR.valid(this.getColumn())) {
			int index = 0;
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(STR.trueValue(c.getPrimary()) || STR.compareIgnoreCase(c.getOnly(), "insert")) {
					continue;
				}
				if(!c.valid(params)) {
					continue;
				}
				if(index > 0) {
					sqlForUpdate.appendL(1, ", ");
				} else {
					sqlForUpdate.append("set ");
				}
				String key = c.getValue();
				if(idx >= 0) {
					key += "." + idx;
				}
				if(STR.startsWithIgnoreCase(c.getValue(), "sql.")) {
					sqlForUpdate.appendL(c.getName() + " = " + c.getValue().substring(4));
				} else if(
					STR.startsWithIgnoreCase(c.getDefaultValue(), "sql.") &&
					!params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, key))
				) {
					sqlForUpdate.appendL(c.getName() + " = " + c.getDefaultValue().substring(4));
				} else {
					SQLParameter parameter =  c.getValue(
						params,
						idx,
						encryptor,
						tables,
						super.getConnectionFactory(),
						Table.SQL_TYPE_UPDATE
					);
					if(parameter != null) {
						sqlForUpdate.appendL(c.getName() + " = ?");
						this.appendParam(params, parameter, c, idx);
						this.appendRow(grow, parameter, c);
						parameters.add(parameter);
					}
				}
				index++;
			}
			sqlForUpdate.append(this.sqlForWhere(params, encryptor, grow, idx, parameters, false, Query.QUERY_FUNC_TYPE_INSERT));
		}
		return new SQLInfo(sqlForUpdate, parameters);
	}
/**
 * delete 구문을 위한 sql 절을 조립한다.
 * - insert 의 POST :
 * - delete :
 */
	private SQLInfo sqlForDelete(
		Record params, 
		Map<String, 
		Encryptor> encryptor, 
		GRow grow, 
		int idx, 
		List<Table> tables, 
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		Buffer sqlForDelete = new Buffer();
		sqlForDelete.appendL("delete from " + this.getTableName(params));
		List<SQLParameter> parameters = new ArrayList();
		sqlForDelete.append(this.sqlForWhere(params, encryptor, grow, idx, parameters, false, queryFuncType));
		return new SQLInfo(sqlForDelete, parameters);
	}
/**
 * where 절을 조립한다.
 * - insert 의 GET : Primary Key 컬럼과 Forgien Key 컬럼 2개만 Where 절에 공급된다.
 * - insert 의 (update 혹은 delete 구문에서만) POST : Primary Key 컬럼만 Where 절에 공급된다.
 * - delete : 정의된 모든 Column 이 where 절에 공급된다.  값이 1개라도 없다면 ParsingException 을 발생시킨다.
 */
	private Buffer sqlForWhere(
		Record params,
		Map<String, Encryptor> encryptor,
		GRow grow,
		int idx,
		List<SQLParameter> parameters,
		boolean findForeignColumn,
		int queryFuncType
	) throws NoSuchProviderException, SQLException {
		Buffer sqlForWhere = new Buffer();
		if(STR.valid(this.getColumn())) {
			int index = 0;
			for(int i = 0; i < this.getColumn().size(); i++) {
				Column c = this.getColumn().get(i);
				if(!c.valid(params)) {
					continue;
				}
				if(
					queryFuncType == Query.QUERY_FUNC_TYPE_DELETE ||
					(
						findForeignColumn &&
						STR.trueValue(c.getForeign())
					) ||
					(
						!findForeignColumn &&
						STR.trueValue(c.getPrimary())
					)
				) {
					if(
						queryFuncType == Query.QUERY_FUNC_TYPE_DELETE && 
						!params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, c.getValue()))
					) {
						throw new ParsingException();
					}
					if(index > 0) {
						sqlForWhere.append(1, "and ");
					} else {
						sqlForWhere.append("where ");
					}
					SQLParameter parameter =  c.getValue(
						params,
						idx,
						encryptor,
						null,
						super.getConnectionFactory(),
						Table.SQL_TYPE_WHERE
					);
					if(parameter != null) {
						sqlForWhere.appendL(c.getName() + " = ?");
						if(grow != null) {
							this.appendParam(params, parameter, c, idx);
							this.appendRow(grow, parameter, c);
						}
						parameters.add(parameter);
					}
					index++;
				}
			}
		}
		if(STR.valid(this.where)) {
			for(int i = 0; i < this.where.size(); i++) {
				Where w = this.where.get(i);
				if(
					!STR.valid(w.getMethod()) ||
					STR.compareIgnoreCase(w.getMethod(), params.getString(Record.key(Record.PREFIX_TYPE_HEADER, "method")))
				) {
					sqlForWhere.append(1, "and ");
					sqlForWhere.appendL(super.parseSQL(w.getSql(), params));
					List<Param> param = w.getParam();
					if(STR.valid(param)) {
						for(int x = 0; x < param.size(); x++) {
							Param p = (Param)param.get(x);
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
			}
		}
		return sqlForWhere;
	}
}
class ContainsResult {
	protected Boolean contains = false; 
	protected Boolean has = false;
	protected Boolean hasPk = true;
}