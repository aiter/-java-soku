<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</head>
	<body>
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			align="center" title="统计每天反馈的操作">
			<TR align="center" bgcolor="#A4D6FF">
				<TD align="center" width="30%">
					日期
				</TD>
				<TD align="center" width="20%">
					操作者
				</TD>
				<TD align="center" width="50%">
					操作反馈数
				</TD>
			</TR>
			<s:iterator value="fus" status="index">
				<TR align="center"  bgcolor="#FFFFFF">
					<td width="30%">
						<s:property value="operateDate" />
					</td>
					<td colspan=2 width="70%">
						<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999">
							<s:iterator value="slo">
								<TR bgcolor="#FFFFFF"
									onmouseover="this.style.background='#FFCC00' "
									onmouseout="this.style.background='#F3F3F3'">
									<td align="left" width="30%">
										<s:property value="operator" />
									</td>
									<td align="left" width="35%">
										删除:<s:property value="deletenum" />
									</td>
									<td align="left" width="35%">
										处理:<s:property value="dealnum" />
									</td>
								</TR>
							</s:iterator>
						</TABLE>
					</td>
				</TR>
			</s:iterator>
		</TABLE>
	</body>
</html>