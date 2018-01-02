//jQuery version
window.device = window.device || 'desktop';
(function ($) {
    'use strict';
    $(function () {
        var myPhotoSwipe = {};

        if ('tablet' === window.device || 'mobile' === window.device) {

            myPhotoSwipe = $(".gallery-icon a").photoSwipe({
                imageScaleMethod: 'fit',
                autoStartSlideshow: false,
                captionAndToolbarAutoHideDelay: 5000,
                loop: true,
                doubleTapZoomLevel: 1.5,
                enableDrag: true,
                enableMouseWheel: false,
                enableKeyboard: false,
                zIndex: 1000
            });

        } else {
            myPhotoSwipe = $(".gallery-icon a").photoSwipe({
                imageScaleMethod: 'fitNoUpscale',
                enableMouseWheel: true,
                enableKeyboard: true,
                autoStartSlideshow: false,
                captionAndToolbarAutoHideDelay: 0,
                loop: true,
                doubleTapZoomLevel: 1.5,
                enableDrag: false,
                zIndex: 1000
            });
        }
    });
}(jQuery));
