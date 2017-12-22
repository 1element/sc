import axios from 'axios';
import router from '../router';

export default function () {
  const axiosClient = axios.create({
    baseURL: process.env.API_BASE_URL,
  });

  axiosClient.interceptors.request.use((config) => {
    router.app.$Progress.start();
    return config;
  }, error => Promise.reject(error));

  axiosClient.interceptors.response.use((response) => {
    router.app.$Progress.finish();
    return response;
  }, (error) => {
    router.app.$Progress.fail();
    // if we receive an unauthorized response
    if (error.response.status === 401) {
      // and we are not trying to login
      if (!(error.config.method === 'post' && /api\/v\d\/auth/.test(error.config.url))) {
        // redirect to login page
        router.replace({ name: 'login' });
      }
    }
    return Promise.reject(error);
  });

  return axiosClient;
}
