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

import kr.graha.post.lib.Record;
import kr.graha.post.lib.ParsingException;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) 인증 관련 유틸리티 모음

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public final class AuthUtility {
	public static int IsEmpty = 1;
	public static int IsNotEmpty = 2;
	public static int NotIn = 3;
	public static int In = 4;
	public static int Equals = 5;
	public static int NotEquals = 6;
	public static int GreaterThan = 7;
	public static int GreaterThanOrEqualTo = 8;
	public static int LessThan = 9;
	public static int LessThanOrEqualTo = 10;
	private AuthUtility() {
	}
	public static AuthInfo parse(String auth) {
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
			return AuthUtility.IsEmpty;
		} else if(op.equalsIgnoreCase("is not empty") || op.equalsIgnoreCase("IsNotEmpty") || op.equalsIgnoreCase("exists") || op.equalsIgnoreCase("notEmpty")) {
			return AuthUtility.IsNotEmpty;
		} else {
			if(op.equalsIgnoreCase("In")) {
				return AuthUtility.In;
			} else if(op.equalsIgnoreCase("not in") || op.equalsIgnoreCase("NotIn")) {
				return AuthUtility.NotIn;
			} else if(op.equalsIgnoreCase("Equals") || op.equalsIgnoreCase("=") || op.equalsIgnoreCase("==")) {
				return AuthUtility.Equals;
			} else if(op.equalsIgnoreCase("not equals") || op.equalsIgnoreCase("NotEquals") || op.equalsIgnoreCase("!=")) {
				return AuthUtility.NotEquals;
			} else if(op.equalsIgnoreCase("GreaterThan") || op.equalsIgnoreCase(">")) {
				return AuthUtility.GreaterThan;
			} else if(op.equalsIgnoreCase("GreaterThanOrEqualTo") || op.equalsIgnoreCase(">=") || op.equalsIgnoreCase("=>")) {
				return AuthUtility.GreaterThanOrEqualTo;
			} else if(op.equalsIgnoreCase("LessThan") || op.equalsIgnoreCase("<")) {
				return AuthUtility.LessThan;
			} else if(op.equalsIgnoreCase("LessThanOrEqualTo") || op.equalsIgnoreCase("<=") || op.equalsIgnoreCase("=<")) {
				return AuthUtility.LessThanOrEqualTo;
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
//			LOG.debug("info is null");
			return true;
		}
		if(info.op == AuthUtility.IsEmpty) {
//			LOG.debug(Boolean.toString(AuthUtility.isEmpty(info.left, param)));
			return AuthUtility.isEmpty(info.left, param);
		} else if(info.op == AuthUtility.IsNotEmpty) {
//			LOG.debug(Boolean.toString(!AuthUtility.isEmpty(info.left, param)));
			return !AuthUtility.isEmpty(info.left, param);
		} else {
			if(info.right == null) {
//				LOG.debug("info.right is null");
				return false;
			}
			if(info.op == AuthUtility.In) {
//				LOG.debug(Boolean.toString(AuthUtility.in(info.left, info.right, param)));
				return AuthUtility.in(info.left, info.right, param);
			} else if(info.op == AuthUtility.NotIn) {
//				LOG.debug(Boolean.toString(!AuthUtility.in(info.left, info.right, param)));
				return !AuthUtility.in(info.left, info.right, param);
			} else if(info.op == AuthUtility.Equals) {
/*
				LOG.debug(
					info.left,
					param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left)),
					info.right,
					Boolean.toString(AuthUtility.equals(info.left, info.right, param))
				);
*/
				return AuthUtility.equals(info.left, info.right, param);
			} else if(info.op == AuthUtility.NotEquals) {
//				LOG.debug(Boolean.toString(!AuthUtility.equals(info.left, info.right, param)));
				return !AuthUtility.equals(info.left, info.right, param);
			} else if(info.op == AuthUtility.GreaterThan) {
//				LOG.debug(Boolean.toString(AuthUtility.gt(info.left, info.right, param)));
				return AuthUtility.gt(info.left, info.right, param);
			} else if(info.op == AuthUtility.GreaterThanOrEqualTo) {
//				LOG.debug(Boolean.toString(AuthUtility.gteq(info.left, info.right, param)));
				return AuthUtility.gteq(info.left, info.right, param);
			} else if(info.op == AuthUtility.LessThan) {
//				LOG.debug(Boolean.toString(AuthUtility.lt(info.left, info.right, param)));
				return AuthUtility.lt(info.left, info.right, param);
			} else if(info.op == AuthUtility.LessThanOrEqualTo) {
//				LOG.debug(Boolean.toString(AuthUtility.lteq(info.left, info.right, param)));
				return AuthUtility.lteq(info.left, info.right, param);
			}
		}
//		LOG.debug("unknown");
		return true;
	}
	private static boolean equals(String left, String right, Record param) {
		return param.equals(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right);
	}
	private static boolean in(String left, String right, Record param) {
		return param.in(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right);
	}
	private static boolean isEmpty(String left, Record param) {
		return param.isempty(Record.key(Record.PREFIX_TYPE_UNKNOWN, left));
	}
	private static boolean gt(String left, String right, Record param) {
		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.GreaterThan);
	}
	private static boolean gteq(String left, String right, Record param) {
		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.GreaterThanOrEqualTo);
	}
	private static boolean lt(String left, String right, Record param) {
		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.LessThan);
	}
	private static boolean lteq(String left, String right, Record param) {
		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.LessThanOrEqualTo);
	}
