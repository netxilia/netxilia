


/**
 * this is used to cache formatter instances. usually there is one per sheet.
 * in a context there is only, one active (in edit mode) formatter at any given moment
 */
function EditingContext(sheet) {
	this.editedValue = "";
	this.editor = null;
	this.editorVisible = false;
	this.elements = {};
	this.defaultEditor=true;
	this.sheet = sheet;
	this.container = sheet.table.parent();
}

EditingContext.prototype = {
	buildEditor: function(editorInfo) {
		// do caching
		if (editorInfo == null || editorInfo.length == 0 || editorInfo == "null") {
			return new Editors["text"](this, {});
		} else if (editorInfo.charAt(0) == "{") {
			var params = eval("(" + editorInfo + ")");
			return new Editors[params.id](this, params);
		} else {
			return new Editors[editorInfo](this, params);
		}
	},
	
	edit: function(cell, useEditorInfo, value) {
		this.editor = this.buildEditor(useEditorInfo || cell.editorInfo());		

		var pos = cell.$td.offset();
		var containerPos = this.container.offset();
		containerPos.top -= this.container.scrollTop();
		containerPos.left -= this.container.scrollLeft();
		pos.top -= containerPos.top;
		pos.left -= containerPos.left;
		
		var w = cell.$td.width(), h = cell.$td.height();

		this.editedValue = cell.value();
		this.editor.edit(cell, {top:pos.top, left: pos.left, width:w, height:h}, value);
		this.editor.show(true);
		this.defaultEditor=false;
	},

	cancelEdit: function() {
		this.editor.edit(this.editedValue);
	},
		
	hide: function(){
		if (!this.editor)
			return;
		this.editor.show(false);
		this.editorVisible = false;
		this.editor = null;
		this.defaultEditor=true;
		this.sheet.focusSelectionContent();
	},

	showDefaultEditor : function(cell, text) {
		this.edit(cell, "text", text);
		this.defaultEditor=true;
	},
	
	hasValueChanged: function() {
		return this.editor && this.editor.value() != this.editedValue; 
	},
	
	value: function() {
		return this.editor ? this.editor.value() : null;
	},
	
	setCaptureSelection: function(topLeft, bottomRight){
		if (this.editor && this.editor.setCaptureSelection)
			this.editor.setCaptureSelection(topLeft, bottomRight);
	}
	
};

/*
 * editors can be derived from formatters or they can be set separately 
 */

function Editor() {
	
}
Editor.prototype = {
	/**
	 * return the editors html to be displayed while editing the cell 
	 */
	edit: function(cell, pos) {},
	
	/**
	 * show or hide the editor's elements 
	 */
	show:function(show) {},
	
	
	/**
	 * return the value last edited. called when the user left the editor (changed the cell)
	 */
	value:function() {}
};

/**
 * this is the collection of editors
 */
var Editors = {	};

/******* textarea-based editors *****/
Editors["text"]=function(context, params) {
	this.params = params;
	this.editorElement = null;
	this.context = context;
};

Editors["text"].prototype = {

	edit: function(cell, pos, value) {
		var that = this;
		this.editorElement = this.context.elements["default-editor"];
		if (!this.editorElement) {
			this.editorElement=$("<textarea id='default-editor' autocapitalize='off' class='editor-visible-index'></textarea>").appendTo(this.context.container);
			//enlarge editor as needed
			this.editorElement.keydown(function(ev) {
				if (that.context.valueChanged && ev.keyCode != 13) {
					var $elm = $(this);
					setTimeout(function(){
						that.context.valueChanged($elm.val());
					}, 1);
				}
				if (ev.keyCode < 32)
					return;				
				if ($(this).height() < this.scrollHeight) {
					$(this).height(this.scrollHeight);
				}
			});
		
			this.context.elements["default-editor"] = this.editorElement; 
		}
		var pwidth = this.editorElement.parent().width();
		this.editorElement.css({top:pos.top, left: pos.left, width:Math.min(pos.width + 150, pwidth - pos.left), height:pos.height});
		this.editorElement.attr("minHeight", pos.height);
		
		//cell->editor				
		if (value) {
			this.editorElement.val(value);
		} else {
			this.editorElement.val(cell.value());
		}
		
		setTimeout(function() {
			if (that.editorElement.height() < that.editorElement[0].scrollHeight) {
				that.editorElement.height(that.editorElement[0].scrollHeight);
			}
			that.editorElement.putCursorAtEnd();
		}, 1);
	},
	
	setCaptureSelection: function(selection){
		//TODO modify selection also
		var cursor = this.editorElement.caret().start;
		var v = this.editorElement.val();
		var leftString = v.substring(0, cursor);
		var endWithRefRegex = new RegExp(regexRef.source + "$");
		var keepPos = 0;
		var m = endWithRefRegex.exec(leftString);
		if (m != null) {
			//replace the matching part with the new reference
			this.editorElement.val(v.substring(0, cursor - m[0].length) + selection.start.ref() + v.substring(cursor));
			keepPos = cursor - m[0].length + selection.start.ref().length;
		} else {
			//add the new reference
			this.editorElement.val(v.substring(0, cursor) + selection.start.ref() + v.substring(cursor));
			keepPos = cursor + selection.start.ref().length;
		}
		
		this.editorElement.caret({start:keepPos, end: keepPos});
		if (this.context.valueChanged) {
			this.context.valueChanged(this.editorElement.val());
		}
	},
	
	value: function() {
		return this.editorElement ? this.editorElement.val() : null;
	},
	

	
	show: function(show) {
		if (show) {
			this.editorElement.show();
			this.editorElement.focus();
			
		} else {
			this.editorElement.hide();
		}		
	}
};

