<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<s:if test="task=='Create'">
        <title><s:text name="variety.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="variety.title.edit"/></title>
    </s:if>
    <link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />

	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {

			$('#uploadposterimg').click(function() {
				$('#imagedialog').dialog('open');

			});

			$("#imagedialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				buttons: {
					'确认': function() {						
						var bValid = true;

						/*
						if($("#FileUpload_upload").val().trim() == "") {
							bValid = false;
							if($("#picPath").val().trim() != "") {
								var orderId = $("#currentOrderId").val();							     				
						        $('#logoUrl' + orderId).val($("#picPath").val());	
						        $('#logoImg' + orderId).attr('src', $("#picPath").val());
						        $(this).dialog('close');
							}
						}*/
							
						if (bValid) {
						$('#FileUpload').submit();
						$(this).dialog('close');
				}
			},
			'取消' : function() {
				$(this).dialog('close');
			}
			},
			close : function() {
				//allFields.val('').removeClass('ui-state-error');
			}
		});

			$("#subVariety").click(function() {
				var subVariety = '<s:url action="VarietySub_list"><s:param name="varietyId" value="varietyId" /><s:param name="varietySubId" value="-1" /></s:url>';
				document.location = subVariety;
				
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$("#varietyEpisode").click(function() {
				
				document.location = $("#varietyEpisodeUrl").val();
				
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$("#dialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
					'确定': function() {
						var personName;
						var obj = document.getElementsByName("cboxperson");
						for( i= 0; i < obj.length; i++) {
							if(obj[i].checked) {
								personName = $("#pernames" + obj[i].value).val();
							}
						}

						if(personName == undefined){
							alert('请选择一个人物');
							return;
						}
						var personRoleId = $("#personRoleId").val();
						var personRoleName = $("#personRoleId option:selected").text();
						
						addRow(personRoleId, personRoleName, personName);
						$(this).dialog('close');
				},
				'取消' : function() {
					$(this).dialog('close');
				}
				},
				close : function() {
					allFields.val('').removeClass('ui-state-error');
				}
			});

			$("#dialogNames").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
					'确定': function() {
				var obj = document.getElementsByName("cbox");
				for( i= 0; i < obj.length; i++) {
					if(obj[i].checked) {
						$("#Variety_save_variety_fkNamesId").val(obj[i].value);
						$("#Variety_save_variety_names").val($("#names" + obj[i].value).val());
					}
				}
						$(this).dialog('close');
				},
				'取消' : function() {
					$(this).dialog('close');
				}
				},
				close : function() {
					allFields.val('').removeClass('ui-state-error');
				}
			});

			$('#addPerson').click(function() {
				$('#dialog').dialog('open');
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});
				
			$('#editNames').click(function() {
				$('#dialogNames').dialog('open');
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});
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


		

		$('#varietysubmit').click(function() {
			/**var varietyname = $("#Variety_save_variety_name"),	
			varietyurl = $("#Variety_save_variety_url"),		
			varietysort = $("#Variety_save_variety_sort"),
			allRequiredFields = $([]).add(varietyname).add(varietyurl).add(varietysort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(varietyname, '栏目名称', 1, 200);

			bValid = bValid && checkLength(varietyurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(varietyurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(varietysort, '排序', 1, 300);
			bValid = bValid && checkRegexp(varietysort,/^[0-9]+$/i,"排序为数字");

			
			if(!bValid)
				return false;
			else
				$('#Variety_save').submit();
			return false;**/
			bValid = checkSearchKeys();
			if(bValid == "false") {
				$("#Variety_save_variety_searchkeys").focus();
				return false;
			} else {
				$('#Variety_save').submit();
			}
			
		});

	

	});

		function addRow(personRoleId, personRoleName, personName) {
			var rowNumber = $("#Variety_save_variety_perSubList_size").val();
			if(rowNumber == "") {
				rowNumber = 0;
			}
			var hiddenHtml = '<input type="hidden" id="Variety_save_variety_perSubList_' + rowNumber + '__fkRoleId" value="' + personRoleId+ '" name="variety.perSubList[' + rowNumber + '].fkRoleId">'
			var tdhtml = '<input type="text" id="Variety_save_variety_perSubList_' + rowNumber + '__personName" value="' + personName + '" name="variety.perSubList[' + rowNumber + '].personName">';
			$("table.form tr:last").after("<tr>" + hiddenHtml + "<td>" + personRoleName + "</td><td>" + tdhtml + "</td></tr>");

			rowNumber++;
			$("#Variety_save_variety_perSubList_size").val(rowNumber);
		}

		function searchNames() {
			var searchWord = $("#diaSearchWord").val();
			if(searchWord == "") {
				alert('搜索关键字不能为空');
				return;
			}
			$.ajax({
				url: '<s:url action="Names_listJson" />',
				type: 'POST',
				data: 'searchWord=' + searchWord,
				success: function(data) {
					if(data == "[]") {
						var url = '<s:url action="Names_input"><s:param name="namesId" value="-1" /></s:url>';
						var resStr = '所搜索的系列名不存在，点击 <a href="' + url + '" class="listbutton" >此处 </a>添加';
						$("#diaResult").html(resStr);	
						return;
					}
					var resultStr = "<table>";
					var jsonArr = eval('(' + data + ')');	
					for(var idx in jsonArr) {
						resultStr += '<tr><td><input type="checkbox" name="cbox" value="' + jsonArr[idx].id + '" onClick="chooseOne(this);">';
						resultStr += jsonArr[idx].name + ' (' + jsonArr[idx].category + ')</td></tr>';
						resultStr += '<input type="hidden" id="names' + jsonArr[idx].id + '" value="' + jsonArr[idx].name + '" />';
						
					}
					
					$("#diaResult").html(resultStr);					
				
				    
				}
			});
		}

		function searchPersons() {
			var searchWord = $("#perSearchWord").val();
			if(searchWord == "") {
				alert('搜索关键字不能为空');
				return;
			}
			$.ajax({
				url: '<s:url action="Person_listJson" />',
				type: 'POST',
				data: 'searchWord=' + searchWord,
				success: function(data) {
					if(data == "[]") {
						var url = '<s:url action="Person_input"><s:param name="personId" value="-1" /></s:url>';
						var resStr = '所搜索的人物名不存在，点击 <a href="' + url + '" class="listbutton" >此处 </a>添加';
						$("#perResult").html(resStr);	
						return;
					}
					var resultStr = "<table>";
					var jsonArr = eval('(' + data + ')');	
					for(var idx in jsonArr) {
						resultStr += '<tr><td><input type="checkbox" name="cboxperson" value="' + jsonArr[idx].id + '" onClick="chooseOne(this);">';
						resultStr += jsonArr[idx].name + '</td></tr>';
						resultStr += '<input type="hidden" id="pernames' + jsonArr[idx].id + '" value="' + jsonArr[idx].name + '" />';
						
					}
					
					$("#perResult").html(resultStr);					
				
				    
				}
			});
		}

		function chooseOne(cb) {
			var obj = document.getElementsByName("cbox");
			for( i= 0; i < obj.length; i++) {
				if(obj[i] != cb)
					obj[i].checked = false;
				else
					obj[i].checked = cb.checked;
			}
		}

		$(document).ready(function() { 
		    var options = { 
		        //target:        '#output',   // target element(s) to be updated with server response 
		        beforeSubmit:  showRequest,  // pre-submit callback 
		        success:       showResponse  // post-submit callback 		 
		      
		    }; 
		 
		    // bind form using 'ajaxForm' 
		    $('#FileUpload').ajaxForm(options); 
		}); 

		
		 
		// pre-submit callback 
		function showRequest(formData, jqForm, options) { 
		    			 
		    return true; 
		} 
		 
		// post-submit callback 
		function showResponse(responseText, statusText)  { 
			var orderId = $("#currentOrderId").val();
		    var o = eval('(' + responseText + ')');  				
	        

		    $('#Variety_save_variety_haibao').val(o.filePath);
        	$('#posterImg').attr('style', '');	
       	 	$('#posterImg').attr('src', o.filePath);
	       // $('#Video_save_video_picturePath').attr("disabled","disabled");
		     
	        	         
		} 

		function checkSearchKeys() {
			
			var searchkeys = $("#Variety_save_variety_searchkeys").val();
			var id = $("#Variety_save_varietyId").val();
			var status;
			$.ajax({
				url: '<s:url action="Variety_checkSearchKeys" />',
				type: 'POST',
				data: 'searchKeys=' + searchkeys + '&varietyId=' + id,
				success: function(data) {
					var o = eval('(' + data + ')');
					if(o.status == "false") {
						$("#Variety_save_variety_searchkeys").css("background", "#ff0000");
						$("#searchKeyError").show();
						$('#searchkeystatus').val(false);
					} else {
						$("#Variety_save_variety_searchkeys").css("background", "");
						$("#searchKeyError").hide();
						$('#searchkeystatus').val(true);;
					}
										
				}
			});

			return $('#searchkeystatus').val();;
		}

		
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
        <h1><s:text name="soku.variety.title" /></h1>
        
      </div>
      <br />
     
      
      <div class="table"> 
        <s:actionerror cssClass="actionerror"/>
        <p id="validateTips"></p>
