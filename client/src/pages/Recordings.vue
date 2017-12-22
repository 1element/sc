<template>
  <div>
    <div class="row">
      <div class="col">
        <h5>Recordings</h5>
      </div>
      <div class="col-auto" v-if="currentArchiveFilter !== 'true'">
        <archive-button v-bind:images="recordings" v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
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

    <div class="row mt-1 mb-2">
      <div class="col">
        <div class="card">
          <div class="card-header">
            {{ page.totalElements }} recordings. Page {{ page.number + 1 }} of {{ page.totalPages }}.
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
        <a v-bind:href="`${properties.imageBaseUrl}${recording.fileName}`">
          <figure class="figure">
            <img class="img-fluid img-thumbnail" v-bind:src="`${properties.imageBaseUrl}${properties.imageThumbnailPrefix}${recording.fileName}`"/>
            <figcaption class="figure-caption text-center">
              {{ recording.receivedAt.year }}-{{ recording.receivedAt.monthValue }}-{{ recording.receivedAt.dayOfMonth }}
              {{ recording.receivedAt.hour }}:{{ recording.receivedAt.minute }}:{{ recording.receivedAt.second }}
            </figcaption>
          </figure>
        </a>
      </div>
    </div>
    <!-- /.row -->

    <div class="row mt-3 text-center" v-if="currentArchiveFilter !== 'true'">
      <div class="col">
        <archive-button v-bind:images="recordings" v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
      </div>
    </div>
    <!-- /.row -->

    <div class="row mt-2 justify-content-center">
      <div class="col">
        <nav>
          <!-- TODO: replace with <b-pagination> component -->
          <paginate
            :pageCount="totalPages"
            :clickHandler="paginateClickCallback"
            :prevText="'Previous'"
            :nextText="'Next'"
            :containerClass="'pagination justify-content-center'"
            :page-class="'page-item'"
            :prev-class="'page-item'"
            :page-link-class="'page-link'"
            :prev-link-class="'page-link'"
            :next-class="'page-item'"
            :next-link-class="'page-link'"
            ref="paginate">
          </paginate>
        </nav>
      </div>
    </div>
    <!-- /.row -->
  </div>
</template>

<script>
import Paginate from 'vuejs-paginate';
import Dropdown from '../components/Dropdown';
import ArchiveButton from '../components/ArchiveButton';
import api from '../services/api';

export default {
  name: 'Recordings',
  components: { Dropdown, ArchiveButton, Paginate },

  created() {
    this.fetchProperties();
    this.fetchCameras();
    this.fetchRecordings();
  },

  data() {
    return {
      currentCameraId: '',
      currentPageNumber: 0,
      currentArchiveFilter: 'false',
      totalPages: 0,
      cameras: [{ id: '', name: 'all cameras' }],
      filter: [{ id: 'false', name: 'new recordings' }, { id: 'true', name: 'archived recordings' }],
      recordings: [],
      page: [],
      properties: [],
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
      api().get(`recordings?camera=${this.currentCameraId}&page=${this.currentPageNumber}&archive=${this.currentArchiveFilter}`)
        .then((response) => {
          this.recordings = response.data._embedded.surveillanceImageList;
          this.page = response.data.page;
          this.totalPages = response.data.page.totalPages;
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
     * Callback handler for pagination.
     */
    paginateClickCallback(pageNumber) {
      this.currentPageNumber = pageNumber - 1;
      this.fetchRecordings();
    },

    /**
     * Resets current page number.
     */
    resetCurrentPageNumber() {
      this.$refs.paginate.selected = 0;
      this.currentPageNumber = 0;
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
</style>
