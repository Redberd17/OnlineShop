import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'
import ListProducts from "@/components/ListProducts";
import HomePage from "@/components/HomePage";

const routes = [
  {
    path: '/',
    name: 'homePage',
    component: HomePage
  },
  {
    path: '/:name/:id',
    name: 'products',
    component: ListProducts
  },
  {
    path: '/imports',
    name: 'importsProduct',
    component: () => import('../components/ImportsProducts.vue')
  },
  {
    path: '/detail/:id',
    name: 'detailInformation',
    component: () => import('../components/DetailInformation.vue')
  },
  {
    path: '/change/:id',
    name: 'changeProduct',
    component: () => import('../components/ChangeProduct.vue')
    // props: (route) => ({ query: route.query })
  },
  {
    path: '/sales',
    name: 'sales',
    component: () => import('../components/Sales.vue')
  },
  {
    path: '/node/:id/statistic/:dateStart/:dateEnd',
    name: 'statistic',
    component: () => import('../components/NodeStatistic.vue')
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router
