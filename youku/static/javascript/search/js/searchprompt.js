var lastindex=-1;
var search_prompt_flag=false;
var listlength=0;
function StringBuffer(){this.data=[];} 
StringBuffer.prototype.append=function(){this.data.push(arguments[0]);return this;} 
StringBuffer.prototype.tostring=function(){return this.data.join("");} 
String.prototype.Trim = function(){return this.replace(/(^\s*)|(\s*$)/g, "");}

var XmlResultBuffer={
	data : {},
	append : function(key,value){this.data[key]=value},
	inBuffer : function(key){if(this.data[key]){return true}else{return false}},
	getBuffer : function(key){if(this.data[key]){return this.data[key]}else{return}},
	cleanBuffer: function(){this.data={}}
}
var RequestDelayHandle=0;
var search_prompt_list_width=0;//提示框宽度
function hiddensearch(){
	if(!$('search_prompt_list') )return;
	$('search_prompt_list').style.display="none";
}
function showsearch(num){
	if(!$('search_prompt_list')  )return;
    $('search_prompt_list').style.display='';
    $('search_prompt_list').style.height=num*20+num+'px';
}

function getposition(element,offset){
    var c=0;
    while(element){
        c+=element[offset];
        element=element.offsetParent
    }
    return c;
}

function create_prompt_list(){
	//边框
	var listDiv=document.createElement("div");
	listDiv.id="search_prompt_list";
	
	//top:29px;left:0px;width:349px;background:#fff;border:1px solid #76ceee;
	
	listDiv.style.zIndex="99999";
	listDiv.style.position="absolute"; 
	listDiv.style.border="solid 1px #76ceee";
	listDiv.style.backgroundColor="#FFFFFF";
	listDiv.style.display="none";	
	listDiv.style.width=(search_prompt_list_width-1)+"px";
	listDiv.style.left=getposition($('tool'),'offsetLeft')+"px";
	listDiv.style.top =(getposition($('headq'),'offsetTop')+$('headq').clientHeight +1)+"px";
	
	
	document.body.appendChild(listDiv);       
	
}
function setstyle(element,classname){
	switch (classname){
        case 'm':
            element.style.fontSize="14px";
		    element.style.fontFamily="arial,sans-serif";
		    element.style.backgroundColor="#3366cc";
		    element.style.color="black";
		    element.style.width=(search_prompt_list_width-11)+"px";
		    element.style.height="20px";
            element.style.padding="1px 5px 0px 5px";
            if(element.displayTitleSpan)element.displayTitleSpan.style.color="white";
            if(element.displayHitSpan)element.displayHitSpan.style.color="white";
		    break;
        case 'd':
	        element.style.fontSize="14px";
		    element.style.fontFamily="arial,sans-serif";
		    element.style.backgroundColor="white";
		    element.style.color="black";
		    element.style.width=(search_prompt_list_width-11)+"px";
		    element.style.height="20px";
            element.style.padding="1px 5px 0px 5px";
            if(element.displayTitleSpan)element.displayTitleSpan.style.color="black";
            if(element.displayHitSpan)element.displayHitSpan.style.color="#909090";
		    break;
	 case 't':
            element.style.width="60%";
            element.setStyle({cssFloat:"left"});
			element.style.display='block';
            element.style.whiteSpace="nowrap";
            element.style.overflow="hidden";
            element.style.textOverflow="ellipsis";
            element.style.fontSize="14px";
            element.style.textAlign="left";
            break;
	 case 'h':
            element.style.width="40%";
            element.setStyle({cssFloat:"left"});
			element.style.display='block';
            element.style.textAlign="right";
            element.style.color="#909090";
            break;
    }
}
function focusitem(index){
    if($('item'+lastindex)!=null)setstyle($('item'+lastindex),'d');
    if($('item'+index)!=null){
        setstyle($('item'+index), 'm');
        lastindex=index;
    }else{
        lastindex=-1;
        $("headq").focus();
    }
}
function searchclick(index){
    if(index){
        $("headq").value=$('title'+index).innerHTML;
        search_prompt_flag=true;
    }else{
        if($('title'+lastindex)!=null){
            $("headq").value=$('title'+lastindex).innerHTML;
            search_prompt_flag=true;
        }
    }
}
function searchformsubmit(){
	if( dosearch($("headq").form) ){
    	$("headq").form.submit();
    }	
}

