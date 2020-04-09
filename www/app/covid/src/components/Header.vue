<template>
  <nav class="navbar navbar-light">
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
                  :to="{ name: 'nations' }"
                  exact
                  class="nav-link"
                  active-class="active"
                >Dynamic Feed</router-link>
              </li>
              <li class="nav-item">
                <router-link
                  :to="{ name: 'combined-grid' }"
                  exact
                  class="nav-link"
                  active-class="active"
                >Combined Data</router-link>
              </li>
            </ul>
          </div>
  </nav>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "RwvHeader",
  computed: {
    ...mapGetters(["currentUser", "isAuthenticated"])
  }
};
</script>
