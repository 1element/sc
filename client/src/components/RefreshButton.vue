<template>
  <button type="button" class="btn btn-primary has-spinner"
      v-bind:disabled="isLoading"
      v-bind:class="{ active: isLoading }"
      v-on:click="refresh">
    <i class="fa fa-spinner fa-spin"></i> <span>Refresh</span>
  </button>
</template>

<script>
export default {
  name: 'RefreshButton',

  props: {
    isLoading: {
      type: Boolean,
      required: true,
    },
  },

  methods: {
    refresh() {
      this.$emit('refresh');
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
