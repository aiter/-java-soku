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
		form.action="menuremove.html";
		form.submit();
	}}
}

function addMenu(fid,form)
{	
	form.action="menutoadd.html?fatherid="+fid;
	form.submit();
}
</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<s:form action="menutoadd.html">
		<table width="100%" border="0" align="center">
			<tr>
				<td>
					<b>菜单信息列表</b>
				</td>
			</tr>
		</table>
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
			<TR align="center" bgcolor="#A4D6FF">
				<TD width="2%">
					选择
				</TD>
				<TD width="5%">
					级别
				</TD>
				<TD width="25%">
					名称
				</TD>
				<TD width="40%">
					URL
				</TD>
				<TD width="10%">
					模块
				</TD>
				<TD width="13%">
				</TD>
			</TR>
			<s:iterator value="ml">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						<input type="checkbox" name="menuids"
							value='<s:property value="m.id"/>' onclick="check(this.form)">
					</TD>
					<TD>
						<s:property value="level" />级
					</TD>
					<TD  align="left">
						<s:property value="m.name" />
					</TD>
					<td><s:property value="m.menuurl"/></td>
					<TD align="left">
						<s:property value="m.module" />
					</TD>
					<TD>
						<s:if test="1==level">
							<input type="button" value="添加" onclick="addMenu(<s:property value="m.id"/>,this.form)" />
						</s:if>
					</TD>
				</TR>
			</s:iterator>
		</TABLE>

		<table width="100%" border="0" align="center" class="top1"
			cellpadding="3">
			<tr>
				<td>
					<input type="checkbox" name="chkall" onclick="CheckAll(this.form)">
					全部选中
				</td>
				<td>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="button" value="删  除" onclick="submitInfo(this.form)" />
					<s:submit value="添  加" />
				</td>
			</tr>
		</table>
</s:form>
	</body>
</html>