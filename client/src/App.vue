<template>
  <div id="app">
    <div id="wrapper">
      <app-navigation></app-navigation>
      <router-view></router-view>
    </div>
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
#page-wrapper {
  padding-bottom: 20px;
}

h1.page-header {
  font-size: 24px;
}

.table-sc > tbody > tr:first-child > td {
  border-top: none;
}
</style>
