<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="teleplayEpisode.title.list" /></title>

	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
	<script type="text/javascript"><!--
		$(function() {

			$("#searchResult").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				width: 400,
				modal: true,
				
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});
			
			
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
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
				
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

			$("#dialogSite").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

			$("#dialogyxSite").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

			$('#changeEpisodeCollected').click(function() {
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

			$('#changeSite').click(function() {
				$('#dialogSite').dialog('open');
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});

			$("a[name='deleteLink']").click(function() {
				return confirm("确认删除？");
			});

			$("#thcheck").click(function() {
				if($(this).attr("checked")) {
					$("[name=batchdeleteids]").each(function(){
						$(this).attr("checked", true);
					});
				} else {
					$("[name=batchdeleteids]").each(function() {
						$(this).attr("checked", false);
					});
				}
			});

			$("#saveEpisode").click(function() {
				alert('<s:url action="TeleplayEpisode_save" />');
			});

		 
		});

		function saveEpisode(orderId) {
			var url = $("#url" + orderId).val();
			
			var teleplaySiteVersionId= '<s:property value="teleplaySiteVersionId"/>';
			var sourceSiteId = '<s:property value="sourceSiteId"/>';
			var id = $("#hiddenId" + orderId).val();
			var logoUrl = $("#logoUrl" + orderId).val();
			var seconds = $("#seconds" + orderId).val();
			var hd = $("#hd" + orderId).attr("checked") ? 1 : 0;
			$.ajax({
				url: '<s:url action="TeleplayEpisode_save" />',
				type: 'POST',
				data: 'teleplaySiteVersionId=' + teleplaySiteVersionId + '&sourceSiteId=' + sourceSiteId + '&url=' + url + '&id=' + id + '&orderId=' + orderId + '&logoUrl=' + logoUrl + '&seconds=' + seconds + '&hd=' + hd,
				success: function(data) {
					var o = eval('(' + data + ')');					
					$("#hiddenId" + orderId).val(o.newId)
					if(o.status == 'success') {
						$("#lock" + orderId).attr("style", "");
						$("#savesuccess"+ orderId).attr("style", "color:green");
						$("#url" + orderId).attr("readonly", "readonly");
					} else {
						$("#savefail" + orderId).attr("style", "color:red");
					}
				    
				}
			});
		}


		function getVideoDetail(orderId) {
			var url = $("#url" + orderId).val();
			
			var teleplaySiteVersionId= '<s:property value="teleplaySiteVersionId"/>';
			var sourceSiteId = '<s:property value="sourceSiteId"/>';
			var id = $("#hiddenId" + orderId).val();
			var logoUrl = $("#logoUrl" + orderId).val();
			var seconds = $("#seconds" + orderId).val();
			var hd = $("#hd" + orderId).attr("checked") ? 1 : 0;
			$.ajax({
				url: '<s:url action="TeleplayEpisode_searchTeleplayDetail" />',
				type: 'POST',
				data: 'teleplaySiteVersionId=' + teleplaySiteVersionId + '&sourceSiteId=' + sourceSiteId + '&url=' + url,
				success: function(data) {
					var o = eval('(' + data + ')');	
					if(o.logo.indexOf("0900641F464A6318D600000000000000000000-0000-0000-0000-000042145BB0") < 0) {
						$("#logoUrl" + orderId).val(o.logo);
						$("#logoImg" + orderId).attr('src', o.logo);
					}
					$("#seconds" + orderId).val(o.seconds);
					$("#secondsH" + orderId).val(o.seconds);
					changeEpisodeSecond();
				}
			});
		}

		function addToBlacklist(orderId) {
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="TeleplayEpisode_addToBlacklist" />',
				type: 'POST',
				data: 'id=' + id,
				success: function(data) {
					var o = eval('(' + data + ')');
					if(o.status == 'success') {
						$("#blacklist"+ orderId).attr("readonly", "readonly");
					} else {
						alert('error');
					}
				}
			});
		}

		function changeLockStatus(orderId) {
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="TeleplayEpisode_changeLockStatus" />',
				type: 'POST',
				data: 'id=' + id,
				success: function(data) {
					var o = eval('(' + data + ')');
					if(o.status == 'success') {
						if(o.operation == 'unlock') {
							$("#url" + orderId).attr("readonly", "");
							$("#lock" + orderId).attr("value", "锁定");
						} else {
							$("#url" + orderId).attr("readonly", "readonly");
							$("#lock" + orderId).attr("value", "解锁");
						}
					} else {
						alert('error');
					}
				}
			});
		}

		function viewEpisode(orderId) {
			var url = $("#url" + orderId).val();
			window.open(url);
		}

		function viewEpisodeUrl(orderId) {
			var url = $("#urlResults" + orderId).val();
			window.open(url);
		}

		function selectEpisodeUrl(resflag, orderId) {
			var url = $("#urlResults" + resflag).val();
			var logo = $("#logoResults" + resflag).val();
			var logoDisplay = logo;
			if(logo.indexOf("ykimg.com") < 0) {
				logoDisplay = "http://g1.ykimg.com/" + logo;
			}
			
			var seconds = $("#secondsResults" + resflag).val();
			var hd = $("#hdResults" + resflag).val();
			$("#url" + orderId).val(url);
			$("#url" + orderId).attr("readonly", "");
			$('#logoUrl' + orderId).val(logo);	
	        $('#logoImg' + orderId).attr('src', logoDisplay);
	        $('#seconds' + orderId).val(formateSeconds(seconds));
	        $('#secondsH' + orderId).val(seconds);
	        if(hd == "true") {
	        	$("#hd" + orderId).attr("checked", "checked")
	        	$("#hdH" + orderId).val(1);
	        } else {
	        	$("#hd" + orderId).attr("checked", "")
	        	$("#hdH" + orderId).val(0);
	        }
			$('#searchResult').dialog('close');
		}

		function searchEpisode(orderId) {
			var teleplaySiteVersionId= '<s:property value="teleplaySiteVersionId"/>';
			var sourceSiteId = '<s:property value="sourceSiteId"/>';
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="TeleplayEpisode_searchTeleplayAjax" />',
				type: 'POST',
				data: 'teleplaySiteVersionId=' + teleplaySiteVersionId + '&sourceSiteId=' + sourceSiteId + '&orderId=' + orderId,
				success: function(data) {
					if(data == '[]') {
						if(!confirm("没有完全匹配的结果，是否手动查找"))
						{
							return;
						}
						var keyword = $("#TeleplayEpisode_searchTeleplay_teleplayNames").val();
						keyword = keyword.substring(0, keyword.indexOf('--'));
						window.open("http://10.102.23.41/v?keyword=" + keyword);
					}
					var jsonArr = eval('(' + data + ')');	
					var resStr = '';
					
					for(var idx in jsonArr) {
						/**
						if($("#url" + jsonArr[idx].order_id).val() != jsonArr[idx].url)
						{
							if($("#url" + jsonArr[idx].order_id).val() == "") {
								$("#url" + jsonArr[idx].order_id).val(jsonArr[idx].url);
							}
							else if(confirm("搜索到新剧集，是否替换"))
							{
								$("#url" + jsonArr[idx].order_id).val(jsonArr[idx].url);
							}
						}*/
						var hd = 0;
						if(jsonArr[idx].hd) {
							hd = 1;
						} else {
							hd = 0
						}

						resStr += getsitename(jsonArr[idx].site) + ': <input type="text" id="urlResults' + jsonArr[idx].order_id + '_' + jsonArr[idx].site +'" value="' + jsonArr[idx].url +'" />' 
									+ '<input type="hidden" id="logoResults' + jsonArr[idx].order_id + '_' + jsonArr[idx].site +'" value="' + jsonArr[idx].logo +'" />'
									+ '<input type="hidden" id="secondsResults' + jsonArr[idx].order_id + '_' + jsonArr[idx].site +'" value="' + jsonArr[idx].seconds +'" />'
									+ '<input type="hidden" id="hdResults' + jsonArr[idx].order_id + '_' + jsonArr[idx].site +'" value="' + jsonArr[idx].hd +'" />'
									+ '<input type="button" value="查看" onclick="viewEpisodeUrl(\''+ jsonArr[idx].order_id + '_' + jsonArr[idx].site +'\')"/>'
									+ '<input type="button" value="选择" onclick="selectEpisodeUrl(\''+ jsonArr[idx].order_id + '_' + jsonArr[idx].site +'\', ' + jsonArr[idx].order_id + ')"/><br />';
									
						
					}

					if(resStr != '') {
						$('#searchResult').dialog('open');
						$('#resultContent').html(resStr);
					}
				    
				}
			});
		}

		function saveAllEpisode() {
			$("#onlySave").val("onlySave");
			$("#TeleplayEpisode_searchTeleplay").submit();
		}

		function searchAllEpisode() {
			$('#dialogyxSite').dialog('open');
		}

		function startSearch() {
			$("#onlySave").val("");
			$("#yxSite").val($("#yxsiteSelect").attr("value"));
			$("#TeleplayEpisode_searchTeleplay").submit();
		}

		function changeLogo(orderId) {
			$("#currentOrderId").val(orderId);
			var id = $("#hiddenId" + orderId).val();
			$('#imagedialog').dialog('open');
			$("#FileUpload_upload").val("");
			$("#picPath").val("");
		}

		$(document).ready(function() { 
		    var options = { 
		        //target:        '#output',   // target element(s) to be updated with server response 
		        beforeSubmit:  showRequest,  // pre-submit callback 
		        success:       showResponse  // post-submit callback 
		 
		      
		    }; 
		 
		    // bind form using 'ajaxForm' 
		    $('#FileUpload').ajaxForm(options); 
		    changeEpisodeSecond();
		}); 

		function changeEpisodeSecond() {
			var eps = document.getElementsByName("seconds");
			for(idx = 0; idx < eps.length; idx++) {
				var v = eps[idx].value;
				if(v.indexOf(':') < 0) {
					var h = v / 60;
					var s = v % 60;
					s = Math.floor(s);
					if(s < 10) {
						s = '0' + s;
					}
					eps[idx].value = Math.floor(h) + ':' + s ;
				}
			}
		}

		function formateSeconds(seconds) {
			if(seconds.indexOf(':') < 0) {
				var h = seconds / 60;
				var s = seconds % 60;
				s = Math.floor(s);
				if(s < 10) {
					s = '0' + s;
				}
				return Math.floor(h) + ':' + s ;
			}
		}
		 
		// pre-submit callback 
		function showRequest(formData, jqForm, options) { 
		    			 
		    return true; 
		} 
		 
		// post-submit callback 
		function showResponse(responseText, statusText)  { 
			var orderId = $("#currentOrderId").val();
		    var o = eval('(' + responseText + ')');  				
	        $('#logoUrl' + orderId).val(o.filePath);	
	        $('#logoImg' + orderId).attr('src', o.filePath);
	       // $('#Video_save_video_picturePath').attr("disabled","disabled");
	        	         
		} 

		function sethiddenhd(orderId) {
			var hd = $("#hd" + orderId).attr("checked") ? 1 : 0;
			$("#hdH" + orderId).val(hd);
		}

		function sethiddenseconds(orderId) {
			var seconds = $("#seconds" + orderId).val();
			ms = seconds.split(":");
			var mm = parseInt(ms[0]) * 60;
			var ss = parseInt(ms[1]);
			var time = mm + ss;
			$("#secondsH" + orderId).val(time);
		}

		function getsitename(siteId) {
			if(siteId == 14) {
				return "优酷";
			} else if(siteId == 6) {
				return "搜狐";
			} else if(siteId == 2) {
				return "56";
			} else if(siteId == 1) {
				return "土豆";
			} else {
				return siteId;
			}
		}
	--></script>
