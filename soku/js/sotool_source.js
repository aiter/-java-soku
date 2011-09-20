var Filter = {
	init: function(){
		this.conditions = $('.filter .item');
		if(this.conditions.length == 0){ return; }
		this.bind();
	},
	bind: function(){
		var _this = this;
		this.conditions.each(function(){
			var condition = $(this);
			var items = condition.find('li');
			items.each(function(){
				$(this).find('a').click(function(){
					var current = condition.find('li.current');	
					if(current.length != 0){
						current.removeClass('current');				
					}
					$(this).parent().addClass('current');
					return false;
				});
			});
		});
		
	}	
}

var Selector = {
	init: function(){
		this.selectors = $('.selector');
		if(this.selectors.length == 0){ return; }
		this.bind();
	},
	bind: function(){
		$.each(this.selectors, function(){
			var selected = $(this).find('.selected');
			var options	= $(this).find('.options');
			var selectedIndex = options.attr('index');					
			var optionitems	= $(this).find('.options').find('li');
			var optioncurrent = options.find('[index='+ selectedIndex + ']');
			selected.click(function(){
				options.toggle();
				return false;
			});
			$.each(optionitems, function(){
				if($(this).attr('index') == selectedIndex){ $(this).addClass('current'); }
				$(this).find('a').click(function(){
					var option = $(this).parent(); 
					var index = option.attr('index');
					if(index != selectedIndex) {
						selected.html($(this).text());
						optioncurrent.removeClass('current');
						option.addClass('current');
						selectedIndex = index;
						optioncurrent = option;
						options.hide();
					}
					return false;	
				});
			});
		});	
	}	
}

var ViewBy = {
	init: function(){
		var viewby = $('.viewby');
		if(viewby.length != 0){
			this.bys = viewby.find('li');
			this.vcoll = $('#vcoll');
			this.pcoll = $('#pcoll');
			this.by = viewby.find('.current');
			this.bind();	
		}	
	},
	bind: function(){
		var _this = this;
		this.bys.each(function(){
			$(this).click(function(){
				if($(this).attr('viewby') != _this.by.attr('viewby')){
					$(this).addClass('current');
					var type = $(this).attr('viewby');
					
					if(_this.vcoll.length != 0){
						if(type == 'list'){ 
							_this.vcoll.addClass('colllist1w').removeClass('collgrid4w');	
						}else{
							_this.vcoll.addClass('collgrid4w').removeClass('colllist1w');
						}
					}
					if(_this.pcoll.length != 0){
						if(type == 'list'){ 
							_this.pcoll.addClass('colllist1w').removeClass('collgrid4w');	
						}else{
							_this.pcoll.addClass('collgrid4w').removeClass('colllist1w');
						}
					}
					if(_this.by.length != 0){
						_this.by.removeClass('current');
					}
				}
				_this.by = $(this);
				
				
				writeCookie("viewby",_this.by.attr("viewby"),24*30*12);
			});
		});
	}	
	
}

var PartPop = {
	init: function(){
		this.pophandles = $('.v .v_part');
		this.globalpop = $('#globalpop');
		if(this.pophandles.length == 0){ return; }
		this.bind();
	},
	bind: function(){
		var _this = this;
		$.each(this.pophandles, function(){
			var handle = $(this).find('.handle');
			var po = $(this).find('.pop');
			$(this).mouseover(function(){
				if(_this.globalpop.length == 0){ _this.create(); }
				$.each(po.find('img'), function(){
					var src = $(this).attr('_src');
					if(src){
						$(this).attr('src', src)
						$(this).removeAttr('_src');	
					}
				});
				_this.globalpop.html(po.html());
				
				var pos = handle.offset();
				var top = pos.top - 4;
				var left = pos.left + 20;
				if(pos.left + 280 + 5 > parseInt($(window).width())){
					left = pos.left - 280;
				} 
				_this.globalpop.css({'top': top, 'left': left});

				_this.globalpop.show();	
			});
			$(this).mouseout(function(){
				_this.globalpop.hide();	
			});
		});	
	},
	create: function(){
		if(this.globalpop.length== 0){
			this.globalpop = $('<div id="globalpop" class="pop"></div>');
			$(document.body).append(this.globalpop);
			this.globalpop.mouseover(function(){ $(this).show(); });	
			this.globalpop.mouseout(function(){ $(this).hide(); });
		}	
	}
}

