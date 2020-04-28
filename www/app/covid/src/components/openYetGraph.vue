<template>
  <div>
    <h1>HELLO WORLD</h1>
    <ve-line :data="chartDataSample"></ve-line>
  </div>
</template>

<script>
import VeLine from "v-charts/lib/line.common";
import * as csvLib from "@/lib/csvLib";
import moment from "moment";
var _ = require("lodash");
export default {
  components: { VeLine },
  props: ["oyRec"],
  data() {
    return {
      or: this.oyRec,
      chartDataSample: {
        columns: ["date", "sales"],
        rows: [
          { date: "1月1日", sales: 123 },
          { date: "1月2日", sales: 1223 },
          { date: "1月3日", sales: 2123 },
          { date: "1月4日", sales: 4123 },
          { date: "1月5日", sales: 3123 },
          { date: "1月6日", sales: 7123 }
        ]
      },
      cols: ["Date", "Value"],
      confirmed: [],
      deaths: [],
      ppositive: []
    };
  },
  computed: {
    chartData: function() {
      let cd = {
        columns: this.cols,
        rows: this.heroes
      };
      return cd;
    },
    heroes: function() {
      const or = this.or;
      if (!or) return null;
      const vm = this;
      let hrecs = [];
      hrecs.push({ type: "confd", date: or.week3, value: or.confd3 });
      hrecs.push({ type: "confd", date: or.week2, value: or.confd2 });
      hrecs.push({ type: "confd", date: or.week1, value: or.confd1 });
      return hrecs;
    }
  },
  created() {
    const vm = this;
  },
  methods: {}
};
</script>
