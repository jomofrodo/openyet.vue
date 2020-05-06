<template>
  <div class="main-status-stats">
    <div>
      <label>Population:</label>
      {{ niceNumber(oyRec.population) }}
    </div>
    <div>
      <label>Confirmed Cases:</label>
      {{niceNumber(oyRec.confirmed) }}
    </div>
    <div>
      <label>Confirmed/1M:</label>
      {{ niceNumber(oyRec.confirmed_1m) }}
    </div>
    <div>
      <label>Deaths</label>
      {{ niceNumber(oyRec.death) }}
    </div>
    <div>
      <label>Deaths/1M</label>
      {{ niceNumber(oyRec.deaths_1m) }}
    </div>
    <div>
      <label>Total Tests:</label>
      {{niceNumber(oyRec.totaltestresults)}}
    </div>
    <div>
      <label>Tests/1M:</label>
      {{niceNumber(oyRec.tests_1m)}}
    </div>
  </div>
</template>
<script>
import * as math from "../../lib/mathLib";
export default {
  props: ["oyRec"],
  methods: {
    niceNumber(num) {
      if (num == null) return "n/a";
      else return math.niceNumber(num);
    }
  }
};
</script>