<s:form action="Variety_save" validate="false" cssClass="form" theme="simple">

    <s:token />
    <s:hidden name="variety.fkNamesId" />
    <s:hidden name="task"/>
    <s:hidden name="varietyId" />
    <s:hidden name="namesId" />
    <s:hidden name="pageNumber" />
    <s:hidden name="searchWord" />
    <s:hidden name="categoryFilter" />
    
    <s:if test="varietyId == -1"><input type="button" id="editNames" class="ui-button ui-state-default ui-corner-all" value="选择系列名" /></s:if> 
    <s:if test="varietyId != -1"><input type="button" id="editNames" class="ui-button ui-state-default ui-corner-all" value="更改系列名" /></s:if>
    <br />
    
    		<label>节目名：</label> 
   			<s:if test="varietyId != -1"><s:textfield key="variety.names" cssClass="text" readonly="true" /></s:if>
    		<s:else><s:textfield key="variety.names" cssClass="text" /></s:else>
    <label>别名：</label>
    		<s:textfield key="variety.namesAlias" cssClass="text" />
    	<br />
    	
    		<label>版本名：</label>
    		<s:textfield key="variety.name" cssClass="text"/>
    	<br />
    		<label>搜索词：</label>
    		<s:textfield key="variety.searchkeys" cssClass="text" onblur="checkSearchKeys();"/>
			<span id="searchKeyError" style="display:none; color: #ff0000">搜索词重复</span>
			<input type="hidden" id="searchkeystatus" />
    	<br />
    	<!-- 
    		<label>上映时间：</label>
     		<s:textfield key="variety.releaseTime" cssClass="text"/>
     		<label>是否上映完成：</label>
     		<s:select key="variety.released" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
     		<br /> 
     	-->
     		排序： <s:textfield key="variety.orderId" cssClass="text"/>
     		
     		<input type="button" id="subVariety" class="ui-button ui-state-default ui-corner-all" value="编辑播放地址" />
     		<!-- <label>默认展示站点： </label> -->
     	<br />
     		<label>分类：</label>
     		<s:checkboxlist  list="varietyCateList" key="variety.cateList"></s:checkboxlist >
     	<br />
     		<label>封面图片：</label>
     		<s:textfield key="variety.haibao" cssClass="text"/> 
     		<img src="<s:property value="variety.haibao" />" id="posterImg" <s:if test="variety.haibao == null">style="visibility: hidden"</s:if> />
 			<input type="button" id="uploadposterimg" class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>
 		<br />
 			地区          ： <s:select key="variety.area" list="areaList" cssClass="text"></s:select>
			语言： <s:select key="variety.language" list="languageList" cssClass="text"></s:select>
		<br />
			优酷是否有版权：<s:select key="variety.haveRight" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
		<br />
			主持人：　<s:textfield key="variety.hostName" cssClass="text" size="60"/>
		<br />
	
			所属电台：　<s:textfield key="variety.tvsite" cssClass="text" size="60"/>  
		<br />
			
			简介： <s:textarea key="variety.brief" cssClass="textarea" cols="60" rows="10"/>	
    <!--  
    <tr>
		
		<td>
				
		</td>
		<td>	
			<input type="button" id="subVariety" class="ui-button ui-state-default ui-corner-all" value="查看子节目" />	
			<input type="button" id="varietyEpisode" class="ui-button ui-state-default ui-corner-all" value="添加站点子节目" />		
			<input type="hidden" id="varietyEpisodeUrl" value='<s:url action="VarietyEpisode_input"><s:param name="varietyId" value="varietyId" /><s:param name="varietyEpisodeId" value="-1" /></s:url>' />		
		</td>
	</tr>
    <s:textfield key="variety.names" cssClass="text" disabled="true"></s:textfield>
    <s:textfield key="variety.name" cssClass="text"/>
    <s:textfield key="variety.cates" cssClass="text"/>    
	<s:textarea key="variety.brief" cssClass="textarea" cols="44" rows="10"/>
	<s:textfield key="variety.area" cssClass="text"/>
	<s:select key="variety.haveRight" list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
    
    <s:hidden name="variety.perSubList.size" cssClass="text"/>
    
	<s:iterator value="variety.perSubList" status="index">
		<tr>
			<s:hidden name="variety.perSubList[%{#index.index}].id"></s:hidden>
			<td>
				<s:select key="variety.perSubList[%{#index.index}].fkRoleId" list="personRoleList" listKey="id" listValue="name" theme="simple" /></td>
			<td><s:textfield key="variety.perSubList[%{#index.index}].personName" theme="simple"/></td>
		</tr>
	</s:iterator>
	
	<tr>
		
		<td><input type="button" id="addPerson" class="ui-button ui-state-default ui-corner-all" value="添加相关人物"></input></td>
	</tr>
	-->
