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
		<s:property value="date" />上升最快,<FONT color="#FF0000">注意：以下简称日期<s:property value="date" />为日期a，其前一天为日期b.</FONT><br/>
		<FONT color="#00FF00">搜索总量上升最快:</FONT>
		<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="搜索总量上升最快">
			  <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					日期a搜索数量
				</TD>
				<TD align="center">
					日期a搜索结果
				</TD>
				<TD align="center">
					日期b搜索数量
				</TD>
				<TD align="center">
					日期b搜索结果
				</TD>
				<TD align="center">
					上升比例
				</TD>
			</TR>
			<s:iterator value="tqv.topviewmap['total']" id="view">
				<TR align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
						<s:property value="keyword" />
					</td>
					<td>
						<s:property value="query_type" />
					</td>
					<td>
						<s:property value="a_query_count" />
					</td>
					<td>
						<s:property value="a_result" />
					</td>
					<td>
						<s:property value="b_query_count" />
					</td>
					<td>
						<s:property value="b_result" />
					</td>
					<td>
						<s:property value="rate" />%
					</td>
				</tr>
			</s:iterator>
		</table>
		<!-- 
		<FONT color="#00FF00">搜索视频上升最快:</FONT>
		<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="搜索视频上升最快">
			  <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					日期a搜索数量
				</TD>
				<TD align="center">
					日期a搜索结果
				</TD>
				<TD align="center">
					日期b搜索数量
				</TD>
				<TD align="center">
					日期b搜索结果
				</TD>
				<TD align="center">
					上升比例
				</TD>
			</TR>
			<s:iterator value="tqv.topviewmap['video']" id="view">
				<TR align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
						<s:property value="keyword" />
					</td>
					<td>
						<s:property value="query_type" />
					</td>
					<td>
						<s:property value="a_query_count" />
					</td>
					<td>
						<s:property value="a_result" />
					</td>
					<td>
						<s:property value="b_query_count" />
					</td>
					<td>
						<s:property value="b_result" />
					</td>
					<td>
						<s:property value="rate" />%
					</td>
				</tr>
			</s:iterator>
		</table>
		<FONT color="#00FF00">搜索专辑上升最快:</FONT>
		<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center" title="搜索专辑上升最快">
			  <TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					搜索词
				</TD>
				<TD align="center">
					类型
				</TD>
				<TD align="center">
					日期a搜索数量
				</TD>
				<TD align="center">
					日期a搜索结果
				</TD>
				<TD align="center">
					日期b搜索数量
				</TD>
				<TD align="center">
					日期b搜索结果
				</TD>
				<TD align="center">
					上升比例
				</TD>
			</TR>
			<s:iterator value="tqv.topviewmap['folder']" id="view">
				<TR align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
						<s:property value="keyword" />
					</td>
					<td>
						<s:property value="query_type" />
					</td>
					<td>
						<s:property value="a_query_count" />
					</td>
					<td>
						<s:property value="a_result" />
					</td>
					<td>
						<s:property value="b_query_count" />
					</td>
					<td>
						<s:property value="b_result" />
					</td>
					<td>
						<s:property value="rate" />%
					</td>
				</tr>
			</s:iterator>
		</table>
		 -->
	</body>
</html>