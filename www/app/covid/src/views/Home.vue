<template>
  <div class="home-page">
    <div class="banner">
      <div class="container">
        <h1 class="logo-font">Covid 19</h1>
        <p>Analysis of covid19 data</p>
      </div>
    </div>
    <div class="container page">
      <div class="row">
        <div class="col-md-9">
          <div class="feed-toggle">
            <ul class="nav nav-pills outline-active">
              <li class="nav-item">
                <router-link
                  :to="{ name: 'nations-static' }"
                  exact
                  class="nav-link"
                  active-class="active"
                >Static Feed</router-link>
              </li>
              <li class="nav-item">
                <router-link
                  :to="{ name: 'combined-grid' }"
                  exact
                  class="nav-link"
                  active-class="active"
                >Time Series Data</router-link>
              </li>
            </ul>
          </div>
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

import { FETCH_TAGS } from "@/store/actions.type";

export default {
  name: "home",
  components: {},
  mounted() {
    this.$store.dispatch(FETCH_TAGS);
  },
  computed: {
    // ...mapGetters(["isAuthenticated", "tags"]),
    isAuthenticated() {
      return false;
    },
    tag() {
      return this.$route.params.tag;
    }
  }
};
</script>
