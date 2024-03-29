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
<title><xsl:value-of select="/document/props/query.document.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body, input, textarea, select, button, table, p, td, a {
	font-family: Dotum,Helvetica,sans-serif;
	font-size:medium;
}
form.graha_xml_config input {
	float:right;
	margin-bottom:10px;
}
form.graha_xml_config textarea {
	display:none;
}
form#query input {
	float:left;
}
h1, h2, h3, h4, h5 {
	font-family: Dotum,Helvetica,sans-serif;
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
	background-color:#788BBD;

	text-align:center;
	color:#000000;

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

function ctrlenter(e) {
	if((e.keyCode == 13 || e.keyCode == 10) &amp;&amp; e.ctrlKey) {
		document.getElementById("query").submit();
		if(Storage) {
			localStorage.selectionStart = document.getElementById('sql').selectionStart;
			localStorage.selectionEnd = document.getElementById('sql').selectionEnd;
		}
	}
}
function setHistory(obj) {
	document.getElementById('sql').value = localStorage.getItem("sqlHistory" + obj.getAttribute("idx"));
}
function bodyload() {
	document.getElementById('sql').focus();
	document.getElementById('sql').setSelectionRange(localStorage.selectionStart, localStorage.selectionEnd);
	if(Storage) {
		if(document.getElementById('sql').value != null &amp;&amp; document.getElementById('sql').value != "" &amp;&amp; !document.getElementById("_error")) {
			var cnt = localStorage.sqlHistoryCount;
			if(cnt == null || cnt == "") {
				cnt = 0;
			}
			cnt++;
			localStorage.setItem("sqlHistory" + cnt, document.getElementById('sql').value);
			localStorage.sqlHistoryCount = cnt;
		}
		var txt = "";
		for(var i = localStorage.sqlHistoryCount; i > 0; i--) {
			if(localStorage.getItem("sqlHistory" + i) != null &amp;&amp; localStorage.getItem("sqlHistory" + i) != "") {
<![CDATA[
				txt += ("<li idx='" + i + "' ondblclick='setHistory(this)' style='white-space:pre-line;margin-top:10px;'>" + localStorage.getItem("sqlHistory" + i) + "</li>");
//]]>
			}
		}
		document.getElementById("_history").innerHTML = txt;
	}
}
function gen(obj) {
	document.getElementById("graha_xml_config").action = "options";
	document.getElementById("graha_xml_config").sql.value = document.getElementById("sql").value;
	document.getElementById("graha_xml_config").submit();
}
</script>
</head>
<body onload="bodyload()">
<form action="list" style="float:right">
<input>
	<xsl:attribute name="type">submit</xsl:attribute>
	<xsl:attribute name="style">display:inline-block;float:right</xsl:attribute>
	<xsl:attribute name="value"><xsl:value-of select="/document/props/query.button.table_list.label" /></xsl:attribute>
</input>

<xsl:if test="/document/params/param/jndi">
	<input>
		<xsl:attribute name="name">jndi</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
	</input>
</xsl:if>
</form>
<form style="width:100%;text-align:left;" method="post" id="query" action="query">
	<xsl:if test="/document/rows[@id='jndi']">
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
	</xsl:if>
	<textarea name="sql" style="width:100%;height:100px;" onkeypress="ctrlenter(event)" id="sql"><xsl:value-of select="/document/params/param/sql" /></textarea>
	<input>
		<xsl:attribute name="type">submit</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/props/query.button.execute.label" /></xsl:attribute>
	</input>
</form>
<form class="graha_xml_config" id="graha_xml_config" method="post">
<xsl:if test="/document/rows[@id='count']/row or /document/rows[@id='data']/row or /document/rows[@id='meta']/row">
	<xsl:if test="/document/rows[@id='count']/row">
		<input>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="name">updated</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/rows[@id='count']/row/count" /></xsl:attribute>
		</input>
	</xsl:if>
	<xsl:if test="/document/rows[@id='data']/row or /document/rows[@id='meta']/row">
		<input>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="name">fetched</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="count(/document/rows[@id='data']/row)" /></xsl:attribute>
		</input>
		<xsl:if test="/document/rows[@id='meta']/row">
			<xsl:for-each select="/document/rows[@id='meta']/row/*">
				<input>
					<xsl:attribute name="type">hidden</xsl:attribute>
					<xsl:attribute name="name">column.<xsl:value-of select="position()" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute>
				</input>
			</xsl:for-each>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/document/params/param/jndi">
		<input>
			<xsl:attribute name="name">jndi</xsl:attribute>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
		</input>
	</xsl:if>
	<input>
		<xsl:attribute name="name">sql</xsl:attribute>
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/params/param/sql" /></xsl:attribute>
	</input>
	<input>
		<xsl:attribute name="type">button</xsl:attribute>
		<xsl:attribute name="onclick">gen(this)</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="/document/props/query.button.generation.label" /></xsl:attribute>
	</input>
</xsl:if>
</form>
<div style="clear:both;" />
<xsl:if test="/document/errors/error">
	<table style="width:100%;" id="_error">
		<tr>
			<th>Message</th>
			<td style="white-space:pre-line;"><xsl:value-of select="/document/errors/error/message" /></td>
		</tr>
		<tr>
			<th>Error Code</th>
			<td style="white-space:pre-line;"><xsl:value-of select="/document/errors/error/error_code" /></td>
		</tr>
		<tr>
			<th>SQL State</th>
			<td style="white-space:pre-line;"><xsl:value-of select="/document/errors/error/sql_state" /></td>
		</tr>
	</table>
</xsl:if>
<xsl:if test="/document/rows[@id='count']/row">
	<table style="width:100%;" id="_update_count">
		<tr>
			<th style="width:150px">Update Count</th>
			<td style="white-space:pre-line;"><xsl:value-of select="/document/rows[@id='count']/row/count" /></td>
		</tr>
	</table>
</xsl:if>
<xsl:if test="/document/rows[@id='data']/row or /document/rows[@id='meta']/row">
	<table style="width:100%;" id="_data">
		<xsl:if test="/document/rows[@id='meta']/row">
		<thead>
			<tr style="background-color:#788BBD;">
				<xsl:for-each select="/document/rows[@id='meta']/row/*">
					<th style="padding:8px;">
						<xsl:value-of select="." />
					</th>
				</xsl:for-each>
			</tr>
		</thead>
		</xsl:if>
		<xsl:if test="/document/rows[@id='data']/row">
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
		</xsl:if>
	</table>
</xsl:if>
<ul>
	<li>DatabaseProductName : <xsl:value-of select="/document/params/param/name" /></li>
	<li>DatabaseProductVersion : <xsl:value-of select="/document/params/param/version" /></li>
	<li>UserName : <xsl:value-of select="/document/params/param/user" /></li>
	<li>Schema : <xsl:value-of select="/document/params/param/schema" /></li>
	<li>DriverName : <xsl:value-of select="/document/params/param/driver" /></li>
	<li>DriverVersion : <xsl:value-of select="/document/params/param/driver_version" /></li>
	
	<li>JDBCMajorVersion : <xsl:value-of select="/document/params/param/jdbcmajorversion" /></li>
	<li>JDBCMinorVersion : <xsl:value-of select="/document/params/param/jdbcminorversion" /></li>
</ul>
<ul id="_history" />
</body>
</html>
</xsl:template>
</xsl:stylesheet>
