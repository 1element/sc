<template>
  <div>
    <div class="row">
      <div class="col">
        <h5>Recordings</h5>
      </div>
      <div class="col-auto" v-if="showArchiveButton">
        <archive-button v-bind:images="recordings" v-bind:latestRecordingReceivedAt="latestRecordingReceivedAt"
          v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
      </div>
    </div>
    <!-- /.row -->

    <div v-if="isLoading">
      <spinner message="Loading..."></spinner>
    </div>

    <div class="row">
      <div class="col">
        <b-alert variant="danger" dismissible :show="errorMessage!==''" @dismissed="errorMessage=''">
          {{ errorMessage }}
        </b-alert>
      </div>
    </div>
    <!-- /.row -->

    <div v-if="!isLoading">
      <div class="row mt-1 mb-2">
        <div class="col">
          <div class="card">
            <div class="card-header">
              {{ page.totalElements }} recordings. <span v-if="page.totalPages > 0">Page {{ page.number + 1 }} of {{ page.totalPages }}.</span>
            </div>
            <div class="card-body">
              <div class="row justify-content-between">
                <div class="col">
                  <dropdown v-bind:items="cameras" initial-selection-id="" v-on:selected="cameraDropdownCallback"></dropdown>
                </div>
                <div class="col-auto">
                  <dropdown v-bind:items="filter" initial-selection-id="false" v-on:selected="filterDropdownCallback"></dropdown>
                </div>
              </div>
            </div>
          </div>
          <!-- /.card -->
        </div>
        <!-- /.col -->
      </div>
      <!-- /.row -->

      <div class="row thumbnails">
        <div class="col-4 col-xl-1 col-lg-2 col-md-2 col-sm-3 edge-to-edge" v-for="recording in recordings">
          <router-link :to="{ name: 'recordings-detail', params: { id: recording.id } }">
            <figure class="figure">
              <img class="img-fluid img-thumbnail" v-bind:src="`${properties.imageBaseUrl}${properties.imageThumbnailPrefix}${recording.fileName}`"/>
              <figcaption class="figure-caption text-center">{{ recording.receivedAt }}</figcaption>
            </figure>
          </router-link>
        </div>
      </div>
      <!-- /.row -->

      <div class="row mt-3 text-center" v-if="showArchiveButton">
        <div class="col">
          <archive-button v-bind:images="recordings" v-bind:latestRecordingReceivedAt="latestRecordingReceivedAt"
            v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
        </div>
      </div>
      <!-- /.row -->

      <div class="row mt-2">
        <div class="col">
          <b-pagination size="sm" align="center" :total-rows="page.totalElements" :per-page="page.size" v-model="currentPageNumber"></b-pagination>
        </div>
      </div>
      <!-- /.row -->
    </div>
  </div>
</template>

<script>
import Spinner from 'vue-simple-spinner';
import Dropdown from '../components/Dropdown';
import ArchiveButton from '../components/ArchiveButton';
import api from '../services/api';

export default {
  name: 'Recordings',
  components: { Spinner, Dropdown, ArchiveButton },

  created() {
    this.fetchProperties();
    this.fetchCameras();
    this.fetchRecordings();

    /**
     * Catch the window scroll event.
     */
    const catchScroll = () => {
      this.visible = (window.pageYOffset > parseInt(this.visibleOffset, 10));
    };
    window.smoothscroll = () => {
      const currentScroll = document.documentElement.scrollTop || document.body.scrollTop;
      if (currentScroll > 0) {
        window.requestAnimationFrame(window.smoothscroll);
        window.scrollTo(0, Math.floor(currentScroll - (currentScroll / 5)));
      }
    };
    window.onscroll = catchScroll;
  },

  data() {
    return {
      isLoading: true,
      currentCameraId: '',
      currentPageNumber: 1,
      currentArchiveFilter: 'false',
      cameras: [{ id: '', name: 'all cameras' }],
      filter: [{ id: 'false', name: 'new recordings' }, { id: 'true', name: 'archived recordings' }],
      recordings: [],
      page: [],
      properties: [],
      latestRecordingReceivedAt: null,
      errorMessage: '',
    };
  },

  methods: {

    /**
     * Fetch camera data from REST API.
     */
    fetchCameras() {
      api().get('cameras')
        .then((response) => {
          response.data.forEach((camera) => {
            this.cameras.push({ id: camera.id, name: camera.name });
          });
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

    /**
     * Fetch recordings data from REST API.
     */
    fetchRecordings() {
      api().get(`recordings?camera=${this.currentCameraId}&page=${this.currentPageNumber - 1}&archive=${this.currentArchiveFilter}`)
        .then((response) => {
          if (response.data._embedded) {
            this.recordings = response.data._embedded.surveillanceImageList;
            if (this.latestRecordingReceivedAt == null) {
              this.latestRecordingReceivedAt = this.recordings[0].receivedAt;
            }
          } else {
            this.recordings = [];
            this.latestRecordingReceivedAt = null;
          }
          this.page = response.data.page;
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

    /**
     * Callback for camera drop-down menu.
     */
    cameraDropdownCallback(cameraId) {
      if (this.currentCameraId !== cameraId) {
        this.resetCurrentPageNumber();
      }
      this.currentCameraId = cameraId;
      this.fetchRecordings();
    },

    /**
     * Callback for archive filter drop-down menu.
     */
    filterDropdownCallback(id) {
      if (this.currentArchiveFilter !== id) {
        this.resetCurrentPageNumber();
      }
      this.currentArchiveFilter = id;
      this.fetchRecordings();
    },

    /**
     * Callback if archive action has been processed.
     */
    archiveProcessedCallback() {
      this.resetCurrentPageNumber();
      this.fetchRecordings();
    },

    /**
     * Callback for archive action if an error occurred.
     */
    archiveErrorCallback(error) {
      this.errorMessage = error.message;
    },

    /**
     * Resets current page number.
     */
    resetCurrentPageNumber() {
      this.currentPageNumber = 1;
    },

    /**
     * Scrolls to the top of the window.
     */
    scrollToTop() {
      window.smoothscroll();
    },

  },

  watch: {
    /**
     * Watch pagination changes and fetch new recordings.
     */
    currentPageNumber() {
      this.fetchRecordings();
      this.scrollToTop();
    },
  },

  computed: {
    /**
     * Returns true if archive button should be shown, based on various conditions.
     */
    showArchiveButton() {
      if (!this.isLoading && this.currentArchiveFilter !== 'true' && this.page.totalElements > 0) {
        return true;
      }
      return false;
    },
  },

};
</script>

<style lang="scss">
.thumbnails {
  figure {
    margin: 0;
  }

  .figure-caption {
    font-size: 70%;
  }
  .img-thumbnail {
    padding: 0.05rem;
    border-radius: 0;
  }
}

.card {
  font-size: 0.8rem;
}

.dropdown-item {
  font-size: 0.95rem;
}
</style>
