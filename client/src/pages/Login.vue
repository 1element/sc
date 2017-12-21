<template>
  <div class="container">

    <h2 class="form-signin-heading">Surveillance Center</h2>
    <label for="inputUsername" class="sr-only">Username</label>
    <input v-model="username" type="text" id="inputUsername" class="form-control" placeholder="Username" required autofocus>
    <label for="inputPassword" class="sr-only">Password</label>
    <input v-model="password" type="password" id="inputPassword" class="form-control" placeholder="Password" required>
    <button class="btn btn-lg btn-primary btn-block" v-on:click="login">Login</button>

    <alert-message v-bind:text="errorMessage" class="alert-danger"></alert-message>
  </div>
  <!-- /.container -->
</template>

<script>
import AlertMessage from '../components/AlertMessage';
import api from '../services/api';

export default {
  name: 'Login',

  components: { AlertMessage },

  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
    };
  },

  methods: {
    login() {
      const credentials = {
        username: this.username,
        password: this.password,
      };

      api().post('auth', credentials)
        .then(() => {
          this.$router.replace({ name: 'root' });
        })
        .catch((error) => {
          this.errorMessage = `Could not login. ${error.message}.`;
        });
    },
  },
};
</script>

<style lang="scss">

</style>
