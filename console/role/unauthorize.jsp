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
{	var isSelect = false;
	for (var i=0;i<form.elements.length;i++)
    {
    	if( form.elements[i].type=="checkbox" && form.elements[i].checked == true)  
		isSelect = true;
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
					<b>用户信息列表</b>
				</td>
			</tr>
		</table>
		<s:form name="aform" action="roleunauthorize.html"  onsubmit="return submitInfo(this.form)">
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
			<TR align="center" bgcolor="#A4D6FF">
				<TD width="2%">
					选择
				</TD>
				<TD width="15%">
					用户名
				</TD>
				<TD width="15%">
					真实姓名
				</TD>
				<TD width="15%">
					团队
				</TD>
				<TD width="23%">
					最后登陆时间
				</TD>
			</TR>
			<s:iterator value="ul">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						<input type="checkbox" name="uids"
							value='<s:property value="id"/>' onclick="check(this.form)">
					</TD>
					<TD>
						<s:property value="name" />
					</TD>
					<TD>
						<s:property value="trueName" />
					</TD>
					<TD>
						<s:property value="team" />
					</TD>
					<TD>
					<s:date name="lastlogindate" format="yyyy-MM-dd hh:mm:ss"/>
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
					<input type="button" value="确定" onclick="submitInfo(this.form)"/>
				</td>
			</tr>
		</table>
</s:form>
	</body>
</html>