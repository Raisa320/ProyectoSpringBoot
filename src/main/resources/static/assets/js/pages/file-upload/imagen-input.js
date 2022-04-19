'use strict';

// Class definition
var KTImageInputDemo = function () {
	// Private functions
	var initDemos = function () {
		
		// Example 5
		var avatar5 = new KTImageInput('kt_image_5');
                
                var avatar4 = new KTImageInput('kt_image_4');
	}

	return {
		// public functions
		init: function() {
			initDemos();
		}
	};
}();

KTUtil.ready(function() {
	KTImageInputDemo.init();
});
