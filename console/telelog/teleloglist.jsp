<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		
		function search(form)
		{
			var keyword=document.getElementById("name").value;
			var status=document.getElementById("status").value;
			form.action="tllist.html";
			form.submit();
		}
		
		function deleteTelelog(form,id)
		{	
			if(confirm("确认选定的剧集信息不存在问题？")){
				form.action="tlDelete.html?episodeLogId="+id;
				form.submit();
			}
		}
		
		function tohandle(form,fid)
		{
			form.action="tl2handle.html?episodeLogId="+fid;
			form.submit();
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="tllist">
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=7>定时剧集更新日志</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=7><s:textfield name="keyword" id="name" theme="simple" label="关键词"></s:textfield><s:select theme="simple" label="状态" id="status" name="status" list="#{'0':'未处理','1':'已删除','2':'已处理'}"></s:select><input type="button" value="搜  索" onclick="search(this.form)"/></td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td colspan=2>视频</td>
	<td colspan=3>创建时间</td>
	<td colspan=2>操作</td>
	</tr>
	<s:iterator value="elvos">
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td colspan=2  align="left">
	<s:if test="!url.isEmpty()&&status==0">
	<a href='<s:property value="url"/>' target="_blank"><s:property value="keyword"/></a>
	</s:if><s:else><s:property value="keyword"/></s:else>
	</td>
	<td colspan=3  align="left"><s:property value="createtime"/></td>
	<td colspan=2  align="center">
	<s:if test="status==0">
		<input type="button" value="删 除" onclick="deleteTelelog(this.form,<s:property value="id"/>)"/><input type="button" value="处 理" onclick="tohandle(this.form,<s:property value="id"/>,<s:property value="fkEpisodeId"/>)"/>
	</s:if>
	</td>
	</tr>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=2><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个剧集信息</td>
	<td><s:if test="page>1"><a href='tllist.html?page=1&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='tllist.html?page=<s:property value="page-1"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='tllist.html?page=<s:property value="page+1"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='tllist.html?page=<s:property value="maxpage"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='tllist.html?keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>&page=' + this.value;">
			<%
			int startpage=1;
			int maxpage=1;
			String check="";
			try{
			startpage=Integer.parseInt(""+request.getAttribute("cuurpage"));
			maxpage=Integer.parseInt(""+request.getAttribute("cuurmaxpage"));
			}catch(Exception e){startpage=1;}
			for(int i=1;i<=maxpage;i++){
			if(i==startpage)check="selected";
			else check="";
			%>
			<option value="<%=i %>" <%=check %>>第<%=i %>页</option>
			<% }%>
		</select>
	</td>
	</tr>
	<tr bgcolor="#A4D6FF">
	<td colspan=7>注:如果剧集无需修改或视频不存在请删除日志，如果存在问题，请点击处理按钮操作</td>
	</tr>
	</table>
	</s:form>
	</body>
</html>