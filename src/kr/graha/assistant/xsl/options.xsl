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
<title><xsl:value-of select="/document/props/options.document.title" /></title>
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
table.basic td input[type='text'] {
	width:100%;
	-moz-box-sizing: border-box;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
}
table.column td input[type='text'] {
	width:100%;
	-moz-box-sizing: border-box;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
}
</style>
<script>
function check(obj) {
	if(obj.query_id.value == null || obj.query_id.value == "") {
		alert("<xsl:value-of select="/document/props/options.message.generation.query_id_is_null.error.msg" />");
		alert("Query ID가 입력되지 않았습니다.");
		obj.query_id.focus();
		return false;
	}
	var msg = null;
	if(obj.query_label.value == null || obj.query_label.value == "") {
		msg = "<xsl:value-of select="/document/props/options.message.generation.query_label_is_null.error.msg" />"
		obj.query_label.focus();
	}
<xsl:for-each select="/document/columns/column">
	if(obj["label_<xsl:value-of select="@key" />"].value == null || obj["label_<xsl:value-of select="@key" />"].value == "") {
		msg = "<xsl:value-of select="/document/props/options.message.generation.column_label_is_null.error.msg" />"
		msg += "(" + <xsl:value-of select="name" /> + ")";
		msg += "<xsl:value-of select="/document/props/options.message.generation.column_label_is_null.continue.msg" />"
		obj["label_<xsl:value-of select="@key" />"].focus();
	}
</xsl:for-each>
	if(msg != null) {
		if(confirm(msg)) {
			return true;
		} else {
			return false;
		}
	}
	return true;
}
function changeLabel(obj) {
	document.getElementById("gen_from_query")["label_column." + obj.name.substring(obj.name.indexOf(".") + 1)].value = obj.options[obj.options.selectedIndex].value;
}
</script>
</head>
<body>
<form action="gen_from_query" method="post" onsubmit="return check(this);" id="gen_from_query">
	<h3 class="basic">1. <xsl:value-of select="/document/props/options.document.basic.label" /></h3>
	<table class="basic">
		<tr class="query_id">
			<th><xsl:value-of select="/document/props/options.table.basic.query_id.label" /></th>
			<td>
				<input type="text" name="query_id" />
			</td>
		</tr>
		<tr class="query_label">
			<th><xsl:value-of select="/document/props/options.table.basic.query_label.label" /></th>
			<td>
				<input type="text" name="query_label" />
			</td>
		</tr>
<xsl:if test="/document/params/param[@name='fetched']">
		<tr class="func_type">
			<th><xsl:value-of select="/document/props/options.table.basic.func_type.label" /></th>
			<td>
				<select name="func_type">
<xsl:if test="/document/params/param[@name='fetched'] = '1' or /document/params/param[@name='fetched'] = '0'">
					<option value="detail">detail</option>
</xsl:if>
					<option value="list">list</option>
					<option value="listAll">listAll</option>
				</select>
			</td>
		</tr>
</xsl:if>
<xsl:if test="/document/params/param[@name='updated']">
		<tr class="redirect">
			<th><xsl:value-of select="/document/props/options.table.basic.redirect.label" /></th>
			<td>
				<input type="text" name="redirect_path" />
			</td>
		</tr>
</xsl:if>
<!--
		<tr class="print_and_file">
			<th><xsl:value-of select="/document/props/options.table.basic.exec_type.label" /></th>
			<td>
				<input type="checkbox" name="print" value="print" checked="checked" disabled="disabled" /> <xsl:value-of select="/document/props/options.table.basic.exec_type.print.label" />
				<input type="checkbox" name="file" value="file" onclick="clickFile(this)" /> <xsl:value-of select="/document/props/options.table.basic.exec_type.file.label" />
			</td>
		</tr>
		<tr class="file_path_hidden" id="file_path">
			<th><xsl:value-of select="/document/props/options.table.basic.file_path.label" /></th>
			<td>
				WEB-INF/graha/generated/<input type="text" name="file_path" style="width:200px;" />.xml
			</td>
		</tr>
-->
	</table>
<xsl:if test="count(/document/columns/column) > 0">
	<h3 class="column">2. <xsl:value-of select="/document/props/options.document.column_info.label" /></h3>
	<table class="column">
		<tr>
			<th><xsl:value-of select="/document/props/options.table.column_info.column_name.label" /></th>
			<th colspan="2"><xsl:value-of select="/document/props/options.table.column_info.column_label.label" /></th>
		</tr>
		<xsl:for-each select="/document/columns/column">
			<tr>
				<td>
					<input>
						<xsl:attribute name="type">hidden</xsl:attribute>
						<xsl:attribute name="name">name_<xsl:value-of select="@key" /></xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="name" /></xsl:attribute>
					</input>
					<xsl:value-of select="name" />
				</td>
				<td>
					<xsl:if test="labels/label">
						<select>
							<xsl:attribute name="onchange">changeLabel(this)</xsl:attribute>
							<xsl:attribute name="name">label_select_<xsl:value-of select="@key" /></xsl:attribute>
							<xsl:for-each select="labels/label">
								<option>
									<xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute>
									<xsl:value-of select="." />
								</option>
							</xsl:for-each>
						</select> (<xsl:value-of select="count(labels/label)" />)
					</xsl:if>
				</td>
				<td>
					<input>
						<xsl:attribute name="type">text</xsl:attribute>
						<xsl:attribute name="name">label_<xsl:value-of select="@key" /></xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="labels/label" /></xsl:attribute>
					</input>
				</td>
			</tr>
		</xsl:for-each>
	</table>
</xsl:if>
<xsl:for-each select="/document/params/param">
	<input>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute>
	</input>
</xsl:for-each>
<div style="width:100%;text-align:center;">
	<input>
		<xsl:attribute name="type">submit</xsl:attribute>
		<xsl:attribute name="value">Generation</xsl:attribute>
	</input>
</div>
</form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
