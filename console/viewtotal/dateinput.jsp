<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<script type="text/javascript" src="<s:url value="/console/images/jquery-1.3.2.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.core.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.dialog.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.datepicker.js"/>"></script>
        <link type="text/css" href="<s:url value="/console/images/ui.all.css"/>" rel="stylesheet" />
    	<link href="<s:url value="/console/images/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
        <script type="text/javascript">
        $(function() {
			$("#date").datepicker({dateFormat: 'yy_mm_dd'});
			$("#date_start").datepicker({dateFormat: 'yy_mm_dd'});
			$("#date_end").datepicker({dateFormat: 'yy_mm_dd'});
			$("#date_remove").datepicker({dateFormat: 'yy_mm_dd'});
        });
        </script>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	
	<s:form action="dateviewtotal.html" title="单天查询">
			<s:textfield onfocus="setday(this)" name="date" id="date" label="日期（如为空，则日期取当天的前一天）(格式:yyyy_mm_dd)" />
			<s:submit value="查 询"/>
			<s:reset value="取 消"/>
	</s:form>
	
	<s:form action="dateviewtotalMulti.html" title="多天查询">
			<s:textfield onfocus="setday(this)" name="date_start" id="date_start" label="开始日期（如为空，则日期取当天的前一天)(格式:yyyy_mm_dd)" />
			<s:textfield onfocus="setday(this)" name="date_end" id="date_end" label="结束日期（如为空，则日期取当天的前一天）(格式:yyyy_mm_dd)" />
			<s:submit value="查 询"/>
			<s:reset value="取 消"/>
	</s:form>
	
	<s:form action="datecacheremove.html" title="删除缓存">
			<s:textfield onfocus="setday(this)" name="date_remove" id="date_remove" label="日期（如为空，表示全部清除）(格式:yyyy_mm_dd)" />
			<s:submit value="删除缓存"/>
	</s:form>
	</body>
</html>