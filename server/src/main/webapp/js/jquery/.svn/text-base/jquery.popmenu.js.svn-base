(function($){

	var elements = new Array();
	var elements_button = new Array();
	var elements_count = 0;

	$.fn.popupmenu = function(options) {

		var defaults = {
			target: false,
			addStyle: false,
			time: 300,
			speed: "",
			autooff: true,
			closeOnClick: false
		};
		var options = $.extend(defaults, options);

		var global_menu_div = false;
		var global_menu_top = false;
		var global_t;


		return this.each(function() {

			var button = $(this);
			var target = $(options.target);

			// Register the element target
			elements[elements_count] = target;
			elements_button[elements_count] = button;
			elements_count++;

			button.mouseover( function() {

				// check to see if any elements need turning off first.
				if(options.autooff){
					$.each( elements, function(i, n){
						n.hide();
					});
					$.each( elements_button, function(i, n){
						n.removeClass(options.addStyle);
					});
				}

				clearTimeout(global_t);

				if(options.addStyle != false){
					button.addClass(options.addStyle);
				}
				target.show(options.speed);

			});

			button.mouseout( function() {

				if(!global_menu_div){
					global_t = setTimeout(
					function() {
						if(options.addStyle != false){
							button.removeClass(options.addStyle);
						}
						target.hide(options.speed);
					}
					,options.time);
				}

			});

			target.mouseover( function() {

				global_menu_div = true;
				clearTimeout(global_t);

			});

			target.mouseout( function() {

				global_menu_div = false;
				global_t = setTimeout(
				function() {
					if(options.addStyle != false){
						button.removeClass(options.addStyle);
					}
					target.hide(options.speed);

				}
				,options.time);

			});
			
			if (options.closeOnClick) {
				target.click(function() {
					target.trigger("mouseout");
				});
			}
		});
	};
})(jQuery);