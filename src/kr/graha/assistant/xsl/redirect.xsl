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
</head>
<body>
<form method="get" style="display:inline;" id="_post" action="{/document/params/param[@type='s']/path}">
<xsl:for-each select="/document/params/param[@type='r']/*">
	<input type="hidden" name="{name()}" value="{.}" />
</xsl:for-each>
<input type="submit" value="Confirm" />
</form>
<script>
if(document.getElementById("_post")) document.getElementById("_post").submit();
</script>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
