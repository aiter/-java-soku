//站内搜索提示

var soku = window.soku || {};
soku.global = this;
window["Ab"] = init;
window["changeSearchType"] = changeSearchParam;
function init(isIndex, youkuSite) {// isIndex 标识是否是首页
    if (!soku) return;
    soku.Autocomplete.prototype.showContent = function () {
        this.sdiv.className = "main";
        soku.dom.show(this.sdiv);
        this.vis = true;
        this.curNodeIdx = -1;
    };  
    aa = new soku.Autocomplete(isIndex, "headq", "aa", "http://tip.soku.com/search_keys");
    if(youkuSite) {
        aa.setSugMoreParams("&site=2");   //请求数据是标识来源是站内 
        aa.setSearchParamName("/search_video/q_");
    }
    aa.setLogServer("http://lstat.youku.com");
    aa.setSearchServer("");
	aa.suggestUpdate('');
}
function changeSearchParam(searchType) {
    if(aa) aa.setSearchParamName(searchType);
}
(function (_) {	

	// 如果浏览器有trim,就用浏览器提供的
	trim = String.prototype.trim ?
		function( text ) {
			return text == null ?
				"" :
				String.prototype.trim.call( text );
		} :

		// Otherwise use our own trimming functionality
		function( text ) {
			return text == null ?
				"" :
				text.toString().replace( /^[\s\xA0]+/, "" ).replace( /[\s\xA0]+$/, "" );
		}

	//封装document.getElementById()方法, 查找不到时返回null,如果参数不合法，直接返回参数
    function g(A) {
        if ('string' == typeof A || A instanceof String) {
			return document.getElementById(A);
		} else if (A && A.nodeName && (A.nodeType == 1 || A.nodeType == 9)) {
			return A;
		}
		return null;
    }

    /**
     * 根据className获取dom对象
     * @param clannName, 要获取dom元素的class属性
     * @param element, 需要获取dom元素的上一级dom
     * @param tagName，限定dom元素的类型
     *
     */
	gc = function (className, element, tagName) {
		var result = [], 
		len, i, elements, node;

		if (!(className = trim(className))) {
			return null;
		}
		
		// 初始化element参数
		if ('undefined' == typeof element) {
			element = document;
		} else {
			element = g(element);
			if (!element) {
				return result;
			}
		}
		
		// 初始化tagName参数
		tagName && (tagName = trim(tagName).toUpperCase());
		
		// 查询元素
		if (element.getElementsByClassName) {
			elements = element.getElementsByClassName(className); 
			len = elements.length;
			for (i = 0; i < len; i++) {
				node = elements[i];
				if (tagName && node.tagName != tagName) {
					continue;
				}
				result[result.length] = node;
			}
		} else {
			className = new RegExp(
							"(^|\\s)" 
							+ className
							+ "(\\s|\x24)");
			elements = tagName 
						? element.getElementsByTagName(tagName) 
						: (element.all || element.getElementsByTagName("*"));
			len = elements.length;
			for (i = 0; i < len; i++) {
				node = elements[i];
				className.test(node.className) && (result[result.length] = node);
			}
		}

		return result;
	};
	
	//属性扩展，将源对象src的所有属性拷贝到目标对象des中
	//关于javascript的Objects和Arrays的关系，推荐阅读《JavaScript: The Definitive Guide》 Chapter 7
    _.extend = function (des, src) {
        for (var p in src) {
			if(src.hasOwnProperty(p)){
				des[p] = src[p];
			}
		}
    };
	
	
	/**
	 *	为对象绑定方法和作用域, 用另一个函数来封装原始的函数对象，将函数运行时的作用域限定为 thisObj 所指定的对象。
	 *  详细例子在scope_example.html中
	 *	类似于jquery的proxy、prototype的bind方法，关于这一方法的意义可参考：http://www.123-cha.com/prototype/api/function/bind.htm
	 *	此处还涉及到call、apply、Array.unshift以及Array.slice方法的使用，https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/function/apply
	 *	https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/function/call
	 *	https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Array/slice
	 *  https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Array/unshift
	 *  Array.prototype.slice.call(arguments, 2); 等同于 arguments.slice(2)
	 *	param B 需要绑定作用域的对象
	 *	param A 作用域
	 */	
   	_.bind = function(B, A) {
		var C = arguments.length > 2 ? Array.prototype.slice.call(arguments, 2) : null; //如果参数大于2，将第三个参数和其之后的参数放到数组C中
		return function () {
			var fn = isString(B) ? A[B] : B,
				_ = (C) ? C.concat(Array.prototype.slice.call(arguments, 0)) : arguments; //将返回函数的arguments对象变为一个数组，此处的arguments和前面那个不一样
			return fn.apply(A || fn, _);
		};
	};
	
	//判断参数是不是一个string对象
	function isString(source) {
		return '[object String]' == Object.prototype.toString.call(source);
	};
	
    _.events = {
        element: function (ev) {
            return ev.target || ev.srcElement  //捕获当前事件作用的对象，IE,event对象有srcElement属性,但是没有target属性;Firefox,event对象有target属性,但是没有srcElement属性
        },
		/**
		  * param targetDom 绑定事件的dom对象
		  * param eventType 绑定的事件类型
		  * param fn 事件触发所执行的方法
		  * param useCapture，设置为true，冒泡取消，对IE没有作用。
		  * 需要取消冒泡的只有一个地方，就是类似 “蜗居30” 后面那张出现的播放。
		  * _.events.listen(C, "mousemove", _.bind(this.mouseMoveItem, this))
		 */
        listen: function (targetDom, eventType, fn, useCapture) {
			/**
			  * Safari，Chrome对keypress, keydown的处理方式不同，尤其是方向键keyCode的值，需要处理
			  * 更多信息可参考： http://www.quirksmode.org/dom/events/keys.html
			  * 详细例子在keyevent_example.html中
			 */
            if (eventType == "keypress" && (navigator.appVersion.match(/Konqueror|Safari|KHTML/) || targetDom.attachEvent)) eventType = "keydown";
            if (targetDom.addEventListener) targetDom.addEventListener(eventType, fn, useCapture);  //非IE的事件绑定
            else if (targetDom.attachEvent) {  //IE的事件绑定
                targetDom["e" + eventType + fn] = fn;
                targetDom[eventType + fn] = function () {
                    return targetDom["e" + eventType + fn](window.event)
                };
                targetDom.attachEvent("on" + eventType, targetDom[eventType + fn])
            }            
        }
    };
	//封装相关dom操作
    _.dom = {
        visible: function (el) {  //显示调用元素
            return g(el).style.display != "none"
        },
        hide: function () { //隐藏调用元素或目标元素，可以element.hide(),也可以_dom.hide(element)
            for (var i = 0; i < arguments.length; i++) {
                var el = g(arguments[i]);
                el.style.display = "none"
            }
        },
        show: function () { //和hide作用相反
            for (var i = 0; i < arguments.length; i++) {
                var el = g(arguments[i]);
                el.style.display = "block"
            }
        },
        getHeight: function (el) { //取得整个元素的高度，包括边框。适用所有浏览器，详解http://www.quirksmode.org/dom/w3c_cssom.html
            el = g(el);  //确保_是dom元素，有offsetHeight属性
            return el.offsetHeight
        },
        addClassName: function (el, aClass) {
            if (!(el = g(el))) return;
            el.className = ("" == el.className) ? aClass : (el.className + " " + aClass)  //给目标元素增加样式
        },
        removeClassName: function (el, aClass) {  //删除目标元素样式
            if (!(el = g(el))) return;
            var aClassReg = new RegExp("(^| )" + aClass + "( |$)");
            el.className = el.className.replace(aClassReg, "$1").replace(/ $/, "")
        },
        attr: function (target, attNames, attValue) {  //给目标元素加以属性
            var tmp = attNames;
            if (typeof attNames === "string") if (attValue === undefined) return target && target.getAttribute(attNames);
            else {
                tmp = {};
                tmp[attNames] = attValue
            }
            for (var p in tmp) target.setAttribute(p, tmp[p])
        }
    };

    _.SK = {   //键盘代码
        BACKSPACE: 8,
        TAB: 9,
        RETURN: 13,
        ESC: 27,
        LEFT: 37,
        UP: 38,
        RIGHT: 39,
        DOWN: 40,
        DELETE: 46,
        PAGE_UP: 33,
        PAGE_DOWN: 34,
        END: 35,
        HOME: 36,
        INSERT: 45,
        SHIFT: 16,
        CTRL: 17,
        ALT: 18
    };
    _.Autocomplete = function (isIndex, inputId, objName, dataUrl) {  //inputId：输入框Id， objName：Autocomplete对象名，dataUrl：请求数据的url, isIndex 是否出现在首页
        if(!isIndex) {  //如果是首页就不绑定这个事件了，这个事件会导致输入框focus事件失效。首页的下拉框要在输入框focus是出了，其他页面不需要
            _.events.listen(document, "click", _.bind(this.hideOnDoc, this));  //绑定隐藏提示到document的click事件
        }
        _.events.listen(document, "blur", _.bind(this.hideOnDoc, this));   //绑定隐藏提示到document的blur事件
        this._dataCache = {}; //缓存。已经输入的词，请求过服务器的，将结果缓存下来，避免再次请求
        this.objName = this.defSugName; //Autocomplete对象名字，要和服务端返回的脚本的对象名一致
        if (objName) this.objName = objName;
        this.isIE = (navigator && navigator.userAgent.toLowerCase().indexOf("msie") != -1);  //是否是IE浏览器
        this.box = document.getElementById(inputId);
        _.events.listen(this.box, "keypress", _.bind(this.onkeydown, this));
        var B = this; //保存this，为了在box的onblur里使用hide方法。因为到下面的funciton里面，this就变了。
        
        this.VideoWall = _.global["VideoWall"];
        this.focusCount = 0;
        this.box.onblur = function () {
            if(isIndex) {
                              //if(VideoWall){setTimeout(function(){VideoWall.show()}, 500);}
                if(VideoWall){VideoWall["show"]();}
          }
            B.hide();
        };
        //_.events.listen(this.box, "click", _.bind(this.reBindBoxEvent, this));
        if(isIndex){
            _.events.listen(this.box, "focus", _.bind(this.focus, this)); //
        }else {
	        _.events.listen(this.box, "dblclick", _.bind(this.dbClick, this));
        }
        this.count = 0;
        this.sugServ = this.defSugServ;  //数据服务器地址
        this.sugServUrlPost = this.S_QUERY_URL_POST; //传给服务器的参数
        if (dataUrl) this.sugServ = dataUrl;  //如果构造函数传入服务器地址，就用构造函数传入的值
        this.sugMoreParams = "";
        this.logServ = this.defSugServ;  //日志服务器地址
        this.logServUrlPost = this.S_LOG_URL_POST; //传给日志服务器的参数
        this.searchServ = this.defSearchServ;  //搜索服务器地址，也就是www.soku.com，用于搜索选中的提示词
        this.searchParamName = this.defSearchParamName; //搜索参数
        this.searchMoreParams = "";
        this.kf = this.defKeyfrom + this.KEYFROM_POST;
        this.openInNewWindow = false;  //链接是否在新窗口打开，由于跳转规则不一致，有的地方target='_blank'可能直接写到链接里了，也就是说这个参数并不能控制所有链接的跳转规则
        this.sugFlag = true;  //是否出现下拉提示，备用，需要设置不显示提示时用。
        this.clickEnabled = true;
        this.sptDiv = document.createElement("div");  //sptDiv,存放生成的script。涉及到javascript的跨域问题，这里是通过script标签解决的。
        document.body.appendChild(this.sptDiv);
        this.sdiv = document.createElement("div");  //sdiv，存放生成的提示内容
        /*this.sdiv.style.position = "absolute";
        this.sdiv.style.zIndex = 10000; */
        _.dom.hide(this.sdiv);
        document.getElementById("kubox").appendChild(this.sdiv); //将生成的提示内容附加到指定的dom节点上
		this.ddiv = document.createElement("div");  //ddiv， 存放对应的直达区内容，即提示词为人名或剧名时，右侧多出来的内容
        //document.getElementById("kubox").appendChild(this.ddiv);
        this.ddiv.className = "panels"; //设置ddiv的样式。
		this.adiv = document.createElement("div");  //最下面那个所有搜索结果
		this.adiv.className = "all";
		document.getElementById("kubox").appendChild(this.adiv);
        this.bdiv = document.createElement("div"); //临时存放生成的提示内容，完成时用来替换旧的提示。
        this.vis = false;  //提示区域是否可见
        this.lastUserQuery = "";  //上一次的查询词，如果和本次一样，就将隐藏的提示框显示出来
        this.initVal = "";  //查询词默认值
        if (this.box && this.box.value != "") this.initVal = this.box.value;
        this.curUserQuery = this.initVal; //当前的查询词，这个词不一定是输入框中的值，而是到服务器请求提示结果的那个词。输入框中的词会随着键盘方向下变化的
        this.upDownTag = false;
		
        this.focusShow = true;  //首页重新获得焦点是否显示
		this.keyboardFocus = -1; //记录键盘的焦点位置，-1表示键盘焦点位于左侧提示词的位置，键盘方向的上下键选择不同的提示词。这一值大于或等于0是表示键盘焦点位于右侧直达区，键盘方向的上下键选择不同的节目。
		this.ddivCureItemIdx = 0; //键盘位于直达区焦点是，高亮的item序号
		this.ddivItemCount = 0; //直达区栏目的个数
        if(isIndex) {
            this.canplay = document.getElementById("canplay");
             _.events.listen(this.canplay, "mouseover", _.bind(this.removeBoxBlur, this)); 
            _.dom.hide(this.canplay);
        }
        this.isIndex = isIndex;
        window.onresize = _.bind(this.winResize, this);
        this.clean();  //初始化数据，将对应的内容清空
        this.site = 1; //日志表示，1表示soku全网搜索， 2表示soku站内搜索
        if (this.box) this.timeoutId = setTimeout(_.bind(this.sugReq, this), this.REQUEST_TIMEOUT);
    };
    _.extend(_.Autocomplete.prototype, {
        start: function () {
            if (this.timeoutId != 0) clearTimeout(this.timeoutId);
            this.timeoutId = setTimeout(_.bind(this.sugReq, this), this.REQUEST_TIMEOUT);
            if (this.box && this.box.value != "") {
                this.initVal = this.box.value;
                this.curUserQuery = this.initVal
            }
        },
        setLogSite: function(site) {
            this.site = site;
        },
        setVideoWall: function(obj) {
            this.VideoWall = obj;
        },
        setObjectName: function (objName) {
            this.objName = objName
        },
        setSugServer: function (url, str) {
            this.sugServ = url;
            if (str) this.sugServUrlPost = str;
            this.clean()
        },
        setSugMoreParams: function (param) {
            this.sugMoreParams = param 
        },
        setLogServer: function (url, str) {
            this.logServ = url;
            if (str) this.logServUrlPost = str
        },
        setSearchServer: function (url) {
            this.searchServ = url
        },
        setKeyFrom: function (str) {
            if (str.indexOf(this.KEYFROM_POST) > 0) this.kf = str;
            else this.kf = str + this.KEYFROM_POST
        },
        setOpenInNewWindow: function () {
            this.openInNewWindow = true
        },
        setSearchParamName: function(str) {
            this.searchParamName = str
        },
        getSearchUrl: function (url) {
            return encodeURI(this.searchServ + this.searchParamName + url)
        },
        getSugQueryUrl: function (queryStr) {
            return encodeURI(this.sugServ + this.sugServUrlPost + queryStr + this.sugMoreParams + this.hour())
        },
        /*
        clicklog: function (selectType, curUserQuery, nodeIdx, curQuery) {//curUserQuery:输入框内的词，即请求服务器的查询词。curQuery用户点击的提示词。
            var logStr = "";
            if (curUserQuery) logStr += curUserQuery;
            if (nodeIdx) logStr += nodeIdx;
            if (curQuery) logStr += curQuery;
            var E = new Image();
            E.src = encodeURI(this.logServ + this.logServUrlPost + selectType + logStr + this.time());
            return true
        },*/
        clicklog : function(type, pos, keyword, url) {
            if(_.global["sokuClickStat"]) {
               /* _.global["logParam"]["site"] = this.site;
                _.global["logParam"]["type"] = type;
                _.global["logParam"]["pos"] = pos;
                _.global["logParam"]["site"] = this.site;
                */
                var logParam = new Object();
                logParam["_log_type"] = type;
                logParam["_log_site"] = this.site;
                logParam["_log_pos"] = pos;
                logParam["keyword"] = keyword;
                if(!url) url = _.global["location"]["href"];
                logParam["_log_url"] = url;
                _.global["sokuClickStat"](logParam);
            } else {
                var logStr = "";
                if(type) logStr += type;
                if(pos) logStr += "&pos=" + pos;
                if(keyword) logStr += "&keyword=" + keyword;
                if(!url) url = _.global["location"]["href"];
                if(url) logStr += "&url=" + encodeURIComponent(url); 
                var e = new Image();
                var rand = Math.round(Math.random() * 2147483647);
                e.src = encodeURI(this.logServ + this.logServUrlPost + logStr + "&site=" + this.site + "&rand" +  rand);
            }
        },
        reBindBoxEvent: function() {
            //if(this.box.onblur == null) {
                var tmp = this;
                this.box.onblur = function () {
                    if(tmp.isIndex) {
                        //if(this.VideoWall){setTimeout(function(){this.VideoWall.show()}, 500);}
                        if(tmp.VideoWall){tmp.VideoWall["show"]();}
                    }
                    tmp.hide();
                };
            //}
        },
        dbClick: function () {  //双击输入框的回调函数
            if (this.box.createTextRange) {
                var boxTextRange = this.box.createTextRange();
                boxTextRange.moveStart("character", 0);
                boxTextRange.select()
            } else if (this.box.setSelectionRange) this.box.setSelectionRange(0, this.box.value.length);
            if (this.sugFlag) {
                if (this.box.value != "") if (this.lastUserQuery == this.box.value) {  //输入内容为空，或者内容没有变化，不去服务器请求数据
					if(this.sdiv.innerHTML != "") {
						this.show();                   
						return
					}
                } else this.doReq()
            }
        },
		
		focus: function () { //输入框或得焦点的回调函数
            this.reBindBoxEvent();
            if(this.isIndex) {
                 if(navigator.userAgent.match(/msie/i)) {
                    if(this.focusCount > 0) {
                        if(this.VideoWall){this.VideoWall["hide"]();}

                    }
                 } else {
                    if(this.VideoWall){this.VideoWall["hide"]();}
                 }
            }
            this.focusCount++;
            if (this.sugFlag && this.focusShow) {
                /*if(this.isIndex) {
                    if(this.box.value != "" && this.lastUserQuery == this.box.value && this.sdiv.innerHTML != "") {
                        this.show();
                        return
                    }else {
                        this.doReq();
                    }
                } else {
                }*/  //去掉首页默认下拉提示，输入框内没有内容不再请求数据

                    if (this.box.value != "") if (this.lastUserQuery == this.box.value) {
    					if(this.sdiv.innerHTML != "") {
    						this.show();                   
    						return
    					}

                    } else this.doReq()
            }
        },
		
		suggestFocus: function() {   //判断键盘焦点是不是在右侧区域
			return this.keyboardFocus != -1;  
		},
		
        winResize: function () {
            if (this.vis) this.show()
        },
        onkeydown: function (A) {  //键盘事件处理
            if(this.isIndex) {
                    if(this.VideoWall){this.VideoWall["hide"]();}
            }

            if (A.ctrlKey) return true;
            var $ = _.SK;
            switch (A.keyCode) {
            case $.PAGE_UP:
            case $.PAGE_DOWN:
            case $.END:
            case $.HOME:
            case $.INSERT:
            case $.CTRL:
            case $.ALT:
            case $.LEFT:
		    if(this.suggestFocus()) {  //如果键盘焦点在右侧，通过方向键←，将焦点移回左侧
			    this.keyboardFocus = -1;
                this.hlOffItem();
                /*
                var endPos = this.box.value.length;
			    if(this.box.createTextRange){
				    var r = this.box.createTextRange();
				    r.moveEnd("character", endPos);
                    return false;
			    } else if(this.box.setSelectionRange){
                    this.box.setSelectionRange(endPos, endPos);
                    return false;
			    }*/
		    }
		    return true;
            case $.RIGHT:
				//var l = this.getCurNode().getAttribute(this.ITEM_LINK);
				var cur_items = document.getElementById("ddiv" + this.curNodeIdx); 
				if(cur_items) {  //如果当前节点是一个有右侧详情区的节点，方向键→，将键盘焦点移到右侧。同时将一些标识赋值
					//this.ddivCurItems = cur_items.getElementsByClassName("item");
                    this.ddivCurItems = gc("item", cur_items);
                    this.ddivItemCount = this.ddivCurItems.length;
					this.keyboardFocus = this.curNodeIdx;
                    this.ddivCurItemIdx = 0;
                    this.hlOnItem();
					return true;
				}
				var l = this.getElemAttr(this.getCurNode(), this.ITEM_LINK);
				var curQueryWord = this.getElemAttr(this.getCurNode(), this.ITEM_QUERY);
				if(l && l.length > 0) {  //如果这个提示词是电视剧的一集，如蜗居1， 方向键→将直接播放
                    this.clicklog(4, 3, curQueryWord, l);
                    window.location = l;
                    //window.open(l, "_blank");
					return false;
				} else if (this.box.value != "") if (this.lastUserQuery == this.box.value) {            
                    return true;
                } // else this.doReq();  为了让方向右键的时间一致，取消了无直达区词方向右键再次请求数据
				return true;
            case $.SHIFT:
            case $.TAB:
                return true;
            case $.ESC:
                this.focusShow = false;
                this.hide();
                return false;
            case $.UP:
				if (this.vis) {  // 如果下拉提示可见，方向键↑的作用是移动键盘焦点
						if(this.suggestFocus()) { //如果此时键盘焦点在右侧详情区域，方向键↑在详情区内移动
                            this.itemUp();
						} else {  //否则 在提示词区域内移动
							this.upDownTag = true;
							this.up()
						}						
					} else {  //如果下拉提示不可见，方向键↑的作用就是让下拉提示可见
						if (this.sdiv.childNodes.length > 1) if (this.lastUserQuery == this.box.value) if (this.sugFlag) {
							this.show();
							return false
						}
						if (this.box.value != "") this.doReq()
					}
					if (this.isIE) A.returnValue = false;
					else A.preventDefault();
					return false;				              
            case $.DOWN:  //方向键↓， 和方向键上一致
                if (this.vis) {
                    if(this.suggestFocus()) {
                        this.itemDown();
                    } else {
                        this.upDownTag = true;
                        this.down()
                    }
                } else {
                    if (this.sdiv.childNodes.length > 1) if (this.lastUserQuery == this.box.value) if (this.sugFlag) {
                        this.show();
                        return false
                    }
                    if (this.box.value != "") this.doReq()
                }
                if (this.isIE) A.returnValue = false;
                else A.preventDefault();
                return false;
            case $.RETURN:   //回车键
                if(this.suggestFocus()){  //如果此时键盘焦点位于右侧详情区，回车确认跳转到选中节目的详情页
				    var curQueryWord = this.getElemAttr(this.getCurNode(), this.ITEM_QUERY);
                    this.clicklog(4, 2, curQueryWord, this.ddivCurItemHref);    
                    window.location.href = this.ddivCurItemHref;
                    if (this.isIE) A.returnValue = false;
                    else A.preventDefault();
                    return false;
                }
                if (this.vis && this.curNodeIdx > -1) if (!this.select()) {  //如果键盘焦点位于正常的下拉提示，回车键将搜索该词
                    if (this.isIE) A.returnValue = false;
                    else A.preventDefault();
                    return false
                }
                //return true;
            case $.BACKSPACE:
                if (this.box.value.length == 1) this.curUserQuery = "";
            default:
                this.upDownTag = false;
                return true
            }
        },
        sugReq: function () {
           if (document.activeElement && document.activeElement != this.box);
            else if (this.box.value != "" && this.box.value != this.initVal) {
                if (this.lastUserQuery != this.box.value) if (!this.upDownTag) this.doReq()
            } else if (this.lastUserQuery != "") {
                this.lastUserQuery = "";
                if (this.vis) {
                    this.hide();
                    this.clean()
                }
            }
            if (this.timeoutId != 0) clearTimeout(this.timeoutId);
            this.timeoutId = setTimeout(_.bind(this.sugReq, this), this.REQUEST_TIMEOUT)
        },
		jumpto: function (keyword, url, el) {  //取消目标元素的冒泡，主要用在电视剧的直接播放处
			//window.event.cancelBubble = true; 
			if (el.stopPropagation){          
				 el.stopPropagation();    
			} else {
				el.cancelBubble = true;   
			}
            this.clicklog(4, 3, keyword, url);
		},
        urlgodetail: function(keyword, url) {
            this.clicklog(4, 2, keyword, url);
        },
        urlgoplay: function(keyword, url) {
            this.clicklog(4, 3, keyword, url);
        },
        urlgodirectplay: function(keyword, url) {
            this.clicklog(5, 1, keyword, url);
        },
        select: function (el) {  //选中下拉提示的某个词, 需要记日志
            if (el) var selectType = this.LOG_MOUSE_SELECT;  //如果有参数，说明是鼠标点击
            else selectType = this.LOG_KEY_SELECT; //否则就是键盘
			var CC = this.getCurNode();
            if (CC) {                
                var nodeType = this.getCurNode().getAttribute(this.ITEM_TYPE),
                    curQuery = this.getElemAttr(this.getCurNode(), this.ITEM_QUERY);
					
					
				try {	
						
                        if (el) {
                            //this.clicklog(selectType, "&q=" + this.curUserQuery, "&index=" + this.curNodeIdx, "&select=" + curQuery);
                            this.curUserQuery = curQuery;
                            if (!this.openInNewWindow) this.box.value = curQuery 
                        } else {
                            if (this.box.value != curQuery) return true;
                            //this.clicklog(selectType, "&q=" + this.curUserQuery, "&index=" + this.curNodeIdx, "&select=" + curQuery);
                            this.curUserQuery = curQuery;
                            curQuery = this.box.value
                        }
                        this.clicklog(4, 1, curQuery);  //选中提示词搜索，发个logstat的url就是当前地址栏的url
                        this.hide();
                        var A = this.getSearchUrl(curQuery);
                        if (this.openInNewWindow) window.open(A, "_blank");
                        else document.location = A
                    } catch ($) {}	
				
            }
            return false
        },
        doReq: function () {
            if (!this.sugFlag) return;
            this.initVal = "";
            var curInput = this.box.value;
            this.curUserQuery = curInput;
            var queryStr = curInput;
            this.lastUserQuery = curInput;
            //console.log("cur query" + $ +"|");
			if(queryStr.length > 15) return;  //超过15个字不再请求
            if (this._dataCache[queryStr]) {
                //console.log("cached yes");
                this.suggestUpdate(this._dataCache[queryStr]);
                return
            }
            var dataUrl = this.getSugQueryUrl(queryStr);
            this.excuteCall(dataUrl, queryStr)
        },
        clean: function () {
            this.size = 0;
            this.curNodeIdx = -1;
            this.sdiv.innerHTML = "";
            this.bdiv.innerHTML = ""
        },
        onComplete: function () {
            setTimeout(_.bind(this.updateContent, this), 5)
        },
        cleanScript: function () {
            while (this.sptDiv.childNodes.length > 0) this.sptDiv.removeChild(this.sptDiv.firstChild)
        },
        isValidNode: function (el) {
            return (el.nodeType == 1)
        },
        getReqStr: function (el) {
            if (el && el.getElementsByTagName("div").length > 0) return this.getElemAttr(el.getElementsByTagName("div")[0], this.QUERY_ATTR);
            return null
        },
        getElemAttr: function (el, _) {
			if(!el || !el.getAttribute(_))
				return;
            return this.unescape(el.getAttribute(_))
        },
        updateContent: function () {  //更新下拉提示，并绑定相应事件
            this.cleanScript(); //清空用于请求数据的script
            var curInput = this.box.value;

            if (this.bdiv.innerHTML == "") if (this.sdiv.innerHTML != "" && this.getReqStr(this.sdiv) == curInput) return;
            else {
                this.hide();
                this.clean();
                return
            }
            if (this.getReqStr(this.bdiv) != curInput && curInput != "") if (this.sdiv.innerHTML != "" && this.getReqStr(this.sdiv) == curInput) return;
            else {
                this.hide();
                return
            }
			
		   var node, valid = false,
                suggestNodes = (((this.bdiv.getElementsByTagName("div"))[0]).getElementsByTagName("li"));
            for (var i = 0; i < suggestNodes.length; i++) {
                node = suggestNodes[i];
                if (this.isValidNode(node)) {
                    valid = true;
                    break
                }
            }
            if (valid) {
                this.sdiv.innerHTML = this.bdiv.innerHTML;
                var suggestDivs = this.sdiv.getElementsByTagName("div");
                suggestNodes = suggestDivs[0].getElementsByTagName("li");

                this.size = 0;
                this.childs = new Array();
                for (i = 0; i < suggestNodes.length; i++) {  //给每一个提示词的节点绑定相应的事件
                    node = suggestNodes[i];
                    if (this.isValidNode(node)) {
                        node.setAttribute(this.ITEM_INDEX, this.size);
                        _.events.listen(node, "mousemove", _.bind(this.mouseMoveItem, this));
                        _.events.listen(node, "mouseover", _.bind(this.mouseOverItem, this));
                        _.events.listen(node, "mouseout", _.bind(this.mouseOutItem, this));
                        
						_.events.listen(node, "click", _.bind(this.select, this));
						var AA = node.getElementsByTagName("a");
                        var logKeyword = this.getElemAttr(node, this.ITEM_QUERY); 
						if(AA[0]) {  //如果该提示词是类似于蜗居10这种词，里面会有播放链接，由于li上有click事件，需要给这个链接绑定一个防止冒泡，并且触发默认行为的事件
							_.events.listen(AA[0], "click", _.bind(this.jumpto, this, logKeyword, AA[0].href), true);
                           // _.events.listen(AA[0], "click", _.bind(this.jumpto, this), true);
						} 
						if(AA[1]) {
							_.events.listen(AA[1], "click", _.bind(this.jumpto, this, logKeyword, AA[0].href), true);
							//_.events.listen(AA[1], "click", _.bind(this.jumpto, this), true);
						} 
						
                        this.childs.push(node);
                        this.size++
                    }
                }
				/*var F = this.ddiv.getElementsByTagName("A");
				if(F.length == 0) F = this.ddiv.getElementsByTagName("a");
				//console.log(F);
				if(F) {
					for(var I = 0; I < F.length; I++) {
						var G = F[I];
						//console.log(G);
					    _.events.listen(G, "mouseover", _.bind(this.removeBoxBlur, this));
                        _.events.listen(G, "mouseout", _.bind(this.revertBoxBlur, this));
					}
				}*/
				
				//console.log($);
				//console.log($.length);
                this.show();
                this.canMouseOver = false;
                var itemSize = 0;
				for(var i = 0; i < 10; i++) {
			            var d = document.getElementById("ddiv" + i);
				    if(d) {
					    _.events.listen(d, "mouseover", _.bind(this.removeBoxBlur, this));  //鼠标移到右侧详情区是，需要把输入框的onblur事件取消，否则绑定到右侧直达区的所有事件都不起作用
					    _.events.listen(d, "mouseout", _.bind(this.revertBoxBlur, this));

                        var items = gc("item", d);
                        itemSize += items.length;
                        
                        /**
                         * 关于mouse event，推荐看下这http://www.quirksmode.org/js/events_mouse.html
                         * 由于event bubbling的存在mouseouve、mouseout这种嵌套的div，比较麻烦。
                         * Microsoft的IE有一个比较好的解决方案，就是mouseenter、mouseleave事件。
                         * 而其他浏览器，就没什么好办法了。我的实现可参考972行左
                         * 右关于mouseOverDdivItem的注释。这种实现目前还存在一个
                         * bug，鼠标移动过快时，不能触发mouseout。
                         */
                        if(d.attachEvent) {
                            for(var j = 0; j < items.length; j++) {
                                _.events.listen(items[j], "mouseenter", _.bind(this.mouseEnterDdivItem, this));
                                _.events.listen(items[j], "mouseleave", _.bind(this.mouseLeaveDdivItem, this));
                            }
                        } else {
                            for(var j = 0; j < items.length; j++) {
                                _.events.listen(items[j], "mouseover", _.bind(this.mouseOverDdivItem, this));
                                _.events.listen(items[j], "mouseout", _.bind(this.mouseOffDdivItem, this));
                            }
                        }
                        var logKeyword = this.getElemAttr(d, "itemq");
                        for(var j = 0; j < items.length; j++) {
                            var baseinfo = gc("baseinfo", items[j]);
                            var thumb = gc("thumb", items[j]);
                            var curLogUrl = baseinfo[0].getElementsByTagName('a')[0].href;
                            if(baseinfo) {
                                 _.events.listen(baseinfo[0].getElementsByTagName('a')[0], "click", _.bind(this.urlgodetail, this, logKeyword, curLogUrl));
                                _.events.listen(thumb[0].getElementsByTagName('img')[0], "click", _.bind(this.urlgodetail, this, logKeyword, curLogUrl));
                            }

                            var play = gc("play", items[j]);
                            if(play) {
                                _.events.listen(play[0].getElementsByTagName('a')[0], "click", _.bind(this.urlgoplay, this, logKeyword, play[0].getElementsByTagName('a')[0].href));
                            }
                        }
				    }
				}
				
				var mainDiv = gc("main", document.getElementById("kubox"))[0];
				if(itemSize > 0) {
					mainDiv.style.height = "299px";
				} else {
					mainDiv.style.height = "" //清空main div的高度，让整体高度自适应
				}
							 
                /*
				var a = this.adiv.getElementsByTagName("a");
				if(a[0]) {
					_.events.listen(a[0], "mouseover", _.bind(this.removeBoxBlur, this));
					_.events.listen(a[0], "mouseout", _.bind(this.revertBoxBlur, this));
				}*/
                var defaultShowDdiv = g("ddiv0");
                if(defaultShowDdiv) {
                    _.dom.show(defaultShowDdiv);
                    _.dom.show(defaultShowDdiv.parentNode);
                    this.lazyLoadDdivImg(defaultShowDdiv);
                }
            } else {
                this.hide();
                this.clean()
            }
			
			
        },
        showContent: function () {
            //var $ = _.dimension.cumOffset(this.box);
            /* this.sdiv.style.top = $[1] + (this.box.offsetHeight - 1) + "px";
            this.sdiv.style.left = $[0] + "px";
            this.sdiv.style.cursor = "default";
            this.sdiv.style.width = this.box.offsetWidth + "px"; */
            _.dom.show(this.sdiv);

            this.vis = true;
            this.curNodeIdx = -1
        },
        show: function () {
			_.dom.show(g("kubox"));
			var kb = g("kubox");
            if (this.sdiv.childNodes.length < 1) return;
            if (this.sugFlag) if (this.box.value != "" && this.getReqStr(this.sdiv) != this.box.value) return;
            this.showContent()
        },
        hide: function () {
            this.hlOff();
            _.dom.hide(this.sdiv);
            //_.dom.hide(this.ddiv);
			_.dom.hide(g("kubox"));
            this.curNodeIdx = -1;
            this.vis = false
        },
        hideOnDoc: function () {
            if (this.clickEnabled) {
                this.hide();
                this.clickEnabled = false;
                setTimeout(_.bind(this.enableClick, this), 60)
            }
        },
        enableClick: function () {
            this.clickEnabled = true
        },
        mouseMoveItem: function ($) {
            this.canMouseOver = true;
            this.mouseOverItem($)
        },
        mouseOverItem: function (A) {
            this.removeBoxBlur();
            if (!this.canMouseOver) {
                this.canMouseOver = true;
                return
            }
            var B = _.events.element(A);
            while (B.parentNode && (!B.tagName || (B.getAttribute(this.ITEM_INDEX) == null))) B = B.parentNode;
            var $ = (B.tagName) ? B.getAttribute(this.ITEM_INDEX) : -1;
            if ($ == -1 || $ == this.curNodeIdx) return;
            this.hlOff();
            this.curNodeIdx = Number($);
            this.hlOn(false)
        },
        mouseOutItem: function () {
            this.hlOff();
            this.curNodeIdx = -1;
            this.revertBoxBlur()
        },
        getNode: function ($) {
            if (this.childs && ($ >= 0 && $ < this.childs.length)) return this.childs[$];
            else return undefined
        },
        getCurNode: function () {
            return this.getNode(this.curNodeIdx)
        },
        hover: function ($, _) {
            if (!$) this.box.value = _
        },
        lazyLoadDdivImg: function(d) {
					//图片延时加载。因为某些词的所生成的下拉菜单会有很多图，如“张”，提示的人物“张学友”，“张卫健”等，差不多能有10张图，会给下拉菜单带来200ms的延迟。
					//因此生出下拉菜单是，img标签没有设置src属性，而是设置了个_src，此处统一将_src属性替换为src
					var l = d.getElementsByTagName('img');
					for(var j = 0; j < l.length; j++){
						l[j].setAttribute('src', l[j].getAttribute('_src'));
					}				
        },
        hlOn: function (B) {
			var C = this.getCurNode();
            var curQueryWord;
            if (C) {
                //var C = curNode.getElementsByTagName("li");
                _.dom.addClassName(C, this.ITEM_HIGHLIGHT_STYLE);
                try {
                    //var A = C.innerHTML;
					curQueryWord = this.getElemAttr(C, this.ITEM_QUERY);
                    this.hover(!B, curQueryWord);
                    var curPlayUrl = this.getElemAttr(C, this.ITEM_LINK);
                    if(B) {  //如果是键盘事件，则改变立刻播放按钮，因为此时输入框的值也变了
                        this.updateCanPlayDiv(curPlayUrl);
                    }
					//this.updateBottomUrl(curQueryWord);
                } catch ($) {}
            }

			for(var i = 0; i < 10; i++) {   //键盘或鼠标移到有右侧详情区的li，展示该详情区，同时根据当前li的位置计算右侧详情区的位置
				var d = g("ddiv" + i);

				if(!d) continue;
				if(i == this.curNodeIdx) {
					_.dom.show(d);
					_.dom.show(d.parentNode);
				
		            this.lazyLoadDdivImg(d);
					var maxMove = (289 - d.getAttribute('itemC') * 90);  //右侧详情区下移最大距离
		//			console.log("d.itemc" + d.getAttribute('itemC'));
		//			console.log("maxMove" + maxMove);
					var m = d.getElementsByTagName('div');
					for(var k = 0; k < m.length; k++) {
						if(m[k] && m[k].className && m[k].className == 'item') {
                            var offSetCount = this.curNodeIdx;
							var itemTopOffSet = ((offSetCount * 28) > maxMove ? maxMove : (offSetCount * 28));
         //                   console.log("itemTopOffSet: " + itemTopOffSet);
							m[k].style.top = itemTopOffSet + "px";
		//					console.log(m[k].style.top);
						}
					}				
				}
				else {
                    this.keyboardFocus = -1;
					_.dom.hide(d);
				}
			}
        },
        hlOff: function () {
			var C = this.getCurNode();
            if (C) {
               // var $ = this.getCurNode().getElementsByTagName("li");
                //for (var A = 0; A < $.length; ++A)
				_.dom.removeClassName(C, this.ITEM_HIGHLIGHT_STYLE);

            }		
        },  
        up: function () {
            var $ = this.curNodeIdx;
            if (this.curNodeIdx > 0) {
                this.hlOff();
                this.curNodeIdx = $ - 1;
                this.hlOn(true)
            } else if (this.curNodeIdx == 0) {
                this.hlOff();
                this.curNodeIdx = $ - 1;
                this.box.value = this.curUserQuery;
				//this.updateBottomUrl(this.curUserQuery)
            } else {
                this.curNodeIdx = this.size - 1;
                this.hlOn(true)
            }
        },
        down: function () {
            var $ = this.curNodeIdx;
            if (this.curNodeIdx < 0) {
                this.curNodeIdx = $ + 1;
                this.hlOn(true)
            } else if (this.curNodeIdx < (this.size - 1)) {
                this.hlOff();
                this.curNodeIdx = $ + 1;
                this.hlOn(true)
            } else {
                this.hlOff();
                this.curNodeIdx = -1;
                this.box.value = this.curUserQuery;
				//this.updateBottomUrl(this.curUserQuery)
            }
        },
		itemUp: function() {
			var i = this.ddivCurItemIdx;

			if(this.ddivCurItemIdx > 0) {
				this.hlOffItem();
				this.ddivCurItemIdx = i - 1;
				this.hlOnItem();
			} else if(this.ddivCurItemIdx == 0) {
				this.hlOffItem();
				this.ddivCurItemIdx = this.ddivItemCount - 1;
				this.hlOnItem();
			}
		},
		itemDown: function() {
			var i = this.ddivCurItemIdx;
			if(this.ddivCurItemIdx < (this.ddivItemCount - 1)) {
				this.hlOffItem();
				this.ddivCurItemIdx = i + 1;
				this.hlOnItem();
			} else if(this.ddivCurItemIdx == (this.ddivItemCount - 1)) {
				this.hlOffItem();
				this.ddivCurItemIdx = 0;
				this.hlOnItem();
			}
		},
        getCurItem: function() {
                return this.ddivCurItems[this.ddivCurItemIdx];
        },
		hlOnItem: function() {
			var I = this.getCurItem();
			var baseinfo = gc("baseinfo", I)[0];
			
			this.ddivCurItemHref = baseinfo.getElementsByTagName("a")[0].href;
            _.dom.addClassName(I, "current");
		},
        hlOffItem: function() {
            var I = this.getCurItem();
            _.dom.removeClassName(I, "current");
        },
        /**
         * 右侧区域鼠标滑过回调函数
         * 关于mouseover, mouseout事件，由于event bubbling的存在，在这种多层嵌套的结构中处
         * 理起来比较麻烦，详细资料参考http://www.quirksmode.org/js/events_mouse.html
         * 这的处理方式是从事件触发的dom节点向上查找，找到目标div后，改变其样式
         */
        mouseOverDdivItem: function(e) {  
          var targetElement = _.events.element(e);
          while(targetElement && targetElement.nodeName != 'BODY') {
              targetElement = targetElement.parentNode;
              if(targetElement.className == "item") {
                  _.dom.addClassName(targetElement, "current");
              }
          }
        },
        mouseOffDdivItem: function(e) {
          var targetElement = _.events.element(e);
          while(targetElement && targetElement.nodeName != 'BODY') {
              targetElement = targetElement.parentNode;
              if(targetElement.className.indexOf("item") > -1) {
                  break;
              }
          }

           var relatedTarget = (e.relatedTarget) ? e.relatedTarget : e.toElement;
          /* while(relatedTarget != targetElement && relatedTarget.nodeName != 'BODY') {
               console.log(relatedTarget.nodeName);
               relatedTarget = relatedTarget.parentNode;
           }*/

           if(relatedTarget != targetElement) return;
           _.dom.removeClassName(targetElement, "current"); 
        },
        mouseEnterDdivItem: function(e) {
            var targetElement = _.events.element(e);
            _.dom.addClassName(targetElement, "current");
        },
        mouseLeaveDdivItem: function(e) {
            var targetElement = _.events.element(e);
            _.dom.removeClassName(targetElement, "current");
        },
        excuteCall: function (url, q) {
            var scriptDom = document.createElement("script");
            scriptDom.src = url;
            this.sptDiv.appendChild(scriptDom)
        },
        unescape: function ($) {
            return $.replace(new RegExp("&quot;", "gm"), "\"").replace(new RegExp("&gt;", "gm"), ">").replace(new RegExp("&lt;", "gm"), "<").replace(new RegExp("&amp;", "gm"), "&")
        },
        escape: function ($) {
            return $.replace(new RegExp("&", "gm"), "&amp;").replace(new RegExp("<", "gm"), "&lt;").replace(new RegExp(">", "gm"), "&gt;").replace(new RegExp("\"", "gm"), "&quot;")
        },
        subLink: function ($) {
            if ($.length <= 43) return $;
            return $.substr($, 40) + "..."
        },
		/**
		  *此处是根据返回的结果，构建下拉提示。
		  *对于返回json的格式，和每个字段的意义下面会有详细说明：
		  *		{q:'我',r:[{c:'我是特种兵', d:1, u:[{o:'0',k:'http://v.youku.com/v_show/id_XMjQ0ODk0NTAw.html',m:'25集电视剧 Fri Jan 14 00:00:00 CST 2011',n:'25集全 ， 高清电视剧',p:'谷智鑫,徐佳,侯勇'}]},{c:'我们约会吧', d:1, u:[{o:'0',k:'http://v.youku.com/v_show/id_XMjQ4MTU1MTQ4.html',m:'null',n:'null',p:'丹尼斯·吴,吴辰君,李菲儿'}], l:'http://v.youku.com/v_show/id_XMjQ4MTU1MTQ4.html', t:1},{c:'我们结婚了'},{c:'我爱记歌词'},{c:'我叫mt'},{c:'我知女人心', d:1, u:[{o:'0',k:'http://v.youku.com/v_show/id_XMjQ4MjI0OTMy.html',m:'null',n:'null',p:'刘德华,巩俐,胡静'}], l:'http://v.youku.com/v_show/id_XMjQ4MjI0OTMy.html', t:1},{c:'我的公主'},{c:'我为祖国喝茅台'},{c:'我们结婚了维尼夫妇'},{c:'我的隔壁是良人'}]}
		  *上面是在输入框内输入“我”所返回的结果。json结果中的q是表示查询词。r表示结果集，也就下拉菜单中的10个提示词，r是一个jsonarray。
		  *r中的每一个提示词是一个json对象，其中c表示提示词。提示词按类型可分为三种
		  *		1. 普通词，没有任何附加信息，json对象只有一个c
		  *     2. 带链接词，类似“火影忍者300”这种，提示词后面会跟一个播放链接。json对象会有有t，和l。t链接词标识，l表示播放链接
		  *     3. 直达区词，类似“蜗居”，“让子弹飞”这种版权剧，这种词被选中后，右侧会有截图和播放按钮。json对象会有d，和u。d直达区词标识，u是直达区详细信息
		           u也是一个jsonarray，
		  t表示该词带有播放链接，l表示该词的播放链接。
		  */
        suggestUpdate: function (M) {
	        this.focusShow = true; 
            this.ddiv.innerHTML = "";
            this.bdiv.innerHTML = "";
			this.keyboardFocus = -1;
        //    _.dom.hide(this.ddiv);
            if (!M || !(typeof M === "object")) {
                this.onComplete();
                return
            }
            var curQueryWord = M["q"];
            this._dataCache[curQueryWord] = M;
            this.updateCanPlayDiv(M["p"]);
            var Q = M["r"];
            if (!Q || !Q.length) {
                this.onComplete();
                return
            }
            var C = document.createElement("div");
		   
            C.setAttribute(this.QUERY_ATTR, this.escape(curQueryWord));
            //C.style.display = "none";
            /*var V = document.createElement("div"),
                N = document.createElement("div"),
                G = document.createElement("ul"),
                R = document.createElement("li");
            _.dom.attr(N, {
                "class": "soauto",
                "style": "display:block"
            });
            _.dom.attr(R, {
                "class": "common"
            });
            V.appendChild(N);
            N.appendChild(G);
            G.appendChild(R);
            this.bdiv.appendChild(V);
            var A = document.createElement("div");
            
            _.dom.attr(A, {
                "class": "soauto",
                "style": "display:block"
            });*/
			var D = document.createElement("ul");
			var SD = document.createElement("ul");
			var DA = 0;
            for (var L = 0, H = Q.length; L < H; L++) {
                var B = Q[L],
                    I = document.createElement("li"),
                    T = B["c"],	
					P = 0;
                    J = 0;
				var word = this.escape(T);
				var attw = word;
                if (B["t"]) J = B["t"];
				DA = B["d"];
				if (B["p"]) P = B["p"];
                var trimCurQueryWord = trim(curQueryWord);
				if (word != trimCurQueryWord && word.indexOf(trimCurQueryWord) > -1) {
					var S = word.substring(word.indexOf(trimCurQueryWord) + trimCurQueryWord.length);
					word = trimCurQueryWord + '<b style="font-weight:bold">' + S + '</b>';					
				}
				I.setAttribute(this.ITEM_TYPE, J);
                if (J == 0) {
					if(DA == 1) word += "<span class='expand'>&gt;</span>";
                    I.innerHTML = word;
                    _.dom.attr(I, {
                        "class": ""
                    });
                    D.appendChild(I);
                    var attw_tmp = attw.replace(/^\d+\. /, "");
                    I.setAttribute(this.ITEM_QUERY, attw_tmp);
                    D.setAttribute(this.ITEM_QUERY, attw_tmp);
                } else if (J == 1) {
					I.setAttribute(this.ITEM_QUERY, attw);	
					I.setAttribute(this.ITEM_LINK, this.escape(B["l"]))					
					var U = word;
						U += "<a class='goplay' target='_blank' href='" + this.escape(B["l"]) + "'><em>\u64ad\u653e</em></a>";						
						I.innerHTML = U;
						_.dom.attr(I, {
							"class": ""
						});
						
						D.appendChild(I);		
                        attw = attw.replace(/^\d+. /, "");
						D.setAttribute(this.ITEM_QUERY, attw);
					
					/*if(DA != 1) {
						
					} 
					else {
						var U = "";
						var EM = "";
						if(P == 1) {
							U += "<span><a target='_blank' fakehref='" + this.getSearchUrl(M.q) + "'>" + this.escape(M.q) + " [\u4eba\u7269]</a></span>";
							EM = "<em>\u70ed\u64ad</em>";
						}
						U += "<span>" + EM + "<a target='_blank' fakehref='" + this.escape(B.l) + "'>" + this.escape(T) + "</a></span>";
						I.innerHTML = U;
						_.dom.attr(I, {
							"class": ""
						});
										
						_.dom.attr(SD, {
								"class": "special"
							});
						I.setAttribute(this.ITEM_TYPE, J + DA);
						SD.appendChild(I);
						SD.setAttribute(this.ITEM_QUERY, attw);
					}*/
                }
				
				if(DA == 1) {  //如果是一个直达区词
					
            		var DD = document.createElement("div");
					var DL = new Array();					
					var ua = B["u"];
					for(var k in ua) { 
						var url = ua[k]["k"];
                        /*if(curQueryWord == ua[k]["j"]) {
                            this.updateCanPlayDiv(url);
                        }*/
						//DL.push("<li><a class='go' href='" + ua[key]["l"] + "'>" + ua[key]["o"] + "</a></li>"); 
						DL.push("<div class='item' itemindex='" + k + "'>");
						DL.push("<div class='thumb'><a target='_blank' href='");
                        DL.push(ua[k]["d"]);
                        DL.push("'><img _src='");
						DL.push(ua[k]["b"]);
						DL.push("' /></a></div>");
						DL.push("<div class='baseinfo'><a target='_blank' href='");
						DL.push(ua[k]["d"]);
						DL.push("'>");
						DL.push(ua[k]["j"] + "</a><i>");
						DL.push(ua[k]["m"]);
						DL.push("</i><i>");
                        DL.push(ua[k]["y"]);
                        DL.push("</i>");
                        DL.push("</div><div class='actor'>");
                        if(ua[k]["p"] && ua[k]["p"].length) {
						    DL.push("主演： ");
						    DL.push(ua[k]["p"]);
                        }
                        DL.push("</div>");
						DL.push("<div class='play'>");
                        if(url && url.length) {
                        DL.push("<a target='_blank' href='");
						DL.push(url);
						DL.push("'><em>播放</em></a>");
                        }
                        DL.push("</div>");
						DL.push("</div>");
						/*var item = document.createElement("div");
						var thumb = document.createElement("div");
						var img = document.createElement("img");
						img.setAttribute("src", ua[k]["b"]);
						thumb.className = 'thumb';
						thumb.appendChild(img);
						var base = document.createElement("div");
						var a = document.createElement("a");
						a.setAttribute("href", ua[k].k);
						a.innerHTML = ua[k]["j"];
						base.className = 'base';
						base.appendChild(a);
						var actor = document.createElement("div");
						actor.innerHTML = "主演 " + ua[k].p;
						actor.className = 'actor';
						var play = document.createElement("div");
						var ea = document.createElement("a");
						ea.setAttribute("href", ua[k].k);
						ea.innerHTML = "<em>播放</em>";
						play.className = 'play';
						play.appendChild(ea);
						item.className = 'item';
						item.appendChild(thumb);
						item.appendChild(img);
						item.appendChild(base);
						item.appendChild(actor);
						item.appendChild(play);
						DD.appendChild(item);*/
					}
					DD.innerHTML = DL.join("");
					DD.setAttribute("id", "ddiv" + L);	
					DD.setAttribute("itemC", ua.length);
                    //if(ua.length == 1) {
				    	//DD.setAttribute("itemq", ua[0]["j"]);  update on 2011-07-12  ddiv 记录查询词，发给logstat
                    //}
                    DD.setAttribute("itemq", attw);
					DD.className = "panel";
					this.ddiv.appendChild(DD);		
				}
                
				_.dom.attr(D, {
						"class": "autolist"
					});

                C.appendChild(D)
				if(DA == 1) {
					C.appendChild(SD);
				}
				this.bdiv.appendChild(C);
				this.bdiv.appendChild(this.ddiv);
            }
			//this.adiv.innerHTML = "<div class='main'><a href='" + this.getSearchUrl(curQueryWord) + "'>所有<em>" + curQueryWord + "</em>的搜索结果</a></div>";
		//	this.updateBottomUrl(curQueryWord);
			
			this.onComplete()
        },
		
		updateBottomUrl: function(q) {
            /*if(!q || !q.length) {
                return
            }
			this.adiv.innerHTML = "<div class='all'><a href='" + this.getSearchUrl(q) + "'>所有<em>" + q + "</em>的搜索结果</a></div>";
            
				var a = this.adiv.getElementsByTagName("a");
				if(a[0]) {
					_.events.listen(a[0], "mouseover", _.bind(this.removeBoxBlur, this));
					_.events.listen(a[0], "mouseout", _.bind(this.revertBoxBlur, this));
				}
                */
		},
        updateCanPlayDiv: function(u) { //首页直接播放功能
            //console.log("update can play div" + u);
            if(this.canplay) {
               if(u && u.length > 0) {
                    _.dom.show(this.canplay);
                    this.canplay.innerHTML = "<a target='_blank' href='" + u + "'><em>播放</em></a>";


                    var canplayBtn = this.canplay.getElementsByTagName("a");
                    var logKeyword = this.box.value;
                    var logUrl = canplayBtn.href;
                    //console.log(canplayBtn);
                    if(canplayBtn) {
                        _.events.listen(canplayBtn[0], "click", _.bind(this.urlgodirectplay, this, keyword, url));
                    }
               } else {
                    _.dom.hide(this.canplay);
                    this.canplay.innerHTML = "";
               }
            }
        },
        focusBox: function () {
            this.box.focus();
            if (this.box.createTextRange) {
                var $ = this.box.createTextRange();
                $.moveStart("character", this.box.value.length);
                $.select()
            } else if (this.box.setSelectionRange) this.box.setSelectionRange(this.box.value.length, this.box.value.length)
        },
      
        removeBoxBlur: function () {
            this.box.onblur = null
        },
        revertBoxBlur: function () {
            this.box.onblur = _.bind(this.hide, this)
        }, 
        time: function () {
            return "&time=" + new Date()
        },
        hour: function () {
            return "&h=" + (new Date()).getHours()
        },
        LOG_MOUSE_SELECT: "mouseSelect",
        LOG_KEY_SELECT: "keySelect",
        LOG_ICON_PRESS: "iconPress",
        CHANGE_SUG_STATUS: "changeStatus",
        REQUEST_TIMEOUT: 50,
        ITEM_INDEX: "autoindex",
        ITEM_HIGHLIGHT_STYLE: "current",
        ITEM_TYPE: "hitt",
        ITEM_QUERY: "hitq",
        ITEM_LINK: "hitl",
        QUERY_ATTR: "squery",
        KEYFROM_POST: ".suggest",
        S_QUERY_URL_POST: "?query=",
        S_LOG_URL_POST: "/sokuClick.php?type=",
        defSugServ: "http://" + location.host + "/suggest/",
        defSearchServ: "http://" + document.domain + "/search?",
        defSearchParamName: "/v?keyword=",
        defKeyfrom: document.domain.replace(/.soku.com/, ""),
        defSugName: "aa",
        sugCookieName: "SUG_STATUS"
    })
})(soku)
