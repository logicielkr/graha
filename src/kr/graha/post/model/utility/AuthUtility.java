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
import kr.graha.helper.STR;

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
	
	protected static int TYPE_OF_RECORD = 91;
	protected static int TYPE_OF_LITERAL = 92;
	
	private AuthUtility() {
	}
	public static AuthInfo parse(String auth) {
		String right = null;
		String left = null;
		int rightType = 0;
		int leftType = 0;
		String op = null;
		if(auth == null || auth.trim().equals("")) {
			return null;
		}
		auth = auth.trim();
		for(int i = 0; i < auth.length(); i++) {
			if(auth.charAt(i) == '\'') {
				if(right == null) {
					right = auth.substring(i + 1, auth.indexOf("'", i + 1));
					rightType = AuthUtility.TYPE_OF_LITERAL;
				} else {
					left = auth.substring(i + 1, auth.indexOf("'", i + 1));
					leftType = AuthUtility.TYPE_OF_LITERAL;
				}
				i = auth.indexOf("'", i + 1);
			} else if(auth.charAt(i) == '$' && auth.charAt(i + 1) == '{') {
				if(left == null) {
					left = auth.substring(i + 2, auth.indexOf("}", i));
					leftType = AuthUtility.TYPE_OF_RECORD;
				} else {
					right = auth.substring(i + 2, auth.indexOf("}", i));
					rightType = AuthUtility.TYPE_OF_RECORD;
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
		info.leftType = leftType;
		info.rightType = rightType;
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
			return true;
		}
		if(info.op == AuthUtility.IsEmpty) {
			return AuthUtility.isEmpty(info, param);
		} else if(info.op == AuthUtility.IsNotEmpty) {
			return !AuthUtility.isEmpty(info, param);
		} else {
			if(info.right == null) {
				return false;
			}
			if(info.op == AuthUtility.In) {
				return AuthUtility.in(info, param);
			} else if(info.op == AuthUtility.NotIn) {
				return !AuthUtility.in(info, param);
			} else if(info.op == AuthUtility.Equals) {
				return AuthUtility.equals(info, param);
			} else if(info.op == AuthUtility.NotEquals) {
				return !AuthUtility.equals(info, param);
			} else if(info.op == AuthUtility.GreaterThan) {
				return AuthUtility.gt(info, param);
			} else if(info.op == AuthUtility.GreaterThanOrEqualTo) {
				return AuthUtility.gteq(info, param);
			} else if(info.op == AuthUtility.LessThan) {
				return AuthUtility.lt(info, param);
			} else if(info.op == AuthUtility.LessThanOrEqualTo) {
				return AuthUtility.lteq(info, param);
			}
		}
		return true;
	}
	private static boolean isEmpty(AuthInfo info, Record param) {
		if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.isempty(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left));
		} else {
			return !STR.valid(info.left);
		}
	}
	private static boolean in(AuthInfo info, Record param) {
		String right = info.right;
		if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			right = param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right));
		}
		if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.in(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), right);
		} else {
			return STR.compare(info.left, right);
		}
	}
	private static boolean equals(AuthInfo info, Record param) {
		String right = info.right;
		if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			right = param.getString(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right));
		}
		if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.equals(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), right);
		} else {
			return STR.compare(info.left, right);
		}
	}
	private static boolean gt(AuthInfo info, Record param) {
		if(
			info.rightType == AuthUtility.TYPE_OF_RECORD &&
			info.leftType == AuthUtility.TYPE_OF_RECORD
		) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.GreaterThan);
		} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(info.left, Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.GreaterThan);
		} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), info.right, AuthUtility.GreaterThan);
		} else {
			return param.check(info.left, info.right, AuthUtility.GreaterThan);
		}
	}
	private static boolean gteq(AuthInfo info, Record param) {
		if(
			info.rightType == AuthUtility.TYPE_OF_RECORD &&
			info.leftType == AuthUtility.TYPE_OF_RECORD
		) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.GreaterThanOrEqualTo);
		} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(info.left, Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.GreaterThanOrEqualTo);
		} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), info.right, AuthUtility.GreaterThanOrEqualTo);
		} else {
			return param.check(info.left, info.right, AuthUtility.GreaterThanOrEqualTo);
		}
//		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.GreaterThanOrEqualTo);
	}
	private static boolean lt(AuthInfo info, Record param) {
		if(
			info.rightType == AuthUtility.TYPE_OF_RECORD &&
			info.leftType == AuthUtility.TYPE_OF_RECORD
		) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.LessThan);
		} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(info.left, Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.LessThan);
		} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), info.right, AuthUtility.LessThan);
		} else {
			return param.check(info.left, info.right, AuthUtility.LessThan);
		}
//		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.LessThan);
	}
	private static boolean lteq(AuthInfo info, Record param) {
		if(
			info.rightType == AuthUtility.TYPE_OF_RECORD &&
			info.leftType == AuthUtility.TYPE_OF_RECORD
		) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.LessThanOrEqualTo);
		} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(info.left, Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), AuthUtility.LessThanOrEqualTo);
		} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
			return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), info.right, AuthUtility.LessThanOrEqualTo);
		} else {
			return param.check(info.left, info.right, AuthUtility.LessThanOrEqualTo);
		}
