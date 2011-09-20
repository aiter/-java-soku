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
        });
        </script>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="topViewLog.html" title="搜索上升最快">
			<s:textfield onfocus="setday(this)" name="date" id="date" label="日期（如为空，则日期取当天的前一天)(格式:yyyy_mm_dd)" />
			<s:submit value="上升最快"/>
	</s:form>
	</body>
</html>