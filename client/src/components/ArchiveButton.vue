<template>
  <b-button size="sm"
    variant="outline-primary"
    v-bind:disabled="isLoading"
    v-bind:active="isLoading"
    v-on:click="process">Archive recordings</b-button>
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
  },

  data() {
    return {
      isLoading: false,
    };
  },

  methods: {
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
  },
};
</script>

<style lang="scss">

</style>
