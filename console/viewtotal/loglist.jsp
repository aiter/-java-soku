<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="filelist.html">
		<table width="100%" border="0" align="center">
			<tr>
				<td align="left">
					<FONT color="#FF0000"><s:property value="message"/></FONT>
				</td>
			</tr>
			<tr>
				<td>
					<b>日志下载(utf-8编码)</b>
				</td>
			</tr>
		</table>
		<s:iterator value="downloadfiles">
		<p>
		<a href="filedown.html?filepath=<s:property value="filepath"/>"><s:property value="filename"/></a>
		</p>
		</s:iterator>
	</s:form>
	</body>
</html>