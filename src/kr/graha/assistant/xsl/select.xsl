<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes" version="5.0" omit-xml-declaration="no" />
<xsl:template match="/">
<xsl:if test="system-property('xsl:vendor') = 'Microsoft'">
<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
</xsl:if>
<html>
<head>
<meta charset="UTF-8" />
<title><xsl:value-of select="/document/props/select.document.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body, input, textarea, select, button, table, p, td, a {
	font-size:medium;
}
table {
	border-collapse:collapse;
	border-top:#004990 2px solid;
	border-bottom:#004990 1px solid;
	margin-bottom:5px;
	width:100%;
	margin-top:10px;
}
table tbody {
	width:100%;
}
table tbody th, table thead th {
	font-weight:600;
	background-color:#0f4c81;
	text-align:center;
	color:#FFFFFF;
	padding-top:7px;
	padding-bottom:7px;
	font-size:medium;
}
table tbody td {
	border-bottom:#C3C3C3 inset 1px;
	color:#333;
	padding-top:7px;
	padding-bottom:7px;
	font-size:medium;
}
table tbody td a {
	text-decoration:none;
}
table.basic th {
	width:180px;
}
table.view th.view,
table.view td.view,
table.view th.link,
table.view td.link,
table.view th.width,
table.view td.width,
table.view th.detail_width,
table.view td.detail_width,
table.view th.insert_width,
table.view td.insert_width,
table.view th.align,
table.view td.align,
table.view th.detail_align,
table.view td.detail_align,
table.view th.insert_align,
table.view td.insert_align,
table.view th.search,
table.view td.search,
table.view th.readonly,
table.view td.readonly,
table.view th.hide_mobile,
table.view td.hide_mobile,
table.view th.detail_hide_mobile,
table.view td.detail_hide_mobile,
table.view th.code,
table.view td.code,
table.view th.format,
table.view td.format {
	width:80px;
	text-align:center;
}
table.view th.graha_data_type,
table.view td.graha_data_type,
table.view th.data_type_name,
table.view td.data_type_name {
	width:150px;
	text-align:center;
}
table.view input.list_width_column,
table.view input.detail_width_column,
table.view input.insert_width_column,
table.tables input.row_count_input,
table.view input.insert_validation_info {
	width:35px;
}

table.tables th.relation,
table.tables td.relation,
table.tables th.row_count,
table.tables td.row_count,
table.tables th.header_position,
table.tables td.header_position {
	text-align:center;
	width:200px;
}
table.view span.column_header,
table.view span.column_header_hide,
table.view span.list_header,
table.view span.list_header_hide,
table.view span.detail_header,
table.view span.detail_header_hide,
table.view span.insert_header,
table.view span.insert_header_hide {
	cursor:pointer;
}
table.view span.column_header_hide,
table.view span.list_header_hide,
table.view span.detail_header_hide,
table.view span.insert_header_hide {
	display:none;
}

table.view select.format_column_hidden,
table.view select.code_column_hidden
{
	display:none;
}

table.view th.comments_hidden,
table.view td.comments_hidden,
table.view th.data_type_name_hidden,
table.view td.data_type_name_hidden,
table.view th.graha_data_type_hidden,
table.view td.graha_data_type_hidden,
table.view input.insert_validation_info_hidden,
table.view input.insert_validation_info_hidden,
table.view th.link_hidden,
table.view td.link_hidden,
table.view th.align_hidden,
table.view td.align_hidden,
table.view th.width_hidden,
table.view td.width_hidden,
table.view th.search_hidden,
table.view td.search_hidden,
table.view th.hide_mobile_hidden,
table.view td.hide_mobile_hidden,
table.view th.detail_align_hidden,
table.view td.detail_align_hidden,
table.view th.detail_width_hidden,
table.view td.detail_width_hidden,
table.view th.detail_hide_mobile_hidden,
table.view td.detail_hide_mobile_hidden,
table.view th.insert_align_hidden,
table.view td.insert_align_hidden,
table.view th.insert_width_hidden,
table.view td.insert_width_hidden,
table.view th.validation_hidden,
table.view td.validation_hidden,
table.view th.calculation_hidden,
table.view td.calculation_hidden,
table.view th.readonly_hidden,
table.view td.readonly_hidden
{
	display:none;
}
table.view td.column_name,
table.view td.comments_hidden,
table.view td.comments,
table.view td.data_type_name,
table.view td.graha_data_type_hidden,
table.view td.graha_data_type,
table.view td.validation,
table.view td.calculation {
	white-space:nowrap;
}