//		return param.check(Record.key(Record.PREFIX_TYPE_UNKNOWN, left), right, AuthUtility.LessThanOrEqualTo);
	}
	public static boolean testAtServer(AuthInfo info, Record params) {
//		return AuthUtility.testInServer(info, params, false);
		return AuthUtility.testInServer(info, params, false);
	}
	public static boolean testInServer(AuthInfo info, Record params) {
		return AuthUtility.testInServer(info, params, true);
	}
	private static boolean testInServer(String part, int type, Record params, Boolean includeQuery) {
		if(type == AuthUtility.TYPE_OF_RECORD) {
			if(STR.startsWithIgnoreCase(part, "param.")) {
			} else if(STR.startsWithIgnoreCase(part, "result.")) {
			} else if(STR.startsWithIgnoreCase(part, "prop.")) {
			} else if(STR.startsWithIgnoreCase(part, "error.")) {
			} else if(STR.startsWithIgnoreCase(part, "query.")) {	
			} else if(STR.startsWithIgnoreCase(part, "query.row.")) {
			} else if(STR.startsWithIgnoreCase(part, "code.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "system.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "_system.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "messages.code.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "message.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "generate.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "default.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "sequence.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "generator.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "header.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "session.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "att.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "init-param.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "context-param.")) {
				return true;
			} else if(STR.startsWithIgnoreCase(part, "uuid.")) {
				return true;
			} else {
				return true;
			}
			return params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, part), includeQuery);
		} else {
			return true;
		}
	}
	private static boolean testInServer(AuthInfo info, Record params, Boolean includeQuery) {
		if(info == null) {
			return false;
		} else if(info.left == null) {
			return false;
		} else {
			if(AuthUtility.testInServer(info.left, info.leftType, params, includeQuery) && AuthUtility.testInServer(info.right, info.rightType, params, includeQuery)) {
				return true;
			}
			/*
			if(
				info.rightType == AuthUtility.TYPE_OF_RECORD &&
				info.leftType == AuthUtility.TYPE_OF_RECORD
			) {
				if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), includeQuery) &&
					params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), includeQuery)
				) {
					return true;
				}
			} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
				if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.right), includeQuery)
				) {
					return true;
				}
			} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
				if(
					params.hasKey(Record.key(Record.PREFIX_TYPE_UNKNOWN, info.left), includeQuery)
				) {
					return true;
				}
			} else {
				return true;
			}
			*/
		}
		return false;
	}
	public static String testExpr(AuthInfo info, Record params, boolean rdf) {
		return AuthUtility.testExpr(info, params, rdf, true);
	}
	public static String testExpr(AuthInfo info, Record params, boolean rdf, boolean full) {
		String test = null;
		String left = null;
		String right = null;
		if(info == null) {
			test = "1";
		} else if(info.left == null) {
			test = "1";
		} else {
			if(
				info.rightType == AuthUtility.TYPE_OF_LITERAL &&
				info.leftType == AuthUtility.TYPE_OF_LITERAL
			) {
				if(AuthUtility.auth(info, params)) {
					test = "1";
				} else {
					test = "0";
				}
				return test;
			}
			if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
				if(
					info.left.startsWith("param.") ||
					info.left.startsWith("result.") ||
					info.left.startsWith("prop.") ||
					info.left.startsWith("error.") ||
					info.left.startsWith("query.")
				) {
					left = XPathUtility.valueExpr(info.left, rdf, full);
				} else if(info.left.startsWith("code.")) {
					throw new ParsingException("auth(cond) expression(" + info.left + ") not allow starts with code.");
				} else {
					throw new ParsingException("auth(cond) expression(" + info.left + ") must be starts with param. or result. or prop. or error. or query.");
				}
			} else {
				left = info.left;
			}
			if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
				if(
					info.right.startsWith("param.") ||
					info.right.startsWith("result.") ||
					info.right.startsWith("prop.") ||
					info.right.startsWith("error.") ||
					info.right.startsWith("query.")
				) {
					right = XPathUtility.valueExpr(info.right, rdf, full);
				} else if(info.right.startsWith("code.")) {
					throw new ParsingException("auth(cond) expression(" + info.right + ") not allow starts with code.");
				} else {
					throw new ParsingException("auth(cond) expression(" + info.right + ") must be starts with param. or result. or prop. or error. or query.");
				}
			} else {
				right = info.right;
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
					throw new ParsingException("auth(cond) expression not allow in and not in");
				} else if(info.op == AuthUtility.Equals) {
					if(
						info.rightType == AuthUtility.TYPE_OF_RECORD &&
						info.leftType == AuthUtility.TYPE_OF_RECORD
					) {
						test = "" + left + " = " + right + "";
					} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
						test = "" + left + " = '" + right + "'";
					} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
						test = "'" + left + "' = " + right + "";
					} 
				} else if(info.op == AuthUtility.NotEquals) {
					if(
						info.rightType == AuthUtility.TYPE_OF_RECORD &&
						info.leftType == AuthUtility.TYPE_OF_RECORD
					) {
						test = "" + left + " != " + right + "";
					} else if(info.leftType == AuthUtility.TYPE_OF_RECORD) {
						test = "" + left + " != '" + right + "'";
					} else if(info.rightType == AuthUtility.TYPE_OF_RECORD) {
						test = "'" + left + "' != " + right + "";
					}
//					test = "" + left + " != '" + right + "'";
				} else if(info.op == AuthUtility.GreaterThan) {
					test = "" + left + " > " + right + "";
				} else if(info.op == AuthUtility.GreaterThanOrEqualTo) {
					test = "" + left + " >= " + right + "";
				} else if(info.op == AuthUtility.LessThan) {
					test = "" + left + " < " + right + "";
				} else if(info.op == AuthUtility.LessThanOrEqualTo) {
					test = "" + left + " <= " + right + "";
				}
			}
		}
		return test;
	}
}
