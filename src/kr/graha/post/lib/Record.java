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

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import kr.graha.post.model.utility.AuthUtility;
import java.util.logging.Level;
import kr.graha.post.xml.GDocument;

/**
 * Graha(그라하)에서 파라미터를 관리하기 위해 사용하는 라이브러리

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Record<K, V> {
	public static int PREFIX_TYPE_NONE = 1;
	public static int PREFIX_TYPE_UNKNOWN = 2;
	public static int PREFIX_TYPE_PARAM = 3;
	public static int PREFIX_TYPE_QUERY = 4;
	public static int PREFIX_TYPE_QUERY_ROW = 5;
	public static int PREFIX_TYPE_SYSTEM = 6;
	public static int PREFIX_TYPE_U_SYSTEM = 7;
	
	public static int PREFIX_TYPE_MESSAGES_CODE = 8;
	public static int PREFIX_TYPE_MESSAGE = 9;
	public static int PREFIX_TYPE_CODE = 10;
	public static int PREFIX_TYPE_ERROR = 11;
	public static int PREFIX_TYPE_GENERATE = 12;
	public static int PREFIX_TYPE_DEFAULT = 13;
	public static int PREFIX_TYPE_SEQUENCE = 14;
	public static int PREFIX_TYPE_HEADER = 15;
	
	public static int PREFIX_TYPE_PROP = 16;
	public static int PREFIX_TYPE_SESSION = 17;
	public static int PREFIX_TYPE_ATT = 18;
	public static int PREFIX_TYPE_INIT_PARAM = 19;
	
	public static int PREFIX_TYPE_RESULT = 20;
	
	public static int PREFIX_TYPE_UUID = 21;
	
	public static int PREFIX_TYPE_GENERATOR = 22;
	
	public static boolean FIND_GROWS = true;
	public static boolean NOT_FIND_GROWS = false;
	public static boolean DEFAULT_FIND_GROWS = false;
	private Map<Key, Object> map = null;
	private GDocument document = null;
	public Record() {
		this.map = new HashMap<Key, Object>();
	}
	public void setGDocument(GDocument document) {
		this.document = document;
	}
	public Object getObject(Key key) {
		return this.get(key);
	}
	public boolean containsKey(Key key) {
		return this.containsKey(key, true);
	}
	public boolean containsKey(Key key, Boolean includeQuery) {
		if(this.map.containsKey(key)) {
			return true;
		} else if(includeQuery && key.getPrefix() == Record.PREFIX_TYPE_QUERY) {
			if(this.document == null) {
				return false;
			} else {
				return this.document.containsKey(key);
			}
		}
		return false;
	}
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	public Set keySet() {
		return this.map.keySet();
	}
	public Object put(Key key, Object value) {
		return this.map.put(key, value);
	}
	private Object get(Key key) {
		return this.get(key, true);
	}
	private Object get(Key key, Boolean includeQuery) {
		if(this.map.containsKey(key)) {
			return this.map.get(key);
		} else if(includeQuery && key.getPrefix() == Record.PREFIX_TYPE_QUERY) {
			if(this.document == null) {
				return null;
			} else {
				return this.document.get(key);
			}
		}
		return null;
	}
	public void puts(Key key, String value) {
		this.puts(key, (Object)value);
	}
	public void puts(Key key, Object value) {
		/*
		if(key.startsWith("prop.")) {
			LOG.fine("" + key);
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			if(trace != null) {
				for(int i = 0; i < trace.length; i++) {
					LOG.finest(trace[i].toString());
				}
			}
		}
		*/
		if(this.map.containsKey(key)) {
			List l = null;
			if(this.map.get(key) instanceof List) {
				l = (List)this.map.get(key);
			} else {
				l = new ArrayList();
				l.add(this.map.get(key));
			}
			l.add(value);
			this.map.put(key, l);
		} else {
			this.map.put(key, value);
		}
	}
	public void putDate(
		Key key, 
		String year, 
		String month, 
		String day
	) {
		this.putDate(key, year, month, day, null, null, null);
	}
	public void putDate(
		Key key, 
		String year, 
		String month, 
		String day, 
		String hour, 
		String minute, 
		String second
	) {
		int y = this.intValue(year, -1);
		int m = this.intValue(month, -1);
		int d = this.intValue(day, -1);
		if(y > 0 && m > 0 && m <= 12 && d > 0 && d <= 31) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, y);
			cal.set(Calendar.MONTH, m);
			cal.set(Calendar.DAY_OF_MONTH, d);
			if(hour != null && !hour.equals("")) {
				int x = this.intValue(hour, -1);
				if(x >= 0 && x <= 24) {
					cal.set(Calendar.HOUR_OF_DAY, x);
				}
			}
			if(minute != null && !minute.equals("")) {
				int x = this.intValue(minute, -1);
				if(x >= 0 && x <= 60) {
					cal.set(Calendar.MINUTE, x);
				}
			}
			if(second != null && !second.equals("")) {
				int x = this.intValue(second, -1);
				if(x >= 0 && x <= 60) {
					cal.set(Calendar.SECOND, x);
				}
			}
			this.puts(key, new Date(cal.getTime().getTime()));
		}
		this.puts(key, null);
	}
	/*
	private int intValue(String value) {
		return this.intValue(value, 0);
	}
	*/
	private Integer intObject(String value) {
		if(value == null) {
			return null;
		}
		value = value.replace(" ", "");
		value = value.replace(",", "");
		if(value != null && value.equals("")) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	private int intValue(String value, int defaultValue) {
		if(value == null) {
			return defaultValue;
		}
		value = value.replace(" ", "");
		value = value.replace(",", "");
		if(value != null && value.equals("")) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	public int getInt(Key key) {
		return this.getInt(key, 0);
	}
	public int getInt(Key key, int defaultValue) {
		Integer value = this.getIntObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.intValue();
	}
	public Object getValueOfList(List l) {
		return l.get(l.size() - 1);
	}
	public Integer getIntObject(Key key) {
		if(this.containsKey(key)) {
			if(this.get(key) instanceof Integer) {
				return (Integer)this.get(key);
			}
			if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Integer) {
					return (Integer)this.getValueOfList(l);
				}
			}
			String value = null;
			if(this.get(key) instanceof String) {
				value = (String)this.get(key);
			} else if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				LOG.warning(e);
			}
		}
		return null;
	}
	public float getFloat(Key key) {
		return this.getFloat(key, 0);
	}
	public float getFloat(Key key, float defaultValue) {
		Float value = this.getFloatObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.floatValue();
	}
	public Float getFloatObject(Key key) {
		if(this.containsKey(key)) {
			if(this.get(key) instanceof Float) {
				return (Float)this.get(key);
			}
			if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Float) {
					return (Float)this.getValueOfList(l);
				}
			}
			String value = null;
			if(this.get(key) instanceof String) {
				value = (String)this.get(key);
			} else if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				return Float.valueOf(value);
			} catch (NumberFormatException e) {
				LOG.warning(e);
			}
		}
		return null;
	}
	public double getDouble(Key key) {
		return this.getDouble(key, 0);
	}
	public double getDouble(Key key, double defaultValue) {
		Double value = this.getDoubleObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.doubleValue();
	}
	public Double getDoubleObject(Key key) {
		if(this.containsKey(key)) {
			if(this.get(key) instanceof Double) {
				return (Double)this.get(key);
			}
			if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Double) {
					return (Double)this.getValueOfList(l);
				}
			}
			String value = null;
			if(this.get(key) instanceof String) {
				value = (String)this.get(key);
			} else if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				return Double.valueOf(value);
			} catch (NumberFormatException e) {
				LOG.warning(e);
			}
		}
		return null;
	}
	public long getLong(Key key) {
		return this.getLong(key, 0);
	}
	public long getLong(Key key, long defaultValue) {
		Long value = this.getLongObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.longValue();
	}
	public Long getLongObject(Key key) {
		if(this.containsKey(key)) {
			if(this.get(key) instanceof Long) {
				return (Long)this.get(key);
			}
			if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Long) {
					return (Long)this.getValueOfList(l);
				}
			}
			String value = null;
			if(this.get(key) instanceof String) {
				value = (String)this.get(key);
			} else if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException e) {
				LOG.warning(e);
			}
		}
		return null;
	}
	public String getString(Key key) {
		if(!this.containsKey(key)) {
			return null;
		}
		if(this.get(key) instanceof String) {
			return (String)this.get(key);
		} else if(this.get(key) instanceof List) {
			List l = (List)this.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					return (String)this.getValueOfList(l);
				} else {
					return ("" + this.getValueOfList(l));
				}
			} else {
				return null;
			}
		} else {
			return ("" + this.get(key));
		}
	}
	public boolean getBoolean(Key key) {
		boolean result = false;
		if(!this.containsKey(key)) {
			result = false;
		} else {
			Object value = this.get(key);
			if(value instanceof Boolean) {
				result = (Boolean)value;
			} else if(value instanceof String) {
				result = STR.trueValue(this.getString(key));
			} else if(value instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty()) {
					if(this.getValueOfList(l) instanceof Boolean) {
						return (Boolean)this.getValueOfList(l);
					} else if(this.getValueOfList(l) instanceof String) {
						result = STR.trueValue((String)this.getValueOfList(l));
					}
				}
			}
		}
		return result;
	}
	public Date getDate(Key key, String pattern) {
		if(!this.containsKey(key)) {
			return null;
		}
		String value = null;
		if(this.get(key) instanceof String) {
			value = (String)this.get(key);
		} else if(this.get(key) instanceof List) {
			List l = (List)this.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		value = value.trim();
		DateFormat df = new SimpleDateFormat(pattern);
		
		Date result = null;
		try {
			result = new Date(df.parse(value).getTime());
		} catch (ParseException e) {
			LOG.warning(e);
		}
		return result;
	}
	public Date getDate(Key key) {
		if(!this.containsKey(key)) {
			return null;
		}
		Object value = this.get(key);
		if(value instanceof Date) {
			return (Date)value;
		} else if(value instanceof java.util.Date) {
			return new java.sql.Date(((java.util.Date)value).getTime());
		} else if(value instanceof Timestamp) {
			return new java.sql.Date(((Timestamp)value).getTime());
		} else if(value instanceof List) {
			List l = (List)this.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof Date) {
					return (Date)this.getValueOfList(l);
				} else if(this.getValueOfList(l) instanceof java.util.Date) {
					return new java.sql.Date(((java.util.Date)this.getValueOfList(l)).getTime());
				} else if(this.getValueOfList(l) instanceof Timestamp) {
					return new java.sql.Date(((Timestamp)this.getValueOfList(l)).getTime());
				}
			}
		}
		return null;
	}
	public Timestamp getTimestamp(Key key) {
		if(!this.containsKey(key)) {
			return null;
		}
		Object value = this.get(key);
		if(value instanceof Timestamp) {
			return (Timestamp)value;
		} else if(value instanceof java.util.Date) {
			return new java.sql.Timestamp(((java.util.Date)value).getTime());
		} else if(value instanceof Date) {
			return new java.sql.Timestamp(((Date)value).getTime());
		} else if(value instanceof List) {
			List l = (List)this.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof Timestamp) {
					return (Timestamp)this.getValueOfList(l);
				} else if(this.getValueOfList(l) instanceof Date) {
					return new Timestamp(((Date)this.getValueOfList(l)).getTime());
				} else if(this.getValueOfList(l) instanceof java.util.Date) {
					return new Timestamp(((java.util.Date)this.getValueOfList(l)).getTime());
				}
			}
		}
		return null;
	}
	public Timestamp getTimestamp(Key key, String pattern) {
		if(!this.containsKey(key)) {
			return null;
		}
		String value = null;
		if(this.get(key) instanceof String) {
			value = (String)this.get(key);
		} else if(this.get(key) instanceof List) {
			List l = (List)this.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		
		value = value.trim();
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		
		Timestamp result = null;
		try {
			result = new Timestamp(df.parse(value).getTime());
		} catch (ParseException e) {
			LOG.warning(e);
		}
		return result;
	}
	public boolean hasKey(Key key) {
		return this.hasKey(key, true);
	}
/**
 * 키에 해당하는 값이 있는지 검사한다.
 * 이 메소드는 containsKey와 다르다.
 * 키에 해당하는 값이 있다고 하더라도, 값이 null 이거나 공백("") 인 경우 fasle 를 반환한다.
 * @param key 키
 * @return 키에 해당하는 값이 있는지 여부
 */
	public boolean hasKey(Key key, Boolean includeQuery) {
		boolean result = false;
		if(this.containsKey(key, includeQuery)) {
			if(this.get(key, includeQuery) == null) {
				result = false;
			} else if(this.get(key, includeQuery) instanceof String && ((String)this.get(key, includeQuery)).trim().equals("")) {
				result = false;
			} else if(this.get(key, includeQuery) instanceof List) {
				result = true;
				List vv = (List)this.get(key, includeQuery);
				for (Object v : vv) {
					if(v instanceof String) {
						if(!((String)v).trim().equals("")) {
							result = true;
							break;
						}
					} else {
						result = true;
					}
				}
			} else {
				result = true;
			}
		}
		return result;
	}
/**
 * AuthUtility 에서 사용
 */
	public boolean in(Key key, String value) {
		if(this.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			if(this.get(key) instanceof String[]) {
				String[] vv = (String[])this.get(key);
				for (String v : vv) {
					
					if(v != null && v.equals(value)) {
						return true;
					}
				}
			} else if(this.get(key) instanceof List) {
				List<String> vv = (List)this.get(key);
				for (String v : vv) {
					if(v != null && v.equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean equals(Key key, String value) {
		if(this.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			if(this.get(key) instanceof String) {
				if(value.equals(this.getString(key))) {
					return true;
				}
			} else if(this.get(key) instanceof List) {
				List l = (List)this.get(key);
				if(!l.isEmpty()) {
					if(this.getValueOfList(l) instanceof String) {
						if((String)this.getValueOfList(l) != null && value.equals((String)this.getValueOfList(l))) {
								return true;
						}
					}
				}
			}
		}
		return false;
	}
/**
 * AuthUtility 에서 사용
 */
	public boolean isempty(Key key) {
		if(this.containsKey(key)) {
			if(this.hasKey(key)) {
				return false;
			}
		}
		return true;
	}
	protected boolean isnotempty(Key key) {
		return !this.isempty(key);
	}
/**
 * AuthUtility 에서 사용
 */
	public boolean check(Key key, String value, int check) {
		if(this.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			return this.check(this.getIntObject(key), this.intObject(value), check);
		}
		return false;
	}
	public boolean check(Key key, Key value, int check) {
		if(this.containsKey(key) && this.containsKey(value)) {
			if(this.get(key) == null || this.get(value) == null) {
				return false;
			}
			return this.check(this.getIntObject(key), this.getIntObject(value), check);
		}
		return false;
	}
	public boolean check(String key, Key value, int check) {
		if(this.containsKey(value) && key != null && !key.trim().equals("")) {
			if(this.get(value) == null) {
				return false;
			}
			return this.check(this.intObject(key), this.getIntObject(value), check);
		}
		return false;
	}
	public boolean check(Integer i1, Integer i2, int check) {
		if(i1 == null || i2 == null) {
			return false;
		}
		if(check == AuthUtility.GreaterThan && i1.intValue() > i2.intValue()) {
			return true;
		} else if(check == AuthUtility.GreaterThanOrEqualTo && i1.intValue() >= i2.intValue()) {
			return true;
		} else if(check == AuthUtility.LessThan && i1.intValue() < i2.intValue()) {
			return true;
		} else if(check == AuthUtility.LessThanOrEqualTo && i1.intValue() <= i2.intValue()) {
			return true;
		}
		return false;
	}
	public boolean check(String key, String value, int check) {
		return this.check(this.intObject(key), this.intObject(value), check);
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean notNull(Key key) {
		return !this.isempty(key);
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean maxLength(Key key, String length) {
		boolean result = false;
		int len = Integer.parseInt(length);
		if(this.isempty(key)) {
			result = true;
		} else if(!(this.get(key) instanceof String)) {
			result = false;
		} else if(this.getString(key) == null) {
			result = true;
		} else if(this.getString(key).length() <= len) {
			result = true;
		}
		return result;
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean minLength(Key key, String length) {
		boolean result = false;
		int len = Integer.parseInt(length);
		if(this.isempty(key)) {
			result = true;
		} else if(!(this.get(key) instanceof String)) {
			result = false;
		} else if(this.getString(key) == null) {
			result = true;
		} else if(this.getString(key).length() == 0) {
			result = true;
		} else if(this.getString(key).length() >= len) {
			result = true;
		}
		return result;
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean maxValue(Key key, String value) {
		boolean result = false;
		if(this.isempty(key)) {
			result = true;
		} else if(this.getString(key) == null) {
			result = true;
		} else {
			if(this.getString(key).indexOf(".") >= 0 || value.indexOf(".") >= 0) {
				if(this.getDouble(key) <= Double.valueOf(value)) {
					result = true;
				}
			} else {
				if(this.getLong(key) <= Long.valueOf(value)) {
					result = true;
				}
			}
		}
		return result;
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean minValue(Key key, String value) {
		boolean result = false;
		if(this.isempty(key)) {
			result = false;
		} else if(this.getString(key) == null) {
			result = false;
		} else {
			if(this.getString(key).indexOf(".") >= 0 || value.indexOf(".") >= 0) {
				if(this.getDouble(key) >= Double.valueOf(value)) {
					result = true;
				}
			} else {
				if(this.getLong(key) >= Long.valueOf(value)) {
					result = true;
				}
			}
		}
		return result;
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean numberFormat(Key key, String type) {
		if(this.isempty(key)) {
			return true;
		} else {
			if(!(this.get(key) instanceof String)) {
				return true;
			}
			String value = (String)this.get(key);
			if(value == null || value.trim().equals("")) {
				return true;
			}
			if(value != null) {
				value = value.replace(" ", "");
				value = value.replace(",", "");
			}
			
			if(type != null && type.equals("int")) {
				try {
					Integer.parseInt(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("float")) {
				try {
					Float.parseFloat(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("double")) {
				try {
					Double.parseDouble(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("long")) {
				try {
					Long.parseLong(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return false;
		}
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean dateFormat(Key key, String pattern) {
		if(!this.containsKey(key)) {
			return false;
		}
		if(!(this.get(key) instanceof String)) {
			return false;
		}
		String value = (String)this.get(key);
		
		value = value.trim();
		
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		try {
			df.parse(value);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
/**
 * ValidationParam 에서 사용
 */
	public boolean format(Key key, String regexp) {
		if(!this.containsKey(key)) {
			return false;
		}
		if(!(this.get(key) instanceof String)) {
			return false;
		}
		String value = (String)this.get(key);
		
		value = value.trim();
		return value.matches(regexp);
	}
	public boolean isArray(Key key) {
		boolean result = false;
		if(!this.map.containsKey(key)) {
			result = false;
		} else if(this.map.get(key) == null) {
			result = false;
		} else if((this.map.get(key) instanceof List)) {
			result = true;
		}
		return result;
	}
	public List getArray(Key key) {
		if(this.isArray(key)) {
			return (List)this.map.get(key);
		} else {
			return null;
		}
	}
	public void dump(Level level) {
		if(!this.map.isEmpty()) {
			Iterator<Key> it = this.map.keySet().iterator();
			while(it.hasNext()) {
				Key key = (Key)it.next();
				if(this.map.get(key) instanceof String[]) {
					for(int i = 0; i < ((String[])this.map.get(key)).length; i++) {
						LOG.log(level, key + "." + i + "(SA)=" + ((String[])this.map.get(key))[i]);
					}
				} else if(this.isArray(key)) {
					for (Object v : (List)this.map.get(key)) {
						LOG.log(level, key + "(A)=" + v);
					}
				} else {
					LOG.log(level, key + "(NA)=" + this.map.get(key));
				}
			}
		}
	}
	public static Key key(String key) {
		return new Key(key);
	}
	public static Key key(Integer prefix, String key) {
		return new Key(prefix, key);
	}
	public static Key key(String prefix, String key) {
		return new Key(prefix, key);
	}
	public static Key key(Key key, String suffix) {
		return new Key(key, suffix);
	}
	public static Key key(Integer prefix, String key, String suffix) {
		return new Key(prefix, key, suffix);
	}
	public static Key key(String prefix, String key, String suffix) {
		return new Key(prefix, key, suffix);
	}
}
