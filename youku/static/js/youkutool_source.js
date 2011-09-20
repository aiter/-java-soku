function ltrim(s){ return s.replace( /^(\s*|　*)/, ""); } 
function rtrim(s){ return s.replace( /(\s*|　*)$/, ""); } 
function trim(s){ return ltrim(rtrim(s));} 
/**
 * form表单检查
 */
 
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



function dosearch(f){
	if(trim( f.q.value.replace(/[\/]/g,' ') )==''){
		f.q.value='';	
		alert('关键词不能为空');
		f.q.focus();
		return false;
	}
	var q = encodeURIComponent(f.q.value.replace(/[\/]/g,' '));//'
	
	if(f.socondition && f.socondition[1].id=='outer' && f.socondition[1].checked){//全网搜索
		var url="http://www.soku.com/v?keyword="+q;
	}else{//站内搜索
		var innersearchdomain = (f.searchdomain != undefined && f.searchdomain.value != undefined) ? f.searchdomain.value : '';
		var btype = f.sbts;//看吧搜索选项
		if(f.searchType.value == "bar" && btype != undefined && btype.value != ""){
			q = q+"_sbt_"+btype.value;
		}
		var url= innersearchdomain+"/search_"+f.searchType.value+"/q_"+q;
	}
	if(typeof(search_prompt_flag) != 'undefined' && search_prompt_flag){//使用下拉提示统计代码
		(new Image()).src="http://lstat.youku.com/sokuHintKeyword.php?keyword="+q;
	}
	
	location.href=url;
	return false;
	
}


