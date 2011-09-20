<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.util.*" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>搜索管理平台</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<script type="text/javascript" src="swfobject.js"></script>
		<%
		int type = DataFormat.parseInt(request.getParameter("type"));
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String betdate = start+"|"+end;
		String divid="";
		if(type==1){
			divid="my_chart1";
		%>
		
		<script type="text/javascript">
		var url ="/json-chart/logViewTotal.html?betdate="+'<%=betdate%>';
		swfobject.embedSWF(
		  "<s:url value="open-flash-chart.swf"/>", "my_chart1", "100%", "400",
		  "9.0.0", "expressInstall.swf",
		  {"data-file":url}
		  );
		</script>
<%
}else if(type==2){
	divid="my_chart2";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewVideo.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart2", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==3){
	divid="my_chart3";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewFolder.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart3", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==4){
	divid="my_chart4";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewUser.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart4", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==5){
	divid="my_chart5";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewUser.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart5", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==6){
	divid="my_chart6";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewPk.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart6", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==7){
	divid="my_chart7";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewPage1Total.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart7", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==8){
	divid="my_chart8";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewAdvvideo.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart8", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}else if(type==9){
	divid="my_chart9";
 %>
		<script type="text/javascript">
		var url ="/json-chart/logViewAdvfolder.html?betdate="+'<%=betdate%>';
swfobject.embedSWF(
  "<s:url value="open-flash-chart.swf"/>", "my_chart9", "100%", "400",
  "9.0.0", "expressInstall.swf",
  {"data-file":url}
  );
</script>
<%
}
 %>
	</head>
	<body>
		<div id="<%=divid %>">
		</div>
	</body>
</html>