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

import kr.graha.post.model.Param;
import kr.graha.post.lib.Buffer;

/**
 * Graha(그라하) SQLParameter
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class SQLParameter {
	private Integer sqlType = null;
	private Object value;
	private int dataType;
	private String sqlTypeName = null;
	public SQLParameter(Object value, int dataType) {
		this.value = value;
		this.dataType = dataType;
	}
	public int getSqlType() {
		if(this.sqlType == null) {
			if(this.dataType == Param.DATA_TYPE_INT) {
				this.sqlType = java.sql.Types.INTEGER;
			} else if(this.dataType == Param.DATA_TYPE_LONG) {
				this.sqlType = java.sql.Types.BIGINT;
			} else if(this.dataType == Param.DATA_TYPE_FLOAT) {
				this.sqlType = java.sql.Types.FLOAT;
			} else if(this.dataType == Param.DATA_TYPE_DOUBLE) {
				this.sqlType = java.sql.Types.DOUBLE;
			} else if(this.dataType == Param.DATA_TYPE_BOOLEAN) {
				this.sqlType = java.sql.Types.BOOLEAN;
			} else if(this.dataType == Param.DATA_TYPE_DATE) {
				this.sqlType = java.sql.Types.DATE;
			} else if(this.dataType == Param.DATA_TYPE_TIMESTAMP) {
				this.sqlType = java.sql.Types.TIMESTAMP;
			} else if(this.dataType == Param.DATA_TYPE_VARCHAR) {
				this.sqlType = java.sql.Types.VARCHAR;
			} else if(this.dataType == Param.DATA_TYPE_CHAR) {
				this.sqlType = java.sql.Types.CHAR;
			}
		}
		return this.sqlType;
	}
	public int getDataType() {
		return this.dataType;
	}
	public String getSqlTypeName() {
		if(this.sqlTypeName == null) {
			if(this.dataType == Param.DATA_TYPE_INT) {
				this.sqlTypeName = "int";
			} else if(this.dataType == Param.DATA_TYPE_LONG) {
				this.sqlTypeName = "long";
			} else if(this.dataType == Param.DATA_TYPE_FLOAT) {
				this.sqlTypeName = "float";
			} else if(this.dataType == Param.DATA_TYPE_DOUBLE) {
				this.sqlTypeName = "double";
			} else if(this.dataType == Param.DATA_TYPE_BOOLEAN) {
				this.sqlTypeName = "boolean";
			} else if(this.dataType == Param.DATA_TYPE_DATE) {
				this.sqlTypeName = "date";
			} else if(this.dataType == Param.DATA_TYPE_TIMESTAMP) {
				this.sqlTypeName = "timestamp";
			} else if(this.dataType == Param.DATA_TYPE_VARCHAR) {
				this.sqlTypeName = "varchar";
			} else if(this.dataType == Param.DATA_TYPE_CHAR) {
				this.sqlTypeName = "char";
			}
		}
		return this.sqlTypeName;
	}
	public Object getValue() {
		return this.value;
	}
	public String toString() {
		Buffer buffer = new Buffer();
		buffer.append("sqlType=" + this.sqlType);
		buffer.append(", dataType=" + this.dataType);
		buffer.append(", sqlTypeName=" + this.sqlTypeName);
		buffer.append(", value=" + this.value);
		return buffer.toString();
	}
}
