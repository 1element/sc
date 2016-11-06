var SurveillanceCenter = {

  CSS_TRIGGER_REFRESH_LIVEVIEW: '.js-refresh-liveview',
  CSS_LIVEVIEW_CONTAINER: '#liveview-container',

  init: function() {
    this.addRefreshLiveviewListener();
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
      });
    });
  }

};

$(document).ready(function() {
  SurveillanceCenter.init();
});
