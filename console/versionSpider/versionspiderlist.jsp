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
			var name=document.getElementById("name").value;
			var status=document.getElementById("status").value;
			form.action="vslist.html";
			form.submit();
		}
		
		function updateVersionspider(form,id,s,k)
		{
			if(s==1){
				if(confirm("确认删除选定的新抓取电视剧版本？")){
					document.getElementById("del"+k).disabled=true;
					form.action="vspiderUpdate.html?sid="+id+"&sstatus="+s;
					form.submit();
				}
			}
			if(s==2){
				if(confirm("确认添加选定的电视剧版本？")){
					document.getElementById("go"+k).disabled=true;
					form.action="vspiderUpdate.html?sid="+id+"&sstatus="+s;
					form.submit();
				}
			}
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="vslist">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=8>新抓取的电视剧版本</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=8><s:textfield name="name" id="name" theme="simple" label="关键词"></s:textfield><s:select theme="simple" label="状态" id="status" name="status" list="#{'0':'未处理','1':'删除','2':'已通过'}"></s:select><input type="button" value="搜  索" onclick="search(this.form)"/></td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td colspan=2>电视剧</td>
	<td colspan=2>版本名称</td>
	<td colspan=2>修改时间</td>
	<td colspan=2>操作</td>
	</tr>
	<%int k=0; %>
	<s:iterator value="vsvos">
	<%k=k+1; %>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td colspan=2  align="left">
	<s:property value="teleName"/>
	</td>
	<td colspan=2  align="left">
	<s:property value="versionName"/>
	</td>
	<td colspan=2  align="left"><s:property value="update_time"/></td>
	<td colspan=2  align="center">
	<s:if test="status==0">
		<input id="del<%=k %>" type="button" value="删 除" onclick="updateVersionspider(this.form,<s:property value="id"/>,1,<%=k %>)"/><input id="go<%=k %>" type="button" value="通 过" onclick="updateVersionspider(this.form,<s:property value="id"/>,2,<%=k %>)"/>
	</s:if>
	<s:if test="status==1">
		<input id="go<%=k %>" type="button" value="通 过" onclick="updateVersionspider(this.form,<s:property value="id"/>,2,<%=k %>)"/>
	</s:if>
	</td>
	</tr>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=3><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个剧集信息</td>
	<td><s:if test="page>1"><a href='vslist.html?page=1&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='vslist.html?page=<s:property value="page-1"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='vslist.html?page=<s:property value="page+1"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='vslist.html?page=<s:property value="maxpage"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='vslist.html?name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>&page=' + this.value;">
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
	<td colspan=8>注:如果要新增此电视剧版本，请点击通过，否则请点击删除(请注意该电视剧是否存在此版本并检测后台是否存在类似的版本)</td>
	</tr>
	</table>
	</s:form>
	</body>
</html>