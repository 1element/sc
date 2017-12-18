import axios from 'axios';

export default function () {
  return axios.create({
    baseURL: process.env.API_BASE_URL,
  });
}
