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
	 <script type="text/javascript">
	 function query(form)
	 {
		 form.action = "lquery.html";
	 	 form.submit();
	 }
	 function down(form)
	 {
		 form.action = "lqdown.html";
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
			$("#logdate").datepicker({dateFormat: 'yy_mm_dd'});
        });
        </script>
	<s:form action="">
		<table cellSpacing="1" cellPadding="3" width="100%" border="0" align="center">
			<tr>
				<td align="left">
					<FONT color="#FF0000"><s:property value="message"/></FONT>
				</td>
			</tr>
			<tr>
				<td>
					<b>日志查询(utf-8编码)</b>
				</td>
			</tr>
		</table>
		<table width="100%" border="0" align="center">
			<tr>
				<td align="left">
					数据日期:
				</td>
				<td align="left">
					<s:textfield theme="simple" name="logdate" id="logdate" />
				</td>
				<td align="left">
					(格式:yyyy_mm_dd,空默认为当前日的前一天,只支持最近30天)
				</td>
			</tr>
			<tr>
				<td align="left">
					关键词:
				</td>
				<td align="left">
					<s:textfield theme="simple" name="keywords"/>
				</td>
				<td align="left">
					(多个词同时存在用+号链接，多个词存在一个及以上用|分隔，不支持+号和竖线同时存在)
				</td>
			</tr>
			<tr>
				<td align="left">
					查询类型:
				</td>
				<td align="left">
					<s:checkboxlist theme="simple" name="types" list="#{'video':'视频','folder':'专辑','user':'空间','bar':'看吧'}"/>
				</td>
				<td align="left">
					(不选择即忽略分类，包括但不限于以上分类)
				</td>
			</tr>
			<tr>
				<td align="center">
					<input type="button" value="查  询" onclick="query(this.form)"/>
				</td>
				<td colspan=2>
					<!-- 当日期为<font color="red">前一天</font>，关键词为<font color="red">单个</font>，类型为<font color="red">视频</font>时，搜索不经过数据库，<font color="red">速度快</font> -->
				</td>
			</tr>
		</table>
		<s:if test="rssize>0">
		<s:hidden name="filename"/>
		<table width="100%" cellSpacing="1" cellPadding="3" border="0" align="center">
			<tr>
				<td align="left">
				</td>
				<td align="left">
					<input type="button" value="下载全部" onclick="down(this.form)"/>
				</td>
			</tr>
			<tr>
				<td align="left">
					搜索词
				</td>
				<td align="left">
					搜索次数
				</td>
			</tr>
			<s:iterator value="es">
			<tr bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
				<td align="left">
					<s:property value="keyword"/>
				</td>
				<td align="left">
					<s:property value="counts"/>
				</td>
			</tr>
			</s:iterator>
		</table>
		</s:if>
	</s:form>
	</body>
</html>