import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './styles/global.css'
import './styles/index.css'
import api from './api';

import { library } from '@fortawesome/fontawesome-svg-core'

import { fas } from '@fortawesome/free-solid-svg-icons'

import { far } from '@fortawesome/free-regular-svg-icons'

import { fab } from '@fortawesome/free-brands-svg-icons'

import { FontAwesomeIcon, FontAwesomeLayers, FontAwesomeLayersText } from '@fortawesome/vue-fontawesome'

library.add(fas, far, fab)

Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.component('font-awesome-layers', FontAwesomeLayers)

Vue.component('font-awesome-layers-text', FontAwesomeLayersText)

Vue.prototype.$api = api;

Vue.config.productionTip = false
Vue.use(ElementUI, { size: "small" })

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
