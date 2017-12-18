<template>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Livestream: {{ camera.name }}</h1>
      </div>
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="col-lg-12">
      <div class="thumbnail">
        <img class="img-responsive" v-bind:src="camera.streamUrl"/>
        <div class="caption">
          <div class="text-center">{{ camera.name }}</div>
        </div>
      </div>
    </div>

  </div>
  <!-- /.page-wrapper -->
</template>

<script>
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';

export default {
  name: 'Livestream-Detail',

  components: { AlertMessage },

  created() {
    this.fetchData();
  },

  data() {
    return {
      camera: [],
      errorMessage: '',
    };
  },

  methods: {
    fetchData() {
      api().get(`cameras/${this.$route.params.id}`)
        .then((response) => {
          this.camera = response.data;
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
