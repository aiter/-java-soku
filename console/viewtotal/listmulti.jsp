<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">

	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="0">
		<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" >
			<s:iterator value="vtlist" id="view">
				<TR align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td><a href="dateviewtotal.html?date=<s:property value="date"/>">
							<s:property value="date" />视频统计结果</a>：
					</td>
					<td>
						首页比例:
						<FONT color="#FF0000"><s:property value="totalRate1" />%</FONT>
					</td>
					<td>
						前2页比例:
						<FONT color="#FF0000"><s:property value="totalRate2" />%</FONT>
					</td>
					<td>
						前3页比例:
						<FONT color="#FF0000"><s:property value="totalRate3" />%</FONT>
					</td>
				</tr>
			</s:iterator>
		</table>
	</body>
</html>