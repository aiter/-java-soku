if (!window.JSON) {
    window.JSON = {};
}
(function () {
    function f(n) {
        return n < 10 ? '0' + n : n;
    }
    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                   this.getUTCFullYear()   + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate())      + 'T' +
                 f(this.getUTCHours())     + ':' +
                 f(this.getUTCMinutes())   + ':' +
                 f(this.getUTCSeconds())   + 'Z' : null;
        };

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function (key) {
            return this.valueOf();
        };
    }
    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;
    function quote(string) {
        escapable.lastIndex = 0;
        return escapable.test(string) ?
            '"' + string.replace(escapable, function (a) {
                var c = meta[a];
                return typeof c === 'string' ? c :
                    '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' :
            '"' + string + '"';
    }
    function str(key, holder) {
        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];
        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }
        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }
        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':
            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':
            return String(value);
        case 'object':
            if (!value) {
                return 'null';
            }
            gap += indent;
            partial = [];
            if (Object.prototype.toString.apply(value) === '[object Array]') {
                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }
                v = partial.length === 0 ? '[]' :
                    gap ? '[\n' + gap +
                            partial.join(',\n' + gap) + '\n' +
                                mind + ']' :
                          '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }
            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    k = rep[i];
                    if (typeof k === 'string') {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {
                for (k in value) {
                    if (Object.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }
            v = partial.length === 0 ? '{}' :
                gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                        mind + '}' : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }
    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {
            var i;
            gap = '';
            indent = '';
            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }
            } else if (typeof space === 'string') {
                indent = space;
            }
            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

            return str('', {'': value});
        };
    }
    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {
            var j;

            function walk(holder, key) {
                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }
            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }
            if (/^[\],:{}\s]*$/.
test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
                j = eval('(' + text + ')');
                return typeof reviver === 'function' ?
                    walk({'': j}, '') : j;
            }
            throw new SyntaxError('JSON.parse');
        };
    }
}());

if (!window.NovaOptions) window.NovaOptions={};
if (!window.Nova) window.Nova={};
Nova.initializing = true;
Nova.version = '$Rev: 1021 $';
Nova.page_loaded = false;
Nova.realdomain = document.domain;
Nova.optionDefault = function(n, v) {if (NovaOptions[n]===undefined) NovaOptions[n]=v;};
Nova.log = window.__LOG__ || function () {};
Nova.optionDefault('debug', window.__LOG__ ? true : false);
Nova.optionDefault('search_level', 3);

/**
 * Check if cross domain
 * 
 * @param url string
 * @return bool
 */
function nova_domain(url) {
    return url.indexOf('://'+Nova.realdomain) != url.indexOf('://');
}

/**
 * Call ajax function
 * 
 * @param url string
 * @param param array
 * @param callback function
 * @param id string optional
 * @return int
 */
function nova_call(url, param, callback, id, remote) {
    id = id === undefined ? Math.floor(Math.random()*1000000) : id;
    var req = new NovaCall(url, param, callback, id, remote);
    return id;
}
/**
* 获取checkbox的value，将checkbox的value转化为 'value1,value2,value3' 的方式返回
* @todo: 改为采用Prototype方式
*/
function nova_checkbox(name){    
        var elementValue='';
        var samenameelements = document.getElementsByName(name);
        if(samenameelements){
            for(j=0;j<samenameelements.length;j++){
                if(samenameelements[j].checked){
                    elementValue=elementValue+samenameelements[j].value+",";
                }
                ////alert("elementName'value:"+elementValue);
            }
            if(elementValue!=','&&elementValue!=''){
                if(elementValue.charAt(elementValue.length-1)==','){
                    elementValue = elementValue.substr(0,elementValue.length-1);
//                    alert(name+"'s value:"+elementValue);
                }
            }                
        }
        return elementValue;
}

/**
 * Merge values of form elements into an object
 * 
 * @param form string
 */
