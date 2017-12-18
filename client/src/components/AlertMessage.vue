<template>
  <div v-if="text" class="alert alert-dismissible" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
    {{ text }}
  </div>
</template>

<script>
export default {
  name: 'AlertMessage',

  props: {
    text: {
      type: String,
      required: true,
    },
  },

};
</script>

<style lang="scss">

</style>
