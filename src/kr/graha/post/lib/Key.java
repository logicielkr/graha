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


package kr.graha.post.lib;

import kr.graha.helper.LOG;
import kr.graha.helper.STR;

/**
 * Record 에서 사용하는...

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Key {
	private Integer _prefix;
	private String _key;
	private String _suffix = null;
	public Key(Integer prefix, String key) {
		if(prefix == Record.PREFIX_TYPE_UNKNOWN) {
			this.parse(key);
		} else {
			this._prefix = prefix;
			this._key = key;
		}
	}
	public Key(Integer prefix, String key, String suffix) {
		if(prefix == Record.PREFIX_TYPE_UNKNOWN) {
			this.parse(key);
		} else {
			this._prefix = prefix;
			this._key = key;
		}
		this._suffix = suffix;
	}
	public Key(String prefix, String key) {
		this._prefix = this.prefix(prefix);
		this._key = key;
	}
	public Key(String prefix, String key, String suffix) {
		this._prefix = this.prefix(prefix);
		this._key = key;
		this._suffix = suffix;
	}
	public Key(Key key, String suffix) {
		this._prefix = key.getPrefix();
		this._key = key._key;
		this._suffix = suffix;
	}
	public Key(String key) {
		this.parse(key);
	}
	private void parse(String key) {
		if(key != null) {
			if(STR.startsWithIgnoreCase(key, "param.")) {
				this._prefix = Record.PREFIX_TYPE_PARAM;
			} else if(STR.startsWithIgnoreCase(key, "query.row.")) {
				this._prefix = Record.PREFIX_TYPE_QUERY_ROW;
			} else if(STR.startsWithIgnoreCase(key, "query.")) {
				this._prefix = Record.PREFIX_TYPE_QUERY;
			} else if(STR.startsWithIgnoreCase(key, "system.")) {
				this._prefix = Record.PREFIX_TYPE_SYSTEM;
			} else if(STR.startsWithIgnoreCase(key, "_system.")) {
				this._prefix = Record.PREFIX_TYPE_U_SYSTEM;
			} else if(STR.startsWithIgnoreCase(key, "messages.code.")) {
				this._prefix = Record.PREFIX_TYPE_MESSAGES_CODE;
			} else if(STR.startsWithIgnoreCase(key, "message.")) {
				this._prefix = Record.PREFIX_TYPE_MESSAGE;
			} else if(STR.startsWithIgnoreCase(key, "code.")) {
				this._prefix = Record.PREFIX_TYPE_CODE;
			} else if(STR.startsWithIgnoreCase(key, "error.")) {
				this._prefix = Record.PREFIX_TYPE_ERROR;
			} else if(STR.startsWithIgnoreCase(key, "generate.")) {
				this._prefix = Record.PREFIX_TYPE_GENERATE;
			} else if(STR.startsWithIgnoreCase(key, "default.")) {
				this._prefix = Record.PREFIX_TYPE_DEFAULT;
			} else if(STR.startsWithIgnoreCase(key, "sequence.")) {
				this._prefix = Record.PREFIX_TYPE_SEQUENCE;
			} else if(STR.startsWithIgnoreCase(key, "generator.")) {
				this._prefix = Record.PREFIX_TYPE_GENERATOR;
			} else if(STR.startsWithIgnoreCase(key, "header.")) {
				this._prefix = Record.PREFIX_TYPE_HEADER;
			} else if(STR.startsWithIgnoreCase(key, "prop.")) {
				this._prefix = Record.PREFIX_TYPE_PROP;
			} else if(STR.startsWithIgnoreCase(key, "session.")) {
				this._prefix = Record.PREFIX_TYPE_SESSION;
			} else if(STR.startsWithIgnoreCase(key, "att.")) {
				this._prefix = Record.PREFIX_TYPE_ATT;
			} else if(STR.startsWithIgnoreCase(key, "init-param.")) {
				this._prefix = Record.PREFIX_TYPE_INIT_PARAM;
			} else if(STR.startsWithIgnoreCase(key, "result.")) {
				this._prefix = Record.PREFIX_TYPE_RESULT;
			} else if(STR.startsWithIgnoreCase(key, "uuid.")) {
				this._prefix = Record.PREFIX_TYPE_UUID;
			} else {
				this._prefix = Record.PREFIX_TYPE_NONE;
				this._key = key;
				return;
			}
			if(
				this._prefix == Record.PREFIX_TYPE_QUERY_ROW ||
				this._prefix == Record.PREFIX_TYPE_MESSAGES_CODE
			) {
				String tmp = key.substring(key.indexOf(".") + 1);
				this._key = tmp.substring(tmp.indexOf(".") + 1);
			} else {
				this._key = key.substring(key.indexOf(".") + 1);
			}
		}
	}
	private Integer prefix(String prefix) {
		if(prefix == null) {
			return null;
		}
		if(STR.compareIgnoreCase(prefix, "param")) {
			return Record.PREFIX_TYPE_PARAM;
		} else if(STR.compareIgnoreCase(prefix, "query.row")) {
			return Record.PREFIX_TYPE_QUERY_ROW;
		} else if(STR.compareIgnoreCase(prefix, "query")) {
			return Record.PREFIX_TYPE_QUERY;
		} else if(STR.compareIgnoreCase(prefix, "system")) {
			return Record.PREFIX_TYPE_SYSTEM;
		} else if(STR.compareIgnoreCase(prefix, "_system")) {
			return Record.PREFIX_TYPE_U_SYSTEM;
		} else if(STR.compareIgnoreCase(prefix, "messages.code")) {
			return Record.PREFIX_TYPE_MESSAGES_CODE;
		} else if(STR.compareIgnoreCase(prefix, "message")) {
			return Record.PREFIX_TYPE_MESSAGE;
		} else if(STR.compareIgnoreCase(prefix, "code")) {
			return Record.PREFIX_TYPE_CODE;
		} else if(STR.compareIgnoreCase(prefix, "error")) {
			return Record.PREFIX_TYPE_ERROR;
		} else if(STR.compareIgnoreCase(prefix, "generate")) {
			return Record.PREFIX_TYPE_GENERATE;
		} else if(STR.compareIgnoreCase(prefix, "default")) {
			return Record.PREFIX_TYPE_DEFAULT;
		} else if(STR.compareIgnoreCase(prefix, "sequence")) {
			return Record.PREFIX_TYPE_SEQUENCE;
		} else if(STR.compareIgnoreCase(prefix, "generator")) {
			return Record.PREFIX_TYPE_GENERATOR;
		} else if(STR.compareIgnoreCase(prefix, "header")) {
			return Record.PREFIX_TYPE_HEADER;
		} else if(STR.compareIgnoreCase(prefix, "prop")) {
			return Record.PREFIX_TYPE_PROP;
		} else if(STR.compareIgnoreCase(prefix, "session")) {
			return Record.PREFIX_TYPE_SESSION;
		} else if(STR.compareIgnoreCase(prefix, "att")) {
			return Record.PREFIX_TYPE_ATT;
		} else if(STR.compareIgnoreCase(prefix, "init-param")) {
			return Record.PREFIX_TYPE_INIT_PARAM;
		} else if(STR.compareIgnoreCase(prefix, "result")) {
			return Record.PREFIX_TYPE_RESULT;
		} else if(STR.compareIgnoreCase(prefix, "uuid")) {
			return Record.PREFIX_TYPE_UUID;
		} else {
			return Record.PREFIX_TYPE_NONE;
		}
	}
	public String getPrefixName() {
		if(this._prefix == Record.PREFIX_TYPE_PARAM) {
			return "param";
		} else if(this._prefix == Record.PREFIX_TYPE_QUERY_ROW) {
			return "query.row";
		} else if(this._prefix == Record.PREFIX_TYPE_QUERY) {
			return "query";
		} else if(this._prefix == Record.PREFIX_TYPE_SYSTEM) {
			return "system";
		} else if(this._prefix == Record.PREFIX_TYPE_U_SYSTEM) {
			return "_system";
		} else if(this._prefix == Record.PREFIX_TYPE_MESSAGES_CODE) {
			return "messages.code";
		} else if(this._prefix == Record.PREFIX_TYPE_MESSAGE) {
			return "message";
		} else if(this._prefix == Record.PREFIX_TYPE_CODE) {
			return "code";
		} else if(this._prefix == Record.PREFIX_TYPE_ERROR) {
			return "error";
		} else if(this._prefix == Record.PREFIX_TYPE_GENERATE) {
			return "generate";
		} else if(this._prefix == Record.PREFIX_TYPE_DEFAULT) {
			return "default";
		} else if(this._prefix == Record.PREFIX_TYPE_SEQUENCE) {
			return "sequence";
		} else if(this._prefix == Record.PREFIX_TYPE_GENERATOR) {
			return "generator";
		} else if(this._prefix == Record.PREFIX_TYPE_HEADER) {
			return "header";
		} else if(this._prefix == Record.PREFIX_TYPE_PROP) {
			return "prop";
		} else if(this._prefix == Record.PREFIX_TYPE_SESSION) {
			return "session";
		} else if(this._prefix == Record.PREFIX_TYPE_ATT) {
			return "att";
		} else if(this._prefix == Record.PREFIX_TYPE_INIT_PARAM) {
			return "init-param";
		} else if(this._prefix == Record.PREFIX_TYPE_RESULT) {
			return "result";
		} else if(this._prefix == Record.PREFIX_TYPE_UUID) {
			return "uuid";
		} else {
			return "";
		}
	}
	private Integer prefixs(String prefix) {
		if(prefix == null) {
			return null;
		}
		if(STR.compareIgnoreCase(prefix, "param.")) {
			return Record.PREFIX_TYPE_PARAM;
		} else if(STR.compareIgnoreCase(prefix, "query.row.")) {
			return Record.PREFIX_TYPE_QUERY_ROW;
		} else if(STR.compareIgnoreCase(prefix, "query.")) {
			return Record.PREFIX_TYPE_QUERY;
		} else if(STR.compareIgnoreCase(prefix, "system.")) {
			return Record.PREFIX_TYPE_SYSTEM;
		} else if(STR.compareIgnoreCase(prefix, "_system.")) {
			return Record.PREFIX_TYPE_U_SYSTEM;
		} else if(STR.compareIgnoreCase(prefix, "messages.code.")) {
			return Record.PREFIX_TYPE_MESSAGES_CODE;
		} else if(STR.compareIgnoreCase(prefix, "message.")) {
			return Record.PREFIX_TYPE_MESSAGE;
		} else if(STR.compareIgnoreCase(prefix, "code.")) {
			return Record.PREFIX_TYPE_CODE;
		} else if(STR.compareIgnoreCase(prefix, "error.")) {
			return Record.PREFIX_TYPE_ERROR;
		} else if(STR.compareIgnoreCase(prefix, "generate.")) {
			return Record.PREFIX_TYPE_GENERATE;
		} else if(STR.compareIgnoreCase(prefix, "default.")) {
			return Record.PREFIX_TYPE_DEFAULT;
		} else if(STR.compareIgnoreCase(prefix, "sequence.")) {
			return Record.PREFIX_TYPE_SEQUENCE;
		} else if(STR.compareIgnoreCase(prefix, "generator.")) {
			return Record.PREFIX_TYPE_GENERATOR;
		} else if(STR.compareIgnoreCase(prefix, "header.")) {
			return Record.PREFIX_TYPE_HEADER;
		} else if(STR.compareIgnoreCase(prefix, "prop.")) {
			return Record.PREFIX_TYPE_PROP;
		} else if(STR.compareIgnoreCase(prefix, "session.")) {
			return Record.PREFIX_TYPE_SESSION;
		} else if(STR.compareIgnoreCase(prefix, "att.")) {
			return Record.PREFIX_TYPE_ATT;
		} else if(STR.compareIgnoreCase(prefix, "init-param.")) {
			return Record.PREFIX_TYPE_INIT_PARAM;
		} else if(STR.compareIgnoreCase(prefix, "result.")) {
			return Record.PREFIX_TYPE_RESULT;
		} else if(STR.compareIgnoreCase(prefix, "uuid.")) {
			return Record.PREFIX_TYPE_UUID;
		} else {
			return Record.PREFIX_TYPE_NONE;
		}
	}
	public Integer getPrefix() {
		return this._prefix;
	}
	private String getSuffix() {
		return this._suffix;
	}
	public String getKey() {
		if(this._suffix == null) {
			if(this._key == null) {
				return null;
			} else {
				return this._key;
			}
		}
		if(this._key == null) {
			if(this._suffix == null) {
				return null;
			} else {
				return this._suffix;
			}
		}
		return this._key + "." + this._suffix;
	}
	public boolean startsWith(String prefix) {
		if(this.getPrefix() == null || this.getPrefix() == Record.PREFIX_TYPE_NONE) {
			return this.getKey().startsWith(prefix);
		} else {
			Integer p = null;
			if(prefix.endsWith(".")) {
				p = this.prefixs(prefix);
			} else {
				p = this.prefix(prefix);
			}
			return this.getPrefix() != null && p != null && this.getPrefix() == p;
		}
	}
	public boolean endsWith(String suffix) {
		if(this._suffix == null) {
			return this.getKey().endsWith(suffix);
		} else {
			if(suffix != null && suffix.startsWith(".") && this.compareSuffix(suffix.substring(1))) {
				return true;
			} else if(this.compareSuffix(suffix)) {
				return true;
			}
		}
		return false;
	}
	public String substring(int idx) {
		return this.getKey();
	}
	public boolean equals(String other) {
		return this.equals(Record.key(other));
	}
	private boolean comparePrefix(Integer other) {
		if(this.getPrefix() == null && other == null) {
			return true;
		} else if(this.getPrefix() == null || other == null) {
			return false;
		} else {
			return this.getPrefix() == other;
		}
	}
	private boolean compareKey(String other) {
		String key = this.getKey();
		if(key == null && other == null) {
			return true;
		} else if(key == null || other == null) {
			return false;
		} else {
			return key.equals(other);
		}
	}
	private boolean compareSuffix(String other) {
		if(this.getSuffix() == null && other == null) {
			return true;
		} else if(this.getSuffix() == null || other == null) {
			return false;
		} else {
			return this.getSuffix().equals(other);
		}
	}
	private boolean equals(Key other) {
		return this.comparePrefix(other.getPrefix()) && this.compareKey(other.getKey());
	}
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other == null || this.getClass() != other.getClass()) {
			return false;
		}
		return this.equals((Key)other);
	}
	public int hashCode() {
		return java.util.Objects.hash(this.getPrefix(), this.getKey());
	}
	public String toString() {
		if(this.getPrefix() == Record.PREFIX_TYPE_NONE) {
			return this.getKey();
		} else {
			return this.getPrefixName() + "." + this.getKey();
		}
	}
}