/*******date editor *****/
Editors["date"]=function(context, params) {
	this.params = params;
	this.editorElement = null;
	this.context = context;
};


Editors["date"].prototype = {	
		edit: function(cell, pos, value) {
			var outer = this;
			this.editorElement = this.context.elements["date-editor"];
			if (!this.editorElement) {
				this.editorElement=$("<input type='text' id='date-editor' class='editor-visible-index'>").appendTo(this.context.container);
				this.context.elements["date-editor"] = this.editorElement; 
				this.editorElement.simpleDatepicker({startdate: 1970, enddate: 2020} );
			}
			this.editorElement.css({top:pos.top, left: pos.left, width:pos.width + 4, height:pos.height});
			
			if (value) {
				this.editorElement.val(value);
			} else {
				this.editorElement.val(cell.value());
			}
		},
		
		value: function() {
			return this.editorElement ? this.editorElement.val() : null;
		},
		
		show: function(show) {
			if (show) {
				this.editorElement.show();
				this.editorElement.focus();
				this.editorElement.trigger("click");
				//this.editorElement.datepicker('show');
			} else {
				this.editorElement.hide();
				//this.editorElement.datepicker('hide');
			}	
		}
	};

/*******select editor *****/
Editors["select"]=function(context, params) {
	this.params = params;
	this.editorElement = null;
	this.context = context;
};

Editors["select"].prototype = {	
		edit: function(cell, pos, value) {
			var outer = this;
			this.editorElement = this.context.elements["select-editor"];
			if (!this.editorElement) {
				var selectHtml = "<select id='select-editor' >";
				selectHtml += "</select>";
				this.editorElement=$(selectHtml).appendTo(this.context.container);
				
				this.editorElement.multiSelect( {selectAll:false, noneSelected: "", oneOrMoreSelected:""}
				);
				this.editorElement = $("#select-editor", this.context.container);
				this.editorElement.addClass('editor-visible-index');
				this.context.elements["select-editor"] = this.editorElement; 
			}
			this.editorElement.css({top:pos.top, left: pos.left, width:pos.width, height:pos.height});
			
			
			var valuesString = (value !== undefined ? value : cell.value());
			nx.resources.formatters.getFormatValues(nx.app.desc.workbook, cell.css(), function(values) {				
				var selValues = valuesString.split(",");
				var options = [];
				for(var v in values ) {
					options.push({text:values[v].name, value: values[v].value});
				}
				outer.select(options, selValues);
				outer.editorElement.multiSelectOptionsUpdate(options);
			});
			//this.editorElement.val(this.valuesString);
			
		},
		
		select: function(options, values) {
			for(var o in options)
				for(var v in values)
					if (options[o].value == values[v])
						options[o].selected = true;
		},
		
		value: function() {
			return this.editorElement ? this.editorElement.selectedValuesString() : null;
		},
		
		show: function(show) {
			if (show) {
				this.editorElement.show();
				this.editorElement.focus();
				this.editorElement.multiSelectOptionsShow();
			} else {
				this.editorElement.hide();
				this.editorElement.multiSelectOptionsHide();
			}	
		}
	};