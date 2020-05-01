<template>
  <div>
    <table class="status-detail">
      <tr>
        <th class="status-detail">Confirmed</th>
        <td class="status-detail" :class="calcStatus(oy.confTrend)">{{formatTrend(oy.confTrend)}}</td>
      </tr>
      <tr>
        <th class="status-detail">% Positive</th>
        <td
          class="status-detail"
          :class="calcStatus(oyRec.ppositiveTrend)"
        >{{formatTrend(oy.ppositiveTrend)}}</td>
      </tr>
      <tr>
        <th class="status-detail">Deaths</th>
        <td
          class="status-detail"
          :class="calcStatus(oy.deathsTrend)"
        >{{formatTrend(oy.deathsTrend)}}</td>
      </tr>
    </table>
  </div>
</template>
<script>
export default {
  props: ["oyStatus", "oyRec"],
  data() {
    return {
      oy: this.oyRec
    };
  },
  watch: {
    oyRec(newVal) {
      this.oy = newVal;
    }
  },
  methods: {
    calcStatus(ct) {
      if (ct == 0) return "STATIC";
      else if (ct >= 0) return "CLOSED";
      else if (ct < 0) return "OPEN";
    },
    formatTrend(trend) {
      if (trend === "n/a") return trend;
      let trendStr = "";
      if (trend > 0) trendStr += "+";
      trendStr += trend;
      trendStr += "%";
      return trendStr;
    }
  }
};
</script>