select.auth_column {
	margin-right:20px;
}
</style>
<script>
function getList(prefix, className) {
	if(document.querySelector &amp;&amp; document.querySelectorAll) {
		return document.querySelectorAll(prefix + "." + className);
	} else {
		return document.getElementsByClassName(className);
	}
}
function getListTableColumn(tableName, columnClassName) {
	if(document.querySelector &amp;&amp; document.querySelectorAll) {
		return document.querySelectorAll("table.view." + tableName.replace(".", "\\.") + " " + "." + columnClassName);
	} else {
		var tables = document.getElementsByClassName(tableName);
		for(var i = 0; i &lt; tables.length; i++) {
			if(
				tables[i].nodeName != null &amp;&amp;
				tables[i].nodeName == "TABLE" &amp;&amp;
				tables[i].className.indexOf("view ") == 0
			) {
				return tables[i].getElementsByClassName(columnClassName);
			}
		}
	}
}
function getElementByClassAndName(prefix, className, name) {
	var list = getList(prefix, className);
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == name) {
			return list[i];
		}
	}
	return null;
}
function changeClassName(list, className) {
	for(var i = 0; i &lt; list.length; i++) {
		setTimeout(changeClassNameBySingleElement, 0, list[i], className);
	}
}
function changeClassNameBySingleElement(obj, className) {
	if(obj != null) {
		obj.className = className;
	}
}
function changeDisable(list, disabled, opt) {
	for(var i = 0; i &lt; list.length; i++) {
		if(opt &amp;&amp; opt != null) {
			if(opt.name &amp;&amp; opt.name != null) {
				if(list[i].name == opt.name) {
					list[i].disabled = disabled;
				}
			}
		} else {
			list[i].disabled = disabled;
		}
	}
}
function changeDisableBySingleElement(obj, disabled) {
	if(obj != null) {
		obj.disabled = disabled;
	}
}
function changeMasterTable(obj) {
	var masterTableName = obj.options[obj.options.selectedIndex].value;
	<xsl:for-each select="/document/params/table">
	if(masterTableName == "<xsl:value-of select="@name" />") {
		document.getElementById("xml_file_name").value = "<xsl:value-of select="@xml_file_name" />";
	}
	</xsl:for-each>
	var list = getList("select", "relation_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "relation_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
		relation(list[i]);
	}
	list = getList("select", "header_position_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "header_position_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("select", "row_count_type_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "row_count_type_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("input", "row_count_input");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "row_count_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("input", "list_view_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_view_column_" + masterTableName + "___") == 0) {
			list[i].disabled = false;
		} else {
			list[i].disabled = true;
		}
	}
	list = getList("input", "list_link_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_link_column_" + masterTableName + "___") == 0) {
			var view = getElementByClassAndName("input", "list_view_column", "list_view_column_" + list[i].name.substring("list_width_column_".length));
			if(view != null &amp;&amp; view.checked) {
				list[i].disabled = false;
			}
		} else {
			list[i].disabled = true;
		}
	}
	list = getList("select", "list_align");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_align_" + masterTableName + "___") == 0) {
			var view = getElementByClassAndName("input", "list_view_column", "list_view_column_" + list[i].name.substring("list_width_column_".length));
			if(view != null &amp;&amp; view.checked) {
				list[i].disabled = false;
			}
		} else {
			list[i].disabled = true;
		}
	}
	list = getList("input", "list_width_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_width_column_" + masterTableName + "___") == 0) {
			var view = getElementByClassAndName("input", "list_view_column", "list_view_column_" + list[i].name.substring("list_width_column_".length));
			if(view != null &amp;&amp; view.checked) {
				list[i].disabled = false;
			}
		} else {
			list[i].disabled = true;
		}
	}
	if(document.getElementById("search").checked) {
		list = getList("input", "list_search_column");
		for(var i = 0; i &lt; list.length; i++) {
			if(
				list[i].name.indexOf("list_search_column_" + masterTableName + "___") == 0 &amp;&amp;
				list[i].className.indexOf(" varchar") == (list[i].className.length - " varchar".length)
			) {
				list[i].disabled = false;
			} else {
				list[i].disabled = true;
			}
		}
	}
	calculation(obj);
}
function clickAuthentication(obj) {
	var list = getList("select", "auth_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(obj.checked) {
			list[i].disabled = false;
		} else {
			list[i].disabled = true;
		}
	}
}
function clickSearch(obj) {
	var masterTableName = document.getElementById("masterTableName").options[document.getElementById("masterTableName").options.selectedIndex].value;
	var list = getList("input", "list_search_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(
			obj.checked &amp;&amp;
			list[i].name.indexOf("list_search_column_" + masterTableName + "___") == 0 &amp;&amp;
			list[i].className.indexOf(" varchar") == (list[i].className.length - " varchar".length)
		) {
			list[i].disabled = false;
		} else {
			list[i].disabled = true;
		}
	}
}
function changeRelation(obj) {
	relation(obj);
	calculation(obj);
}
function changeDisableByRelation(list, prefix, tableName, selectedValue, equals) {
	for(var i = 0; i &lt; list.length; i++) {
		if(
			(equals &amp;&amp; list[i].name == prefix + tableName) ||
			(!equals &amp;&amp; list[i].name.indexOf(prefix + tableName + "___") == 0)
		) {
			if(selectedValue == "1") {
				setTimeout(changeDisableBySingleElement, 0, list[i], true);
			} else if(selectedValue == "many") {
				setTimeout(changeDisableBySingleElement, 0, list[i], false);
			}
		}
	}
}
function relation(obj) {
	var selectedValue = 1;
	if(!obj.disabled) {
		selectedValue = obj.options[obj.options.selectedIndex].value;
	}

	var tableName = obj.name.substring("relation_".length);
	var list = getList("select", "header_position_select");
	changeDisableByRelation(list, "header_position_", tableName, selectedValue, true);
	list = getList("input", "row_count_input");
	changeDisableByRelation(list, "row_count_", tableName, selectedValue, true);
	list = getList("select", "row_count_type_select");
	changeDisableByRelation(list, "row_count_type_", tableName, selectedValue, true);
	
	list = getList("select", "detail_align");
	changeDisableByRelation(list, "detail_align_", tableName, selectedValue, false);
	list = getList("input", "detail_width_column");
	changeDisableByRelation(list, "detail_width_column_", tableName, selectedValue, false);
	list = getList("input", "detail_hide_mobile_column");
	changeDisableByRelation(list, "detail_hide_mobile_column_", tableName, selectedValue, false);
	
	list = getList("select", "insert_align");
	changeDisableByRelation(list, "insert_align_", tableName, selectedValue, false);
	list = getList("input", "insert_width_column");
	changeDisableByRelation(list, "insert_width_column_", tableName, selectedValue, false);
	
}
function changeInsertValidation(obj) {
	var tableAndColumnName = obj.name.substring("insert_validation_".length);
	var selectedValue = obj.options[obj.options.selectedIndex].value;
	var list = getList("input", "insert_validation_info_hidden");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "insert_validation_info_" + tableAndColumnName) {
			if(selectedValue == "" || selectedValue == "not-null") {
				setTimeout(changeClassNameBySingleElement, 0, list[i], "insert_validation_info_hidden");
			} else {
				setTimeout(changeClassNameBySingleElement, 0, list[i], "insert_validation_info");
			}
		}
	}
	list = getList("input", "insert_validation_info");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "insert_validation_info_" + tableAndColumnName) {
			if(selectedValue == "" || selectedValue == "not-null") {
				setTimeout(changeClassNameBySingleElement, 0, list[i], "insert_validation_info_hidden");
			} else {
				setTimeout(changeClassNameBySingleElement, 0, list[i], "insert_validation_info");
			}
		}
	}
}

