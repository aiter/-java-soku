<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="animeEpisode.title.list" /></title>

	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>
	
	<script type="text/javascript">
		$(function() {
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

			$("#siteSelect").change(function() {
				$("#AnimeEpisode_list").submit();
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
				alert('<s:url action="AnimeEpisode_save" />');
			});

		 
		});

		function saveEpisode(orderId) {
			var url = $("#url" + orderId).val();
			
			var animeSiteVersionId= '<s:property value="animeSiteVersionId"/>';
			var sourceSiteId = '<s:property value="sourceSiteId"/>';
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="AnimeEpisode_save" />',
				type: 'POST',
				data: 'animeSiteVersionId=' + animeSiteVersionId + '&sourceSiteId=' + sourceSiteId + '&url=' + url + '&id=' + id + '&orderId=' + orderId,
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

		function addToBlacklist(orderId) {
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="AnimeEpisode_addToBlacklist" />',
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
				url: '<s:url action="AnimeEpisode_changeLockStatus" />',
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

		function searchEpisode(orderId) {
			var animeSiteVersionId= '<s:property value="animeSiteVersionId"/>';
			var sourceSiteId = '<s:property value="sourceSiteId"/>';
			var id = $("#hiddenId" + orderId).val();
			$.ajax({
				url: '<s:url action="AnimeEpisode_searchAnimeAjax" />',
				type: 'POST',
				data: 'animeSiteVersionId=' + animeSiteVersionId + '&sourceSiteId=' + sourceSiteId + '&orderId=' + orderId,
				success: function(data) {
					if(data == '[]') {
						if(!confirm("没有完全匹配的结果，是否手动查找"))
						{
							return;
						}
						var keyword = $("#AnimeEpisode_searchAnime_animeNames").val();
						keyword = keyword.substring(0, keyword.indexOf('--'));
						window.open("http://10.102.23.41/v?keyword=" + keyword);
					}
					var jsonArr = eval('(' + data + ')');	
					for(var idx in jsonArr) {
						if($("#url" + jsonArr[idx].order_id).val() != jsonArr[idx].url)
						{
							if($("#url" + jsonArr[idx].order_id).val() == "") {
								$("#url" + jsonArr[idx].order_id).val(jsonArr[idx].url);
							}
							else if(confirm("搜索到新剧集，是否替换"))
							{
								$("#url" + jsonArr[idx].order_id).val(jsonArr[idx].url);
							}
						}
					}
				    
				}
			});
		}

		function saveAllEpisode() {
			var totalCount = '<s:property value="animeEpisodeList.size"/>';
			for(i = 1; i <= totalCount; i++) {
				saveEpisode(i);
			}
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
      
        <h1><s:text name="soku.animeEpisode.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
     
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="AnimeEpisode_searchAnime" validate="false" cssClass="form" theme="simple">
        <table class="listing" cellpadding="0" cellspacing="0">

				<tr>
					<th align="center" colspan="6"><s:property value="animeNames" /><s:hidden name="animeNames" /></th>
				</tr>

				<tr>
					<td colspan="6">	
							<s:hidden name="animeSiteVersionId"></s:hidden>		
							<s:hidden name="sourceSiteId"></s:hidden>					
            				<input type="button" id="changeEpisodeCollected" class="ui-button ui-state-default ui-corner-all" value="更改集数"></input>
            				<input type="button" id="changeSite" class="ui-button ui-state-default ui-corner-all" value="更改站点"></input>
            				<input type="submit" id="searcAllhbtn" class="ui-button ui-state-default ui-corner-all" value="搜索全部剧集" onclick='searchEpisode(-1);' />
            				
					</td>
				</tr>
				<s:iterator value="animeEpisodeList" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="left" width="15%">第<s:property value="orderId"/>集</td>
						<td align="left" width="10%"><input type="button" id="viewbtn<s:property value="orderId"/>" value="查看" onclick='viewEpisode(<s:property value="orderId"/>)' /></td>
						<td align="left" width="40%"><input id="url<s:property value="orderId"/>" value="<s:property value="url"/>" class="text" size="60%" 
														<s:if test="locked == 1">readonly="readonly"</s:if>
														/></td>
						<td align="left" width="5%"><input type="button" id="searchbtn<s:property value="orderId"/>" value="搜索" onclick='searchEpisode(<s:property value="orderId"/>)' /></td>
						<td align="left" width="15%"><input id="savebtn<s:property value="orderId"/>" type="button" onclick='saveEpisode(<s:property value="orderId"/>);' value="保存"></input>
										 <input id="hiddenId<s:property value="orderId"/>" type="hidden" value="<s:property value="id"/>"/>
										 <input id="lock<s:property value="orderId"/>" type="button" onclick='changeLockStatus(<s:property value="orderId"/> );' 
										 				<s:if test="locked == 1">value="解锁"</s:if>
										 				<s:if test="locked == 0">style="display:none" value="解锁"</s:if>
										 				/>
										 <p id="savesuccess<s:property value="orderId"/>" style="display:none">保存成功!</p>
										 <p id="savefail<s:property value="orderId"/>" style="display:none">保存失败!</p> 
						</td>
						<td align="left" width="5%"><input type="button" id="blacklist<s:property value="orderId"/>" onclick='addToBlacklist(<s:property value="orderId"/> );' value="黑名单"></input></td>
					</tr>
				</s:iterator>
			<tr>
				<td align="left" width="5%" colspan="6">
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

	<s:form action="AnimeEpisode_updateEpisodeCollected" validate="false" cssClass="form" enctype="multipart/form-data">
		<input name="episodeCollected" type="text" />
		<s:hidden name="animeSiteVersionId"></s:hidden>		
		<input type="submit" value="提交"/>
	</s:form>
 </div>
 
<div id="dialogSite" title="选择站点">

	<s:form action="AnimeEpisode_list" validate="false" cssClass="form" enctype="multipart/form-data">
		<s:select id="siteSelect" name="siteFilter" list="sitesFilterMap" listKey="key" listValue="value"></s:select>
		<s:hidden name="animeSiteVersionId"></s:hidden>		
		
	</s:form>
 </div>
</body>
</html>
