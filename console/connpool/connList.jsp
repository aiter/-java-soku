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
		<table width="100%" border="0" align="center">
			<tr>
				<td>
					<b>连接池信息列表</b>
				</td>
			</tr>
		</table>
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
			<TR align="center" bgcolor="#999999">
				<TD width="60%" align="center" colspan=2>
					连接总数:<s:property value="cinfo.total['idle']+cinfo.total['active']" />
				</TD>
				<TD width="20%" align="center">
					空闲:<s:property value="cinfo.total['idle']" />
				</TD>
				<TD width="20%" align="center">
					活动:<s:property value="cinfo.total['active']"/>
				</TD>
			</TR>
			<TR align="center" bgcolor="#A4D6FF">
				<TD width="40%" align="center">
					ip地址
				</TD>
				<TD width="20%" align="center">
					端口号
				</TD>
				<TD width="20%" align="center">
					空闲连接
				</TD>
				<TD width="20%" align="center">
					活动连接
				</TD>
			</TR>
			<s:iterator value="cinfo.detail" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						<s:property value="ip" />
					</TD>
					<TD>
						<s:property value="port" />
					</TD>
					<TD>
						<s:property value="status['idle']" />
					</TD>
					<TD>
						<s:property value="status['active']" />
					</TD>
				</TR>
			</s:iterator>
		</TABLE>
		<div align="right">
		服务器： <select name="no" onchange="location.href='conlist.html?pageno=' + this.value;">
			<%
			String[] ips;;
			int startpage=0;
			String check="";
			try{
			ips=(String[])request.getAttribute("ips");
			}catch(Exception e){
			ips=null;}
			try{
			startpage=Integer.parseInt(""+request.getAttribute("startpage"));
			}catch(Exception e){startpage=0;}
			for(int i=0;i<ips.length;i++){
			if(i==startpage)check="selected";
			else check="";
			%>
			<option value="<%=i %>" <%=check %>><%=ips[i] %></option>
			<% }%>
		</select></div>
	</body>
</html>