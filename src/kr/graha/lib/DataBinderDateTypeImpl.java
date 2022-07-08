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


package kr.graha.lib;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.logging.Logger;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;

/**
 * Graha(그라하) 데이타바인딩 날짜형(Date 및 Timestamp) 구현 클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DataBinderDateTypeImpl extends DataBinderImpl {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderDateTypeImpl() {
		LOG.setLogLevel(logger);
	}
	
	public void bind(
		PreparedStatement stmt, 
		String datatype,
		int index, 
		String[] value, 
		int idx, 
		String defaultValue, 
		String pattern, 
		String table, 
		String column, 
		java.util.Map<String, Encryptor> encryptor, 
		String encrypt, 
		Buffer sb,
		Record params,
		XMLTag tag, 
		Record info,
		DatabaseMetaData dmd
	) throws SQLException, java.security.NoSuchProviderException {
		if(params.hasKey(value[0])) {
			if(STR.compareIgnoreCase(datatype, "date")) {
				setDate(stmt, index, params.getDate(value[0], pattern));
			} else {
				setTimestamp(stmt, index, params.getTimestamp(value[0], pattern));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(STR.compareIgnoreCase(datatype, "date")) {
						params.put("query." + table + "." + column + "." + idx, params.getDate(value[0], pattern));
					} else {
						params.put("query." + table + "." + column + "." + idx, params.getTimestamp(value[0], pattern));
					}
				} else {
					if(STR.compareIgnoreCase(datatype, "date")) {
						params.put("query." + table + "." + column, params.getDate(value[0], pattern));
					} else {
						params.put("query." + table + "." + column, params.getTimestamp(value[0], pattern));
					}
				}
			}
			if(column != null && sb != null) {
				if(STR.compareIgnoreCase(datatype, "date")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
			return;
		} else if(idx >= 0 && params.hasKey(value[0] + "." + idx)) {
			if(STR.compareIgnoreCase(datatype, "date")) {
				setDate(stmt, index, params.getDate(value[0] + "." + idx, pattern));
			} else {
				setTimestamp(stmt, index, params.getTimestamp(value[0] + "." + idx, pattern));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(STR.compareIgnoreCase(datatype, "date")) {
						params.put("query." + table + "." + column + "." + idx, params.getDate(value[0] + "." + idx, pattern));
					} else {
						params.put("query." + table + "." + column + "." + idx, params.getTimestamp(value[0] + "." + idx, pattern));
					}
				} else {
					if(STR.compareIgnoreCase(datatype, "date")) {
						params.put("query." + table + "." + column, params.getDate(value[0] + "." + idx, pattern));
					} else {
						params.put("query." + table + "." + column, params.getTimestamp(value[0] + "." + idx, pattern));
					}
				}
			}
			if(column != null && sb != null) {
				if(STR.compareIgnoreCase(datatype, "date")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(value[0] + "." + idx, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(value[0] + "." + idx, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
			return;
		} else if(STR.valid(defaultValue) && !STR.compareIgnoreCase(defaultValue, "null")) {
			String dValue = defaultValue;
			if(dValue != null && (dValue.startsWith("prop.") || dValue.startsWith("param.") || dValue.startsWith("code."))) {
				dValue = params.getString(dValue);
			}
			if(STR.valid(dValue) && !STR.compareIgnoreCase(dValue, "null")) {
				params.put(value[0], dValue);
				long today = new java.util.Date().getTime();
				if(STR.compareIgnoreCase(datatype, "date")) {
					if(STR.compareIgnoreCase(defaultValue, "system.today")) {
						setDate(stmt, index, new java.sql.Date(today));
						params.put(value[0], STR.formatDate(new java.sql.Date(today), pattern));
					} else {
						setDate(stmt, index, params.getDate(value[0], pattern));
					}
				} else {
					if(STR.compareIgnoreCase(defaultValue, "system.today")) {
						setTimestamp(stmt, index, new java.sql.Timestamp(today));
						params.put(value[0], STR.formatDate(new java.sql.Timestamp(today), pattern));
					} else {
						setTimestamp(stmt, index, params.getTimestamp(value[0], pattern));
					}
				}
								
				if(table != null && column != null) {
					if(idx >= 0) {
						if(STR.compareIgnoreCase(datatype, "date")) {
							if(STR.compareIgnoreCase(defaultValue, "system.today")) {
								params.put("query." + table + "." + column + "." + idx, new java.sql.Date(today));
							} else {
								params.put("query." + table + "." + column + "." + idx, params.getDate(value[0], pattern));
							}
						} else {
							if(STR.compareIgnoreCase(defaultValue, "system.today")) {
								params.put("query." + table + "." + column + "." + idx, new java.sql.Timestamp(today));
							} else {
								params.put("query." + table + "." + column + "." + idx, params.getTimestamp(value[0], pattern));
							}
						}
					} else {
						if(STR.compareIgnoreCase(datatype, "date")) {
							if(STR.compareIgnoreCase(defaultValue, "system.today")) {
								params.put("query." + table + "." + column, new java.sql.Date(new java.util.Date().getTime()));
							} else {
								params.put("query." + table + "." + column, params.getDate(value[0], pattern));
							}
						} else {
							if(STR.compareIgnoreCase(defaultValue, "system.today")) {
								params.put("query." + table + "." + column, new java.sql.Timestamp(new java.util.Date().getTime()));
							} else {
								params.put("query." + table + "." + column, params.getTimestamp(value[0], pattern));
							}
						}
					}
				}
				if(column != null && sb != null) {
					if(STR.compareIgnoreCase(datatype, "date")) {
						if(STR.compareIgnoreCase(defaultValue, "system.today")) {
							sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + new java.sql.Date(today) + "]]></" + tag.tag("row", column, null, false) + ">");
						} else {
							sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
						}
					} else {
						if(STR.compareIgnoreCase(defaultValue, "system.today")) {
							sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + new java.sql.Timestamp(today) + "]]></" + tag.tag("row", column, null, false) + ">");
						} else {
							sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
						}
						
					}
				}
				return;
			}
		}
		if(STR.compareIgnoreCase(datatype, "date")) {
			setNull(stmt, index, java.sql.Types.DATE);
		} else {
			setNull(stmt, index, java.sql.Types.TIMESTAMP);
		}
		if(table != null && column != null) {
			if(idx >= 0) {
				params.put("query." + table + "." + column + "." + idx, null);
			} else {
				params.put("query." + table + "." + column, null);
			}
		}
	}
}