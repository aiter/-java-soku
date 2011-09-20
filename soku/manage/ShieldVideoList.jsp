<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><s:text name="shieldVideo.title.list" /></title>


	<link href="<s:url value="/soku/manage/css/all.css"/>" rel="stylesheet"
	type="text/css" />
	<link href="<s:url value="/soku/manage/css/soku.css"/>" rel="stylesheet"
	type="text/css" />
	<style type="text/css">
			.china {
   				background: url(/soku/manage/img/china.png) no-repeat;
   				display: block;
                position: absolute;
  			    width: 128px;
                height: 96px;
                top: 0px;
   				border: 0px;
			}
		
	</style>
	<script type="text/javascript" src="<s:url value="/soku/manage/js/jquery-1.3.2.js"/>"></script>
	
	
	<script type="text/javascript">
		

		function search() {
			try {
				$.ajax({
					url: '<s:url action="ShieldVideo_search" />',
					type: 'POST',
					data: 'keyword=' + $("#keyword").val() + '&curpage=' + $("#curpage").val(),
					success: function(data) {
						var json = eval('(' + data + ')');
						var html = '';							
						for(var idx in json.items) {							
								var spanclass = '';
								if(json.items[idx].delete_flag) {
									spanclass = 'class = "china"';
								}
								html += '<div class="item">';
								html += '<ul>';							
								html += '<li class="i_thumb"> <span id="china' + idx + '" ' + spanclass + '></span> <img src="' + formatLogo(json.items[idx].logo) + '" /></li>';
								html += '<li class="i_title"><a title="' + json.items[idx].title + '" href="' + json.items[idx].url + '" target="_blank">' + json.items[idx].title + '</a></li>';
								html += '<li class="i_param"><a href="#" onclick="deleteVideo(' + idx + '); return false">删除</a></li>';
								html += '</ul>';
								html += '<input type="hidden" id="url' + idx + '" value="' + json.items[idx].url + '" />';
								html += '</div>';
								
							
								if((parseInt(idx) + 1) % 5 == 0) {
									html += '<div class="clear"></div>';
								}
							}							
						$("#items").html(html);
						//document.getElementById("items").innerHTML = html;
						
						var pages = json.total / json.page;
						var curpage = $("#curpage").val();
						if(curpage == "") {
							curpage = 1;
						}

						var pageshtml = '';
						var prepage = 0;
						var lastpage = 0;
						if(curpage > 1) {
							pageshtml += '<a href="#" onclick="selectpage(' + (parseInt(curpage) - 1) + '); return false"> &lt;上一页 </a>'
						}
						if(curpage <= 5) {
							for(var i = 1; i <= 10; i++) {							
								var pagenum = parseInt(i);
								if(pagenum > pages) {
									break;
								}
								if(pagenum == curpage) {
									pageshtml += '<span class="current">' + pagenum + '</span>';
								} else {
									pageshtml += '<a href="#" onclick="selectpage(' + pagenum + '); return false">' + pagenum + '</a>';
								}
								lastpage = pagenum;
							}
						} else {
							for(var i = 5; i > 0; i--) {
								var pagenum = parseInt(curpage) - parseInt(i);
								
								if(pagenum == curpage) {
									pageshtml += '<span class="current">' + pagenum + '</span>';
								} else {
									pageshtml += '<a href="#" onclick="selectpage(' + pagenum + '); return false">' + pagenum + '</a>';
								}							
							}

							for(var i = 0; i < 5; i++) {
								var pagenum = parseInt(curpage) + parseInt(i);
								if(pagenum > pages) {
									break;
								}
								if(pagenum == curpage) {
									pageshtml += '<span class="current">' + pagenum + '</span>';
								} else {
									pageshtml += '<a href="#" onclick="selectpage(' + pagenum + '); return false">' + pagenum + '</a>';
								}	
								lastpage = pagenum;						
							}
						}
						if((lastpage + 1) < pages) {
							pageshtml += '<a href="#" onclick="selectpage(' + (parseInt(curpage) + 1) + '); return false">  下一页&gt;  </a>'
						} 
						
						$("#pages").html(pageshtml);
						}
						
				});	
			}catch(e) {
				alert('excepiton');
				alert(e);
			}
		}

		function formatLogo(logo) {
			var httpHead = "http://";
			var imghead = httpHead + 'g' + parseInt(Math.random() * 4 + 1) + '.ykimg.com/';
			if(logo.indexOf(httpHead) < 0){
				logo = imghead + logo;
			}
			return logo;
		}

		function selectpage(page) {
			$("#curpage").val(page);
			search();
		}

		function deleteVideo(idx) {

			if(confirm("确认删除此视频？")) {
				$.ajax({
					url: '<s:url action="ShieldVideo_delete" />',
					type: 'GET',
					data: 'url=' + escape($("#url" + idx).val()),
					success: function(data) {
						$("#deletevideo").val(1);
						if(data.indexOf("ok") >= 0) {
							$("#china" + idx).addClass("china");
						}
					}
				});
			}
			
			
		}


		function deleteVideoUrl(url) {

			if(url == 1) {
				url = $("#url").val();
				if(url == '') {
					alert("所填url为空");
					return;
				}
			}

			if(confirm("确认删除此视频？")) {
				$.ajax({
					url: '<s:url action="ShieldVideo_delete" />',
					type: 'GET',
					data: 'url=' + escape(url),
					success: function(data) {
						
						if(data.indexOf("ok") >= 0) {
							alert('删除成功！');
						}
					}
				});
			}
		}

		function clearCache() {
			if($("#deletevideo").val() != 1) {
				alert('还没删除视频');
				return;
			}
			$.ajax({
				url: '<s:url action="ShieldVideo_clearCache" />',
				type: 'GET',
				data: 'keyword=' + $("#keyword").val() + '&curpage=' + $("#curpage").val(),
				success: function(data) {					
					alert('删除成功！');
				}
			});
		}
	</script>
</head>
<body>
<div id="main">
  <div id="header">
  </div>
  <div id="middle">
    <s:include value="module/shieldleftnav.jsp"></s:include>
    <div id="center-column">
      <div class="top-bar"> 
      
      <h1>搜索结果删除</h1>
      <a href="<s:url action="ShieldWord_input">
			<s:param name="wordId" value="-1" />
		</s:url>"  class="shieldbutton">添加屏蔽词</a>
	 
      <a href="<s:url action="ShieldVideo_list"></s:url>" class="shieldbutton">删除单个视频</a>
      <br />
   	    
      </div>
     删除单个URL: <s:textfield key="keyword" id="url" size="60" theme="simple"/>
	       	  <button id="search" onclick="deleteVideoUrl(1);">删除</button>
      <div class="soku_tool">         
      		<div id="tool" class="tool outer">
	        <s:textfield key="keyword" id="keyword" cssClass="sotext" theme="simple"/>
	       	<button type="submit" class="sobtn" id="search" onclick="search();">搜索</button>
	      
	        <label>
	        <input type="hidden" id="curpage" value=""/>
	        <input type="hidden" id="deletevideo" value=""/>
	         </label>   
	         </div>  
	          	<button id="clearcache" onclick="clearCache();">提交删除</button> 	(提交删除，可以立即生效)
      </div>
      
  
      <div class="table"> 
        <s:form action="ShieldVideo_batchdelete" validate="false" cssClass="form" theme="simple">
        	<div class="result">
        		<div class="items" id="items"></div>
        		<div class="pages" id="pages"></div>
        	</div>
		</s:form>
       
      </div>
      
    </div>

  </div>
  <div id="footer"></div>
</div>
</body>
</html>
