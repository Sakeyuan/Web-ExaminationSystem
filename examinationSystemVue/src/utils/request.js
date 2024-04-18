import router from '@/router';
import axios from 'axios';
import { Message } from 'element-ui';
import store from '@/store';
import api from '@/api';
import { serverIp } from '../../public/config';
import { serverPort } from '../../public/config';

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
    const { data, config } = response;
    return data;
  },
  async (error) => {
    console.log(error);
    if (error.code === 'ECONNABORTED' && error.message.indexOf('timeout') !== -1) {
      // 处理请求超时
      handleTimeoutError();
      return Promise.reject(error);
    }
   else if (error.response && error.response.status && error.response.status === 4003) {
      // 令牌失效，尝试刷新令牌
      await handleTokenRefresh(error);
    } else if (error.response && error.response.status && (error.response.status === 4001 || error.response.status === 4002)) {
      // 处理用户身份认证错误
      handleUserAuthenticationError();
    } else {
      // 其他错误，加入请求队列以便稍后重试
      return new Promise((resolve, reject) => {
        addToQueue((token) => resolve(instance(error.config)));
        setTimeout(() => reject(error), calculateRetryDelay());
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

        retryRequest(token);
        clearQueue();

        return instance(error.config);
      }
    } catch (error) {
      console.error('刷新令牌时出错:', error);
      handleUserAuthenticationError();
      return Promise.reject(error);
    } finally {
      isRefreshing = false;
    }
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

// 计算重试间隔
function calculateRetryDelay() {
  const maxDelay = 3000;
  const baseDelay = 500;

  const retries = requests.length;
  const delay = Math.min(baseDelay * Math.pow(2, retries), maxDelay);

  return delay;
}

export default instance;
