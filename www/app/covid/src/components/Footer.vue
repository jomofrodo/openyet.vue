<template>
  <footer>
    <div class="container">
      <router-link class="logo-font" :to="{ name: 'home', params: {} }">
        OpenYet?
      </router-link>
      <span class="attribution">
        A data analysis project from
        <a rel="noopener noreferrer" target="blank" href="http://netazoic.com"
          >Netazoic</a
        >. Code &amp; design licensed under MIT.
      </span>
    </div>
  </footer>
</template>

<script>
export default {
  name: "Footer"
};
</script>