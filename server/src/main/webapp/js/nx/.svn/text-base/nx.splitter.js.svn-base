(function($) {

$.widget("nx.splitter", $.extend({}, $.ui.mouse, {
	
	_init: function() {
		var self = this, o = this.options;
		this.top = $(this.element.children().get(0)); 
		this.bottom = $(this.element.children().get(1)); 
		
		this.handle = $('<div class="ui-resizable-handle ui-resizable-n" unselectable="on" style="position:absolute"></div>').appendTo(this.bottom);
			// Initialize the mouse interaction
		this._mouseInit();
	},
			
	moveDown: function(pos) {
		this.top.css("bottom", pos + "px");
		this.bottom.css("height", pos + "px");		
	},	
	
	_mouseCapture: function(event) {		
		return this.options.disabled || this.handle[0] == event.target;

	},

	_mouseStart: function(event) {
		this.position = event.pageY;
		this.initialHeight = this.bottom.height();
		this.resizeMask = $("<div class='ui-resizable-mask'></div>").appendTo(this.top.add(this.bottom));		
	},
	
	_mouseDrag: function(event) {
		this.moveDown(this.initialHeight - event.pageY + this.position);
	},
	
	_mouseStop: function(event) {
		this.position = null;
		if (this.resizeMask) {
			this.resizeMask.remove();
			this.resizeMask = null;
		}
	},
	
	plugins: {},

	ui: function() {
		return {
			element: this.element,
			helper: this.helper,
			position: this.position
		};
	}
}));

$.extend($.nx.splitter, {
	version: "1.7.2",
	eventPrefix: "drag",
	defaults: {
		distance: 1
	}
});

})(jQuery);