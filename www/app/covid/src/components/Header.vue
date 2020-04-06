<template>
  <nav class="navbar navbar-light">
    <div class="container">
      <router-link class="navbar-brand" :to="{ name: 'home' }">
        Covid19
      </router-link>
      <ul v-if="!isAuthenticated" class="nav navbar-nav pull-xs-right">
        <li class="nav-item">
          <router-link
            class="nav-link"
            active-class="active"
            exact
            :to="{ name: 'home' }"
          >
            Home
          </router-link>
        </li>
      </ul>

    </div>
  </nav>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "Header",
  computed: {
    isAuthenticated(){
      return false;
    }
    // ...mapGetters(["currentUser", "isAuthenticated"])
  }
};
</script>
