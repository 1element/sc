<template>
  <nav v-if="showNavigation" class="navbar navbar-expand navbar-dark fixed-top bg-dark justify-content-center">
    <div class="navbar-brand d-none d-sm-block pr-3 pt-0">Surveillance Center</div>
    <ul class="navbar-nav">
      <li class="nav-item pr-3">
        <router-link :to="{ name: 'recordings' }" class="nav-link">
          <i class="material-icons">apps</i>
        </router-link>
      </li>
      <li class="nav-item pr-3">
        <router-link :to="{ name: 'liveview' }" class="nav-link">
          <i class="material-icons">photo_camera</i>
        </router-link>
      </li>
      <li class="nav-item pr-3">
        <router-link :to="{ name: 'livestream' }" class="nav-link">
          <i class="material-icons">videocam</i>
        </router-link>
      </li>
      <li class="nav-item">
        <router-link :to="{ name: 'settings' }" class="nav-link">
          <i class="material-icons">settings</i>
        </router-link>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  name: 'AppNavigation',

  data() {
    return {
      showNavigation: true,
    };
  },

  created() {
    if (this.$route.name === 'login') {
      this.showNavigation = false;
    }
  },

  watch: {
    $route(value) {
      if (value.name === 'login') {
        this.showNavigation = false;
      } else {
        this.showNavigation = true;
      }
    },
  },

};
</script>

<style lang="scss">
  .navbar-dark .navbar-brand {
    color: rgba(255, 255, 255, 0.75);

    &:hover {
      color: rgba(255, 255, 255, 0.75);
    }
  }

  .router-link-active {
    color: #fff !important;
  }
</style>
