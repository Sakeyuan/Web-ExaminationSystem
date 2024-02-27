import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    userData: localStorage.getItem('userData') || null,
    avatarData:localStorage.getItem('avatarData') || null,
    id: 0,
    userId: 0,
  },
  mutations: {
    setUser(state, user) {
        state.user = user;
    },
    updateToken(state,token) {
      localStorage.setItem("token",token);
    },
    upDateRefreshToken(state,refreshToken) {
      localStorage.setItem("refreshToken",refreshToken);
    },
    setToken(state, token) {
      localStorage.setItem("token",token);
    },
    setRefreshToken(state, refreshToken) {
      localStorage.setItem("refreshToken",refreshToken);
    },
    setAvatarData(state, avatarData) {
      localStorage.setItem("avatarData",avatarData);
      state.avatarData = avatarData;
    },
    setUserData(state, userData) {
      localStorage.setItem("userData",userData);
      state.userData = userData;
    },
    setId(state, id) { 
      state.id = id;
      localStorage.setItem("id",id);
    },
    setUserId(state, userId) { 
      state.userId = userId;
      localStorage.setItem("userId",userId);
    },
    clearUser(state) {
      state.user = null;
      state.id = 0;
      localStorage.removeItem('user');
      localStorage.removeItem('id');
      localStorage.removeItem('userId');
      localStorage.removeItem('userData');
      localStorage.removeItem('avatarData');
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      location.reload();
    },
  },
  actions: {
      initialize({ commit }, userData) {
        localStorage.setItem("user", JSON.stringify(userData));          
        if (userData) {
            commit('setUser', userData);
            commit('setId', userData.id);
            commit('setUserId', userData.userId);
            commit('setToken', userData.token);
            commit('setRefreshToken', userData.refreshToken);
        }
      },
  },
  getters: {
    getUser: state => state.user,
    getToken: state => state.token,
    getRefreshToken: state => state.refreshToken,
    getUserData: state => state.userData,
  },
});

export default store;
