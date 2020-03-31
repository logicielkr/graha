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
 * Graha(그라하) 데이타바인딩 문자열(char, varchar) 구현 클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class DataBinderStringTypeImpl extends DataBinderImpl {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderStringTypeImpl() {
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
			if(encryptor != null && encrypt != null && !encrypt.trim().equals("") && encryptor.containsKey(encrypt)) {
				setString(stmt, index, encryptor.get(encrypt).encrypt(params.getString(value[0])));
			} else {
				setString(stmt, index, params.getString(value[0]));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, params.getString(value[0]));
				} else {
					params.put("query." + table + "." + column, params.getString(value[0]));
				}
			}
			if(column != null && sb != null) {
				sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + (params.getString(value[0])).replace("]]>", "]]]]><![CDATA[>") + "]]></" + tag.tag("row", column, null, false) + ">");
			}
		} else if(idx >= 0 && params.hasKey(value[0] + "." + idx)) {
			if(encryptor != null && encrypt != null && !encrypt.trim().equals("") && encryptor.containsKey(encrypt)) {
				setString(stmt, index, encryptor.get(encrypt).encrypt(params.getString(value[0] + "." + idx)));
			} else {
				setString(stmt, index, params.getString(value[0] + "." + idx));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, params.getString(value[0] + "." + idx));
				} else {
					params.put("query." + table + "." + column, params.getString(value[0] + "." + idx));
				}
			}
			if(column != null && sb != null) {
				if(params.getString(value[0]) == null) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + (params.getString(value[0] + "." + idx)) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + (params.getString(value[0]).replace("]]>", "]]]]><![CDATA[>") + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else if(defaultValue != null && !params.compare(defaultValue, "null")) {
			
			String dValue = defaultValue;

			if(dValue != null && dValue.startsWith("system.today.yyyy")) {
				if(!params.hasKey("system.today.yyyy")) {
					int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
					params.put("system.today.yyyy", Integer.toString(year));
				}
				dValue = params.getString("system.today.yyyy");
			} else if(dValue != null && dValue.startsWith("system.today.mm")) {
				if(!params.hasKey("system.today.mm")) {
					int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
					month++;
					if(month >= 10) {
						params.put("system.today.mm", Integer.toString(month));
					} else {
						params.put("system.today.mm", "0" + Integer.toString(month));
					}
				}
				dValue = params.getString("system.today.mm");
			} else if(dValue != null && dValue.startsWith("system.today.dd")) {
				if(!params.hasKey("system.today.dd")) {
					int day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
					if(day >= 10) {
						params.put("system.today.dd", Integer.toString(day));
					} else {
						params.put("system.today.dd", "0" + Integer.toString(day));
					}
				}
				dValue = params.getString("system.today.dd");
			}
			if(defaultValue != null && !defaultValue.equals("%")) {
				params.put(value[0], dValue);
			}
			if(encryptor != null && encrypt != null && !encrypt.trim().equals("") && encryptor.containsKey(encrypt)) {
				setString(stmt, index, encryptor.get(encrypt).encrypt(dValue));
			} else {
				setString(stmt, index, dValue);
			}
			
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
		} else if(params.compare(datatype, "char")) {
			setNull(stmt, index, java.sql.Types.CHAR);
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, null);
				} else {
					params.put("query." + table + "." + column, null);
				}
			}
		} else if(params.compare(datatype, "varchar")) {
			setNull(stmt, index, java.sql.Types.VARCHAR);
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