function nova_form2object(form) {
    form = $(form);
    ret = {};
    var eles = form.getElementsByTagName('input');
    //added by liuz
    var procecced = [];    
    var eleValue;
    for (var i = 0; i < eles.length; i++) { 
        //alert(element);        
        elementType = eles[i].type.toUpperCase();
        if(elementType=="CHECKBOX"||elementType=="RADIO"){
            eleName = eles[i].name;
            var processdTag = 'false';
            for(f=0;f<procecced.length;f++){
                if(eleName==procecced[f]){
                    processdTag = 'true';
                    break;
                }
            }
            if(processdTag=='true'){//同名的元素已经处理过
                continue;
            }
            eleValue = nova_checkbox(eleName);
            procecced.push(eleName);
            }else{
                eleValue = Form.Element.getValue(eles[i]);
            }
        //added by liuz end
        ret[eles[i].name || eles[i].id] = eleValue; //modify by luz
    }
    eles = form.getElementsByTagName('textarea');
    for (var i = 0; i < eles.length; i++) {
        ret[eles[i].name || eles[i].id] = Form.Element.getValue(eles[i]);
    }
    eles = form.getElementsByTagName('select');
    for (var i = 0; i < eles.length; i++) {
        ret[eles[i].name || eles[i].id] = Form.Element.getValue(eles[i]);
    }
    return ret;
}

NovaCall = Class.create();
Object.extend(Object.extend(NovaCall.prototype, Ajax.Request.prototype), {
    initialize: function(url, param, callback, id, remote) {
        this.id = id;
		if(param===undefined){
			param = new Object;
		}
		param = JSON.stringify(param);
		if(remote!=undefined){
			//跨域名版本
			//callback 必须是字符串
        	var method = 'get';
        	this.url = url + '?__ap=' +encodeURIComponent(param) + '&__ai=' + id + '&__callback=' + callback;
			Nova.addScript(this.url);
		}else{
        	var method = 'post';
        	this.url = url;
		    this.callback = callback;
        	this.transport = Ajax.getTransport(url);
        	this.setOptions({method: method, parameters: '__ap='+encodeURIComponent(param)});
        	this.options.onComplete = this.recv.bind(this);
        	this.options.onFailure = this.error.bind(this);
        	this.request(this.url);
		}
		
    },
    recv: function(trans, obj) {
        if (!obj) {
            try { obj = eval('('+trans.responseText+')'); } catch(e) {}
        }
        this.callback(obj, this.id);
    },
    error: function() {
        if (window.nova_error_hook) window.nova_error_hook(); 
    else Nova.log('Error in transport.');
    }
});

/**
 * Walk through nova elements with a callback func
 * 
 * @param func function
 * @param parentElement string
 */
function nova_apply(func, parentElement) {
    var children = ($(parentElement) || document.body).getElementsByTagName('*');
    var re = new RegExp("(^|\\s)nova_[^\\s]+(\\s|$)","");
    var ar;
    for (var i = 0; i < children.length; i++) {
        if (!children[i].className) continue;
        ar = re.exec(children[i].className);
        if (ar != null) func(Element.extend(children[i]), ar[0].replace(/^\s*|\s*$/g,""));
    }
}

/**
 * Event handler, cancel an event
 */
function nova_cancel(event) {
    Event.stop(event);
}

/**
 * Show an element
 */
function nova_show(id, effect) {
	$(id).style.display = 'block';
    //Effect.Appear($(id));
}

/**
 * Hide an element
 */
function nova_hide(id, effect) {
	$(id).style.display = 'none';
    //Effect.Fade($(id));
}
/**
 * Request url and return transport data
 */
function nova_request(callback, url, param, method, args){
	var loader = new NovaRequestLoader(callback, url, param, method, args);
	loader.start();
}
/**
 * Update an element with ajax call
 */
