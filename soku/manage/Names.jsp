<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="series.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="series.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {
			
			var name = $("#name"),				
				allFields = $([]).add(name),
				tips = $("#validateTips");
	
			function updateTips(t) {
				tips.text(t);
			}
	
			function checkLength(o,n,min,max) {
	
				if ( o.val().length > max || o.val().length < min ) {
					o.addClass('ui-state-error');
					updateTips(n + " 的长度应该在 " + min + " 和 "+max+"之间.");
					return false;
				} else {
					return true;
				}
	
			}
	
			function checkRegexp(o,regexp,n) {
	
				if ( !( regexp.test( o.val() ) ) ) {
					o.addClass('ui-state-error');
					updateTips(n);
					return false;
				} else {
					return true;
				}
	
			}


		

		$('#submitword').click(function() {
			var wordname = $("#Series_save_series_name"),			
			wordsort = $("#Series_save_series_sort"),
			allRequiredFields = $([]).add(wordname).add(wordsort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(wordname, '关键字', 1, 500);
			//bValid = bValid && checkRegexp(videoname,/^[a-z]([0-9a-z_])+$/i,"视频名格式不正确");

			



			if(!bValid)
				return false;
			else
				$('#Series_save').submit();
			return false;
		});

		$('#cancelword').click(function() {
			
		});

	});
</script>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1><s:text name="soku.series.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="Series_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="seriesId" />
   	<s:hidden name="pageNumber" />
    <s:hidden name="searchWord" />
    <s:textfield key="series.name" cssClass="text"/>
    <s:textfield key="series.alias" cssClass="text"/>
	<s:select list="categoryList" listKey="id" listValue="name" key="series.cate"></s:select>
	<s:textfield key="series.excluding" cssClass="text"/>

    

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="submitword">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="Series_list"><s:param name="pageNumber" value="1" /></s:url>" class="button" id="cancelword">
				<s:text name="button.cancel" /> 
			</a>
			
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>
</body>
</html>