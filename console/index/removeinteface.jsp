<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript">
			function submitform(form)
			{
				var indexid = document.getElementById("indexid").value;
				if (indexid.length == 0 )
				{
					alert("索引号不能为空!");
					document.getElementById("indexid").focus();
					
				 	return false;
				 }
				 var re = /\D/;
				if(re.exec(indexid)!=null){
				alert("索引号为数字!");
				return false;
				}
				if(indexid>2147483647){
					alert("输入数字太大，索引号不得大于2147483647!");
					return false;
				}
				 form.action="indexremove.html";
				 if(confirm("确定删除索引号:"+indexid+"的索引？"))
				 form.submit();
				 else return false;
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="0">
		<s:form  action=""><table>
		<tr><td align="left"><s:textfield name="indexid" label="索引号" id="indexid" required="true"></s:textfield></td></tr>
		<tr><td align="center"><input type="button" value="删 除" onclick="submitform(this.form,1)"/></td></tr>
	</table>
	<s:hidden name="typestr"/>
	</s:form>
	</body>
</html>