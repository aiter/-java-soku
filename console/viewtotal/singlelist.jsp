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
				<td><s:property value="date"/>统计结果</td>
			</tr>
		</table>
		<TABLE cellSpacing="1" cellPadding="0" width="100%" bgcolor="#999999"
			 align="center" title="统计搜索次数最多的词" border="0">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center" width="27%">
					总数
				</TD>
				<TD align="center" width="17%">
					page1总数
				</TD>
				<TD align="center" width="12%">
					视频
				</TD>
				<TD align="center" width="12%">
					专辑
				</TD>
				<TD align="center" width="10%">
					用户
				</TD>
				<TD align="center" width="10%">
					看吧
				</TD>
				<TD align="center" width="12%">
					视频高级检索
				</TD>
				<TD align="center" width="10%">
					专辑高级检索
				</TD>
			</TR>
			 <tr>
			  <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="搜索总数最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['total']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="type"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="第一页搜索总数最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['page1Total']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="type"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="视频搜索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['video']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="专辑搜索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['folder']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="用户搜索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['user']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="看吧搜索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['bar']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="视频高级检索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['advvideo']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 
			 <td valign="top">
			 <TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="专辑高级检索最多的词">
			 <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					数量
				</TD>
			</TR>
			<s:iterator value="sd.em['advfolder']" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:property value="keyword"/>
					</td>
					<td>
					<s:property value="counts"/>
					</td>
				</TR>
			</s:iterator>
			 </TABLE>
			 </td>
			 </tr>
		</TABLE>
	</body>
</html>