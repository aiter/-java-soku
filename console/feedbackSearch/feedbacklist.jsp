<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
		
		function CheckAll(form)
		  {
		  	for (var i=0;i<form.elements.length;i++)
		    {
		   		 var e = form.elements[i];   
		       	 e.checked = form.chkall.checked;
		    }
		  }
		 function check(form)
		  {
		  	for (var i=0;i<form.elements.length;i++)
		    {
		   		 var e = form.elements[i];
		   		 if(e.checked){
		       	 	form.chkall.checked=false;
		       	 break;
		       	 }
		    }
		  }
		function submitInfo(form)
			{	
			 var isSelect = false;
				for (var i=0;i<form.elements.length;i++)
			    {
			    	if( form.elements[i].type=="checkbox" && form.elements[i].checked == true)  
					isSelect = true;
			    }
				if(isSelect==false){
					alert ("请选择一项后在操作！！");
				    return;
				}
				else{
					if(confirm("确认删除选定的反馈信息？")){
					form.action="sfdelete.html";
					form.submit();
					}
				}
			}
			
			function search(form)
			{
				var keyword=document.getElementById("name").value;
				form.action="sflist.html";
				form.submit();
			}
			function searchWords(k,form)
			{
				document.getElementById("name").value=k;
				sofeed.action="sflist.html?f=1";
				sofeed.submit();
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
	<s:form action="sflist" name="sofeed">
	<s:property value="message"/>
	<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
	<tr bgcolor="#A4D6FF">
	<td colspan=12>搜索反馈信息列表</td>
	</tr>
	<tr align="left" bgcolor="#A4D6FF">
	<td colspan=12>关键词:<s:textfield name="keyword" id="name" theme="simple"></s:textfield>排序:<s:select theme="simple" name="order" list="#{'1':'反馈时间','2':'反馈次数'}"></s:select>
	<s:radio theme="simple" id="isPrecise" name="isPrecise" list="#{'0':'模糊匹配','1':'精准匹配'}"></s:radio>
	<input type="button" value="搜  索" onclick="search(this.form)"/>
	</td>
	</tr>
	<tr align="center" bgcolor="#A4D6FF">
	<td width="2%"></td>
	<td colspan=2 width="12%">关键词</td>
	<td colspan=3 width="45%">url</td>
	<td colspan=4 width="23%">错误描述</td>
	<td width="8%">次数</td>
	<td width="10%">创建时间</td>
	</tr>
	<s:iterator value="fbs">
	<tr align="left" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
	<td>
		<input type="checkbox" name="fids"
			value='<s:property value="id"/>' onclick="check(this.form)">
	</td>
	<td align="left" colspan=2><s:if test="order!=1"><a href="#" onclick="searchWords('<s:property value="keywords"/>',this.form)"><s:property value="keywords"/></a></s:if><s:else><s:property value="keywords"/></s:else></td>
	<td colspan=3 align="left"><a href='<s:property value="url"/>' target="_blank"><s:property value="url"/></a></td>
	<td colspan=4 align="left"><s:property value="description"/></td>
	<td align="left"><s:property value="num"/></td>
	<td align="left"><s:property value="createtime"/></td>
	</tr>
	</s:iterator>
	<tr bgcolor="#A4D6FF">
	<td colspan=6 align="left" width="59%"><s:hidden name="maxpage"/><s:hidden name="page"/>第<s:property value="page"/>页，总共<s:property value="maxpage"/>页,共<s:property value="totalSize"/>个反馈信息</td>
	<td align="center" width="6%"><s:if test="page>1"><a href='sflist.html?page=1&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&isPrecise=<s:property value="isPrecise"/>'>首页</a></s:if></td>
	<td align="center" width="6%"><s:if test="page>1"><a href='sflist.html?page=<s:property value="page-1"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&isPrecise=<s:property value="isPrecise"/>'>上一页</a></s:if></td>
	<td align="center" width="6%"><s:if test="page!=maxpage&&maxpage>0"><a href='sflist.html?page=<s:property value="page+1"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&isPrecise=<s:property value="isPrecise"/>'>下一页</a></s:if></td>
	<td align="center" width="5%"><s:if test="page!=maxpage&&maxpage>0"><a href='sflist.html?page=<s:property value="maxpage"/>&keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&isPrecise=<s:property value="isPrecise"/>'>尾页</a></s:if></td>
	<td colspan=2 align="center" width="18%">页码:<select id="cuurpage" name="cuurpage" onchange="location.href='sflist.html?keyword=<s:property value="encodeKeyword"/>&order=<s:property value="order"/>&isPrecise=<s:property value="isPrecise"/>&page=' + this.value;">
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
	<s:if test="fbs.size!=0">
	<tr bgcolor="#A4D6FF">
		<td colspan="12">
			<input type="checkbox" name="chkall" onclick="CheckAll(this.form)">
					全部选中
		</td>
	</tr>
	<tr bgcolor="#A4D6FF">
		<td colspan="12">
			<input type="button" value="删  除" onclick="submitInfo(this.form)" />
		</td>
	</tr>
	</s:if>
	</table>
	</s:form>
	</body>
</html>