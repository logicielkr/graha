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


package kr.graha.assistant;

import java.sql.Connection;
import java.io.IOException;

/**
 * Graha(그라하) Hyper SQL Database를 위한 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class DBUtilHSQLImpl extends DBUtil {
	protected DBUtilHSQLImpl() throws IOException {
	}
	protected String getNextval(Connection con, String tableName, String columnName, String schemaName, String defaultSchema) {
		String prefix = "";
		if(defaultSchema != null && schemaName != null && !schemaName.equals(defaultSchema)) {
			prefix = schemaName + ".";
		}
		String sequence = getSequence(con, tableName + "$" + columnName);
		if(sequence == null) {
			return "NEXT VALUE FOR &quot;" + prefix + tableName + "$" + columnName + "&quot;";
		} else {
			return "NEXT VALUE FOR &quot;" + prefix + sequence + "&quot;";
		}
	}
	protected String getSequence(Connection con, String sequenceName) {
		String sql = "select SEQUENCE_NAME from INFORMATION_SCHEMA.SEQUENCES where lower(SEQUENCE_NAME) = lower(?)";
		return super.getSequence(con, sql, sequenceName);
	}
}
