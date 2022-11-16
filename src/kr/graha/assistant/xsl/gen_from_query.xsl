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
<title><xsl:value-of select="/document/props/gen_from_query.document.title" /></title>
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
</style>
</head>
<body>
<h3><xsl:value-of select="/document/props/gen_from_query.document.title.label" /></h3>
<textarea name="gen" id="gen" style="width:100%;height:500px;"><xsl:value-of select="/document/rows[@id='gen']/row/gen" /></textarea>
<form method="get" action="query">
<xsl:if test="/document/params/param[@name='jndi']">
	<input>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param[@name='jndi']" /></xsl:attribute>
	</input>
</xsl:if>
	<div style="width:100%;text-align:center;">
		<input>
			<xsl:attribute name="type">submit</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/props/gen_from_query.button.sql_runner.label" /></xsl:attribute>
		</input>
	</div>
</form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
