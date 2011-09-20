<%@page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.*,
				com.youku.search.*
				com.youku.soku.index.analyzer.*,
				com.youku.search.analyzer.danga.MemCached.*,
				com.youku.search.util.*,
				java.sql.Connection,
				org.apache.torque.*,
				java.sql.*,
				java.util.*,
				org.apache.lucene.analysis.*,
				java.io.*"%>

分词：<form><input type="text" name=s ><input type="submit" value="submit"></form>
高级搜索分解：<form><input type="text" name=s2 ><input type="submit" value="submit"></form>
<%

if (request.getParameter("s") != null){
	//String keywords= MyUtil.prepareAndAnalyzeWord(request.getParameter("s"));
//	out.println(keywords);
String s = request.getParameter("s");


WholeWord tokens_title = WordProcessor.getWholeWord(s);
TokenStream ts = AnalyzerManager.getMyAnalyzer(false).tokenStream(new StringReader(s),tokens_title);
 Token t = null;
	try {
		while ((t = ts.next()) != null)
		{
			out.println(t.termText() + "\tstart="+t.startOffset() + "\tend="+t.endOffset() + "<br>");
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
}
if (request.getParameter("s2") != null){
	//String keywords= MyUtil.prepareAndAnalyzeWord(request.getParameter("s"));
//	out.println(keywords);
String s = request.getParameter("s2");

out.println(MyUtil.parseSyntax(s));
}
%>