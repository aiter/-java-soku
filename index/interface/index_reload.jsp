<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.query.*,
				com.youku.search.index.manager.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.Config"%>
<%
int type = DataFormat.parseInt(request.getParameter("type"));

	if (type == Constant.QueryField.VIDEO)
	{
		VideoQueryManager.getInstance().reopenIndex();
	}
	else if (type == Constant.QueryField.FOLDER)
	{
		FolderQueryManager.getInstance().reopenIndex();
	}
	else if (type == Constant.QueryField.MEMBER)
	{
		UserQueryManager.getInstance().reopenIndex();
	}
	else if (type == Constant.QueryField.BARPOST_SUBJECT)
	{
		BarpostQueryManager.getInstance().reopenIndex();
	}
	else if (type == Constant.QueryField.PK)
	{
		PkQueryManager.getInstance().reopenIndex();
	}
	else if (type == Constant.QueryField.STAT)
	{
		StatQueryManager.getInstance().reopenIndex();
	}
	
	else if (type == Constant.QueryField.BAR)
	{
		BarQueryManager.getInstance().reopenIndex();
	}
%>