/*
	public static void debug(AuthInfo info) {
		if(info != null) {
			LOG.debug(
				info.left,
				Integer.toString(info.op),
				info.right
			);
		}
	}
*/
	public static boolean testInServer(AuthInfo info, Record params) {
		if(info == null) {
//			LOG.debug("info is null");
			return false;
		} else if(info.left == null) {
//			LOG.debug("left is null");
			return false;
		} else {
			if(params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left))) {
//				LOG.debug(info.left + " exists");
				return true;
			}
		}
//		LOG.debug(info.left + " empty");
		return false;
	}
	public static String testExpr(AuthInfo info, Record params, boolean rdf) {
		String test = null;
		String left = null;
		if(info == null) {
			test = "1";
		} else if(info.left == null) {
			test = "1";
		} else {
			if(
				info.left.startsWith("param.") ||
				info.left.startsWith("result.") ||
				info.left.startsWith("prop.") ||
				info.left.startsWith("error.") ||
				info.left.startsWith("query.")
			) {
				left = XPathUtility.valueExpr(info.left, rdf);
			} else if(info.left.startsWith("code.")) {
				throw new ParsingException();
			} else {
				if(AuthUtility.auth(info, params)) {
					test = "1";
				} else {
					test = "0";
				}
			}
			if(test == null) {
				if(info.op == AuthUtility.IsEmpty) {
					test = "not(" + left + ") or " + left + " = ''"; 
				} else if(info.op == AuthUtility.IsNotEmpty) {
					test = "" + left + " and " + left + " != ''"; 
				} else {
					if(info.right == null) {
						test = "0";
					}
				}
				if(info.op == AuthUtility.In || info.op == AuthUtility.NotIn) {
					throw new ParsingException();
				} else if(info.op == AuthUtility.Equals) {
					test = "" + left + " = '" + info.right + "'";
				} else if(info.op == AuthUtility.NotEquals) {
					test = "" + left + " != '" + info.right + "'";
				} else if(info.op == AuthUtility.GreaterThan) {
					test = "" + left + " > " + info.right + "";
				} else if(info.op == AuthUtility.GreaterThanOrEqualTo) {
					test = "" + left + " >= " + info.right + "";
				} else if(info.op == AuthUtility.LessThan) {
					test = "" + left + " < " + info.right + "";
				} else if(info.op == AuthUtility.LessThanOrEqualTo) {
					test = "" + left + " <= " + info.right + "";
				}
			}
		}
		return test;
	}
}
