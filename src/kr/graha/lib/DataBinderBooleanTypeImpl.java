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

/**
 * Graha(그라하) 데이타바인딩 Boolean Type 구현 클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DataBinderBooleanTypeImpl extends DataBinderImpl {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderBooleanTypeImpl() {
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
			setBoolean(stmt, index, params.getBoolean(value[0]));
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, params.getBoolean(value[0]));
				} else {
					params.put("query." + table + "." + column, params.getBoolean(value[0]));
				}
			}
			if(column != null && sb != null) {
				sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getBoolean(value[0]) + "]]></" + tag.tag("row", column, null, false) + ">");
			}
		} else if(idx >= 0 && params.hasKey(value[0] + "." + idx)) {
			setBoolean(stmt, index, params.getBoolean(value[0] + "." + idx));
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, params.getBoolean(value[0] + "." + idx));
				} else {
					params.put("query." + table + "." + column, params.getBoolean(value[0] + "." + idx));
				}
			}
			if(column != null && sb != null) {
				sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getBoolean(value[0] + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
			}
		} else if(defaultValue != null && !params.compare(defaultValue, "") && !params.compare(defaultValue, "null")) {
			
			String dValue = defaultValue;
			if(dValue != null && (dValue.startsWith("prop.") || dValue.startsWith("param.") || dValue.startsWith("code."))) {
				dValue = params.getString(dValue);
			}

			if(defaultValue != null && !defaultValue.equals("%")) {
				params.put(value[0], dValue);
			}
			setBoolean(stmt, index, params.getBoolean(value[0]));
			
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, dValue);
				} else {
					params.put("query." + table + "." + column, dValue);
				}
			}
			if(column != null && sb != null) {
				sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + dValue + "]]></" + tag.tag("row", column, null, false) + ">");
			}
		} else {
			setNull(stmt, index, java.sql.Types.BOOLEAN);
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