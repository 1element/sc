var SurveillanceCenter = {

  CSS_TRIGGER_REFRESH_LIVEVIEW: '.js-refresh-liveview',
  CSS_TRIGGER_CAMERA_FALLBACK: '.js-camera-fallback',
  CSS_TRIGGER_PUSH_NOTIFICATION_SETTINGS_TOGGLE: '.js-push-notification-settings-toggle',
  CSS_PUSH_NOTIFICATION_SETTINGS_ERROR: '#js-push-notification-settings-error',
  CSS_SETTINGS_ENDPOINT_CONFIGURATION: '#settings-endpoint-configuration',
  CSS_NOTIFIER_CONFIGURATION: '#notifier-configuration',
  CSS_NOTIFIER_BADGE: '#js-notifier-badge',
  CSS_LIVEVIEW_CONTAINER: '#liveview-container',
  CSS_LIVEVIEW_CONTAINER_LOADER: '#liveview-container-loader',
  LIVEVIEW_CONTAINER_ID: 'liveview-container',
  LIVEVIEW_CONTAINER_LOADER_ID: 'liveview-container-loader',

  /**
   * Initialize all listeners.
   */
  init: function() {
    this.addRefreshLiveviewListener();
    this.addRotationListener();
    this.addCameraFallbackImagesListener();
    this.addPushNotificationSettingsToggleListener();
    this.addNotifier();
  },

  /**
   * Refresh liveview image on click.
   */
  addRefreshLiveviewListener: function() {
    var that = this;
    $(this.CSS_TRIGGER_REFRESH_LIVEVIEW).on('click', function() {
      var button = this;
      $(button).addClass('active');
      $(button).prop('disabled', true);
      $.ajax({
        url: $(this).data('url')
      }).done(function(data) {
        // load result in hidden container
        $(that.CSS_LIVEVIEW_CONTAINER_LOADER).append(data);
        that.addCameraFallbackImagesListener();
        // wait for image(s) to be loaded
        $(that.CSS_LIVEVIEW_CONTAINER_LOADER).imagesLoaded(function() {
          // move content to visible container
          $(that.CSS_LIVEVIEW_CONTAINER).replaceWith($(that.CSS_LIVEVIEW_CONTAINER_LOADER));
          $(that.CSS_LIVEVIEW_CONTAINER_LOADER).attr('id', that.LIVEVIEW_CONTAINER_ID);
          $(that.CSS_LIVEVIEW_CONTAINER).removeClass('hidden');
          // create a new hidden loader container for next request
          $(that.CSS_LIVEVIEW_CONTAINER).after($('<div/>').attr('id', that.LIVEVIEW_CONTAINER_LOADER_ID).addClass('hidden'));
          // rotate image if necessary
          that.rotateImages();
          // activate refresh button again
          $(button).prop('disabled', false);
          $(button).removeClass('active');
        });
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
      var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/liveview"));
      $(this).attr('src', contextPath + '/img/camera-offline.svg');
    });
  },

  /**
   * Add listener for push notification toggle buttons on settings page.
   */
  addPushNotificationSettingsToggleListener: function() {
    var that = this;
    $(this.CSS_TRIGGER_PUSH_NOTIFICATION_SETTINGS_TOGGLE).on('click', function() {
      var endpoint = $(that.CSS_SETTINGS_ENDPOINT_CONFIGURATION).data('push-notification-settings-endpoint');
      var cameraId = $(this).data('camera-id');
      var currentEnabledStatus = $(this).data('enabled-status');
      var newEnabledStatus = !currentEnabledStatus;
      var data = {
    	'enabled': newEnabledStatus	  
      };
      var button = this;
      $.ajax({
        url: endpoint + cameraId,
        method: 'PUT',
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json',
        data: JSON.stringify(data)
      }).done(function(data) {
        $(button).data('enabled-status', newEnabledStatus);
        $(button).find('.btn').toggleClass('active');
        $(button).find('.btn').toggleClass('btn-primary');
        $(button).find('.btn').toggleClass('btn-default');
      }).fail(function(data) {
        $(that.CSS_PUSH_NOTIFICATION_SETTINGS_ERROR).collapse('show');
      });
    });
  },

  /**
   * Add notifier, showing number of recordings in window title and as headline badge.
   */
  addNotifier: function() {
    var enabled = $(this.CSS_NOTIFIER_CONFIGURATION).data('notifier-enabled');
    if (enabled) {
      var endpoint = $(this.CSS_NOTIFIER_CONFIGURATION).data('notifier-endpoint');
      var interval = $(this.CSS_NOTIFIER_CONFIGURATION).data('notifier-interval') * 1000;
      var that = this;

      (function pollApi() {
        $.getJSON(endpoint, function(data) {
          var count = data.count;
          var currentTitle = $(document).attr('title');
          var pattern = /\(\d+\)/;

          // remove, replace or append count value
          if (count === 0) {
            $(that.CSS_NOTIFIER_BADGE).text('');
            $(document).attr('title', currentTitle.replace(pattern, ''));
          } else {
            $(that.CSS_NOTIFIER_BADGE).text(count);
            if (pattern.exec(currentTitle)) {
              $(document).attr('title', currentTitle.replace(pattern, '(' + count + ')'));
            } else {
              $(document).attr('title', currentTitle + ' (' + count + ')');
            }
          }
          setTimeout(pollApi, interval);
        });
      }());
    }
  },

  /**
   * Listener to invoke image rotation.
   */
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

  /**
   * Rotate images.
   * @private
   */
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

/**
 * Initialize SurveillanceCenter on dom ready.
 */
$(document).ready(function() {
  SurveillanceCenter.init();
});
