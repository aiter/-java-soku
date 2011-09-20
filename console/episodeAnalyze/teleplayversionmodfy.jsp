<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		function formsubmit(form){
			if(document.getElementById("vname").value.length<1){
				alert("版本名称必须填写!");return false;
			}
			if(document.getElementById("cate").value==1){
				alert("类型必须选择!");return false;
			}
			form.submit();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:if test="0==sv.pid"><s:form action="teleplayVersionSave.html" title="添加版本" name="saveform" id="saveform" onsubmit="return formsubmit(this.form)">
	<s:hidden name="sv.fkTeleplayId"/>
	<s:textfield name="sv.versionname" id="vname" label="版本名字(注:不明确请勿填写,用于后台搜索)"/>
	<s:textfield name="sv.viewname" id="viewname" label="版本名字(注:不明确请勿填写,用于前台显示)"/>
	<s:doubleselect id="cate" label="分类" name="sv.cate" list="sv.viewbeancate" listKey="viewcatecode" listValue="viewcatename" doubleName="sv.subcate" doubleList="sv.viewbeansubcate.get(top.viewcatecode)" doubleListKey="viewcatecode" doubleListValue="viewcatename"   formName="saveform"/>
	<s:textfield name="sv.alias" label="版本别名(注:不明确请勿填写,多个用$$分开)"/>
	<s:textfield name="sv.orderId" label="排序号"/>
	<s:textfield name="sv.reverse" label="剧集是否反序（是为1）"/>
	<s:textfield name="sv.total_Count" label="总集数(注:不明确请填0)"/>
	<s:submit value="保存"/>
	<s:reset value="取消"/>
	</s:form>
	<input type="button" value="返回" onclick="javascript:history.go(-1);"/>
	</s:if>
	<s:else><s:form action="teleplayVersionUpdate.html" title="更新版本信息" name="updateform" id="updateform">
	<s:hidden name="sv.pid"/>
	<s:hidden name="sv.fkTeleplayId"/>
	<s:textfield name="sv.versionname" id="vname" label="版本名字(注:不明确请勿填写,用于后台搜索)"/>
	<s:textfield name="sv.viewname" id="viewname" label="版本名字(注:不明确请勿填写,用于前台显示)"/>
	<s:doubleselect id="cate" label="分类" name="sv.cate" list="sv.viewbeancate" listKey="viewcatecode" listValue="viewcatename" doubleName="sv.subcate" doubleList="sv.viewbeansubcate.get(top.viewcatecode)" doubleListKey="viewcatecode" doubleListValue="viewcatename"   formName="updateform"/>
	<s:textfield name="sv.alias" label="版本别名(注:不明确请勿填写,多个用$$分开)"/>
	<s:textfield name="sv.orderId" label="排序号"/>
	<s:textfield name="sv.reverse" label="剧集是否反序（是为1）"/>
	<s:textfield name="sv.total_Count" label="总集数(注:不明确请填0)"/>
	<s:radio name="sv.fixed" list="#{'0':'未完成','1':'已完成'}" label="是否收录完"/>
	<s:textfield name="sv.firstlogo" label="初始画面"/>
	<s:submit value="保存"/>
	<s:reset value="取消"/>
	</s:form>
	<input type="button" value="返回" onclick="javascript:history.go(-1);"/>
	</s:else>
	</body>
</html>