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
		<table width="100%" border="0" align="center">
			<tr>
				<td><s:property value="date"/>统计结果：</td><td>视频首页比例:<FONT color="#FF0000"><s:property value="vt.totalRate1"/>%</FONT></td><td>前2页比例:<FONT color="#FF0000"><s:property value="vt.totalRate2"/>%</FONT></td><td>前3页比例:<FONT color="#FF0000"><s:property value="vt.totalRate3"/>%</FONT></td>
			</tr>
			<tr>
				<td colspan=4>说明:1<=n<=5,page(n)记为该搜索词第n页搜索次数,sum(n-)记为page(1)+page(2)+...+page(n-1),sum(n+)记为page(n+1)+page(n+2)+...+page(5),则比例1=page(n)/sum(n-),比例2=sum(n+)/sum(n-)</td>
			</tr>
		</table>
		<TABLE cellSpacing="0" cellPadding="0" width="100%" bgcolor="#999999"
			 align="center" title="统计" border="0">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center" width="25%">
					到达第二页最多的搜索词
				</TD>
				<TD align="center" width="25%">
					到达第三页最多的搜索词
				</TD>
				<TD align="center" width="25%">
					到达第四页最多的搜索词
				</TD>
				<TD align="center" width="25%">
					超过前四页的搜索词
				</TD>
			</TR>
			 <tr>
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="到达第二页最多的搜索词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					比例1
				</TD>
				<TD align="center">
					比例2
				</TD>
			</TR>
			<s:iterator value="vt.turnmap[2]" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="query_type"/>
					</td>
					<td>
					<s:property value="rate"/>%
					</td>
					<td>
					<s:property value="rate_remain"/>%
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			  <td  valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="到达第三页最多的搜索词" topmargin=0>
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					比例1
				</TD>
				<TD align="center">
					比例2
				</TD>
			</TR>
			<s:iterator value="vt.turnmap[3]" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="query_type"/>
					</td>
					<td>
					<s:property value="rate"/>%
					</td>
					<td>
					<s:property value="rate_remain"/>%
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			  <td  valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="到达第四页最多的搜索词" topmargin=0>
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					比例1
				</TD>
				<TD align="center">
					比例2
				</TD>
			</TR>
			<s:iterator value="vt.turnmap[4]" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="query_type"/>
					</td>
					<td>
					<s:property value="rate"/>%
					</td>
					<td>
					<s:property value="rate_remain"/>%
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			  <td  valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="超过前四页的搜索词" topmargin=0>
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					比例1
				</TD>
			</TR>
			<s:iterator value="vt.turnmap[5]" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="query_type"/>
					</td>
					<td>
					<s:property value="rate"/>%
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 </tr>
		</TABLE>
	</body>
</html>