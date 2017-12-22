<template>
  <div>
    <div class="row align-items-center justify-content-between">
      <div class="col">
        <h5>Liveview: {{ camera.name }}</h5>
      </div>
      <div class="col-auto">
        <b-button variant="primary" size="sm" v-on:click="getNewSnapshot" class="icon"><i class="material-icons">refresh</i></b-button>
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
      <div class="col edge-to-edge" v-images-loaded:on="getImageLoadingCallback()">
        <figure class="figure">
          <img class="img-fluid" v-bind:src="camera.snapshotUrl"/>
          <figcaption class="figure-caption text-center">{{ currentTimestamp }}</figcaption>
        </figure>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import imagesLoaded from 'vue-images-loaded';
import api from '../services/api';
import urlUtils from '../utils/urlUtils';

const cameraOfflineAsset = require('../assets/camera-offline.svg');

export default {
  name: 'Liveview-Detail',

  created() {
    this.fetchData();
    this.getCurrentTimestamp();
  },

  data() {
    return {
      camera: [],
      currentTimestamp: '',
      errorMessage: '',
    };
  },

  directives: {
    imagesLoaded,
  },

  methods: {

    /**
     * Fetch data from REST API.
     */
    fetchData() {
      api().get(`cameras/${this.$route.params.id}`)
        .then((response) => {
          this.camera = response.data;
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

    /**
     * Reload snapshot url.
     */
    getNewSnapshot() {
      this.camera.snapshotUrl = urlUtils.appendHashFragment(this.camera.snapshotUrl);
      this.getCurrentTimestamp();
    },

    /**
     * Returns current timestamp.
     */
    getCurrentTimestamp() {
      const date = new Date();
      this.currentTimestamp = `${date.toDateString()} ${date.toLocaleTimeString()}`;
    },

    /**
     * Callback function that will be executed as soon as the camera image has been loaded.
     */
    getImageLoadingCallback() {
      return {
        progress: (instance, image) => {
          if (!image.isLoaded) {
            const brokenImage = image;
            brokenImage.img.src = cameraOfflineAsset;
          }
        },
        always: () => {
        },
      };
    },

  },
};
</script>
