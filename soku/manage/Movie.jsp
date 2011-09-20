<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<s:if test="task=='Create'">
	<title><s:text name="movie.title.create" /></title>
</s:if>
<s:if test="task=='Edit'">
	<title><s:text name="movie.title.edit" /></title>
</s:if>

<link type="text/css"
	href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<s:url value="/soku/manage/css/ui.datepicker.css"/>"
	rel="stylesheet" type="text/css" />

<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
<script type="text/javascript"
	src="<s:url value="/soku/manage/js/ui.datepicker.js"/>"></script>



<script type="text/javascript">
		$(function() {
			$("#Movie_save_movie_releaseTime").datepicker({dateFormat: 'yy-mm-dd'});
			$('#Movie_save_cates').hide();

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
						$("#Movie_save_movie_fkNamesId").val(obj[i].value);
						$("#Movie_save_movie_names").val($("#names" + obj[i].value).val());
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


		

		$('#moviesubmit').click(function() {
			/**var moviename = $("#Movie_save_movie_name"),	
			movieurl = $("#Movie_save_movie_url"),		
			moviesort = $("#Movie_save_movie_sort"),
			allRequiredFields = $([]).add(moviename).add(movieurl).add(moviesort);
			
			var bValid = true;
			allRequiredFields.removeClass('ui-state-error');

			bValid = bValid && checkLength(moviename, '栏目名称', 1, 200);

			bValid = bValid && checkLength(movieurl, '栏目链接地址', 1, 1000);
			bValid = bValid && checkRegexp(movieurl,/^http\:\/\/(\S)+?$/i,"栏目链接地址格式不正确");
			
			bValid = bValid && checkLength(moviesort, '排序', 1, 300);
			bValid = bValid && checkRegexp(moviesort,/^[0-9]+$/i,"排序为数字");

			
			if(!bValid)
				return false;
			else
				$('#Movie_save').submit();
			return false;**/

			bValid = checkSearchKeys();
			if(bValid == "false") {
				$("#Movie_save_movie_searchkeys").focus();
				return false;
			} else {
				$('#Movie_save').submit();
			}
		});

		$('#uploadfirstlogo').click(function() {
				$('#imagetype').val("firstlogo");
				$('#imagedialog').dialog('open');

			});
		$('#uploadposterimg').click(function() {
			$('#imagetype').val("posterimg");
			$('#imagedialog').dialog('open');
			});

		$('#playMovie').click(function() {
				var url = $('#Movie_save_movie_viewUrl').val();
				if(url.trim() == "") {
					alert("播放链接为空");
					return;
				}
				window.open(url);
			});
	

	});

		function addRow(personRoleId, personRoleName, personName) {
			var rowNumber = $("#Movie_save_movie_perSubList_size").val();
			if(rowNumber == "") {
				rowNumber = 0;
			}

			var hiddenHtml = '<input type="hidden" id="Movie_save_movie_perSubList_' + rowNumber + '__fkRoleId" value="' + personRoleId+ '" name="movie.perSubList[' + rowNumber + '].fkRoleId">'
			var tdhtml = '<input type="text" id="Movie_save_movie_perSubList_' + rowNumber + '__personName" value="' + personName + '" name="movie.perSubList[' + rowNumber + '].personName">';
			$("table.form tr:last").after("<tr>" + hiddenHtml + "<td>" + personRoleName + "</td><td>" + tdhtml + "</td></tr>");

			rowNumber++;
			$("#Movie_save_movie_perSubList_size").val(rowNumber);
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

		function chooseOne(cb) {
			var obj = document.getElementsByName("cboxperson");
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
	        

	        if($('#imagetype').val() == "firstlogo") {
	        	$('#Movie_save_movie_firstLogo').val(o.filePath);
	        	$('#firstLogo').attr('style', '');	
	       	 	$('#firstLogo').attr('src', o.filePath);
	       	 	
	        } else {
	        	$('#Movie_save_movie_haibao').val(o.filePath);
	        	$('#posterImg').attr('style', '');	
	       	 	$('#posterImg').attr('src', o.filePath);
	       		
	        }
	       // $('#Video_save_video_picturePath').attr("disabled","disabled");
		     
	        	         
		} 

		function getVideoDetail() {
			
			var url = $("#Movie_save_movie_viewUrl").val();
			$.ajax({
				url: '<s:url action="Movie_searchMovieDetail" />',
				type: 'POST',
				data: 'url=' + url,
				success: function(data) {
					var o = eval('(' + data + ')');	
					if(o.firstLogo.indexOf("0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0") < 0) {
						$("#Movie_save_movie_firstLogo").val(o.firstLogo);
						$("#firstLogo").attr('src', o.firstLogo);
					}					
					$("#Movie_save_movie_seconds").val(getSeconds(o.seconds));
				}
			});
		}

		function getSeconds(v) {
			if(1 == 1) {
				var h = v / 60;
				var s = v % 60;
				s = Math.floor(s);
				if(s < 10) {
					s = '0' + s;
				}
				return Math.floor(h) + ':' + s ;
			}
		}


		function checkSearchKeys() {
			
			var searchkeys = $("#Movie_save_movie_searchkeys").val();
			var id = $("#Movie_save_movieId").val();
			var status;
			$.ajax({
				url: '<s:url action="Movie_checkSearchKeys" />',
				type: 'POST',
				data: 'searchKeys=' + searchkeys + '&movieId=' + id,
				success: function(data) {
					var o = eval('(' + data + ')');
					if(o.status == "false") {
						$("#Movie_save_movie_searchkeys").css("background", "#ff0000");
						$("#searchKeyError").show();
						$('#searchkeystatus').val(false);
					} else {
						$("#Movie_save_movie_searchkeys").css("background", "");
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
<h1><s:text name="soku.movie.title" /></h1>

</div>
<br />


<div class="table"><s:actionerror cssClass="actionerror" />





	<s:form action="Movie_save" validate="false" cssClass="form"
	theme="simple">

			<p id="validateTips"></p>
			<s:token /> 
			<s:hidden name="task" /> 
			<s:hidden name="movieId" /> <s:hidden name="namesId" /> <s:hidden
				name="pageNumber" /> <s:hidden name="searchWord" /> <s:hidden
				name="siteFilter" /> <s:hidden name="statusFilter" /> <s:hidden
				name="movie.fkNamesId" /> <s:hidden name="categoryFilter" /> <input
				type="hidden" id="imagetype" /> <br />
		<s:if test="movieId == -1">
				<input type="button" id="editNames"
					class="ui-button ui-state-default ui-corner-all" value="选择系列名" />
			</s:if> <s:if test="movieId != -1">
				<input type="button" id="editNames"
					class="ui-button ui-state-default ui-corner-all" value="更改系列名" />
			</s:if><br />
	电影名称： <s:if test="movieId != -1">
				<s:textfield key="movie.names" cssClass="text" readonly="true" />
			</s:if> <s:else>
				<s:textfield key="movie.names" cssClass="text" />
			</s:else> 别名：<s:textfield key="movie.namesAlias" cssClass="text"></s:textfield>
		<br />
	版本名&nbsp;&nbsp;&nbsp; ：<s:textfield key="movie.name"
				cssClass="text" /><br />
          搜索命中词： <s:textfield key="movie.searchkeys" cssClass="text"
				onblur="checkSearchKeys();" /> <span id="searchKeyError"
				style="display: none; color: #ff0000">搜索词重复</span> <input
				type="hidden" id="searchkeystatus" /><br />
          上映时间：<s:textfield key="movie.releaseTime" cssClass="text" />
		<br />
	播放地址：<s:textfield key="movie.viewUrl" cssClass="text"
				onblur="getVideoDetail();" /> <input type="button" id="playMovie"
				class="ui-button ui-state-default ui-corner-all" value="播放" />
	<br />
	排序： <s:textfield key="movie.orderId" cssClass="text" /> 时长：
			<s:textfield key="movie.seconds" cssClass="text" /><br />
	站点： <s:select key="movie.sourceSite" list="sitesMap"
				listKey="key" listValue="value"></s:select> 高清： <s:select
				key="movie.hd" list="#{1:'是', 0:'否'}" listKey="key"
				listValue="value"></s:select><br />
            分类： <s:checkboxlist list="movieCateList"
				key="movie.cateList"></s:checkboxlist><br />
	视频截图：<s:textfield key="movie.firstLogo" cssClass="text" /> <img
				src="<s:property value="movie.firstLogo" />" id="firstLogo"
				<s:if test="movie.firstLogo == null">style="visibility: hidden" </s:if> />
			<input type="button" id="uploadfirstlogo"
				class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>
		<br />
	封面图片：<s:textfield key="movie.haibao" cssClass="text" /> <img
				src="<s:property value="movie.haibao" />" id="posterImg"
				<s:if test="movie.haibao == null">style="visibility: hidden"</s:if> />

			<input type="button" id="uploadposterimg"
				class="ui-button ui-state-default ui-corner-all" value="更改图片"></input>

		<br />
	地区 ： <s:select key="movie.area" list="areaList"
				cssClass="text"></s:select> 语言： <s:select key="movie.language"
				list="languageList" cssClass="text"></s:select>
	<br />			
	优酷是否有版权：<s:select key="movie.haveRight"
				list="#{1:'是', 0:'否'}" listKey="key" listValue="value"></s:select>
			是否屏蔽：<s:select key="movie.blocked" list="#{1:'是', 0:'否'}"
				listKey="key" listValue="value"></s:select>
				
	<br />			
	导演： <s:textfield key="movie.directorsName" cssClass="text"
				size="60" />
	<br />			
	演员： <s:textfield key="movie.performersName" cssClass="text"
				size="60" />
	<br />
	编剧： <s:textfield key="movie.scenaristName" cssClass="text"
				size="60" />
	<br />			
	制片人：<s:textfield key="movie.producersName" cssClass="text"
				size="60" />
				
	<br />			
	简介&nbsp;&nbsp;&nbsp;： <s:textarea key="movie.brief"
				cssClass="textarea" cols="60" rows="10" />
				
				
	<br />





		<s:hidden name="movie.perSubList.size" cssClass="text" />

		<!--  
    <s:textfield key="movie.detailUrl" cssClass="text"/>
	<s:iterator value="movie.perSubList" status="index">
		<tr>
			<s:hidden name="movie.perSubList[%{#index.index}].id"></s:hidden>
			<td>
				<s:select key="movie.perSubList[%{#index.index}].fkRoleId" list="personRoleList" listKey="id" listValue="name" theme="simple" /></td>
			<td><s:textfield key="movie.perSubList[%{#index.index}].personName" theme="simple"/></td>
		</tr>
	</s:iterator>
	<tr>		
		<td><input type="button" id="addPerson" class="ui-button ui-state-default ui-corner-all" value="添加人物"></input></td>
	</tr>
	-->

</s:form>


<p>&nbsp;</p>
<div class="buttom"><a href="#" class="button" id="moviesubmit">
<s:if test="task == 'Create'">
	<s:text name="button.create" />
</s:if> <s:if test="task == 'Edit'">
	<s:text name="button.edit" />
</s:if> </a> <a
	href="<s:url action="Movie_list"><s:param name="pageNumber" value="1" /></s:url>"
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