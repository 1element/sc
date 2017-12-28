<template>
  <div>
    <div class="row">
      <div class="col">
        <h5>Livestream</h5>
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
      <div class="col-sm edge-to-edge" v-for="camera in cameras">
        <router-link :to="{ name: 'livestream-detail', params: { id: camera.id } }">
          <figure class="figure mb-0">
            <img class="img-fluid" v-bind:src="camera.streamGeneratorUrl"/>
            <figcaption class="figure-caption text-center">{{ camera.name }}</figcaption>
          </figure>
        </router-link>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import api from '../services/api';

export default {
  name: 'Livestream',

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
