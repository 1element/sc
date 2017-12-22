<template>
  <div>
    <div class="row justify-content-between">
      <div class="col">
        <h5>Liveview</h5>
      </div>
      <div class="col-auto">
        <b-button variant="primary" size="sm" v-on:click="getNewSnapshots" class="icon"><i class="material-icons">refresh</i></b-button>
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

    <div class="row" v-images-loaded:on="getImageLoadingCallback()">
      <div class="col-sm edge-to-edge" v-for="camera in cameras">
        <router-link :to="{ name: 'liveview-detail', params: { id: camera.id } }">
          <figure class="figure mb-0">
            <img class="img-fluid" v-bind:src="camera.snapshotUrl"/>
            <figcaption class="figure-caption text-center">{{ camera.name }} - {{ currentTimestamp }}</figcaption>
          </figure>
        </router-link>
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
  name: 'Liveview',

  created() {
    this.fetchData();
    this.getCurrentTimestamp();
  },

  data() {
    return {
      cameras: [],
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
      this.$Progress.start();
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
          this.$Progress.finish();
        },
      };
    },

  },
};
</script>
