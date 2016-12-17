var SurveillanceCenter = {

  CSS_TRIGGER_REFRESH_LIVEVIEW: '.js-refresh-liveview',
  CSS_TRIGGER_CAMERA_FALLBACK: '.js-camera-fallback',
  CSS_LIVEVIEW_CONTAINER: '#liveview-container',

  init: function() {
    this.addRefreshLiveviewListener();
    this.addRotationListener();
    this.addCameraFallbackImagesListener();
  },

  addRefreshLiveviewListener: function() {
    var that = this;
    $(this.CSS_TRIGGER_REFRESH_LIVEVIEW).on('click', function() {
      var button = this;
      $(button).addClass('active');
      $.ajax({
        url: $(this).data('url')
      }).done(function(data) {
        $(that.CSS_LIVEVIEW_CONTAINER).replaceWith(data);
        $(button).removeClass('active');
        that.rotateImages();
        that.addCameraFallbackImagesListener();
      });
    });
  },

  /**
   * Add camera fallback images listener.
   * Will show a default image for cameras that are offline.
   */
  addCameraFallbackImagesListener: function() {
    $(this.CSS_TRIGGER_CAMERA_FALLBACK).on('error', function() {
      console.log('Camera ' + $(this).attr('src') + ' seems to be offline.');
      $(this).off();
      $(this).attr('src', '/img/camera-offline.svg');
    });
  },

  addRotationListener: function() {
    if ($('[data-rotate]').length == 0) {
      return;
    }

    var that = this;
    this.rotateImages();
    $(window).resize(function() {
      that.rotateImages();
    });
  },

  rotateImages: function() {
    $('[data-rotate]').each(function() {
      var rotateValue = $(this).data('rotate');

      if (rotateValue > 0) {
        $(this).css({
          '-webkit-transform': 'rotate(' + rotateValue + 'deg)',
          'transform': 'rotate(' + rotateValue + 'deg)',
          'height': $(this).parent().width() + 'px',
          'width': 'auto'
        });
      }
    });
  }

};

$(document).ready(function() {
  SurveillanceCenter.init();
});
