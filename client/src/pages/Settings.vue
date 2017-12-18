<template>
  <div id="page-wrapper">

    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">Settings</h1>
      </div>
    </div>
    <!-- /.row -->

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>

    <div class="row">
      <div class="col-lg-12">
        <h4>Push notifications</h4>
        <p>You can enable or disable push notifications for each camera here. If enabled, a push notification will be
          sent as soon as a new image is received via FTP.</p>
      </div>
    </div>
    <!-- /.row -->

    <div class="row" v-for="setting in settings">
      <div class="col-lg-2">{{ setting.camera.name }}</div>
      <div class="col-lg-10">
        <div class="btn-group">
          <button class="btn btn-sm"
            v-bind:class="{ 'btn-primary active': setting.pushNotificationSetting.enabled, 'btn-default': !setting.pushNotificationSetting.enabled }"
            v-on:click="toggleSetting(setting.pushNotificationSetting.cameraId)">ON</button>
          <button class="btn btn-sm"
            v-bind:class="{ 'btn-primary active': !setting.pushNotificationSetting.enabled, 'btn-default': setting.pushNotificationSetting.enabled }"
            v-on:click="toggleSetting(setting.pushNotificationSetting.cameraId)">OFF</button>
        </div>
      </div>
    </div>
    <!-- /.row -->

  </div>
  <!-- /#page-wrapper -->
</template>

<script>
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';

export default {
  name: 'Settings',
  components: { AlertMessage },

  created() {
    this.fetchSettings();
  },

  data() {
    return {
      settings: [],
      errorMessage: '',
      successMessage: '',
    };
  },

  methods: {

    /**
     * Fetch settings data from REST API.
     */
    fetchSettings() {
      api().get('push-notification-settings')
        .then((response) => {
          this.settings = response.data;
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

    /**
     * Callback if settings toggle has been clicked.
     * Push new state to REST endpoint.
     */
    toggleSetting(cameraId) {
      const currentSetting = this.settings.find(setting =>
        setting.pushNotificationSetting.cameraId === cameraId);

      const data = { enabled: !currentSetting.pushNotificationSetting.enabled };
      api().put(`push-notification-settings/${cameraId}`, data)
        .then(() => {
          this.fetchSettings();
        })
        .catch((error) => {
          this.errorMessage = error.message;
        });
    },

  },
};
</script>
