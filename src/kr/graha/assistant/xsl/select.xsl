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
<title>Select Master Table</title>
<meta http-equiv="Content-Language" content="Korean" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body, input, textarea, select, button, table, p, td, a {
	font-size:medium;
}
form {
	position:absolute;
	top:45%;
	left:50%;
	height:5em;
	margin-top:-2em;
	width:310px;
	margin-left:-155px;
	line-height:2em;
	border:none;
	color:#000;
	white-space: nowrap;
}
</style>
</head>
<body>
<form action="gen" method="post">
Master Table
<select name="table">
	<xsl:for-each select="/document/params/table">
		<option value="{@name}">
		<xsl:value-of select="@name" />
		</option>
	</xsl:for-each>
</select>
<input type="submit" value="Generation"/>

<xsl:for-each select="/document/params/table">
	<input type="hidden" name="tables" value="{@name}" />
</xsl:for-each>
</form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
