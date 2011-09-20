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
function deleteInfo(form)
{	    var isSelect = false;
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
		if(confirm("确认删除选中的资源?")){
		form.action="searchrecomendremove.html";
		form.submit();
	}}
}

function updateInfo(form)
{	var isSelect = false;
	var num=0;
	for (var i=0;i<form.elements.length;i++)
    {
    	if( form.elements[i].type=="checkbox" && form.elements[i].checked == true)
    	{ 
    		num=num+1;
			isSelect = true;
		}
		if(num>1){
			alert ("请仅选择一项!");
	    	return;
		}
    }
	if(isSelect==false){
		alert ("请选择一项后在操作!");
	    return;
	}else{
		form.action="searchrecomendtoupdate.html";
		form.submit();
	}
}

function search(form)
{	
	form.action="searchrecomendlist.html";
	form.submit();
}
</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<s:form action="searchrecomendtoadd.html">
			<table width="100%" border="0" align="center">
				<tr>
					<td>
						<b>搜索下拉关键词信息列表</b>
					</td>
				</tr>
			</table>
			<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
				align="center">
				<tr align="left" bgcolor="#A4D6FF">
					<td colspan=8>
						<s:textfield name="keyword" id="keyword" theme="simple" />
						<input type="button" value="搜 索" onclick="search(this.form)" />
					</td>
				</tr>
				<TR align="center" bgcolor="#A4D6FF">
					<TD width="2%" align="center">
						选择
					</TD>
					<TD width="15%" align="center">
						关键词
					</TD>
					<TD width="15%" align="center">
						拼音
					</TD>
					<TD width="5%" align="center">
						类型
					</TD>
					<TD width="13%" align="center">
						搜索次数
					</TD>
					<TD width="10%" align="center">
						结果数
					</TD>
					<TD width="20%" align="center">
						开始时间
					</TD>
					<TD width="20%" align="center">
						结束时间
					</TD>
				</TR>
				<s:iterator value="tl">
					<TR align="center" bgcolor="#FFFFFF"
						onmouseover="this.style.background='#FFCC00' "
						onmouseout="this.style.background='#F3F3F3'">
						<TD  align="left">
							<input type="checkbox" name="tids"
								value='<s:property value="id"/>' onclick="check(this.form)">
						</TD>
						<TD  align="left">
							<s:property value="keyword" />
						</TD>
						<TD  align="left">
							<s:property value="keywordPy" />
						</TD>
						<TD  align="left">
							<s:property value="recomendType" />
						</TD>
						<TD  align="left">
							<s:property value="queryCount" />
						</TD>
						<TD  align="left">
							<s:property value="result" />
						</TD>
						<TD  align="left">
							<s:date name="starttime" format="yyyy-MM-dd" />
						</TD>
						<TD  align="left">
							<s:date name="endtime" format="yyyy-MM-dd" />
						</TD>
					</TR>
				</s:iterator>
			</TABLE>

			<table width="100%" border="0" align="center" class="top1"
				cellpadding="3">
				<tr>
					<td colspan="3" align="left">
						<input type="checkbox" name="chkall" onclick="CheckAll(this.form)">
						全部选中
					</td>
					<td colspan="5">
					</td>
				</tr>
				<tr>
					<td colspan="1"  align="left">
						<input type="button" value="修  改" onclick="updateInfo(this.form)" />
					</td>
					<td colspan="2"  align="left">
						<input type="button" value="删  除" onclick="deleteInfo(this.form)" />
					</td>
					<td colspan="5">
					</td>
				</tr>
				<tr>
					<td colspan="8"  align="left">
						<s:submit value="添  加"  align="left"/>
					</td>
				</tr>
				<tr bgcolor="#A4D6FF">
					<td colspan=3>
						<s:hidden name="maxpage" />
						第
						<s:property value="page" />
						页，总共
						<s:property value="maxpage" />
						页,共
						<s:property value="totalSize" />
						个关键词
					</td>
					<td>
						<s:if test="page>1">
							<a
								href='searchrecomendlist.html?keyword=<s:property value="encodename"/>&page=1'>首页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page>1">
							<a
								href='searchrecomendlist.html?keyword=<s:property value="encodename"/>&page=<s:property value="page-1"/>'>上一页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page!=maxpage&&maxpage>0">
							<a
								href='searchrecomendlist.html?keyword=<s:property value="encodename"/>&page=<s:property value="page+1"/>'>下一页</a>
						</s:if>
					</td>
					<td>
						<s:if test="page!=maxpage&&maxpage>0">
							<a
								href='searchrecomendlist.html?page=<s:property value="maxpage"/>&keyword=<s:property value="encodename"/>'>尾页</a>
						</s:if>
					</td>
					<td>
						页码:
						<select id="cuurpage" name="cuurpage"
							onchange="location.href='searchrecomendlist.html?keyword=<s:property value="encodename"/>&page=' + this.value;">
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
			</table>
		</s:form>
	</body>
</html>