var SpeedSelector = {

	init: function(){
		var thislabels = $('.source .check label');
		$.each(thislabels,function(){
			SpeedSelector.selectSite($(this),false);
		});
		this.selectors = $('.gotoplay .source');
		this.bind();	
	},

	bind: function(){
		$.each(this.selectors, function(){
			var check = $(this).find('.check');
			var other = $(this).find('.other');
			var others = other.find('li');
			$(this).click(function(){
				if(other.length != 0){
					 if(others.length != 0){
						 /** sort start*/
						 var temp;
						 var exchange;
						 for(var i=0; i<others.length; i++) {
						   exchange = false;
						   for(var j=others.length-2; j>=i; j--) {
						    if(parseInt($(others[j+1]).find('div').attr("class").substring(11)) < parseInt($(others[j]).find('div').attr("class").substring(11))) {
						     temp = others[j+1];
						     others[j+1] = others[j];
						     others[j] = temp;
						     exchange = true;
						    }
						   }
						   if(!exchange) break;
						  }
						 var htmlArr = new Array();
						 for(var i=0; i<others.length; i++) {
							 htmlArr[i]=$(others[i]).html();
						 }
						 var othersTmp = other.find('li');
						 for(var i=0; i<othersTmp.length; i++) {
							 $(othersTmp[i]).html(htmlArr[htmlArr.length-1-i]);
						 }
						 /** sort end*/
						other.toggle();
					 }
				}
				return false;
			});
			$(document).bind('click', function(){
				other.hide();
			});
			$.each(others, function(){
				$(this).click(function(){
					var thislabel = $(this).find('label');
					var detailDiv = $(this).parent().parent().parent().parent();
					var linkpanels = detailDiv.find('.linkpanels');
					$.each(linkpanels,function(){
						$(this).css("display","none");
					});
					detailDiv.find('.'+thislabel.attr("id")).css("display","");
					SpeedSelector.selectSite(thislabel,true);
					var thishtml = $(this).html();
					var chckhtml = check.html();
					check.html(thishtml);
					$(this).html(chckhtml);
					other.hide();
					
					if($('.source .check label').attr("id")!="site14"){
						
						$('.base_update').css("display","none");
					}else{
						$('.base_update').css("display","");
					}
					return false;	
				});

			});
			
			/*if($(check).find('label').attr('id').substring(4)!="14" && others.lenth != 0){
				*//** sort start*//*
				 var temp;
				 var exchange;
				 for(var i=0; i<others.length; i++) {
				   exchange = false;
				   for(var j=others.length-2; j>=i; j--) {
				    if(parseInt($(others[j+1]).find('div').attr("class").substring(11)) < parseInt($(others[j]).find('div').attr("class").substring(11))) {
				     temp = others[j+1];
				     others[j+1] = others[j];
				     others[j] = temp;
				     exchange = true;
				    }
				   }
				   if(!exchange) break;
				  }
				 
				 *//** change checked site & other top site*//*
				 var otherTop = parseInt($(others[0]).find('div').attr("class").substring(11));
				 var checked = parseInt($(check).find('div').attr("class").substring(11));
				 if(otherTop>checked){
					 var thislabel = $(others[0]).find('label');
						var detailDiv = $(others[0]).parent().parent().parent().parent();
						var linkpanels = detailDiv.find('.linkpanels');
						$.each(linkpanels,function(){
							$(this).css("display","none");
						});
						detailDiv.find('.'+thislabel.attr("id")).css("display","");
						SpeedSelector.selectSite(thislabel);
						
					 var otherTopHtml=$(others[0]).html();
					 $(others[0]).html($(check).html());
					 $(check).html(otherTopHtml);
					 //$(othres[0]).click();
				 }
				 *//** sort end*//*
			}*/

		});

	},
	selectSite: function(thislabel,isLog){
		var detailDiv = thislabel.parent().parent().parent().parent().parent().parent();
		detailDiv.find('.p_status span.status').html(thislabel.attr("title"));
        detailDiv.find('.p_ishd span').attr("class","ico__"+thislabel.attr("stype"));
        detailDiv.find('.p_ishd span').attr("title",thislabel.attr("stypename"));
        var showBg = thislabel.attr("title").length+thislabel.attr("stype").length;
        if(showBg==0){
        	detailDiv.find('.p_status span.bg').attr("class","bgd");
        }else{
        	detailDiv.find('.p_status span.bgd').attr("class","bg");
        }
        
        if(isLog){
        	var logParam = {_log_type:2, _log_pos:thislabel.attr("_log_pos"),_log_ct:thislabel.attr("_log_ct"),_log_directpos:8};
        	sokuClickStat(logParam);
		}
		var linkpanels = detailDiv.find(".linkpanels");
		$.each(linkpanels, function(){
			if($(this).css("display")!='none'){
				var videos = $(this).find("ul").find("li").find("a");
				if(videos.length>0){
					var url = $(videos[0]).attr("href");
					var btnplay = detailDiv.find('.btnplay_large a');
					btnplay.attr("href",url);
        			detailDiv.find('.p_link_site a').attr("href",url);
        			
        			//detailDiv.find('.p_link_site a').unbind('click').removeAttr('onclick');
        			//detailDiv.find('.p_link_site a').bind( "click", function() { 
        				//clicklog(url,btnplay.attr('keyword'),2,btnplay.attr('cate'),-1);
        			//});
        			//btnplay.unbind('click').removeAttr('onclick');
        			//btnplay.bind( "click", function() { 
        			//	clicklog(url,btnplay.attr('keyword'),2,btnplay.attr('cate'),0);
        			//});
				}
			}
		});
	}	


}

var PanelExpand = {
	init: function(){
		this.handles = $('.linkpanel .handle');
		if(this.handles.length == 0){ return; }
		this.bind();	
	},
	bind: function(){
		$.each(this.handles, function(){
			$(this).click(function(){
				var expandpanel = $(this).parent().parent().find('.panelexpand');
				$(this).toggleClass('handleexpand');
				expandpanel.toggle();
				if($.trim($(this).text()) == '全部'){
					$(this).html('<span>收起</span>');	
				}else{
					$(this).html('<span>全部</span>');	
				}
				return false;	
			});	
		})	
	}	
}

