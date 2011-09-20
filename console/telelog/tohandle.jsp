<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		
		function updateEpisode(form)
		{
			if(confirm("确认修改此剧集信息？")){
				form.action="tlEpisodeUpdate.html?episodeLogId="+'<s:property value="elvo.id"/>'+"&eid="+'<s:property value="elvo.fkEpisodeId"/>';
				form.submit();
			}
		}
		
		function deleteEpisode(form)
		{	
			if(confirm("确认删除此剧集信息？")){
				form.action="tlEpisodeDelete.html?episodeLogId="+'<s:property value="elvo.id"/>'+"&eid="+'<s:property value="elvo.fkEpisodeId"/>';
				form.submit();
			}
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="tlEpisodeUpdate.html">
	<s:hidden name="elvo.key"/><s:hidden name="elvo.status"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=3>定时剧集更新日志</td>
	</tr>
	<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td width="15%">
	<s:if test="!elvo.url.isEmpty()">
		<a href='<s:property value="elvo.url"/>' target="_blank"><s:property value="elvo.keyword"/></a>
	</s:if>
	<s:else><s:property value="elvo.keyword"/></s:else>
	</td>
	<td width="60%"><s:textfield name="elvo.url" theme="simple" size="95%"></s:textfield></td>
	<td width="25%"><input type="button" value="返回" onclick="javascript:history.go(-1);"/><input type="button" value="修改" onclick="updateEpisode(this.form)"/><input type="button" value="加入黑名单并删除" onclick="deleteEpisode(this.form)"/></td>
	</tr>
	</table>
	</s:form>
	</body>
</html>