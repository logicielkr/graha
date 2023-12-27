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


package kr.graha.post.xml;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * GColumn
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GColumn {
	private String name;
	private int type;
	private String typeName;
	protected GColumn(ResultSetMetaData rsmd, int index) throws SQLException {
		this.setName(rsmd.getColumnName(index).toLowerCase());
		this.setType(rsmd.getColumnType(index));
		this.setTypeName(rsmd.getColumnTypeName(index));
	}
	public GColumn(String name, int type, String typeName) {
		this.setName(name);
		this.setType(type);
		this.setTypeName(typeName);
	}
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	protected int getType() {
		return this.type;
	}
	private void setType(int type) {
		this.type = type;
	}
	protected String getTypeName() {
		return this.typeName;
	}
	private void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
