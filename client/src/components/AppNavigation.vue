<template>
  <nav v-if="showNavigation" class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
    <router-link :to="{ name: 'recordings' }" class="navbar-brand">Surveillance Center</router-link>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarCollapse">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link :to="{ name: 'recordings' }" class="nav-link">
            <i class="fa fa-th fa-fw"></i> <span>Recordings</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'liveview' }" class="nav-link">
            <i class="fa fa-camera fa-fw"></i> <span>Liveview</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'livestream' }" class="nav-link">
            <i class="fa fa-video-camera fa-fw"></i> <span>Livestream</span>
          </router-link>
        </li>
        <li class="nav-item">
          <router-link :to="{ name: 'settings' }" class="nav-link">
            <i class="fa fa-gear fa-fw"></i> <span>Settings</span>
          </router-link>
        </li>
      </ul>
    </div>
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

</style>
