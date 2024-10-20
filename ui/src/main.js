import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import DialogComponent from './components/DialogComponent'

const app = createApp(App);
app.use(router);
app.component('DialogComponent', DialogComponent);
app.mount('#app');