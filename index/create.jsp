<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.index.manager.*,
				com.youku.search.util.*,
				com.youku.search.index.*"%>
<%
int start = DataFormat.parseInt(request.getParameter("start"));
int end = DataFormat.parseInt(request.getParameter("end"));
	if (request.getParameter("video_button") != null)
	{
		VideoIndexManager.getInstance().createIndex(start,end);
		try
		{
			VideoIndexManager.getInstance().reopenIndex();
		}catch(Exception e)
		{}
		IndexPrepareThread t = new IndexPrepareThread();
		t.start();
	}
	else if (request.getParameter("folder_button") != null)
	{
		FolderIndexManager.getInstance().createIndex(start,end);
		FolderIndexManager.getInstance().reopenIndex();
	}
	else if (request.getParameter("user_button") != null)
	{
		UserIndexManager.getInstance().createIndex(start,end);
		UserIndexManager.getInstance().reopenIndex();
	}
	else if (request.getParameter("bar_button") != null)
	{
		BarIndexManager.getInstance().createIndex(start,end);
		BarIndexManager.getInstance().reopenIndex();
	}
	else if (request.getParameter("barpost_button") != null)
	{
		BarpostIndexManager.getInstance().createIndex(start,end);
		BarpostIndexManager.getInstance().reopenIndex();
	}
	else if (request.getParameter("pk_button") != null)
	{
		PkIndexManager.getInstance().createIndex(start,end);
		PkIndexManager.getInstance().reopenIndex();
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE> New Document </TITLE>
<META NAME="Generator" CONTENT="EditPlus">
<META NAME="Author" CONTENT="">
<META NAME="Keywords" CONTENT="">
<META NAME="Description" CONTENT="">
</HEAD>

<BODY>

<form name="" action="" method="post">
创建视频索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="video_button">
</form>

<form name="" action="" method="post">
创建专辑索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="folder_button">
</form>


<form name="" action="" method="post">
创建会员索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="user_button">
</form>

<form name="" action="" method="post">
创建看吧索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="bar_button">
</form>

<form name="" action="" method="post">
创建看吧帖子索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="barpost_button">
</form>

<form name="" action="" method="post">
创建PK索引：<br>
从<input type="text" name="start">
到<input type="text" name="end">
<input type="submit" value="提交" name="pk_button">
</form>

</BODY>
</HTML>
