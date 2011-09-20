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
			if(document.getElementById("name").value.length<1){
				alert("版本名称必须填写!");return false;
			}
			if(document.getElementById("cate").value==0){
				alert("类别必须填写!");return false;
			}
			form.submit();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="teleplayUpdate.html" title="更新连续剧信息"  onsubmit="return formsubmit(this.form);" name="updateform">
		<s:hidden name="tuvo.tid"/>
		<s:textfield name="tuvo.keyword" label="电视剧名字"/>
		<s:doubleselect id="cate" label="分类" name="tuvo.tc.cate" list="tuvo.tc.viewbeancate" listKey="viewcatecode" listValue="viewcatename" doubleName="tuvo.tc.subcate" doubleList="tuvo.tc.viewbeansubcate.get(top.viewcatecode)" doubleListKey="viewcatecode" doubleListValue="viewcatename"   formName="updateform"/>
		<s:textfield name="tuvo.exclude" label="综艺排除词(多个以;隔开)"/>
		<s:textfield name="tuvo.version_count" label="版本数量"/>
		<s:radio name="tuvo.is_valid" list="#{'0':'无效','1':'有效'}" label="是否有效"/>
		<s:hidden name="tuvo.pid"/>
		<s:textfield name="tuvo.aliasStr" label="别名(多个以;隔开)"/>
		<s:hidden name="tuvo.idstr"/>
		<s:submit value="保存"/>
		<s:reset value="取消"/>
	</s:form>
	<input type="button" value="返回" onclick="javascript:history.go(-1);"/>
	</body>
</html>