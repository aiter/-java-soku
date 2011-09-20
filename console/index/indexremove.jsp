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
		<b>索引管理</b>
		<s:form action="indexremoveshow.html" target="FF">
		<s:radio list="#{'1':'视频','2':'专辑','3':'会员','5':'看吧帖子','7':'PK'}" onclick="javascript:this.form.submit();" id="type" name="type"></s:radio>
		</s:form>
		<iframe src="" name="FF" frameborder="0" height="220" width="100%" marginheight="0" marginwidth="0" scrolling="no"></iframe>
	</body>
</html>