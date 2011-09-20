<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		
		function update(form)
		{
			form.action="synonymRestore.html";
			form.submit();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="synonymRestore.html">
	<s:hidden name="synonym.id"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=3>同义词(用分号;分隔开)</td>
	</tr>
	<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td width="100%" align="left" colspan=3>
	<s:textfield maxlength="255" size="100%" name="synonym.keywords" theme="simple"/>
	</td>
	</tr>
	<tr bgcolor="#A4D6FF">
	<td width="5%"></td>
	<td align="center"><input type="button" value="保存" onclick="update(this.form)"/></td>
	<td align="left"><input type="button" value="返回" onclick="javascript:history.go(-1);"/></td>
	</tr>
	</table>
	</s:form>
	</body>
</html>