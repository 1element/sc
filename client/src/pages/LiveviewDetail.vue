<template>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Liveview: {{ camera.name }}</h1>
      </div>
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="row">
      <div class="col-lg-12">
        <refresh-button v-bind:is-loading="isLoading" v-on:refresh="getNewSnapshot"></refresh-button>
      </div>
    </div>
    <!-- /.row -->

    <div class="col-lg-12" v-images-loaded:on="getImageLoadingCallback()">
      <div class="thumbnail">
        <img class="img-responsive" v-bind:src="camera.snapshotUrl"/>
        <div class="caption">
          <div class="text-center">{{ camera.name }}</div>
          <div class="text-center">{{ currentTimestamp }}</div>
        </div>
      </div>
    </div>

  </div>
  <!-- /.page-wrapper -->
</template>

<script>
import imagesLoaded from 'vue-images-loaded';
import RefreshButton from '../components/RefreshButton';
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';
import urlUtils from '../utils/urlUtils';

const cameraOfflineAsset = require('../assets/camera-offline.svg');

export default {
  name: 'Liveview-Detail',
  components: { RefreshButton, AlertMessage },

  created() {
    this.fetchData();
    this.getCurrentTimestamp();
  },

  data() {
    return {
      camera: [],
      currentTimestamp: '',
      isLoading: false,
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
      this.isLoading = true;
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
          this.isLoading = false;
        },
      };
    },

  },
};
</script>
