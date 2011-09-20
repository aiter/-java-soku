<%@ page contentType="text/html; charset=utf-8" language="java" %>

<%
String charsetPrevious  = "";
String charsetPage      = "";
String charsetNext      = "";

if("video".equals(searchType)){
    charsetPrevious = "801-2-1";
    charsetPage     = "801-2-2";
    charsetNext     = "801-2-3";
} else if ("playlist".equals(searchType)){
    charsetPrevious = "803-2-1";
    charsetPage     = "803-2-2";
    charsetNext     = "803-2-3";
}


int total=0;
if(result!=null){
	total= result.optInt("total");
}else if(postsearchJson!=null){
	total=postsearchJson.optInt("total");
	
}
int page_size   = webParam.getPagesize();
int page_count  = total / page_size + (total % page_size == 0 ? 0 : 1);

if(page_count > 100){
    page_count = 100;
}
%>

<% if (page_count > 1) { %>
    <%--上一页--%>
    <% if (webParam.getPage() > 1) { %>
	    <li class="prev"><a href="<%= base_url %><%= WebParamHelper.encode(webParam, "page_" + (webParam.getPage() - 1))%>" title='上一页' <%= WebUtils.eq(charsetPrevious, "", "", "charset=\"" + charsetPrevious + "\"") %>>上一页</a></li>
	<% } %>

    <%--每一页--%>
        <%
        int pageMin = webParam.getPage() - 4;
        if (pageMin < 1) {
            pageMin = 1;
        }
        
        int pageMax = webParam.getPage() + 5;
        if (pageMax < pageMin + 9) {
            pageMax = pageMin + 9;
        } else {
            pageMax = pageMin + 9;
        }
        if (pageMax > page_count) {
            pageMax = page_count;
        }
       
        if(pageMax==100) pageMin=91;
        for (int i = pageMin; i <= pageMax; i++) {
            if (i == webParam.getPage()) {
        %>
        <li class="current"><span><%= i %></span></li>
        <%    
            } else {
        %>
        <li><a href="<%= base_url %><%= WebParamHelper.encode(webParam, "page_" + i)%>" title='第<%= i %>页' <%= WebUtils.eq(charsetPage, "", "", "charset=\"" + charsetPage + "-" + i + "\"") %>><%= i %></a></li>
        <%        
            }
        }
        %>
    
    <%--下一页--%>
    <% if (webParam.getPage() < page_count) { %>
    <li class="next"><a href="<%= base_url %><%= WebParamHelper.encode(webParam, "page_" + (webParam.getPage() + 1))%>" title='下一页' <%= WebUtils.eq(charsetNext, "", "", "charset=\"" + charsetNext + "\"") %>>下一页</a></li>
    <% } %>
<% } %>