<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript" type="text/javascript">
    
function submitInfo(form)
{	   
	var isSelect = false;
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
		alert ("请选择一项后在操作！！");
	    return false;
	}else{
	form.submit();
	}
}

</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<table width="100%" border="0" align="center">
			<tr>
				<td>
					<b>菜单信息列表</b>
				</td>
			</tr>
		</table>
		<s:form name="aform" action="resmatch.html"  onsubmit="return submitInfo(this.form)">
		<s:hidden name="resid" />
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
			<TR align="center" bgcolor="#A4D6FF">
				<TD width="2%">
					选择
				</TD>
				<TD width="25%">
					菜单
				</TD>
				<TD width="38%">
					描述
				</TD>
				<TD width="35%">
					URL
				</TD>
			</TR>
			<s:iterator value="ml">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						<input type="checkbox" name="menuids"
							value='<s:property value="id"/>' onclick="check(this.form)">
					</TD>
					<TD>
						<s:property value="name" />
					</TD>
					<TD>
						<s:property value="menuurl" />
					</TD>
					<TD>
						<s:property value="module" />
					</TD>
				</TR>
			</s:iterator>
		</TABLE>

		<table width="100%" border="0" align="center" class="top1"
			cellpadding="3">
			<tr>
				<td colspan="2">
				<input type="button" value="确定" onclick="submitInfo(this.form)"/>
				</td>
			</tr>
		</table>
</s:form>
	</body>
</html>