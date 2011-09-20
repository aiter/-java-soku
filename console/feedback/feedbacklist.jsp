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
			form.action="feedbacklist.html";
			form.submit();
		}
		
		function deleteFeedback(form,eid)
		{	
			if(confirm("确认删除选定的反馈信息？")){
				form.action="feedbackDelete.html?episodeId="+eid+"&page="+'<s:property value="page"/>';
				form.submit();
			}
		}
		
		function tohandle(form,fid,eid)
		{
			form.action="feedback2handle.html?pkFeedbackId="+fid+"&episodeId="+eid+"&page="+'<s:property value="page"/>';
			form.submit();
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="feedbacklist">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=8>反馈信息列表</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=8>关键词:<s:textfield name="keyword" id="name" theme="simple"></s:textfield>排序:<s:select theme="simple" name="order" list="#{'0':'反馈人次','1':'反馈时间'}"></s:select><input type="button" value="搜  索" onclick="search(this.form)"/></td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td>视频</td>
	<td colspan=3>问题</td>
	<td>反馈人次</td>
	<td>最后更新时间</td>
	<td colspan=2>操作</td>
	</tr>
	<s:iterator value="fbvos">
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td  align="left">
	<s:if test="!url.isEmpty()">
	<a href='<s:property value="url"/>' target="_blank"><s:property value="keyword"/></a>
	</s:if><s:else><s:property value="keyword"/></s:else>
	</td>
	<td colspan=3  align="left"><s:property value="errorContent"/></td>
	<td align="center"><s:property value="total"/></td>
	<td align="center"><s:property value="lastModefyDate"/></td>
	<td colspan=2  align="center">
		<input type="button" value="删除" onclick="deleteFeedback(this.form,<s:property value="episodeId"/>)"/><input type="button" value="处理" onclick="tohandle(this.form,<s:property value="id"/>,<s:property value="episodeId"/>)"/>
	</td>
	</tr>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=3><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个反馈信息</td>
	<td><s:if test="page>1"><a href='feedbacklist.html?page=1&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='feedbacklist.html?page=<s:property value="page-1"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='feedbacklist.html?page=<s:property value="page+1"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='feedbacklist.html?page=<s:property value="maxpage"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='feedbacklist.html?keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&page=' + this.value;">
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
	<td colspan=8>注:如果反馈信息不正确请删除，如果问题确实存在，请点击处理按钮操作</td>
	</tr>
	</table>
	</s:form>
	</body>
</html>