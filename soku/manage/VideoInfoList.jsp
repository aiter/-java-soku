<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="videoInfo.title.list" /></title>

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
	var editing = false;
	var newhtmlhead;
	var newhtmltail;
	var editobj;
	var noVersion;	

	function updateContent(category, id, versionname, order, type) {
		var updateurl = "<s:url action="VideoInfo_update"></s:url>";
		
		$.ajax({
		    url: updateurl,
		    type: "POST",
		    data: ({"versionName" : versionname, "orderId": order, "updateType": type, "videoId" : id}),
		    success: function(msg) {	
		    	var o = eval('(' + msg + ')');
		    				
				if(type == "versionname") {
					editobj.html(newhtmlhead + versionname + newhtmltail);	
				} else if(type == "order"){
					editobj.html(neworder + newhtmltail);
				}				
	    	}
		});
	}
	
	function updatekeyword(id, category){	
	    editing = false;
	    newkeyword = $("#newkeyword").val();
	    updateContent(category, id, newkeyword, 0, "versionname");
	}

	function updateorder(id, category){
		editing = false;
	    neworder = $("#neworder").val();
	    updateContent(category, id, "", neworder, "order");
	}

	function cancelupdatekeyword(){		
	    
	    editing = false;
	    newkeyword = $("#newkeyword").val();
	    if(noVersion != undefined) {
		    newkeyword = noVersion;
		    noVersion = undefined;
	    }
	    editobj.html(newhtmlhead + newkeyword + newhtmltail);
	}

	function cancelupdateorder(){		    
		editing = false;
		neworder = $("#neworder").val();	    
	    editobj.html(neworder + newhtmltail);
	}
	
		$(function() {

			$('.editVersionName').dblclick(function(event) {			
				if(editing) return;
				editobj = $(this);
				html = $(this).html();
				html = html.toLowerCase();
				wordstartidx = html.indexOf('>');
				wordendidx = html.indexOf('</a>');
				word = html.substring(wordstartidx + 1, wordendidx);
				if(word.indexOf('<span class="highlight">无版本名</span>') >= 0) {
					word = '';
					noVersion = '<span class="highlight">无版本名</span>';
				}

				idstartidx = html.indexOf('value="');
				idendidx = html.indexOf('"', idstartidx + 7);
				wordid = html.substring(idstartidx + 7, idendidx);

				categorystartidx = html.indexOf('value="', idstartidx + 7);
				categoryendidx = html.indexOf('"', categorystartidx + 7);
				categoryid = html.substring(categorystartidx + 7, categoryendidx);

				newhtmlhead = html.substring(0, wordstartidx+1);
				newhtmltail = html.substring(wordendidx); 
		        $(this).html('<input id="newkeyword" type="text" value="' + word + '" /> <input type="button" id="ajaxupdate" value="更新" onclick="updatekeyword(' + wordid + ', ' + categoryid + ');"/> <input type="button" id="ajaxupdate" value="取消" onclick="cancelupdatekeyword();"/>');
		        editing = true;
		    });

			$('.editOrder').dblclick(function(event) {			
				if(editing) return;
				editobj = $(this);
				html = $(this).html();
				html = html.toLowerCase();
				orderendidx = html.indexOf('<input');
				order = html.substring(0, orderendidx);

				idstartidx = html.indexOf('value="');
				idendidx = html.indexOf('"', idstartidx + 7);
				wordid = html.substring(idstartidx + 7, idendidx);

				categorystartidx = html.indexOf('value="', idstartidx + 7);
				categoryendidx = html.indexOf('"', categorystartidx + 7);
				categoryid = html.substring(categorystartidx + 7, categoryendidx);

				newhtmltail = html.substring(orderendidx); 
		        $(this).html('<input id="neworder" type="text" value="' + $.trim(order) + '" /> <input type="button" id="ajaxupdate" value="更新" onclick="updateorder(' + wordid + ', ' + categoryid + ');"/> <input type="button" id="ajaxupdate" value="取消" onclick="cancelupdateorder();"/>');
		        editing = true;
		    });

			var blockSuccess= '<s:property value="blockSuccess"/>';

			

			if(blockSuccess == "blocksuccess") {
				alert("屏蔽成功");
			}
			if(blockSuccess == "unblocksuccess") {
				alert("解除屏蔽成功");
			}

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
						$("#VideoInfo_changeSerial_serialId").val(obj[i].value);
						$('#VideoInfo_changeSerial').submit();
					}
				}
						$(this).dialog('close');
						
				},
				'取消' : function() {
					$(this).dialog('close');
				}
				},
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

			$('#changeSerial').click(function() {
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

			$('#deleteSerial').click(function() {
				$('#VideoInfo_changeSerial').attr("action", "VideoInfo_deleteSerial.do");
				$('#VideoInfo_changeSerial').submit();
				$('#VideoInfo_changeSerial').attr("action", "VideoInfo_changeSerial.do");
			}).hover(function() {
				$(this).addClass("ui-state-hover");
			}, function() {
				$(this).removeClass("ui-state-hover");
			}).mousedown(function() {
				$(this).addClass("ui-state-active");
			}).mouseup(function() {
				$(this).removeClass("ui-state-active");
			});
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
			});

			$("#dialogVideoOption").dialog({
				bgiframe: true,
				autoOpen: false,
				height: 250,
				modal: true,
				buttons: {
					'确定': function() {
				var obj = document.getElementsByName("cboxvideo");
				for( i= 0; i < obj.length; i++) {
					if(obj[i].checked) {
						var options = obj[i].value;						
					}
				}
				var resultLocation = "";
				if(options == 1) {
					resultLocation = '<s:url action="TeleplayVersion_input"><s:param name="teleplayVersionId" value="-1" /></s:url>';
				} else if(options == 2) {
					resultLocation = '<s:url action="Movie_input"><s:param name="movieId" value="-1" /></s:url>';
				} else if(options == 3) {
					resultLocation = '<s:url action="Variety_input"><s:param name="varietyId" value="-1" /></s:url>';
				} else if(options == 4) {
					resultLocation = '<s:url action="AnimeVersion_input"><s:param name="videoId" value="-1" /></s:url>';
				}
				var fkNamesId = $("#VideoInfo_changeSerial_fkNamesId").val();
				document.location = resultLocation + '&fkNamesId=' + fkNamesId;
				$(this).dialog('close');
				
						
				},
				'取消' : function() {
					$(this).dialog('close');
				}
				},
				close : function() {
					//allFields.val('').removeClass('ui-state-error');
				}
			});

		
			$("#pageselect").change(function() {
				location.href = $("#pageurl").val() + $("#pageselect").val();
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

		 $("#batchdelete").click(function() {
			 
			 haschecked = false;
			 $("[name=batchdeleteids]").each(function(){
					if($(this).attr("checked"))
						haschecked = true;
				});
			 if(haschecked) {
				if(!confirm("确认删除？"))
					 return false;
				$("#VideoInfo_batchdelete").submit();
			 } else {
				 alert("没有数据要删除");
				 return false;
			 }
			 return false;
		 });
		});

		function searchNames() {
			var searchWord = $("#diaSearchWord").val();
			if(searchWord == "") {
				alert('搜索关键字不能为空');
			}
			$.ajax({
				url: '<s:url action="Series_listJson" />',
				type: 'POST',
				data: 'searchWord=' + searchWord,
				success: function(data) {
					if(data == "[]") {
						var url = '<s:url action="Series_input"><s:param name="seriesId" value="-1" /></s:url>';
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

		function chooseOne(cb) {
			var obj = document.getElementsByName("cbox");
			for( i= 0; i < obj.length; i++) {
				if(obj[i] != cb)
					obj[i].checked = false;
				else
					obj[i].checked = cb.checked;
			}
		}

		function chooseOneVideo(cb) {
			var obj = document.getElementsByName("cboxvideo");
			for( i= 0; i < obj.length; i++) {
				if(obj[i] != cb)
					obj[i].checked = false;
				else
					obj[i].checked = cb.checked;
			}
		}

		function showOption(namesId) {
			$("#VideoInfo_changeSerial_fkNamesId").val(namesId);
			$('#dialogVideoOption').dialog('open');
		}

		function confirmDelete() {
			if(!confirm("确认删除？"))
				 return false;
		}

		function confirmBlock() {
			return confirm("确认屏蔽?");
		}

		function confirmUnBlock() {
			return confirm("确认解除屏蔽");
		}

		function confirmConcern(flag) {
			if(flag == 1) 
				return confirm("确认取消关注");
			else
				return confirm("确认关注该剧");
		}

		function setConcernLevel(versionId, cateId) {
			$.ajax({
				url: '<s:url action="EpisodeAuditLog_concern" />',
				type: 'POST',
				data: 'versionId=' + versionId + "&cateId=" + cateId, 
				success: function(data) {
				    alert('关注成功');
				}
			});
		}
	
		function exportXls(){
		var num = document.getElementById("num").value;
		var cate = document.getElementById("VideoInfo_list_categoryFilter").value;
		var accuratelyMatched = document.getElementById("VideoInfo_list_accuratelyMatched").value;
		if(num.length==0){
			alert("请输入导出的条数！");
			document.getElementById("num").focus();
			return;
		}
		window.location = 'VideoInfo_exportXls.do?num='+num+'&categoryFilter='+cate+'&accuratelyMatched='+accuratelyMatched;
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
	
<h1><s:text name="soku.videoInfo.title" /></h1>

</div>

<div class="select-bar">
	<s:form action="VideoInfo_list"	validate="true" cssClass="form" theme="simple">
			<label> <s:textfield key="searchWord" cssClass="text" /> </label>
			<label>  </label>
			
			精确匹配:
			<s:select key="accuratelyMatched" list="#{0:'否', 1:'是'}" listKey="key" listValue="value"></s:select>
			
			剧集状态： 
			<s:select key="statusFilter" list="statusMap" listKey="key" listValue="value"></s:select>
			
			类型：
			<s:select key="categoryFilter" list="directCategory" listKey="key" listValue="value"></s:select>
			重点关注:
			<s:select key="concernLevel" list="#{-1:'所有',0:'否', 1:'是'}" listKey="key" listValue="value"></s:select>
			<input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" /> 
			<s:hidden name="listSerials" />
		</s:form> 
		<span>总集数为0表示持续更新, 版本名为绿色表示被屏蔽， 版本名为红色的表示</span>
		<br/><label> <s:textfield id="num" name="num" cssClass="text" value="200"/> </label>
	<label> <input id="list" type="button" onclick="exportXls()" 
		class="ui-button ui-state-default ui-corner-all" value="导出剧集" /> </label>
</div>
<s:actionerror cssClass="actionerror"/>
<input type="hidden" id="teleplayupdateurl" value="<s:url action="TeleplayVersion_update"></s:url>" />
<input type="hidden" id="movieupdateurl" value="<s:url action="Movie_update"></s:url>" />
<input type="hidden" id="varietyupdateurl" value="<s:url action="Variety_update"></s:url>" />
<input type="hidden" id="animeupdateurl" value="<s:url action="AnimeVersion_update"></s:url>" />
<div class="table"><img src="img/bg-th-left.gif" width="8"
	height="7" alt="" class="left" /> <img src="img/bg-th-right.gif"
	width="7" height="7" alt="" class="right" /> <s:form
	action="VideoInfo_changeSerial" validate="false" cssClass="form"
	theme="simple">
	<table class="listing" cellpadding="0" cellspacing="0">
		<s:hidden name="serialId"></s:hidden>
		<s:hidden name="fkNamesId"></s:hidden>
		<tr>
			<th align="center" width="5%"><input id="thcheck"
				type="checkbox" /></th>
			<th align="center" width="5%">入库时间</th>
			<th align="center" width="10%"><s:text
				name="videoInfo.heading.name" /></th>
			<th align="center" width="5%"><s:text
				name="videoInfo.heading.category" /></th>
			<th align="center" width="10%">节目名</th>
			<th align="center">海报</th>
			<th align="center" width="5%"><s:text
				name="videoInfo.heading.order" /></th>
				
			<th align="center" width="5%">版权</th>
			<th align="center" width="5%">展示</th>
			<th align="center" width="5%">总集数</th>
			
			<th align="center" width="15%">基本信息</th>
			<th align="center" width="10%"><s:text
				name="videoInfo.heading.operation" /></th>
		</tr>

		<s:iterator value="pageInfo.results" status="index">
			<tr <s:if test="#index.odd">class="bg"</s:if>>
				<!-- checkbox -->
				<td align="center"><s:if test="category != 0">
					<input type="checkbox" name="batVideoids"
						value="<s:property value="id" />,<s:property value="category" />" />
				</s:if></td>
				<!-- createTime -->
				<td><s:property value="createTime" /></td>
				<!-- name -->
				<td align="center">
				
					<s:if test="category == 0">
						<a class="listbutton"
							href="<s:url action="Series_input">
										<s:param name="seriesId" value="id"/>
										<s:param name="searchWord" value="searchWord"/>
										<s:param name="pageNumber" value="pageNumber"/>
											</s:url>">
						<s:property value="seriesName" /> </a>
					</s:if> 
				
					<s:else>
					 <a <s:if test="block == 1">class="listbuttonblock"</s:if>
						<s:if test="block == 0">class="listbutton01"</s:if>
						href="<s:url action="VideoInfo_input">
									<s:param name="videoId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
					<s:property value="seriesName" escape="false" /> </a>
					</s:else>
				
				</td>
				<s:if test="category == 0">
					<td align="left" colspan="8"><s:if test="serialAlias != null">系列别名： <s:property
							value="serialAlias" />
				</s:if>
					
				</td>
				</s:if>
				<!-- category name and version name -->
				<s:if test="category != 0">
					<td align="center"><s:property value="categoryName" /></td>
					<td align="center">					
					<a <s:if test="block == 1">class="listbuttonblock"</s:if>
						<s:if test="block == 0">class="listbutton01"</s:if>
						href="<s:url action="VideoInfo_input">
									<s:param name="videoId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
					<s:property value="name" escape="false" /> </a>
					</td>
					<td align="center">
						<s:if test="thumb != null"><img src="<s:property value="thumb" />" /></s:if>
					</td>
					<td align="center" <s:if test="seriesId > 0">class="editOrder"</s:if> >
						<s:property value="seriesOrder" />
						<s:hidden name="id" />
						<s:hidden name="category" />
						
					</td>
	
					<s:if test="haveRight == 1"><td align="center" class="haveright">版权</td></s:if>
					<s:if test="haveRight == 3"><td align="center" class="shenhe">剧集库</td></s:if>
					<s:if test="haveRight == 0"><td align="center">无版权</td></s:if>
					<td align="center"><s:if test="block == 1">不展示</s:if><s:if test="block == 0">展示</s:if></td>
					<td align="center">总集数: <s:property value="episodeTotal" /></td>
					<td align="left"><s:property value="brief" escape="false"/></td>
				</s:if>
				<td align="center">
					<s:if test="category != 0">
					<s:if test="block == 1">
						<a class="listbutton" onclick="return confirmUnBlock();"
							href="<s:url action="VideoInfo_unblock">
									<s:param name="videoId" value="id"/>
									<s:param name="latestUpdate" value="latestUpdate" />
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="accuratelyMatched" value="accuratelyMatched" />
									<s:param name="statusFilter" value="statusFilter" />
									<s:param name="categoryFilter" value="categoryFilter" />
									<s:param name="concernLevel" value="concernLevel" />
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
						<s:text name="videoInfo.unblock" /> </a> <br/>
						<br />
					</s:if>

					<s:if test="block == 0">
						<a class="listbutton" onclick="return confirmBlock();"
							href="<s:url action="VideoInfo_block">
									<s:param name="videoId" value="id"/>
									<s:param name="latestUpdate" value="latestUpdate" />
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="accuratelyMatched" value="accuratelyMatched" />
									<s:param name="statusFilter" value="statusFilter" />
									<s:param name="categoryFilter" value="categoryFilter" />
									<s:param name="concernLevel" value="concernLevel" />
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
						<s:text name="videoInfo.block" /> </a> <br/>
						<br />
					</s:if>
					
					<s:if test="concernLevel == 1">
						<a class="listbutton" onclick="return confirmConcern(1);"
							href="<s:url action="VideoInfo_unconcern">
									<s:param name="videoId" value="id"/>
									<s:param name="latestUpdate" value="latestUpdate" />
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="accuratelyMatched" value="accuratelyMatched" />
									<s:param name="statusFilter" value="statusFilter" />
									<s:param name="categoryFilter" value="categoryFilter" />
									<s:param name="concernLevel" value="concernLevel" />
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
						取消关注 </a> <br/>
						<br />
					</s:if>

					<s:if test="concernLevel == 0">
						<a class="listbutton" onclick="return confirmConcern(0);"
							href="<s:url action="VideoInfo_concern">
									<s:param name="videoId" value="id"/>
									<s:param name="latestUpdate" value="latestUpdate" />
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="accuratelyMatched" value="accuratelyMatched" />
									<s:param name="statusFilter" value="statusFilter" />
									<s:param name="categoryFilter" value="categoryFilter" />
									<s:param name="concernLevel" value="concernLevel" />
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
						关注更新 </a> <br/>
						<br />
					</s:if>

					<a class="listbutton" href="/EpisodeDetail_detail.do?programmeId=<s:property value="id"/>">全剧情况</a> <br /> <br/>
					<a class="listbutton"
						href="<s:url action="ProgrammeSite_list">
									<s:param name="programmeId" value="id" />
									</s:url>">站点版本</a> 
			
						<br/> 
						</s:if>
						<s:else>
							<br />
							<br />
						</s:else>
				</td>
			</tr>
		</s:iterator>

		<tfoot>
			<tr>
				<td><a href="#" id="batchdelete"></a></td>
				<td colspan="2"><input type="button" id="changeSerial"
					class="ui-button ui-state-default ui-corner-all" value="组建系列"></input>
					<input type="button" id="deleteSerial"
					class="ui-button ui-state-default ui-corner-all" value="移除系列"></input>
					</td>
				<td colspan="9"><s:property
					value="pageInfo.totalRecords" /> ----- <s:text name="totalpagenumber" />: <s:property
					value="pageInfo.totalPageNumber" /> <s:text
					name="currentpagenumber" />: <s:property
					value="pageInfo.currentPageNumber" /> <s:if
					test="pageInfo.hasPrevPage">
					<a
						href="<s:url action="VideoInfo_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="statusFilter" value="statusFilter" />
								<s:param name="categoryFilter" value="categoryFilter" />
								<s:param name="listSerials" value="listSerials" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="area" value="area" />
							</s:url>"><s:text
						name="prevpage" /></a>
				</s:if> <s:if test="pageInfo.hasNextPage">
					<a
						href="<s:url action="VideoInfo_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="categoryFilter" value="categoryFilter" />
								<s:param name="statusFilter" value="statusFilter" />
								<s:param name="listSerials" value="listSerials" />
								<s:param name="searchWord" value="searchWord" />
								<s:param name="area" value="area" />
							</s:url>"><s:text
						name="nextpage" /></a>
				</s:if></td>
			</tr>
		</tfoot>
	</table>
	<s:hidden name="pageNumber" />
	<s:hidden name="searchWord" />
</s:form>
<div class="select"><strong>Other Pages: </strong> <input
	type="hidden" id="pageurl"
	value="<s:url action="VideoInfo_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="videoInfoFilter" value="videoInfoFilter" />
								<s:param name="statusFilter" value="statusFilter" />
								<s:param name="categoryFilter" value="categoryFilter" />
								<s:param name="listSerials" value="listSerials" />
								<s:param name="area" value="area" />
								<s:param name="pageNumber" value="0" />
							</s:url>" />
<select id="pageselect">
	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
				<option><s:property
					value="pageInfo.currentPageNumber - #count.index" /></option>
			</s:if>
		</s:iterator>
	</s:bean>

	<s:bean name="org.apache.struts2.util.Counter" id="counter">
		<s:param name="first" value="1" />
		<s:param name="last" value="10" />
		<s:iterator status="count">
			<s:if
				test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
				<option><s:property
					value="#count.index + pageInfo.currentPageNumber + 1" /></option>
			</s:if>
		</s:iterator>
	</s:bean>
</select></div>
</div>

</div>

</div>
<div id="footer"></div>
</div>
<div id="dialogNames" title="Search"><input type="text"
	name="searchWord" id="diaSearchWord" /> <input type="button"
	value="search" onclick="searchNames()" />
<div id="diaResult"></div>
</div>
<div id="dialogVideoOption" title="选择分类"><input type="checkbox"
	name="cboxvideo" value="1" onclick="chooseOneVideo(this);" />电视剧 <input
	type="checkbox" name="cboxvideo" value="2"
	onclick="chooseOneVideo(this);" />电影 <input type="checkbox"
	name="cboxvideo" value="3" onclick="chooseOneVideo(this);" />综艺节目 <input
	type="checkbox" name="cboxvideo" value="4"
	onclick="chooseOneVideo(this);" />动漫</div>
</body>
</html>