function clickcClculation(obj) {
	var tableAndColumnName = obj.name.substring("insert_is_calculation_column_".length);
	
	var list = getList("input", "insert_calculation_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "insert_calculation_column_" + tableAndColumnName) {
			if(obj.checked) {
				list[i].disabled = false;
			} else {
				list[i].disabled = true;
			}
		}
	}
}
function clickListView(obj) {
	var tableAndColumnName = obj.name.substring((obj.className + "_").length);
	var tableName = tableAndColumnName.substring(0, tableAndColumnName.indexOf("___"));
	var func = "list";
	if(obj.className == "list_view_column") {
		func = "list";
		changeDisable(getList("input", "list_link_column"), !obj.checked, {name:"list_link_column_" + tableAndColumnName});
		changeDisable(getList("input", "list_width_column"), !obj.checked, {name:"list_width_column_" + tableAndColumnName});
		changeDisable(getList("input", "list_hide_mobile_column"), !obj.checked, {name:"list_hide_mobile_column_" + tableAndColumnName});
		changeDisable(getList("select", "list_align"), !obj.checked, {name:"list_align_" + tableAndColumnName});
	} else if(obj.className == "detail_view_column") {
		func = "detail";
		var relation = getElementByClassAndName("select", "relation_select", "relation_" + tableName);
		
		if(relation != null &amp;&amp; !relation.disabled &amp;&amp; relation.value == "many") {
			changeDisable(getList("input", "detail_width_column"), !obj.checked, {name:"detail_width_column_" + tableAndColumnName});
			changeDisable(getList("input", "detail_hide_mobile_column"), !obj.checked, {name:"detail_hide_mobile_column_" + tableAndColumnName});
			changeDisable(getList("select", "detail_align"), !obj.checked, {name:"detail_align_" + tableAndColumnName});
		}
	} else if(obj.className == "insert_view_column") {
		func = "insert";
		var relation = getElementByClassAndName("select", "relation_select", "relation_" + tableName);
		if(relation != null &amp;&amp; !relation.disabled &amp;&amp; relation.value == "many") {
			changeDisable(getList("input", "insert_width_column"), !obj.checked, {name:"insert_width_column_" + tableAndColumnName});
			changeDisable(getList("select", "insert_align"), !obj.checked, {name:"insert_align_" + tableAndColumnName});
		}
		changeDisable(getList("select", "insert_validation"), !obj.checked, {name:"insert_validation_" + tableAndColumnName});
		changeDisable(getList("input", "insert_validation_info_hidden"), !obj.checked, {name:"insert_validation_info_" + tableAndColumnName});
		changeDisable(getList("input", "insert_validation_info"), !obj.checked, {name:"insert_validation_info_" + tableAndColumnName});
		changeDisable(getList("input", "insert_is_calculation_column"), !obj.checked, {name:"insert_is_calculation_column_" + tableAndColumnName});
		
		var insert_is_calculation = getElementByClassAndName("input", "insert_is_calculation_column", "insert_is_calculation_column_" + tableAndColumnName);
		
		if(!obj.checked) {
			changeDisable(getList("input", "insert_calculation_column"), true, {name:"insert_calculation_column_" + tableAndColumnName});
		} else {
			changeDisable(getList("input", "insert_calculation_column"), !insert_is_calculation.checked, {name:"insert_calculation_column_" + tableAndColumnName});
		}
		
		changeDisable(getList("input", "insert_readonly_column"), !obj.checked, {name:"insert_readonly_column_" + tableAndColumnName});
	}
	if(func == "insert") {
		return;
	}
	if(obj.checked) {
		if(func != "insert") {
			var format = getElementByClassAndName("select", "format_column_hidden", "format_column_" + tableAndColumnName);
			if(format != null) {
				changeClassNameBySingleElement(format, "format_column");
			}
		}
	} else {
		var detailView = getElementByClassAndName("input", "detail_view_column", "detail_view_column_" + tableAndColumnName);
		var listView = getElementByClassAndName("input", "list_view_column", "list_view_column_" + tableAndColumnName);
		if(func == "insert") {
			return;
		}
		if(
			(detailView != null &amp;&amp; detailView.checked) ||
			(listView != null &amp;&amp; listView.checked)
		) {
			
			var format = getElementByClassAndName("select", "format_column_hidden", "format_column_" + tableAndColumnName);
			changeClassNameBySingleElement(format, "format_column");
		} else {
			var format = getElementByClassAndName("select", "format_column", "format_column_" + tableAndColumnName);
			if(format != null) {
				changeClassNameBySingleElement(format, "format_column_hidden");
			}
		}
	}
}