var FeedBack = {
	init: function(){
		this.handles = $('.feedwin .handle');
		this.steps=$('.feedwin .step');
		if(this.handles.length == 0){ return; }
		this.bind();	
	},
	bind: function(){
		$('.feedwin .close').click(function(){ 
			$(".feedwin .feed").css("display","none");
			
			$.each($('.feedwin .step'), function(){
     				
     					$(this).css("display","block");
     					
     				});
     				
     		$('.feedwin .feed .like').removeClass("selected");
     		$('.feedwin .feed .unlike').removeClass("selected");
     		$('.feedwin  .feed .step3').css("display","none");
     		$("#state").attr("value","");
     		
     		$("#message").attr("value","倾听您的建议(100字以内)");
     		$("#message").css("color","#909090");
     		$('#message').click(function(){
				$('#message').attr("value","");
				$("#message").css("color","#000000");
				$(this).unbind("click");
			});
		});
		
		$('#message').click(function(){
			$('#message').attr("value","");
			$("#message").css("color","#000000");
			$(this).unbind("click");
		});
		
		this.handles.click(function (){
			$('.feedwin').find(".feed").toggle();
			
			$.each($('.feedwin .step'), function(){
     				
     					$(this).css("display","block");
     					
     				});
     		
     		$('.feedwin .feed .like').removeClass("selected");
     		$('.feedwin .feed .unlike').removeClass("selected");	
     		$('.feedwin  .feed .step3').css("display","none");
     		$("#message").css("color","#909090");
     		$("#message").attr("value","倾听您的建议(100字以内)");
     		$("#state").attr("value","");
     		$('#message').click(function(){
				$('#message').attr("value","");
				$("#message").css("color","#000000");
				$(this).unbind("click");
			});
			$('.feedwin .feed  .input button').html("提交");
			$('.feedwin .feed  .input button').unbind("click");
			$('.feedwin .feed  .input button').click(function(){
				commitFeedbak();
			});
			
			
		});
		
		$('.feedwin .feed .like').click(function(){ 
			$("#state").attr("value",1);
			$(this).addClass("selected");
			$('.feedwin .feed .unlike').removeClass("selected");
			
		});
		
		$('.feedwin .feed .unlike').click(function(){ 
			$("#state").attr("value",0);
			$(this).addClass("selected");
			$('.feedwin .feed .like').removeClass("selected");
		});
		
	}	
}

function commitFeedbak(){
			if($("#state").attr("value")!="1" && $("#state").attr("value")!="0" ){ alert("请选择是否喜欢");return false;};
			
			var message="";
			if($("#message").attr("value")!="倾听您的建议(100字以内)"){message=$("#message").attr("value")};
			
			if(message.length>100){alert("您的输入已经超出限制，请修改！");return false;}
			
			$.ajax({
				url: '/service/feedback',
				type: 'POST',
				data: "keyword="+encodeURI($("#keyword").val())+"&state="+$("#state").val()+"&url="+encodeURI(window.location.href)+"&message="+encodeURI(message)
			});
			$('.feedwin .feed  .input button').html("正在提交");
			$('.feedwin .feed  .input button').unbind("click");
			
			$.each($('.feedwin .step'), function(){
					$(this).css("display","none");});
			$('.feedwin  .feed .step3').css("display","block");
     		$(".feedwin .feed").fadeOut(1000);
     		$('.feedwin .handle').fadeOut(1100);
}


function ltrim(s){ return s.replace( /^(\s*|　*)/, ""); } 
function rtrim(s){ return s.replace( /(\s*|　*)$/, ""); } 
function trim(s){ return ltrim(rtrim(s));} 

var library =0,result_count =0;

function dosearch(f){
	if(trim( f.keyword.value.replace(/[\/_]/g,' ') )==''){
		
		f.keyword.value='';	
		alert('关键词不能为空');
		f.keyword.focus();
		return false;
	}
	var from = f.from.value;
	var q = encodeURIComponent(f.keyword.value.replace(/[\/_]/g,' '));
	if(f.socondition && f.socondition[1].id=='outer' && f.socondition[1].checked){//全网搜索
		var url="/v?keyword="+q;
		if (from > 0){
			url+="&from="+from;
		}
	}else{//站内搜索
		var innersearchdomain = f.searchdomain.value;
		if(!innersearchdomain)innersearchdomain="http://www.soku.com";
		var btype = f.sbts;//看吧搜索选项
		if(f.searchType.value == "bar" && btype != undefined && btype.value != ""){
			q = q+"_sbt_"+btype.value;
		}
		var url= innersearchdomain+"/search_"+f.searchType.value+"/q_"+q;
	}
	
	location.href=url;
	return false;
	
}
//新soku搜索
function donewsearch(f){
	if(trim( f.keyword.value.replace(/[\/_]/g,' ') )==''){
		
		f.keyword.value='';	
		alert('关键词不能为空');
		f.keyword.focus();
		return false;
	}
	
	var q = encodeURIComponent(f.keyword.value.replace(/[\/_]/g,' '));
	
	var url="/v?keyword="+q;	
	
	location.href=url;
	return false;
	
}


function   checkTextArea(limit){
	obj=document.getElementById("message");
if   (obj.value.length> limit){
	alert("您的输入已经超出："+limit+" 限制");
	return   false;
}
	return   true;
} 




