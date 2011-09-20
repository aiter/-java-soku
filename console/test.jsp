<%@ page contentType="text/html;charset=utf-8" language="java" import="com.youku.search.recomend.DateRead,com.youku.search.console.operate.DateConn,java.sql.Connection,com.youku.search.console.operate.*,com.youku.search.console.operate.LogDateRead,com.youku.search.recomend.*"%>
<%
TeleplayOneUsed t=new TeleplayOneUsed();
//VideoApiCompare vac=new VideoApiCompare();
//VideoCompare vc=new VideoCompare();
long third=System.currentTimeMillis();
t.updateEpisodeCount();
System.out.println("更新episode_count字段,用时:"+(System.currentTimeMillis()-third));
//long four=System.currentTimeMillis();
//t.updateVersion();
//System.out.println("更新firstlogo字段,用时:"+(System.currentTimeMillis()-four));
//long five=System.currentTimeMillis();
//t.updateVersionFix();
//System.out.println("更新fix字段,用时:"+(System.currentTimeMillis()-five));
//long six=System.currentTimeMillis();
//t.deleteevs();
//vc.videoCom();
//vac.apiCompare();
//System.out.println("删除episode_video中重复的,用时:"+(System.currentTimeMillis()-six));
//System.out.println("比较,用时:"+(System.currentTimeMillis()-six));
//long eight=System.currentTimeMillis();
//t.updateCate();
//System.out.println("更新cate字段,用时:"+(System.currentTimeMillis()-eight));

//long eight=System.currentTimeMillis();
//t.addToEPisodeVideo();
//System.out.println("添加episode_video,用时:"+(System.currentTimeMillis()-eight));

//long night=System.currentTimeMillis();
//t.deleteFeedbackBykeyword();
//System.out.println("删除feedback数据,用时:"+(System.currentTimeMillis()-night));
//long b=System.currentTimeMillis();
//t.write();
//System.out.println("输出美剧,用时:"+(System.currentTimeMillis()-b));

//long first=System.currentTimeMillis();
//t.updateSecond();
//System.out.println("更新seconds数据,用时:"+(System.currentTimeMillis()-first));

//long second=System.currentTimeMillis();
//t.callApis();
//System.out.println("调用api,用时:"+(System.currentTimeMillis()-second));

//long a=System.currentTimeMillis();
//t.updateLogo();
//System.out.println("更新logo,用时:"+(System.currentTimeMillis()-a));
//long l=System.currentTimeMillis();
//t.deleteEpisodeVideos();
//System.out.println("删除episode_video数据,用时:"+(System.currentTimeMillis()-l));


 %>