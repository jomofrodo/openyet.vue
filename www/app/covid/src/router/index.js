import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      component: () => import("@/views/Home"),
      children: [
        {
          path: "",
          name: "home",
          component: () => import("@/views/HomeGlobal")
        }
      ]
    },
    {
      path: "/nations/static",
      name: "nations-static",
      component: () => import("@/views/Static")
    },
    {
      path: "/nations",
      name: "nations",
      component: () => import("@/views/Nations")
    },
    {
      path: "/grid/combined",
      name: "combined-grid",
      component: () => import("@/views/CombinedGrid")
    },
    {
      path: "/data/update",
      name: "data-update",
      component: () => import("@/views/DataUpdate")
    }
    
  ]
});
