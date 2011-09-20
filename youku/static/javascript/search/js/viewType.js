var nowType = "img";//默认排列
//var nowType = "detail";//默认排列

function view(type){
	var icoImg = document.getElementById("icoImg");
	var icoDetail = document.getElementById("icoDetail");
	var objImg = document.getElementById("img");
	var objDetail = document.getElementById("detail");
	if(!icoImg || !icoDetail || !objImg || !objDetail)return
	if(type == "detail"){
		icoDetail.className = "current";
		objDetail.style.display = "block";
		icoImg.className = "";
		objImg.style.display = "none";
		setNowType("detail");
	}
	else if(type == "img"){
		icoDetail.className = "";
		objDetail.style.display = "none";
		icoImg.className = "current";
		objImg.style.display = "block";
		setNowType("img");
	}
}
function setNowType(type){
	nowType = type;
	Nova.Cookie.set("SearchViewType",nowType,7,"/",".soku.com");
}

function initsearchviewtype(){
	var type=Nova.Cookie.get("SearchViewType");
	if(type=="detail" || type=="img"){
		nowType=type;
	}
	view(nowType);
}
//window.nova_init_hook_initsearchviewtype=initsearchviewtype;

function showMore(order){
	var list = document.getElementById("list" + order).getElementsByTagName("LI");
	var handle =  document.getElementById("handle" + order);
	handle.style.display = "none";
	for(var i=0;i<list.length;i++){
		list[i].style.display = "block";
	}
}

function showExpand(handle, expand, textshow, texthide){
	if(!(handle && expand)){ return; }
	if(expand.style.display != 'block'){
		handle.innerHTML = textshow;
		expand.style.display = 'block';
	}else{
		handle.innerHTML = texthide;
		expand.style.display = 'none';
	}
}




//系列节目相关
function clickseriesodshow(index){
	for(var i=0;true;i++){
		if( $("div_series_odshow_subtitle_"+i) && $("div_series_odshow_detail_"+i)){
			$("div_series_odshow_subtitle_"+i).className = i==index?"current":"";
			$("div_series_odshow_detail_"+i).style.display = i==index?"":"none";
		}else{
			break;
		}
	}
}


var seriesodshowBuffer={
	data : {},
	append : function(key,value){this.data[key]=value},
	inBuffer : function(key){if(this.data[key]){return true}else{return false}},
	getBuffer : function(key){if(this.data[key]){return this.data[key]}else{return}}
}

