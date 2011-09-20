<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<s:if test="task=='Create'">
	<title><s:text name="animeVersion.title.create" /></title>
</s:if>
<s:if test="task=='Edit'">
	<title><s:text name="animeVersion.title.edit" /></title>
</s:if>
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
<link type="text/css"
	href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>


<script type="text/javascript">
		$(function() {

			$('#uploadfirstlogo').click(function() {
				$('#imagetype').val("firstlogo");
				$('#imagedialog').dialog('open');

			});
			$('#uploadposterimg').click(function() {
				$('#imagetype').val("posterimg");
				$('#imagedialog').dialog('open');
			});
			

			$("#imagedialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				buttons: {
					'取消' : function() {
							$(this).dialog('close');
						},	
				
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
						}
			
			},
			close : function() {
				//allFields.val('').removeClass('ui-state-error');
			}
		});
			


			$("#dialog").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
					'取消' : function() {
							$(this).dialog('close');
						},
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
						var personName = $("#personName").val();
						addRow(personRoleId, personRoleName, personName);
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
				'取消' : function() {
					$(this).dialog('close');
				},				
				'确定': function() {
				var obj = document.getElementsByName("cbox");
				for( i= 0; i < obj.length; i++) {
					if(obj[i].checked) {
						$("#AnimeVersion_save_animeVersion_fkNamesId").val(obj[i].value);
						$("#AnimeVersion_save_animeVersion_names").val($("#names" + obj[i].value).val());
					}
				}
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

			$("#editAnimeEpisode").click(function() {
				var siteVersion = '<s:property value="defaultSiteVersionId" />';
				var listEpisodeUrl = '<s:url action="AnimeEpisode_list"><s:param name="animeSiteVersionId" value="defaultSiteVersionId" /></s:url>';
				var addSiteVersion = '<s:url action="AnimeSiteVersion_list"><s:param name="animeVersionId" value="animeVersionId" /><s:param name="animeSiteVersionId" value="-1" /></s:url>';
				if(siteVersion != 0) {
					document.location = listEpisodeUrl;
				} else {
					alert('该剧集没有相关站点版本，请先添加！');
					document.location = addSiteVersion;
				}
				
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$("#animeSiteVersion").click(function() {
				var addSiteVersion = '<s:url action="AnimeSiteVersion_list"><s:param name="animeVersionId" value="animeVersionId" /><s:param name="animeSiteVersionId" value="-1" /></s:url>';
				document.location = addSiteVersion;
				
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


		

		$('#animeVersionsubmit').click(function() {
			/**var animeVersionname = $("#AnimeVersion_save_animeVersion_name"),	
			animeVersionurl = $("#AnimeVersion_save_animeVersion_url"),		
			animeVersionsort = $("#AnimeVersion_save_animeVersion_sort"),
			allRequiredFields = $([]).add(animeVersionname).add(animeVersionurl).add(animeVersionsort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(animeVersionname, '栏目名称', 1, 200);

			bValid = bValid && checkLength(animeVersionurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(animeVersionurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(animeVersionsort, '排序', 1, 300);
			bValid = bValid && checkRegexp(animeVersionsort,/^[0-9]+$/i,"排序为数字");

			
			if(!bValid)
				return false;
			else
				$('#AnimeVersion_save').submit();
			return false;**/
			bValid = checkSearchKeys();
			if(bValid == "false") {
				$("#AnimeVersion_save_animeVersion_searchkeys").focus();
				return false;
			} else {
				$('#AnimeVersion_save').submit();
			}
		});

	

	});

		function addRow(personRoleId, personRoleName, personName) {
			var rowNumber = $("#AnimeVersion_save_animeVersion_perSubList_size").val();
			if(rowNumber == "") {
				rowNumber = 0;
			}

			var hiddenHtml = '<input type="hidden" id="AnimeVersion_save_animeVersion_perSubList_' + rowNumber + '__fkRoleId" value="' + personRoleId+ '" name="animeVersion.perSubList[' + rowNumber + '].fkRoleId">'
			var tdhtml = '<input type="text" id="AnimeVersion_save_animeVersion_perSubList_' + rowNumber + '__personName" value="' + personName + '" name="animeVersion.perSubList[' + rowNumber + '].personName">';
			$("table.form tr:last").after("<tr>" + hiddenHtml + "<td>" + personRoleName + "</td><td>" + tdhtml + "</td></tr>");

			rowNumber++;
			$("#AnimeVersion_save_animeVersion_perSubList_size").val(rowNumber);
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
						resultStr += jsonArr[idx].name + ' (' + jsonArr[idx].category + ')</td></tr>';;
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
	        

		 	// $('#Video_save_video_picturePath').attr("disabled","disabled");
		     if($('#imagetype').val() == "firstlogo") {
	        	$('#AnimeVersion_save_animeVersion_firstLogo').val(o.filePath);
	        	$('#firstLogo').attr('style', '');	
	       	 	$('#firstLogo').attr('src', o.filePath);
	       	 	
	        } else {
	        	$('#AnimeVersion_save_animeVersion_haibao').val(o.filePath);
	        	$('#posterImg').attr('style', '');	
	       	 	$('#posterImg').attr('src', o.filePath);
	       		
	        }       
		} 

		function checkSearchKeys() {
			
			var searchkeys = $("#AnimeVersion_save_animeVersion_searchkeys").val();
			var id = $("#AnimeVersion_save_animeVersionId").val();
			var status;
			$.ajax({
				url: '<s:url action="AnimeVersion_checkSearchKeys" />',
				type: 'POST',
				data: 'searchKeys=' + searchkeys + '&animeVersionId=' + id,
				success: function(data) {
					var o = eval('(' + data + ')');
					if(o.status == "false") {
						$("#AnimeVersion_save_animeVersion_searchkeys").css("background", "#ff0000");
						$("#searchKeyError").show();
						$('#searchkeystatus').val(false);
					} else {
						$("#AnimeVersion_save_animeVersion_searchkeys").css("background", "");
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
<div id="header"><a href="#" class="logo"><img
	src="img/logo.gif" width="101" height="29" alt="" /></a></div>
<div id="middle"><s:include value="module/leftnav.jsp"></s:include>
<div id="center-column">
<div class="top-bar">
<h1><s:text name="soku.animeVersion.title" /></h1>

</div>
<br />


<div class="table"><s:actionerror cssClass="actionerror" />
<p id="validateTips"></p>
<s:form action="AnimeVersion_save" validate="false" cssClass="form"
	theme="simple">

	<s:token />
	<s:hidden name="animeVersion.fkNamesId" />
	<s:hidden name="task" />
	<s:hidden name="animeVersionId" />
	<s:hidden name="namesId" />
	<s:hidden name="pageNumber" />
	<s:hidden name="searchWord" />
	<s:hidden name="categoryFilter" />
	<input type="hidden" id="imagetype" />
	<s:if test="animeVersionId == -1">
		<input type="button" id="editNames"
			class="ui-button ui-state-default ui-corner-all" value="选择系列名" />
	</s:if>
	<s:if test="animeVersionId != -1">
		<input type="button" id="editNames"
			class="ui-button ui-state-default ui-corner-all" value="更改系列名" />
	</s:if>
	<br />
	<label>动漫名称：</label>
	<s:if test="animeVersionId != -1">
		<s:textfield key="animeVersion.names" cssClass="text" readonly="true" />
	</s:if>
	<s:else>
		<s:textfield key="animeVersion.names" cssClass="text" />
	</s:else>


	<label>别名：</label>
	<s:textfield key="animeVersion.namesAlias" cssClass="text" />
	<br />

	<label>版本名：</label>
	<s:textfield key="animeVersion.name" cssClass="text" />
	<br />
	<label>搜索词：</label>
	<s:textfield key="animeVersion.searchkeys" cssClass="text" onblur="checkSearchKeys();"/>
	<span id="searchKeyError" style="display:none; color: #ff0000">搜索词重复</span>
	<input type="hidden" id="searchkeystatus" />
	<br />
	<label>上映时间：</label>
	<s:textfield key="animeVersion.releaseTime" cssClass="text" />
	<label>是否上映完成：</label>
	<s:select key="animeVersion.released" list="#{1:'是', 0:'否'}"
		listKey="key" listValue="value"></s:select>
		
	排序： <s:textfield key="animeVersion.orderId" cssClass="text"/>
	<br />
	<label>总集数：</label>
	<s:textfield key="animeVersion.episodeTotal" cssClass="text" />
	<input type="button" id="editAnimeEpisode"
		class="ui-button ui-state-default ui-corner-all" value="剧集搜索" />
	<label>默认展示站点： <s:select key="animeVersion.defaultDisplaySite" list="sitesMap" listKey="key" listValue="value" /></label>
	<br />
	<label>分类：</label>
	<s:checkboxlist list="animeCateList" key="animeVersion.cateList"></s:checkboxlist>
	<br />
	<label>封面图片：</label>
	<s:textfield key="animeVersion.haibao" cssClass="text" />
	<img src="<s:property value="animeVersion.haibao" />" id="posterImg"
		<s:if test="animeVersion.haibao == null">style="visibility: hidden"</s:if> />
	<input type="button" id="uploadposterimg"
		class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>
	<br />
	
	<label>剧集截图：</label>
     		<s:textfield key="animeVersion.firstLogo" cssClass="text"/> 
     		<img src="<s:property value="animeVersion.firstLogo" />" id="firstLogo" <s:if test="animeVersion.firstLogo == null">style="visibility: hidden"</s:if> />
 			<input type="button" id="uploadfirstlogo" class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>
 		<br />
 			地区          ： <s:select key="animeVersion.area" list="areaList"
		cssClass="text"></s:select>
			语言： <s:select key="animeVersion.language" list="languageList"
		cssClass="text"></s:select>
	<br />
			优酷是否有版权：<s:select key="animeVersion.haveRight" list="#{1:'是', 0:'否'}"
		listKey="key" listValue="value"></s:select>
			
	<br />
			导演：　<s:textfield key="animeVersion.directorsName" cssClass="text"
		size="60" />
	<br />
	
			演员：　<s:textfield key="animeVersion.performersName" cssClass="text"
		size="60" />
	<br />
		
	 		编剧：　<s:textfield key="animeVersion.scenaristName" cssClass="text"
		size="60" />
	<br />
	 	
			制片人：<s:textfield key="animeVersion.producersName" cssClass="text"
		size="60" />

	<br />
			
			简介： <s:textarea key="animeVersion.brief" cssClass="textarea"
		cols="60" rows="10" />

</s:form>
<p>&nbsp;</p>
<div class="buttom"><a href="#" class="button"
	id="animeVersionsubmit"> <s:if test="task == 'Create'">
	<s:text name="button.create" />
</s:if> <s:if test="task == 'Edit'">
	<s:text name="button.edit" />
</s:if> </a> <a
	href="<s:url action="AnimeVersion_list"><s:param name="pageNumber" value="1" /></s:url>"
	class="button"> <s:text name="button.cancel" /> </a></div>
</div>
</div>

</div>
<div id="footer"></div>
</div>

<div id="dialog" title="Add Related Person"><input type="text"
	name="searchWord" id="perSearchWord" /> <input type="button"
	value="search" onclick="searchPersons()" /> <s:select
	id="personRoleId" list="personRoleList" listKey="id" listValue="name"
	theme="simple"></s:select>
<div id="perResult"></div>

</div>

<div id="dialogNames" title="Search the Name"><input type="text"
	name="searchWord" id="diaSearchWord" /> <input type="button"
	value="search" onclick="searchNames()" />
<div id="diaResult"></div>
</div>

<div id="imagedialog" title="Upload Picture"><s:form
	action="FileUpload" validate="false" cssClass="form"
	enctype="multipart/form-data">
	图片路径： <input type="text" id="picPath" name="imageUrl" />
	<s:file name="upload" label="上传图片" />
</s:form></div>
</body>
</html>