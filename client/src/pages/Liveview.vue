<template>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Liveview</h1>
      </div>
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="row">
      <div class="col-lg-12">
        <refresh-button v-bind:is-loading="isLoading" v-on:refresh="getNewSnapshots"></refresh-button>
      </div>
    </div>
    <!-- /.row -->

    <div class="row margin-top-s" v-images-loaded:on="getImageLoadingCallback()">
      <div class="col-lg-6 col-md-6 col-xs-12" v-for="camera in cameras">
        <router-link :to="{ name: 'liveview-detail', params: { id: camera.id } }" class="thumbnail">
          <img class="img-responsive" v-bind:src="camera.snapshotUrl"/>
          <div class="caption">
            <div class="text-center">{{ camera.name }}</div>
            <div class="text-center">
              {{ currentTimestamp }}
            </div>
          </div>
        </router-link>
      </div>
    </div>

  </div>
</template>

<script>
import imagesLoaded from 'vue-images-loaded';
import RefreshButton from '../components/RefreshButton';
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';
import urlUtils from '../utils/urlUtils';

const cameraOfflineAsset = require('../assets/camera-offline.svg');

export default {
  name: 'Liveview',

  components: { RefreshButton, AlertMessage },

  created() {
    this.fetchData();
    this.getCurrentTimestamp();
  },

  data() {
    return {
      cameras: [],
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
      api().get('cameras')
        .then((response) => {
          this.cameras = this.appendHashFragments(response.data);
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

    /**
     * Reload snapshot urls and retrieve new images.
     */
    getNewSnapshots() {
      this.isLoading = true;
      this.cameras = this.appendHashFragments(this.cameras);
      this.getCurrentTimestamp();
    },

    /**
     * Append hash fragments to snapshot urls.
     * This is to prevent browsers to cache the image object.
     */
    appendHashFragments(cameraList) {
      const modifiedCameraList = cameraList;

      for (let i = 0; i < cameraList.length; i += 1) {
        modifiedCameraList[i].snapshotUrl = urlUtils.appendHashFragment(cameraList[i].snapshotUrl);
      }

      return modifiedCameraList;
    },

    /**
     * Returns current timestamp.
     */
    getCurrentTimestamp() {
      const date = new Date();
      this.currentTimestamp = `${date.toDateString()} ${date.toLocaleTimeString()}`;
    },

    /**
     * Callback function that will be executed as soon as all images have been loaded,
     * either broken or successfully.
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
