<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body>
	 <script type="text/javascript">
	 function down(form)
	 {
		 form.action = "timequerydown.html";
	 	 form.submit();
	 }
	 function query(form)
	 {
		 form.action = "timequery.html";
	 	 form.submit();
	 }
	</script>
	<s:form action="">
		<s:hidden name="downdate"/>
		<s:hidden name="lastdate"/>
		<table border="0" align="left" width="100%" style="margintop:0;marginleft:0">
		<tr>
				<td align="left" colspan="6">
					<FONT color="#FF0000"><s:property value="message"/></FONT>
				</td>
			</tr>
			<tr>
				<td  colspan="6">
					<b>榜单搜索统计</b>
				</td>
			</tr>
			<tr>
				<td align="left" width="30%">
					<s:radio cssStyle="width:30px" theme="simple" name="isMonth" list="#{'2':'周','1':'月'}" label="时间跨度"></s:radio>
				</td>
				<td align="left" width="20%">
					返回数据数目:<s:textfield name="limit" theme="simple" />
				</td>
				<td align="left" width="20%">
					<input type="button" value="查询" onclick="query(this.form)"/>
				</td>
				<td align="left" width="30%" colspan="3">
				<s:if test="0!=isMonth" >
					<input type="button" value="导出到excel" onclick="down(this.form)"/>
				</s:if>
				</td>
			</tr>
		<tr style="margintop:0;marginleft:0">
		<s:if test="null!=qvomap">
		<s:iterator value="qvomap">
		<td style="margintop:0;marginleft:0" align="left">
		<table  width="15%" cellSpacing="0" cellPadding="0" border="1" align="left" title='<s:property value="key"/>'>
			<tr>
				<td align="left" colspan="3">
					<Font color="red"><s:property value="key"/></Font>
				</td>
			</tr>
			<tr>
				<td width="8%" align="left" style="font-size:12px;padding:3px">
					关键词
				</td>
				<td width="4%" align="left" style="font-size:12px;padding:3px">
					<s:property value="downdate"/>
				</td>
				<td width="3%" align="left" style="font-size:12px;padding:3px">
					<s:property value="lastdate"/>
				</td>
			</tr>
			<s:iterator value="value">
			<tr bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
				<td align="left"  style="font-size:12px;padding:3px;overflow:hidden;width:240px;white-space:nowrap">
					<s:property value="keyword"/>
				</td>
				<td align="left" style="font-size:12px;padding:3px">
					<s:property value="query_count1"/>
				</td>
				<td align="left" style="font-size:12px;padding:3px">
					<s:property value="query_count2"/>
				</td>
			</tr>
			</s:iterator>
		</table>
		</td>
		</s:iterator>
		</s:if>
		</tr></table>
	</s:form>
	</body>
</html>