import Vue from 'vue';
import Router from 'vue-router';
import Login from '@/pages/Login';
import Recordings from '@/pages/Recordings';
import RecordingsDetail from '@/pages/RecordingsDetail';
import Liveview from '@/pages/Liveview';
import LiveviewDetail from '@/pages/LiveviewDetail';
import Livestream from '@/pages/Livestream';
import LivestreamDetail from '@/pages/LivestreamDetail';
import Settings from '@/pages/Settings';

Vue.use(Router);

export default new Router({
  base: process.env.APP_BASE_URL,
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'root',
      redirect: '/liveview',
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/recordings',
      name: 'recordings',
      component: Recordings,
    },
    {
      path: '/recordings/:id',
      name: 'recordings-detail',
      component: RecordingsDetail,
    },
    {
      path: '/liveview',
      name: 'liveview',
      component: Liveview,
    },
    {
      path: '/liveview/:id',
      name: 'liveview-detail',
      component: LiveviewDetail,
    },
    {
      path: '/livestream',
      name: 'livestream',
      component: Livestream,
    },
    {
      path: '/livestream/:id',
      name: 'livestream-detail',
      component: LivestreamDetail,
    },
    {
      path: '/settings',
      name: 'settings',
      component: Settings,
    },
  ],
});
