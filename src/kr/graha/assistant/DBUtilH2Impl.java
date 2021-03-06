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

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import kr.graha.helper.LOG;
import java.util.Hashtable;
import java.util.Properties;
import java.io.IOException;

/**
 * Graha(그라하) H2를 위한 데이타베이스 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DBUtilH2Impl extends DBUtil {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected DBUtilH2Impl() throws IOException {
		LOG.setLogLevel(logger);
	}
	protected String getNextval(Connection con, String tableName, String columnName) {
		/*
SELECT SEQUENCE_NAME
FROM INFORMATION_SCHEMA.SEQUENCES
		*/
		return "NEXT VALUE FOR " + tableName + "$" + columnName + "";
	}
}