function getseriesodshow(showid,index){
	if(seriesodshowBuffer.inBuffer('headdata_'+showid)){
		renderseriesodshow(showid,seriesodshowBuffer.getBuffer('headdata_'+showid),index);
		return;
	}
	
	if($('div_series_first_video'))$('div_series_first_video').innerHTML="loading";
	if($('div_series_subtitle'))$('div_series_subtitle').innerHTML="loading";
	if($('div_series_odshow_detail'))$('div_series_odshow_detail_all').innerHTML="loading";
    url = '/QSearch/~ajax/getSeriesOdshow';
	nova_call(url, {'showid':showid},
	    function callback(result){
	    	var data = typeof(result) == "string" ? eval("("+result+")") : result;
			if(!data)return;
			seriesodshowBuffer.append('headdata_'+showid,data)
	    	renderseriesodshow(showid,data,index);
	    }
	);

}
function renderseriesodshow(showid,data,index){
	var firstvideohtml='';
	var subtitlehtml='';
	var detailhtml='';
	for(var i=0;true;i++){
		if( $("div_series_title_"+i) ){
			$("div_series_title_"+i).className = i==index?"current":"";
		}else{
			break;
		}
	}
	var searchKey ='';
	var keys = document.getElementsByClassName("key","");
	if(keys.length>1){
		searchKey = keys[0].innerHTML;
	}
	if($("div_series_show_url")){
		$("div_series_show_url").href="http://www.youku.com/show_page/id_z"+showid+".html";
		$("div_series_show_url").onclick="logSearchClick('youku_click','video','http://www.youku.com/show_page/id_z"+showid+".html','"+searchKey+"',data['showcateid'])";
	}
	if($("div_series_show_rating")){
		var avg_rating = data['avg_rating'];
		var fullNum1 = parseInt(avg_rating/2)
		var isPart1 = avg_rating>(fullNum1*2);
		var tmp = "";
		for(var j=1;j<=5;j++){
			if(fullNum1>=j){
				tmp+='<em class="ico__ratefull"></em>';
			}else {
				if(isPart1==true){
					isPart1 =false;
					tmp+='<em class="ico__ratepart"></em>';
				}else {
					tmp+='<em class="ico__ratenull"></em>';
				}
			}
		}
		tmp+='<em class="num">'+avg_rating+'</em>';
		
		var showtotal_up = data['showtotal_up'];
		var showtotal_down = data['showtotal_down'];
		$("div_series_show_rating").innerHTML=tmp;
		$("div_series_show_rating").title="有"+showtotal_up+"人顶过,有"+showtotal_down+"人踩过";
	}
	if($("div_series_show_person")){
		var person = "";
		for(var j=0;j<data['person'].length&j<3;j++){
			person += data['person'][j]+" ";
		}
		$("div_series_show_person").innerHTML=person;
	}
	
	if( $('div_series_first_video') ){
		firstvideohtml+='<li class="show_link">';
		if(data['firstvideodisabled']==1){
			firstvideohtml+='<a title="即将上映" href="#"></a>';
		}else{
			firstvideohtml+='<a title="'+data['firsttitle']+'" href="http://v.youku.com/v_show/id_'+data['firstvideoid']+'.html" onclick="logSearchClick(\'youku_click\',\'video\',\'http://v.youku.com/v_show/id_'+data['firstvideoid']+'.html\',\''+searchKey+'\','+data['showcateid']+')" target="_blank" charset="801-101"></a>';
		}
		firstvideohtml+='</li>';
		firstvideohtml+='<li class="show_img">';
		firstvideohtml+='<img  src="'+data['showthumburl']+'" title="'+data['firsttitle']+'" alt="'+data['firsttitle']+'" />';
		firstvideohtml+='</li>';
		firstvideohtml+=data['paid']==1?'<li class="show_ischarge">':'';
		firstvideohtml+='<li class="show_update">'
		firstvideohtml+='<span class="status">'
		firstvideohtml+=data['firststage'];
		firstvideohtml+=data['new']==1?'<span class="new" title="新"></span>':'';
		firstvideohtml+='</span>';
		firstvideohtml+=data['hd']==1?'<span class="ico__HD" title="高清"></span>':'';
		firstvideohtml+='<span class="bg"></span>';
		firstvideohtml+='</li>';
		firstvideohtml+='<li><span style="color:#f00;">';
		firstvideohtml+=data['update_notice'];
		firstvideohtml+='</span></li>';
	}
	$('div_series_first_video').innerHTML=firstvideohtml;
	
	if(data['videos'] ){
		detailhtml+='<ul id="div_series_odshow_detail" class="detail">';
		for(var j=0;j<data['videos'].length;j++){
			if(data['videos'][j]['show_videostage']==0)continue;
			if( (data['videos'][j]['state']!='normal' && data['videos'][j]['state']!='limited') || data['videos'][j]['show_videocompleted']==0 ){
				detailhtml+='<li class="disabled"> <span>'+data['videos'][j]['show_videostage']+'</span> </li>';
			}else{
				detailhtml+='<li> <a href="http://v.youku.com/v_show/id_'+ data['videos'][j]['videoid'] +'.html" onclick="logSearchClick(\'youku_click\',\'video\',\'http://v.youku.com/v_show/id_'+data['videos'][j]['videoid']+'.html\',\''+searchKey+'\','+data['showcateid']+')" target="_blank" title="'+data['videos'][j]['title']+'" charset="801-101">'+data['videos'][j]['show_videostage']+'</a> </li>';
			}
		}
		detailhtml+='</ul> ';
		
		if(data['usemore']==1){
			detailhtml+='<ul class="detail" id="div_series_odshow_detail_more" style="display:none;"></ul>';
			detailhtml+='<div class="clear"></div>';
			detailhtml+='<div class="expandHandle"><a class="arrow" href="javascript:getseriesodshowmore(\''+data['currentshowid']+'\','+data['lastseq']+')" id="div_series_odshow_detail_more_button">查看更多&gt;&gt;</a></div>';
		}
		
		$('div_series_odshow_detail_all').innerHTML=detailhtml;
	}
	
	
}

