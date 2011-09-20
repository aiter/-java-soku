<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.soku.*,
				com.youku.search.util.*,
				org.json.*"%>
<%
String keyword= request.getParameter("keyword");
%>
<form name="form1">
关键字：<input type="text" name="keyword" value="<%=keyword!=null?keyword:""%>"><input type="submit" value="搜素">
</form>
<%
int p = DataFormat.parseInt(request.getParameter("p"),1);
int total = 0;
int pagesize=20;
int cost = 0;
JSONArray ja = null;
int totalpage = 1;
if (keyword!=null && keyword.trim().length()>0)
{
	keyword = keyword.trim();
	String s = Request.requestGet("http://127.0.0.1/search?keyword="+StringUtil.urlEncode(keyword,"UTF8")+"&curpage="+p+"&pagesize="+pagesize);
	JSONObject json = new JSONObject(s);
	total = json.getInt("total");
	cost = json.getInt("cost");
	ja = json.getJSONArray("items");
	out.print(total+"个结果,耗时："+cost+"毫秒<br/>");
	out.println("<table width=\"500\" border=\"0\">");
	if (ja != null)
	{
		for (int i = 0;i< ja.length();i++)
		{
			JSONObject jo = ja.optJSONObject(i);
			%>
			 <tr>
			<td width="127" rowspan="3"><a href="<%=jo.getString("url")%>" target="_blank"><img src="<%=jo.getString("logo")%>" width="120" border=0></a></td>
			<td width="363"><a href="<%=jo.getString("url")%>" target="_blank"><%=jo.getString("title")%></a>[<%=jo.getInt("seconds")/60%>分<%=jo.getInt("seconds")%60%>秒]</td>
			</tr>
			<tr>
			<td>标签：
			<%
			try{
				out.print(jo.getString("tags"));
			}catch(Exception e)
			{}
			%></td>
			</tr>
			<tr>
			<td>来源：<%=jo.getString("site")%></td>
			</tr>
			<%
		}
	}
	out.println("</table>");
	totalpage = (total+pagesize-1)/pagesize;
	for(int i = 1;i<= totalpage;i++)
	{
		if (i == p)
			out.println("<font color=red>"+i + "</font>&nbsp;");
		else
			out.println("<a href='?keyword="+ StringUtil.urlEncode(keyword,"UTF8") +"&p="+i+"'>"+i + "</a>&nbsp;");
	}
}
%>
