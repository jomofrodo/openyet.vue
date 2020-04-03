<template>
  <div id="app">
    <Header />
    <router-view></router-view>
    <Footer />
  </div>
</template>

<script>
import Header from "@/components/Header";
import Footer from "@/components/Footer";

export default {
  name: "netazoic-covid19",
  components: {
    Header,
    Footer
  }
};
</script>

<style></style>