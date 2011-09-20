<%@ page contentType="text/html;charset=utf-8" language="java" import="java.util.*,com.youku.search.console.vo.LeftMenuVO,com.youku.search.console.operate.rights.UserMgt,com.youku.search.console.util.Convert"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML><HEAD><TITLE>菜单</TITLE>
<LINK
href="images/dtree.css" type=text/css rel=StyleSheet>
<META content="MSHTML 6.00.2900.2995" name=GENERATOR></HEAD>
<BODY bgColor=#f2f2f2>
<%
out.print("<SCRIPT src=\"images/dtree.js\" type=text/javascript></SCRIPT>");
out.print("<SCRIPT src=\"images/common.js\" type=text/javascript></SCRIPT>");
out.print("<DIV class=dtree>");
out.print("<P><A href=\"javascript:%20d.openAll();\">全部打开</A> |");
out.print("<A href=\"javascript:%20d.closeAll();\">全部关闭</A></P>");

out.print("<SCRIPT type=text/javascript>");
out.print("var d = new dTree(\"d\",\"images/\");");
out.print("d.add(0,-1,'系统管理面板');");
List<LeftMenuVO> ml=null;
LeftMenuVO[] mvos;
try{
ml=UserMgt.getInstance().mapToList((LinkedHashMap<Integer,LeftMenuVO>)session.getAttribute("urlmap"));
}catch(Exception e){}
int i=0;
if(ml!=null&&ml.size()>0){
for(;i<ml.size();i++){
out.print("d.add("+(i+1)+",0,'"+ml.get(i).getName()+"');");
mvos=Convert.setToArray(ml.get(i).getTwoMenu());
if(null!=mvos&&mvos.length>0){
for(int j=0;j<mvos.length;j++){
out.print("d.add("+(j+100)+","+(i+1)+",'"+mvos[j].getName()+"','"+mvos[j].getUrl()+"','"+mvos[j].getName()+"',\"main\");");
}
}
}
out.print("d.add("+(i+1)+",0,\"资料修改\",\"useredit.html\",\"资料修改\",\"main\");");
}
out.print("d.add("+(i+2)+",0,\"退出系统\",\"userlogout.html\",\"退出系统\",\"_parent\");");
out.print("document.write(d);");
out.print("d.openAll();");
out.print("</SCRIPT>");
out.print("</DIV>");
 %>
</BODY></HTML>