function search_show(pos,searchType,href){
    document.getElementById(pos+"SearchType").value=searchType;
    document.getElementById(pos+"Sel").style.display="none";
    document.getElementById(pos+"Slected").innerHTML=href.innerHTML;
    document.getElementById(pos+'q').focus();
    
    var s2 = document.getElementById('soswitch');
	var sl = document.getElementById('sorelated');
    var s0 = document.getElementById("searchextend0");
    if(s0 != undefined && searchType == "bar" && pos=="head"){
    	s0.style.display="block";
		if(sl) sl.style.display = 'none';
		if(s2) s2.style.display="none";
    }else if(s0 != undefined && pos=="head"){
    	s0.style.display="none";
		if(sl) sl.style.display = '';
		if(s2) s2.style.display = '';
    }
    var s1 = document.getElementById("searchextend1");
    if(s1 != undefined && (searchType == "video" || searchType == "playlist") && pos=="head"){
    	s1.style.display="block";
		if(sl) sl.style.display = 'none';
    }else if(s1 != undefined && pos=="head"){
    	s1.style.display="none";
		if(sl) sl.style.display = '';
    }
    
    var s2 = document.getElementById("searchextend2");
    if(s1 != undefined && searchType == "user" && pos=="head"){
    	s2.style.display="block";
		if(sl) sl.style.display = 'none';
    }else if(s1 != undefined && pos=="head"){
    	s2.style.display="none";
		if(sl) sl.style.display = '';
    }
    
	try{window.clearTimeout(timer);}catch(e){}
	return false;
}
function csbt(sbt,sbts){
	if(sbt.value == sbts.value){
		sbt.checked = false;
		sbts.value='bar';
	}else{
		sbts.value=sbt.value;
	}
}

function drop_mouseover(pos){
	try{window.clearTimeout(timer);}catch(e){}
}
function drop_mouseout(pos){
	var posSel=$(pos+"Sel").style.display;
	if(posSel=="block"){
		timer = setTimeout("drop_hide('"+pos+"')", 1000);
	}
}
function drop_hide(pos){
	$(pos+"Sel").style.display="none";
}

function showEpisode(i,total)
{
	for (var m =0;m<total;m++)
	{	
		if (i == m){
			$("index_"+m).addClassName("current");
			$("items"+m).addClassName("current").style.display="";
		}
		else{
			$("index_"+m).removeClassName("current");
			$("items"+m).removeClassName("current").style.display="none";
		}
	}
		
}


function clicklog(url,keyword){
	if(!url)return false;
	
	var img = new Image();
	var host = 'http://log.so.youku.com/click?';
	var source = 'soku';
	var logtype='soku_click';
	if(!keyword)keyword='';
	img.src = host+"log_type="+logtype+"&type=video&url="+encodeURIComponent(url)+"&keyword="+keyword+"&source="+source;
}

var _glogParam = {site:1};
function sokuClickStat(logParam, logClick){
		var logStr = [];
		var hasUrl = false;
		var logUrl;
		if(logParam.nodeType) {
			if($(logParam).attr("_log_type")) logStr.push("?type=" + $(logParam).attr("_log_type"));
			else return;
				
			if($(logParam).attr("_log_pos")) logStr.push("&pos=" + $(logParam).attr("_log_pos"));
			if($(logParam).attr("_log_ct")) logStr.push("&ct=" + $(logParam).attr("_log_ct"));		
			if($(logParam).attr("_log_directpos")) logStr.push("&directpos=" + $(logParam).attr("_log_directpos"));
			if($(logParam).attr("_log_url")) {
					//logStr.push("&url=" + encodeURIComponent($(logParam).attr("_log_url")));
					logUrl = encodeURIComponent($(logParam).attr("_log_url"));
					hasUrl = true;
				}
		} else {
			if(logParam._log_type) logStr.push("?type=" + logParam._log_type);
			else return;
				
			if(logParam._log_pos) logStr.push("&pos=" + logParam._log_pos);
			if(logParam._log_ct) logStr.push("&ct=" + logParam._log_ct);		
			if(logParam._log_directpos) logStr.push("&directpos=" + logParam._log_directpos);
			if(logParam._log_url) {
					//logStr.push("&url=" + encodeURIComponent(logParam._log_url));
					logUrl = encodeURIComponent(logParam._log_url);
					hasUrl = true;
				}
		}

		if(!hasUrl && logParam.href) {
			//logStr.push("&url=" + encodeURIComponent(logParam.href));
			logUrl = encodeURIComponent(logParam.href);
		}
		
		if(logUrl && !logUrl.match(/^javascript(.*)/i)) {
			logStr.push("&url=" + logUrl);
		}
		if(logParam.keyword) {
			logStr.push("&keyword=" + logParam.keyword);
		} else if(_glogParam.keyword) {
			logStr.push("&keyword=" + _glogParam.keyword);
		}
		if(_glogParam.site) logStr.push("&site=" + _glogParam.site);
		if(_glogParam.curpage) logStr.push("&curpage=" + _glogParam.curpage);
		
        var rand = Math.round(Math.random() * 2147483647);
        logStr.push("&rand" + rand); 
		var href="http://lstat.youku.com/sokuClick.php" + logStr.join("");
        var s = new Image() ;
		
        s.src = href ;

        s.onload=function() { return; }
        
        if(logClick) {
        	clicklog(logUrl, _glogParam.keyword);
        }

}

