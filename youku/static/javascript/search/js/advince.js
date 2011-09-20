function advinced_search_submit(form,type){
	var keyword="";
	if(!type)type='video';

	var key1=form.key1.value.strip();
	key1=key1.replace(/[\/_]/g,' '); //'
	if(key1!=''){
		var key=key1.split(" ");
		for(var i=0;i<key.length;i++){
			if(key[i].strip()!='')keyword+="+"+key[i].strip();
		}
	}
	var key2=form.key2.value.strip();
	key2=key2.replace(/[\/_]/g,' '); //'
	if(key2!=''){
		var key=key2.split(" ");
		for(var i=0;i<key.length;i++){
			if(key[i].strip()!='')keyword+='+"'+key[i].strip()+'"';
		}
	}
	var key3=form.key3.value.strip();
	key3=key3.replace(/[\/_]/g,' '); //'
	if(key3!=''){
		if(key3.indexOf(" ")==-1){
			keyword+="+"+key3;
		}else{
			keyword+="+("+key3.replace(/\s+/ig," ")+")";  //"
		}
	}
	var key4=form.key4.value.strip();
	key4=key4.replace(/[\/_]/g,' '); //'
	if(key4!=''){
		var key=key4.split(" ");
		for(var i=0;i<key.length;i++){
			if(key[i].strip()!='')keyword+='-'+key[i].strip();
		}
	}
	if(keyword==''){
		alert('请输入搜索关键字!');
		return false;
	}else{
		keyword="{"+keyword+"}";
	}
	
	var fields='';
	if(!form.checkAllField.checked){
		for(var i=0;i<form.fields.length;i++){
			if(form.fields[i].checked){
				fields+=(fields==''?'':'|')+form.fields[i].value;
			}
		}
	}
	var limitdate=form.limitdate.value;
	var categories='';
	if(!form.checkAllCate.checked){
		for(var i=0;i<form.categories.length;i++){
			if(form.categories[i].checked){
				categories+=(categories==''?'':'|')+form.categories[i].value;
			}
		}
	}
	var hd=0;
	if(form.hd){
		hd=form.hd.value;
	}
	var pv=form.pv.value;
	
	var comments=0;
	if(form.comments){
		comments=form.comments.value;
	}
	var pagesize=form.pagesize.value;
	
	var viewtype=form.viewtype.value;
	setNowType(viewtype);
	
	var url="/search_"+type+"/";
	url+="q_"+encodeURIComponent(keyword);  //'
	if(fields!=''){
		url+="_fields_"+encodeURIComponent(fields);
	}
	
	url+="_limitdate_"+limitdate;
	
	if(categories!=''){
		url+="_categories_"+encodeURIComponent(categories);
	}
	if(hd>0){
		url+="_hd_"+hd;
	}
	
	if(pv>0){
		url+="_pv_"+pv;
	}
	if(comments>0){
		url+="_comments_"+comments;
	}
	if(pagesize>0){
		url+="_pagesize_"+pagesize;
	}

	if(form.openmethod.value==0){
		window.open(url);
	}else{
		location.href=url;
	}
	
}

function check_all(chkobj,listobj){
	if(!chkobj || !listobj)return false;
	for(var i=0;i<listobj.length;i++){
		listobj[i].checked=chkobj.checked;
	}
}
function check_click(chkobj,listobj){
	for(var i=0;i<listobj.length;i++){
		if(!listobj[i].checked){
			chkobj.checked=false;
			return;
		}
	}
	chkobj.checked=true;
}