function ltrim(a){return a.replace(/^(\s*|\u3000*)/,"")}function rtrim(a){return a.replace(/(\s*|\u3000*)$/,"")}function trim(a){return ltrim(rtrim(a))}function readCookie(a){var b="";a+="=";if(document.cookie.length>0&&(offset=document.cookie.indexOf(a),offset!=-1)){offset+=a.length;end=document.cookie.indexOf(";",offset);if(end==-1)end=document.cookie.length;b=unescape(document.cookie.substring(offset,end))}return b}
function writeCookie(a,b,c){var d="";c!=null&&(d=new Date((new Date).getTime()+c*36E5),d="; expires="+d.toGMTString());document.cookie=a+"="+escape(b)+d}
function dosearch(a){if(trim(a.q.value.replace(/[\/]/g," "))=="")return a.q.value="",alert("\u5173\u952e\u8bcd\u4e0d\u80fd\u4e3a\u7a7a"),a.q.focus(),!1;var b=encodeURIComponent(a.q.value.replace(/[\/]/g," "));if(a.socondition&&a.socondition[1].id=="outer"&&a.socondition[1].checked)a="http://www.soku.com/v?keyword="+b;else{var c=a.searchdomain!=void 0&&a.searchdomain.value!=void 0?a.searchdomain.value:"",d=a.sbts;a.searchType.value=="bar"&&d!=void 0&&d.value!=""&&(b=b+"_sbt_"+d.value);a=c+"/search_"+
a.searchType.value+"/q_"+b}if(typeof search_prompt_flag!="undefined"&&search_prompt_flag)(new Image).src="http://lstat.youku.com/sokuHintKeyword.php?keyword="+b;location.href=a;return!1}
function search_show(a,b,c){document.getElementById(a+"SearchType").value=b;changeSearchType("/search_"+b+"/q_");document.getElementById(a+"Sel").style.display="none";document.getElementById(a+"Slected").innerHTML=c.innerHTML;document.getElementById(a+"q").focus();var c=document.getElementById("soswitch"),d=document.getElementById("sorelated"),e=document.getElementById("searchextend0");if(e!=void 0&&b=="bar"&&a=="head"){e.style.display="block";if(d)d.style.display="none";if(c)c.style.display="none"}else if(e!=
void 0&&a=="head"){e.style.display="none";if(d)d.style.display="";if(c)c.style.display=""}e=document.getElementById("searchextend1");if(e!=void 0&&(b=="video"||b=="playlist")&&a=="head"){if(e.style.display="block",d)d.style.display="none"}else if(e!=void 0&&a=="head"&&(e.style.display="none",d))d.style.display="";c=document.getElementById("searchextend2");if(e!=void 0&&b=="user"&&a=="head"){if(c.style.display="block",d)d.style.display="none"}else if(e!=void 0&&a=="head"&&(c.style.display="none",d))d.style.display=
"";try{window.clearTimeout(timer)}catch(f){}return!1}var _glogParam={site:2};
function sokuClickStat(a,b){var c=[],d=!1,e;if(a.nodeType){if($(a).attr("_log_type"))c.push("?type="+$(a).attr("_log_type"));else return;$(a).attr("_log_pos")&&c.push("&pos="+$(a).attr("_log_pos"));$(a).attr("_log_ct")&&c.push("&ct="+$(a).attr("_log_ct"));$(a).attr("_log_directpos")&&c.push("&directpos="+$(a).attr("_log_directpos"));$(a).attr("_log_url")&&(e=encodeURIComponent($(a).attr("_log_url")),d=!0)}else{if(a._log_type)c.push("?type="+a._log_type);else return;a._log_pos&&c.push("&pos="+a._log_pos);
a._log_ct&&c.push("&ct="+a._log_ct);a._log_directpos&&c.push("&directpos="+a._log_directpos);a._log_url&&(e=encodeURIComponent(a._log_url),d=!0)}!d&&a.href&&(e=encodeURIComponent(a.href));e&&!e.match(/^javascript(.*)/i)&&c.push("&url="+e);a.keyword?c.push("&keyword="+a.keyword):_glogParam.keyword&&c.push("&keyword="+_glogParam.keyword);_glogParam.site&&c.push("&site="+_glogParam.site);_glogParam.curpage&&c.push("&curpage="+_glogParam.curpage);c.push("&rand"+Math.round(Math.random()*2147483647));c=
"http://lstat.youku.com/sokuClick.php"+c.join("");d=new Image;d.src=c;d.onload=function(){};b&&clicklog(e,_glogParam.keyword)}
var KUBOX={init:function(){this.textfiled=$(".header .sotxt");var a=$(".page_index");if(a.length!=0)this.textfiled=a.find(".sotxt");this.kubox=$(".kubox");this.textfiled.length*this.kubox.length!=0&&this.bind()},bind:function(){var a=this;this.textfiled.focus(function(){typeof VideoWall!="undefined"&&VideoWall&&VideoWall.hide();$(this).val()!=""&&a._show()});this.textfiled.keyup(function(b){$(this).val()==""?a._hide():a._show();b.keyCode==27&&a._hide()});this.textfiled.blur(function(){a._hide();typeof VideoWall!=
"undefined"&&VideoWall&&VideoWall.show()})},_show:function(){this.kubox.slideDown(200)},_hide:function(){this.kubox.slideUp(200)}},SoOption={init:function(){var a=$(".sotool .sooption");if(a.length)this.curoption=a.find(".current"),this.curtext=$.trim(this.curoption.text()),this.options=a.find(".options"),this.optionhandles=this.options.find("a"),this.bind()},bind:function(){var a=this;this.optionhandles.each(function(){$(this).click(function(){var b=$.trim($(this).text());if(b!=a.curtext)a.curoption.html(b),
a.curtext=b;a.hide();return!1})});this.curoption.click(function(){a.toggle();return!1});$(document).bind("click",function(){a.hide()})},show:function(){this.options.show()},hide:function(){this.options.hide()},toggle:function(){this.options.toggle()}},Filter={minwidth:1140,status:$(".filter .handle a").attr("status"),init:function(){this.handle=$(".filter .handle a");this.panel=$(".filter .panel");this.conditions=$(".filter .item");this.conditions.length!=0&&this.bind()},getwinwidth:function(){return $(window).width()},
setsize:function(){$(document.body).css("width",this.minwidth)},resize:function(){$(document.body).css("width","auto")},checksize:function(){this.status=="show"&&(this.getwinwidth()>=this.minwidth?this.resize():this.setsize())},show:function(){this.handle.html("&gt;&gt; \u9690\u85cf\u7b5b\u9009");this.handle.show();this.handle.attr("status","show");this.panel.show();this.status="show";this.getwinwidth()<this.minwidth&&this.setsize()},hide:function(){this.handle.html("&lt;&lt; \u7b5b\u9009\u89c6\u9891");
this.handle.show();this.handle.attr("status","hide");this.panel.hide();this.status="hide";this.resize()},bind:function(){var a=this;if(this.handle.length){var b=readCookie("filterforyouku");b.length>0?b=="show"&&(this.handle.show(),this.show()):this.getwinwidth()>=this.minwidth&&(this.handle.show(),this.show());this.handle.click(function(){$(this).attr("status")=="hide"?a.show():a.hide();writeCookie("filterforyouku",a.status,8640);return!1});$(window).bind("resize",function(){a.checksize()})}}},Selector=
{init:function(a){this.initHTML(a);this.selectors=$(".selector");this.selectors.length!=0&&this.bind()},initHTML:function(a){$(".orderby .selector .options").attr("index",a);var b=$(".orderby .selector .options").find("li");$.each(b,function(){$(this).attr("index")==a?($(this).addClass("current"),$(".orderby .selector .selected").html($(this).find("a").html())):$(this).removeClass("current")})},bind:function(){$.each(this.selectors,function(){var a=$(this).find(".selected"),b=$(this).find(".options"),
c=b.attr("index"),d=$(this).find(".options").find("li"),e=b.find("[index="+c+"]");a.click(function(){b.toggle();return!1});$.each(d,function(){$(this).attr("index")==c&&$(this).addClass("current");$(this).find("a").click(function(){var d=$(this).parent(),g=d.attr("index");if(g!=c)a.html($(this).text()),e.removeClass("current"),d.addClass("current"),c=g,e=d,b.hide(),window.location.href=e.find("a").attr("href");return!1})})});$(document).bind("click",function(){$(this).find(".options").hide()})}},
ViewBy={init:function(){var a=$(".viewby");if(a.length!=0)this.bys=a.find("li"),this.vcoll=$("#vcoll"),this.pcoll=$("#pcoll"),this.by=a.find(".current"),this.bind()},bind:function(){var a=this;this.bys.each(function(){$(this).click(function(){if($(this).attr("viewby")!=a.by.attr("viewby")){$(this).addClass("current");var b=$(this).attr("viewby");a.vcoll.length!=0&&(b=="list"?a.vcoll.addClass("colllist1w").removeClass("collgrid4w"):a.vcoll.addClass("collgrid4w").removeClass("colllist1w"));a.pcoll.length!=
0&&(b=="list"?a.pcoll.addClass("colllist1w").removeClass("collgrid4w"):a.pcoll.addClass("collgrid4w").removeClass("colllist1w"));a.by.length!=0&&a.by.removeClass("current")}a.by=$(this);writeCookie("youkuviewby",a.by.attr("viewby"),8640)})})}},PartPop={init:function(){this.pophandles=$(".v .v_part");this.globalpop=$("#globalpop");this.pophandles.length!=0&&this.bind()},bind:function(){var a=this;$.each(this.pophandles,function(){var b=$(this).find(".handle"),c=$(this).find(".pop");$(this).mouseover(function(){a.globalpop.length==
0&&a.create();$.each(c.find("img"),function(){var a=$(this).attr("_src");a&&($(this).attr("src",a),$(this).removeAttr("_src"))});a.globalpop.html(c.html());var d=b.offset(),e=d.top-4,f=d.left+20;d.left+280+5>parseInt($(window).width())&&(f=d.left-280);a.globalpop.css({top:e,left:f});a.globalpop.show()});$(this).mouseout(function(){a.globalpop.hide()})})},create:function(){if(this.globalpop.length==0)this.globalpop=$('<div id="globalpop" class="pop"></div>'),$(document.body).append(this.globalpop),
this.globalpop.mouseover(function(){$(this).show()}),this.globalpop.mouseout(function(){$(this).hide()})}},SpeedSelector={init:function(){this.selectors=$(".gotoplay .source");this.selectors.length!=0&&this.bind()},bind:function(){$.each(this.selectors,function(){var a=$(this).find(".check"),b=$(this).find(".other"),c=b.find("li");$(this).click(function(){b.length!=0&&c.length!=0&&b.toggle();return!1});$(document).bind("click",function(){b.hide()});$.each(c,function(){$(this).click(function(){var c=
$(this).html(),e=a.html();a.html(c);$(this).html(e);b.hide();return!1})})})}},PanelExpand={init:function(){this.handles=$(".linkpanel .handle");this.handles.length!=0&&this.bind()},bind:function(){$.each(this.handles,function(){$(this).click(function(){var a=$(this).parent().parent().find(".panelexpand");$(this).toggleClass("handleexpand");a.toggle();$.trim($(this).text())=="\u5168\u90e8"?$(this).html("<span>\u6536\u8d77</span>"):$(this).html("<span>\u5168\u90e8</span>");return!1})})}},Feedwin={init:function(){var a=
$(".feedwin"),b=navigator.userAgent.toLowerCase().match(/ipad|iphone|ipod|itouch/i),c=$(document.body).attr("scrollHeight");a.show();b&&a.css("top",c-280)}},FeedBack={init:function(){this.handles=$(".feedwin .handle");this.steps=$(".feedwin .step");this.handles.length!=0&&this.bind()},bind:function(){$(".feedwin .close").click(function(){$(".feedwin .feed").css("display","none");$.each($(".feedwin .step"),function(){$(this).css("display","block")});$(".feedwin .feed .like").removeClass("selected");
$(".feedwin .feed .unlike").removeClass("selected");$(".feedwin  .feed .step3").css("display","none");$("#state").attr("value","");$("#message").attr("value","\u503e\u542c\u60a8\u7684\u5efa\u8bae(100\u5b57\u4ee5\u5185)");$("#message").css("color","#909090");$("#message").click(function(){$("#message").attr("value","");$("#message").css("color","#000000");$(this).unbind("click")})});$("#message").click(function(){$("#message").attr("value","");$("#message").css("color","#000000");$(this).unbind("click")});
this.handles.click(function(){$(".feedwin").find(".feed").toggle();$.each($(".feedwin .step"),function(){$(this).css("display","block")});$(".feedwin .feed .like").removeClass("selected");$(".feedwin .feed .unlike").removeClass("selected");$(".feedwin  .feed .step3").css("display","none");$("#message").css("color","#909090");$("#message").attr("value","\u503e\u542c\u60a8\u7684\u5efa\u8bae(100\u5b57\u4ee5\u5185)");$("#state").attr("value","");$("#message").click(function(){$("#message").attr("value",
"");$("#message").css("color","#000000");$(this).unbind("click")});$(".feedwin .feed  .input button").html("\u63d0\u4ea4");$(".feedwin .feed  .input button").unbind("click");$(".feedwin .feed  .input button").click(function(){commitFeedbak()})});$(".feedwin .feed .like").click(function(){$("#state").attr("value",1);$(this).addClass("selected");$(".feedwin .feed .unlike").removeClass("selected")});$(".feedwin .feed .unlike").click(function(){$("#state").attr("value",0);$(this).addClass("selected");
$(".feedwin .feed .like").removeClass("selected")})}};
function commitFeedbak(){if($("#state").attr("value")!="1"&&$("#state").attr("value")!="0")return alert("\u8bf7\u9009\u62e9\u662f\u5426\u559c\u6b22"),!1;var a="";$("#message").attr("value")!="\u503e\u542c\u60a8\u7684\u5efa\u8bae(100\u5b57\u4ee5\u5185)"&&(a=$("#message").attr("value"));if(a.length>100)return alert("\u60a8\u7684\u8f93\u5165\u5df2\u7ecf\u8d85\u51fa\u9650\u5236\uff0c\u8bf7\u4fee\u6539\uff01"),!1;$.ajax({url:"/service/feedback",type:"POST",data:"keyword="+encodeURI($("#keyword").val())+
"&state="+$("#state").val()+"&source=1&url="+encodeURI(window.location.href)+"&message="+encodeURI(a)});$(".feedwin .feed  .input button").html("\u6b63\u5728\u63d0\u4ea4");$(".feedwin .feed  .input button").unbind("click");$.each($(".feedwin .step"),function(){$(this).css("display","none")});$(".feedwin  .feed .step3").css("display","block");$(".feedwin .feed").fadeOut(1E3)}SoOption.init();Filter.init();ViewBy.init();SpeedSelector.init();PanelExpand.init();PartPop.init();FeedBack.init();Feedwin.init();
