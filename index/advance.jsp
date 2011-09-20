<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.SearchManager,
				com.youku.search.index.entity.*,
				com.youku.search.index.manager.*,
				com.youku.search.index.query.*,
				com.youku.search.util.*,
				com.youku.search.index.*"%>
<%
Result result = null;
long start=0 ,end = 0;
AdvanceQuery query = null;
String keywords1 = null;
String keywords2 = null;
String keywords3 = null;
String keywords4 = null;
if (request.getParameter("keywords1") != null)
{
	keywords1 = (String)request.getParameter("keywords1");
	keywords2 = (String)request.getParameter("keywords2");
	keywords3 = (String)request.getParameter("keywords3");
	keywords4 = (String)request.getParameter("keywords4");
	query = new AdvanceQuery();
	query.field=DataFormat.parseInt(request.getParameter("field"));
	query.fields="title,tags_index,memo";
	query.start=0;
	query.end=50;

	StringBuffer sb = new StringBuffer();
	String[] arr = null;
	if (keywords1 != null && keywords1.length() > 0)
	{
		arr = keywords1.split(" ");
		for (int i=0;i<arr.length;i++)
		{
			sb.append("+" + arr[i] );
		}
	}
	
	if (keywords2 != null && keywords2.length() > 0)
	{
		arr = keywords2.split(" ");
		for (int i=0;i<arr.length;i++)
		{
			sb.append("+\"" + arr[i] + "\"");
		}
	}
	
	if (keywords3 != null && keywords3.length() > 0)
	{
		if (keywords3.indexOf(" ") > -1)
			sb.append("+(" + keywords3 + ")");
		else
			sb.append("+"+ keywords3 );
	}
	
	if (keywords4 != null && keywords4.length() > 0)
	{
		arr = keywords4.split(" ");
		for (int i=0;i<arr.length;i++)
		{
			sb.append("-" + arr[i] + "");
		}
	}

	query.keywords = sb.toString().trim();
	
	out.println(query.keywords+"<br>");

	start = System.currentTimeMillis();

	result = (Result)SearchManager.getInstance().processQuery(query);
}
else if (request.getParameter("keywords") != null)
{
	query = new AdvanceQuery();
	query.field=DataFormat.parseInt(request.getParameter("field"));
	if (query.field == 1)
		query.fields="title,tags_index,memo";
	else
		query.fields="folder_name,tags_index,description";
	query.start=0;
	query.end=50;
	query.keywords = request.getParameter("keywords");
	
	out.println(query.keywords+"<br>");

	start = System.currentTimeMillis();

	result = (Result)SearchManager.getInstance().processQuery(query);
}
	end = System.currentTimeMillis();
%>

<form name="" method="get">
包含以下全部关键词：<input type="text" name="keywords1"><br>
包含以下完整关键词：<input type="text" name="keywords2"><br>
包含以下任意关键词：<input type="text" name="keywords3"><br>
不包含以下关键词：<input type="text" name="keywords4"><br>

<select name="field">
<option value="1">搜视频</option>
<option value="2">搜专辑</option>
</select>
<br>
<input type="submit" value="提交">
<br>
</form>


<form name="" method="get">
完整表达式搜索：<input type="text" name="keywords" value='<%=query!=null?query.keywords:""%>'><br>
<select name="field">
<option value="1">搜视频</option>
<option value="2">搜专辑</option>
</select>
<br>
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

	if (list != null && list.size()>0)
	{
		for (int i=0;i<list.size();i++)
		{
			if (query.field == 1 || query.field == 8)
			{
				Video video = (Video)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + video.score);
				out.println("vid="+video.vid+"		"+video.title+ "     创建人:" +video.owner_username+ " 创建时间："+ DataFormat.formatDate(new java.util.Date(video.createtime),2) +"<br>");
				out.println("tags:" + video.tags+"<br>");
				out.println("memo:" + video.memo+"<br>");
				out.println(video.tags+" 播放数:"+ video.total_pv + " 评论："+ video.total_comment + " 收藏："+ video.total_fav +"<br>");
				out.println(video.score);
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 2)
			{
				Folder folder = (Folder)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + folder.score);
				out.println("folder_id="+folder.pk_folder+"		"+folder.title+ "     创建人:" +folder.owner_name+ " 创建时间："+ DataFormat.formatDate(new java.util.Date(folder.update_time),2) +"<br>");
				out.println("tags:"+folder.tags);
				for (int j=0;j<folder.video_title.length;j++)
				{
					out.println("video " + j + " title=" +folder.video_title[j]);
					out.println("video " + j + " seconds=" +folder.video_seconds[j]);
					out.println("<br>");
				}
				
				out.println("<br>===============================================================<br>");
			}
		}
	}
	else
	{
		out.print("没有找到结果");
	}
}
%>