function nova_update(id, url, param, method, history) {
    //Element.update($(id), 'Loading');
    var loader = new NovaLoader(id, url, param, method, history);
    loader.start();
}

/**
 * Update an element with ajax call and fade effect
 */
function nova_update_fade (id, url, param, method, history) {
	//added by kevin
	nova_update(id, url, param, method, history);
	return;
    var loader = new NovaFadingLoader(id, url, param, method, history);
    loader.start();
}

/**
 * Apply event handlers to elements
 */
function nova_event_assign(id) {
    var code = [];
    var triggers = [];
    var sandboxes = [];
    nova_apply( function(el, cl){
        switch (cl) {
            case 'nova_trigger':
                code.push(el.value);
                el.value = '';
                triggers.push(el);
                break;
            case 'nova_form':
                Event.observe(el, 'submit', nova_cancel);
                break;
            case 'nova_sandbox':
                sandboxes.push(el);
                break;
            default:
                if (rules_click[cl]) Event.observe(el, 'click', rules_click[cl]);
        }
    }, $(id));
    for(var i=0;i<code.length;i++) {
        try {
            eval(code[i]);
        } catch(e) {
        }
    }
    if (!Nova.page_loaded) Nova.initriggers = code;
    for(var i=0;i<triggers.length;i++) {
        try {
            Element.remove(triggers[i]);
        } catch(e) {
        }
    }
    for(var i=0;i<sandboxes.length;i++) {
        if (!sandboxes[i].childNodes.length) continue;
        var s = sandboxes[i].childNodes[0].value;
        Element.remove(sandboxes[i].childNodes[0]);
        Nova.runSandbox(sandboxes[i], s);
    }
}

/**
 * Clear event handlers of elements
 */
function nova_event_clear(id) {
    nova_apply( function(el, cl) {
        switch (cl) {
            case 'nova_trigger':
                Element.remove(el);
                break;
            case 'nova_form':
                Event.stopObserving(el, 'submit', nova_cancel);
                break;
            default:
                if (rules_click[cl]) Event.stopObserving(el, 'click', rules_click[cl]);
        }
        if (rules_click[cl]) Event.stopObserving(el, 'click', rules_click[cl]);
    }, id);
}

/**
 * Find real nova raiser of a click event
 */
function nova_raiser(element) {
    var search_level = NovaOptions.search_level; //increase this if u want a deeper throat!
    while (--search_level) {
        if (!element) return null;
        if (element.href) return element;
        element = element.parentNode;
    }
    return null;
}

var rules_click = {
    'nova_updater': function(event) {
        var element = Event.element(event);
        element = nova_raiser(element);
        if (!element) return; //error here.
        var id = element.id.split('_')[0];
        var url = element.href;
        nova_update(id, url, undefined, undefined, true);
        Event.stop(event);
    },
    'nova_updater_fade': function(event) {
        var element = Event.element(event);
        element = nova_raiser(element);
        if (!element) return; //error here.
        var id = element.id.split('_')[0];
        var url = element.href;
        nova_update_fade(id, url, undefined, undefined, true);
        Event.stop(event);
    },
    'nova_submit': function(event) {
        var element = Event.element(event);
        var id = element.id.split('_')[0];
        var form = $(element.id.split('_')[1]);
        var url = form.action;
        nova_update(id, url, Form.serialize(form), 'post');
        Event.stop(event);
    },
    'nova_submit_fade': function(event) {
        var element = Event.element(event);
        id = element.id.split('_')[0];
        form = $(element.id.split('_')[1]);
        url = form.action;
        nova_update_fade(id, url, Form.serialize(form), 'post');
        Event.stop(event);
    }
}

function nova_iframe(id) {
    var f = $(id);
    if (f) return f;
    f = document.createElement('iframe');
    f.name = id;
    if (navigator.appVersion.match(/\bMSIE\b/)) f = document.createElement('<iframe name="'+id+'"></iframe>');
    f.id = id;
    f.width="0";
    f.height="0";
    f.scrolling="no";
    f.frameborder="0";
    f.style.position="absolute";
    f.style.bottom="0";
    f.style.right="0";
    document.body.appendChild(f);
    return f;
}

