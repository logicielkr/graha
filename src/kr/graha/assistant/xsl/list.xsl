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
<title><xsl:value-of select="/document/props/list.document.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body, input, textarea, select, button, table, p, td, a {
	font-size:medium;
}

h1, h2, h3, h4, h5 {
	text-decoration:none;
	font-weight:bold;
	padding:0;
	margin:0;
	line-height:1.3em;
}
tr.tr_0 {
	background-color:#F3F3F3;
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
</style>
<script>
function gen() {
	var list = document.querySelectorAll("input[type='checkbox']");
	var isChecked = false;
	var isError = false;
	var checked = new Array();
	if(list != null) {
		for(var i = 0; i &lt; list.length; i++) {
			if(list[i].checked) {
				isChecked = true;
				var name = list[i].name;
				if(name.indexOf(".") > 0) {
					var schemaName;
					var tableNamee
					if(name.indexOf(".") == name.lastIndexOf(".")) {
						var schemaName = "";
						var tableName = name.substring(name.indexOf(".") + 1);
					} else {
						var schemaName = name.substring(name.indexOf(".") + 1, name.lastIndexOf("."));
						var tableName = name.substring(name.lastIndexOf(".") + 1);
					}
					if(checked[tableName]) {
						alert("<xsl:value-of select="/document/props/list.message.generation.duplicate_table_name.error.msg" />");
						isError = true;
					} else {
						checked[tableName] = schemaName;
					}
				}
			}
		}
	}
	if(!isChecked) {
		alert("<xsl:value-of select="/document/props/list.message.generation.no_selected_table.error.msg" />");
	} else if(!isError) {
		document.getElementById("list_frm").action = "select";
		document.getElementById("list_frm").submit();
	}
}
</script>
</head>
<body>
<xsl:if test="/document/rows[@id='jndi']">
	<form style="float:left;" action="list">
		<select name="jndi">
			<xsl:for-each select="/document/rows[@id='jndi']/row">
				<xsl:choose>
					<xsl:when test="/document/params/param/jndi = name">
						<option>
							<xsl:attribute name="selected">selected</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="name" /></xsl:attribute>
							<xsl:value-of select="name" />
						</option>
					</xsl:when>
					<xsl:otherwise>
						<option>
							<xsl:attribute name="value"><xsl:value-of select="name" /></xsl:attribute>
							<xsl:value-of select="name" />
						</option>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</select>
		<input>
			<xsl:attribute name="type">submit</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/props/list.button.search.label" /></xsl:attribute>
		</input>
	</form>
</xsl:if>
<form action="list" style="float:right" method="post">
<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
<!--
<input type="button" value="Generation"  style="display:inline-block;float:right" onclick="gen()" />
-->
<input>
	<xsl:attribute name="type">button</xsl:attribute>
	<xsl:attribute name="style">display:inline-block;float:right</xsl:attribute>
	<xsl:attribute name="onclick">gen();</xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="/document/props/list.button.generation.label" /></xsl:attribute>
</input>



</form>
<form action="query" style="float:right">
<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
<input>
	<xsl:attribute name="type">submit</xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="/document/props/list.button.sql_runner.label" /></xsl:attribute>
</input>
<!--
<input type="submit" value="SQL Runner" />
-->
</form>
<form method="post" id="list_frm" action="list">
<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
<table style="width:100%;">
	<thead>
		<tr style="background-color:#788BBD;">
		
			<th />
			
			<th style="padding:8px;"><xsl:value-of select="/document/props/list.table.schema.label" /></th>
			<th style="width:100px;padding:10px;"><xsl:value-of select="/document/props/list.table.table_name.label" /></th>
			<th style="width:100px;padding:10px;"><xsl:value-of select="/document/props/list.table.type.label" /></th>
			<th style="padding:8px;"><xsl:value-of select="/document/props/list.table.comments.label" /></th>
			<th style="padding:8px;" colspan="2" />
		</tr>
	</thead>
	<tbody>
		<xsl:for-each select="/document/rows[@id='tables']/row">
		<tr class="tr_{position() mod 2}">
			<td>
				<input>
					<xsl:attribute name="type">checkbox</xsl:attribute>
					<xsl:choose>
					<xsl:when test="table_schem">
					<xsl:attribute name="name">check.<xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					<xsl:attribute name="name">check.<xsl:value-of select="table_name" /></xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
				</input>
			</td>
		
			<td style="padding:10px;">
				<xsl:value-of select="table_schem" />
			
			</td>
			<td style="padding:10px;">
				<xsl:value-of select="table_name" />
			</td>
			<td style="text-align:center;padding:10px;"><xsl:value-of select="table_type" /></td>
			<td style="text-align:center;padding:10px;">
				<input>
					<xsl:attribute name="type">text</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="remarks" /></xsl:attribute>
					<xsl:choose>
					<xsl:when test="table_schem">
					<xsl:attribute name="name">remarks.<xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
					<xsl:attribute name="name">remarks.<xsl:value-of select="table_name" /></xsl:attribute>
					</xsl:otherwise>
					</xsl:choose>
				</input>
			</td>
			<td style="text-align:center;padding:10px;">
				<a>
					<xsl:attribute name="href">table?table=<xsl:if test="table_schem"><xsl:value-of select="table_schem" />.</xsl:if><xsl:value-of select="table_name" /><xsl:if test="/document/params/param/jndi">&amp;jndi=<xsl:value-of select="/document/params/param/jndi" /></xsl:if></xsl:attribute>
					<xsl:value-of select="/document/props/list.table.table_info.label" />
				</a>
			</td>
			<td style="text-align:center;padding:10px;">
				<a>
					<xsl:attribute name="href">data?table=<xsl:if test="table_schem"><xsl:value-of select="table_schem" />.</xsl:if><xsl:value-of select="table_name" /><xsl:if test="/document/params/param/jndi">&amp;jndi=<xsl:value-of select="/document/params/param/jndi" /></xsl:if></xsl:attribute>
					<xsl:value-of select="/document/props/list.table.view_data.label" />
				</a>
			</td>
		</tr>
		</xsl:for-each>
	</tbody>
</table>
<input>
	<xsl:attribute name="type">submit</xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="/document/props/list.button.save_comments.label" /></xsl:attribute>
</input>
</form>
<ul>
	<li>DatabaseProductName : <xsl:value-of select="/document/params/param/name" /></li>
	<li>DatabaseProductVersion : <xsl:value-of select="/document/params/param/version" /></li>
	<li>UserName : <xsl:value-of select="/document/params/param/user" /></li>
	<li>DriverName : <xsl:value-of select="/document/params/param/driver" /></li>
	<li>DriverVersion : <xsl:value-of select="/document/params/param/driver_version" /></li>
</ul>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
