<template>
  <div class="dropdown">
    <button class="btn btn-default dropdown-toggle" type="button" v-bind:id="id"
            data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
      <span>{{ currentSelectionName }}</span>
      <span class="caret"></span>
    </button>
    <ul class="dropdown-menu" v-bind:aria-labelledby="id">
      <li v-for="item in items">
        <a v-on:click="selected(item.id)">{{ item.name }}</a>
      </li>
    </ul>
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

  mounted() {
    this.id = this._uid;
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
