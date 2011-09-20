<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.index.query.*,
				com.youku.search.util.DataFormat"%>
<%
int type = DataFormat.parseInt(request.getParameter("type"));

if (type == 1){
	VideoQueryManager.getInstance().reopenIndex();
}
else if (type == 2){
	WordQueryManager.getInstance().reopenIndex();
}
else if (type == 4){
	System.out.println("library reopen");
	LibraryQueryManager.getInstance().reopenIndex();
}
%>
