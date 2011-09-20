<%@ page contentType="text/html; charset=utf-8" language="java" %>
    <% { %>
    <% String barname = bar == null ? "" : bar.optString("barname"); %>
        <div class="barentrance">
            <a href="http://kanba.youku.com/bar_bar/word_<%= WebUtils.urlEncode(webParam.getQ()) %>" target="_blank" charset="801-4-1">
            <% if(barname.length() == 0) { %>
                创建<span class="key"><%= WebUtils.htmlEscape(webParam.getQ()) %></span>的看吧
            <% } else { %>
                查看<span class="key"><%= WebUtils.htmlEscape(barname) %></span>的相关视频讨论<span class="num"><%= bar.optInt("subjectcount") %></span>篇
            <% } %>
            </a>
    </div>
    <% } %>