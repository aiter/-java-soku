<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="varietyEpisode.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="varietyEpisode.title.edit"/></title>
    </s:if>
        <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	


	
	<script type="text/javascript">
		$(function() {

			$("#epDate").datepicker({dateFormat: 'yy-mm-dd'});
			//$("#epDate").datepicker("setDate", new Date());
			
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


		

		$('#varietyEpisodesubmit').click(function() {
			/**var varietyEpisodename = $("#VarietyEpisode_save_varietyEpisode_name"),	
			varietyEpisodeurl = $("#VarietyEpisode_save_varietyEpisode_url"),		
			varietyEpisodesort = $("#VarietyEpisode_save_varietyEpisode_sort"),
			allRequiredFields = $([]).add(varietyEpisodename).add(varietyEpisodeurl).add(varietyEpisodesort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(varietyEpisodename, '栏目名称', 1, 200);

			bValid = bValid && checkLength(varietyEpisodeurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(varietyEpisodeurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(varietyEpisodesort, '排序', 1, 300);
			bValid = bValid && checkRegexp(varietyEpisodesort,/^[0-9]+$/i,"排序为数字");

			
			if(!bValid)
				return false;
			else
				$('#VarietyEpisode_save').submit();
			return false;**/
			$('#VarietyEpisode_save').submit();
		});

	

	});
</script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1><s:text name="soku.varietyEpisode.title" /></h1>
        
      </div>
      <p>标红的字段填写不当可能会造成数据库错误</p>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="VarietyEpisode_save" validate="false" cssClass="form" >

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="varietyEpisodeId" />
    <s:hidden name="varietyEpisode.fkVarietySubId" />
    <s:hidden name="varietyId" />
    <s:hidden name="varietySubId" />
    <s:hidden name="namesId" />
    <s:textfield key="varietyEpisode.names" cssClass="text" readonly="true"></s:textfield>
    <s:if test="varietyEpisode.orderIdType == 1">
    	<tr>
    	<td><p class="highlight" >节目日期：</p></td>
    	<td><s:textfield id="epDate" key="varietyEpisode.epDate" cssClass="text" theme="simple"></s:textfield></td>
    	</tr>
    </s:if>
    <s:if test="varietyEpisode.orderIdType == 0">
    	<tr>
    	<td><p class="highlight" >节目序号：</p></td>
    	<td><s:textfield key="varietyEpisode.epDate" cssClass="text" theme="simple"></s:textfield></td>
    	</tr>
    </s:if>
    <s:if test="varietyEpisode.orderIdType == 2">
    	<tr>
    	<td><p class="highlight" >新节目，排序类型不能确定。可选格式：数字，年-月-日</p></td>
    	<td><s:textfield key="varietyEpisode.epDate" cssClass="text" theme="simple"></s:textfield></td>
    	</tr>
    </s:if>
     <tr>
    	<td><p class="highlight" >日期重复：</p></td>
    	<td><s:checkbox name="sameDate" fieldValue="true" theme="simple"/></td>
    	</tr>
    <s:textfield id="subName" key="varietyEpisode.varietySubName" cssClass="text" ></s:textfield>
    
    <tr>
    	<td><p class="highlight" >站点名：</p></td>
    	<td><s:select key="varietyEpisode.sourceSite" list="sitesMap" listKey="key" listValue="value" theme="simple"></s:select></td>
    	</tr>
    <s:textfield key="varietyEpisode.title" cssClass="text"/>
    <s:textfield key="varietyEpisode.orderId"  value="30" cssClass="text"/>
   	
	<s:select key="varietyEpisode.hd" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
	<s:textfield key="varietyEpisode.url" cssClass="text"/>
	<s:textfield key="varietyEpisode.readyUrl" cssClass="text"/>
    

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="varietyEpisodesubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="VarietyEpisode_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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