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
	protected static int IsEmpty = 1;
	protected static int IsNotEmpty = 2;
	protected static int NotIn = 3;
	protected static int In = 4;
	protected static int Equals = 5;
	protected static int NotEquals = 6;
	protected static int GreaterThan = 7;
	protected static int GreaterThanOrEqualTo = 8;
	protected static int LessThan = 9;
	protected static int LessThanOrEqualTo = 10;
	private AuthParser() {
	}
	protected static AuthInfo parse(String auth) {
		String right = null;
		String left = null;
		String op = null;
		if(auth == null || auth.trim().equals("")) {
			return null;
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
			return null;
		}
		op = op.trim();
		AuthInfo info = new AuthInfo();
		info.left = left;
		info.right = right;
		info.op = parseOP(op);
		if(info.op == 0) {
			return null;
		} else {
			return info;
		}
	}
	private static int parseOP(String op) {
		if(op == null) {
			return 0;
		}
		if(op.equalsIgnoreCase("is empty") || op.equalsIgnoreCase("IsEmpty") || op.equalsIgnoreCase("empty")) {
			return AuthParser.IsEmpty;
		} else if(op.equalsIgnoreCase("is not empty") || op.equalsIgnoreCase("IsNotEmpty") || op.equalsIgnoreCase("exists") || op.equalsIgnoreCase("notEmpty")) {
			return AuthParser.IsNotEmpty;
		} else {
			if(op.equalsIgnoreCase("In")) {
				return AuthParser.In;
			} else if(op.equalsIgnoreCase("not in") || op.equalsIgnoreCase("NotIn")) {
				return AuthParser.NotIn;
			} else if(op.equalsIgnoreCase("Equals") || op.equalsIgnoreCase("=") || op.equalsIgnoreCase("==")) {
				return AuthParser.Equals;
			} else if(op.equalsIgnoreCase("not equals") || op.equalsIgnoreCase("NotEquals") || op.equalsIgnoreCase("!=")) {
				return AuthParser.NotEquals;
			} else if(op.equalsIgnoreCase("GreaterThan") || op.equalsIgnoreCase(">")) {
				return AuthParser.GreaterThan;
			} else if(op.equalsIgnoreCase("GreaterThanOrEqualTo") || op.equalsIgnoreCase(">=") || op.equalsIgnoreCase("=>")) {
				return AuthParser.GreaterThanOrEqualTo;
			} else if(op.equalsIgnoreCase("LessThan") || op.equalsIgnoreCase("<")) {
				return AuthParser.LessThan;
			} else if(op.equalsIgnoreCase("LessThanOrEqualTo") || op.equalsIgnoreCase("<=") || op.equalsIgnoreCase("=<")) {
				return AuthParser.LessThanOrEqualTo;
			}
		}
		return 0;
	}
	public static boolean auth(String auth, Record param) {
		AuthInfo info = parse(auth);
		return auth(info, param);
	}
	public static boolean auth(AuthInfo info, Record param) {
		if(info == null) {
			return true;
		}
		if(info.op == AuthParser.IsEmpty) {
			return AuthParser.isEmpty(info.left, param);
		} else if(info.op == AuthParser.IsNotEmpty) {
			return !AuthParser.isEmpty(info.left, param);
		} else {
			if(info.right == null) {
				return false;
			}
			if(info.op == AuthParser.In) {
				return AuthParser.in(info.left, info.right, param);
			} else if(info.op == AuthParser.NotIn) {
				return !AuthParser.in(info.left, info.right, param);
			} else if(info.op == AuthParser.Equals) {
				return AuthParser.equals(info.left, info.right, param);
			} else if(info.op == AuthParser.NotEquals) {
				return !AuthParser.equals(info.left, info.right, param);
			} else if(info.op == AuthParser.GreaterThan) {
				return AuthParser.gt(info.left, info.right, param);
			} else if(info.op == AuthParser.GreaterThanOrEqualTo) {
				return AuthParser.gteq(info.left, info.right, param);
			} else if(info.op == AuthParser.LessThan) {
				return AuthParser.lt(info.left, info.right, param);
			} else if(info.op == AuthParser.LessThanOrEqualTo) {
				return AuthParser.lteq(info.left, info.right, param);
			}
		}
		return true;
	}
	private static boolean equals(String left, String right, Record param) {
		return param.equals(left, right);
	}
	private static boolean in(String left, String right, Record param) {
		return param.in(left, right);
	}
	private static boolean isEmpty(String left, Record param) {
		return param.isempty(left);
	}
	private static boolean gt(String left, String right, Record param) {
		return param.check(left, right, AuthParser.GreaterThan);
	}
	private static boolean gteq(String left, String right, Record param) {
		return param.check(left, right, AuthParser.GreaterThanOrEqualTo);
	}
	private static boolean lt(String left, String right, Record param) {
		return param.check(left, right, AuthParser.LessThan);
	}
	private static boolean lteq(String left, String right, Record param) {
		return param.check(left, right, AuthParser.LessThanOrEqualTo);
	}
}
class AuthInfo {
	protected String left;
	protected String right;
	protected int op;
}