function nova_element_update(id, html) {
    var el = $(id);
    nova_event_clear(el);
    el.innerHTML = html.replace(new RegExp(Prototype.ScriptFragment, 'img'), '');
    if (html.evalScripts) setTimeout(function() {html.evalScripts()}, 10);
    nova_event_assign(el);
}

Nova.Indicator = {
    counter: 0,
    init: function() {
        if (NovaOptions.compatibleMode) return;
        return;//XXX: disabled by request
        var i = $('nova_indicator');
        if (i) return;
        i = document.createElement('div');
        i.id = 'nova_indicator';
        i.style.display = 'none';
        i.innerHTML = '<img src="/img/loading2.gif" width="18" height="18" align="absmiddle" />&nbsp;正在打开...';
        document.body.appendChild(i);
        //if (!navigator.appVersion.match(/\bMSIE\b/)) 
    },
    show: function() {
    	return;//XXX: disabled by request
        this.counter++;
        if (this.counter==1) {
            if (!$('nova_indicator')) return;
            //Effect.Appear('nova_indicator',{duration: 0.2, queue: 'end'});
        };
    },
    hide: function() {
    	return ;//XXX: disabled by request
        this.counter--;
        if (this.counter==0) {
            if (!$('nova_indicator')) return;
            //Effect.Fade('nova_indicator',{duration: 0.4, queue: 'end'});
        }
    }
}

Nova.clone = function (what) {
    for (i in what) {
        if (typeof what[i] == 'object') {
            this[i] = new Nova.clone(what[i]);
        }
        else
            this[i] = what[i];
    }
}

Nova.uid = function (prefix) {
  if (!prefix) prefix='';
  var r = Math.floor( (Math.random() % 1) * Math.pow(2, 32) );
  var s = r.toString(16);
  while (s.length < 8) {
    s = "0" + s;
  }
  var now = new Date();
  s = now.valueOf().toString(16) + s;
  return prefix + s.substr(s.length - 16 + prefix.length, 16 - prefix.length);
}

Nova.History = {
  buffer: [],
  current: {id: null, html: '', hash: Nova.uid('H')},
  frameLoading: 0,
  initHash: '',
  frameLoaded: function() {
    Nova.History.frameLoading = 0;
  },
  init: function() {
    if (NovaOptions.compatibleMode) return;
    return;//XXX: disabled by request
    Event.observe(nova_iframe('nova_history'), 'load', Nova.History.frameLoaded);
    Nova.History.frameLoading = 1;
    $('nova_history').src='/js/nova_history.htm?'+Nova.History.current.hash;
    Nova.History.locationTimer = setInterval(Nova.History.checkLocation, 200);
    Nova.History.initHash = Nova.History.current.hash;
  },
  checkLocation: function() {
    if (!$('nova_history')) return;
    var hash;
    try{hash = window.frames['nova_history'].location.search.replace(/\?/g, '');} catch(err) { }
    //var hash = nova_iframe('nova_history').src.replace(/.*\?/g, '');
    if (Nova.History.frameLoading) return;
    if (!hash) return;
    if (hash == Nova.History.current.hash) return;
    if (Nova.History.buffer[hash]) Nova.History.switchTo(Nova.History.buffer[hash]);
    else {
      Nova.log('BUG? Unknown history hash: '+hash);
    }
  },
  switchTo: function(data) {
    Nova.log('switch to '+data.hash);
    Nova.History.current = data;
    if (!$('nova_history')) return;
    Nova.History.frameLoading = 1;
    try{window.frames['nova_history'].location.search = data.hash;} catch(err) { }
    nova_element_update(data.id, data.html);
    if (Nova.History.current.hash==Nova.History.initHash) {
      for(var i=0;i<Nova.initriggers.length;i++) {
        try {
          eval(Nova.initriggers[i]);
        } catch(e) {
        }
      }
    }
    Element.setOpacity($(data.id), 1);
  },
  beforeUpdate: function(id, html) {
    Nova.History.current.id = id;
    Nova.History.current.html = html;
    Nova.History.buffer[Nova.History.current.hash] = new Nova.clone(Nova.History.current);
  },
  beforeSend: function(id) {
    if (!Nova.History.current.id) { //Virgin
      Nova.History.current.id = id;
      Nova.History.current.html = $(id).innerHTML;
      Nova.History.buffer[Nova.History.current.hash] = new Nova.clone(Nova.History.current);
    }
    var hash = Nova.uid('H');
    Nova.History.current.hash = hash;
    if (!$('nova_history')) return;
    Nova.History.frameLoading = 1;
    try{window.frames['nova_history'].location.search = hash;} catch(err) { }
  }
};

