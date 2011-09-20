<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
 <style>
	.nobr br{display:none}
</style>
		<script language="javascript" type="text/javascript">
		
		function formsubmit(form)
		{
			var cate=document.getElementById("cate").value;
			var subcate=document.getElementById("subcate").value;
			if(cate==0){
				alert("请选择关键词类型!");return;
			}
			window.returnValue=cate+"@"+subcate;
			window.close();
		}
		
		function formreset(form)
		{
			window.close();
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="" name="cateform" id="cateform">
	<table cellSpacing="0" cellPadding="3" width="100%" height="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td align="center">
	<div class="nobr">
	分类:<s:doubleselect id="cate" doubleId="subcate" label="分类" theme="simple" name="tc.cate" list="tc.viewbeancate" listKey="viewcatecode" listValue="viewcatename" doubleName="tc.subcate" doubleList="tc.viewbeansubcate.get(top.viewcatecode)" doubleListKey="viewcatecode" doubleListValue="viewcatename"   formName="cateform"/>
	</div>
	</td>
	</tr>
	<tr bgcolor="#A4D6FF">
	<td  align="center">
	</td>
	</tr>
	<tr bgcolor="#A4D6FF">
	<td  align="center">
		<input type="button" value="确定" onclick="formsubmit(this.form)"/>
		<input type="button" value="取消" onclick="formreset(this.form)"/>
	</td>
	</tr>
	</table>
	</s:form>
	</body>
</html>