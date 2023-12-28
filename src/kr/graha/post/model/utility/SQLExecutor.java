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


package kr.graha.post.model.utility;

import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import kr.graha.post.lib.Record;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.HashMap;
import kr.graha.post.model.Encrypt;
import kr.graha.post.interfaces.Encryptor;
import java.util.Map;
import java.sql.SQLException;
import kr.graha.post.model.Param;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.helper.DB;
import kr.graha.post.xml.GRows;

/**
 * Graha(그라하) SQLExecutor
 * SQL 을 실행하는 객체들은 모두 이 Class 를 상속받아서 사용한다.
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public abstract class SQLExecutor {
	private ConnectionFactory connectionFactory = null;
	protected void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	protected ConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}
	protected CallableStatement prepareCall(Buffer sql) throws SQLException {
		return this.prepareCall(sql);
	}
	protected CallableStatement prepareCall(String sql) throws SQLException {
		if(
			this.getConnectionFactory() != null &&
			this.getConnectionFactory().getConnection() != null
		) {
			return this.getConnectionFactory().getConnection().prepareCall(sql);
		}
		return null;
	}
	protected PreparedStatement prepareStatement(Buffer sql) throws SQLException {
		return this.prepareStatement(sql.toString());
	}
	protected PreparedStatement prepareStatement(String sql) throws SQLException {
		if(
			this.getConnectionFactory() != null &&
			this.getConnectionFactory().getConnection() != null
		) {
			return this.getConnectionFactory().getConnection().prepareStatement(sql);
		}
		return null;
	}
	protected Buffer parseSQL(Node node, Record params) {
		return this.parseSQL((Element)node, params);
	}
	protected Buffer parseSQL(Element node, Record params) {
		if(node == null) {
			return null;
		}
		Buffer sql = new Buffer();
		NodeList list = node.getChildNodes();
		for(int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Node n = (org.w3c.dom.Node)list.item(i);
			if(n.getNodeType() == org.w3c.dom.Node.TEXT_NODE || n.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE ) {
				sql.appendL(n.getNodeValue());
			} else if(n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					Element e = (Element)n;
					if(!e.hasAttribute("cond") || AuthUtility.auth(e.getAttribute("cond"), params)) {
						if(n.getNodeName() != null && n.getNodeName().equals("entity")) {
/*
이 기능은 신중하게 사용하세요.
사용자가 입력한 파라미터를 테이블이름 등으로 대체하는 것은
원칙적으로 허용되지 않는 방식입니다.
*/
							String result  = TextParser.parse(e.getFirstChild().getNodeValue(), params);
							if(STR.valid(result)) {
								sql.append(result);
							}
						} else if(n.getNodeName() != null && n.getNodeName().equals("tile")) {
							sql.appendL(e.getFirstChild().getNodeValue());
						}
					}
			} else {
				LOG.warning(
					"NodeName = " + n.getNodeName(),
					"NodeType = " + n.getNodeType(),
	
					"2	ATTRIBUTE_NODE",
					"4	CDATA_SECTION_NODE",
					"8	COMMENT_NODE",
					"11	DOCUMENT_FRAGMENT_NODE",
					"9	DOCUMENT_NODE",
					"16	DOCUMENT_POSITION_CONTAINED_BY",
					"8	DOCUMENT_POSITION_CONTAINS",
					"1	DOCUMENT_POSITION_DISCONNECTED",
					"4	DOCUMENT_POSITION_FOLLOWING",
					"32	DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC",
					"2	DOCUMENT_POSITION_PRECEDING",
					"10	DOCUMENT_TYPE_NODE",
					"1	ELEMENT_NODE",
					"6	ENTITY_NODE",
					"5	ENTITY_REFERENCE_NODE",
					"12	NOTATION_NODE",
					"7	PROCESSING_INSTRUCTION_NODE",
					"3	TEXT_NODE"
				);
			}
		}
		return sql;
	}
	protected java.util.Map<String, Encryptor> getEncryptor(String encrypt, List<Encrypt> encrypts) {
		Map<String, Encryptor> encryptor = null;
		if(STR.valid(encrypt)) {
			encryptor = new HashMap();
			try {
				encryptor.put("true", (Encryptor) Class.forName(encrypt).getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				LOG.severe(e);
				encryptor.clear();
				encryptor = null;
				return null;
			}
		}
		if(STR.valid(encrypts)) {
			if(encryptor == null) {
				encryptor = new HashMap();
			}
			for(int i = 0; i < encrypts.size(); i++) {
				Encrypt crypt = (Encrypt)encrypts.get(i);
				try {
					encryptor.put(crypt.getKey(), (Encryptor) Class.forName(crypt.getName()).getConstructor().newInstance());
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
					LOG.severe(e);
					encryptor.clear();
					encryptor = null;
					return null;
				}
			}
		}
		return encryptor;
	}
	protected void bind(PreparedStatement pstmt, List<SQLParameter> parameters) throws SQLException {
		if(STR.valid(parameters)) {
			for(int i = 0; i < parameters.size(); i++) {
				SQLParameter parameter = (SQLParameter)parameters.get(i);
				if(parameter.getValue() == null) {
					pstmt.setNull(i + 1, parameter.getSqlType());
					continue;
				}
				if(parameter.getDataType() == Param.DATA_TYPE_INT && parameter.getValue() instanceof Integer) {
					pstmt.setInt(i + 1, (Integer)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_LONG && parameter.getValue() instanceof Long) {
					pstmt.setLong(i + 1, (Long)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_FLOAT && parameter.getValue() instanceof Float) {
					pstmt.setFloat(i + 1, (Float)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_DOUBLE && parameter.getValue() instanceof Double) {
					pstmt.setDouble(i + 1, (Double)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_BOOLEAN && parameter.getValue() instanceof Boolean) {
					pstmt.setBoolean(i + 1, (Boolean)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_DATE && parameter.getValue() instanceof java.sql.Date) {
					pstmt.setDate(i + 1, (java.sql.Date)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_TIMESTAMP && parameter.getValue() instanceof java.sql.Timestamp) {
					pstmt.setTimestamp(i + 1, (java.sql.Timestamp)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_VARCHAR && parameter.getValue() instanceof String) {
					pstmt.setString(i + 1, (String)parameter.getValue());
				} else if(parameter.getDataType() == Param.DATA_TYPE_CHAR && parameter.getValue() instanceof String) {
					pstmt.setString(i + 1, (String)parameter.getValue());
				}
			}
		}
	}
	protected GRows executeQuery(
		SQLInfo info,
		String commandOrTableName,
		Map<String, Encryptor> encryptor,
		java.util.Map<String, String> encrypted,
		java.util.Map<String, String> pattern,
		boolean multi,
		boolean columnAuto
	) throws SQLException {
		GRows grows = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = this.prepareStatement(info.getSql());
			this.bind(pstmt, info.getParameters());
			rs = pstmt.executeQuery();
			grows = GRows.load(
				rs,
				commandOrTableName,
				encryptor,
				encrypted,
				pattern,
				this.getConnectionFactory(),
				multi,
				columnAuto
			);
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
		return grows;
	}
	protected int executeUpdate(SQLInfo info) throws SQLException {
		PreparedStatement pstmt = null;
		CallableStatement cstmt = null;
		int result = 0;
		try {
			if(info.getType() == SQLInfo.TYPE_PSTMT) {
				pstmt = this.prepareStatement(info.getSql());
				this.bind(pstmt, info.getParameters());
				pstmt.executeUpdate();
				result = pstmt.getUpdateCount();
				DB.close(pstmt);
				pstmt = null;
			} else if(info.getType() == SQLInfo.TYPE_CSTMT) {
				cstmt = this.prepareCall(info.getSql());
				this.bind(cstmt, info.getParameters());
				cstmt.executeUpdate();
				result = cstmt.getUpdateCount();
				DB.close(cstmt);
				cstmt = null;
			}
		} catch (SQLException e) {
			LOG.severe(e);
			throw e;
		} finally {
			DB.close(pstmt);
			pstmt = null;
			DB.close(cstmt);
			cstmt = null;
		}
		return result;
	}
	private static Object getNextSequenceValue(String sequenceName, ConnectionFactory connectionFactory, int dataType) throws SQLException {
		Object result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if(connectionFactory != null && connectionFactory.getConnection() != null) {
				if(STR.startsWithIgnoreCase(sequenceName, "sequence.")) {
					pstmt = connectionFactory.getConnection().prepareStatement(connectionFactory.sqlForSequence(sequenceName.substring(9)));
				} else {
					pstmt = connectionFactory.getConnection().prepareStatement(connectionFactory.sqlForSequence(sequenceName));
				}
				rs = pstmt.executeQuery();
				if(rs.next()) {
					if(dataType == Param.DATA_TYPE_INT) {
						result = rs.getInt(1);
					} else if(dataType == Param.DATA_TYPE_LONG) {
						result = rs.getLong(1);
					}
				}
				DB.close(rs);
				rs = null;
				DB.close(pstmt);
				pstmt = null;
			} else {
				LOG.severe("connectionFactory or connectionFactory.getConnection() is null");
			}
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
	public static Integer getNextSequenceIntegerValue(String sequenceName, ConnectionFactory connectionFactory) throws SQLException {
		Object result = getNextSequenceValue(sequenceName, connectionFactory, Param.DATA_TYPE_INT);
		if(result != null && result instanceof Integer) {
			return (Integer)result;
		}
		return null;
	}
	public static Long getNextSequenceLongValue(String sequenceName, ConnectionFactory connectionFactory) throws SQLException {
		Object result = getNextSequenceValue(sequenceName, connectionFactory, Param.DATA_TYPE_LONG);
		if(result != null && result instanceof Long) {
			return (Long)result;
		}
		return null;
	}
}
