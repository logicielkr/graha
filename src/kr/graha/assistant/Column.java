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

/**
 * Graha(그라하) Column 정보
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Column {
	protected String name;
	protected String typeName;
	protected int dataType;
	protected String isPk;
	protected String isNullable;
	protected String isAutoincrement;
	protected String remarks;
	protected boolean isFk = false;
	protected String getLowerName() {
		return name.toLowerCase();
	}
	protected boolean isNotEmptyRemarks() {
		if(this.remarks == null || this.remarks.trim().equals("")) {
			return false;
		} else {
			return true;
		}
	}
	protected String getRemarksOrName() {
		if(this.remarks == null || this.remarks.trim().equals("")) {
			return name;
		} else {
			return remarks;
		}
	}
	protected boolean isPk() {
		return Boolean.parseBoolean(this.isPk);
	}
	protected boolean isFk() {
		return this.isFk;
	}
}
