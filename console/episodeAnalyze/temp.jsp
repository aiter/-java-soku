<%
	String searchwords=""+request.getAttribute("searchwords");
	request.removeAttribute("searchwords");
	String url="http://so.youku.com/search_video/q_"+searchwords;
	response.sendRedirect(url);
%>