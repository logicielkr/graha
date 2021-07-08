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
 * Graha(그라하) 데이타바인딩 숫자형(int, float, double, long) 구현 클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DataBinderNumberTypeImpl extends DataBinderImpl {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderNumberTypeImpl() {
		LOG.setLogLevel(logger);
	}
	public boolean isSequenceValue(String[] value) {
		if(value != null && value.length > 0 && value[0] != null && value[0].startsWith("sequence.")) {
			return true;
		} else {
			return false;
		}
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
			if(params.compare(datatype, "int")) {
				setInt(stmt, index, params.getInt(value[0]));
			} else if(params.compare(datatype, "float")) {
				setFloat(stmt, index, params.getFloat(value[0]));
			} else if(params.compare(datatype, "double")) {
				setDouble(stmt, index, params.getDouble(value[0]));
			} else if(params.compare(datatype, "long")) {
				setLong(stmt, index, params.getLong(value[0]));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column + "." + idx, params.getInt(value[0]));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column + "." + idx, params.getFloat(value[0]));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column + "." + idx, params.getDouble(value[0]));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column + "." + idx, params.getLong(value[0]));
					}
				} else {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column, params.getInt(value[0]));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column, params.getFloat(value[0]));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column, params.getDouble(value[0]));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column, params.getLong(value[0]));
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "int")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getInt(value[0]) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "float")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getFloat(value[0]) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "double")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDouble(value[0]) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "long")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getLong(value[0]) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else if(idx >= 0 && params.hasKey(value[0] + "." + idx)) {
			if(params.compare(datatype, "int")) {
				setInt(stmt, index, params.getInt(value[0] + "." + idx));
			} else if(params.compare(datatype, "float")) {
				setFloat(stmt, index, params.getFloat(value[0] + "." + idx));
			} else if(params.compare(datatype, "double")) {
				setDouble(stmt, index, params.getDouble(value[0] + "." + idx));
			} else if(params.compare(datatype, "long")) {
				setLong(stmt, index, params.getLong(value[0] + "." + idx));
			}
			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column + "." + idx, params.getInt(value[0] + "." + idx));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column + "." + idx, params.getFloat(value[0] + "." + idx));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column + "." + idx, params.getDouble(value[0] + "." + idx));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column + "." + idx, params.getLong(value[0] + "." + idx));
					}
				} else {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column, params.getInt(value[0] + "." + idx));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column, params.getFloat(value[0] + "." + idx));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column, params.getDouble(value[0] + "." + idx));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column, params.getLong(value[0] + "." + idx));
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "int")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getInt(value[0] + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "float")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getFloat(value[0] + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "double")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getDouble(value[0] + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "long")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + params.getLong(value[0] + "." + idx) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else if(params.compare(datatype, "int") && isSequenceValue(value)) {
			int sequence = DBHelper.getNextSequenceValue(stmt.getConnection(), value[0], info, dmd);
			setInt(stmt, index, sequence);
			if(table != null && column != null) {
				if(idx >= 0) {
					params.put("query." + table + "." + column + "." + idx, sequence);
				} else {
					params.put("query." + table + "." + column, sequence);
				}
			}
			if(column != null && sb != null) {
				sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + sequence + "]]></" + tag.tag("row", column, null, false) + ">");
			}
		} else if(defaultValue != null && !params.compare(defaultValue, "null")) {
			String dValue = defaultValue;
			params.put(value[0], dValue);
			if(params.compare(datatype, "int")) {
				setInt(stmt, index, Integer.parseInt(dValue));
			} else if(params.compare(datatype, "float")) {
				setFloat(stmt, index, Float.parseFloat(dValue));
			} else if(params.compare(datatype, "double")) {
				setDouble(stmt, index, Double.parseDouble(dValue));
			} else if(params.compare(datatype, "long")) {
				setLong(stmt, index, Long.parseLong(dValue));
			}

			if(table != null && column != null) {
				if(idx >= 0) {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column + "." + idx, Integer.parseInt(dValue));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column + "." + idx, Float.parseFloat(dValue));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column + "." + idx, Double.parseDouble(dValue));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column + "." + idx, Long.parseLong(dValue));
					}
				} else {
					if(params.compare(datatype, "int")) {
						params.put("query." + table + "." + column, Integer.parseInt(dValue));
					} else if(params.compare(datatype, "float")) {
						params.put("query." + table + "." + column, Float.parseFloat(dValue));
					} else if(params.compare(datatype, "double")) {
						params.put("query." + table + "." + column, Double.parseDouble(dValue));
					} else if(params.compare(datatype, "long")) {
						params.put("query." + table + "." + column, Long.parseLong(dValue));
					}
				}
			}
			if(column != null && sb != null) {
				if(params.compare(datatype, "int")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + Integer.parseInt(dValue) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "float")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + Float.parseFloat(dValue) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "double")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + Double.parseDouble(dValue) + "]]></" + tag.tag("row", column, null, false) + ">");
				} else if(params.compare(datatype, "long")) {
					sb.append("<" + tag.tag("row", column, null, true) + "><![CDATA[" + Long.parseLong(dValue) + "]]></" + tag.tag("row", column, null, false) + ">");
				}
			}
		} else {
			if(params.compare(datatype, "int")) {
				setNull(stmt, index, java.sql.Types.INTEGER);
			} else if(params.compare(datatype, "float")) {
				setNull(stmt, index, java.sql.Types.FLOAT);
			} else if(params.compare(datatype, "double")) {
				setNull(stmt, index, java.sql.Types.DOUBLE);
			} else if(params.compare(datatype, "long")) {
				setNull(stmt, index, java.sql.Types.BIGINT);
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