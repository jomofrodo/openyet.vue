<template>
  <div>
    <table class="status-detail">
      <tr>
        <th class="status-detail">
          Confirmed Trend
          <i class="fas fa-info-circle" :title="help.confirmed" />
        </th>
        <td class="status-detail" :class="calcStatus(oy.confTrend)">{{formatTrend(oy.confTrend)}}</td>
      </tr>
      <tr>
        <th class="status-detail">
          % Positive Trend
          <i class="fas fa-info-circle" :title="help.ppositive" />
        </th>
        <td
          class="status-detail"
          :class="calcStatus(oyRec.ppositiveTrend)"
        >{{formatTrend(oy.ppositiveTrend)}}</td>
      </tr>
      <tr>
        <th class="status-detail">
          Deaths Trend
          <i class="fas fa-info-circle" :title="help.deaths" />
        </th>
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
      oy: this.oyRec,
      help: {
        ppositive:
          "Change in % of tests for Covid19 that returned a positive result (i.e., tests showing that person being tested DOES HAVE the Covid19 disease)",
        confirmed: "Change in growth rate of number of cases of Covid19 confirmed during this period",
        deaths: "Change in growth rate of number of deaths attributed to Covid19 during this period"
      }
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