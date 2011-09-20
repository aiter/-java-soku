tPopWait=20;
showPopStep=5;
popOpacity=80;

sPop=null;
curShow=null;
tFadeOut=null;
tFadeIn=null;
tFadeWaiting=null;

document.write("<style type='text/css'id='defaultPopStyle'>");
document.write(".cPopText { font-family: Verdana, Tahoma; background-color: #ffffff; border: 1px #aaaaaa solid; font-size: 12px;  padding-right: 4px; padding-left: 4px; padding-top: 4px; padding-bottom: 4px; line-height: 12px; filter: Alpha(Opacity=0)}");

document.write("</style>");
document.write("<div id='popLayer' style='position:absolute;z-index:1000;' class='cPopText'></div>");


function showPopupText(){
	var o=event.srcElement;
	MouseX=event.x;
	MouseY=event.y;
	if(o.alt!=null && o.alt!="") { o.pop=o.alt;o.alt="" }
    if(o.title!=null && o.title!=""){ o.pop=o.title;o.title="" }
    if(o.pop) { o.pop=o.pop.replace("\n","<br>"); o.pop=o.pop.replace("\n","<br>"); }
	if(o.pop!=sPop) {
		sPop=o.pop;
		clearTimeout(curShow);
		clearTimeout(tFadeOut);
		clearTimeout(tFadeIn);
		clearTimeout(tFadeWaiting);	
		if(sPop==null || sPop=="") {
			popLayer.innerHTML="";
			popLayer.style.filter="Alpha()";
			popLayer.filters.Alpha.opacity=0;	
		} else {
			if(o.dyclass!=null) popStyle=o.dyclass 
			else popStyle="cPopText";
			curShow=setTimeout("showIt()",tPopWait);
		}
	}
}

function showIt() {
	popLayer.className=popStyle;
	popLayer.innerHTML=sPop;
	popWidth=popLayer.clientWidth;
	popHeight=popLayer.clientHeight;
	if(MouseX+12+popWidth>document.body.clientWidth) popLeftAdjust=-popWidth-24
		else popLeftAdjust=0;
	if(MouseY+12+popHeight>document.body.clientHeight) popTopAdjust=-popHeight-24
		else popTopAdjust=0;
	popLayer.style.left=MouseX+12+document.body.scrollLeft+popLeftAdjust;
	popLayer.style.top=MouseY+12+document.body.scrollTop+popTopAdjust;
	popLayer.style.filter="Alpha(Opacity=0)";
	fadeOut();
}

function fadeOut(){
	if(popLayer.filters.Alpha.opacity<popOpacity) {
		popLayer.filters.Alpha.opacity+=showPopStep;
		tFadeOut=setTimeout("fadeOut()",1);
	}
}
document.onmouseover=showPopupText;

// 常用
function openWin(mypage,myname,w,h,resizable)
{
    LeftPosition = (screen.width) ? (screen.width-w)/2 : 0;
    TopPosition = (screen.height) ? (screen.height-h)/2 : 0;
    settings =
    'height='+h+',width='+w+',top='+TopPosition+',left='+LeftPosition+',scrollbars=yes,resizable='+resizable;
    win = window.open('about:blank','',settings);
	win.name = myname;
    win.location = mypage;
}

// 展开
function expandIt(str)
{
	obj  = "page" + str;
	icon = "expand" + str;

	if(document.getElementById(obj).style.display == "none")
	{
		document.getElementById(icon).src = "../images/open.gif";
		document.getElementById(obj).style.display = "block";
	}
	else
	{
		document.getElementById(icon).src = "../images/close.gif";
		document.getElementById(obj).style.display = "none";
	}
}

// 全选
function checkAll(form)
{
    for (var i=0;i<form.elements.length;i++)
    {
        var e = form.elements[i];
        if (e.name != 'chkall')
        e.checked = form.chkall.checked;
    }
}

// 对输入项进行小数限制
function on_KeyPress() {
	var value=event.srcElement.value;
	var kc=event.keyCode;
	if(kc<32 || (kc>47 && kc<58) || (kc==45 && value.indexOf("\-")==-1) || (kc==46 && value.indexOf("\.")==-1)) {
		return true;
	}
	return false;
}

function on_KeyUp() {
	var obj=event.srcElement;
	var value=obj.value;
	var re=/(\..{2}).+$/;
	if(value.indexOf("\-")>0) {
		obj.value=value.replace("\-","");
	} else if(re.test(value)) {
		obj.value=value.replace(re,"$1");
	}	
}

// 跳转到指定页面
function gotoUrl(url)
{
	window.location.href = url;
	return false;
}

// 跳转到删除页
function delConfirm()
{
	return window.confirm("是否确认执行删除操作");
}


// 查看摘要长度
function checkLength(obj,dis_obj,n,note_obj)
{
	var n = parseInt(n);
    var num = obj.value.length;
    var arr = obj.value.match(/[^\x00-\x80]/ig);
    if(arr!=null)
	{
		num+=arr.length;
	}
	dis_obj.value= n - num;
	if (num > n)
	{
		note_obj.style.display = "block";
		note_obj.innerHTML = "<font color='red'>字数过长，超过部分会被截掉</font>";
	}
	else
	{
		note_obj.innerHTML = '';
		note_obj.style.display = "none";
	}
}