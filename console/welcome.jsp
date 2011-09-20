<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body  bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<table width="90%" align="center"  width="100%" bgcolor="#EEEEEE">
		<tr>
		<td width="20%">Welcome <s:property value="#session.user_name" />!</td>
		<td width="80%" align="right"> <a href="useredit.action" target="F">资料修改</a>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
		<td width="20%" valign="top" align="center"><table>
		<s:iterator value="ml" id="res">
		<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'" height="30"><td valign="top" align="center">
		<a href="<s:property  value="menuurl"/>" target="F" ><s:property  value="name"/></a>
		</td>
		</tr>
		</s:iterator>
		<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'" height="30"><td><a href="userlogout.action">退出</a></td></tr>
		</table>
		</td>
		<td width="80%">
		<iframe src="<s:property  value="firstUrl"/>" name="F" frameborder="0" height="600" width="100%" marginheight="0" marginwidth="0" scrolling="auto"></iframe>
		</td>
		</tr>
		</table>
	</body>
</html>