"use strict";

// Class definition
var KTAppChat = function () {
    var _chatAsideEl;
    // Private functions
    var _initAside = function () {
        // User listing
        var cardScrollEl = KTUtil.find(_chatAsideEl, '.scroll');
        var cardBodyEl = KTUtil.find(_chatAsideEl, '.card-body');
        console.log(cardBodyEl)
        var searchEl = KTUtil.find(_chatAsideEl, '#cabecera');
         console.log(searchEl)

        if (cardScrollEl) {
            // Initialize perfect scrollbar(see:  https://github.com/utatti/perfect-scrollbar)
            KTUtil.scrollInit(cardScrollEl, {
                mobileNativeScroll: true, // Enable native scroll for mobile
                desktopNativeScroll: true, // Disable native scroll and use custom scroll for desktop
                resetHeightOnDestroy: true, // Reset css height on scroll feature destroyed
                handleWindowResize: true, // Recalculate hight on window resize
                rememberPosition: true, // Remember scroll position in cookie
                height: function () {  // Calculate height
                    var height=575;

                    return height;
                }
            });
        }
    };

    return {
        // Public functions
        init: function () {
            // Elements
            _chatAsideEl = KTUtil.getById('kt_foro');
            // Init aside and user list
            _initAside();
        }
    };
}();

jQuery(document).ready(function () {
    KTAppChat.init();
});