/* soku弹出框 */
var winList = null;
var winVideo = null;

function openVideo(listurl,url){
	var maxWidth = window.outerWidth || findsize('width');
	var maxHeight = window.outerHeight - 165 || findsize('height');
	var winX = window.screenLeft || window.screenX || 0;
	var winY= window.screenTop - 100 || window.screenY;

	winX = (winX - 8) > 0? winX - 8 : 0 ;
	var panelWidth = 165;
	var panelHeight = maxHeight;
	if(document.all) {
		panelHeight = panelHeight + 125;
	}
		
	var videofeature = "left="+(winX + 165)+", top="+winY+", location=yes, scrollbars=yes, resizable=yes, toolbar=yes, menubar=yes, directories=yes, status=yes, copyhistory=no, width="+(maxWidth-180)+", height="+panelHeight;
	
	if (winList==null || winList=="undefined")
	{
		winList = document.createElement('iframe'); //动态创建框架
		winList.id = 'winList';

	}
	if (listurl)
	{
		
		winList.setAttribute('frameborder','no');
		winList.setAttribute('border','0');
		winList.setAttribute('scrolling','0');
		document.body.appendChild(winList);
		document.body.style.marginLeft = '165px'; 
		winList.src=listurl;
	}

	winVideo = window.open(url, 'soVideo', videofeature);
	winVideo.focus();
}


function findsize(type){ 
	var winWidth = 0; 
	var winHeight = 0; 
   //获取窗口宽度 
   if (window.innerWidth) 
     winWidth = window.innerWidth; 
   else if ((document.body) && (document.body.clientWidth)) 
     winWidth = document.body.clientWidth; 
   //获取窗口高度 
   if (window.innerHeight) 
     winHeight = window.innerHeight; 
   else if ((document.body) && (document.body.clientHeight)) 
     winHeight = document.body.clientHeight; 
   //通过深入Document内部对body进行检测，获取窗口大小 
   if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth){ 
     winHeight = document.documentElement.clientHeight; 
     winWidth = document.documentElement.clientWidth; 
   } 

  if(type=="width"){
    return winWidth;
  }else if(type=="height"){
    return winHeight;
  }
}

function focusWin()
{
	if(!document.all)
		this.blur();
	try{
		winVideo.focus();
	}catch(e){}
	
	
}
String.prototype.GetValue= function(para) {  
  var reg = new RegExp("(^|&)"+ para +"=([^&]*)(&|$)");  
  var r = this.substr(this.indexOf("\?")+1).match(reg);  
  if (r!=null) return unescape(r[2]); return null;  
}  

function sokuHz(){
		var url = location.href;

		var cp = url.GetValue("cp");
		var cpp = url.GetValue("cpp");
		if (null != cp && cp != "" && 'undefined' != cp
			&& null != cpp && cpp != "" && 'undefined' != cpp)
		{
			var href="http://hz.youku.com/red/redir.php?tp=1&cp="+cp+"&cpp="+cpp;

			var rand = Math.round(Math.random() * 2147483647);

			href += "&rand" + rand ; 

			var s = new Image() ;

			s.src = href ;

			s.onload=function() { return; }
		}
}

//读写cookie
    function readCookie(name){
      var cookieValue = "";
      var search = name + "=";
      if(document.cookie.length > 0){
        offset = document.cookie.indexOf(search);
        if (offset != -1){
          offset += search.length;
          end = document.cookie.indexOf(";", offset);
          if (end == -1) end = document.cookie.length;
          cookieValue = unescape(document.cookie.substring(offset, end))
        }
      }
      return cookieValue;
    }

    function writeCookie(name, value, hours){
      var expire = "";
      if(hours != null){
        expire = new Date((new Date()).getTime() + hours * 3600000);
        expire = "; expires=" + expire.toGMTString();
      }
      document.cookie = name + "=" + escape(value) + expire;
    }
