import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: "/",
      name: "admin-home",
      component: () => import("@/views/DataUpdate")
    },
    { path: "/home",
      name: "home-alias",
      component: () => import("@/views/OpenYet")
    },
    {
      path: "/grid/combined",
      name: "combined-grid",
      component: () => import("@/views/CombinedGrid")
    },
    {
      path: "/cvd/admin",
      name: "api"
    }
    
  ]
});
