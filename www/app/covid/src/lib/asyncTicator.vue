<template>
    <transition name="fade2">
    <div v-if="flgVisible" id="asyncticator"><img id="activity" :src="asyncImg" class="async-img"/></div>
    </transition>
</template>

<script>
import async_gif from "../assets/async.gif";
export default {
  name: "asyncTicator",
  props: { flgVisible: Boolean },
  data: function() {
    return {
      asyncImg: async_gif
    };
  },
  ref: "asyncDiv"
};
</script>
<style scoped>
div#asyncticator {
  position: fixed;
  top: 30%;
  left: 50%;
  width: 4em;
  height: 4em;
  margin-top: -2em;
  margin-left: -2em;
  color: white;
  border: 0px solid blue;
}
img#activity {
  width: 100%;
}
</style>