import router from '@/router';
import axios from 'axios'
import { Message } from 'element-ui'; // 引入 Message 组件
import store from '@/store';
import api from '@/api';
import { serverIp } from '../../public/config';

const instance = axios.create({
  baseURL: `http://${serverIp}:9090`,  // 使用反引号（`）进行模板字符串
  timeout: 30000
});


instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config
}, error => {
    return Promise.reject(error)
});

let isRefreshing = false;
let requests = [];

instance.interceptors.response.use(
  async response => {
    const { data, config } = response;
    return data;
  },
  async error => {
   if(error.code == 'ECONNABORTED' && error.message.indexOf('timeout') != -1 ){
      Message.error("请求超时，请检查网络连接或稍后重试")
      return Promise.reject(error);
	  }
    else if (error.response.status === 4003) {
      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const res = await api.authObj.refreshToken({ refreshToken: localStorage.getItem('refreshToken') });
          if (res && res.code === 200) {
            console.log("token刷新成功");
            const { data: token, other: refreshToken } = res;
            store.commit("setToken", token);
            store.commit("setRefreshToken", refreshToken);

            // 执行之前失败的请求
            requests.forEach((cb) => cb(token));
            requests = []; // 清空数组，以便重新执行请求

            // 重新尝试原始请求
            return instance(error.config);
          }
        } catch (error) {
          console.error("刷新令牌时出错:", error);
          store.commit("clearUser");
          router.push('/');
          return Promise.reject(error);
        } finally {
          isRefreshing = false;
        }
      }
    }
    else if (error.response.status === 4001 || error.response.status === 4002) {
      store.commit('clearUser')
      router.push('/')
    }
    else {
      return new Promise(resolve => {
        requests.push(() => resolve(instance(error.config)));
      });
    }
  }
);

export default instance

