<template>
<body class="container-fluid d-flex flex-column grow">
  <div id="asyncticator-mount"></div>
  <div class="row">
    <aside
      class="col-2 order-md-1 left-nav"
      :class="{'col-md-1':flgSidebar,'col-md-0':!flgSidebar}"
    >
      <transition>
        <div v-if="flgSidebar">Sidebar</div>
      </transition>
    </aside>
    <header class="col-10 col-md-12 order-md-0 navbarx">
      <Header />
    </header>
    <div class="col-12 order-last main" :class="{'col-md-11':flgSidebar,'col-md-12':!flgSidebar}">
      <div v-if="false">
        <input type="checkbox" v-model="flgSidebar" />
      </div>
      <router-view></router-view>
    </div>
  </div>
  <footer>
    <Footer />
  </footer>
</body>
</template>

<script>
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import as from "@/lib/libAsyncticator";

export default {
  name: "netazoic-covid19",
  components: {
    Header,
    Footer
  },
  created() {
    as.setupAxiosIndicators(this.$http);
  },
  data() {
    return {
      flgSidebar: false
    };
  }
};
</script>

<style scoped>
/* get the main container to fill height */
.grow {
  flex: 1;
} /* scroll the content area as needed */
.main {
  /* overflow-y: auto; */
  min-height: 40rem;
}
.navbarx {
  height: 3.6rem;
  background-color: #fff;
  border-bottom: 1px solid #eaecef;
}
.left-nav {
  border-right: 1px solid #eaecef;
}
</style>