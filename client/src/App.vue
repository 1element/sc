<template>
  <div id="app">
    <app-navigation></app-navigation>
    <main role="main" class="container-fluid">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import AppNavigation from './components/AppNavigation';

export default {
  name: 'app',
  components: { AppNavigation },
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
