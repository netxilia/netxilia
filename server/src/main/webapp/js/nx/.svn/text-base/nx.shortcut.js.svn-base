		//Special Keys - and their codes
var special_keys = {
	'esc':27,
	'escape':27,
	'tab':9,
	'space':32,
	'return':13,
	'enter':13,
	'backspace':8,

	'scrolllock':145,
	'scroll_lock':145,
	'scroll':145,
	'capslock':20,
	'caps_lock':20,
	'caps':20,
	'numlock':144,
	'num_lock':144,
	'num':144,
	
	'pause':19,
	'break':19,
	
	'insert':45,
	'home':36,
	'delete':46,
	'end':35,
	
	'pageup':33,
	'page_up':33,
	'pu':33,

	'pagedown':34,
	'page_down':34,
	'pd':34,

	'left':37,
	'up':38,
	'right':39,
	'down':40,

	'f1':112,
	'f2':113,
	'f3':114,
	'f4':115,
	'f5':116,
	'f6':117,
	'f7':118,
	'f8':119,
	'f9':120,
	'f10':121,
	'f11':122,
	'f12':123
};

var mask = {
	CTRL: 1,
	SHIFT: 2,
	ALT: 4,
	META: 8
};

var meta = {
	'ctrl': mask.CTRL,
	'shift': mask.SHIFT,
	'alt': mask.ALT,
	'meta': mask.META	
};

function Shortcuts() {
	this.handlers={};
	this.defaultHandler = null;
}

Shortcuts.prototype.add=function(combination, handler, propagate) {
	var keys = combination.toLowerCase().split("+");
	var metaCode = 0, code = 0;
	for(var k in keys) {
		var key = keys[k];
		if (meta[key]) 
			metaCode += meta[key];
		else if (special_keys[key])
			code = special_keys[key];
		else
			code = key.toUpperCase().charCodeAt(0);//the upper case letter
	}
	
	var h = {handler:handler, propagate:propagate || false, combination: combination, code: metaCode * 1000 + code};
	this.handlers[metaCode * 1000 + code] = h;
};

Shortcuts.prototype.addDefault=function(handler, propagate) {
	this.defaultHandler = {handler:handler, propagate:propagate || false};
};

Shortcuts.prototype.handleEvent=function(e) {
//	if (e.keyCode == 17) {
//		e.preventDefault();
//		return;
//	}
		
	var code = e.keyCode;
	var metaCode = 0;
	if(e.ctrlKey)	metaCode += mask.CTRL;
	if(e.shiftKey)	metaCode += mask.SHIFT;
	if(e.altKey)	metaCode += mask.ALT;
	//if(e.metaKey)   metaCode += mask.META;

	var h = this.handlers[metaCode * 1000 + code];
	if (!h)
		h = this.defaultHandler;
	if (h) {
		var ret = h.handler(e);
		if (!ret && !h.propagate)
			e.preventDefault();
		return h.propagate && ret;
	} 
	return true;
};
