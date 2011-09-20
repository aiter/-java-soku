<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.server.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*,
				com.youku.search.config.*"%>
<%
Result result = null;
long start=0 ,end = 0;
com.youku.search.index.entity.Query query = null;
String keywords = null;


if (request.getParameter("keywords") != null)
{
	int sort = DataFormat.parseInt(request.getParameter("sort"));
	keywords = (String)request.getParameter("keywords");
	query = new com.youku.search.index.entity.Query();
	query.field=DataFormat.parseInt(request.getParameter("field"));
	query.keywords = keywords;
	query.start=0;
	query.end=10;
	
	query.sort=sort;
	int needAnalyze = DataFormat.parseInt(request.getParameter("needAnalyze"));
	if (needAnalyze == 0)
		query.needAnalyze = false;
	start = System.currentTimeMillis();

	if (query.field == 15 || query.field == 16)
	{
		query.category=1;
	}
	
	result = (Result)SearchManager.getInstance().processQuery(query);
}
	end = System.currentTimeMillis();
%>
视频索引数：<%=VideoQueryManager.getInstance().getIndexReader()!=null?VideoQueryManager.getInstance().getIndexReader().numDocs()+"":""%><br/>

<form name="" action="" method="get">
搜索关键字：<input type="text" name="keywords">
排序：<input type="text" name="sort" size=4>（不填按相关度）<br>
<select name="field">
<option value="1">搜视频</option>
</select>
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
	out.println("返回结果集："+result.totalCount + " 耗时:" + (end -start)+"ms<br>");
	int total = result.totalCount>25?25:result.totalCount;
	List list = result.results;
	String[] keys = new String[list.size()];

	for (int i=0;i<list.size();i++)
	{
		PrimaryKey key =  (PrimaryKey)list.get(i);
		keys[i] = key.key;
	}
	Object[] objs = com.youku.search.store.ContainerFactory.getContainer().getMultiArray(keys);
	int i=0;
	for(Object obj:objs){
		i++;
			Video video = (Video)obj;
			//out.println(video.vid+"<br>");
			String title = video.title_hl!=null?video.title_hl:video.title;
			String tags = video.tags_hl!=null?video.tags_hl:video.tags;
			String username = video.username_hl!=null?video.username_hl:video.owner_username;

			out.println("结果：" + (i));
			out.println("vid="+video.vid+"		"+ video.title + "     创建人:" + username+ " 创建时间："+ DataFormat.formatDate(new java.util.Date(video.createtime),2) +"<br>");
			out.println("tags:" + tags+"<br>");
			out.println("memo:" + video.memo+"<br>");
			out.println("播放数:"+ video.total_pv + " 评论："+ video.total_comment + " 收藏："+ video.total_fav +"<br>");
			out.println(video.seconds);
			out.println("<br>===============================================================<br>");
	}
}
%>