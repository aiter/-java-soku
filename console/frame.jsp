<%@ page contentType="text/html;charset=utf-8" language="java"%>
<HTML>
	<HEAD>
		<TITLE>管理面板</TITLE>
		<META http-equiv=Content-Type content="text/html; charset=gb2312">
		<LINK href="images/common.css" type=text/css rel=stylesheet>
		<STYLE>
.navPoint {
	FONT-SIZE: 9px;
	CURSOR: hand;
	COLOR: white;
	FONT-FAMILY: Webdings
}

.css1 {
	COLOR: #ffffff
}
</STYLE>

		<SCRIPT>
function switchSysBar()
{
    if (switchPoint.innerText==3)
	{
        switchPoint.innerText=4;
        document.all("frmTitle").style.display="none"
    }
	else
	{
        switchPoint.innerText=3;
        document.all("frmTitle").style.display=""
    }
}
</SCRIPT>

		<META content="MSHTML 6.00.2900.2995" name=GENERATOR>
	</HEAD>
	<BODY style="MARGIN: 0px" scroll=no>
		<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
			<TBODY>
				<TR bgColor=#6699cc>
					<TD colSpan=4 height=30>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TBODY>
								<TR>
									<TD>
										<SPAN class=title_white>&nbsp;&nbsp;&nbsp;&nbsp;<B>后台管理系统</B>
										</SPAN>
									</TD>
									<TD align=right>
										<SPAN class=title_white></SPAN>&nbsp;&nbsp;
									</TD>
								</TR>
							</TBODY>
						</TABLE>
					</TD>
				</TR>
				<TR>
					<TD id=frmTitle vAlign=center noWrap align=middle>
						<IFRAME id=left
							style="Z-INDEX: 2; VISIBILITY: inherit; WIDTH: 150px; HEIGHT: 100%"
							name=left src="console/left.jsp" frameBorder=0></IFRAME>
					</TD>
					<TD bgColor=#6699cc>
						<TABLE style="CURSOR: hand" height=86 cellSpacing=0 cellPadding=0
							width=8 bgColor=#006699 border=0>
							<TBODY>
								<TR>
									<TD onclick=switchSysBar()>
										<SPAN class=navPoint id=switchPoint title=关闭/打开左栏>3</SPAN>
									</TD>
								</TR>
							</TBODY>
						</TABLE>
					</TD>
					<TD width=1 bgColor=#999999></TD>
					<TD style="WIDTH: 100%">
						<IFRAME id=main
							style="Z-INDEX: 1; VISIBILITY: inherit; WIDTH: 100%; HEIGHT: 100%"
							name=main src="about:blank" frameBorder=0>
						</IFRAME>
					</TD>
				</TR>
			</TBODY>
		</TABLE>
		<SCRIPT>
if(window.screen.width<'1024'){switchSysBar()}
</SCRIPT>
	</BODY>
</HTML>