function searchkeydown(e){
	if(!$('search_prompt_list') )return;
    if($('search_prompt_list').innerHTML=='')return;
    var keycode=(window.navigator.appName=="Microsoft Internet Explorer")?event.keyCode:e.which;
    
    //down
    if(keycode==40){
	   if(lastindex==-1||lastindex==listlength-1){
	       focusitem(0);
	       searchclick(0);
	   }else{
	       focusitem(lastindex+1);
	       searchclick();
	   }
    }
    //up
    if(keycode==38){
	   if(lastindex==-1 && listlength>0){
	       //focusitem(0);
	       //searchclick(0);
	       focusitem(listlength-1);
	       searchclick(listlength-1);
	   }else{
	       focusitem(lastindex-1);
	       searchclick();
	   }
    }
    //enter
    if(keycode==13){
        if(lastindex!=-1){
            focusitem(lastindex);
            //$("headq").value=$('title'+lastindex).innerHTML;
        }
        hiddensearch();
    }
    //backspace || delete
    if(keycode==46||keycode==8){search_prompt_flag=false;ajaxsearch();}
}
function showresult(responseText,buffer){
	if(typeof(responseText)!="string")return;
	if(responseText.Trim()=="")return;
	var result=eval(responseText);
	
	if(typeof(result)!="object")return;
	if(!buffer){
		var outer= ($('outer') && $('outer').checked) ? 't' : 'f';
		var searchtype = result.type?result.type:'video';
		XmlResultBuffer.append(result.keyword+searchtype+outer,responseText);
	}
	switch(result.type){
		case 'video':type='视频';break;
		case 'playlist':type='专辑';break;
		case 'user':type='空间';break;
		case 'bar':type='帖子';break;
		default:type="视频";
	}
	if(result.result!=''){
        var resultstring=new StringBuffer();
        for(var i=0;i<result.result.length;i++){
            resultstring.append('<div id="item'+i+'" onmousemove="focusitem('+i+')" onmousedown="searchclick('+i+');searchformsubmit()">');
            resultstring.append('<span id=title'+i+'>');
            resultstring.append(result.result[i].keyword);
            resultstring.append('</span>');
            resultstring.append('<span id=hits'+i+'>');
            resultstring.append(result.result[i].count+"个"+type);
            resultstring.append('</span>');
            resultstring.append('</div>');
        }
        $('search_prompt_list').innerHTML=resultstring.tostring();
        for(var j=0;j<result.result.length;j++){
            setstyle($('item'+j),'d');
            $('item'+j).displayTitleSpan=$('title'+j);
            $('item'+j).displayHitSpan=$('hits'+j);
            setstyle($('title'+j),'t');
            setstyle($('hits'+j),'h');
        }
        showsearch(result.result.length);
        listlength=result.result.length;
        lastindex=-1;
	}else hiddensearch();
}
function ajaxsearch(){
	
	var value=$F('headq').Trim();
	if(value=='')return ;
	/*
	var pattern=/([`~!@#\$\%\^\&\*\(\)\_\+\-\=\{\}\|\[\]\\:\";\'<>\?,\.\/｀～！·＃￥％……—＊（）—＋－＝｛｝｜［］、：“；‘《》？，。／])+$/;
	if((pattern.test(value)))return;
	*/
	var type=$('headSearchType').value;
	var outer= ($('outer') && $('outer').checked) ? 't' : 'f';
	if(outer=='t')type='video';
		
	if(XmlResultBuffer.inBuffer(value+type+outer)){
		showresult( XmlResultBuffer.getBuffer(value+type+outer) ,true);
	}else{
		clearTimeout(RequestDelayHandle);
		RequestDelayHandle=setTimeout("sendRequest()",400);
	}
}
function sendRequest(){
	var value=$F('headq').Trim();
	if(value=='')return;
	var type=$('headSearchType').value;
	if($('outer') && $('outer').checked){//全网搜索
		var url="http://tip.soku.com/search_keys?type=video&k="+encodeURIComponent(value);
	}else{
		var url="http://tip.so.youku.com/search_keys?k="+encodeURIComponent(value)+"&type="+type;
	}
	Nova.addScript(url);
}
function initPrompt(e){
	//return false;//取消下拉提示
	if($F('headq').Trim()==''){
	    hiddensearch();
	}else{
		var keycode=(document.all)?event.keyCode:e.which;
        if(keycode!=40 && keycode!=38 && keycode!=13)search_prompt_flag=false;
		if($F('headq')!=''&&search_prompt_flag==false)ajaxsearch();
		$('headq').onkeydown=searchkeydown;
        if(listlength==0){
            hiddensearch();
        }
	}
}

