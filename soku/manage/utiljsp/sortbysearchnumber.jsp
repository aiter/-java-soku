<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page buffer="1kb" %>
<%@ page import="com.youku.soku.manage.jsp.*, 
java.util.List,
java.util.ArrayList,
java.util.Comparator,
java.util.StringTokenizer,
java.util.Collections"%>

<%
	class Word {
		int searchNum;
		String word;
	}
	StringBuilder result = new StringBuilder();
	String words = request.getParameter("words");
	
	if(words != null) {
		StringTokenizer token = new StringTokenizer(words, "\n\r");
		List<Word> list = new ArrayList<Word>();
		
		while(token.hasMoreTokens()) {
			String word = token.nextToken();
			System.out.println(word);
					
			int searchNum = TypeWordsSearchNumberInit.getSearchNumber(word);
			Word w = new Word();
			w.word = word;
			w.searchNum = searchNum;
			list.add(w);
		}
		Collections.sort(list, new Comparator<Word>() {
			public int compare(Word w1, Word w2) {
				return w2.searchNum - w1.searchNum;
			}
		})	;	
		
		for(Word word : list) {
			result.append(word.word);
			result.append("                ");
			result.append(word.searchNum);
			result.append('\n');
		}
	}
%>
<form action="sortbysearchnumber.jsp" method="post">
<table>
<tr>
<td><textarea name="words" rows="20" cols="50"><%=words %></textarea>
<input type="submit" value="排序" /></td>
<td>Result:
<textarea rows="20" cols="50"><%=result.toString() %></textarea></td>
</tr>
</table>

</form>

