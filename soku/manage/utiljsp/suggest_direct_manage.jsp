<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page buffer="1kb" %>
<%@ page import="com.youku.soku.suggest.trie.*, 
com.youku.soku.suggest.parser.*,
com.youku.soku.suggest.data.*,
com.youku.soku.suggest.entity.*,
com.youku.soku.suggest.trie.*,
java.util.List,
java.util.Map,
com.youku.soku.web.util.WebUtil,
java.util.Collection,
com.youku.soku.manage.shield.ShieldInfo,
com.youku.soku.shield.Filter,
com.youku.soku.shield.Filter.Source,
com.youku.soku.suggest.parser.WordProcessor"%>
<%
response.resetBuffer();
%>
查看：
<form action="suggest_direct_manage.jsp">
		<input name="o" value="ls" type="hidden" />
        <input name="k" value="" type="text" />
        <input type="submit" />
</form>
删除：
<form action="suggest_direct_manage.jsp">
		<input name="o" value="rm" type="hidden" />
        <input name="k" value="" type="text" />
        <input type="submit" />
</form>
<%
//soku站外版  主要区别在详情页
long startTime = System.currentTimeMillis();
	if(request.getParameter("k") != null){
		String operatType = request.getParameter("o");
		String keyword = request.getParameter("k");

		try {
			

			TrieTree tree = TrieTreeHolder.getCurrentThreadLocal();	
			Map<String, List<DirectEntity>> directEntityMap = tree.getDirectEntityMapp();

			
			
			System.out.println("operat type: " + operatType + " keyword: " + keyword);
			if(operatType.equals("ls")) {
				List<DirectEntity> resultList = directEntityMap.get(keyword);
				for(DirectEntity d : resultList) {
					out.print(d);
					out.print("<br />");
				}
			} else if(operatType.equals("rm")) {
				directEntityMap.remove(keyword);
			}
		
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			TrieTreeHolder.removeCurrentTrheadLocal();
		}
	
		
		
	}
	long endTime = System.currentTimeMillis();
%>
