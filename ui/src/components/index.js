import Vue from 'vue'

import Dialog from './Dialog'


Vue.component('Dialog',Dialog)
//全局注册事件处理中心
Vue.prototype.$bus = new Vue()