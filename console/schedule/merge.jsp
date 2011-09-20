<%@ page contentType="text/html;charset=utf-8" language="java" import="java.sql.Connection,com.youku.search.console.operate.log.*,java.util.*,com.youku.search.util.*,com.youku.search.console.operate.*"%>
<%
long s = System.currentTimeMillis();
System.out.println(new Date()+"--start--每周关键词合并,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory());
String end = request.getParameter("end");
if(end==null) end=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_SPECIAL);
String start = request.getParameter("start");
if(start==null) start=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-7), DataFormat.FMT_DATE_SPECIAL);
KeywordPerWeek kp=new KeywordPerWeek();
String table = new StringBuilder("keyword_").append(start).append("_").append(end).toString();
Connection inconn = null;
Connection outconn = null;
try {
	List<String> uniondates = new ArrayList<String>();
	for(int i=0;i<7;i++){
		uniondates.add(DataFormat.formatDate(DataFormat.getNextDate(DataFormat.parseUtilDate(start,DataFormat.FMT_DATE_SPECIAL),i), DataFormat.FMT_DATE_YYYY_MM_DD));
	}
	inconn = DataConn.getLogStatConn();
	System.out.println("更新 table:"+table);
	//站内查询
	kp.unionQueryKeyword(inconn, table,"query_",uniondates);
	System.out.println("站内搜索数据更新完毕");
	outconn = DataConn.getWebLogStatConn();
	//站外查询
	kp.unionQueryKeyword(outconn, table,"query_",uniondates);
	System.out.println("站外搜索数据更新完毕");
	//站外点击
	kp.unionClickKeyword(outconn, new StringBuilder("click_").append(start).append("_").append(end).toString(),"click_",uniondates);
	System.out.println("站外点击数据更新完毕");
} catch (Exception e) {
	e.printStackTrace();
}finally{
	DataConn.releaseConn(inconn);
	DataConn.releaseConn(outconn);
}
System.out.println(new Date()+"--end--每周关键词合并,总内存:" + Runtime.getRuntime().totalMemory()+",剩余内存:"+Runtime.getRuntime().freeMemory()+",占用时间:"+(System.currentTimeMillis()-s));

 %>