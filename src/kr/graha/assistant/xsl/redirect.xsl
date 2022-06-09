<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes" version="5.0" omit-xml-declaration="no" />
<xsl:template match="/">
<xsl:if test="system-property('xsl:vendor') = 'Microsoft'"><xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text></xsl:if>
<html>
<head>
<meta charset="UTF-8" />
<title></title>
<meta http-equiv="Content-Language" content="Korean" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style>
body {
	width:100%;
	height:100%;
	margin:0;
}
div.wrapper {
	display:block;
	margin:0;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}
div.wrapper div.link {
	text-align:center;
}
div.wrapper div.link form {
	display:inline;
}
</style>
</head>
<body>
<div class="wrapper">
	<xsl:if test="count(/document/params/param[@type='f']/path) > 0">
		<h3>File Created</h3>
		<ul>
			<xsl:for-each select="/document/params/param[@type='f']/path">
				<li><xsl:value-of select="." /></li>
			</xsl:for-each>
		</ul>
	</xsl:if>
	<div class="link">
		<form method="get" id="_post" action="{/document/params/param[@type='s']/path}">
			<xsl:for-each select="/document/params/param[@type='r']/*">
				<input type="hidden" name="{name()}" value="{.}" />
			</xsl:for-each>
			<input>
				<xsl:attribute name="type">submit</xsl:attribute>
				<xsl:choose>
					<xsl:when test="not(/document/params/param[@type='s']/auto_redirect) or /document/params/param[@type='s']/auto_redirect != 'false'">
						<xsl:attribute name="value">Confirm</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="value">Table List</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</input>
		</form>
		<xsl:for-each select="/document/params/param[@type='a']/path">
			<form method="get" action="{.}">
				<xsl:for-each select="/document/params/param[@type='r']/*">
					<input type="hidden" name="{name()}" value="{.}" />
				</xsl:for-each>
				<input>
					<xsl:attribute name="type">submit</xsl:attribute>
					<xsl:attribute name="value">Go App</xsl:attribute>
				</input>
			</form>
		</xsl:for-each>
	</div>
</div>
<xsl:if test="not(/document/params/param[@type='s']/auto_redirect) or /document/params/param[@type='s']/auto_redirect != 'false'">
	<script>
if(document.getElementById("_post")) document.getElementById("_post").submit();
	</script>
</xsl:if>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