function reposition(){
	if(!$('search_prompt_list') )return;
	if($('search_prompt_list')){
		$('search_prompt_list').style.left=getposition($('tool'),'offsetLeft')+"px";
		$('search_prompt_list').style.top =(getposition($('headq'),'offsetTop')+$('headq').clientHeight +1)+"px";	
	}
	
}


window.nova_init_hook_searchinputinit=function(){
	var ie=(document.all)?true:false;
	if(ie){
		$('headq').attachEvent('onfocus',initPrompt);
		$('headq').attachEvent('onblur',hiddensearch);
		$('headq').attachEvent('onkeyup',initPrompt);
		
	}else{
		$('headq').addEventListener('focus',initPrompt,false);
		$('headq').addEventListener('blur',hiddensearch,false);
		$('headq').addEventListener('keyup',initPrompt,false);
	}
	search_prompt_list_width=$('headq').clientWidth>350?390:350;
	
	create_prompt_list();
	window.onresize=reposition;
	
	search_attachEvent($('inner'),'click',	function(){
		$('tool').className = 'tool inner';
	});
	search_attachEvent($('outer'),'click',	function(){
		$('tool').className = 'tool outer';
	});
	
	search_attachEvent($('inner'),'mousedown',	function(){
		if(!$('inner').checked)switchSo('inner');
	});
	search_attachEvent($('outer'),'mousedown',	function(){
		if(!$('outer').checked)switchSo('outer');
	});
	if($('lb_inner')){
		search_attachEvent($('lb_inner'),'click',	function(){
			if(!$('inner').checked){
				$('inner').checked='checked';
				switchSo('inner');
			}
		});
	}
	if($('lb_outer')){
		search_attachEvent($('lb_outer'),'click',	function(){
			if(!$('outer').checked){
				$('outer').checked='checked';
				switchSo('outer');
			}
		});
	}
}

function show_search_type(pos){
	if($(pos+'Sel').style.display=='none'){
		$(pos+'Sel').style.display = 'block';
	}else{	
		$(pos+'Sel').style.display = 'none';
	}
}

function search_attachEvent(o, evt, func){
	if(window.addEventListener){
		o.addEventListener(evt,  func , false); 
	}
	else if(window.attachEvent){
		o.attachEvent('on'+ evt, func);
	}
}
function switchSo(type){
	if(trim( $F('headq').replace(/[\/_]/g,' ') )=='')return;//'
	var q = encodeURIComponent($F('headq').replace(/[\/_]/g,' '));//'
	var image=new Image();
	
	var searchdomain = $F("searchdomain")?$F("searchdomain"):'http://www.soku.com';
	if(type=='outer'){//全网搜索
		var url=searchdomain+"/v?keyword="+q;
		image.src='http://hz.youku.com/red/click.php?tp=1&cp=4000542&cpp=1000221&url=';
	}else{//站内搜索
		var url= searchdomain+"/search_"+$F("headSearchType")+"/q_"+q;
		image.src='http://hz.youku.com/red/click.php?tp=1&cp=4000541&cpp=1000221&url=';
	}
	location.href=url;
}

		