NovaRequestLoader = Class.create();
Object.extend(Object.extend(NovaRequestLoader.prototype, Ajax.Request.prototype), {
	initialize: function(callback, url, param, method, args) {
        param = param === undefined ? '' : param;
        method = method === undefined ? 'get' : method;
        this.url = url;
        this.transport = Ajax.getTransport(url);
        this.callback = callback;
        this.setOptions({method:method,parameters: param});
        this.options.onComplete = this.recv.bind(this);
        this.options.onFailure = this.error.bind(this);
        this.args = args;
    },
    start: function() {
        this.send();
    },
    send: function() {
        this.request(this.url);
    },
    error: function() {
        if (window.nova_error_hook) window.nova_error_hook(); 
        else Nova.log('Error in transport.');
    },
    recv : function(){
    	this.callback(this.transport.responseText, this.args);
    	this.end();
    },
    end: function() {
        try {
            delete this;
        } catch (err) {
        }
    }
});

NovaLoader = Class.create();
Object.extend(Object.extend(NovaLoader.prototype, Ajax.Request.prototype), {
    initialize: function(id, url, param, method, history) {
        param = param === undefined ? '' : param;
        method = method === undefined ? 'get' : method;
        this.id = id;
        this.url = url + ((url.indexOf('?')==-1) ? '?' : '&') + '__rt=1&__ro='+id;
        this.transport = Ajax.getTransport(url);
        this.setOptions({method: method, parameters: param});
        this.options.onComplete = this.recv.bind(this);
        this.options.onFailure = this.error.bind(this);
        this.options.fixHistory = (!NovaOptions.compatibleMode) && (history || false);
    },
    start: function() {
        this.send();
    },
    send: function() {
        Nova.Indicator.show();
        if (this.options.fixHistory) Nova.History.beforeSend(this.id);
        this.request(this.url);
    },
    recv: function(req, obj) {
        this.update();
        this.end();
    },
    error: function() {
        Nova.Indicator.hide();
        if (window.nova_error_hook) window.nova_error_hook(); 
        else Nova.log('Error in transport.');
    },
    update: function() {
        if (this.options.fixHistory) Nova.History.beforeUpdate(this.id, this.transport.responseText);
        nova_element_update(this.id, this.transport.responseText);
        Nova.Indicator.hide();
    },
    end: function() {
        //this.transport = null;
        try {
            delete this;
        } catch (err) {
        }
    }
});