var VideoWall = {
	init: function(dataurl){
		this.queue = [];
		this.points = {
		'grade1':[{x:740,y:125,w:16,h:10,f:0.2},{x:51,y:304,w:16,h:10,f:0.2},{x:718,y:239,w:16,h:10,f:0.2},{x:50,y:179,w:16,h:10,f:0.2},{x:25,y:522,w:16,h:10,f:0.2},{x:722,y:525,w:16,h:10,f:0.2}],
		'grade2':[{x:557,y:120,w:32,h:20,f:0.4},{x:722,y:196,w:32,h:20,f:0.4},{x:48,y:350,w:32,h:20,f:0.4},{x:500,y:501,w:32,h:20,f:0.4},{x:244,y:508,w:32,h:20,f:0.4},{x:12,y:206,w:32,h:20,f:0.4},{x:122,y:142,w:32,h:20,f:0.4},{x:715,y:136,w:32,h:20,f:0.4},{x:744,y:375,w:32,h:20,f:0.4},{x:4,y:371,w:25,h:15,f:0.4},{x:389,y:57,w:35,h:21,f:0.4},{x:290,y:16,w:20,h:12,f:0.4},{x:509,y:531,w:32,h:20,f:0.4},{x:16,y:449,w:32,h:20,f:0.4},{x:700,y:285,w:32,h:20,f:0.4},{x:46,y:457,w:32,h:20,f:0.4},{x:430,y:534,w:32,h:20,f:0.4},{x:722,y:511,w:32,h:20,f:0.4},{x:228,y:129,w:32,h:20,f:0.4},{x:72,y:213,w:25,h:15,f:0.4}],
		'grade3':[{x:141,y:498,w:48,h:30,f:0.6},{x:111,y:107,w:48,h:30,f:0.6},{x:202,y:511,w:48,h:30,f:0.6},{x:397,y:500,w:48,h:30,f:0.6},{x:34,y:124,w:48,h:30,f:0.6},{x:701,y:381,w:48,h:30,f:0.6},{x:446,y:20,w:16,h:10,f:0.6},{x:189,y:525,w:28,h:18,f:0.6},{x:8,y:331,w:48,h:30,f:0.6},{x:33,y:375,w:48,h:30,f:0.6},{x:694,y:201,w:48,h:30,f:0.6},{x:712,y:347,w:48,h:30,f:0.6},{x:340,y:68,w:16,h:10,f:0.6},{x:731,y:284,w:48,h:30,f:0.6},{x:26,y:479,w:48,h:30,f:0.6},{x:422,y:35,w:25,h:16,f:0.6},{x:36,y:310,w:48,h:30,f:0.6},{x:621,y:509,w:48,h:30,f:0.6},{x:295,y:124,w:48,h:30,f:0.6},{x:462,y:125,w:48,h:30,f:0.6},{x:677,y:507,w:48,h:30,f:0.6}],
		'grade4':[{x:63,y:119,w:64,h:40,f:0.8},{x:156,y:115,w:64,h:40,f:0.8},{x:262,y:512,w:64,h:40,f:0.8},{x:445,y:507,w:64,h:40,f:0.8},{x:386,y:103,w:64,h:40,f:0.8},{x:497,y:109,w:64,h:40,f:0.8},{x:245,y:107,w:64,h:40,f:0.8},{x:540,y:501,w:64,h:40,f:0.8},{x:591,y:110,w:64,h:40,f:0.8},{x:333,y:504,w:64,h:40,f:0.8},{x:17,y:222,w:64,h:40,f:0.8},{x:19,y:161,w:64,h:40,f:0.8},{x:707,y:309,w:64,h:40,f:0.8},{x:654,y:116,w:64,h:40,f:0.8},{x:324,y:48,w:26,h:16,f:0.8},{x:698,y:461,w:64,h:40,f:0.8},{x:710,y:165,w:64,h:40,f:0.8}],
		'grade5':[{x:4,y:258,w:80,h:50,f:1},{x:52,y:488,w:80,h:50,f:1},{x:604,y:507,w:80,h:50,f:1},{x:331,y:96,w:80,h:50,f:1},{x:697,y:234,w:80,h:50,f:1},{x:308,y:37,w:22,h:13,f:1},{x:5,y:402,w:80,h:50,f:1},{x:695,y:408,w:80,h:50,f:1},{x:116,y:512,w:80,h:50,f:1},{x:355,y:77,w:39,h:24,f:1}]
		};
		this.readystate = false;
		this.readycount = 0;
		this.dom = $('#wall');		
		this.nodes = [];
		this.mouselock = true;
		if(this.dom.length == 0){ return; }
		var _this = this;
		$.getJSON(dataurl, function(data){
			if(!data.data){ return; }
			var data = data.data;
			for(var i=0, len=data.length; i<len; i++){
				_this.queue.push(data[i]);
			}
			_this.bind();
		});
	},
	
	bind: function(){
		var _this = this;
		var glens = [0];
		for(var key in this.points){
			glens.push(this.points[key].length);	
		}
		var group = 5;
		var posindex = 0;
		var count = this.points['grade5'].length;
		for(var i=0, len=this.queue.length; i<len; i++){
			if (i+1>count){
				group--; 
				count+=glens[group]; 
				posindex=0;
			}
			if(!this.points['grade'+group]){ return; }
			var pos =this.points['grade'+group][posindex]; 
			if(!pos){ return; }
			var item = this.queue[i];
			var img = $('<img src="'+ item.pic +'" />').css('opacity', 0);
			var linkto = $('<a onclick="sokuClickStat(this);" href="/v?keyword='+ encodeURI(item.name) + '" target="_blank" _log_type="5" _log_pos="2"></a>');
			var name = $('<span class="name"><span class="text">'+ item.name +'</span><span class="bg"></span></span>')
			linkto.append(img).append(name);
			var node = $('<div class="node grade'+ group +'"></div>').
						attr({
						'index': i,
						'w': pos.w,
						'h': pos.h,
						'f': pos.f,
						'X': pos.x,
						'Y': pos.y,
						'z': group
						}).	
						css({
						'display':'none',	
						'width':0, 
						'height': 0,
						'top': pos.y+(pos.h/2),
						'left': pos.x+(pos.w/2), 
						'zIndex': group
						}).
						append(linkto).
						appendTo(this.dom);
			this.bindmouse(node);			
			img.load(function(){ _this.bubble($(this).parent().parent()); });
			this.nodes.push(node);			
			posindex++;
		}
		
		
	},
	
	bubble: function(node){
		var t = 500 + 1000*Math.random();
		var w = parseInt(node.attr('w'));
		var h = parseInt(node.attr('h'));
		var x = parseInt(node.attr('x'));
		var y = parseInt(node.attr('y'));
		var f = parseFloat(node.attr('f'));
		var img = $(node).find('img');
		var _this = this;
		node.css('visibility', 'visible').
		animate({
		'width': w,
		'height': h,
		'top': y,
		'left': x
		}, t, 'easeInExpo', function(){
			if(!_this.readystate){
				_this.readycount ++;
				if(_this.readycount >= _this.nodes.length){
					_this.readystate = true;
					_this.mouselock = false;
				}	
			}
		});
		img.animate({
		'opacity': f
		},t, 'easeInExpo');
		
	},
	
	bindmouse: function(node){
		var _this = this;
		node.
		mouseenter(function(){
			if(_this.mouselock){ return; }
			var target = $(this);
			var w = parseInt(target.attr('w'));
			var h = parseInt(target.attr('h'));
			var x = parseInt(target.attr('x'));
			var y = parseInt(target.attr('y'));
			var f = parseFloat(target.attr('f'));
			var z = parseInt(target.attr('z'));
			target.stop().
			css('zIndex', 100).
			animate({
			'width': 128,
			'height':80,
			'top': y - ((80-h-6)/2),
			'left':x - ((128-w-6)/2),
			'borderWidth': 3
			}, 200, 'easeInExpo', function(){ 
				 target.addClass('nodemax');
			});
			var img = target.find('img');
			img.stop().
			animate({
			'opacity': 1	
			}, 200, 'easeInExpo');
			
			return false;	
		}).
		mouseleave(function(){
			if(_this.mouselock){ return; }
			var target = $(this);
			var w = parseInt(target.attr('w'));
			var h = parseInt(target.attr('h'));
			var x = parseInt(target.attr('x'));
			var y = parseInt(target.attr('y'));
			var f = parseFloat(target.attr('f'));
			var z = parseInt(target.attr('z'));
			target.removeClass('nodemax');
			target.stop().
			animate({
			'width': w,
			'height':h,
			'top': y,
			'left':x,
			'borderWidth': 0
			}, 100, '', function(){
				target.css('zIndex', z);
			});
			var img = target.find('img');
			img.stop().
			animate({
			'opacity': f	
			}, 100, '');
			return false;
		});
	},
	
	show: function(){
		this.dom.fadeIn(300);
	},
	
	hide: function(){
		this.dom.fadeOut(300);	
	},
	
	shake: function(){
		
	}
	
}


