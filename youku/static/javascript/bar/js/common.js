//去左空格; 
function ltrim(s){ 
	return s.replace( /^\s*/, ""); 
} 
//去右空格; 
function rtrim(s){ 
	return s.replace( /\s*$/, ""); 
} 
//去左右空格; 
function trim(s){ 
	return rtrim(ltrim(s)); 
} 
/**
* 验证标准URL
*/
function checkURL(strValue){
	var regTextUrl = /^(http|https|ftp):\/\/(.+)$/;
	return regTextUrl.test(strValue);
}

/**
* 验证Email合法性
*/
function checkEmail(strValue){
	var regTextUrl = /[\w-.]+@{1}[\w-]+\.{1}\w{2,4}(\.{0,1}\w{2}){0,1}/ig;
	return regTextUrl.test(strValue);
}

/**
* 验证网站视频地址的合法性
*/
function checkVideoURL(strValue){
	var regTextUrl = /^http:\/\/[\w\.]+.youku.com\/v_show\/id_[\w=]+\.html$/;
	return regTextUrl.test(strValue);
}

function isDate(strDate){   
	
	if( strDate.match(/\d{4}-\d{1,2}-\d{1,2}/g)!=strDate )return false; //'
	
	var arrDate=strDate.split("-");
	if(arrDate.length!=3)return false;
	var nYear=parseInt(arrDate[0]);
	var nMonth=parseInt(arrDate[1]);
	var nDay=parseInt(arrDate[2]);
	
    if(nMonth == 2){
		if((nYear % 400 == 0) || (nYear % 4 == 0) && (nYear % 100 != 0)){
			if((nDay < 1) || (nDay > 29)){
				return false;
			}
		}else{
			if((nDay < 1) || (nDay > 28)){
				return false;
			}
		}
	}else if( (nMonth == 4) || (nMonth == 6) || (nMonth == 9) || (nMonth == 11) ){
		if(nDay<1 || nDay>30)return false;
	}else{
		if(nDay<1 || nDay>31)return false;
	}
	return true;
}


/**
* 清理html
*/
function clearhtml(str){
	var s = str;
	if(s=='')return s;
	s=s.replace(/<[^<]+>/ig,"");
	//s=s.replace(/[\b\n\s]*/ig,"");
	return s;
}

