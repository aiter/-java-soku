<%@ page contentType="text/html; charset=GBK"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
	<head>
		<title>搜索管理平台</title>
		<LINK href="images/common.css" type=text/css rel=stylesheet>
		<META content="MSHTML 6.00.2900.2995" name=GENERATOR>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript">
function checkForm(form)
{
	var name = form.name.value;
	var passwd = form.password.value;
	if (name.length == 0 )
	{
		alert("登录名不能为空!");
		form.name.focus();
	 	return false;
	 }
	 if (passwd.length == 0 )
	{
		alert("密码不能为空!");
		form.password.focus();
	 	return false;
	 }
	 form.submit();
}

function checkTopWindow(){
    if(window.top != window.self){
        window.top.location=window.self.location;
    }
}
</script>
	</head>
	<BODY leftMargin=0 topMargin=0>
		<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
			<TBODY>
				<TR>
					<TD vAlign=center align=middle>
						<TABLE borderColor=#d8d8d8 cellSpacing=0 cellPadding=5 width=300
							border=1>
							<s:form action="userlogin.html" onsubmit="return checkForm(this.form)">
								<FONT color="#FF0000"><s:property value="message" /> </FONT>
								<TR align=middle bgColor=#f7f7f7>
									<TD class=title colSpan=2>
										管理
									</TD>
								</TR>
								<TR>
									<TD align=middle>
										<IMG height=70 src="images/login.jpg" width=60>
									</TD>
									<TD>
										<TABLE cellSpacing=0 cellPadding=2 align=center border=0>
											<TBODY>
												<TR>
													<TD>
														用户名
													</TD>
													<TD>
														<INPUT class=box2 id=name style="WIDTH: 100px"
															maxLength=25 name="name">
													</TD>
												</TR>
												<TR>
													<TD align=left>
														密码
													</TD>
													<TD>
														<INPUT class=box2 id=password style="WIDTH: 100px"
															type=password maxLength=25 name="password">
													</TD>
												</TR>
											</TBODY>
										</TABLE>
									</TD>
								</TR>
								<TR>
									<TD align=middle colSpan=2>
										<INPUT class=btn type="submit" value=" 登录系统 ">
										<INPUT class=btn onclick=window.close(); type=button value=" 关闭窗口 " name=close>
									</TD>
								</TR>
							</s:form>
						</TABLE>
					</TD>
				</TR>
			</TBODY>
		</TABLE>
		<script type="text/javascript">
			checkTopWindow();
		</script>
	</BODY>
</HTML>