</head>
<body>
<div id="main">
  <div id="header"> <a href="#" class="logo"><img src="img/logo.gif" width="101" height="29" alt="" /></a>
   
  </div>
  <div id="middle">
    <s:include value="module/leftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      	
        <h1><a href="<s:url action="TeleplayVersion_input"><s:param name="teleplayVersionId" value="teleplayVersionId" /></s:url>">
        		<s:text name="soku.teleplayVersion.title" />
        	</a> <span class="chevron"> » </span> <s:text name="soku.teleplayEpisode.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
     
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="TeleplayEpisode_searchTeleplay" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" colspan="6"><s:property value="teleplayNames" /><s:hidden name="teleplayNames" /></th>
				</tr>

				<tr>
					
					<td colspan="6">	
							<s:hidden name="teleplaySiteVersionId"></s:hidden>		
							<s:hidden name="sourceSiteId"></s:hidden>
							<input type="hidden" id="yxSite" name="yxSite" value="" />	
							<input type="hidden" name="onlySave" id="onlySave"/>						
            				<input type="button" id="changeEpisodeCollected" class="ui-button ui-state-default ui-corner-all" value="更改集数"></input>
            				<input type="button" id="changeSite" class="ui-button ui-state-default ui-corner-all" value="更改站点"></input>
            				<input type="button" id="searcAllhbtn" class="ui-button ui-state-default ui-corner-all" value="搜索全部剧集" onclick="searchAllEpisode();" />
            				<input type="button" id="searcAllhbtn" class="ui-button ui-state-default ui-corner-all" value="保存全部剧集" onclick="saveAllEpisode();" />
					</td>
				</tr>
				<s:iterator value="teleplayEpisodeList" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left" width="15%">第<s:property value="orderId"/>集</td>
						<td align="left" width="10%"><input type="button" id="viewbtn<s:property value="orderId"/>" value="查看" onclick='viewEpisode(<s:property value="orderId"/>)' /></td>
						<td align="left" width="25%"><input id="url<s:property value="orderId"/>" name="teleplayEpisodeList[<s:property value="orderId"/>].url" value="<s:property value="url"/>" class="text" 
														<s:if test="locked == 1">readonly="readonly"</s:if>
														onblur="getVideoDetail(<s:property value="orderId"/> );"/></td>
						<td width="20%">
							<div>
								<img src='<s:property value="logo" />' id="logoImg<s:property value="orderId"/>" /> <br/>								
							 	<input type="hidden" id="logoUrl<s:property value="orderId"/>" value="<s:property value="logo" />" name="teleplayEpisodeList[<s:property value="orderId"/>].logo"/>
								<input id="changelogo<s:property value="orderId"/>" type="button" onclick='changeLogo(<s:property value="orderId"/> );' 
										 				value="更改图片" />	<br />
								时长：<input id="seconds<s:property value="orderId"/>" name="seconds" type="text" value="<s:property value="seconds"/>" class="editing"  onchange="sethiddenseconds(<s:property value="orderId"/>)"/> <br/>
									 <input id="secondsH<s:property value="orderId"/>" type="hidden" value="<s:property value="seconds"/>" class="editing"  name="teleplayEpisodeList[<s:property value="orderId"/>].seconds"/>
								<span class="HD" title="高清"></span>
								<input type="checkbox" id="hd<s:property value="orderId"/>" <s:if test="hd == 1">checked="checked"</s:if> onchange="sethiddenhd(<s:property value="orderId"/>)"/>
								<input type="hidden" id="hdH<s:property value="orderId"/>" value="<s:property value="hd"/>"  name="teleplayEpisodeList[<s:property value="orderId"/>].hd"/>
							</div>
						</td>
						<td align="left" width="5%"><input type="button" id="searchbtn<s:property value="orderId"/>" value="更换链接" onclick='searchEpisode(<s:property value="orderId"/>)' /></td>
						<td align="left" width="5%">
										  <input id="savebtn<s:property value="orderId"/>" type="button" onclick='saveEpisode(<s:property value="orderId"/>);' value="保存"></input>
										 <input id="hiddenId<s:property value="orderId"/>" name="teleplayEpisodeList[<s:property value="orderId"/>].id" type="hidden" value="<s:property value="id"/>"/>
										 <input name="teleplayEpisodeList[<s:property value="orderId"/>].orderId" type="hidden" value="<s:property value="orderId"/>"/>
										 <input id="lock<s:property value="orderId"/>" type="button" onclick='changeLockStatus(<s:property value="orderId"/> );' 
										 				<s:if test="locked == 1">value="解锁"</s:if>
										 				<s:if test="locked == 0">style="display:none" value="解锁"</s:if>
										 				/>
										 <p id="savesuccess<s:property value="orderId"/>" style="display:none">保存成功!</p>
										 <p id="savefail<s:property value="orderId"/>" style="display:none">保存失败!</p> 
						</td>
					</tr>
				</s:iterator>
			<tr>
				<td align="left" width="5%" colspan="7">
					<input id="currentOrderId" type="hidden" value="" />
				</td>
				
			</tr>
			</table>
			</s:form>
     
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>

  <div id="dialog" title="更改收录集数">

	<s:form action="TeleplayEpisode_updateEpisodeCollected" validate="false" cssClass="form" enctype="multipart/form-data">
		<input name="episodeCollected" type="text" value="<s:property value="episodeTotal"/>"/>
		<s:hidden name="teleplaySiteVersionId"></s:hidden>		
		<input type="submit" value="提交"/>
	</s:form>
 </div>
 
   <div id="dialogSite" title="选择站点">

	<s:form action="TeleplayEpisode_list" validate="false" cssClass="form" enctype="multipart/form-data" theme="simple">
		<s:select id="siteSelect" name="siteFilter" list="sitesFilterMap" listKey="key" listValue="value"></s:select>
		<s:hidden name="teleplaySiteVersionId"></s:hidden>		
		<input type="submit" value="确认" />
	</s:form>
 </div>
 
 <div id="dialogyxSite" title="选择优先站点">

	<s:select id="yxsiteSelect" name="siteFilter" list="sitesFilterMap" listKey="key" listValue="value"></s:select>
	<input type="button" id="startSearch" value="开始搜索" onclick="startSearch();"/> 
 </div>
 
 <div id="imagedialog" title="Upload Picture">

	<s:form action="FileUpload" validate="false" cssClass="form" enctype="multipart/form-data">
	图片路径： <input type="text"  id="picPath" name="imageUrl"/>
	<s:file name="upload" label="上传图片"/>	
	</s:form>
 </div>
 
 <div id="searchResult" title="请选择要替换的链接">
 	<div id="resultContent"></div> 	
 </div>
</body>
</html>