function getseriesodshowmore(showid,lastseq,index){
	if(index!=undefined){
		var more = $('div_series_odshow_detail_more_'+index);
		var btn= $('div_series_odshow_detail_more_button_'+index);
	}else{
		var more = $('div_series_odshow_detail_more');
		var btn= $('div_series_odshow_detail_more_button');
	}
	
	if(!more)return;
	
	if(more.innerHTML!=''){
		if(more.style.display=='none'){
			more.style.display='block';
			btn.innerHTML='收起↑';
		}else{
			more.style.display='none';
			btn.innerHTML='查看更多&gt;&gt;';
		}
		return;
	}
	btn.innerHTML="";
	
	if(seriesodshowBuffer.inBuffer('moredata_'+showid)){
		renderseriesodshowmore(showid,seriesodshowBuffer.getBuffer('moredata_'+showid));
		return;
	}
	
    url = '/QSearch/~ajax/getSeriesOdshow';
	nova_call(url, {'showid':showid,'lastseq':lastseq},
	    function callback(result){
	    	var data = typeof(result) == "string" ? eval("("+result+")") : result;
	    	if(!data)return;
	    	seriesodshowBuffer.append('moredata_'+showid,data)
	    	renderseriesodshowmore(showid,data,index);
	    }
	    
	);
}
function renderseriesodshowmore(showid,data,index){
	var detailhtml='';
	if(!data['videos'])return;
	if(index!=undefined){
		var more = $('div_series_odshow_detail_more_'+index);
		var btn= $('div_series_odshow_detail_more_button_'+index);
		var charset='801-104';
	}else{
		var more = $('div_series_odshow_detail_more');
		var btn= $('div_series_odshow_detail_more_button');
		var charset='801-101';
	}
	
	var searchKey ='';
	var keys = document.getElementsByClassName("key","");
	if(keys.length>1){
		searchKey = keys[0].innerHTML;
	}
		
	for(var j=0;j<data['videos'].length;j++){
		if(data['videos'][j]['show_videostage']==0)continue;
		if( (data['videos'][j]['state']!='normal' && data['videos'][j]['state']!='limited') || data['videos'][j]['show_videocompleted']==0 ){
			detailhtml+='<li class="disabled"> <span>'+data['videos'][j]['show_videostage']+'</span> </li>';
		}else{
			if(data['videos'][j]['show_videostage']<=99 && charset!='801-101' )charset='801-103';
			detailhtml+='<li> <a href="http://v.youku.com/v_show/id_'+ data['videos'][j]['videoid'] +'.html" onclick="logSearchClick(\'youku_click\',\'video\',\'http://v.youku.com/v_show/id_'+data['videos'][j]['videoid']+'.html\',\''+searchKey+'\','+data['showcateid']+')" target="_blank" title="'+data['videos'][j]['title']+'" charset="'+charset+'">'+data['videos'][j]['show_videostage']+'</a> </li>';
		}
	}
	more.innerHTML=detailhtml;
	more.style.display='block';
	btn.innerHTML='收起↑';
}


