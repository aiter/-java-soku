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
				var menuurl =document.getElementById("menuurl").value;
				var description = document.getElementById("module").value;
				 if (name.length == 0 )
				{
					alert("菜单名不能为空!");
					document.getElementById("name").focus();
				 	return false;
				 }
				if (description.length == 0 )
				{
					alert("菜单模块不能为空!");
					document.getElementById("module").focus();
				 	return false;
				 }
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="menustore.html" onsubmit="return checkForm(this);">
		<s:textfield id="name" name="m.name" label="菜单名" required="true"/>
		<s:if test="-1==m.fatherId"><s:textfield id="menuurl" name="m.menuurl" label="URL" /></s:if>
		<<s:else><s:textfield id="menuurl" name="m.menuurl" label="URL" required="true"/></s:else>
		<s:textarea id="module" name="m.module" label="模块" required="true"/>
		<s:hidden name="m.fatherId"/>
		<s:submit value="保 存"/>
		<s:reset value="取 消"/>
	</s:form>
	</body>
</html>