function htmlDecode(str){
    if(str=='')return str;
    str=str.replace(/&amp;/ig   ,"&");
    str=str.replace(/&gt;/ig    ,">");
    str=str.replace(/&lt;/ig    ,"<");
    str=str.replace(/&nbsp;/ig  ," ");
    str=str.replace(/&quot;/ig  ,"\"");
    str=str.replace(/&#0?39;/ig ,"'");
    return str;
}

function chkSearchForm(form){
    if(trim(form.kw.value)==''){
        alert('请输入查寻内容!');
        form.kw.focus();
        return false;
    }
	sd = form.searchdomain.value;
    var q = encodeURIComponent(form.kw.value.replace(/[\/_]/g,' ')); //'
	if(form.st[1].checked)q = q+"_sbt_post";
	if(form.st[2].checked)q = q+"_sbt_user";
	
	window.location.href= sd+"/search_bar/q_"+q;
	return false;
}
/**
* 弹出窗口
*/
function showWin(winSrc,width,height){
    var newWin = null;
	if(width == "" || width== null){
		width =560;
	}
	if(height == "" || height== null){
	    height=600;
	}
	newWin = window.open(winSrc,null,'width='+ width +',height='+ height +',status=yes,toolbar=no,menubar=no,location=no');
	newWin.focus();
	void(0);
} 

/**
*判断焦点
*/ 
function chkFocus(obj,f){
    if(obj.id=='username'){
        if(f=='onfocus'){
            if(obj.value=='Email或昵称'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='Email或昵称';
                obj.style.color='#909090';
            }
        }
    } 
    if(obj.id=='password'){
        if(f=='onfocus'){
            if(obj.value=='登录密码'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='登录密码';
                obj.style.color='#909090';
            }
        }
    }
    if(obj.id=='imgcontent'){
        if(f=='onfocus'){
            if(obj.value=='单贴最多可插入6个图片，用分号；隔开'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='单贴最多可插入6个图片，用分号；隔开';
                obj.style.color='#909090';
            }
        }
    }
    if(obj.id=='videocontent'){
        if(f=='onfocus'){
            if(obj.value=='单贴最多可插入6个视频，用分号；隔开'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='单贴最多可插入6个视频，用分号；隔开';
                obj.style.color='#909090';
            }
        }
    }
     if(obj.id=='commendvideo'){
        if(f=='onfocus'){
            if(obj.value=='一次最多可推荐5个视频，用分号；隔开'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='一次最多可推荐5个视频，用分号；隔开';
                obj.style.color='#909090';
            }
        }
    } 
    if(obj.id=='videovote'){
        if(f=='onfocus'){
            if(obj.value=='不超过40个视频，用分号；隔开'){
                obj.value='';
                obj.style.color='#000000';
            }
        }else{
            if(obj.value==''){
                obj.value='不超过40个视频，用分号；隔开';
                obj.style.color='#909090';
            }
        }
    } 
}

/*从字符串中获得Image地址，返回地址数组*/
function getImagesFromString(str){
    var re = new RegExp("\\[img\\]([^\\[]+)\\[\\/img\\]","ig");
    var imgs=Array();
    while ((arr = re.exec(str)) != null)imgs[imgs.length]=RegExp.$1;
    return imgs;
}
/*从字符串中获得视频地址，返回地址数组*/
function getVideosFromString(str){
    var re = new RegExp("\\[video\\](http:\\/\\/[\\w\\.]+.youku.com\\/v_show\\/id_[\\w=]+\\.html)\\[\\/video\\]","ig");
    var videos=Array();
    while ((arr = re.exec(str)) != null){
        videos[videos.length]=RegExp.$1;
    }
    return videos;
}



function closeWin(){
    try{
        parent.pop.close();
    }
    catch(e){}
}

/*替换页面中所有分级视频显示*/
function replaceGrade(){
    if(!$("post_list_part"))return ;
    var str=$("post_list_part").innerHTML;
    //var regEx=/flash_player_\d+_([A-Za-z0-9=]+)_\d+/ig;
    var regEx=/div_post_player_\d+_([A-Za-z0-9=]+)_\d+/ig;
    var videoDiv=new Array();
    var videoIds=new Array();
    while ((arr = regEx.exec(str)) != null){
        videoDiv[videoIds.length]=arr[0];
        videoIds[videoIds.length]=arr[1];
    }
       
    if(videoIds.length==0)return;
    
    videoIdsStr=videoIds.join(",");
    //调用分级检查接口
    result=videoIdsStr;
    result='';

    if(result=='' || result==null)return;
    result=','+result+',';//前后加上个,防止冲突
    for (i=0;i<videoIds.length;i++){
        if( result.indexOf(','+videoIds[i]+',')!=-1 ){
            if($(videoDiv[i])){
                $(videoDiv[i]).innerHTML='该视频被分级';
            }
        }
    }

}


function commendVideoWin(barid){
    Nova.QBar.isLogin({},
    	function (result) {
    		if(!result){
    		    login(commendVideoWin.bind(commendVideoWin,barid));return;
    		}else{
    		    Nova.QBar.isMember({'barid':barid},
            		function (result) {
            			if(result){
            			    if(pop!=null)pop.close();
                        	pop=new Popup({contentType:1,isSupportDraging:false,isReloadOnClose:false,width:420,height:400});
                            pop.setContent("title","推荐视频");
                        	pop.setContent("contentUrl", "/bar_barCommendVideo/barid_"+barid);
                        	pop.build();
                        	pop.show();
            			}else{
            			    alert("必须是本吧成员才能推荐视频.");
            			    return false;
            			}
            		}
        		);
    		    
    		}
    	}
	);
} 
function shortCutCommendVideo(barid,videoUrl){
    Nova.QBar.isLogin({},
    	function (result) {
    		if(!result){
    		    login(shortCutCommendVideo.bind(shortCutCommendVideo,barid,videoUrl));return;
    		}else{
    		    params="act=commend&barid="+barid+"&commendvideo="+encodeURIComponent(videoUrl);
                barAction.postCommendVideo(params,1);
    		}
    	}
	);
}  


/*插入播放器*/
function play(divObj,videoId,width,height,v){
    
    if(!width)width=433;
    if(!height)height=325;
    if(!v)v=2;
    if(!window.SWFObject||!document.getElementById(divObj)){
		setTimeout("play('"+divObj+"',"+videoId+","+width+","+height+","+v+");", 2000); 
		return;
	}

	var fo = new SWFObject("http://static.youku.com"+PLAYERVERSION+"/v/swf/qplayer.swf", "movie_player", width, height, 7, "#FFFFFF");
	fo.addVariable("VideoIDS",videoId); //视频ID
	fo.addParam("allowFullScreen","true");
	fo.addVariable("isAutoPlay",false);
	fo.addParam("allScriptAccess","*");
	fo.addParam("isShowRelatedVideo","true");
	fo.addVariable("winType",'index');
	if(!fo.write(divObj)){
		$(divObj).innerHTML='<span align="center" valign="middle"><a href="http://www.youku.com/v/js/install_flash_player_ie.exe" target=_blank><img src="/v/img/download.jpg" alt="下载播放器" width="312" height="234" border="0" /></a></span>';
	}
	
}

function copyUrlToClipboard() {
	if (window.clipboardData) {   
        window.clipboardData.setData("Text",location.href);
        alert("地址已复制到系统剪切板。");
    }else{
    	alert("您使用的浏览器不支持此复制功能，请使用Ctrl+C或鼠标右键。");
    }
}


var QBar = Class.create();
/**
 * 公共js方法
 */
QBar.Common = {
    /**
    /*将按钮设为disabled
     */
    disableButton : function (){
        var buttons = document.getElementsByClassName("functionButton");        
        for(var i=0; i<buttons.length; ++i){
            buttons[i].disabled = true;
        }
    },
	/**
     * 选中所有列表选项
     */
    checkAll : function (form,isChecked,btnClassName){
        var checkBoxes = form.chkv;
        
        if(undefined == checkBoxes || checkBoxes.length == 0) return;
        if(undefined == checkBoxes.length)checkBoxes.checked = isChecked;
        // 根据chkall的选中状态设置列表其他选项的选中状态
        for(var i=0; i<checkBoxes.length; ++i){
            if(checkBoxes[i].disabled != true){
                checkBoxes[i].checked = isChecked;
            }
        }
        
        // 控制按钮是否可用
        this.checkSellist(form,btnClassName);
    },
    /**
     * 检查列表选中状态 控制按钮是否可用
     */
    checkSellist : function(form,btnClassName){
        var checkBoxes = form.chkv;
        if(undefined == checkBoxes || checkBoxes.length == 0) return;
        if(undefined !== checkBoxes.length){
            var isChecked = false;
            for(var i=0; i<checkBoxes.length; ++i){
                if(checkBoxes[i].disabled != true && checkBoxes[i].checked == true){
                    isChecked = true;
                    break;
                }
            }
        }else{
            isChecked = checkBoxes.checked;
        }
        var buttons = document.getElementsByClassName(btnClassName);        
        for(var i=0; i<buttons.length; ++i){
            buttons[i].disabled = isChecked ? false : true;
        }
        
        return isChecked;
    }
}
/**
 * 公共方法
 */
QBar.Common.pager = Class.create();
QBar.Common.pager.prototype = {
    initialize : function(baseUrl, updateId) {
        if(arguments.length !== 2 || !baseUrl || !updateId){
            QBar.Ext.alert('youku 提示！','参数不正确！');
            return;
        }
        this.params = {};
        this.params.baseUrl  = baseUrl;
        this.params.updateId = updateId;
        this.params.page  = 1;
        this.params.size  = 20;
        
    },
    /**
     * 选择列表显示数量
     */
    changeSize : function (val){
        this.params.size=val;
        this.params.page=1;//改变显示数量时，要重新从第一页显示，带着页号没有意义
        url=this.getPostUrl();
        nova_update(this.params.updateId, url, '', 'GET');
    },
    /**
     *得到post路径
     */
    getPostUrl : function (){
        var url = '';
        for(param in this.params){
            if(param!='baseUrl'){
                if(url != ''){
                    url+='&'+param+'='+this.params[param];
                }else{
                    url+=param+'='+this.params[param];
                }
            }
        }
        if(url != ''){
            url=this.params.baseUrl+"?"+url;
        }
        return url;
    }
}

QBar.Action = Class.create();
QBar.Action.prototype = {
    initialize : function(){},
    /**
    * 加入一个看吧
    */
    join : function(barid){
        Nova.QBar.isLogin({},
        	function (result) {
        		if(!result){
        		    login(barAction.join.bind(this,barid));return;
        		}else{
        		    params='barid='+barid;
                    new Ajax.Request(
                        			"/bar/ajaxJoin",
                        		    {method: "post",
                        		     parameters: params, 
                        		     onSuccess: function(o){
                        		         var result=o.responseText;
                        				 if(result==1){
                        					 alert('提交成功，等待吧主审核...');
                        					 return true;
                        				 }else if(result==-1){
                        				     alert('您还没有登录，请先登录优酷!');
                        					 return true;
                        				 }else if(result==-2){
                        				     alert('您的申请正在审核中，请耐心等待。');
                        					 return true;
                        				 }else if(result==-3){
                        				     alert('您已经是本看吧的成员了!');
                        					 return true;
                        				 }else if(result==-4){
                        				     alert('您已经是本看吧的成员了，但你的帐号被吧主锁定!');
                        					 return true;
                        				 }else{
                        				     alert('提交失败，请重试。');
                        				     return false;
                        				 }
                        		      }
                        			}
                        		);
        		}
        	}
    	);
        
    },
    /**
    * 收藏/取消一个看吧
    */
    myFavBar : function(barid){
        Nova.QBar.isLogin({},
        	function (result) {
        		if(!result){
        		    login(barAction.myFavBar.bind(this,barid));return;
        		}else{
        		    params='barid='+barid;
                    new Ajax.Request(
                        			"/bar/ajaxMyFavBar",
                        		    {method: "post",
                        		     parameters: params, 
                        		     onSuccess: function(o){
                        		         var result=o.responseText;
                        				 if(result==1){
                        					 alert('收藏成功.');
                        					 return true;
                        				 }else if(result==-1){
                        				     alert('您还没有登录，请先登录优酷!');
                        					 return true;
                        				 }else if(result==-2){
                        				     alert('您已经收藏了本吧!');
                        					 return true;
                        				 }else{
                        				     alert('提交失败，请重试。');
                        				     return false;
                        				 }
                        		      }
                        			}
                        		);
        		}
        	}
    	);
        
    },
    postCommendVideo : function (params,f){
        new Ajax.Request(
            			"/bar/barCommendVideo",
            		    {method: "post",
            		     parameters: params, 
            		     onSuccess: function(o){
            		         var result=o.responseText;
            				 if(result==1){
            					 alert('推荐成功。');
            					 if(f == "" || f == null){
            					     parent.history.go(0);
            					     closeWin();
            					 }
            					 return true;
            				 }else if(result==-1){
            				     alert('您还没有登录，请先登录优酷!');
            					 return false;
            				 }else if(result==-2 || result==-3){
            				     alert('无效看吧。');
            					 return false;
            				 }else if(result==-4 || result==-5){
            				     alert('只有本吧成员才可推荐视频。');
            					 return false;
            				 }else if(result==-6){
            				     alert('对不起，您的帐号被本吧吧主屏蔽了。');
            					 return false;
            				 }else if(result==-7){
            				     alert('请输入优酷网站视频地址。');
            					 return false;
            				 }else if(result==-8){
            				     alert('一次最多可推荐5个视频。');
            					 return false;
        					 }else if(result==-9){
            		             alert('再次推荐需间隔1分钟，请稍后再试。');
            		             closeWin();
            		             return false;
            				 }else if(result.match(/^-10\|.+$/ig)==result){
            				     errorMsg=result.split("|")[1];
            				     alert('提交完成，但出现以下错误：\n'+errorMsg);
            				     closeWin();
            					 return false;
            		         }else{
            				     alert('提交失败，请重试。');
            				     return false;
            				 }
            		      }
            			}
            		);
    }
}
var barAction=new QBar.Action;

//热点代码
window.nova_init_hook_click_charset=function(){
	Event.observe(document, "mousedown",  charset_click);
}