jQuery.easing['jswing'] = jQuery.easing['swing'];

jQuery.extend( jQuery.easing,
{
	def: 'easeOutQuad',
	swing: function (x, t, b, c, d) {
		//alert(jQuery.easing.default);
		return jQuery.easing[jQuery.easing.def](x, t, b, c, d);
	},
	easeInQuad: function (x, t, b, c, d) {
		return c*(t/=d)*t + b;
	},
	easeOutQuad: function (x, t, b, c, d) {
		return -c *(t/=d)*(t-2) + b;
	},
	easeInOutQuad: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	},
	easeInCubic: function (x, t, b, c, d) {
		return c*(t/=d)*t*t + b;
	},
	easeOutCubic: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t + 1) + b;
	},
	easeInOutCubic: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	},
	easeInQuart: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t + b;
	},
	easeOutQuart: function (x, t, b, c, d) {
		return -c * ((t=t/d-1)*t*t*t - 1) + b;
	},
	easeInOutQuart: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
		return -c/2 * ((t-=2)*t*t*t - 2) + b;
	},
	easeInQuint: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t*t + b;
	},
	easeOutQuint: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t*t*t + 1) + b;
	},
	easeInOutQuint: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
		return c/2*((t-=2)*t*t*t*t + 2) + b;
	},
	easeInSine: function (x, t, b, c, d) {
		return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
	},
	easeOutSine: function (x, t, b, c, d) {
		return c * Math.sin(t/d * (Math.PI/2)) + b;
	},
	easeInOutSine: function (x, t, b, c, d) {
		return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
	},
	easeInExpo: function (x, t, b, c, d) {
		return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
	},
	easeOutExpo: function (x, t, b, c, d) {
		return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
	},
	easeInOutExpo: function (x, t, b, c, d) {
		if (t==0) return b;
		if (t==d) return b+c;
		if ((t/=d/2) < 1) return c/2 * Math.pow(2, 10 * (t - 1)) + b;
		return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
	},
	easeInCirc: function (x, t, b, c, d) {
		return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
	},
	easeOutCirc: function (x, t, b, c, d) {
		return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
	},
	easeInOutCirc: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
		return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
	},
	easeInElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
	},
	easeOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
	},
	easeInOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d/2)==2) return b+c;  if (!p) p=d*(.3*1.5);
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		if (t < 1) return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
		return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
	},
	easeInBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*(t/=d)*t*((s+1)*t - s) + b;
	},
	easeOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	},
	easeInOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158; 
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
	},
	easeInBounce: function (x, t, b, c, d) {
		return c - jQuery.easing.easeOutBounce (x, d-t, 0, c, d) + b;
	},
	easeOutBounce: function (x, t, b, c, d) {
		if ((t/=d) < (1/2.75)) {
			return c*(7.5625*t*t) + b;
		} else if (t < (2/2.75)) {
			return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
		} else if (t < (2.5/2.75)) {
			return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
		} else {
			return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
		}
	},
	easeInOutBounce: function (x, t, b, c, d) {
		if (t < d/2) return jQuery.easing.easeInBounce (x, t*2, 0, c, d) * .5 + b;
		return jQuery.easing.easeOutBounce (x, t*2-d, 0, c, d) * .5 + c*.5 + b;
	}
});
/*
 * Lazy Load - jQuery plugin for lazy loading images
 *
 * Copyright (c) 2007-2009 Mika Tuupola
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *   http://www.appelsiini.net/projects/lazyload
 *
 * Version:  1.5.0
 *
 */