NovaFadingLoader = Class.create();
Object.extend(Object.extend(NovaFadingLoader.prototype, NovaLoader.prototype), {
    start: function(){
        this.wait = 2;
        this.send();
        Element.addClassName($(this.id+'_wrap'), 'nova_loading');
        this.fader = new nova_fadeout(this.id, {afterFinish:this.hided.bind(this)});
    },
    hided: function(effect){
        try {
            delete this.fader;
        } catch (err) {
        }
        this.wait--;
        if (!this.wait) this.show();
    },
    recv: function(request, object){
        this.wait--;
        if (!this.wait) this.show();
    },
    show: function() {
        this.update();
        this.fader = new nova_fadein(this.id, {afterFinish:this.showed.bind(this)});
        Element.removeClassName($(this.id+'_wrap'), 'nova_loading');
    },
    showed: function(effect){
        try {
            delete this.fader;
        } catch (err) {
        }
        this.end();
    }
});

/**
 * Hide an element, with fade effect
 */
nova_fadeout = function(element) {
	nova_hide(element);
	return ; //disable to use Effect
  element = $(element);
  var oldOpacity = element.getInlineOpacity();
  var options = Object.extend({
  duration: 0.5,
  from: element.getOpacity() || 1.0,
  to:   0.1,
  afterFinishInternal: function(effect) {
    if(effect.options.to!=0) return;
    //effect.element.hide();
    //effect.element.setStyle({opacity: oldOpacity});
  }}, arguments[1] || {});
  return new Effect.Opacity(element,options);
}

/**
 * Show an element, with fade effect
 */
nova_fadein = function(element) {
	nova_show(element);
	return; //disable to use Effect
  element = $(element);
  var options = Object.extend({
  duration: 0.5,
  from: (element.getStyle('display') == 'none' ? 0.1 : element.getOpacity() || 0.1),
  to:   1.0,
  // force Safari to render floated elements properly
  afterFinishInternal: function(effect) {
    effect.element.forceRerendering();
  },
  beforeSetup: function(effect) {
    effect.element.setOpacity(effect.options.from);
    effect.element.show();
  }}, arguments[1] || {});
  return new Effect.Opacity(element,options);
}

function nova_init_hook_internal () {
    var loadiv = $('nova_loading');
    if (loadiv) Element.hide(loadiv);
}

/**
 * Initialize nova framework
 */
function nova_init() {
    Nova.log('Novajax browser engine initialized. ' + Nova.version);
    nova_event_assign(document.body);
    Nova.page_loaded = true;
    Nova.callHooks('init');
    Nova.History.init();
    Nova.Indicator.init();
}

Nova.callHooks = function (hookname) {
    var s = 'nova_'+hookname+'_hook';
    var f = [];
    for (var h in window) {
        if (h.indexOf(s)!=0) continue;
        f.push(h);
    }
    Nova.hooksInterval(f);
}
Nova.hooksInterval = function(hooks) {
    if(0 === hooks.length) {
        if(Nova.timer) clearTimeout(Nova.timer);
        return;
    }
    var h = hooks.shift();
    try {
        window[h].apply();
        window[h] = undefined;
    } catch (e){
        //alert("Error in " + h + ": " + e.description)
        Nova.log("Error in " + h + ": " + e.description);
    }
    Nova.timer = Nova.setTimeout(Nova.hooksInterval, 200, hooks);
}
Nova.setTimeout = function(callback, timeout, param){
    var args = Array.prototype.slice.call(arguments,2);
    var call = function(){callback.apply(null,args);}
    return window.setTimeout(call, timeout);
}
Nova.initCallbacks = [];

Nova.addInitFunc = function (listener) {
  if (!Nova.initializing) listener();
  else Nova.initCallbacks[Nova.initCallbacks.length]=listener;
}

Nova.processInit = function () {
  if (!Nova.initializing) return;
  Nova.initializing = false;
  for (var i=0; i<Nova.initCallbacks.length; i++) {
    var func = Nova.initCallbacks[i];
    func();
  }
  Nova.initCallbacks = [];
}

Nova.scheduleInit = function () {
  if (!Nova.initializing) return true;
  if(typeof document.getElementsByTagName != 'undefined' && (document.getElementsByTagName('body')[0] != null || document.body != null)) {
    Nova.processInit();
  } else {
    setTimeout("Nova.scheduleInit()", 200);
  }
  return true;
}
Nova.addInitFunc(nova_init);