//节目相关
function clickodshow(showindex,index){
	for(var i=0;true;i++){
		if( $("div_odshow_"+showindex+"_slice_title_"+i) && $("div_odshow_"+showindex+"_slice_detail_"+i)){
			$("div_odshow_"+showindex+"_slice_title_"+i).className = i==index?"current":"";
			$("div_odshow_"+showindex+"_slice_detail_"+i).style.display = i==index?"":"none";
		}else{
			break;
		}
	}
}
				
//剧集相关
function clickdrama(index,type){
	for(var i=0;true;i++){
		if( $("div_drama_logo_"+i) && $("div_drama_version_"+i) && $("div_drama_detail_"+i)){
			$("div_drama_logo_"+i).style.display = i==index?"":"none";
			$("div_drama_version_"+i).className = i==index?"current":"";
			$("div_drama_detail_"+i).style.display = i==index?"":"none";
		}else{
			break;
		}
	}
}
function dramaExpandControl(index){
	var expandDetail = document.getElementById("dramaExpandDetail_"+index);
	var expandHandle = document.getElementById("dramaExpandHandle_"+index);
	if(expandDetail.style.display == "none"){
		expandDetail.style.display = "block";
		expandHandle.innerHTML = '<a class="arrow" href="javascript:dramaExpandControl('+index+')">收起↑</a> ';
	}else{
		expandDetail.style.display = "none";
		expandHandle.innerHTML = '<a class="arrow" href="javascript:dramaExpandControl('+index+')">查看更多&gt;&gt;</a> ';
	}
}

//综艺相关
function clickzongyi(index){
	for(var i=0;true;i++){
		if( $("div_zongyi_logo_"+i) && $("div_zongyi_version_"+i) && $("div_zongyi_detail_"+i)){
			$("div_zongyi_logo_"+i).style.display = i==index?"":"none";
			$("div_zongyi_version_"+i).className = i==index?"current":"";
			$("div_zongyi_detail_"+i).style.display = i==index?"":"none";
		}else{
			break;
		}
	}
}
function zongyiExpandControl(act,index,episodeCount){
	var expandHandle = document.getElementById("zongyiExpandHandle_"+index);
	if(!expandHandle)return;
	
	if(act == "open"){
		for(var i=0;i<episodeCount;i++){
			var expandDetail = document.getElementById("zongyiExpandDetail_"+index+"_"+i);
			expandDetail.style.display="block";
		}
		expandHandle.innerHTML = '<a class="arrow" href="javascript:zongyiExpandControl(\'close\','+index+','+episodeCount+')">收起↑</a>';
	}else{
		for(var i=0;i<(episodeCount-1);i++){
			var expandDetail = document.getElementById("zongyiExpandDetail_"+index+"_"+i);
			expandDetail.style.display="none";
		}
		expandHandle.innerHTML = '<a class="arrow" href="javascript:zongyiExpandControl(\'open\','+index+','+episodeCount+')">查看更多&gt;&gt;</a>';
	}
}


//人物相关

function person_show(personname,n){
	if(personname=='')return false;
	var detailid = 'person_show_detail_'+n;
	if(!detailid)return false;
	for(var i=0;i<10;i++){
		var tab=$("person_tab_"+i);
		var detail = $("person_show_detail_"+i);
		if(!tab || tab=='undefined' || tab==null || !detail || detail=='undefined' || detail==null)continue;
		if(n==i){
			tab.className="current";
			detail.style.display='block';
		}else{
			tab.className="";
			detail.style.display='none';
		}
	}
	if($(detailid).innerHTML!='')return false;
	$(detailid).innerHTML='loading';
	var url = '/search/getPersonOdshow';
	var params={'personname':personname,'categorytab':n};
	nova_update(detailid, url, params, 'GET');
}
function init_person_show_tab(personname){
	for(var i=0;i<10;i++){
		var tab=$("person_tab_"+i);
		var detail = $("person_show_detail_"+i);
		if(!tab || tab=='undefined' || tab==null || !detail || detail=='undefined' || detail==null)continue;
		person_show(personname,i);
		return;
	}
}
