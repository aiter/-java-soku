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
				form.action="feedbackEpisodeUpdate.html?pkFeedbackId="+'<s:property value="fbhvo.pkFeedbackId"/>'+"&episodeId="+'<s:property value="fbhvo.evo.id"/>';
				form.submit();
			}
		}
		
		function deleteEpisode(form)
		{	
			if(confirm("确认删除此剧集信息？")){
				form.action="feedbackEpisodeDelete.html?pkFeedbackId="+'<s:property value="fbhvo.pkFeedbackId"/>'+"&episodeId="+'<s:property value="fbhvo.evo.id"/>';
				form.submit();
			}
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="feedbackEpisodeUpdate.html">
	<s:hidden name="fbhvo.keyword"/>
	<s:hidden name="fbhvo.page"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=3>反馈信息处理,反馈总数:<s:property value="fbhvo.total"/></td>
	</tr>
	<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td width="20%">
	<s:if test="!fbhvo.evo.url.isEmpty()">
		<a href='<s:property value="fbhvo.evo.url"/>' target="_blank"><s:property value="fbhvo.evo.name"/></a>
	</s:if>
	<s:else><s:property value="fbhvo.evo.name"/><s:hidden name="fbhvo.evo.name"/></s:else>
	</td>
	<td width="60%"><s:textfield name="fbhvo.evo.url" theme="simple" size="95%"></s:textfield></td>
	<td width="20%"><input type="button" value="返回" onclick="javascript:history.go(-1);"/><input type="button" value="修改" onclick="updateEpisode(this.form)"/><input type="button" value="加入黑名单并删除" onclick="deleteEpisode(this.form)"/></td>
	</tr>
	<tr bgcolor="#A4D6FF">
	<td>最后反馈时间</td>
	<td>反馈问题</td>
	<td>反馈人次</td>
	</tr>
	<s:iterator value="fbhvo.feedbackVo">
	
	<tr bgcolor="#A4D6FF">
	<td><s:property value="lastModefyDate"/></td>
	<td><s:property value="errorContent"/></td>
	<td><s:property value="feedbacknum"/></td>
	</tr>
	</s:iterator>
	</table>
	
	
	</s:form>
	</body>
</html>