import axios from 'axios'

const AXIOS = axios.create({
  baseURL: '/api/v1',
  timeout: 5000
});


export default {
    axios() {
      return AXIOS;
    }
}