(function($) {

    $.fn.lazyload = function(options) {
        var settings = {
            threshold    : 0,
            failurelimit : 0,
            event        : "scroll",
            effect       : "show",
            container    : window
        };
                
        if(options) {
            $.extend(settings, options);
        }

        /* Fire one scroll event per scroll. Not one scroll event per image. */
        var elements = this;
        if ("scroll" == settings.event) {
            $(settings.container).bind("scroll", function(event) {
                
                var counter = 0;
                elements.each(function() {
                    if ($.abovethetop(this, settings) ||
                        $.leftofbegin(this, settings)) {
                            /* Nothing. */
                    } else if (!$.belowthefold(this, settings) &&
                        !$.rightoffold(this, settings)) {
                            $(this).trigger("appear");
                    } else {
                        if (counter++ > settings.failurelimit) {
                            return false;
                        }
                    }
                });
                /* Remove image from array so it is not looped next time. */
                var temp = $.grep(elements, function(element) {
                    return !element.loaded;
                });
                elements = $(temp);
            });
        }
        
        this.each(function() {
            var self = this;
            
            /* Save original only if it is not defined in HTML. */
            if (undefined == $(self).attr("original")) {
                $(self).attr("original", $(self).attr("src"));     
            }

            if ("scroll" != settings.event || 
                    undefined == $(self).attr("src") || 
                    settings.placeholder == $(self).attr("src") || 
                    ($.abovethetop(self, settings) ||
                     $.leftofbegin(self, settings) || 
                     $.belowthefold(self, settings) || 
                     $.rightoffold(self, settings) )) {
                        
                if (settings.placeholder) {
                    $(self).attr("src", settings.placeholder);      
                } else {
                    $(self).removeAttr("src");
                }
                self.loaded = false;
            } else {
                self.loaded = true;
            }
            
            /* When appear is triggered load original image. */
            $(self).one("appear", function() {
                if (!this.loaded) {
                    $("<img />")
                        .bind("load", function() {
                            $(self)
                                .hide()
                                .attr("src", $(self).attr("original"))
                                [settings.effect](settings.effectspeed);
                            self.loaded = true;
                        })
                        .attr("src", $(self).attr("original"));
                };
            });

            /* When wanted event is triggered load original image */
            /* by triggering appear.                              */
            if ("scroll" != settings.event) {
                $(self).bind(settings.event, function(event) {
                    if (!self.loaded) {
                        $(self).trigger("appear");
                    }
                });
            }
        });
        
        /* Force initial check if images should appear. */
        $(settings.container).trigger(settings.event);
        
        return this;

    };

    /* Convenience methods in jQuery namespace.           */
    /* Use as  $.belowthefold(element, {threshold : 100, container : window}) */

    $.belowthefold = function(element, settings) {
        if (settings.container === undefined || settings.container === window) {
            var fold = $(window).height() + $(window).scrollTop();
        } else {
            var fold = $(settings.container).offset().top + $(settings.container).height();
        }
        return fold <= $(element).offset().top - settings.threshold;
    };
    
    $.rightoffold = function(element, settings) {
        if (settings.container === undefined || settings.container === window) {
            var fold = $(window).width() + $(window).scrollLeft();
        } else {
            var fold = $(settings.container).offset().left + $(settings.container).width();
        }
        return fold <= $(element).offset().left - settings.threshold;
    };
        
    $.abovethetop = function(element, settings) {
        if (settings.container === undefined || settings.container === window) {
            var fold = $(window).scrollTop();
        } else {
            var fold = $(settings.container).offset().top;
        }
        return fold >= $(element).offset().top + settings.threshold  + $(element).height();
    };
    
    $.leftofbegin = function(element, settings) {
        if (settings.container === undefined || settings.container === window) {
            var fold = $(window).scrollLeft();
        } else {
            var fold = $(settings.container).offset().left;
        }
        return fold >= $(element).offset().left + settings.threshold + $(element).width();
    };
    /* Custom selectors for your convenience.   */
    /* Use as $("img:below-the-fold").something() */

    $.extend($.expr[':'], {
        "below-the-fold" : "$.belowthefold(a, {threshold : 0, container: window})",
        "above-the-fold" : "!$.belowthefold(a, {threshold : 0, container: window})",
        "right-of-fold"  : "$.rightoffold(a, {threshold : 0, container: window})",
        "left-of-fold"   : "!$.rightoffold(a, {threshold : 0, container: window})"
    });
    
})(jQuery);