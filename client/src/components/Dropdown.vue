<template>
  <div>
    <b-dropdown size="sm" v-bind:text="currentSelectionName">
      <b-dropdown-item v-for="item in items" :key="item.id" v-on:click="selected(item.id)">
        {{ item.name }}
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script>
export default {
  name: 'Dropdown',

  props: {
    items: {
      type: Array,
      required: true,
    },
    initialSelectionId: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      currentSelectionId: this.initialSelectionId,
      id: null,
    };
  },

  computed: {
    currentSelectionName() {
      const currentSelection = this.items.find(item => item.id === this.currentSelectionId);
      return currentSelection.name;
    },
  },

  methods: {
    selected(id) {
      this.currentSelectionId = id;
      this.$emit('selected', id);
    },
  },
};
</script>

<style lang="scss">

</style>
