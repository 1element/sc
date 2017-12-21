import axios from 'axios';
import router from '../router';

export default function () {
  const axiosClient = axios.create({
    baseURL: process.env.API_BASE_URL,
  });

  axiosClient.interceptors.response.use(response => response, (error) => {
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
