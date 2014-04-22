function bind(n, min, max){
	var x = Math.max(n, min);
	x = Math.min(x, max);
	return x;
}

function setCaretPos (obj, pos) {
	if (obj.selectionStart) {
		obj.focus();
		obj.setSelectionRange(pos, pos);
	} else if (document.selection) {
		var range = obj.createTextRange();
		range.move('character', pos);
		//range.select();
		}
	}

function positionUnder(src, target) {
	var $src = $(src);
	var pos = $src.offset();
	var parentPos = $src.offsetParent().offset();
	var h = $src.outerHeight();
	$(target).css({top:pos.top - parentPos.top + h, left: pos.left - parentPos.left});
}

/**
 *
 * @param spec is an array of any of 'f', 'm', 'l' (for 'first', 'middle', 'last')
 * @param start
 * @param end
 * @return
 * tests
 * console.info(intervals(['f'], 1, 2));
	console.info(intervals(['f'], 1, 1));
	console.info(intervals(['f', 'm'], 1, 2));
	console.info(intervals(['f', 'l'], 1, 2));
	console.info(intervals(['f', 'l', 'm'], 1, 2));
 */
function intervals(spec, start, end) {
	var r = [];

	if ($.inArray('f', spec) >= 0 && start != 0)
		r.push({start: start -1, end: start-1});
	if ($.inArray('m', spec) >= 0&& end - 1 >= start)
		r.push({start: start, end: end - 1});
	if ($.inArray('l', spec) >= 0)
		r.push({start: end, end: end});
	//collapse intervals
	for(var i = r.length - 1; i > 0; --i)
		if (r[i].start == r[i-1].end + 1) {
			r[i-1] = {start: r[i-1].start, end: r[i].end};
			r.splice(i, 1);
		}
	return r;
}

function TreeView() {
	this.root = {level: -1, data: null, children: [], parent: null};
	this.crtNode = this.root;
	this.nodes = {};
}

TreeView.prototype = {
	node: function(level, key, data) {
		for(var p = this.crtNode; p.parent != null && level <= p.level; p = p.parent)
			{}
		var n = {key: key, data:data, children: [], parent:p, level: p.level + 1};
		p.children.push(n);
		this.nodes[key] = n;
		this.crtNode = n;
	},

	walk: function(callback, n) {
		n = n || this.root;
		for(var c in n.children) {
			var nc = n.children[c];
			callback(nc);
			this.walk(callback, nc);
		}
	}
};


var regexRef = new RegExp("(?:([\\w.]+)!)?([$]?)([A-Za-z]+)([$]?)([0-9]+)");
var regexAreaOrRef = new RegExp(regexRef.source + "(:" + regexRef.source + ")?") ;

function parseAreaReference(areaRef) {
	var refs = areaRef.split(":");
	if (refs.length != 2)
		return null;
	return {topLeft: parseCellReference(refs[0]), bottomRight: parseCellReference(refs[1])};
}

function parseCellReference(ref){
	var m = regexRef.exec(ref);
	if (!m)
		return null;

	return {col: columnLabelIndex(m[3]), row: parseInt(m[5]) - 1, sheet: m[1] };
}

function findReferencesInFormula(formula){
	var m = regexAreaOrRef.exec(formula);
	var refs = [];
	var crtText = formula;
	while(m != null){
		var tl = {col: columnLabelIndex(m[3]), row: parseInt(m[5]) - 1, sheet: m[1]};
		var br = tl;
		if (m[9] != null)
			br = {col: columnLabelIndex(m[9]), row: parseInt(m[11]) - 1, sheet: m[7]};
		refs.push({topLeft:tl, bottomRight: br});
		crtText = crtText.substring(m.index + m[0].length);
		m = regexAreaOrRef.exec(crtText);
	}
	return refs;
}

function columnLabelIndex(str) {
	// Converts A to 1, B to 2, Z to 26, AA to 27.
	var num = 0;
	var str = str.toUpperCase();
	for (var i = 0; i < str.length; i++) {
		var digit = str.charCodeAt(i) - 65;	   // 65 == 'A'.
		num = (num * 26) + digit;
	}
	return num;
}

function columnLabel(col) {
	//TODO convert to more than one-letter code
	return String.fromCharCode(65 + col);
}

function isEmptyObject( obj ) {
	for ( var name in obj ) {
		return false;
	}
	return true;
}
/**
 * calculate the index of the column for a cell, taking in consideration colspans
 */
hasColSpan = function($tr) {
	if ($tr.data("hasColSpan") != null)
		return $tr.data("hasColSpan");
	var has = false;
	$tr.children().each(function(){
		if (this.colSpan > 1) {
			has = true;
			return false;
		}
	});
	$tr.data("hasColSpan", has);
	return has;
};

