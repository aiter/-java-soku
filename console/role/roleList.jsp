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
function submitInfo(o,form)
{	var isSelect = false;
	var num=0;
	for (var i=0;i<form.elements.length;i++)
    {
    	if( form.elements[i].type=="checkbox" && form.elements[i].checked == true)
    	{ 
    		num=num+1;
			isSelect = true;
		}
		if(num>1&&(o==4||o==5)){
			alert ("请仅选择一项!");
	    	return;
		}
    }
	if(isSelect==false){
		alert ("请选择一项后在操作!");
	    return;
	}
	else{
	if(o==1){
		if(confirm("确认删除选定的角色？")){
		form.action="roleremove.html";
		form.submit();
		}
		}
	else if(o==2){
		form.action="roletoauthorize.html";
		form.submit();
		}
	else if(o==3){
		form.action="roletomatch.html";
		form.submit();
		}
	else if(o==4){
		form.action="roletounauthorize.html";
		form.submit();
		}
	else {
		form.action="roletounmatch.html";
		form.submit();
	}
	}
}

</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<s:form action="roletoadd.html">
			<table width="100%" border="0" align="center">
				<tr>
					<td>
						<b>角色信息列表</b>
					</td>
				</tr>
			</table>
			<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
				align="center">
				<TR align="center" bgcolor="#A4D6FF">
					<TD width="2%">
						选择
					</TD>
					<TD width="15%">
						角色
					</TD>
					<TD width="43%">
						描述
					</TD>
				</TR>
				<s:iterator value="rol">
					<TR align="center" bgcolor="#FFFFFF"
						onmouseover="this.style.background='#FFCC00' "
						onmouseout="this.style.background='#F3F3F3'">
						<TD>
							<input type="checkbox" name="roleids"
								value='<s:property value="id"/>' onclick="check(this.form)">
						</TD>
						<TD>
							<s:property value="name" />
						</TD>
						<TD>
							<s:property value="description" />
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
				</tr>
				<tr>
					<td>
						<input type="button" value="删  除"
							onclick="submitInfo(1,this.form)" />
					</td>
				</tr>
				<tr>
					<td>
						<input type="button" value="授 权" onclick="submitInfo(2,this.form)" />
						<input type="button" value="匹 配" onclick="submitInfo(3,this.form)" />
						<input type="button" value="取消授权"
							onclick="submitInfo(4,this.form)" />
						<input type="button" value="取消匹配"
							onclick="submitInfo(5,this.form)" />
						<s:submit value="添  加" />
					</td>
				</tr>
			</table>
		</s:form>
	</body>
</html>