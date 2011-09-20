<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html;charset=utf-8"%>
<%@page import="com.youku.soku.sort.word_match.WordMatcher,
				com.youku.soku.sort.ext.ExtInfoSearcher"%>
<font style="color:red;font-weight: bold;">提示：</font><br/>
   1、将搜索词，匹配字典中的词。<br/>
   2、如果选中{<font style="color:red;">使用原始词匹配</font>}，那么直接用搜索词，去匹配直达区</br>
     如果未选中，就用字典匹配后的词去匹配直达区<br/><br/>
<%
String keyword = null;
keyword = request.getParameter("keyword");
boolean useOrig =  false;
useOrig = request.getParameter("uo")!=null;
%>
搜索词：<form>
<input type="text" name="keyword" value="<%=keyword==null?"":keyword %>"/>
<br/>
使用原始词匹配 <input type="checkbox" name="uo" <%=useOrig?"checked":"" %>/>
<input type="submit" value="submit"/>

</form>
<%
if ( keyword != null){
	String dictKeyword = WordMatcher.process(keyword);
%>
<br/>字典匹配为：<font style="color:red;"><%=dictKeyword %></font>
<%
JSONObject ext = new JSONObject();
if(useOrig){
	ext = ExtInfoSearcher.searchByKeyword(keyword);
}else {
	ext = ExtInfoSearcher.searchByKeyword(dictKeyword);
}
%>
<br/><font style="color:red;"><%=useOrig?"原始词："+keyword:"字典词："+dictKeyword %></font> 直达区结果为：<%=ext.toString(4) %>
<%
}
%>
