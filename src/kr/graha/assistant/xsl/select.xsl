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

table.tables th.relation,
table.tables td.relation,
table.tables th.header_position,
table.tables td.header_position {
	text-align:center;
	width:200px;
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
function changeMasterTable(obj) {
	var masterTableName = obj.options[obj.options.selectedIndex].value;
	var list = getList("select", "relation_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "relation_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("select", "header_position_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "header_position_" + masterTableName) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("input", "list_view_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_view_column_" + masterTableName + "___")) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
	list = getList("input", "list_link_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name.indexOf("list_link_column_" + masterTableName + "___")) {
			list[i].disabled = true;
		} else {
			list[i].disabled = false;
		}
	}
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
function changeRelation(obj) {
	var selectedValue = obj.options[obj.options.selectedIndex].value;
	var tableName = obj.name.substring("relation_".length);
	var list = getList("select", "header_position_select");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "header_position_" + tableName) {
			if(selectedValue == "1") {
				list[i].disabled = true;
			} else if(selectedValue == "many") {
				list[i].disabled = false;
			}
		}
		
	}
}
function clickListView(obj) {
	var tableAndColumnName = obj.name.substring("list_view_column_".length);
	
	var list = getList("input", "list_link_column");
	for(var i = 0; i &lt; list.length; i++) {
		if(list[i].name == "list_link_column_" + tableAndColumnName) {
			if(obj.checked) {
				list[i].disabled = false;
			} else {
				list[i].disabled = true;
			}
		}
	}

}
</script>
</head>
<body>
<form action="gen" method="post" class="conf">
	<xsl:for-each select="/document/params/table">
		<input type="hidden" name="tables" value="{@name}" />
	</xsl:for-each>
	<xsl:if test="/document/params/param/jndi">
		<input>
			<xsl:attribute name="name">jndi</xsl:attribute>
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="/document/params/param/jndi" /></xsl:attribute>
		</input>
	</xsl:if>
	<h3 class="basic">1. Basic</h3>
	<table class="basic">
		<tr>
			<th>Master Table</th>
			<td>
				<select name="table" onChange="changeMasterTable(this)">
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
			<th>File Upload</th>
			<td>
				<input type="checkbox" name="file_upload" value="true" />
			</td>
		</tr>
		<tr>
			<th>Authentication</th>
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
										<xsl:when test="@name = /document/params/prop/owner_column">
											<option value="{@name}" selected="selected"><xsl:value-of select="@name" /></option>
										</xsl:when>
										<xsl:otherwise>
											<option value="{@name}"><xsl:value-of select="@name" /></option>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:if>
							</xsl:for-each>
						</select>
						* Select Authentication Column
				</td>
			</tr>
		</xsl:for-each>
	</table>
	<xsl:variable name="master_table_name"><xsl:value-of select="/document/params/table[@master = 'true']/@name" /></xsl:variable>
	<xsl:if test="count(/document/params/table) > 1">
		<h3 class="tables">2. Tables</h3>
		<table class="tables">
			<tr>
				<th class="table_name">Table Name</th>
				<th class="relation">Relation</th>
				<th class="header_position">Header Position(Detail/Insert)</th>
			</tr>
			<xsl:for-each select="/document/params/table">
				<xsl:sort select="@master" order="descending" />
				<tr>
					<td class="table_name"><xsl:value-of select="@name" /></td>
					<td class="relation">
						<xsl:choose>
							<xsl:when test="@name != $master_table_name">
								<select name="relation_{@name}" class="relation_select" onchange="changeRelation(this)">
									<option value="1">1-1</option>
									<option value="many" selected="selected">1-many</option>
								</select>
							</xsl:when>
							<xsl:otherwise>
								<select name="relation_{@name}" disabled="disabled" class="relation_select" onchange="changeRelation(this)">
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
				</tr>
			</xsl:for-each>
		</table>
	</xsl:if>
	
	<h3 class="view"><xsl:choose><xsl:when test="count(/document/params/table) > 1">3</xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>. View</h3>
	<xsl:for-each select="/document/params/table">
		<xsl:sort select="@master" order="descending" />
		<xsl:variable name="table_name"><xsl:value-of select="@name" /></xsl:variable>
		<h4 class="view {@name}"><xsl:choose><xsl:when test="count(/document/params/table) > 1">3</xsl:when><xsl:otherwise>2</xsl:otherwise></xsl:choose>.<xsl:value-of select="position()" />. <xsl:value-of select="@name" /></h4>
		<table class="view {@name}">
			<tr>
				<th class="column_name" rowspan="2">Column Name</th>
				<th class="comments" rowspan="2">Comments</th>
				<th class="graha_data_type" rowspan="2">Graha Data Type</th>
				<th class="data_type_name" rowspan="2">Data Type Name</th>
				<th class="list" colspan="2">List</th>
				<th class="detail">Detail</th>
				<th class="insert">Insert/Update</th>
				<th class="format" rowspan="2">Format</th>
			</tr>
			<tr>
				<th class="view">View</th>
				<th class="link">Link</th>
				<th class="view">View</th>
				<th class="view">View</th>
			</tr>
			<xsl:for-each select="column">
				<tr>
					<td class="column_name"><xsl:value-of select="@name" /></td>
					<td class="comments"><xsl:value-of select="." /></td>
					<td class="graha_data_type"><xsl:value-of select="@graha_data_type" /></td>
					<td class="data_type_name"><xsl:value-of select="@data_type_name" /></td>
					<td class="view">
						<xsl:choose>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text' and $table_name = $master_table_name">
								<input type="checkbox" name="list_view_column_{$table_name}___{@name}" class="list_view_column" value="{@name}" checked="checked" onClick="clickListView(this)" />
							</xsl:when>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text'">
								<input type="checkbox" name="list_view_column_{$table_name}___{@name}" class="list_view_column" value="{@name}" checked="checked" disabled="disabled" onClick="clickListView(this)" />
							</xsl:when>
							<xsl:when test="$table_name = $master_table_name">
								<input type="checkbox" name="list_view_column_{$table_name}___{@name}" class="list_view_column" value="{@name}" onClick="clickListView(this)" />
							</xsl:when>
							<xsl:otherwise>
								<input type="checkbox" name="list_view_column_{$table_name}___{@name}" class="list_view_column" value="{@name}" disabled="disabled" onClick="clickListView(this)" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="link">
						<xsl:choose>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text' and $table_name = $master_table_name">
								<input type="checkbox" name="list_link_column_{$table_name}___{@name}" class="list_link_column" value="{@name}" checked="checked" />
							</xsl:when>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false' and @data_type_name != 'text'">
								<input type="checkbox" name="list_link_column_{$table_name}___{@name}" class="list_link_column" value="{@name}" checked="checked" disabled="disabled" />
							</xsl:when>
							<xsl:when test="$table_name = $master_table_name">
								<input type="checkbox" name="list_link_column_{$table_name}___{@name}" class="list_link_column" value="{@name}" disabled="disabled" />
							</xsl:when>
							<xsl:otherwise>
								<input type="checkbox" name="list_link_column_{$table_name}___{@name}" class="list_link_column" value="{@name}" disabled="disabled" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="view">
						<xsl:choose>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false'">
								<input type="checkbox" name="detail_view_column_{$table_name}___{@name}" value="{@name}" checked="checked" />
							</xsl:when>
							<xsl:otherwise>
								<input type="checkbox" name="detail_view_column_{$table_name}___{@name}" value="{@name}" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="view">
						<xsl:choose>
							<xsl:when test="@def = 'false' and @pk = 'false' and @fk = 'false'">
								<input type="checkbox" name="insert_view_column_{$table_name}___{@name}" value="{@name}" checked="checked" />
							</xsl:when>
							<xsl:otherwise>
								<input type="checkbox" name="insert_view_column_{$table_name}___{@name}" value="{@name}" disabled="disabled" />
							</xsl:otherwise>
						</xsl:choose>
					</td>
					<td class="format">
						<xsl:if test="@graha_data_type = 'int' or @graha_data_type = 'long' or @graha_data_type = 'float' or @graha_data_type = 'double'">
							<xsl:if test="@def = 'false' and @pk = 'false' and @fk = 'false'">
								<select name="format_column_{$table_name}___{@name}">
									<option value="">N/A</option>
									<option value="ts">T/S</option>
								</select>
							</xsl:if>
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:for-each>
	<input type="submit" value="Generation"/>
</form>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
