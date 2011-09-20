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
			if(name.length<1&&status==0){
				alert("请输入搜索关键词或者选择搜索状态!");return false;
			}
			form.action="teleplaySearch.html";
			form.submit();
		}
		function allsearch(form)
		{
			form.action="teleplaySearchAll.html";
			form.submit();
		}
		function add(form)
		{
			form.action="teleplayToSave.html";
			form.submit();
		}
		
		function updateTeleplay(form,id)
		{
			form.action="teleplayToUpdate.html?tid="+id;
			form.submit();
		}
		
		function deleteTeleplay(form,id)
		{	
			if(confirm("确认删除选定的电视剧（包括其所有版本）？")){
				form.action="teleplayDelete.html?tid="+id;
				form.submit();
			}
		}
		
		function addVersion(form,id)
		{
			form.action="teleplayVersionToSave.html?tid="+id;
			form.submit();
		}
		
		function updateVersion(form,id)
		{
			form.action="teleplayVersionToUpdate.html?pid="+id;
			form.submit();
		}
		
		function deleteVersion(form,tid,pid)
		{
			if(confirm("确认删除该版本？")){
				form.action="teleplayVersionDelete.html?tid="+tid+"&pid="+pid;
				form.submit();
			}
		}
		
		function getepisode(form,tid,pid,count,subcate)
		{
			form.action="teleplayEpisodeget.html?tid="+tid+"&pid="+pid+"&count="+count+"&subcate="+subcate;
			form.submit();
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="teleplaySearch.html">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%"  bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=8>剧集关键词列表</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=5><s:textfield name="name" id="name" theme="simple" ></s:textfield><s:select theme="simple" id="status" name="status" list="#{'0':'全部','1':'中间有空集','2':'未收录完','3':'已收录完','4':'没有剧集','5':'有效','6':'无效','7':'综艺节目'}"></s:select>
	<s:radio theme="simple" id="isPrecise" name="isPrecise" list="#{'0':'模糊匹配','1':'精准匹配'}"></s:radio>
	<input type="button" value="搜索" onclick="search(this.form)"/>
	</td>
	<td colspan=1><input type="button" value="全部搜索" onclick="allsearch(this.form)"/></td>
	<td colspan=2><input type="button" value="新增" onclick="add(this.form)"/></td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td>关键词</td>
	<td>别名</td>
	<td>状态</td>
	<td>类型</td>
	<td>版本</td>
	<td>总集数</td>
	<td>已收录集数</td>
	<td>操作</td>
	</tr>
	<s:iterator value="tp.sl">
	<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td><s:property value="keyword"/></td>
	<td><s:property value="aliasStr"/></td>
	<td><s:if test="1==isvalid">有效</s:if><s:else>无效</s:else></td>
	<td colspan=5><input type="button" value="编辑" onclick="updateTeleplay(this.form,<s:property value="id"/>)"/><input type="button" value="删除" onclick="deleteTeleplay(this.form,<s:property value="id"/>)"/><input type="button" value="添加版本" onclick="addVersion(this.form,<s:property value="id"/>)"/></td>
	</tr>
	<s:iterator value="svl">
	<tr align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td></td>
	<td></td>
	<td><s:if test="1==fixed">已收录完</s:if><s:else>未收录完</s:else></td>
	<td><s:property value="catestr"/>-<s:property value="subcatestr"/></td>
	<td><s:property value="versionname"/></td>
	<td><s:property value="total_Count"/></td>
	<td><s:property value="episode_count"/></td>
	<td><input type="button" value="编辑" onclick="updateVersion(this.form,<s:property value="pid"/>)"/><input type="button" value="删除" onclick="deleteVersion(this.form,<s:property value="id"/>,<s:property value="pid"/>)"/><input type="button" value="剧集搜索" onclick="getepisode(this.form,<s:property value="id"/>,<s:property value="pid"/>,<s:property value="total_Count"/>,<s:property value="subcate"/>)"/></td>
	</tr>
	</s:iterator>
	</s:iterator>
	
	<tr bgcolor="#A4D6FF">
	<td colspan=3><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="teleplaySize"/>部电视剧</td>
	<td><s:if test="page>1"><a href='<s:property value="fronturl"/>?name=<s:property value="encodename"/>&status=<s:property value="status"/>&isPrecise=<s:property value="isPrecise"/>&page=1'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='<s:property value="fronturl"/>?name=<s:property value="encodename"/>&status=<s:property value="status"/>&isPrecise=<s:property value="isPrecise"/>&page=<s:property value="page-1"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='<s:property value="fronturl"/>?name=<s:property value="encodename"/>&status=<s:property value="status"/>&isPrecise=<s:property value="isPrecise"/>&page=<s:property value="page+1"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='<s:property value="fronturl"/>?page=<s:property value="maxpage"/>&name=<s:property value="encodename"/>&status=<s:property value="status"/>&isPrecise=<s:property value="isPrecise"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='<s:property value="fronturl"/>?name=<s:property value="encodename"/>&status=<s:property value="status"/>&isPrecise=<s:property value="isPrecise"/>&page=' + this.value;">
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
	
	</table>
	</s:form>
	</body>
</html>