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
		function search(form)
		{
			form.action="synonymsList.html";
			form.submit();
		}
		
		function dodelete(form)
		{
			if(confirm("确认彻底删除选中的同义词？")){
				form.action="synonymDelete.html";
				form.submit();
			}
		}
		
		function toupdate(form,fid)
		{
			form.action="synonym2Update.html?synonymId="+fid;
			form.submit();
		}
		
		function toadd(form)
		{
			form.action="synonym2Add.html";
			form.submit();
		}
		
		function tohandle(form,state)
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
		}else{
			if(0==state){
				if(confirm("确认删除选定的同义词组？")){
					form.action="synonymUpdate.html?state=0";
					form.submit();
				}
			}else if(1==state){
				if(confirm("确认还原选定的同义词组？")){
					form.action="synonymUpdate.html?state=1";
					form.submit();
				}
			}
		}
		}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<s:form action="synonymsList">
			<table cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
				align="center">
				<tr bgcolor="#A4D6FF">
					<td colspan=10>
						同义词管理
					</td>
				</tr>
				<tr align="left" bgcolor="#A4D6FF">
					<td colspan=10>
						<s:textfield name="keyword" id="name" theme="simple" label="关键词"></s:textfield>
						<s:select theme="simple" label="状态" id="status" name="status"
							list="#{'1':'有效','0':'已删除'}"></s:select>
						<input type="button" value="搜  索" onclick="search(this.form)" />
					</td>
				</tr>
				<tr align="center" bgcolor="#A4D6FF">
					<td width="5%">
						选择
					</td>
					<td colspan=8>
						同义词
					</td>
					<td width="10%">
						操作
					</td>
				</tr>
				<s:iterator value="synonyms">
					<tr align="left" bgcolor="#FFFFFF"
						onmouseover="this.style.background='#FFCC00' "
						onmouseout="this.style.background='#F3F3F3'">
						<td width="5%">
							<input type="checkbox" name="ids"
								value='<s:property value="id"/>' onclick="check(this.form)">
						</td>
						<td colspan=8 align="left">
							<s:property value="keywords"/>
						</td>
						<td align="center">
							<s:if test="state==1">
								<input type="button" value="修改"
									onclick="toupdate(this.form,<s:property value="id"/>)" />
							</s:if>
						</td>
					</tr>
				</s:iterator>
				<s:if test="synonyms.size>0">
				<tr bgcolor="#A4D6FF">
					<td colspan=2>
						<input type="checkbox" name="chkall" onclick="CheckAll(this.form)">
						全部选中
					</td>
					<td colspan=3>
						<s:hidden name="maxpage" />
						第
						<s:property value="page" />
						页，总共
						<s:property value="maxpage" />
						页,共
						<s:property value="totalSize" />
						个同义词组信息
					</td>
					<td>
						<s:if test="page>1">
							<a
								href='synonymsList.html?page=1&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>首页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page>1">
							<a
								href='synonymsList.html?page=<s:property value="page-1"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>上一页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page!=maxpage&&maxpage>0">
							<a
								href='synonymsList.html?page=<s:property value="page+1"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>下一页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page!=maxpage&&maxpage>0">
							<a
								href='synonymsList.html?page=<s:property value="maxpage"/>&keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>'>尾页</a>
						</s:if>
					</td>
					<td>
						页码:
						<select id="cuurpage" name="cuurpage"
							onchange="location.href='synonymsList.html?keyword=<s:property value="encodeKeyword"/>&status=<s:property value="status"/>&page=' + this.value;">
							<%
								int startpage = 1;
									int maxpage = 1;
									String check = "";
									try {
										startpage = Integer.parseInt(""
												+ request.getAttribute("cuurpage"));
										maxpage = Integer.parseInt(""
												+ request.getAttribute("cuurmaxpage"));
									} catch (Exception e) {
										startpage = 1;
									}
									for (int i = 1; i <= maxpage; i++) {
										if (i == startpage)
											check = "selected";
										else
											check = "";
							%>
							<option value="<%=i%>" <%=check%>>
								第<%=i%>页
							</option>
							<%
								}
							%>
						</select>
					</td>
				</tr>
				<tr bgcolor="#A4D6FF">
					<td colspan=5  align="left">
						<s:if test="status==1">
							<input type="button" value="删除" onclick="tohandle(this.form,0)" />
						</s:if>
						<s:else>
							<input type="button" value="还原" onclick="tohandle(this.form,1)" />
							<input type="button" value="彻底删除" onclick="dodelete(this.form)" />
						</s:else>
					</td>
					<td colspan=5  align="center">
						<input type="button" value="添加" onclick="toadd(this.form)" />
					</td>
				</tr>
				</s:if>
			</table>
		</s:form>
	</body>
</html>