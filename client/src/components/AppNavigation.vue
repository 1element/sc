<template>
  <nav v-if="showNavigation" class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <router-link :to="{ name: 'recordings' }" class="navbar-brand">
        <span>Surveillance Center</span>
        <span id="js-notifier-badge" class="badge"></span>
      </router-link>
    </div>
    <!-- /.navbar-header -->

    <div class="navbar-default sidebar" role="navigation">
      <div class="sidebar-nav navbar-collapse">
        <ul class="nav" id="side-menu">
          <li>
            <router-link :to="{ name: 'recordings' }">
              <i class="fa fa-th fa-fw"></i> <span>Recordings</span>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'liveview' }">
              <i class="fa fa-camera fa-fw"></i> <span>Liveview</span>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'livestream' }">
              <i class="fa fa-video-camera fa-fw"></i> <span>Livestream</span>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'settings' }">
              <i class="fa fa-gear fa-fw"></i> <span>Settings</span>
            </router-link>
          </li>

        </ul>
      </div>
      <!-- /.sidebar-collapse -->
    </div>
    <!-- /.navbar-static-side -->
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
