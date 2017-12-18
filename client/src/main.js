// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import App from './App';
import router from './router';

Vue.config.productionTip = false;

// include some 3rd party legacy libraries
require('../node_modules/bootstrap/dist/css/bootstrap.css');
require('../node_modules/metismenu/dist/metisMenu.css');
require('../node_modules/startbootstrap-sb-admin-2/dist/css/sb-admin-2.css');
require('../node_modules/font-awesome/css/font-awesome.css');
require('../node_modules/jquery/dist/jquery.min.js');
require('../node_modules/bootstrap/dist/js/bootstrap.min.js');
require('../node_modules/metismenu/dist/metisMenu.min.js');
require('../node_modules/startbootstrap-sb-admin-2/dist/js/sb-admin-2.min.js');
require('../node_modules/imagesloaded/imagesloaded.pkgd.min.js');

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App },
});
