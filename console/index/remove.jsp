<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript">
			function submitform(form,type)
			{	
				if(type==1){
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
				 else if(type==2){
				  form.action="indexupdate.html";
				 	if(confirm("确定更新索引？"))
				 	form.submit();
				 	else return false;
				 }
				 else{
				  form.action="indexcreate.html";
				 var order =document.getElementById("order").value;
				 if(confirm("确定重建机号:"+order+"的索引？"))
				 form.submit();
				 else return false;
				 }
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="0">
		<s:form name="remove" action=""><table>
		<tr><td align="left">索引号:<s:textfield theme="simple" name="indexid" id="indexid" required="true"></s:textfield></td></tr>
		<tr><td align="left"><input type="button" value="删 除" onclick="submitform(this.form,1)"/></td></tr>
		<tr><td align="left"><input type="button" value="更 新" onclick="submitform(this.form,2)"/></td></tr>
		<tr><td align="left">索引机器号:<s:select theme="simple" list="orders" name="order" id="order"></s:select></td></tr>
		<tr><td align="left"><input type="button" value="创 建" onclick="submitform(this.form,3)"/></td></tr>
	</table>
	<s:hidden name="typestr"/>
	</s:form>
	</body>
</html>