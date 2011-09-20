<%@ page contentType="text/html;charset=utf-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script language="javascript">
			function initXMLHttpRequest(){
				if(window.XMLHttpRequest){
					return new XMLHttpRequest();
				} else if(window.ActiveXObject) {
					return new ActiveXObject('Microsoft.XMLHTTP');
				}
			}
			function submitForm(form)
			{
				var keyword=document.getElementById("keyword").value;
				var type=document.getElementById("type").value;
				if(keyword==null||keyword.length==0){
					alert("缓存关键字不能为空！");
					return false;
				}
				var q=confirm("确认要清空关键字为"+keyword+"的缓存?");
				if(q){
					var xmlReq =initXMLHttpRequest();
					xmlReq.onreadystatechange = function(){
					if(xmlReq.readyState == 4){
						if (200==xmlReq.status){
						document.getElementById("info").innerHTML=xmlReq.responseText;
						document.getElementById("keyword").value="";
						}
					}
					}
					xmlReq.open("post", "cacheClean.html", true);
					xmlReq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					xmlReq.send("keyword="+keyword+"&type="+type);
					}
			}
			function cleanCacheForm(form)
			{
					form.action="cachelist.html?pageno="+document.getElementById("pageno").value;
					form.submit();
			}
		</script>
	</head>
	<body bgcolor="#FFFFFF" text="#000000" topmargin="2">
		<table width="100%" border="0" align="center">
			<tr>
				<td>
					<b>mencache状态信息列表</b>
				</td>
			</tr>
		</table>
		<TABLE cellSpacing="1" cellPadding="3" width="100%" bgcolor="#999999"
			 align="center">
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						bytes_written:
					</TD>
					<TD>
						<s:property value="mc.bytes_written" />
					</TD>
					<TD>
						connection_structures:
					</TD>
					<TD>
						<s:property value="mc.connection_structures" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						bytes:
					</TD>
					<TD>
						<s:property value="mc.bytes" />
					</TD>
					<TD>
						total_items:
					</TD>
					<TD>
						<s:property value="mc.total_items" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						rusage_system:
					</TD>
					<TD>
						<s:property value="mc.rusage_system" />
					</TD>
					<TD>
						total_connections:
					</TD>
					<TD>
						<s:property value="mc.total_connections" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						rusage_user:
					</TD>
					<TD>
						<s:property value="mc.rusage_user" />
					</TD>
					<TD>
						uptime:
					</TD>
					<TD>
						<s:property value="mc.uptime" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						pid:
					</TD>
					<TD>
						<s:property value="mc.pid" />
					</TD>
					<TD>
						get_hits:
					</TD>
					<TD>
						<s:property value="mc.get_hits" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						curr_items:
					</TD>
					<TD>
						<s:property value="mc.curr_items" />
					</TD>
					<TD>
						evictions:
					</TD>
					<TD>
						<s:property value="mc.evictions" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						version:
					</TD>
					<TD>
						<s:property value="mc.version" />
					</TD>
					<TD>
						cmd_get:
					</TD>
					<TD>
						<s:property value="mc.cmd_get" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						time:
					</TD>
					<TD>
						<s:property value="mc.time" />
					</TD>
					<TD>
						pointer_size:
					</TD>
					<TD>
						<s:property value="mc.pointer_size" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						cmd_set:
					</TD>
					<TD>
						<s:property value="mc.cmd_set" />
					</TD>
					<TD>
						threads:
					</TD>
					<TD>
						<s:property value="mc.threads" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						limit_maxbytes:
					</TD>
					<TD>
						<s:property value="mc.limit_maxbytes" />
					</TD>
					<TD>
						bytes_read:
					</TD>
					<TD>
						<s:property value="mc.bytes_read" />
					</TD>
				</TR>
				<TR align="center" bgcolor="#FFFFFF"
					onmouseover="this.style.background='#FFCC00' "
					onmouseout="this.style.background='#F3F3F3'">
					<TD>
						curr_connections:
					</TD>
					<TD>
						<s:property value="mc.curr_connections" />
					</TD>
					<TD>
						get_misses:
					</TD>
					<TD>
						<s:property value="mc.get_misses" />
					</TD>
				</TR>
		</TABLE>
		<div align="right">
		服务器： <select id="pageno" name="pageno" onchange="location.href='cachelist.html?pageno=' + this.value;">
			<%
			String[] ips;
			int startpage=0;
			String check="";
			try{
			ips=(String[])request.getAttribute("ips");
			}catch(Exception e){
			ips=null;}
			try{
			startpage=Integer.parseInt(""+request.getAttribute("startpage"));
			}catch(Exception e){startpage=0;}
			for(int i=0;i<ips.length;i++){
			if(i==startpage)check="selected";
			else check="";
			%>
			<option value="<%=i %>" <%=check %>><%=ips[i] %></option>
			<% }%>
		</select></div><div align="left">
		<s:form action="">
		类型:<s:select theme="simple" id="type" name="type" list="#{'1':'视频','2':'专辑','3':'会员','4':'看吧','5':'PK'}"/>
		关键字:<s:textfield theme="simple" name="keyword" id="keyword" required="true"/>
		<input type="button" onclick="submitForm(this.form)" value="删 除"/>
		<input type="button" onclick="cleanCacheForm(this.form)" value="刷 新"/><br/>
		<div id="info"></div>
		</s:form></div>
	</body>
</html>