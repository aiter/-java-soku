<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="shieldWord.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="shieldWord.title.edit"/></title>
    </s:if>
 
    <link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"   type="text/css"/>
    <link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>" rel="stylesheet"   type="text/css"/>
    
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>
	
	<script type="text/javascript"><!--
		$(function() {
			
			$("#startDate").datepicker({dateFormat: 'yy-mm-dd'});
			$("#endDate").datepicker({dateFormat: 'yy-mm-dd'});
			if($("#startDate").val() == "") {
				$("#startDate").datepicker("setDate", new Date());
			}

			if($("#endDate").val() == "") {
				$("#endDate").datepicker("setDate", new Date());
			}
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

		

		$('#shieldWordsubmit').click(function() {

			if($("#ShieldWord_save_shieldWordsBo_word").val() == "") {
				alert("关键词不能为空");
				return;
			}

			if(!checkRadioButtonSelect("wordtype")) {
				alert("选择关键词类型");
				return;
			}

			var wordtype1 = document.getElementById("wordtype1");
			var wordtype2 = document.getElementById("wordtype2");

			if(wordtype1.checked && wordtype2.checked) {
				$("#ShieldWord_save_searchParameter_type").val(3);
			} else if(wordtype1.checked && !wordtype2.checked) {
				$("#ShieldWord_save_searchParameter_type").val(1);
			}  else if(!wordtype1.checked && wordtype2.checked) {
				$("#ShieldWord_save_searchParameter_type").val(2);
			}
			
			

			var wordtype = $("#ShieldWord_save_searchParameter_type").val();
			$("#shieldWordsBoType").val(wordtype);
			

			/**
			
			var selectChannel = "";
			$("[name='shieldWordsBo.shieldChannelIdList'][checked]").each(function() {
				selectChannel += $(this).val();
			});
			alert(selectChannel);

			if(selectChannel == "") {
				//alert("至少选择一个生效频道");
				//return;
			}
			var selectHitRole = "";
			selectHitRole = $("[name='shieldWordsBo.hitRole'][checked]").val();

			alert(selectHitRole);
			if(selectHitRole == undefined || selectHitRole == "") {
				alert("请选择关键词命中规则");
				return;
			}

			var selectCategory = "";
			selectCategory = $("[name='shieldWordsBo.fkShieldCategoryId'][checked]").val();

			alert(selectCategory);
			if(selectCategory == undefined || selectCategory == "") {
				alert("请选择关键词分类");
				return;
			}*/

			if(!checkRadioButtonSelect("shieldWordsBo.shieldChannelIdList") && wordtype == 1) {
				alert("至少选择一个生效频道");
				return;
			}
		
			if(!checkRadioButtonSelect("shieldWordsBo.hitRole")) {
				alert("请选择关键词命中规则");
				return;
			}

			if(!checkRadioButtonSelect("shieldWordsBo.fkShieldCategoryId")) {
				alert("请选择关键词分类");
				return;
			}	

			if(!checkRadioButtonSelect("shieldWordsBo.wordSiteCategory") && wordtype == 2) {
				alert("请选择站点属性");
				return;
			}

			if(!checkRadioButtonSelect("shieldWordsBo.wordSiteLevel") && wordtype == 2) {
				alert("请选择站点分级");
				return;
			}
			
			$('#ShieldWord_save').submit();
		});

	

	});


		function checkRadioButtonSelect(name) {
			var selectHitRole = document.getElementsByName(name);
			var flag = false;
			for(j = 0; j < selectHitRole.length; j++)
				if(selectHitRole[j].checked){
					flag = true;
					break;
				}


			return flag;
		}
--></script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
    
  </div>
  <div id="middle">
    <s:include value="module/shieldleftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
        <h1>屏蔽词管理</h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="ShieldWord_save" validate="false" cssClass="form" theme="simple">

    <s:token />
    <s:hidden name="task"/>
    <s:hidden name="wordId" />
    <s:hidden name="searchParameter.type" />
    <input id="shieldWordsBoType" type="hidden" value="<s:property value="searchParameter.type" />" name="shieldWordsBo.type" />
          关键词： <s:textfield key="shieldWordsBo.word" cssClass="text"/>
          排它词：<s:textfield key="shieldWordsBo.excluding" cssClass="text"/>
          
  
    <fieldset> 
    	<legend>命中规则（单选）</legend>
    	<s:radio list="radioHitRoleMap" listKey="key" listValue="value" key="shieldWordsBo.hitRole"></s:radio>
    </fieldset>
    
	<fieldset>
		<legend>关键词分类（单选）</legend>
		<s:radio list="radioShieldCategoryMap" listKey="key" listValue="value" key="shieldWordsBo.fkShieldCategoryId"></s:radio>
	</fieldset>
	
	
	<input type="checkbox" name="wordtype" id="wordtype1" class="checkboxLabel" <s:if test="searchParameter.type == 1 || searchParameter.type == 3">checked="checked"</s:if>/>屏蔽词
    <fieldset>
    	<legend>选择生效频道（可多选）</legend>
    	<s:checkboxlist list="shieldChannelList" listKey="id" listValue="name" key="shieldWordsBo.shieldChannelIdList"></s:checkboxlist >
    </fieldset>
  	<input type="checkbox" name="wordtype" id="wordtype2" class="checkboxLabel" <s:if test="searchParameter.type == 2 || searchParameter.type == 3">checked="checked"</s:if>/>限定词
	<fieldset>
		<legend>限定词属性</legend>
		<fieldset>
		<legend>站点属性（单选）</legend>
		<s:radio list="radioSiteCategoryMap" listKey="key" listValue="value" key="shieldWordsBo.wordSiteCategory"></s:radio>
		</fieldset>
	
		<fieldset>
			<legend>站点分级（单选）</legend>
			<s:radio list="radioSiteLevelMap" listKey="key" listValue="value" key="shieldWordsBo.wordSiteLevel"></s:radio>
		</fieldset>
	</fieldset>
	
	<fieldset>
		<legend>生效时间</legend>
		<s:textfield key="shieldWordsBo.startTime" id="startDate"></s:textfield> 至 <s:textfield key="shieldWordsBo.expireTime" id="endDate"></s:textfield>
	</fieldset>
	
	<fieldset>
		<legend>备注</legend>
		<s:textarea key="shieldWordsBo.remark" cssClass="text" cols="80"/>
	</fieldset>
	

</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button"
			 id="shieldWordsubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="ShieldWord_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
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