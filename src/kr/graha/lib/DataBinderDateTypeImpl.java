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
import java.util.logging.Logger;

/**
 * Graha(그라하) 데이타바인딩 날짜형(Date 및 Timestamp) 구현 클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DataBinderDateTypeImpl extends DataBinderImpl {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderDateTypeImpl() {
		LogHelper.setLogLevel(logger);
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
		XMLTag tag
	) throws SQLException, java.security.NoSuchProviderException {
		if(params.hasKey(value[0])) {
			if(params.compare(datatype, "date")) {
				setDate(stmt, index, params.getDate(value[0], pattern));
			} else {
				setTimestamp(stmt, index, params.getTimestamp(value[0], pattern));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "date")) {
						params.put("query." + table + "." + column + "." + idx, params.getDate(value[0], pattern));
					} else {
						params.put("query." + table + "." + column + "." + idx, params.getTimestamp(value[0], pattern));
					}
				} else {
					if(params.compare(datatype, "date")) {
						params.put("query." + table + "." + column, params.getDate(value[0], pattern));
					} else {
						params.put("query." + table + "." + column, params.getTimestamp(value[0], pattern));
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "date")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(value[0], pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else if(idx >= 0 && params.hasKey(value[0] + "." + idx)) {
			if(params.compare(datatype, "date")) {
				setDate(stmt, index, params.getDate(value[0] + "." + idx, pattern));
			} else {
				setTimestamp(stmt, index, params.getTimestamp(value[0] + "." + idx, pattern));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "date")) {
						params.put("query." + table + "." + column + "." + idx, params.getDate(value[0] + "." + idx, pattern));
					} else {
						params.put("query." + table + "." + column + "." + idx, params.getTimestamp(value[0] + "." + idx, pattern));
					}
				} else {
					if(params.compare(datatype, "date")) {
						params.put("query." + table + "." + column, params.getDate(value[0] + "." + idx, pattern));
					} else {
						params.put("query." + table + "." + column, params.getTimestamp(value[0] + "." + idx, pattern));
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "date")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(value[0] + "." + idx, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(value[0] + "." + idx, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else if(defaultValue != null && !params.compare(defaultValue, "null")) {
			
			String dValue = defaultValue;
			params.put(dValue, defaultValue);
			if(params.compare(datatype, "date")) {
				if(params.compare(defaultValue, "today")) {
					setDate(stmt, index, new java.sql.Date(new java.util.Date().getTime()));
				} else {
					setDate(stmt, index, params.getDate(dValue, pattern));
				}
			} else {
				if(params.compare(defaultValue, "today")) {
					setTimestamp(stmt, index, new java.sql.Timestamp(new java.util.Date().getTime()));
				} else {
					setTimestamp(stmt, index, params.getTimestamp(dValue, pattern));
				}
				
			}
							
			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "date")) {
						if(params.compare(defaultValue, "today")) {
							params.put("query." + table + "." + column + "." + idx, new java.sql.Date(new java.util.Date().getTime()));
						} else {
							params.put("query." + table + "." + column + "." + idx, params.getDate(dValue, pattern));
						}
					} else {
						if(params.compare(defaultValue, "today")) {
							params.put("query." + table + "." + column + "." + idx, new java.sql.Timestamp(new java.util.Date().getTime()));
						} else {
							params.put("query." + table + "." + column + "." + idx, params.getTimestamp(dValue, pattern));
						}
					}
				} else {
					if(params.compare(datatype, "date")) {
						if(params.compare(defaultValue, "today")) {
							params.put("query." + table + "." + column, new java.sql.Date(new java.util.Date().getTime()));
						} else {
							params.put("query." + table + "." + column, params.getDate(dValue, pattern));
						}
					} else {
						if(params.compare(defaultValue, "today")) {
							params.put("query." + table + "." + column, new java.sql.Timestamp(new java.util.Date().getTime()));
						} else {
							params.put("query." + table + "." + column, params.getTimestamp(dValue, pattern));
						}
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "date")) {
					if(params.compare(defaultValue, "today")) {
						sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + new java.sql.Date(new java.util.Date().getTime()) + "]]></" + tag.tag("row", column, null, false) + ">");
					} else {
						sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDate(dValue, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
					}
				} else {
					if(params.compare(defaultValue, "today")) {
						sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + new java.sql.Timestamp(new java.util.Date().getTime()) + "]]></" + tag.tag("row", column, null, false) + ">");
					} else {
						sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getTimestamp(dValue, pattern) + "]]></" + tag.tag("row", column, null, false) + ">");
					}
					
				}
			}
		} else {
			if(params.compare(datatype, "date")) {
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
}