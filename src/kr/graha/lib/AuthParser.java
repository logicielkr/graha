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

import java.util.logging.Logger;

/**
 * Graha(그라하) 인증 파서

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public final class AuthParser {
	private static Logger logger = Logger.getLogger("kr.graha.lib.AuthParser");
	private AuthParser() {
	}
	public static boolean auth(String auth, Record param) {
		String right = null;
		String left = null;
		String op = null;
		if(auth == null || auth.trim().equals("")) {
			return true;
		}
		auth = auth.trim();
		for(int i = 0; i < auth.length(); i++) {
			if(auth.charAt(i) == '\'') {
				if(right == null) {
					right = auth.substring(i + 1, auth.indexOf("'", i + 1));
				} else {
					right = auth.substring(i + 1, auth.indexOf("'", i + 1));
				}
				i = auth.indexOf("'", i + 1);
			} else if(auth.charAt(i) == '$' && auth.charAt(i + 1) == '{') {
				if(left == null) {
					left = auth.substring(i + 2, auth.indexOf("}", i));
				} else {
					right = auth.substring(i + 2, auth.indexOf("}", i));
				}
				i = auth.indexOf("}", i);
			} else {
				if(op == null) {
					op = new String();
				}
				op += auth.charAt(i);
			}
		}
		if(op == null) {
			return true;
		}
		op = op.trim();
		if(op != null && (op.equalsIgnoreCase("is empty") || op.equalsIgnoreCase("isempty"))) {
			return AuthParser.isEmpty(left, param);
		} else if(op != null && (op.equalsIgnoreCase("is not empty") || op.equalsIgnoreCase("isnotempty"))) {
			return !AuthParser.isEmpty(left, param);
		} else {
			if(right == null) {
				return false;
			}
			if(op != null && op.equalsIgnoreCase("in")) {
				return AuthParser.in(left, right, param);
			} else if(op != null && (op.equalsIgnoreCase("not in") || op.equalsIgnoreCase("notin"))) {
				return !AuthParser.in(left, right, param);
			} else if(op != null && op.equalsIgnoreCase("equals")) {
				return AuthParser.equals(left, right, param);
			} else if(op != null && (op.equalsIgnoreCase("not equals") || op.equalsIgnoreCase("notequals"))) {
				return !AuthParser.equals(left, right, param);
			}
		}
		return true;
	}
	public static boolean equals(String left, String right, Record param) {
		return param.equals(left, right);
	}
	public static boolean in(String left, String right, Record param) {
		return param.in(left, right);
	}
	public static boolean isEmpty(String left, Record param) {
		return param.isempty(left);
	}
}
