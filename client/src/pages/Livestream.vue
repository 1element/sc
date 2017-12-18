<template>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Livestream</h1>
      </div>
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="row">
      <div class="col-lg-6 col-md-6 col-xs-12" v-for="camera in cameras">
        <router-link :to="{ name: 'livestream-detail', params: { id: camera.id } }" class="thumbnail">
          <img class="img-responsive" v-bind:src="camera.streamUrl"/>
          <div class="caption">
            <div class="text-center">{{ camera.name }}</div>
          </div>
        </router-link>
      </div>
    </div>

  </div>
</template>

<script>
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';

export default {
  name: 'Livestream',

  components: { AlertMessage },

  created() {
    this.fetchData();
  },

  data() {
    return {
      cameras: [],
      errorMessage: '',
    };
  },

  methods: {
    fetchData() {
      api().get('cameras')
        .then((response) => {
          this.cameras = response.data;
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },
  },
};
</script>

<style lang="scss">

</style>
