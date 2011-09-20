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
			form.action="tslist.html";
			form.submit();
		}
		
		function updateTelespider(form,id,s,k)
		{	
			if(s==1){
				if(confirm("确认删除选定的电视剧？")){
					document.getElementById("del"+k).disabled=true;
					form.action="tspiderUpdate.html?sid="+id+"&sstatus="+s;
					form.submit();
				}
			}
			if(s==2){
				if(confirm("确认添加选定的电视剧？")){
					document.getElementById("go"+k).disabled=true;
					var features = "dialogWidth:"+300+"px;dialogHeight:"+200+"px; scroll:0; help:0; status:yes; fullscreen;";
					features +=	"dialogLeft:500;dialogTop:200";
					var lvs = window.showModalDialog("tspiderSelect.html",[window],features);
					if(null!=lvs&&lvs.length>0&&lvs!="undefined"){
						var telecate =lvs.split("@");
						form.action="tspiderUpdate.html?sid="+id+"&sstatus="+s+"&cate="+telecate[0]+"&subcate="+telecate[1];
						//form.action="tspiderUpdate.html?sid="+id+"&sstatus="+s+"&cate="+0+"&subcate="+0;
						form.submit();
					}else {
						document.getElementById("go"+k).disabled=false;
						return;
					}
					
				}
			}
		}
		
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="tslist">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=7>新抓取的电视剧</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=7><s:textfield name="name" id="name" theme="simple" label="关键词"></s:textfield><s:select theme="simple" label="状态" id="status" name="status" list="#{'0':'未处理','1':'删除','2':'已通过'}"></s:select><input type="button" value="搜  索" onclick="search(this.form)"/></td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td colspan=2>电视剧</td>
	<td colspan=3>创建时间</td>
	<td colspan=2>操作</td>
	</tr>
	<%int k=0; %>
	<s:iterator value="tsvos">
	<%k=k+1; %>
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td colspan=2  align="left">
	<s:property value="name"/>
	</td>
	<td colspan=3  align="left"><s:property value="createtime"/></td>
	<td colspan=2  align="center">
	<s:if test="status==0">
		<input id="del<%=k %>" type="button" value="删 除" onclick="updateTelespider(this.form,<s:property value="id"/>,1,<%=k %>)"/><input id="go<%=k %>" type="button" value="通 过" onclick="updateTelespider(this.form,<s:property value="id"/>,2,<%=k %>)"/>
	</s:if>
	</td>
	</tr>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=2><s:hidden name="maxpage"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个剧集信息</td>
	<td><s:if test="page>1"><a href='tslist.html?page=1&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>首页</a></s:if></td>
	<td><s:if test="page>1"><a href='tslist.html?page=<s:property value="page-1"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>上一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='tslist.html?page=<s:property value="page+1"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>下一页</a></s:if></td>
	<td><s:if test="page!=maxpage&&maxpage>0"><a href='tslist.html?page=<s:property value="maxpage"/>&name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>尾页</a></s:if></td>
	<td>页码:<select id="cuurpage" name="cuurpage" onchange="location.href='tslist.html?name=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>&page=' + this.value;">
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
	<td colspan=7>注:如果要新增此电视剧，请点击通过，否则请点击删除</td>
	</tr>
	</table>
	</s:form>
	</body>
</html>