<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.server.*,
				com.youku.search.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.*,
				java.io.File,
				com.youku.search.config.Config"%>
<%

if (MyUtil.isIndexServer(Constant.QueryField.VIDEO)){
	IndexManager.getInstance().delete(AffectManager.Type.VIDEO);
}
if (MyUtil.isIndexServer(Constant.QueryField.FOLDER))
	IndexManager.getInstance().delete(AffectManager.Type.FOLDER);

if (MyUtil.isIndexServer(Constant.QueryField.MEMBER))
	IndexManager.getInstance().delete(AffectManager.Type.USER);

%>