function showOrHideColumn(obj, tableName, columnName) {
	var span = null;
	var display = "none";
	var list = null;
	var header = null;
	var colspan = 1;
	if(obj.className.indexOf("column_header ") == 0) {
		list = getListTableColumn(tableName, "comments_hidden");
		changeClassName(list, "comments");
		list = getListTableColumn(tableName, "data_type_name_hidden");
		changeClassName(list, "data_type_name");
		list = getListTableColumn(tableName, "graha_data_type_hidden");
		changeClassName(list, "graha_data_type");
		
		header = getListTableColumn(tableName, "column");
		colspan = 4;
		
		span = getList("span", "column_header_hide");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("column_header_hide ") == 0) {
		list = getListTableColumn(tableName, "comments");
		changeClassName(list, "comments_hidden");
		list = getListTableColumn(tableName, "data_type_name");
		changeClassName(list, "data_type_name_hidden");
		list = getListTableColumn(tableName, "graha_data_type");
		changeClassName(list, "graha_data_type_hidden");
		
		header = getListTableColumn(tableName, "column");
		colspan = 1;
		
		span = getList("span", "column_header");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("list_header ") == 0) {
		list = getListTableColumn(tableName, "link_hidden");
		changeClassName(list, "link");
		list = getListTableColumn(tableName, "align_hidden");
		changeClassName(list, "align");
		list = getListTableColumn(tableName, "width_hidden");
		changeClassName(list, "width");
		list = getListTableColumn(tableName, "search_hidden");
		changeClassName(list, "search");
		list = getListTableColumn(tableName, "hide_mobile_hidden");
		changeClassName(list, "hide_mobile");
		
		header = getListTableColumn(tableName, "list");
		colspan = 6;
		
		span = getList("span", "list_header_hide");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("list_header_hide ") == 0) {
		list = getListTableColumn(tableName, "link");
		changeClassName(list, "link_hidden");
		list = getListTableColumn(tableName, "align");
		changeClassName(list, "align_hidden");
		list = getListTableColumn(tableName, "width");
		changeClassName(list, "width_hidden");
		list = getListTableColumn(tableName, "search");
		changeClassName(list, "search_hidden");
		list = getListTableColumn(tableName, "hide_mobile");
		changeClassName(list, "hide_mobile_hidden");
		
		header = getListTableColumn(tableName, "list");
		colspan = 1;
		
		span = getList("span", "list_header");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("detail_header ") == 0) {
		list = getListTableColumn(tableName, "detail_align_hidden");
		changeClassName(list, "detail_align");
		list = getListTableColumn(tableName, "detail_width_hidden");
		changeClassName(list, "detail_width");
		list = getListTableColumn(tableName, "detail_hide_mobile_hidden");
		changeClassName(list, "detail_hide_mobile");
		
		header = getListTableColumn(tableName, "detail");
		colspan = 4;
		
		span = getList("span", "detail_header_hide");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("detail_header_hide ") == 0) {
		list = getListTableColumn(tableName, "detail_align");
		changeClassName(list, "detail_align_hidden");
		list = getListTableColumn(tableName, "detail_width");
		changeClassName(list, "detail_width_hidden");
		list = getListTableColumn(tableName, "detail_hide_mobile");
		changeClassName(list, "detail_hide_mobile_hidden");
		
		header = getListTableColumn(tableName, "detail");
		colspan = 1;
		
		span = getList("span", "detail_header");
		display = "inline";
		obj.style.display = "none";
	}
	
	if(obj.className.indexOf("insert_header ") == 0) {
		list = getListTableColumn(tableName, "insert_align_hidden");
		changeClassName(list, "insert_align");
		list = getListTableColumn(tableName, "insert_width_hidden");
		changeClassName(list, "insert_width");
		
		list = getListTableColumn(tableName, "validation_hidden");
		changeClassName(list, "validation");
		list = getListTableColumn(tableName, "calculation_hidden");
		changeClassName(list, "calculation");
		list = getListTableColumn(tableName, "readonly_hidden");
		changeClassName(list, "readonly");
		
		header = getListTableColumn(tableName, "insert");
		colspan = 6;
	
		span = getList("span", "insert_header_hide");
		display = "inline";
		obj.style.display = "none";
	}
	if(obj.className.indexOf("insert_header_hide ") == 0) {
		list = getListTableColumn(tableName, "insert_align");
		changeClassName(list, "insert_align_hidden");
		list = getListTableColumn(tableName, "insert_width");
		changeClassName(list, "insert_width_hidden");
		
		list = getListTableColumn(tableName, "validation");
		changeClassName(list, "validation_hidden");
		list = getListTableColumn(tableName, "calculation");
		changeClassName(list, "calculation_hidden");
		list = getListTableColumn(tableName, "readonly");
		changeClassName(list, "readonly_hidden");
		
		header = getListTableColumn(tableName, "insert");
		colspan = 1;
		
		span = getList("span", "insert_header");
		display = "inline";
		obj.style.display = "none";
	}
	if(header != null) {
		for(var i = 0; i &lt; header.length; i++) {
			if(
				header[i].nodeName != null &amp;&amp;
				header[i].nodeName == "TH"
			) {
				if(header[i].colspan) {
					header[i].colspan = colspan;
				} else {
					header[i].setAttribute("colspan", colspan.toString());
				}
			}
		}
	}
	if(span != null) {
		for(var i = 0; i &lt; span.length; i++) {
			if(span[i].className.indexOf(" " + tableName) == (span[i].className.length - (" " + tableName).length)) {
				span[i].style.display = display;
			}
		}
	}
}

