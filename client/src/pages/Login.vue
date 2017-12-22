<template>
  <div class="container">

    <div class="form-signin">
      <h2 class="form-signin-heading">Surveillance Center</h2>
      <label for="inputUsername" class="sr-only">Username</label>
      <input v-model="username" type="text" id="inputUsername" class="form-control" placeholder="Username" required autofocus>
      <label for="inputPassword" class="sr-only">Password</label>
      <input v-model="password" type="password" id="inputPassword" class="form-control" placeholder="Password" required>
      <button class="btn btn-lg btn-primary btn-block" v-on:click="login">Login</button>
    </div>

    <div class="error-message">
      <b-alert variant="danger" dismissible :show="showErrorMessage" @dismissed="showErrorMessage=false">
        <strong>Could not login.</strong> {{ errorMessage }}
      </b-alert>
    </div>

  </div>
</template>

<script>
import api from '../services/api';

export default {
  name: 'Login',

  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      showErrorMessage: false,
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
          this.showErrorMessage = true;
          this.errorMessage = error.message;
        });
    },
  },
};
</script>

<style lang="scss">
body {
  background-color: #eee;
  padding-top: 40px;
  padding-bottom: 40px;
}

.form-signin {
  max-width: 330px;
  padding: 15px;
  margin: 0 auto;

  .form-signin-heading {
    margin-bottom: 10px;
  }

  .form-control {
    position: relative;
    box-sizing: border-box;
    height: auto;
    padding: 10px;
    font-size: 16px;

    &:focus {
      z-index: 2;
    }
  }

  input[type="text"] {
    margin-bottom: -1px;
    border-bottom-right-radius: 0;
    border-bottom-left-radius: 0;
  }

  input[type="password"] {
    margin-bottom: 10px;
    border-top-left-radius: 0;
    border-top-right-radius: 0;
  }
}

.error-message {
  margin: 25px auto;
  max-width: 500px;
}
</style>
