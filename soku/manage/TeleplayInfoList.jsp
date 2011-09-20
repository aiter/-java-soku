<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="videoInfo.title.list" /></title>

	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
          type="text/css"/>
	<link type="text/css" href="<s:url value="/soku/manage/css/ui.all.css"/>" rel="stylesheet" />
	
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.core.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/ui.dialog.js"/>"></script>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery.form.js"/>"></script>

	<script type="text/javascript">
		$(function() {

			var blockSuccess= '<s:property value="blockSuccess"/>';

			

			if(blockSuccess == "blocksuccess") {
				alert("屏蔽成功");
			}
			if(blockSuccess == "unblocksuccess") {
				alert("解除屏蔽成功");
			}

			$('.editname').dblclick(function(event) {			
				if(editing) return;
				editobj = $(this);
				html = $(this).html();
				wordstartidx = html.indexOf('>');
				wordendidx = html.indexOf('</a>');
				word = html.substring(wordstartidx + 1, wordendidx);

				idstartidx = html.indexOf('value="');
				idendidx = html.indexOf('"', idstartidx + 7);
				wordid = html.substring(idstartidx + 7, idendidx);

				newhtmlhead = html.substring(0, wordstartidx+1);
				newhtmltail = html.substring(wordendidx); 
		        $(this).html('<input id="newkeyword" type="text" value="' + word + '" /> <input type="button" id="ajaxupdate" value="更新" onclick="updatekeyword();"/> <input type="button" id="ajaxupdate" value="取消" onclick="cancelupdatekeyword();"/>');
		        editing = true;
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
					resultLocation = '<s:url action="AnimeVersion_input"><s:param name="animeVersionId" value="-1" /></s:url>';
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
						resultStr += jsonArr[idx].name + '</td></tr>';
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

		function updatekeyword(){		
    	    
		    editing = false;
		    newkeyword = $("#newkeyword").val();
		    $.ajax({
			    url: $("#changewordurl").val(),
			    type: "POST",
			    data: ({"teleplayName" : newkeyword, "teleplayId" : wordid}),
			    success: function(msg) {					
					editobj.html(newhtmlhead + newkeyword + newhtmltail);					
		    	}
			});
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
      	 <a href="<s:url action="Names_input">
			<s:param name="namesId" value="-1" />
		</s:url>"  class="button"><s:text
            name="videonames.add"/></a>
        <h1><s:text name="soku.videoInfo.title" /></h1>
      </div>
      <br />
      <div class="select-bar">
         <s:form action="VideoInfo_list" validate="false" cssClass="form" theme="simple">
	        <s:if test="videoInfoFilter!=null">
			</s:if>
      		<label>
	        <s:textfield key="searchWord" cssClass="text"/>
	        </label>
	        <label>
	        <input type="submit" name="Submit" class="ui-button ui-state-default ui-corner-all" value="Search" />
	        </label>
	        
      	</s:form> 
      	<span>总集数为0表示持续更新, 版本名为绿色表示被屏蔽， 版本名为红色的表示<span>
      </div>
      <div class="table"> <img src="img/bg-th-left.gif" width="8" height="7" alt="" class="left" /> <img src="img/bg-th-right.gif" width="7" height="7" alt="" class="right" />
        <s:form action="VideoInfo_changeSerial" validate="false" cssClass="form" theme="simple">
         <table class="listing" cellpadding="0" cellspacing="0">
				<s:hidden name="serialId"></s:hidden>
				<s:hidden name="fkNamesId"></s:hidden>
				<tr>
					<th align="center" width="5%"><input id="thcheck" type="checkbox" /></th>
					<th align="center" width="10%"><s:text name="videoInfo.heading.name" /></th>
					<th align="center" width="10%"><s:text name="videoInfo.heading.category" /></th>
					<th align="center" width="25%"><s:text name="videoInfo.heading.versionName" /></th>
					
					<th align="center" width="10%"><s:text name="videoInfo.heading.versionAlias" /></th>
					<th align="center" width="5%"><s:text name="videoInfo.heading.order" /></th>
					<th align="center" width="5%"><s:text name="videoInfo.heading.episodeTotal" /></th>
					<th align="center" width="10%"><s:text name="videoInfo.heading.area" /></th>
					<th align="center" width="10%"><s:text name="videoInfo.heading.operation" /></th>
				</tr>

				<s:iterator value="pageInfo.results" status="index">
					<tr <s:if test="#index.odd">class="bg"</s:if>>
						<td align="center">
						<s:if test="category != 0">
							<input type="checkbox" name="batVideoids" value="<s:property value="id" />,<s:property value="category" />" />
						</s:if>
						
						</td>
						<td align="center">
							<s:if test="category == 0">
								<a class="listbutton" href="<s:url action="Names_input">
									<s:param name="namesId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:property value="name" />
								</a></s:if>
							
						</td>
						<s:if test="category == 0">
							<td align="left" colspan="6">
								<s:if test="serialAlias != null">系列别名： <s:property value="serialAlias" /></s:if>
							</td>
						</s:if>
						
						<s:if test="category != 0">
						<td align="center"><s:property value="categoryName" /></td>
						<td align="center" class="editname">
							<a <s:if test="block == 1">class="listbuttonblock"</s:if>
									<s:if test="block == 0">class="listbutton01"</s:if> href="<s:url action="TeleplayVersion_input">
									<s:param name="teleplayVersionId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:property value="versionName" escape="false"/>
								</a>
						</td>
						
						<td align="center"><s:property value="versionAlias" /></td>
						<td align="center" class="editorder"><s:property value="order" /></td>
						<td align="center">
							<s:if test="episodeTotal != 0"><s:property value="episodeTotal" /></s:if>							
						</td>
						<td align="center"><s:property value="area" /></td>
						</s:if>
						<td align="center">
							
							<s:if test="category == 0">
								<a class="listbutton" href="<s:url action="Names_input">
									<s:param name="namesId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:text name="videoInfo.edit" /> 
								</a>
								<a class="listbutton" href="#" onclick="showOption(<s:property value="id" />);"><s:text
            							name="videoInfo.add"/></a>
							</s:if> 
							
							<s:if test="category == 1">
							<!--  
								<a class="listbutton" href="<s:url action="TeleplayVersion_input">
									<s:param name="teleplayVersionId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:text name="videoInfo.edit" /> 
								</a> -->
								
								<a class="listbutton" href="<s:url action="TeleplayVersion_block">
									<s:param name="teleplayVersionId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:text name="videoInfo.block" /> 
								</a> 
								
								<a class="listbutton" href="<s:url action="TeleplayVersion_unblock">
									<s:param name="teleplayVersionId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:text name="videoInfo.unblock" /> 
								</a> 
								 
								<a class="listbutton" onclick="return confirmDelete();" href="<s:url action="TeleplayVersion_delete">
									<s:param name="teleplayVersionId" value="id"/>
									<s:param name="searchWord" value="searchWord"/>
									<s:param name="pageNumber" value="pageNumber"/>
										</s:url>">
									<s:text name="videoInfo.delete" /> 
								</a> 
								<a class="listbutton" href="<s:url action="TeleplaySiteVersion_list">
								<s:param name="teleplaySiteVersionId" value="-1" /><s:param name="teleVersionId" value="id" />
									</s:url>" ><s:text
            							name="teleplaySiteVersion.list"/></a>
							</s:if>   
							
							
							</td>
					</tr>
				</s:iterator>
				
				 <tfoot>
					<tr>
						<td><a href="#" id="batchdelete"></a></td>
						<td><input type="button" id="changeSerial" class="ui-button ui-state-default ui-corner-all" value="组建系列"></input></td></td>
						<td colspan="7">
						<s:text name="totalpagenumber" />: <s:property value="pageInfo.totalPageNumber"/>
						<s:text name="currentpagenumber" />: <s:property value="pageInfo.currentPageNumber"/>
						<s:if test="pageInfo.hasPrevPage">
							<a href="<s:url action="VideoInfo_list"> 
								<s:param name="pageNumber" value="pageInfo.currentPageNumber - 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="prevpage"/></a>
						</s:if>
						<s:if test="pageInfo.hasNextPage">
						<a href="<s:url action="VideoInfo_list">
								<s:param name="pageNumber" value="pageInfo.currentPageNumber + 1" />
								<s:param name="searchWord" value="searchWord" />
							</s:url>"><s:text
								name="nextpage"/></a>
						</s:if></td>
					</tr>
				</tfoot>
			</table>
			<s:hidden name="pageNumber" />
    		<s:hidden name="searchWord" />
			</s:form>
        <div class="select"> <strong>Other Pages: </strong>
           <input type="hidden" id="pageurl" value="<s:url action="VideoInfo_list">
								<s:param name="searchWord" value="searchWord" />
								<s:param name="videoInfoFilter" value="videoInfoFilter" />
								<s:param name="pageNumber" value="0" />
							</s:url>" />
          <select id="pageselect">
          		<s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber - #count.index) > 0">
     					 <option><s:property value="pageInfo.currentPageNumber - #count.index"/></option>
     					 </s:if> 
    				</s:iterator>  
   			 	</s:bean> 
          		 
          	   <s:bean name="org.apache.struts2.util.Counter" id="counter">  
     			 <s:param name="first" value="1"/>  
     			 <s:param name="last" value="10"/>  
   				 	<s:iterator status="count">  
   				 		<s:if test="(pageInfo.currentPageNumber + #count.index) < pageInfo.totalPageNumber">
     					 	<option><s:property value="#count.index + pageInfo.currentPageNumber + 1"/></option>
     					</s:if>  
    				</s:iterator>  
   			 	</s:bean>  
          </select>
        </div>
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>
 <div id="dialogNames" title="Search">
	<input type="text" name="searchWord" id="diaSearchWord"/> <input type="button" value="search" onclick="searchNames()" />
	<div id="diaResult"> </div>
 </div>
  <div id="dialogVideoOption" title="选择分类">
	
	<input type="checkbox" name="cboxvideo" value="1" onclick="chooseOneVideo(this);"/>电视剧
	<input type="checkbox" name="cboxvideo" value="2" onclick="chooseOneVideo(this);"/>电影
	<input type="checkbox" name="cboxvideo" value="3" onclick="chooseOneVideo(this);"/>综艺节目
	<input type="checkbox" name="cboxvideo" value="4" onclick="chooseOneVideo(this);"/>动漫
 </div>
</body>
</html>
