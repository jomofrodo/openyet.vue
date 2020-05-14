<template>
  <nav class="navbar navbar-light">
          <div class="nav-root">
            <ul class="nav nav-pills outline-active">
              <li class="nav-item">
                <router-link 
                  :to="{ name: 'home'}"
                  exact
                  class="nav-link"
                  active-class="active">
                  Open Yet&#63;</router-link>
              </li>
            </ul>
          </div>
          <div class="feed-toggle">
            <ul class="nav nav-pills outline-active">
               <li v-for="rt in routes" :key="rt.label"  class="nav-item">
                <router-link
                  class="nav-link"
                  :to="{ name: rt.name }"
                  exact
                  active-class="active">{{rt.label}}
                </router-link>
              </li>
             </ul>
          </div>
  </nav>
</template>
<script>
const headerRoutes = [

 {name:'docs-methodology',label:'Methodology'},
 {name: 'summary-grid', label: 'Summary Data'},
 {name:'combined-grid',label:'Time Series Data'},
  
];
import { mapGetters } from "vuex";

export default {
  name: "RwvHeader",
  data(){
    return {
      routes: headerRoutes
    }
  },
  computed: {
    ...mapGetters(["currentUser", "isAuthenticated"])
  }
};
</script>

<style scoped>
.nav-pils .nav-link{
  color: #473c3c;
}
a {
    color: #473c3c;
    text-decoration: none;
    background-color: transparent;
}
a:hover{
  color: #473c3c;
  font-weight:bold;
}
.nav-pills .nav-link.active, .nav-pills .show>.nav-link, .my-pils .nav-link.active {
    color: #111010;
    background-color: #eaecef;
    font-weight: bold;
}
</style>