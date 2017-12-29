<template>
  <div>
    <div class="row">
      <div class="col">
        <b-alert variant="danger" dismissible :show="errorMessage!==''" @dismissed="errorMessage=''">
          {{ errorMessage }}
        </b-alert>
      </div>
    </div>
    <!-- /.row -->

    <div v-if="isLoading" class="row">
      <div class="col">
        <spinner message="Loading..."></spinner>
      </div>
    </div>

    <div class="row">
      <div class="col edge-to-edge">
        <figure class="figure">
          <img class="img-fluid" v-bind:src="`${properties.imageBaseUrl}${recording.fileName}`"/>
          <figcaption class="figure-caption text-center" v-if="recording.receivedAt">
            {{ recording.receivedAt | formatDate }}
          </figcaption>
        </figure>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import Spinner from 'vue-simple-spinner';
import api from '../services/api';

export default {
  name: 'Recordings-Detail',
  components: { Spinner },

  created() {
    this.fetchProperties();
    this.fetchData();
  },

  data() {
    return {
      recording: [],
      properties: [],
      errorMessage: '',
      isLoading: true,
    };
  },

  methods: {

    /**
     * Fetch data from REST API.
     */
    fetchData() {
      api().get(`recordings/${this.$route.params.id}`)
        .then((response) => {
          this.recording = response.data;
          this.isLoading = false;
        })
        .catch((error) => {
          this.errorMessage = error.message;
          this.isLoading = false;
        });
    },

    /**
     * Fetch configuration properties from REST API.
     */
    fetchProperties() {
      api().get('properties')
        .then((response) => {
          this.properties = response.data;
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

  },
};
</script>
