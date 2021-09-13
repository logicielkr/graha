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

/**
 * Graha(그라하) 수식실행기
 * @author HeonJik, KIM (https://graha.kr)
 * @version 0.5
 * @since 0.5
 */

function GrahaFormula() {
}
GrahaFormula.FORM_NAME = null;
GrahaFormula.INDEX = null;
GrahaFormula.Oper = function(value) {
	this.value = value;
};
GrahaFormula.Oper.prototype.valueOf = function() {
	return this.value;
};
GrahaFormula.Const = function(value, type) {
	this.value = value;
	if(type) {
		this.type = type;
	}
};
GrahaFormula.Const.prototype.valueOf = function() {
	if(typeof(this.value) == "number") {
		return this.value;
	} else if(
		this.value != null && this.value.length >= 2 &&
		(
			(this.value.charAt(0) == "\"" && this.value.charAt(this.value.length - 1) == "\"")
			|| (this.value.charAt(0) == "'" && this.value.charAt(this.value.length - 1) == "'")
		)
	) {
		if(this.value.length > 1) {
			return this.value.substring(1, this.value.length - 1)
		} else {
			throw new Error(this.value.substring(0,1) + " literal not terminated before end of script");
		}
	} else if(this.type && this.type == "string") {
		return this.value;
	} else if(isNaN(this.value)) {
		return NaN;
	} else {
		return GrahaFormula.Func.udf.number(this.value);
	}
};
GrahaFormula.Const.prototype.typeOf = function() {
	if(
		this.value != null && this.value.length >= 2 &&
		(
			(this.value.charAt(0) == "\"" && this.value.charAt(this.value.length - 1) == "\"")
			|| (this.value.charAt(0) == "'" && this.value.charAt(this.value.length - 1) == "'")
		)
	) {
		return "string";
	} else if(this.type && this.type == "string") {
		return "string";
	} else if(isNaN(this.value)) {
		return NaN;
	} else {
		return "number";
	}
};
GrahaFormula.Val = function(value, index) {
	this.value = value;
	if(arguments.length) {
		this.index = index;
	}
};
GrahaFormula.Val.prototype.valueOf = function() {
	var name = this.value;
	if(
		name.length > 3 &&
		name.lastIndexOf("[N]") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("[N]")))
	) {
		name = name.substring(0, name.lastIndexOf("[N]"));
	} else if(
		name.length > 3 &&
		name.lastIndexOf("{N}") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("{N}")) + GrahaFormula.INDEX)
	) {
		name = name.substring(0, name.lastIndexOf("{N}")) + GrahaFormula.INDEX;
	}
	var result = GrahaFormula.Val._get(name);
	if(result == null) {
		return NaN;
	} else if(result.length && result.length > 1) {
		if(this.index != null && this.index >= 0 && result.length > this.index) {
			return result[this.index].value;
		} else if(GrahaFormula.INDEX != null && GrahaFormula.INDEX >= 0 && result.length > GrahaFormula.INDEX) {
			return result[GrahaFormula.INDEX].value;
		} else {
			return NaN;
		}
	} else if(result.length && result.length > 0) {
		return result[0].value;
	} else {
		return result.value;
	}
};
GrahaFormula.Val.prototype.extract = function() {
	var result = GrahaFormula.Val._extract(this.value);
	if(result instanceof Array) {
		var arr = new Array();
		for(var i = 0; i < result.length; i++) {
			var data = GrahaFormula.Val._get(result[i]);
			if(data == null) {
			} else if(data.length && data.length > 1) {
				for(var x = 0; x < data.length; x++) {
					arr.push(new GrahaFormula.Val(result[i], x));
				}
			} else {
				arr.push(new GrahaFormula.Val(result[i]));
			}
		}
		return arr;
	} else {
		return this;
	}
};
GrahaFormula.Val._typeof = function(name) {
	var result = GrahaFormula.Val._get(name);
	if(result == null) {
		return null;
	} else if(result.length && result.length > 1) {
		return "array";
	} else {
		return "single";
	}
};
GrahaFormula.Val._has = function(name) {
	var result = GrahaFormula.Val._typeof(name);
	if(result == null) {
		return false;
	} else {
		return true;
	}
};
GrahaFormula.Val._get = function(name) {
	var jquery = false;
	var selector = false;
	var legacy = false;
	if(typeof($) == "function") {
		jquery = true;
	} else if(document.querySelector && document.querySelectorAll) {
		selector = true;
	} else {
		legacy = true;
	}
	var formName = null;
	var elementName = null;
	if(name.indexOf("#") > 0) {
		formName = name.substring(0, name.indexOf("#"));
		if(formName != null && formName.trim() != "") {
			if(
				(jquery && $("form[name='" + formName + "']").length > 0) ||
				(selector && document.querySelectorAll("form[name='" + formName + "']").length > 0) ||
				(legacy && document.forms[formName])
			) {
				elementName = name.substring(name.indexOf("#") + 1);
			} else {
				formName = null;
			}
		} else {
			formName = null;
		}
	}
	if(formName == null && GrahaFormula.FORM_NAME != null) {
		formName = GrahaFormula.FORM_NAME;
	}
	if(formName == null) {
		return null;
	}
	if(elementName == null) {
		elementName = name;
	}
	if(jquery) {
		if($("form[name='" + formName + "'] [name='" + elementName.replace(/'/g, "\\'").replace(/\./g, "\\.") + "']").length == 0) {
			return null;
		} else {
			return $("form[name='" + formName + "'] [name='" + elementName.replace(/'/g, "\\'").replace(/\./g, "\\.") + "']");
		}
	} else if(selector) {
		if(document.querySelectorAll("form[name='" + formName + "'] [name='" + elementName.replace(/'/g, "\\'").replace(/\./g, "\\.") + "']").length == 0) {
			return null;
		} else {
			return document.querySelectorAll("form[name='" + formName + "'] [name='" + elementName.replace(/'/g, "\\'").replace(/\./g, "\\.") + "']");
		}
	} else {
		var result = document.forms[formName].elements[elementName];
		if(result) {
			if(
				(result.length && result.length > 1) ||
				(result.name && result.name == elementName)
			) {
				return document.forms[formName].elements[elementName];
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}
};
GrahaFormula.Val._extract = function(name) {
	if(GrahaFormula.Val._isExtract(name)) {
		var result = new Array();
		if(
			name.length >= 2 &&
			name.charAt(name.length - 1) == "." &&
			GrahaFormula.Val._has(name + "1")
		) {
			var index = 1;
			while(true) {
				if(GrahaFormula.Val._has(name + index)) {
					result.push(name + index);
				} else {
					break;
				}
				index++;
			}
			return result;
		} else if(
			name.indexOf(":") > 1 &&
			name.indexOf(":") == name.lastIndexOf(":")
		) {
			var left = name.substring(0, name.indexOf(":"));
			var right = name.substring(name.lastIndexOf(":") + 1);
			if(left == null || right == null || left == "" || right == "") {
				throw new Error("syntax error (" + name + ")");
			} else if(
				GrahaFormula.Val._has(left) &&
				GrahaFormula.Val._has(right)
			) {
				if(
					left.lastIndexOf(".") > 0 &&
					right.lastIndexOf(".") > 0 &&
					left.substring(0, left.lastIndexOf(".")) == right.substring(0, right.lastIndexOf(".")) &&
					!isNaN(left.substring(left.lastIndexOf(".") + 1)) &&
					!isNaN(right.substring(right.lastIndexOf(".") + 1))
				) {
					var min = Math.min(
						parseInt(left.substring(left.lastIndexOf(".") + 1)),
						parseInt(right.substring(right.lastIndexOf(".") + 1))
					);
					var max = Math.max(
						parseInt(left.substring(left.lastIndexOf(".") + 1)),
						parseInt(right.substring(right.lastIndexOf(".") + 1))
					);
					for(var index = min; index <= max; index++) {
						if(GrahaFormula.Val._has(left.substring(0, left.lastIndexOf(".") + 1) + index)) {
							result.push(left.substring(0, left.lastIndexOf(".") + 1) + index);
						} else {
							throw new Error("syntax error (" + name + ")");
						}
					}
					return result;
				} else {
					result.push(left);
					result.push(right);
					return result;
				}
			} else {
				throw new Error("syntax error (" + name + ")");
			}
		} else {
			result.push(name);
			return result;
		}
	} else if(
		name.length > 3 &&
		name.lastIndexOf("{N}") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("{N}")) + GrahaFormula.INDEX)
	) {
		result.push(name.substring(0, name.lastIndexOf("{N}")) + GrahaFormula.INDEX);
		return result;
	} else if(
		name.length > 3 &&
		name.lastIndexOf("[N]") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("[N]")) + GrahaFormula.INDEX)
	) {
		result.push(name);
		return result;
	} else {
		return name;
	}
};
GrahaFormula.Val.prototype.isExtract = function() {
	return GrahaFormula.Val._isExtract(this.value);
};
GrahaFormula.Val._isExtract = function(name) {
	if(
		name.length >= 2 &&
		name.charAt(name.length - 1) == "." &&
		GrahaFormula.Val._has(name + "1")
	) {
		return true;
	} else if(
		name.indexOf(":") > 1 &&
		name.indexOf(":") == name.lastIndexOf(":")
	) {
		var left = name.substring(0, name.indexOf(":"));
		var right = name.substring(name.lastIndexOf(":") + 1);
		if(left == null || right == null || left == "" || right == "") {
			return false;
		} else if(
			GrahaFormula.Val._has(left) &&
			GrahaFormula.Val._has(right)
		) {
			return true;
		} else {
			return false;
		}
	} else if(
		name.length > 3 &&
		name.lastIndexOf("{N}") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("{N}")) + GrahaFormula.INDEX)
	) {
		return true;
	} else if(
		name.length > 3 &&
		name.lastIndexOf("[N]") == (name.length - 3) &&
		GrahaFormula.INDEX != null &&
		GrahaFormula.Val._has(name.substring(0, name.lastIndexOf("[N]")))
	) {
		return true;
	} else if(GrahaFormula.Val._typeof(name) == "array") {
		return true;
	}
	return false;
};
GrahaFormula.Val.contains = function(name) {
	if(name == null || name == "") {
		return false;
	} else if(
		name != null && name.length >= 2 &&
		(
			(name.charAt(0) == "\"" && name.charAt(name.length - 1) == "\"")
			|| (name.charAt(0) == "'" && name.charAt(name.length - 1) == "'")
		)
	) {
		return false;
	} else if(GrahaFormula.Val._has(name)) {
		return true;
	} else if(GrahaFormula.Val._isExtract(name)) {
		return true;
	}
	return false;
};
GrahaFormula.Func = function(value) {
	this.value = value;
	this.element = new Array();
};
GrahaFormula.Func.prototype.valueOf = function() {
	var args = new Array();
	var last = 0;
	for(var i = 0; i < this.element.length; i++) {
		if(this.element[i] == ",") {
			if(last == (i - 1)) {
				args.push(this.element[last]);
			} else {
				args.push(new GrahaFormula.Expr(this.element.slice(last, i)));
			}
			last = i + 1;
		}
	}
	if(last < this.element.length) {

		if(last == (this.element.length - 1)) {
			args.push(this.element[last]);
		} else {
			args.push(new GrahaFormula.Expr(this.element.slice(last)));
		}
	}
	return GrahaFormula.Func.udf[this.value].call(this, args);
};
GrahaFormula.Func.prototype.append = function(expr) {
	this.element.push(expr);
};
GrahaFormula.Func.udf = function() {
};
GrahaFormula.Func.udf.trim = function(data) {
	var result = GrahaFormula.Func.udf.string(data);
	if(result == null) {
		return null;
	} else {
		return result.trim();
	}
};
GrahaFormula.Func.udf.upper = function(data) {
	var result = GrahaFormula.Func.udf.string(data);
	if(result == null) {
		return null;
	} else {
		return result.toUpperCase();
	}
};
GrahaFormula.Func.udf.lower = function(data) {
	var result = GrahaFormula.Func.udf.string(data);
	if(result == null) {
		return null;
	} else {
		return result.toLowerCase();
	}
};
GrahaFormula.Func.udf.typeof = function(data) {
	if(
		data instanceof GrahaFormula.Const
		|| data instanceof GrahaFormula.Func
		|| data instanceof GrahaFormula.Expr
	) {
		var result = data.valueOf();
		if(typeof(result) == "number") {
			return "number";
		} else if(typeof(result) == "string") {
			if(new RegExp(/^-?[0-9.,]+$/).test(result.trim())) {
				return "float";
			} else if(new RegExp(/^-?[0-9,]+$/).test(result.trim())) {
				return "int";
			} else {
				return "string";
			}
		}
	} else if(data instanceof GrahaFormula.Val) {
		var reult;
		if(data.isExtract()) {
			var arr = data.extract();
			if(arr instanceof Array && arr.length > 0) {
				result = arr[0].valueOf();
			} else {
				result = data.valueOf();
			}
		} else {
			result = data.valueOf();
		}
		if(typeof(result) == "number") {
			return "number";
		} else if(typeof(result) == "string") {
			if(new RegExp(/^-?[0-9.,]+$/).test(result.trim())) {
				return "float";
			} else if(new RegExp(/^-?[0-9,]+$/).test(result.trim())) {
				return "int";
			} else {
				return "string";
			}
		}
	} else if(typeof(data) == "number") {
		return "number";
	} else if(typeof(data) == "string") {
		if(new RegExp(/^-?[0-9.,]+$/).test(data.trim())) {
			return "float";
		} else if(new RegExp(/^-?[0-9,]+$/).test(data.trim())) {
			return "int";
		} else {
			return "string";
		}
	} else if(data.length) {
		if(
			data[0] instanceof GrahaFormula.Const
			|| data[0] instanceof GrahaFormula.Func
			|| data[0] instanceof GrahaFormula.Expr
		) {
			result = data[0].valueOf();
			if(typeof(result) == "number") {
				return "number";
			} else if(typeof(result) == "string") {
				if(new RegExp(/^-?[0-9.,]+$/).test(result.trim())) {
					return "float";
				} else if(new RegExp(/^-?[0-9,]+$/).test(result.trim())) {
					return "int";
				} else {
					return "string";
				}
			}
		} else if(data[0] instanceof GrahaFormula.Val) {
			var reult;
			if(data[0].isExtract()) {
				var arr = data[0].extract();
				if(arr instanceof Array && arr.length > 0) {
					result = arr[0].valueOf();
				} else {
					result = data[0].valueOf();
				}
			} else {
				result = data[0].valueOf();
			}
			if(typeof(result) == "number") {
				return "number";
			} else if(typeof(result) == "string") {
				if(new RegExp(/^-?[0-9.,]+$/).test(result.trim())) {
					return "float";
				} else if(new RegExp(/^-?[0-9,]+$/).test(result.trim())) {
					return "int";
				} else {
					return "string";
				}
			}
		} else if(typeof(data[0]) == "number") {
			return "number";
		} else if(typeof(data[0]) == "string") {
			if(new RegExp(/^-?[0-9.,]+$/).test(data[0].trim())) {
				return "float";
			} else if(new RegExp(/^-?[0-9,]+$/).test(data[0].trim())) {
				return "int";
			} else {
				return "string";
			}
		} else {
			return null;
		}
	} else {
		return null;
	}
};
GrahaFormula.Func.udf._int = function(data) {
	if(new RegExp(/^-?[0-9,]+$/).test(data.trim())) {
		return parseInt(data.trim().replace(/,/g, ""));
	} else {
		return NaN;
	}
};
GrahaFormula.Func.udf._float = function(data) {
	if(new RegExp(/^-?[0-9.,]+$/).test(data.trim())) {
		return parseFloat(data.trim().replace(/,/g, ""));
	} else {
		return NaN;
	}
};
GrahaFormula.Func.udf.comma = function(data) {
	var value = GrahaFormula.Func.udf.number(data);
	if(value == null) {
		return null;
	} else if(isNaN(value)) {
		return NaN;
	} else {
		return value.toLocaleString();
	}
};
GrahaFormula.Func.udf.nvl = function(data) {
	if(data.length == 1) {
		var value = GrahaFormula.Func.udf.number(data[0]);
		return value;
	} else if(data.length > 1) {
		var value = GrahaFormula.Func.udf.number(data[0]);
		var defaultValue = GrahaFormula.Func.udf.number(data[1]);
		if(value == null || value == "") {
			return defaultValue;
		} else if(isNaN(value)) {
			return defaultValue;
		} else {
			return value;
		}
	} else {
		return NaN;
	}
};
GrahaFormula.Func.udf.ceil = function(data) {
	var value = GrahaFormula.Func.udf.number(data);
	if(value == null) {
		return NaN;
	} else if(isNaN(value)) {
		return NaN;
	} else {
		return Math.ceil(value);
	}
};
GrahaFormula.Func.udf.round = function(data) {
	var value = GrahaFormula.Func.udf.number(data);
	if(value == null) {
		return NaN;
	} else if(isNaN(value)) {
		return NaN;
	} else {
		return Math.round(value);
	}
};
GrahaFormula.Func.udf.floor = function(data) {
	var value = GrahaFormula.Func.udf.number(data);
	if(value == null) {
		return NaN;
	} else if(isNaN(value)) {
		return NaN;
	} else {
		return Math.floor(value);
	}
};
GrahaFormula.Func.udf.abs = function(data) {
	var value = GrahaFormula.Func.udf.number(data);
	if(value == null) {
		return NaN;
	} else if(isNaN(value)) {
		return NaN;
	} else {
		return Math.abs(value);
	}
};
GrahaFormula.Func.udf.number = function(data) {
	if(
		data instanceof GrahaFormula.Const
		|| data instanceof GrahaFormula.Func
		|| data instanceof GrahaFormula.Expr
	) {
		var result = data.valueOf();
		if(typeof(result) == "number") {
			return result;
		} else if(result.indexOf(".") >= 0) {
			return GrahaFormula.Func.udf._float(result);
		} else {
			return GrahaFormula.Func.udf._int(result);
		}
	} else if(data instanceof GrahaFormula.Val) {
		var result;
		if(data.isExtract()) {
			var arr = data.extract();
			if(arr instanceof Array && arr.length > 0) {
				result = arr[0].valueOf().trim();
			} else {
				result = data.valueOf().trim();
			}
		} else {
			result = data.valueOf().trim();
		}
		if(typeof(result) == "number") {
			return result;
		} else if(result.indexOf(".") >= 0) {
			return GrahaFormula.Func.udf._float(result);
		} else {
			return GrahaFormula.Func.udf._int(result);
		}
	} else if(typeof(data) == "number") {
		return data;
	} else if(typeof(data) == "string") {
		if(data.indexOf(".") >= 0) {
			return GrahaFormula.Func.udf._float(data);
		} else {
			return GrahaFormula.Func.udf._int(data);
		}
	} else if(data.length) {
		if(
			data[0] instanceof GrahaFormula.Const
			|| data[0] instanceof GrahaFormula.Func
			|| data[0] instanceof GrahaFormula.Expr
		) {
			var result = data[0].valueOf();
			if(typeof(result) == "number") {
				return result;
			} else if(result.indexOf(".") >= 0) {
				return GrahaFormula.Func.udf._float(result);
			} else {
				return GrahaFormula.Func.udf._int(result);
			}
		} else if(data[0] instanceof GrahaFormula.Val) {
			var result;
			if(data[0].isExtract()) {
				var arr = data[0].extract();
				if(arr instanceof Array && arr.length > 0) {
					result = arr[0].valueOf().trim();
				} else {
					result = data[0].valueOf().trim();
				}
			} else {
				result = data[0].valueOf().trim();
			}
			if(typeof(result) == "number") {
				return result;
			} else if(result.indexOf(".") >= 0) {
				return GrahaFormula.Func.udf._float(result);
			} else {
				return GrahaFormula.Func.udf._int(result);
			}
		} else if(typeof(data[0]) == "number") {
			return data[0];
		} else if(typeof(data[0]) == "string") {
			if(data[0].indexOf(".") >= 0) {
				return GrahaFormula.Func.udf._float(data[0]);
			} else {
				return GrahaFormula.Func.udf._int(data[0]);
			}
		} else {
			return NaN;
		}
	} else {
		return NaN;
	}
};
GrahaFormula.Func.udf.string = function(data) {
	if(
		data instanceof GrahaFormula.Const
		|| data instanceof GrahaFormula.Func
		|| data instanceof GrahaFormula.Expr
	) {
		var result = data.valueOf();
		if(typeof(result) == "string") {
			return result;
		} else if(typeof(result) == "number") {
			return result.toString();
		} else {
			return null;
		}
	} else if(data instanceof GrahaFormula.Val) {
		var result;
		if(data.isExtract()) {
			var arr = data.extract();
			if(arr instanceof Array && arr.length > 0) {
				result = arr[0].valueOf().trim();
			} else {
				result = data.valueOf().trim();
			}
		} else {
			result = data.valueOf().trim();
		}
		if(typeof(result) == "string") {
			return result;
		} else if(typeof(result) == "number") {
			return result.toString();
		} else {
			return null;
		}
	} else if(typeof(data) == "string") {
		return data;
	} else if(typeof(data) == "number") {
		return data.toString();
	} else if(data.length) {
		if(
			data[0] instanceof GrahaFormula.Const
			|| data[0] instanceof GrahaFormula.Func
			|| data[0] instanceof GrahaFormula.Expr
		) {
			var result = data[0].valueOf();
			if(typeof(result) == "string") {
				return result;
			} else if(typeof(result) == "number") {
				return result.toString();
			} else {
				return null;
			}
		} else if(data[0] instanceof GrahaFormula.Val) {
			var result;
			if(data[0].isExtract()) {
				var arr = data[0].extract();
				if(arr instanceof Array && arr.length > 0) {
					result = arr[0].valueOf().trim();
				} else {
					result = data[0].valueOf().trim();
				}
			} else {
				result = data[0].valueOf().trim();
			}
			if(typeof(result) == "string") {
				return result;
			} else if(typeof(result) == "number") {
				return result.toString();
			} else {
				return null;
			}
		} else if(typeof(data[0]) == "string") {
			return data[0];
		} else if(typeof(data[0]) == "number") {
			return data[0].toString();
		} else {
			return null;
		}
	} else {
		return null;
	}
};
GrahaFormula.Func.udf._extract = function(data) {
	var result = new Array();
	for(var i = 0; i < data.length; i++) {
		if(data[i] instanceof GrahaFormula.Val) {
			if(data[i].isExtract()) {
				var arr = data[i].extract();
				if(arr instanceof Array && arr.length > 0) {
					Array.prototype.push.apply(result, arr);
				} else {
					result.push(data[i]);
				}
			}
		} else {
			result.push(data[i]);
			
		}
	}
	return result;
};
GrahaFormula.Func.udf.sum = function(data) {
	var sum = 0;
	var arr = GrahaFormula.Func.udf._extract(data);
	var result;
	for(var i = 0; i < arr.length; i++) {
		result = arr[i].valueOf();
		if(typeof(result) == "number") {
			sum += result;
		} else if(result != null && result != "") {
			sum += GrahaFormula.Func.udf.number(result);
		}
	}
	return sum;
};
GrahaFormula.Func.udf.max = function(data) {
	var max;
	var arr = GrahaFormula.Func.udf._extract(data);
	var result;
	for(var i = 0; i < arr.length; i++) {
		result = arr[i].valueOf();
		if(i == 0) {
			if(typeof(result) == "number") {
				max = result;
			} else if(result != null && result != "") {
				max = GrahaFormula.Func.udf.number(result);
			}
		} else {
			if(typeof(result) == "number") {
				max = Math.max(max, result);
			} else if(result != null && result != "") {
				max = Math.max(max, GrahaFormula.Func.udf.number(result));
			}
		}
	}
	return max;
};
GrahaFormula.Func.udf.min = function(data) {
	var min;
	var arr = GrahaFormula.Func.udf._extract(data);
	var result;
	for(var i = 0; i < arr.length; i++) {
		result = arr[i].valueOf();
		if(i == 0) {
			if(typeof(result) == "number") {
				min = result;
			} else if(result != null && result != "") {
				min = GrahaFormula.Func.udf.number(result);
			}
		} else {
			if(typeof(result) == "number") {
				min = Math.min(min, result);
			} else if(result != null && result != "") {
				min = Math.min(min, GrahaFormula.Func.udf.number(result));
			}
		}
	}
	return min;
};
GrahaFormula.Func.udf.avg = function(data) {
	var sum = 0;
	var count = 0;
	var result;
	var arr = GrahaFormula.Func.udf._extract(data);
	for(var i = 0; i < arr.length; i++) {
		result = arr[i].valueOf();
		if(typeof(result) == "number") {
			sum += result;
			count++;
		} else if(result != null && result != "") {
			sum += GrahaFormula.Func.udf.number(result);
			count++;
		}
	}
	if(count > 0) {
		return sum/count;
	} else {
		return 0;
	}
};
GrahaFormula.Func.udf.count = function(data) {
	var count = 0;
	var arr = GrahaFormula.Func.udf._extract(data);
	var result;
	for(var i = 0; i < arr.length; i++) {
		result = arr[i].valueOf();
		if(result != null && result != "") {
			count++;
		}
	}
	return count;
};
GrahaFormula.Func.udf.plus = function() {
	var first;
	var second;
	if(arguments.length == 1) {
		first = GrahaFormula.Func.udf.number(arguments[0][0]); 
		second = GrahaFormula.Func.udf.number(arguments[0][1]);
	} else if(arguments.length == 2) {
		first = GrahaFormula.Func.udf.number(arguments[0]); 
		second = GrahaFormula.Func.udf.number(arguments[1]);
	} else {
		return NaN;
	}
	return first + second;
};
GrahaFormula.Func.udf.minus = function() {
	var first;
	var second;
	if(arguments.length == 1) {
		first = GrahaFormula.Func.udf.number(arguments[0][0]); 
		second = GrahaFormula.Func.udf.number(arguments[0][1]);
	} else if(arguments.length == 2) {
		first = GrahaFormula.Func.udf.number(arguments[0]); 
		second = GrahaFormula.Func.udf.number(arguments[1]);
	} else {
		return NaN;
	}
	return first - second;
};
GrahaFormula.Func.udf.concat = function() {
	if(arguments.length == 1) {
		return GrahaFormula.Func.udf.string(arguments[0][0]) + GrahaFormula.Func.udf.string(arguments[0][1]);
	} else if(arguments.length == 2) {
		return GrahaFormula.Func.udf.string(arguments[0]) + GrahaFormula.Func.udf.string(arguments[1]);
	} else {
		return NaN;
	}
};
GrahaFormula.Func.udf.multiplication = function() {
	var first;
	var second;
	if(arguments.length == 1) {
		first = GrahaFormula.Func.udf.number(arguments[0][0]); 
		second = GrahaFormula.Func.udf.number(arguments[0][1]);
	} else if(arguments.length == 2) {
		first = GrahaFormula.Func.udf.number(arguments[0]); 
		second = GrahaFormula.Func.udf.number(arguments[1]);
	} else {
		return NaN;
	}
	return first * second;
};
GrahaFormula.Func.udf.division = function() {
	if(arguments.length == 1) {
		if(GrahaFormula.Func.udf.number(arguments[0][1]) == 0) {
			return Infinity;
		} else {
			return GrahaFormula.Func.udf.number(arguments[0][0]) / GrahaFormula.Func.udf.number(arguments[0][1]);
		}
	} else if(arguments.length == 2) {
		if(GrahaFormula.Func.udf.number(arguments[1]) == 0) {
			return Infinity;
		} else {
			return GrahaFormula.Func.udf.number(arguments[0]) / GrahaFormula.Func.udf.number(arguments[1]);
		}
	} else {
		return NaN;
	}
};
GrahaFormula.Func.contains = function(name) {
	if(name == null || name == "") {
		return false;
	} else if(GrahaFormula.Func.udf[name]) {
		return true;
	} else {
		return false;
	}
};
GrahaFormula.Expr = function() {
	if(arguments.length == 0) {
		this.element = new Array();
	} else {
		this.element = arguments[0];
	}
};
GrahaFormula.Expr.typeof = function(name) {
	if(GrahaFormula.Func.contains(name)) {
		return "function";
	} else if(GrahaFormula.Val.contains(name)) {
		return "val";
	} else if(name != "") {
		return "const";
	} else {
		return "undifined";
	}
};
GrahaFormula.Expr.prototype.append = function(expr) {
	this.element.push(expr);
};
GrahaFormula.Expr.prototype.prevIndex = function(z, el) {
	if(arguments.length > 1) {
		if(z > 0) {
			for(var i = (z - 1); i >= 0; i--) {
				if(el != null) {
					return i;
				}
			}
		}
	} else {
		if(z > 0) {
			for(var i = (z - 1); i >= 0; i--) {
				if(this.element[i] != null) {
					return i;
				}
			}
		}
	}
	return NaN;
};
GrahaFormula.Expr.prototype.nextIndex = function(z, el) {
	if(arguments.length > 1) {
		if(z < this.element.length - 1) {
			for(var i = (z + 1); i < el.length; i++) {
				if(el[i] != null) {
					return i;
				}
			}
		}
	} else {
		if(z < el.length - 1) {
			for(var i = (z + 1); i < this.element.length; i++) {
				if(this.element[i] != null) {
					return i;
				}
			}
		}
	}
	return NaN;
};
GrahaFormula.Expr.prototype.valueOf = function() {
	if(this.element.length == 1) {
		return this.element[0].valueOf();
	}
	var clone = this.element.slice();
	for(var z = 0; z < clone.length; z++) {
		var a = clone[z];
		if(a instanceof GrahaFormula.Oper && a == "%") {
			var prevIndex = this.prevIndex(z, clone);
			if(isNaN(prevIndex)) {
				throw new Error("unknown syntax");
			} else {
				var prev = clone[prevIndex].valueOf();
				clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.division(prev, 100));
				clone[z] = null;
			}
		} else if(a instanceof GrahaFormula.Oper && (a == "*" || a == "/")) {
			var prevIndex = this.prevIndex(z, clone);
			var nextIndex = this.nextIndex(z, clone);
			if(isNaN(prevIndex) || isNaN(nextIndex)) {
				throw new Error("unknown syntax");
			} else {
				var prev = clone[prevIndex].valueOf();
				var next = clone[nextIndex].valueOf();
				clone[prevIndex] = null;
				clone[z] = null;
				if(a == "*") {
					clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.multiplication(prev, next));
				} else if(a == "/") {
					clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.division(prev, next));
				}
				z = nextIndex;
			}
		}
	}
	for(var z = 0; z < clone.length; z++) {
		var a = clone[z];
		if(a instanceof GrahaFormula.Oper && (a == "+" || a == "-")) {
			var prevIndex = this.prevIndex(z, clone);
			var nextIndex = this.nextIndex(z, clone);
			if(isNaN(prevIndex) || isNaN(nextIndex)) {
				throw new Error("unknown syntax");
			} else {
				var prev = clone[prevIndex].valueOf();
				var next = clone[nextIndex].valueOf();
				
				clone[prevIndex] = null;
				clone[z] = null;
				if(a == "+") {
					clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.plus(prev, next));
				} else if(a == "-") {
					clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.minus(prev, next));
				}
				z = nextIndex;
			}
		}
	}
	for(var z = 0; z < clone.length; z++) {
		var a = clone[z];
		if(a instanceof GrahaFormula.Oper && a == "&") {
			var prevIndex = this.prevIndex(z, clone);
			var nextIndex = this.nextIndex(z, clone);
			if(isNaN(prevIndex) || isNaN(nextIndex)) {
				throw new Error("unknown syntax");
			} else {
				var prev = clone[prevIndex].valueOf();
				var next = clone[nextIndex].valueOf();
				clone[prevIndex] = null;
				clone[z] = null;
				clone[nextIndex] = new GrahaFormula.Const(GrahaFormula.Func.udf.concat(prev, next), "string");
				z = nextIndex;
			}
		}
	}
	for(var i = (clone.length - 1); i >= 0; i--) {
		if(clone[i] != null) {
			return clone[i].valueOf();
		}
	}
	return NaN;
};
GrahaFormula.parseFormula = function(formula) {
	var isQ = false;
	var qT = null;
	var t = "";
	var result = new Array();
	var hist = new Array();
	var refer = new Array();
	result.push(new GrahaFormula.Expr());
	var current = 0;
	for(var z in formula) {
		var x = formula[z];
		if(x == "\"" && !isQ && qT == null) {
			qT = "\"";
			isQ = !isQ;
			t += x;
		} else if(x == "\"" && isQ && qT == "\"") {
			if(
				t.substring(t.length-1) != "\\"
				|| (t.substring(t.length-1) == "\\" && t.substring(t.length-2) == "\\")
			) {
				qT = null;
				isQ = !isQ;
			}
			t += x;
		} else if(x == "'" && !isQ && qT == null) {
			qT = "'";
			isQ = !isQ;
			t += x;
		} else if(x == "'" && isQ && qT == "'") {
			if(
				t.substring(t.length-1) != "\\"
				|| (t.substring(t.length-1) == "\\" && t.substring(t.length-2) == "\\")
			) {
				qT = null;
				isQ = !isQ;
			}
			t += x;
		} else if(
			!isQ && (
				x == "+"
				|| x == "-"
				|| x == "*"
				|| x == "/"
				|| x == "%"
				|| x == "&"
				|| x == ")"
				|| x == "("
				|| x == ","
			)
		) {
			if(x == "(") {
				if(GrahaFormula.Expr.typeof(t.trim()) == "function") {
					hist.push(current);
					result.push(new GrahaFormula.Func(t.trim()));
					current = result.length - 1;
				} else {
					if(GrahaFormula.Expr.typeof(t.trim()) == "val") {
						result[current].append(new GrahaFormula.Val(t.trim()));
						if(GrahaFormula.Val._isExtract(t.trim())) {
							Array.prototype.push.apply(refer, GrahaFormula.Val._extract(t.trim()));
						} else {
							refer.push(t.trim());
						}
					} else if(GrahaFormula.Expr.typeof(t.trim()) == "const") {
						result[current].append(new GrahaFormula.Const(t.trim()));
					}
					hist.push(current);
					result.push(new GrahaFormula.Expr());
					current = result.length - 1;
				}
			} else if(x == ")") {
				if(GrahaFormula.Expr.typeof(t.trim()) == "val") {
					result[current].append(new GrahaFormula.Val(t.trim()));
					if(GrahaFormula.Val._isExtract(t.trim())) {
						Array.prototype.push.apply(refer, GrahaFormula.Val._extract(t.trim()));
					} else {
						refer.push(t.trim());
					}
				} else if(GrahaFormula.Expr.typeof(t.trim()) == "const") {
					result[current].append(new GrahaFormula.Const(t.trim()));
				}
				
				hist.pop();
				if(result[result.length - 2]) {
					result[result.length - 2].append(result[current]);
					result.pop();
					current = result.length - 1;
				} else {
					throw new Error("syntax error )");
				}
			} else {
				if(t.trim() != "") {
					if(GrahaFormula.Expr.typeof(t.trim()) == "val") {
						result[current].append(new GrahaFormula.Val(t.trim()));
						if(GrahaFormula.Val._isExtract(t.trim())) {
							Array.prototype.push.apply(refer, GrahaFormula.Val._extract(t.trim()));
						} else {
							refer.push(t.trim());
						}
					} else {
						result[current].append(new GrahaFormula.Const(t.trim()));
					}
				}
				result[current].append(new GrahaFormula.Oper(x));
			}
			t = "";
		} else {
			t += x;
		}
	}
	if(GrahaFormula.Expr.typeof(t.trim()) == "val") {
		result[current].append(new GrahaFormula.Val(t.trim()));
		if(GrahaFormula.Val._isExtract(t.trim())) {
			Array.prototype.push.apply(refer, GrahaFormula.Val._extract(t.trim()));
		} else {
			refer.push(t.trim());
		}
	} else if(t.trim() != "") {
		result[current].append(new GrahaFormula.Const(t.trim()));
	}
	if(hist.length > 0) {
		hist.pop();
		result[result.length - 1].append(result[current])
		result.pop();
	}
	return {
		expr:result[0],
		refer:refer
	};
};

function test(expr, expected) {
	var result = GrahaFormula.parseFormula(expr);
	if(typeof(result.expr.valueOf()) == typeof(expected)) {
		if(result.expr.valueOf() == expected) {
			if(typeof(log) == "function") {
				log("Y\t" + expr + " = /" + result.expr.valueOf() + "/" + " = /" + expected + "/");
			} else {
				console.log("Y\t" + expr + " = /" + result.expr.valueOf() + "/" + " = /" + expected + "/");
			}
		} else {
			if(typeof(log) == "function") {
				log("N\t" + expr + " = /" + result.expr.valueOf() + "/" + " != /" + expected + "/");
			} else {
				console.log("N\t" + expr + " = /" + result.expr.valueOf() + "/" + " != /" + expected + "/");
			}
		}
	} else {
		if(typeof(log) == "function") {
			log("E\t" + expr + " = /" + result.expr.valueOf() + "/:/" + typeof(result.expr.valueOf()) + "/" + " != /" + expected + "/:/" + typeof(expected) + "/");
		} else {
			console.log("E\t" + expr + " = /" + result.expr.valueOf() + "/:/" + typeof(result.expr.valueOf()) + "/" + " != /" + expected + "/:/" + typeof(expected) + "/");
		}
	}
}
