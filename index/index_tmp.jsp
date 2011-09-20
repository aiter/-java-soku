<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.Result,
				com.youku.search.index.entity.Query,
				com.youku.search.index.server.*,
				com.youku.so.index.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.*"%>
<%

QueryManager videoQueryManager = QueryManager.getInstance();


Result result = null;
long start=0 ,end = 0;
Query query = null;
String keywords = null;
if (request.getParameter("keywords") != null)
{
	keywords = (String)request.getParameter("keywords");
	query = new Query();
	query.keywords = keywords;
	query.start=0;
	query.end=50;
	query.highlight = true;
	query.hl_prefix="<font color=red>";
	query.hl_suffix="</font>";

	int needAnalyze = DataFormat.parseInt(request.getParameter("needAnalyze"));
	if (needAnalyze == 0)
		query.needAnalyze = false;
	start = System.currentTimeMillis();
	
	
	result = (Result)videoQueryManager.query(query);
}
	end = System.currentTimeMillis();
%>
视频索引数：<%=videoQueryManager != null ?videoQueryManager.getIndexReader()!=null?videoQueryManager.getIndexReader().numDocs()+"":"":""%><br/>

<form name="" method="get">
搜索关键字：<input type="text" name="keywords">

<br>
<input type="checkbox" value="1" name="needAnalyze" checked>需要分词
<input type="submit" value="提交">
</form>

<br>
<%=query!=null?query.toString():""%>
<br>
<%
if (result != null)
{
	String k = java.net.URLEncoder.encode(request.getParameter("keywords"));
%>
<%
	out.println("返回结果集："+result.totalCount + " 耗时:" + (end -start)+"ms<br>");
	int total = result.totalCount>25?25:result.totalCount;
	List list = result.results;

	if (list != null && list.size()>0)
	{
		for (int i=0;i<list.size();i++)
		{
			Video video = (Video)list.get(i);
			//out.println(video.vid+"<br>");
			String title = video.title;
			String tags = video.tags;

			out.println("结果：" + (i+1) + ",匹配度：" + video.score);
			out.println("vid="+video.id+"		标题："+ title + " 创建时间："+ DataFormat.formatDate(new java.util.Date(video.uploadTime),2) +"<br>");
			out.println("tags:" + tags+"<br>");
			out.println("地址:" + video.site + " " +video.url+"<br>video.uploadTime="+video.uploadTime);
			out.println("<br>===============================================================<br>");
		}
	}
	else
	{
		out.print("没有找到结果");
	}
}
%>
