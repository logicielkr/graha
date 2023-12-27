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

import java.util.List;
import kr.graha.post.lib.Buffer;

/**
 * Graha(그라하) SQLInfo
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class SQLInfo {
	public static int TYPE_PSTMT = 1;
	public static int TYPE_CSTMT = 2;
	private String sql;
	private List<SQLParameter> parameters;
	private int type = 0;
	public SQLInfo(
		Buffer sql,
		List<SQLParameter> parameters
	) {
		this.sql = sql.toString();
		this.type = SQLInfo.TYPE_PSTMT;
		this.parameters = parameters;
	}
	public SQLInfo(
		Buffer sql,
		int type,
		List<SQLParameter> parameters
	) {
		this.sql = sql.toString();
		this.type = type;
		this.parameters = parameters;
	}
	public SQLInfo(
		String sql,
		List<SQLParameter> parameters
	) {
		this.sql = sql;
		this.type = SQLInfo.TYPE_PSTMT;
		this.parameters = parameters;
	}
	public SQLInfo(
		String sql,
		int type,
		List<SQLParameter> parameters
	) {
		this.sql = sql;
		this.type = type;
		this.parameters = parameters;
	}
	public String getSql() {
		return this.sql;
	}
	public int getType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<SQLParameter> getParameters() {
		return this.parameters;
	}
	public String toString() {
		Buffer buffer = new Buffer();
		buffer.appendL("type = " + this.type);
		buffer.appendL(sql);
		if(this.parameters != null) {
			for(int i = 0; i < this.parameters.size(); i++) {
				SQLParameter parameter = this.parameters.get(i);
				buffer.append(parameter.toString());
			}
		}
		return buffer.toString();
	}
}
