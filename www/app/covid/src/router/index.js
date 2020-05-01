import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("@/views/OpenYet")
    },
    { path: "/home",
      name: "home-alias",
      component: () => import("@/views/OpenYet")
    },
    {
      path: "/nations/static",
      name: "nations-static",
      component: () => import("@/views/Static")
    },
    {
      path: "/grid/combined",
      name: "combined-grid",
      component: () => import("@/views/CombinedGrid")
    },
    { 
      path: "/docs/methodology",
      name: "docs-methodology",
      component: () => import("@/views/docs/Methodology")
    }
    
  ]
});
