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
	 function query(form)
	 {
		 form.action = "liquery.html";
	 	 form.submit();
	 }
	 function down(form,downdate)
	 {
		 form.action = "liqdown.html";
		 form.downdate.value = downdate;
	 	 form.submit();
	 }
	</script>
	
	 <script type="text/javascript" src="<s:url value="/console/images/jquery-1.3.2.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.core.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.dialog.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.datepicker.js"/>"></script>
        <link type="text/css" href="<s:url value="/console/images/ui.all.css"/>" rel="stylesheet" />
    	<link href="<s:url value="/console/images/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
        <script type="text/javascript">
        $(function() {
			$("#startdate").datepicker({dateFormat: 'yy_mm_dd'});
			$("#enddate").datepicker({dateFormat: 'yy_mm_dd'});
        });
        </script>
	<s:form action="">
		<table border="0" align="left"  width="100%">
			<tr>
				<td align="left" colspan=10>
					<FONT color="#FF0000"><s:property value="message"/></FONT>
				</td>
			</tr>
			<tr>
				<td  colspan=10>
					<b>日志查询(utf-8编码)</b>
				</td>
			</tr>
			<tr>
				<td align="left">
					开始日期:<s:textfield theme="simple" name="startdate" id="startdate" />
				</td>
				<td align="left">
					结束日期:<s:textfield theme="simple" name="enddate" id="enddate" />
				</td>
				<td  colspan=8 align="left" style="font-size:12px;overflow:hidden;white-space:nowrap">
					(格式:yyyy_mm_dd,空默认为当前日的前一天,只支持最近7天)
				</td>
			</tr>
			<tr>
				<td align="left">
					关键词:<s:textfield theme="simple" name="keywords"/>
				</td>
				<td  colspan=9 align="left" style="font-size:12px;overflow:hidden;white-space:nowrap">
					(只支持单个关键词,暂时不支持高级搜索)
				</td>
			</tr>
			<tr>
				<td align="center"  colspan=10>
					<input type="button" value="查  询" onclick="query(this.form)"/>
				</td>
			</tr><tr>
		<s:hidden name="downdate"/>
		<s:iterator value="infomaps">
		<td style="margintop:0;marginleft:0" align="left">
		<table cellSpacing="0" cellPadding="0" border="1" align="left" title='<s:property value="key"/>'>
			<tr>
				<td align="left">
					<Font color="red"><s:property value="key"/></Font>
				</td>
				<td align="left">
					<input type="button" value="下载全部" onclick="down(this.form,'<s:property value="key"/>')"/>
				</td>
			</tr>
			<tr>
				<td align="left" style="font-size:12px">
					搜索词
				</td>
				<td align="left" style="font-size:12px">
					搜索次数
				</td>
			</tr>
			<s:iterator value="value">
			<tr bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
				<td align="left"  style="font-size:12px;overflow:hidden;white-space:nowrap">
					<s:property value="keyword"/>
				</td>
				<td align="left" style="font-size:12px;">
					<s:property value="counts"/>
				</td>
			</tr>
			</s:iterator>
		</table>
		</td>
		</s:iterator>
		</tr></table>
	</s:form>
	</body>
</html>