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

    <div class="row">
      <div class="col edge-to-edge">
        <figure class="figure">
          <img class="img-fluid" v-bind:src="`${properties.imageBaseUrl}${recording.fileName}`"/>
          <figcaption class="figure-caption text-center" v-if="recording.receivedAt">
            {{ recording.receivedAt.year }}-{{ recording.receivedAt.monthValue }}-{{ recording.receivedAt.dayOfMonth }}
            {{ recording.receivedAt.hour }}:{{ recording.receivedAt.minute }}:{{ recording.receivedAt.second }}
          </figcaption>
        </figure>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import api from '../services/api';

export default {
  name: 'Recordings-Detail',

  created() {
    this.fetchProperties();
    this.fetchData();
  },

  data() {
    return {
      recording: [],
      properties: [],
      errorMessage: '',
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
        })
        .catch((error) => {
          this.errorMessage = error.message;
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