</s:form>
        <p>&nbsp;</p>
		<div class="buttom">
			<a href="#" class="button" id="varietysubmit">
				<s:if test="task == 'Create'">
					<s:text name="button.create"/>
				</s:if>
				<s:if test="task == 'Edit'">
					<s:text name="button.edit" />
				</s:if>
			</a>
			<a href="<s:url action="Variety_list"><s:param name="pageNumber" value="1" /></s:url>" class="button">
				<s:text name="button.cancel" />
			</a>
		</div>
      </div>
    </div>
    
  </div>
  <div id="footer"></div>
</div>

<div id="dialog" title="Add Related Person">
	<input type="text" name="searchWord" id="perSearchWord"/> <input type="button" value="search" onclick="searchPersons()" />
	<s:select id="personRoleId" list="personRoleList" listKey="id" listValue="name" theme="simple"></s:select>
	<div id="perResult"> </div>
	
</div>

 <div id="dialogNames" title="Search the Name">
	<input type="text" name="searchWord" id="diaSearchWord"/> <input type="button" value="search" onclick="searchNames()" />
	<div id="diaResult"> </div>
 </div>
 
<div id="imagedialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	图片路径： <input type="text"  id="picPath" name="imageUrl"/>
	<s:file name="upload" label="上传图片"/>	
	</s:form>
 </div>
</body>
</html>