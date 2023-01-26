import { createApp } from 'vue'
import App from './App.vue'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'

import Router from 'vue-router'

import router from './router'

createApp(App).use(router).use(router).use(Router);

createApp(App).use(router).use(router).mount('#app');