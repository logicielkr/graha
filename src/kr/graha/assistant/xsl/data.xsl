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
<title><xsl:value-of select="/document/props/data.document.title" /></title>
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
</head>
<body>
<form style="float:left;" action="data">
	<select name="table">
		<xsl:for-each select="/document/rows[@id='tables']/row">
			<xsl:choose>
				<xsl:when test="/document/params/param/table = table_name and (not(table_schem) or /document/params/param/table_schem = table_schem)">
					<option>
						<xsl:attribute name="selected">selected</xsl:attribute>
						<xsl:choose>
						<xsl:when test="table_schem">
						<xsl:attribute name="value"><xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						<xsl:attribute name="value"><xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="table_name" />
						 <xsl:if test="remarks">
						(<xsl:value-of select="remarks" />)
						</xsl:if>
					</option>
				</xsl:when>
				<xsl:otherwise>
					<option>
					<xsl:choose>
						<xsl:when test="table_schem">
						<xsl:attribute name="value"><xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						<xsl:attribute name="value"><xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="table_name" />
					 <xsl:if test="remarks">
						(<xsl:value-of select="remarks" />)
					</xsl:if>
					</option>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</select>
	<xsl:if test="/document/params/param/jndi">
		<input>
			<xsl:attribute name="name">jndi</xsl:attribute>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
		</input>
	</xsl:if>
	<input>
		<xsl:attribute name="type">submit</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/props/data.button.search.label" /></xsl:attribute>
	</input>
</form>
<form action="list" style="float:right">
<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
<input>
	<xsl:attribute name="type">submit</xsl:attribute>
	<xsl:attribute name="style">display:inline-block;float:right</xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="/document/props/data.button.table_list.label" /></xsl:attribute>
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
	<xsl:attribute name="value"><xsl:value-of select="/document/props/data.button.sql_runner.label" /></xsl:attribute>
</input>
</form>
<table style="width:100%;">
	<thead>
		<tr style="background-color:#788BBD;">
			<xsl:for-each select="/document/rows[@id='data']/row[position() = 1]/*">
				<th style="padding:8px;">
					<xsl:value-of select="name()" />
				</th>
			</xsl:for-each>
		</tr>
	</thead>
	<tbody>
		<xsl:for-each select="/document/rows[@id='data']/row">
		<tr class="tr_{position() mod 2}">
			<xsl:for-each select="*">
				<td style="padding:10px;">
					<xsl:value-of select="."/>
				</td>
			</xsl:for-each>
		</tr>
		</xsl:for-each>
	</tbody>
</table>
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
