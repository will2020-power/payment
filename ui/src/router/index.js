import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes:[
    {
        path: '/',
        //路由重定向
        redirect: '/pay',
        children: [
            {path: '/pay', component:()=>import('@/views/pay')},
            {path: '/paySuccess', component:()=>import('@/views/pay/pay-success')}
        ]        

    }
  ]
});

export default router