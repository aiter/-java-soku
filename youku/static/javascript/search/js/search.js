var SearchStatServerURI = "http://so.lstat.youku.com/"; 
var SearchResultPage = 0;
var SearchSource = "";
var isCharsetClicked = true;
var opentime = (new Date()).valueOf(); //页面打开的时间

function toSearchStat(){

	toStat("search.php?result="+$('resultTotal').value+"&time="+getSearchTime()+getKeyAndType()+getReferrer()+getShowIds()+getPersonId(),1);
}

function toSearchStatClick(objectid,pos){
	if(SearchResultPage < 1){
		SearchResultPage = 1;
	}
	pos = pos+1+(SearchResultPage-1)*25;
	toStat("result.php?objectid="+objectid+"&pos="+pos+"&time="+getSearchTime()+getKeyAndType()+getReferrer(),0);
}

function getShowIds(){
	if(typeof search_showids == "undefined"){return "";}
	return "&showids="+search_showids;
}

function getPersonId(){
	if(typeof search_personid == "undefined"){return "";}
	return "&personid="+search_personid;
}
function toNoResultStat(){
        toStat("noresult.php?time="+getSearchTime()+getKeyAndType()+getReferrer(),-1);
}

function toStat(url,pagelimit){
//    if((pagelimit <1 || SearchResultPage < 2) && SearchSource != "recommend"){
//		var image=new Image(1,1);
//		image.src=SearchStatServerURI+url;
//		image.onload=function() { return true; }
//	}
	return true;
}

function getReferrer(){
	if(document.referrer.length > 0){
		return "&refer="+encodeURIComponent(document.referrer);
	}else{
		return "";
	}
}

function getSearchTime(){
	return document.getElementById('searchTime').value;
}

function getKeyAndType(){
	var url = document.location.pathname;
	if(url.indexOf("/") == 0){
		url = url.substr(1);
	}
	url = url.replace(/_/g,"/")
	var result = "";
	var url_parms = url.split("/");
	if(url_parms.length > 1){
		var preKey = "";
		for(var i=0;i<url_parms.length;i++){
			if(preKey == "search"){
				result += "&type="+url_parms[i];
				preKey = "";
			}else if(preKey == "q"){
				result += "&keyword="+url_parms[i];
				preKey = "";
			}else if(preKey == "page"){
				SearchResultPage = url_parms[i];
				preKey = "";
			}else if(preKey == "source"){
				SearchSource = url_parms[i].toLowerCase();
				preKey = "";
			}else{
				preKey = url_parms[i].toLowerCase();
			}
		}
	}
	return result;
}

function setAgainSearchFocus(){
	window.location.href = "#searchagin";
	$('SearchAgainKeyword').focus();
	return false;
}

window.nova_init_hook_click_charset=function(){
	//Event.observe(document, "mousedown",  charset_click);
	Event.observe(document, "mousedown",  search_hot_charset_click);
}
function search_hot_charset_click(evt){
	if(!evt) evt = window.event;
	var ret=true;
	var obj = (evt.target)? evt.target:evt.srcElement;
	if(obj.onclick){ret=false;}
	if(obj.tagName!='A')obj = obj.parentNode;
	if(obj.tagName=='A'){
		var _charset= (obj.charset)? obj.charset:'';
		var _url = window.location.href.replace('http://'+document.domain,'');
		if(_charset!=''){
			_url = encodeURIComponent(_url)
			_charset = encodeURIComponent(_charset);
			var Img = new Image();
			Img.src="http://lstat.youku.com/hotsearch.php?url="+_url+"&code="+_charset+"&rnd="+(new Date()).getTime();
		}
		return ret
	}else{
		return true;
	}
}

var search_module_view_id='';
function log_module_view(id){
	if(search_module_view_id==''){
		search_module_view_id=id;
	}else{
		search_module_view_id+=","+id;
	}
}
window.nova_init_hook_search_module_view=function(){
	if(!search_module_view_id)return;
	var Img = new Image();
	Img.src="http://lstat.youku.com/sokuInsiteShow.php?pos="+search_module_view_id+"&rnd="+(new Date()).getTime();;
}


function VerifySearchAgainForm(f){
	if(f.SearchAgainKeyword.value==''){
		alert('关键词不能为空');
		f.SearchAgainKeyword.focus();
	}else{
		var k = encodeURIComponent(f.SearchAgainKeyword.value.replace(/\//g,''));
		var su = f.action + '/q_' + k;
		if(f.exKeywords){
			var mk = encodeURIComponent(f.midKeywords.value.replace(/\//g,''));
			var xso = f.exSearchOptions.value;
			var xk =  encodeURIComponent(f.exKeywords.value.replace(/\//g,''));
			if(xk != k && mk != k){
				su = f.action +'/q_'+k+'/ex_'+xk+'/again_in';
				if(mk != ""){su = su + '/mid_'+mk;}
				if(xso != ""){su = su + xso;}
			}
		}
		window.location.href= su;
	}
	return false;
}

function logSearchClick(logtype,type,url,keyword,cate){
	if(!url)return false;
	
	var nowtime = (new Date()).valueOf();
	var img = new Image();
	var host = 'http://log.so.youku.com/click?';
	var source = 'youku';
	if(!logtype)logtype='youku_click';
	if(!type)type='video';
	if(!keyword)keyword='';
	if(!cate)cate=0;
	var wait = nowtime - opentime;
	img.src = host+"log_type="+logtype+"&type="+type+"&url="+encodeURIComponent(url)+"&keyword="+keyword+"&source="+source+"&cate="+ cate +"&wait=" + wait;
}
