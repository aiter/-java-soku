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
if (Config.getServerType()==2){
	List<Server> servers = ServerManager.getOnlineVideoServers(Config.getGroupNumber());
	for (int i=0;i<servers.size();i++)
	{
		out.print(servers.get(i).getIp());
	}
}
 VideoQueryManager videoQueryManager = SearchManager.getInstance().getVideoQueryManager();
	 FolderQueryManager folderQueryManager = SearchManager.getInstance().getFolderQueryManager();
	 UserQueryManager userQueryManager = SearchManager.getInstance().getUserQueryManager();
	 BarpostQueryManager barpostQueryManager = SearchManager.getInstance().getBarpostQueryManager();
	 PkQueryManager pkQueryManager = SearchManager.getInstance().getPkQueryManager();
	 StatQueryManager statQueryManager = SearchManager.getInstance().getStatQueryManager();
	 BarQueryManager barQueryManager = SearchManager.getInstance().getBarQueryManager();


Result result = null;
long start=0 ,end = 0;
Query query = null;
String keywords = null;
if (request.getParameter("keywords") != null)
{
	int sort = DataFormat.parseInt(request.getParameter("sort"));
	keywords = (String)request.getParameter("keywords");
	query = new Query();
	query.field=DataFormat.parseInt(request.getParameter("field"));
	
	if (query.field == 15 || query.field == 16)
	{
		query.category=1;
	}
	else{
		if (!MyUtil.isIndexServer(query.field))
		{
			return;
		}
	}

	query.keywords = keywords;
	query.start=0;
	query.end=50;
	query.highlight = true;
	query.hl_prefix="<font color=red>";
	query.hl_suffix="</font>";
	query.timeless=DataFormat.parseInt(request.getParameter("timeless"));
	query.timemore=DataFormat.parseInt(request.getParameter("timemore"));
	query.category=DataFormat.parseInt(request.getParameter("category"));

	query.sort=sort;
	int needAnalyze = DataFormat.parseInt(request.getParameter("needAnalyze"));
	if (needAnalyze == 0)
		query.needAnalyze = false;
	start = System.currentTimeMillis();
	
	System.out.println(query);
	result = (Result)SearchManager.getInstance().processQuery(query);
}
	end = System.currentTimeMillis();
%>
视频索引数：<%=videoQueryManager != null ?videoQueryManager.getIndexReader()!=null?videoQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
专辑索引数：<%=folderQueryManager != null ?folderQueryManager.getIndexReader()!=null?folderQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
会员索引数：<%=userQueryManager != null ?userQueryManager.getIndexReader()!=null?userQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
看吧帖子索引数：<%=barpostQueryManager != null ?barpostQueryManager.getIndexReader()!=null?barpostQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
PK索引数：<%=pkQueryManager != null ?pkQueryManager.getIndexReader()!=null?pkQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
Stat索引数：<%=statQueryManager != null ?statQueryManager.getIndexReader()!=null?statQueryManager.getIndexReader().numDocs()+"":"":""%><br/>
看吧索引数：<%=barQueryManager != null ?barQueryManager.getIndexReader()!=null?barQueryManager.getIndexReader().numDocs()+"":"":""%><br/>

<form name="" action="index.jsp" method="get">
搜索关键字：<input type="text" name="keywords">
排序：<input type="text" name="sort" size=4>（不填按相关度）<br>
<select name="field">
<option value="1">搜视频</option>
<option value="8">搜视频TAG</option>
<option value="2">搜专辑</option>
<option value="3">搜会员</option>
<option value="4">搜看吧</option>
<option value="5">按标题搜帖子</option>
<option value="6">按作者搜帖子</option>
<option value="7">搜PK</option>
<option value="15">智能纠错</option>
<option value="16">相关搜索</option>
<option value="18">标题全匹配</option>
<option value="19">标题和标签</option>
</select>
<input type="checkbox" value="1" name="needAnalyze" checked>需要分词
<br>
所在分类：<input type="text" name="category" size=4>  
视频长度小于：<input type="text" name="timeless" size=4>分钟  
视频长度大于：<input type="text" name="timemore" size=4>分钟  
<input type="submit" name="sub" value="提交">
</form>