function calculation(obj) {
	var masterTableName = null;
	if(obj.name == "table") {
		masterTableName = obj.options[obj.options.selectedIndex].value;
	} else {
		masterTableName = document.getElementById("masterTableName").options[document.getElementById("masterTableName").options.selectedIndex].value;
	}
	var list = getList("input", "insert_calculation_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].value == null || list[i].value.trim() == "") {
			continue;
		}
		var tableAndColumnName = list[i].name.substring("insert_calculation_column_".length);
		var tableName = tableAndColumnName.substring(0, tableAndColumnName.indexOf("___"));
		var tableNameOnly = tableName;
		if(tableName.indexOf(".") > 0) {
			tableNameOnly = tableName.substring(tableName.indexOf(".") + 1);
		}

		var columnName = tableAndColumnName.substring(tableAndColumnName.indexOf("___") + 3);
		if(
			obj.name != "table" &amp;&amp;
			obj.name != "relation_" + tableName
		) {
			continue;
		}
		var func = null;
		if(
			list[i].value.trim() == "trim(" + columnName.toLowerCase() + ")" ||
			list[i].value.trim() == "trim(" + tableNameOnly.toLowerCase() + "." + columnName.toLowerCase() + ")" ||
			list[i].value.trim() == "trim(" + tableNameOnly.toLowerCase() + "." + columnName.toLowerCase() + ".{N})"
		) {
			func = "trim";
		} else if(
			list[i].value.trim() == "comma(" + columnName.toLowerCase() + ")" ||
			list[i].value.trim() == "comma(" + tableNameOnly.toLowerCase() + "." + columnName.toLowerCase() + ")" ||
			list[i].value.trim() == "comma(" + tableNameOnly.toLowerCase() + "." + columnName.toLowerCase() + ".{N})"
		) {
			func = "comma";
		}
		if(func != null) {
			var expr = func + "(";
			if(tableName == masterTableName) {
				expr += columnName.toLowerCase() + ")";
			} else {
				expr += tableNameOnly + "." + columnName.toLowerCase();
				var relation = getElementByClassAndName("select", "relation_select", "relation_" + tableName);
				if(relation == null) {
					expr += ")";
					alert("<xsl:value-of select="/document/props/select.message.generation.relation_is_null.error.msg" />");
				} else {
					if(relation.value == "1") {
						expr += ")";
					} else if(relation.value == "many") {
						expr += ".{N})";
					} else {
						alert("<xsl:value-of select="/document/props/select.message.generation.relation_value_unknown.error.msg" /> : " + relation.value + "");
					}
				}
			}
			list[i].value = expr;
		}
	}
}
function check() {
	var list = getList("input", "row_count_input");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].disabled) {
		} else if(isNumberic(list[i].value)){
		} else {
			alert("<xsl:value-of select="/document/props/select.message.generation.row_count.error.msg" />");
			list[i].focus();
			return false;
		}
	}
	
	list = getList("input", "insert_validation_info");
	for(var i = 0; i &lt; list.length; i++) {
		if(isNumberic(list[i].value)){
		} else {
			alert("<xsl:value-of select="/document/props/select.message.generation.validation_length.error.msg" />");
			list[i].focus();
			return false;
		}
	}
	var isSearch = document.getElementById("search");
	
	if(isSearch.checked) {
		list = getList("input", "list_search_column");
		var cnt = 0;
		for(var i = 0; i &lt; list.length; i++) {
			if(list[i].disabled) {
			} else if(list[i].checked) {
				cnt++;
			}
		}
		if(cnt == 0) {
			alert("<xsl:value-of select="/document/props/select.message.generation.search_column.error.msg" />");
		}
	}
	return true;
}
function isNumberic(v) {
	if(!isNaN(Number(v)) &amp;&amp; !isNaN(parseInt(v)) &amp;&amp; parseInt(v).toString() == v) {
		return true;
	}
	return false;
}
</script>
</head>
<body>
<form action="gen" method="post" class="conf" onsubmit="return check();">
	<xsl:for-each select="/document/params/table">
		<xsl:sort select="@master" order="descending" />
		<input type="hidden" name="tables" value="{@name}" />
	</xsl:for-each>
	<xsl:if test="/document/params/param/jndi">
		<input>
			<xsl:attribute name="name">jndi</xsl:attribute>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
		</input>
	</xsl:if>
	<h3 class="basic">1. <xsl:value-of select="/document/props/select.document.basic.label" /></h3>
	<table class="basic">
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.master_table.label" /></th>
			<td>
				<select name="table" onChange="changeMasterTable(this)" id="masterTableName">
					<xsl:for-each select="/document/params/table[@master = 'true']">
						<xsl:sort select="@master" order="descending" />
						<option value="{@name}">
							<xsl:value-of select="@name" />
						</option>
					</xsl:for-each>
				</select>
			</td>
		</tr>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.xml_file_name.label" /></th>
			<td>
				<input type="text" name="xml_file_name" id="xml_file_name" value="{/document/params/table[@master = 'true']/@xml_file_name}" />
			</td>
		</tr>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.file_upload.label" /></th>
			<td>
				<input type="checkbox" name="file_upload" value="true" />
			</td>
		</tr>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.authentication.label" /></th>
			<td>
				<input type="checkbox" name="authentication" value="true" onClick="clickAuthentication(this)" />
			</td>
		</tr>
		<xsl:for-each select="/document/params/table">
			<xsl:sort select="@master" order="descending" />
			<tr>
				<th><xsl:value-of select="@name" /></th>
				<td>
						<select name="auth_column_{@name}" class="auth_column" value="{/document/params/prop/owner_column}" disabled="disabled">
							<xsl:for-each select="column">
								<xsl:if test="@pk != 'true' and @fk != 'true'">
									<xsl:choose>
										<xsl:when test="@lower_name = /document/params/prop/owner_column">
											<option value="{@name}" selected="selected"><xsl:value-of select="@name" /></option>
										</xsl:when>
										<xsl:otherwise>
											<option value="{@name}"><xsl:value-of select="@name" /></option>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
							</xsl:for-each>
						</select>
						<xsl:value-of select="/document/props/select.table.basic.select_authentication_column.label" />
				</td>
			</tr>
		</xsl:for-each>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.search.label" /></th>
			<td>
				<input type="checkbox" name="search" value="true" onClick="clickSearch(this)" id="search" />
			</td>
		</tr>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.list_type.label" /></th>
			<td>
				<select name="list_type">
					<option value="page">page</option>
					<option value="all">all</option>
				</select>
			</td>
		</tr>
		<tr>
			<th><xsl:value-of select="/document/props/select.table.basic.width.label" /></th>
			<td>
				<input type="text" name="header_column_width" value="120px" />
			</td>
		</tr>
	</table>
	<xsl:variable name="master_table_name"><xsl:value-of select="/document/params/table[@master = 'true']/@name" /></xsl:variable>
	<xsl:if test="count(/document/params/table) > 1">
		<h3 class="tables">2. <xsl:value-of select="/document/props/select.document.tables.label" /></h3>
		<table class="tables">
			<tr>
				<th class="table_name"><xsl:value-of select="/document/props/select.table.tables.table_name.label" /></th>
				<th class="relation"><xsl:value-of select="/document/props/select.table.tables.relation.label" /></th>
				<th class="header_position"><xsl:value-of select="/document/props/select.table.tables.header_position.label" /></th>
				<th class="row_count"><xsl:value-of select="/document/props/select.table.tables.row_count.label" /></th>
			</tr>
			<xsl:for-each select="/document/params/table">
				<xsl:sort select="@master" order="descending" />
				<tr>
					<td class="table_name"><xsl:value-of select="@name" /></td>
					<td class="relation">
						<xsl:choose>
							<xsl:when test="@name != $master_table_name">
								<select name="relation_{@name}" class="relation_select" id="relation" onchange="changeRelation(this)">
									<option value="1">1-1</option>
									<option value="many" selected="selected">1-many</option>
								</select>
							</xsl:when>
							<xsl:otherwise>
								<select name="relation_{@name}" disabled="disabled" class="relation_select" id="relation" onchange="changeRelation(this)">
									<option value="1">1-1</option>
									<option value="many" selected="selected">1-many</option>
								</select>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="header_position">
						<xsl:choose>
							<xsl:when test="@name != $master_table_name">
								<select name="header_position_{@name}" class="header_position_select">
									<option value="top">Top</option>
									<option value="left">Left</option>
								</select>
							</xsl:when>
							<xsl:otherwise>
								<select name="header_position_{@name}" disabled="disabled" class="header_position_select">
									<option value="top">Top</option>
									<option value="left">Left</option>
								</select>
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="row_count">
						<xsl:choose>
							<xsl:when test="@name != $master_table_name">
								<select name="row_count_type_{@name}" class="row_count_type_select">
									<option value="append">append</option>
									<option value="total">total</option>
								</select>
								<input type="text" class="row_count_input" name="row_count_{@name}" value="3" />
							</xsl:when>
							<xsl:otherwise>
								<select name="row_count_type_{@name}" disabled="disabled" class="row_count_type_select">
									<option value="append">append</option>
									<option value="total">total</option>
								</select>
								<input type="text" class="row_count_input" name="row_count_{@name}" disabled="disabled" value="3" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
	
	<h3 class="view"><xsl:choose><xsl:when test="count(/document/params/table) > 1">3</xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>. <xsl:value-of select="/document/props/select.document.display.label" /></h3>
	<xsl:for-each select="/document/params/table">
		<xsl:sort select="@master" order="descending" />
		<xsl:variable name="table_name"><xsl:value-of select="@name" /></xsl:variable>
		<xsl:variable name="tab_name"><xsl:value-of select="@table_name" /></xsl:variable>
		<h4 class="view {@name}"><xsl:choose><xsl:when test="count(/document/params/table) > 1">3</xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>.<xsl:value-of select="position()" />. <xsl:value-of select="@name" /></h4>
		<table class="view {@name}">
			<tr>
				<th class="column" colspan="1">
					<xsl:value-of select="/document/props/select.table.display.column.label" /> <span class="column_header {@name}" onClick="showOrHideColumn(this, '{@name}', 'column')">(+)</span>
					<span class="column_header_hide {@name}" onClick="showOrHideColumn(this, '{@name}', 'column')">(-)</span>
				</th>
				<th class="format" rowspan="2"><xsl:value-of select="/document/props/select.table.display.format.label" /></th>
				<xsl:if test="count(/document/params/codes/code) > 0">
					<th class="code" rowspan="2"><xsl:value-of select="/document/props/select.table.display.code.label" /></th>
				</xsl:if>
				<th class="list" colspan="1">
					<xsl:value-of select="/document/props/select.table.display.list.label" /> <span class="list_header {@name}" onClick="showOrHideColumn(this, '{@name}', 'list')">(+)</span>
					<span class="list_header_hide {@name}" onClick="showOrHideColumn(this, '{@name}', 'list')">(-)</span>
				</th>
				<th class="detail" colspan="1">
					<xsl:value-of select="/document/props/select.table.display.detail.label" /> <span class="detail_header {@name}" onClick="showOrHideColumn(this, '{@name}', 'detail')">(+)</span>
					<span class="detail_header_hide {@name}" onClick="showOrHideColumn(this, '{@name}', 'detail')">(-)</span>
				</th>
				<th class="insert" colspan="1">
					<xsl:value-of select="/document/props/select.table.display.insert_or_update.label" /> <span class="insert_header {@name}" onClick="showOrHideColumn(this, '{@name}', 'insert')">(+)</span>
					<span class="insert_header_hide {@name}" onClick="showOrHideColumn(this, '{@name}', 'insert')">(-)</span>
				</th>
			</tr>
			<tr>
				<th class="column_name"><xsl:value-of select="/document/props/select.table.display.column_name.label" /></th>
				<th class="comments_hidden"><xsl:value-of select="/document/props/select.table.display.column_comments.label" /></th>
				<th class="data_type_name_hidden"><xsl:value-of select="/document/props/select.table.display.column_data_type_name.label" /></th>
				<th class="graha_data_type_hidden"><xsl:value-of select="/document/props/select.table.display.column_graha_data_type.label" /></th>
				
				<th class="view"><xsl:value-of select="/document/props/select.table.display.list_view.label" /></th>
				<th class="link_hidden"><xsl:value-of select="/document/props/select.table.display.list_link.label" /></th>
				<th class="align_hidden"><xsl:value-of select="/document/props/select.table.display.list_align.label" /></th>
				<th class="width_hidden"><xsl:value-of select="/document/props/select.table.display.list_width.label" /></th>
				<th class="search_hidden"><xsl:value-of select="/document/props/select.table.display.list_search.label" /></th>
				<th class="hide_mobile_hidden"><xsl:value-of select="/document/props/select.table.display.list_hide_mobile.label" /></th>
				
				<th class="view"><xsl:value-of select="/document/props/select.table.display.detail_view.label" /></th>
				<th class="detail_align_hidden"><xsl:value-of select="/document/props/select.table.display.detail_align.label" /></th>
				<th class="detail_width_hidden"><xsl:value-of select="/document/props/select.table.display.detail_width.label" /></th>
				<th class="detail_hide_mobile_hidden"><xsl:value-of select="/document/props/select.table.display.detail_hide_mobile.label" /></th>
				
				<th class="view"><xsl:value-of select="/document/props/select.table.display.insert_view.label" /></th>
				<th class="insert_align_hidden"><xsl:value-of select="/document/props/select.table.display.insert_align.label" /></th>
				<th class="insert_width_hidden"><xsl:value-of select="/document/props/select.table.display.insert_width.label" /></th>
				<th class="validation_hidden"><xsl:value-of select="/document/props/select.table.display.insert_validation.label" /></th>
				<th class="calculation_hidden"><xsl:value-of select="/document/props/select.table.display.insert_calculation.label" /></th>
				<th class="readonly_hidden"><xsl:value-of select="/document/props/select.table.display.insert_readonly.label" /></th>
			</tr>
			<xsl:for-each select="column">
				<tr>
					<td class="column_name"><xsl:value-of select="@name" /></td>
					<td class="comments_hidden"><xsl:value-of select="." /></td>
					<td class="data_type_name_hidden"><xsl:value-of select="@data_type_name" /></td>
					<td class="graha_data_type_hidden"><xsl:value-of select="@graha_data_type" /></td>
					<td class="format">
						<xsl:if test="@pk = 'false' and @fk = 'false'">
							<xsl:if test="@graha_data_type = 'int' or @graha_data_type = 'long' or @graha_data_type = 'float' or @graha_data_type = 'double' or @graha_data_type = 'timestamp' or @graha_data_type = 'date'">
								<select>
									<xsl:attribute name="name">format_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
									<xsl:choose>
										<xsl:when test="@def = 'true'">
											<xsl:attribute name="class">format_column_hidden</xsl:attribute>
										</xsl:when>
									</xsl:choose>
									<xsl:if test="@graha_data_type = 'int' or @graha_data_type = 'long' or @graha_data_type = 'float' or @graha_data_type = 'double'">
										<option value="">N/A</option>
										<option value="ts">T/S</option>
									</xsl:if>
									<xsl:if test="@graha_data_type = 'timestamp' or @graha_data_type = 'date'">
										<option value="date">yyyy-MM-dd</option>
										<xsl:if test="@graha_data_type = 'timestamp'">
											<option value="datetime">yyyy-MM-dd HH:mm:ss</option>
										</xsl:if>
									</xsl:if>
								</select>
							</xsl:if>
						</xsl:if>
					</td>
					<xsl:if test="count(/document/params/codes/code) > 0">
						<td class="code">
							<xsl:if test="@pk = 'false' and @fk = 'false' and @def = 'false'">
								<xsl:if test="@graha_data_type = 'char' or @graha_data_type = 'varchar'">
									<select>
										<xsl:attribute name="name">code_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
										<xsl:attribute name="class">code_column</xsl:attribute>
										<option value="">N/A</option>
											<xsl:for-each select="/document/params/codes/code">
												<option value="{@value}"><xsl:value-of select="@label" /></option>
											</xsl:for-each>
									</select>
								</xsl:if>
							</xsl:if>
						</td>
					</xsl:if>
					<td class="view">
						<input>
							<xsl:attribute name="name">list_view_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">list_view_column</xsl:attribute>
							<xsl:attribute name="onClick">clickListView(this)</xsl:attribute>
							<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
							<xsl:if test="$table_name != $master_table_name">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="link_hidden">
						<input>
							<xsl:attribute name="name">list_link_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">list_link_column</xsl:attribute>
							<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
							<xsl:if test="$table_name != $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="align_hidden">
						<select>
							<xsl:attribute name="name">list_align_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">list_align</xsl:attribute>
							<xsl:if test="$table_name != $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
							<option value="left">left</option>
							<option value="center">center</option>
							<option value="right">right</option>
						</select>
					</td>
					<td class="width_hidden">
						<input>
							<xsl:attribute name="name">list_width_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">text</xsl:attribute>
							<xsl:attribute name="class">list_width_column</xsl:attribute>
							<xsl:if test="not(@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text' and $table_name = $master_table_name)">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="search_hidden">
						<input>
							<xsl:attribute name="name">list_search_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:choose>
								<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false' and (@graha_data_type = 'varchar')">
									<xsl:attribute name="checked">checked</xsl:attribute>
									<xsl:attribute name="class">list_search_column <xsl:value-of select="@graha_data_type" /></xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="class">list_search_column</xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:attribute name="disabled">disabled</xsl:attribute>
						</input>
					</td>
					<td class="hide_mobile_hidden">
						<input>
							<xsl:attribute name="name">list_hide_mobile_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">list_hide_mobile_column</xsl:attribute>
							<xsl:if test="$table_name != $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="view">
						<input>
							<xsl:attribute name="name">detail_view_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">detail_view_column</xsl:attribute>
							<xsl:attribute name="onClick">clickListView(this)</xsl:attribute>
							<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="detail_align_hidden">
						<select>
							<xsl:attribute name="name">detail_align_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">detail_align</xsl:attribute>
							<xsl:if test="$table_name = $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
							<option value="left">left</option>
							<option value="center">center</option>
							<option value="right">right</option>
						</select>
					</td>
					<td class="detail_width_hidden">
						<input>
							<xsl:attribute name="name">detail_width_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">text</xsl:attribute>
							<xsl:attribute name="class">detail_width_column</xsl:attribute>
							<xsl:if test="$table_name = $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="detail_hide_mobile_hidden">
						<input>
							<xsl:attribute name="name">detail_hide_mobile_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
							<xsl:attribute name="class">detail_hide_mobile_column</xsl:attribute>
							<xsl:if test="$table_name = $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
						</input>
					</td>
					<td class="view">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
							<input>
								<xsl:attribute name="name">insert_view_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="class">insert_view_column</xsl:attribute>
								<xsl:attribute name="onClick">clickListView(this)</xsl:attribute>
								<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
									<xsl:attribute name="checked">checked</xsl:attribute>
								</xsl:if>
							</input>
						</xsl:if>
					</td>
					<td class="insert_align_hidden">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
							<select>
								<xsl:attribute name="name">insert_align_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="class">insert_align</xsl:attribute>
								<xsl:if test="$table_name = $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
									<xsl:attribute name="disabled">disabled</xsl:attribute>
								</xsl:if>
								<option value="left">left</option>
								<option value="center">center</option>
								<option value="right">right</option>
							</select>
						</xsl:if>
					</td>
					<td class="insert_width_hidden">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
							<input>
								<xsl:attribute name="name">insert_width_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="type">text</xsl:attribute>
								<xsl:attribute name="class">insert_width_column</xsl:attribute>
								<xsl:if test="$table_name = $master_table_name or not(@def = 'false' and @pk = 'false' and @fk = 'false')">
									<xsl:attribute name="disabled">disabled</xsl:attribute>
								</xsl:if>
							</input>
						</xsl:if>
					</td>
					<td class="validation_hidden">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
							<xsl:if test="@graha_data_type = 'int' or @graha_data_type = 'long' or @graha_data_type = 'float' or @graha_data_type = 'double'">
								<select name="insert_validation_{$table_name}___{@name}" class="insert_validation">
									<option value="">N/A</option>
									<option value="int">int</option>
									<option value="long">long</option>
									<option value="float">float</option>
									<option value="double">double</option>
								</select>
							</xsl:if>
							<xsl:if test="@graha_data_type = 'varchar'">
								<select name="insert_validation_{$table_name}___{@name}" onChange="changeInsertValidation(this)" class="insert_validation">
									<option value="">N/A</option>
									<option value="not-null">not null</option>
									<option value="min-length">min length</option>
									<option value="max-length">max length</option>
								</select>
								<input type="text" name="insert_validation_info_{$table_name}___{@name}" class="insert_validation_info_hidden" />
							</xsl:if>
						</xsl:if>
					</td>
					<td class="calculation_hidden">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false' and @graha_data_type != 'boolean'">
							<input>
								<xsl:attribute name="name">insert_is_calculation_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="class">insert_is_calculation_column</xsl:attribute>
								<xsl:attribute name="value"><xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="onClick">clickcClculation(this)</xsl:attribute>
							</input>
							<input>
								<xsl:attribute name="name">insert_calculation_column_<xsl:value-of select="$table_name" />___<xsl:value-of select="@name" /></xsl:attribute>
								<xsl:attribute name="type">text</xsl:attribute>
								<xsl:if test="@graha_data_type = 'varchar'">
									<xsl:attribute name="value">trim(<xsl:if test="$table_name != $master_table_name"><xsl:value-of select="$tab_name" />.</xsl:if><xsl:value-of select="@lower_name" /><xsl:if test="$table_name != $master_table_name">.{N}</xsl:if>)</xsl:attribute>
									<xsl:attribute name="readonly">readonly</xsl:attribute>
								</xsl:if>
								<xsl:if test="@graha_data_type = 'int' or @graha_data_type = 'long' or @graha_data_type = 'float' or @graha_data_type = 'double'">
									<xsl:attribute name="value">comma(<xsl:if test="$table_name != $master_table_name"><xsl:value-of select="$tab_name" />.</xsl:if><xsl:value-of select="@lower_name" /><xsl:if test="$table_name != $master_table_name">.{N}</xsl:if>)</xsl:attribute>
								</xsl:if>
								<xsl:attribute name="class">insert_calculation_column</xsl:attribute>
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</input>
						</xsl:if>
					</td>
					<td class="readonly_hidden">
						<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false' and @graha_data_type != 'boolean'">
							<input type="checkbox" name="insert_readonly_column_{$table_name}___{@name}" class="insert_readonly_column" value="{@name}" />
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:for-each>
	<div style="width:100%;text-align:center;">
		<input>
			<xsl:attribute name="type">submit</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/props/select.button.generation.label" /></xsl:attribute>
		</input>
	</div>
</form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
