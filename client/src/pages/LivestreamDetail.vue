<template>
  <div>
    <div class="row">
      <div class="col">
        <h5>Livestream: {{ camera.name }}</h5>
      </div>
    </div>
    <!-- /.row -->

    <div class="row">
      <div class="col">
        <b-alert variant="danger" dismissible :show="errorMessage!==''" @dismissed="errorMessage=''">
          {{ errorMessage }}
        </b-alert>
      </div>
    </div>
    <!-- /.row -->

    <div class="row">
      <div class="col edge-to-edge">
        <img class="img-fluid" v-bind:src="camera.streamUrl"/>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import api from '../services/api';

export default {
  name: 'Livestream-Detail',

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
