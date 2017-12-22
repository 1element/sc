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
</style>
