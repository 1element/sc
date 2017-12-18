<template>
  <button type="button" class="btn btn-primary has-spinner"
    v-bind:disabled="isLoading"
    v-bind:class="{ active: isLoading }"
    v-on:click="process">Archive recordings</button>
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
  .has-spinner {
    .fa-spinner {
      opacity: 0;
      max-width: 0;
    }
    &.active .fa-spinner {
      opacity: 1;
      max-width: 50px;
    }
  }
</style>
