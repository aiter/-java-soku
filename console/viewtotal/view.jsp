<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		 <script type="text/javascript">
			function showPic(type){
				var url = "/console/viewtotal/picview.jsp?type="+type+"&start="+'<s:property value="start"/>'+"&end="+'<s:property value="dateend"/>';
				document.getElementById("F").src=url;
			}
		</script>
		<script type="text/javascript" src="<s:url value="/console/images/jquery-1.3.2.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.core.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.dialog.js"/>"></script>
        <script type="text/javascript" src="<s:url value="/console/images/ui.datepicker.js"/>"></script>
        <link type="text/css" href="<s:url value="/console/images/ui.all.css"/>" rel="stylesheet" />
    	<link href="<s:url value="/console/images/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
        <script type="text/javascript">
        $(function() {
			$("#startdate").datepicker({dateFormat: 'yy-mm-dd'});
			$("#enddate").datepicker({dateFormat: 'yy-mm-dd'});
        });
        </script>
	</head>
	<body>
		<s:form action="logView.html">
		<s:hidden name="start"/><s:hidden name="dateend"/>
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			align="center" title="统计每天各搜索类型的搜索次数">
			
			<TR align="left" bgcolor="#A4D6FF">
			<td colspan=10>(格式:yyyy-mm-dd)开始日期:<s:textfield theme="simple" onfocus="setday(this)" name="startdate" id="startdate"/>
			结束日期:<s:textfield theme="simple" onfocus="setday(this)" name="enddate" id="enddate"/>
			<s:submit  theme="simple" value="查询"/>（如为空，则日期取当天的前一天)
			</td>
			</TR>
			<TR align="center" bgcolor="#A4D6FF">
				<TD align="center">
					日期
				</TD>
				<TD align="center">
					<input type="button" value="总数" onclick="showPic(1)"/>
				</TD>
				<TD align="center">
					<input type="button" value="第一页搜索总数" onclick="showPic(7)"/>
				</TD>
				<TD align="center">
					首页比例
				</TD>
				<TD align="center">
					<input type="button" value="视频" onclick="showPic(2)"/>
				</TD>
				<TD align="center">
					<input type="button" value="专辑" onclick="showPic(3)"/>
				</TD>
				<TD align="center">
					<input type="button" value="用户" onclick="showPic(4)"/>
				</TD>
				<TD align="center">
					<input type="button" value="看吧" onclick="showPic(5)"/>
				</TD>
				<TD align="center">
					<input type="button" value="视频高级检索" onclick="showPic(8)"/>
				</TD>
				<TD align="center">
					<input type="button" value="专辑高级检索" onclick="showPic(9)"/>
				</TD>
			</TR>
			<s:iterator value="lvd" status="index">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<td>
					<s:if test="inMounth">
						<a href="<%=request.getContextPath() %>/logSingleView.html?date=<s:property value="date"/>"><s:property value="date"/></a>
					</s:if>
					<s:else>
						<s:property value="date"/>
					</s:else>
					</td>
					<td>
						<s:property value="sum" />
					</td>
					<td>
						<s:property value="datemap['page1Total']" />
					</td>
					<td>
						<s:property value="rate"/>%
					</td>
					<td>
						<s:property value="datemap['video']" />
					</td>
					<td>
						<s:property value="datemap['folder']" />
					</td>
					<td>
						<s:property value="datemap['user']" />
					</td>
					<td>
						<s:property value="datemap['bar']" />
					</td>
					<td>
						<s:property value="datemap['advvideo']" />
					</td>
					<td>
						<s:property value="datemap['advfolder']" />
					</td>
				</TR>
			</s:iterator>
		</TABLE>
		</s:form>
<iframe src="" name="F" id="F" frameborder="0" height="400" width="100%" marginheight="0" marginwidth="0" scrolling="auto"></iframe>
	
	</body>
</html>