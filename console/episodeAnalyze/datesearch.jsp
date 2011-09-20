<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		var rowNum = 0;
		function insertRow()
		{
		var table1 = document.getElementById("tbl");
		var tr1 = document.createElement("tr");
		
		for (var j=0;j<2; j++)
		{
		var td1 = document.createElement("td");
		if(j!=0)
		td1.colspan=2;
		var tn = document.createTextNode(rowNum);
		td1.appendChild(tn);
		tr1.appendChild(td1);
		}
		rowNum = rowNum + 1;
		tr1.childNodes[0].innerText="别名";
		tr1.childNodes[0].align="right";
		tr1.childNodes[1].innerHTML="<input type='text' name='aliases' value=''></input>";
		table1.childNodes[0].appendChild(tr1);
		}
		function formsubmit(form){
			if(document.getElementById("name").value.length<1){
				alert("电视名称必须填写!");return false;
			}
			if(document.getElementById("cate").value==0){
				alert("类别必须填写!");return false;
			}
			form.submit();
		}
		function formreset(form){
			form.reset();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	
	<s:form action="teleplaySave.html" title="添加连续剧"  name="saveform" id="saveform">
			<table id="tbl" width="100%">
			<tr>
			<td width="10%" align="right">名称</td>
			<td width="90%"><input width="90%" type="text" name="name" value="" id="name"/></td>
			</tr>
			
			<tr>
			<td width="10%" align="right">别名</td>
			<td width="90%"><input width="90%" type="text" name="aliases" value=""/><input width="10%" type="button" value="增加" onclick="insertRow()"/></td>
			</tr>
			</table>
			<table width="100%">
			<tr>
			<td width="10%" align="right">排除词</td>
			<td colspan=5 width="90%"><input type="text" name="excludes" value="" id="excludes"/>(暂时仅对综艺节目有用,多个用分号;分隔)</td>
			</tr>
			
			<tr>
			<td width="10%" align="right">分类</td>
			<td colspan=5 width="90%"><s:doubleselect id="cate" doubleId="subcate"  theme="simple" name="tc.cate" list="tc.viewbeancate" listKey="viewcatecode" listValue="viewcatename" doubleName="tc.subcate" doubleList="tc.viewbeansubcate.get(top.viewcatecode)" doubleListKey="viewcatecode" doubleListValue="viewcatename"   formName="saveform"/></td>
			</tr>
			
			<tr>
			<td width="10%"></td>
			<td width="15%" align="center">
			<input type="button" value="保存" onclick="formsubmit(this.form)"/>
			</td>
			<td width="15%" align="center">
			<input type="button" value="取消" onclick="formreset(this.form)"/>
			</td>
			<td width="15%" align="center">
			<input type="button" value="返回" onclick="javascript:history.go(-1);"/>
			</td>
			<td width="45%"></td>
			</tr>
			</table>
			
	</s:form>
	
	</body>
</html>