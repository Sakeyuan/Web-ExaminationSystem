import router from '@/router';
import axios from 'axios';
import { Message } from 'element-ui';
import store from '@/store';
import api from '@/api';
import { serverIp, serverPort } from '../../public/config';

const instance = axios.create({
  baseURL: `https://${serverIp}:${serverPort}`,
  timeout: 30000
});

let isRefreshing = false;
let requests = [];

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  async (response) => {
    return response.data;
  },
  async (error) => {
    const { response, config } = error;
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      handleTimeoutError();
      return Promise.reject(error);
    } else if (response && response.status === 4003) {
      // 令牌失效，尝试刷新令牌
      return handleTokenRefresh(error);
    } else if (response && (response.status === 4001 || response.status === 4002)) {
      handleUserAuthenticationError();
    } else {
      return new Promise((resolve, reject) => {
        addToQueue((token) => {
          config.headers['Authorization'] = `Bearer ${token}`;
          resolve(instance(config));
        });
        setTimeout(() => reject(error), 1000);
      });
    }
  }
);

// 处理请求超时
function handleTimeoutError() {
  Message.error('请求超时，请检查网络连接或稍后重试');
}

// 尝试刷新令牌
async function handleTokenRefresh(error) {
  if (!isRefreshing) {
    isRefreshing = true;
    try {
      const res = await api.authObj.refreshToken({ refreshToken: localStorage.getItem('refreshToken') });
      if (res && res.code === 200) {
        console.log('Token刷新成功');
        const { data: token, other: refreshToken } = res;
        store.commit('setToken', token);
        store.commit('setRefreshToken', refreshToken);

        //重新使用新的token发起请求
        retryRequest(token);
        clearQueue();
      } else {
        handleUserAuthenticationError();
      }
    } catch (refreshError) {
      console.error('刷新令牌时出错:', refreshError);
      handleUserAuthenticationError();
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  } else {
    //如果请求成功，则将请求结果作为成功的值，通过调用 resolve 来完成 Promise。如果请求在一定时间内没有成功，则将请求失败的原因作为失败的值，通过调用 reject 来完成 Promise。
    return new Promise((resolve, reject) => {
      addToQueue((token) => {
        error.config.headers['Authorization'] = `Bearer ${token}`;
        resolve(instance(error.config));
      });
      setTimeout(() => reject(error), calculateRetryDelay());
    });
  }
}

// 处理用户身份认证错误
function handleUserAuthenticationError() {
  store.commit('clearUser');
  router.push('/');
}

// 添加请求到队列
function addToQueue(callback) {
  requests.push(callback);
}

// 重新执行队列中的请求
function retryRequest(token) {
  requests.forEach((cb) => cb(token));
}

// 清空请求队列
function clearQueue() {
  requests = [];
}

export default instance;
