<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="item.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="item.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
;
</script>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1>意见反馈</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="Item_save" validate="false" cssClass="form" >

    <s:token />
    
   
     <table class="listing" cellpadding="0" cellspacing="0">
     	<tr>
     		<td>错误类型</td>
     		<td>
     			<s:if test="errorType==0">
								其他
				</s:if>
     			<s:if test="feedback.errorType==1">
					电视、电影、综艺节目、动慢，剧集有错误 
				</s:if>
				<s:if test="feedback.errorType==2">
					搜索找不到您需要的内容 
				</s:if>
				<s:if test="feedback.errorType==3">
					结果包含不健康信息  
				</s:if>
     		</td>
     	</tr>
     	<tr class="bg">
     		<td>
     			关键字
     		</td>
     		<td>
     			<s:property value="keyword" />
     		</td>
     	</tr>
     	<tr>
     		<td>
     			意见描述
     		</td>
     		<td>
     			<s:property value="feedback.description" />
     		</td>
     	</tr>
     	<tr class="bg">
     		<td>
     			url
     		</td>
     		<td>
     			<s:property value="feedback.url" />
     		</td>
     	</tr>
     	<tr>
     		<td>
     			qq
     		</td>
     		<td>
     			<s:property value="feedback.qq" />
     		</td>
     	</tr>
     	<tr class="bg">
     		<td>
     			email
     		</td>
     		<td>
     			<s:property value="feedback.email" />
     		</td>
     	</tr>
     	<tr>
     		<td>
     			时间
     		</td>
     		<td>
     			<s:date name="feedback.createtime" format="yyyy-MM-dd HH:mm:ss" />
     		</td>
     	</tr>	
     </table>

   

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
		
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>
</body>
</html>