function search_show(pos,searchType,href){
    document.getElementById(pos+"SearchType").value=searchType;
    changeSearchType("/search_" + searchType + "/q_");
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


var _glogParam = {site:2};
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


var KUBOX = {
	init: function(){
		this.textfiled = $('.header .sotxt');
		var page = $('.page_index');
		if(page.length !=0 ){
			this.textfiled = page.find('.sotxt');
		}
		this.kubox = $('.kubox');
		if(this.textfiled.length * this.kubox.length == 0){ return; }
		this.bind();	
	},
	bind: function(){
		var _this = this;
		this.textfiled.focus(function(){
			if(typeof(VideoWall) != 'undefined'){ if(VideoWall){ VideoWall.hide(); }}
			if($(this).val() != ''){ _this._show(); }
		});
		this.textfiled.keyup(function(e){
			if($(this).val() == ''){ _this._hide(); }
			else{ _this._show();}
			if(e.keyCode == 27){_this._hide();}
		});
		this.textfiled.blur(function(){
			_this._hide();
			if(typeof(VideoWall) != 'undefined'){ if(VideoWall){ VideoWall.show();}}
		});
	},
	_show: function(){ this.kubox.slideDown(200);},
	_hide: function(){ this.kubox.slideUp(200); }	
} 
var SoOption = {
	init: function(){
		var sooption = $('.sotool .sooption');
		if(!sooption.length){ return; }
		this.curoption = sooption.find('.current');
		this.curtext =$.trim( this.curoption.text());
		this.options = sooption.find('.options');
		this.optionhandles = this.options.find('a');			
		this.bind();
	},
	bind: function(){
		var _this = this;
		this.optionhandles.each(function(){
			var option = $(this);
			option.click(function(){
				var text = $.trim($(this).text());
				if(text != _this.curtext){
					_this.curoption.html(text);
					_this.curtext = text; 
				}
				_this.hide();
				return false;
			});	
		});
		this.curoption.click(function(){
			_this.toggle();
			return false;	
		});
		$(document).bind('click', function(){
			_this.hide();
		});
	},
	show: function(){
		this.options.show();	
	},
	hide: function(){
		this.options.hide();
	},
	toggle: function(){
		this.options.toggle();
	}	
}
var Filter = {
	minwidth: 180 + 960,
	status: $('.filter .handle a').attr("status"),
	init: function(){
		this.handle = $('.filter .handle a');
		this.panel = $('.filter .panel');
		this.conditions = $('.filter .item');
		if(this.conditions.length == 0){ return; }
		this.bind();
	},
	getwinwidth: function(){
		return  $(window).width();
	},
	setsize: function(){
		$(document.body).css('width', this.minwidth); 
	},
	resize: function(){
		$(document.body).css('width', 'auto');
	},
	checksize: function(){
		if(this.status == 'show'){
			if(this.getwinwidth() >= this.minwidth){
				this.resize();
			}else{
				this.setsize();	
			}
		}
	},
	show: function(){
		this.handle.html('&gt;&gt; 隐藏筛选');
		this.handle.show();
		this.handle.attr("status","show");
		this.panel.show();
		this.status = 'show';
		if(this.getwinwidth() < this.minwidth){
			this.setsize();
		}
	},
	hide: function(){
		this.handle.html('&lt;&lt; 筛选视频');
		this.handle.show();
		this.handle.attr("status","hide");
		this.panel.hide();
		this.status = 'hide';
		this.resize();
	},
	bind: function(){
		var _this = this;
		if(this.handle.length){
			var filterCookie=readCookie("filterforyouku");
			
			if(filterCookie.length>0){
				if(filterCookie=="show"){
					this.handle.show();
					this.show();
				}else{
					
				}	
			}else{
				if(this.getwinwidth() >= this.minwidth){
					this.handle.show();
					this.show();				
				}	
			}				
			
			this.handle.click(function(){
				var handle = $(this);
				if(handle.attr("status")== 'hide'){
					_this.show();
				}else{
					_this.hide();
				}
				writeCookie("filterforyouku",_this.status,24*30*12);
				
				return false;
			});
			$(window).bind('resize', function(){
				_this.checksize();
			});
		}
		
	}	
}


var Selector = {
	init: function(orderby){
		this.initHTML(orderby);
		this.selectors = $('.selector');
		if(this.selectors.length == 0){ return; }
		this.bind();
	},
	initHTML:function(orderby){

		$('.orderby .selector .options').attr('index',orderby);
		var optionitems	= $('.orderby .selector .options').find('li');
		$.each(optionitems, function(){
			if($(this).attr('index') == orderby){ 
				$(this).addClass('current');
				$('.orderby .selector .selected').html($(this).find('a').html());
			 }
			else{
				$(this).removeClass('current');
			}
		})
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
						
						//alert("the selected url:"+optioncurrent.find('a').attr('href'));
						window.location.href=optioncurrent.find('a').attr('href');
					}
					return false;	
				});
			});
		});
		
		$(document).bind('click', function(){
			$(this).find('.options').hide();
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
				writeCookie("youkuviewby",_this.by.attr("viewby"),24*30*12);
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
		this.selectors = $('.gotoplay .source');
		if(this.selectors.length == 0){ return; }
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
					var thishtml = $(this).html();
					var chckhtml = check.html();
					check.html(thishtml);
					$(this).html(chckhtml);
					other.hide();
					return false;	
				});
			});
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

var Feedwin = {
	init: function(){
		var layer = $('.feedwin');
		var ua = navigator.userAgent.toLowerCase();
		var isIOS = ua.match(/ipad|iphone|ipod|itouch/i);
		var winheight = $(document.body).attr('scrollHeight');
		layer.show();
		if(isIOS){
			layer.css('top', winheight - 280);
		}	
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
				data: "keyword="+encodeURI($("#keyword").val())+"&state="+$("#state").val()+"&source=1&url="+encodeURI(window.location.href)+"&message="+encodeURI(message)
			});
			$('.feedwin .feed  .input button').html("正在提交");
			$('.feedwin .feed  .input button').unbind("click");
			
			$.each($('.feedwin .step'), function(){
					$(this).css("display","none");});
			$('.feedwin  .feed .step3').css("display","block");
     		$(".feedwin .feed").fadeOut(1000);
     		
     		//$('.feedwin .handle').fadeOut(1100);
}

SoOption.init();
//KUBOX.init();
Filter.init();
ViewBy.init();
//Selector.init(orderby);
SpeedSelector.init();
PanelExpand.init();
PartPop.init();
FeedBack.init();
Feedwin.init();

