(function($) {

$.widget("nx.threeColumn", $.extend({}, $.ui.mouse, {
	
	_init: function() {
		var self = this, o = this.options;
		this.colmid = $("> .colmid", this.element); 
		this.colleft = $("> .colleft", this.colmid); 
		this.col1wrap = $("> .col1wrap", this.colleft); 
		this.col1 = $("> .col1", this.col1wrap); 
		this.col2 = $("> .col2", this.colleft); 
		this.col3 = $("> .col3", this.colleft); 
		
		this.handleRight =  this.handleLeft = null;
		if (o.resizeRight) {
			this.handleRight = $('<div class="ui-resizable-handle ui-resizable-w" unselectable="on" style="position:absolute"></div>').appendTo(this.col3);
		}
		if (o.resizeLeft) {
			this.handleLeft = $('<div class="ui-resizable-handle ui-resizable-e" unselectable="on" style="position:absolute"></div>').appendTo(this.col2);			
		}
	
	
		// Initialize the mouse interaction
		this._mouseInit();
	},
	
	_pixels: function($elem, property) {
		var crt = $elem.css(property);
		if (crt)
			crt = parseInt(crt.substring(crt, crt.length - 2));
		else 
			crt = 0;
		return crt;
	},
	_incr: function($elem, property, diff) {
			$elem.css(property, (this._pixels($elem, property) + diff) + "px");
	},
		
	incrRight: function(diff) {
		this._incr(this.colmid, "margin-left", diff);
		this._incr(this.colleft, "left", -diff);
		this._incr(this.col1, "margin-right", -diff);
		this._incr(this.col3, "width", -diff);
	},
	
	incrLeft: function(diff) {
		this._incr(this.colleft, "left", diff);
		this._incr(this.col1wrap, "right", diff);
		this._incr(this.col1, "margin-left", diff);
		this._incr(this.col2, "width", diff);
	},
	
	left: function(x) {
		var oldVal = this._pixels(this.col2, "width");
		if (oldVal == x)
			return;
		this.incrLeft(-oldVal + x);
	},
	
	right: function(x) {
		var oldVal = this._pixels(this.col3, "width");
		if (oldVal == x)
			return;
		this.incrRight(oldVal - x);
	},
	
	_mouseCapture: function(event) {		
		return this.options.disabled || this.handleLeft && this.handleLeft[0] == event.target || 
			this.handleRight && this.handleRight[0] == event.target ;

	},

	_mouseStart: function(event) {
		this.position = event.pageX;
		if (this.handleLeft && this.handleLeft[0] == event.target)
			this.incrementer = this.incrLeft;
		else if (this.handleRight && this.handleRight[0] == event.target)
			this.incrementer = this.incrRight;
		this.resizeMask = $("<div class='ui-resizable-mask'></div>").appendTo(this.col1);		
	},
	
	_mouseDrag: function(event) {
		this.incrementer(event.pageX - this.position);		
		this.position = event.pageX;
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
// size: this.size,
// originalSize: this.originalSize,
// originalPosition: this.originalPosition
		};
	}
}));

$.extend($.nx.threeColumn, {
	version: "1.7.2",
	eventPrefix: "drag",
	defaults: {
		resizeRight: false,
		resizeLeft: false,
		distance: 1
	}
});

})(jQuery);