$.fn.getNonColSpanIndex = function() {
    if(! $(this).is('td') && ! $(this).is('th'))
        return -1;
    var $tr = this.parent('tr');
    if (!hasColSpan($tr))
    	return $(this).attr("cellIndex");

    var that = this;
    var allCells = $tr.children();
    var nonColSpanIndex = 0;

    allCells.each(function(i, item) {
            if(item === that[0])
                return false;
            var colspan = $(this).attr('colSpan');
            colspan = colspan ? parseInt(colspan) : 1;
            nonColSpanIndex += colspan;
        }
    );

    return nonColSpanIndex;
};

$.fn.colSpan= function(colSpan) {
	$(this).attr('colSpan', colSpan);
	var $tr = $(this).parent("tr");
	if (colSpan > 1)
		$tr.data("hasColSpan", true);
	else
		//force recalculation whether the enclosing TR has colspans
		$tr.data("hasColSpan", null);
};

$.fn.tdAtIndex=function(idx) {
	if(! $(this).is('tr'))
        return null;

	var $ch = $(this).children();
	if (!hasColSpan($(this)))
		return $ch[idx];

	var nonColSpanIndex = 0;
	var result = null;

	$ch.each(function(i, item) {
        var colspan = $(this).attr('colSpan');
        colspan = colspan ? parseInt(colspan) : 1;
        nonColSpanIndex += colspan;
		if(nonColSpanIndex > idx) {
			result = item;
            return false;
		}
	});
	return result;
};

/**
 * relativeTo can be 'parent', 'page', 'parentInPage'
 */
$.fn.bounds=function(relativeTo){
	var $this = $(this);
	var pos = null;
	if (relativeTo == "parent")
		pos = {top: $this.attr("offsetTop"), left: $this.attr("offsetLeft")};
	else if (relativeTo == "parentInPage")
		pos = $this.position();
	else
		pos = $this.offset();
	var b = {w: $this.width(), h: $this.height(), t: pos.top, l: pos.left};
	b.b = b.t + b.h;
	b.r = b.l + b.w;
	return b;
};

$.fn.scrollBounds=function(){
	var $this = $(this);
	var b = {w: $this.width(), h: $this.height(), t: this.scrollTop(), l: this.scrollLeft()};
	b.b = b.t + b.h;
	b.r = b.l + b.w;
	return b;
};



$.nxdialog=function(name, options) {
	// a workaround for a flaw in the demo system (http://dev.jqueryui.com/ticket/4375), ignore!
	//$("#dialog").dialog("destroy");
	self.dialogs = self.dialogs || {};
	if (!self.dialogs[name]) {
		var opt =  {
				resizable: false,
				height:140,
				modal: true,
				closable: true,
				autoOpen: false
			};
		$.extend(opt, options);
		if (opt.closable) {
			$.extend(opt.buttons, {
				Cancel: function() {
					$(this).dialog('close');
				}
			});
		}

		self.dialogs[name] = $("#dialogs #" + name).dialog(opt);
	}
	self.dialogs[name].dialog("open");
};

$.diff=function(oldList, newList){
	if (!oldList)
		return {added:newList, deleted:[]};
	if (!newList)
		return {added:[], deleted:oldList};
	var ret = {added:[], deleted:[]};
	for(var i in oldList)
		if ($.inArray(oldList[i], newList) < 0)
			ret.deleted.push(oldList[i]);
	for(var i in newList)
		if ($.inArray(newList[i], oldList) < 0)
			ret.added.push(newList[i]);
	return ret;
};

$.url=function(url) {
	var p = 1;
	var params = arguments;
	return url.replace(/\{\}/g, function(m){
		return encodeURIComponent(params[p++]);
	});
};
//jQuery plugin: PutCursorAtEnd 1.0
//http://plugins.jquery.com/project/PutCursorAtEnd
//by teedyay
//
//Puts the cursor at the end of a textbox/ textarea

//codesnippet: 691e18b1-f4f9-41b4-8fe8-bc8ee51b48d4
(function($)
{
 jQuery.fn.putCursorAtEnd = function()
 {
     return this.each(function()
     {
         $(this).focus()

         // If this function exists...
         if (this.setSelectionRange)
         {
             // ... then use it
             // (Doesn't work in IE)

             // Double the length because Opera is inconsistent about whether a carriage return is one character or two. Sigh.
             var len = $(this).val().length * 2;
             this.setSelectionRange(len, len);
         }
         else
         {
             // ... otherwise replace the contents with itself
             // (Doesn't work in Google Chrome)
             $(this).val($(this).val());
         }

         // Scroll to the bottom, in case we're in a tall textarea
         // (Necessary for Firefox and Google Chrome)
         this.scrollTop = 999999;
     });
 };
})(jQuery);
