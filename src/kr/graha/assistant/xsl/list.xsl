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
<title>Table List</title>
<meta http-equiv="Content-Language" content="Korean" />
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
	var isSubmit = false;
	if(list != null) {
		for(var i = 0; i &lt; list.length; i++) {
			if(list[i].checked) {
				document.getElementById("list_frm").action = "select";
				document.getElementById("list_frm").submit();
				isSubmit = true;
			}
		}
	}
	if(!isSubmit) {
		alert("No Selected Table!!!");
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
		<input type="submit" value="Search" />
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
<input type="button" value="Generation"  style="display:inline-block;float:right" onclick="gen()" />
</form>
<form action="query" style="float:right">
<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
<input type="submit" value="SQL Runner" />
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
			
			<th style="padding:8px;">Schema</th>
			<th style="width:100px;padding:10px;">Table Name</th>
			<th style="width:100px;padding:10px;">Type</th>
			<th style="padding:8px;">Comments</th>
			<th style="padding:8px;" />
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
				<form action="table" style="display:inline-block">
					<input>
						<xsl:attribute name="type">hidden</xsl:attribute>
						<xsl:attribute name="name">table</xsl:attribute>
						<xsl:choose>
						<xsl:when test="table_schem">
						<xsl:attribute name="value"><xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						<xsl:attribute name="value"><xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:otherwise>
						</xsl:choose>
					</input>
					<xsl:if test="/document/params/param/jndi">
						<input>
							<xsl:attribute name="name">jndi</xsl:attribute>
							<xsl:attribute name="type">hidden</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
						</input>
					</xsl:if>
					<input type="submit" value="Table Info" />
				</form>
				<form action="data" style="display:inline-block">
					<input>
						<xsl:attribute name="type">hidden</xsl:attribute>
						<xsl:attribute name="name">table</xsl:attribute>
						<xsl:choose>
						<xsl:when test="table_schem">
						<xsl:attribute name="value"><xsl:value-of select="table_schem" />.<xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
						<xsl:attribute name="value"><xsl:value-of select="table_name" /></xsl:attribute>
						</xsl:otherwise>
						</xsl:choose>
					</input>
					<xsl:if test="/document/params/param/jndi">
						<input>
							<xsl:attribute name="name">jndi</xsl:attribute>
							<xsl:attribute name="type">hidden</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
						</input>
					</xsl:if>
					<input type="submit" value="View Data" />
				</form>
			</td>
		</tr>
		</xsl:for-each>
	</tbody>
</table>
<input type="submit" value="Save Comments" />
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
