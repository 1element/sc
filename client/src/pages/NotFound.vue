<template>
  <div class="container error-page">
    <h1>404</h1>
    <h3>Not found</h3>
    <p>
      There seems to be a problem with the page you requested: {{ currentLocation }}<br/>
      The page does not exist.
    </p>
  </div>
</template>

<script>
export default {
  name: 'NotFound',

  computed: {
    currentLocation() {
      return window.location.href;
    },
  },
};
</script>

<style lang="scss">

.error-page {
  text-align: center;

  h1 {
    font-size: 5.5rem;
  }

  p {
    color: #647788;
    font-size: 0.8rem;
  }
}

</style>
