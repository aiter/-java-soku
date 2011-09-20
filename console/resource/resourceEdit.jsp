<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<s:head theme="ajax" />
		<script language="javascript">
			function checkForm(form)
			{
				var name =document.getElementById("name").value;
				var description = document.getElementById("description").value;
				 if (name.length == 0 )
				{
					alert("资源名不能为空!");
					document.getElementById("name").focus();
				 	return false;
				 }
				if (description.length == 0 )
				{
					alert("资源描述不能为空!");
					document.getElementById("description").focus();
				 	return false;
				 }
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="resstore.html" onsubmit="return checkForm(this);">
		<s:textfield id="name" name="r.name" label="资源名" required="true"/>
		<s:textarea id="description" name="r.description" label="描述" required="true"/>
		<s:submit value="保 存"/>
		<s:reset value="取 消"/>
	</s:form>
	</body>
</html>