<br>
<%=query!=null?query.toString():""%>
<br>
<%
if (result != null)
{
	String k = java.net.URLEncoder.encode(request.getParameter("keywords"));
%>
<a href="?keywords=<%=k%>&needAnalyze=<%=query.needAnalyze?"1":"0"%>&field=<%=query.field%>&sort=0">按相关度排序</a>
	<a href="?keywords=<%=k%>&needAnalyze=<%=query.needAnalyze?"1":"0"%>&field=<%=query.field%>&sort=<%=Constant.Sort.SORT_NEW%>">按最新排序</a>
	<a href="?keywords=<%=k%>&needAnalyze=<%=query.needAnalyze?"1":"0"%>&field=<%=query.field%>&sort=<%=Constant.Sort.SORT_PV%>">按PV排序</a>
	<a href="?keywords=<%=k%>&needAnalyze=<%=query.needAnalyze?"1":"0"%>&field=<%=query.field%>&sort=<%=Constant.Sort.SORT_COMMENT%>">按评论排序</a>
	<a href="?keywords=<%=k%>&needAnalyze=<%=query.needAnalyze?"1":"0"%>&field=<%=query.field%>&sort=<%=Constant.Sort.SORT_FAV%>">按收藏排序</a>
	<br>
<%
	out.println("返回结果集："+result.totalCount + " 耗时:" + (end -start)+"ms<br>");
	int total = result.totalCount>25?25:result.totalCount;
	List list = result.results;

	if (list != null && list.size()>0)
	{
		for (int i=0;i<list.size();i++)
		{
			if (query.field == 1 || query.field == 8 || query.field == 18 || query.field == 19)
			{
				Video video = (Video)list.get(i);
				//out.println(video.vid+"<br>");
				String title = video.title_hl!=null?video.title_hl:video.title;
				String tags = video.tags_hl!=null?video.tags_hl:video.tags;
				String username = video.username_hl!=null?video.username_hl:video.owner_username;

				out.println("结果：" + (i+1) + ",匹配度：" + video.score);
				out.println("vid="+video.vid+"		"+ video.title + "     创建人:" + username+ " 创建时间："+ DataFormat.formatDate(new java.util.Date(video.createtime),2) +"<br>");
				out.println("tags:" + tags+"<br>");
				out.println("memo:" + video.memo+"<br>");
				out.println("播放数:"+ video.total_pv + " 评论："+ video.total_comment + " 收藏："+ video.total_fav +"<br>");
				out.println(video.seconds);
				out.println("<br>===============================================================<br>");

			}
			else if (query.field == 2)
			{
				Folder folder = (Folder)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + folder.score);
				out.println("folder_id="+folder.pk_folder+"		"+folder.title+ "     创建人:" +folder.owner_name+ " 创建时间："+ DataFormat.formatDate(new java.util.Date(folder.update_time),2) +"<br>");
				out.println("tags:"+folder.tags);
				if (folder.video_title!=null){
				for (int j=0;j<folder.video_title.length;j++)
				{
					out.println("video " + j + " title=" +folder.video_title[j]);
					out.println("video " + j + " seconds=" +folder.video_seconds[j]);
					out.println("video " + j + " md5=" +folder.video_md5[j]);
					out.println("<br>");
				}
				}
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 3)
			{
				User user = (User)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + user.score);
				out.println(user.user_name+ "     icon64:" +user.icon64+ " video_count："+ user.video_count +"<br>");
				out.println(user.reg_date + "   " + user.last_content_date + "     " + user.last_login_date);
				
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 4)
			{
				Bar bar = (Bar)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + bar.score);
				out.println(bar.bar_name+ "     count_member:" +bar.count_member+ " count_subject："+ bar.count_subject +"<br>");
				out.println("所在分类：" + (bar.bar_catalog_ids==null?"null":bar.bar_catalog_ids.size()));
				String[] subjects = bar.subjects;
				if (subjects != null){
				for (String t:subjects)
				{
					out.println(t + "<br/>");
				}
				}
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 5)
			{
				BarPost barpost = (BarPost)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + barpost.score);
				out.println("看吧：("+ barpost.pk_bar +")"+barpost.bar_name +"   作者："+ barpost.poster_name +"<br>");
				out.println("标题：（"+barpost.fk_subject+"）"+barpost.subject +"<br>");
				out.println("视频logo：（"+barpost.videologo+"）"+barpost.encodeVid +"<br>");
				out.println("内容："+barpost.content +"<br>");
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 6)
			{
				BarPost barpost = (BarPost)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + barpost.score);
				out.println("看吧："+barpost.bar_name +"   作者："+ barpost.poster_name +"<br>");
				out.println("标题：（"+barpost.fk_subject+"）"+barpost.subject +"<br>");
				out.println("内容："+barpost.content +"<br>");
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 7)
			{
				Pk pk = (Pk)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + pk.score);
				out.println("logo："+pk.logo +"<br>");
				out.println("status："+pk.status +"<br>");
				out.println("标题："+pk.pk_name +"<br>");
				out.println("内容："+pk.description +"<br>");
				if (pk.video_title != null){
				for (int j=0;j<pk.video_title.length;j++)
				{
					out.println("video " + j + " title=" +pk.video_title[j]);
					out.println("<br>");
				}
				}
				out.println("<br>===============================================================<br>");
			}
			else if (query.field == 15 )
			{
				Stat stat = (Stat)list.get(i);
					out.print("<br><br>你要搜索的是不是:" + stat.keyword + " 查询数："+stat.query_count + " 结果数："+stat.result);
			}
			else if ( query.field == 16)
			{
				Stat stat = (Stat)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + stat.score);
				out.println("keyword："+stat.keyword +"<br>");
				out.println("query_count："+stat.query_count +"<br>");
				out.println("result："+stat.result +"<br>");
				out.println("<br>===============================================================<br>");
			}
			else if ( query.field == 17)
			{
				Ring ring = (Ring)list.get(i);
				out.println("结果：" + (i+1) + ",匹配度：" + ring.score);
				out.println("cname："+ring.cname +"<br>");
				out.println("csinger："+ring.csinger +"<br>");
				out.println("cprice："+ring.cprice +"<br>");
				out.println("cdate："+ring.cdate +"<br>");
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
