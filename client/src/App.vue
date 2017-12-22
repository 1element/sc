<template>
  <div id="app">
    <app-navigation></app-navigation>
    <main role="main" class="container-fluid">
      <router-view></router-view>
    </main>
    <vue-progress-bar></vue-progress-bar>
  </div>
</template>

<script>
import AppNavigation from './components/AppNavigation';

export default {
  name: 'app',
  components: { AppNavigation },

  mounted() {
    // finish progress bar after component is mounted
    this.$Progress.finish();
  },

  created() {
    // start progress bar when component is created
    this.$Progress.start();

    this.$router.beforeEach((to, from, next) => {
      // start progress bar before each route
      this.$Progress.start();
      next();
    });
    this.$router.afterEach(() => {
      // finish progress bar after each route
      this.$Progress.finish();
    });
  },

};
</script>

<style lang="scss">
body {
  background-color: #fff;
  padding-top: 4.5rem;
}

.edge-to-edge {
  padding-left: 0;
  padding-right: 0;
}

.btn {
  &.icon {
    padding-bottom: 0;
  }
}

@font-face {
  font-family: 'Material Icons';
  font-style: normal;
  font-weight: 400;
  src: local('Material Icons'),
    local('MaterialIcons-Regular'),
    url(./assets/MaterialIcons-Regular.woff2) format('woff2'),
    url(./assets/MaterialIcons-Regular.woff) format('woff'),
    url(./assets/MaterialIcons-Regular.ttf) format('truetype');
}

.material-icons {
  font-family: 'Material Icons';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;  /* Preferred icon size */
  display: inline-block;
  line-height: 1;
  text-transform: none;
  letter-spacing: normal;
  word-wrap: normal;
  white-space: nowrap;
  direction: ltr;

  /* Support for all WebKit browsers. */
  -webkit-font-smoothing: antialiased;
  /* Support for Safari and Chrome. */
  text-rendering: optimizeLegibility;

  /* Support for Firefox. */
  -moz-osx-font-smoothing: grayscale;

  /* Support for IE. */
  font-feature-settings: 'liga';
}

</style>
