// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import VueProgressBar from 'vue-progressbar';
import moment from 'moment';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import App from './App';
import router from './router';

Vue.config.productionTip = false;

Vue.use(BootstrapVue);
Vue.use(VueProgressBar);
require('../node_modules/imagesloaded/imagesloaded.pkgd.min.js');

Vue.filter('formatDate', (value) => {
  if (!value) {
    return '';
  }
  return moment(String(value)).format('YYYY-MM-DD HH:mm:ss');
});

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App },
});