Nova.runSandbox = (function () {
var element_stack = [];
var input_stack = [];
var timer = null;
var ua = navigator.userAgent.toLowerCase();
var isIE = (ua.indexOf('msie') >= 0 && ua.indexOf('opera') < 0);
var old_document_write = document.write;
var old_document_writeln = document.writeln;
 
var callback = function () {
    if (element_stack.length == 0) {
        clearInterval(timer);
        timer = null;
        document.write = old_document_write;
        document.writeln = old_document_writeln;
        return;
    }
    var index = element_stack.length - 1;
    var input = input_stack[index];
    if (input.length == 0) {
        var element = element_stack.pop();
        //element.innerHTML = ''; //FIXME
        element.innerHTML = element.hiddenHTML;
        input_stack.pop();
        return;
    }
    var item = input[input.length - 1];
    if (typeof item == 'string') {
        element_stack[index].hiddenHTML += item;
        //element_stack[index].innerHTML = element_stack[index].hiddenHTML; //FIXME
        input.pop();
    } else if (typeof item == 'object') {
        if (item.src) {
            input[input.length - 1] = {};
            var script = document.createElement('script');
            script.src = item.src;
            script.__index = index;
            if (isIE) {
                script.onreadystatechange = script_loaded;
            } else {
                script.onload = script_loaded;
            }
            var head = document.getElementsByTagName('head')[0];
            head.appendChild(script);
        }
        if (item.text) {
            var script = document.createElement('script');
            script.text = item.text;
            var head = document.getElementsByTagName('head')[0];
            head.appendChild(script);
            input.pop();
        }
    } else {
        input.pop();
    }
}
 
var script_loaded = function () {
    if (isIE && this.readyState.toLowerCase() != "loaded" && this.readyState.toLowerCase() != "complete") {
        return;
    }
    var index = this.__index;
    input_stack[index].pop();
}
 
var new_document_write = function() {
    for (var i = 0; i < arguments.length; i++) {
        element_stack[element_stack.length - 1].hiddenHTML += arguments[i];
        //element_stack[element_stack.length - 1].innerHTML = element_stack[element_stack.length - 1].hiddenHTML; //FIXME
    }
}
 
var new_document_writeln = function () {
    for (var i = 0; i < arguments.length; i++) {
        new_document_write(arguments[i] + "\n");
    }
}
 
return function (element, htmlCode) {
    element.innerHTML = '';
    element.hiddenHTML = '';
    element_stack.push(element);
    var input = [];
    while (true) {
        if ((m = htmlCode.match(/<script([^>]*>)((.|\r|\n)*?)<\/script>/i)) == null) {
            break;
        }
        input.unshift(htmlCode.substr(0, m.index));
        htmlCode = htmlCode.substr(m.index + m[0].length);
        if ((m2 = m[1].match(/src\s*=\s*(['"]?)([^'">\s]*)\1/i)) != null) {
            input.unshift({src:m2[2]});
        } else {
            input.unshift({text:m[2]});
        }
    }
    input.unshift(htmlCode);
    input_stack.push(input);
    if (timer == null) {
        document.write = new_document_write;
        document.writeln = new_document_writeln;
        timer = setInterval(callback, 10);
    }
}})();

Nova.Cookie = {
    set : function (name,value,days,path,domain) {
        if (days) {
            var date = new Date();
            date.setTime(date.getTime()+(days*24*60*60*1000));
            var expires = "; expires="+date.toGMTString();
        }
        else var expires = "";
		if(path==undefined || path ==null || path==""){path="/";}
		if(domain==undefined || domain ==null || domain==""){domain=".youku.com";}
        document.cookie = name+"="+value+expires+";domain="+domain+";path="+path;
    },
    get : function (name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    },
    remove : function (name) {
        createCookie(name,"",-1);
    }
}

Nova.addScriptContent = function(content){
	js=unescape(content);
    var g = document.createElement("script");
    g.type = "text/javascript";
	g.text = js;
    document.getElementsByTagName('head')[0].appendChild(g);
}

Nova.addScript = function(src) {
    // Check if we have the script loaded already
    var scriptElements = document.getElementsByTagName('script');
    if (scriptElements ) {
        var c = scriptElements.length;
        for (var i = 0; i < c; i++) {
            scriptElement = scriptElements[i];
            if (scriptElement.src == src) {
                // Found a match
                return false;
            }
        }
    } 

    var g = document.createElement("script");
    g.type = "text/javascript";
    g.src = src;
    document.getElementsByTagName('head')[0].appendChild(g);
}

Nova.addCSS = function(src) {
    var g = document.createElement("link");
    g.type = "text/css";
    g.rel = "stylesheet";
    g.href = src;
    document.getElementsByTagName('head')[0].appendChild(g);
}

Nova.waitFor = function(name, func) {
    window[name+'_handler'] = func || name+'();';
    window[name+'_timer'] = setInterval('Nova.waiting("'+name+'");', 500);
}

Nova.waiting = function(name) {
    if (!window[name]) return;
    clearInterval(window[name+'_timer']);
    if (typeof window[name+'_handler']=='function') window[name+'_handler'](); else eval(window[name+'_handler']);
    window[name+'_handler'] = undefined;
}
Nova.onDOMContentLoaded = function(onready){
    var Browser = {
        IE           :  !!(window.attachEvent && navigator.userAgent.indexOf('Opera') === -1),
        Opera        :  navigator.userAgent.indexOf('Opera') > -1,
        WebKit       :  navigator.userAgent.indexOf('AppleWebKit/') > -1,
        Gecko        :  navigator.userAgent.indexOf('Gecko') > -1 && navigator.userAgent.indexOf('KHTML') === -1,
        MobileSafari :  !!navigator.userAgent.match(/Apple.*Mobile.*Safari/),
        Version      :  parseFloat(navigator.appVersion)
    }
    var isReady = false;
    function doReady(){
        if( isReady ) return;
        //确保onready只执行一次
        isReady = true;
        onready();
    }
    /*IE*/
    if( Browser.IE ){
        (function(){
            if ( isReady ) return;
            try {
                document.documentElement.doScroll("left");
            } catch( error ) {
                setTimeout( arguments.callee, 0 );
                return;
            }
            doReady();
        })();
        window.attachEvent('onload',doReady);
    }
    /*Webkit*/
    else if (Browser.WebKit && Browser.Version < 525){
        (function(){
            if( isReady ) return;
            if (/loaded|complete/.test(document.readyState))
                doReady();
            else
                setTimeout( arguments.callee, 0 );
        })();
        window.addEventListener('load',doReady,false);
    }
    /*FF Opera 高版webkit Other*/
    else{
        document.addEventListener( "DOMContentLoaded", function(){
            document.removeEventListener( "DOMContentLoaded", arguments.callee, false );
            doReady();
        }, false );
        window.addEventListener('load',doReady,false);
    }
}
Nova.onDOMContentLoaded(Nova.scheduleInit);

function charset_click(evt){
	if(!evt) evt = window.event;
	var ret=true;
	var obj = (evt.target)? evt.target:evt.srcElement;
	if(obj.onclick){ret=false;}
	if(obj.tagName!='A')obj = obj.parentNode;
	if(obj.tagName=='A'){
		var _charset= (obj.charset)? obj.charset:'';
		document.cookie = '__utmarea='+_charset+'; path=/; domain=.youku.com;';
		if(obj.onclick){ret=false;}
		return ret;
	}else{
		return true;
	}
}
/*
window.nova_init_hook_click_charset=function(){
        Event.observe(document, "click",  charset_click);
}
*/
