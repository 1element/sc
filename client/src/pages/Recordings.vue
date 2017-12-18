<template>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Recordings</h1>

        <div class="panel panel-default">
          <div class="panel-heading">
            <span>{{ page.totalElements }} recordings. Page {{ page.number + 1 }} of {{ page.totalPages }}.</span>
          </div>
          <div class="panel-body">

            <div class="row">
              <div class="col-xs-6">
                <dropdown v-bind:items="cameras" initial-selection-id="" v-on:selected="cameraDropdownCallback"></dropdown>
              </div>
              <div class="col-xs-6">
                <dropdown v-bind:items="filter" initial-selection-id="false" v-on:selected="filterDropdownCallback"></dropdown>
              </div>
            </div>

          </div>
        </div>
      </div>
      <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="row" v-if="currentArchiveFilter !== 'true'">
      <div class="col-lg-12 margin-bottom-m">
        <archive-button v-bind:images="recordings" v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
      </div>
    </div>
    <!-- /.row -->

    <div class="row">
      <div class="col-lg-2 col-md-3 col-sm-4 col-xs-6" v-for="recording in recordings">
        <a class="thumbnail" data-toggle="lightbox" data-gallery="recordings-gallery" v-bind:href="`${properties.imageBaseUrl}${recording.fileName}`">
          <img class="img-responsive" v-bind:src="`${properties.imageBaseUrl}${properties.imageThumbnailPrefix}${recording.fileName}`"/>
          <div class="caption text-center">
            {{ recording.receivedAt.year }}-{{ recording.receivedAt.monthValue }}-{{ recording.receivedAt.dayOfMonth }}
            {{ recording.receivedAt.hour }}:{{ recording.receivedAt.minute }}:{{ recording.receivedAt.second }}
          </div>
        </a>
      </div>
    </div>
    <!-- /.row -->

    <div class="row" v-if="currentArchiveFilter !== 'true'">
      <div class="col-lg-12 margin-bottom-m">
        <archive-button v-bind:images="recordings" v-on:processed="archiveProcessedCallback" v-on:error="archiveErrorCallback"></archive-button>
      </div>
    </div>
    <!-- /.row -->

    <div class="row">
      <div class="col-lg-12 text-center">
        <paginate
          :pageCount="totalPages"
          :clickHandler="paginateClickCallback"
          :prevText="'Previous'"
          :nextText="'Next'"
          :containerClass="'pagination'"
          ref="paginate">
        </paginate>
      </div>
    </div>
    <!-- /.row -->

  </div>
  <!-- /#page-wrapper -->
</template>

<script>
import Paginate from 'vuejs-paginate';
import Dropdown from '../components/Dropdown';
import ArchiveButton from '../components/ArchiveButton';
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';

export default {
  name: 'Recordings',
  components: { Dropdown, ArchiveButton, AlertMessage, Paginate },

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
