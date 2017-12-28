<template>
  <b-button-group>
    <b-button size="sm"
      variant="outline-primary"
      v-bind:disabled="isLoading"
      v-bind:active="isLoading"
      v-on:click="process">Archive recordings</b-button>
    <b-dropdown size="sm"
      variant="outline-primary"
      v-bind:disabled="isLoading">
        <b-dropdown-item v-on:click="processAll">Archive all recordings</b-dropdown-item>
    </b-dropdown>
  </b-button-group>
</template>

<script>
import api from '../services/api';

export default {
  name: 'ArchiveButton',

  props: {
    images: {
      type: Array,
      required: true,
    },
    latestRecordingReceivedAt: {
      required: true,
    },
  },

  data() {
    return {
      isLoading: false,
    };
  },

  methods: {

    /**
     * Process archive recordings action.
     */
    process() {
      this.isLoading = true;

      const data = [];
      this.images.forEach((image) => {
        data.push({ id: image.id, archived: true });
      });

      api().patch('recordings', data)
        .then(() => {
          this.$emit('processed');
          this.isLoading = false;
        })
        .catch((error) => {
          this.$emit('error', error);
        });
    },

    /**
     * Process archive all recordings action.
     */
    processAll() {
      this.isLoading = true;

      const data = { dateBefore: this.latestRecordingReceivedAt, archived: true };
      api().post('recordings', data)
        .then(() => {
          this.$emit('processed');
          this.isLoading = false;
        })
        .catch((error) => {
          this.$emit('error', error);
        });
    },
  },
};
</script>

<style lang="scss">

</style>
