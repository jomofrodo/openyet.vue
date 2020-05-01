<template>
  <div>
    <h4>Coming Soon</h4>
    <ve-line :data="chartData"></ve-line>
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
      initData: {
        columns: ["date", "value"],
        rows: [
          { "Country/Region": "Afghanistan", date: "2020-03-19", value: 22 },
          { "Country/Region": "Afghanistan", date: "2020-03-18", value: 22 },
          { "Country/Region": "Afghanistan", date: "2020-03-17", value: 22 },
          { "Country/Region": "Afghanistan", date: "2020-03-16", value: 21 },
          { "Country/Region": "Afghanistan", date: "2020-03-15", value: 16 },
          { "Country/Region": "Afghanistan", date: "2020-03-14", value: 11 },
          { "Country/Region": "Afghanistan", date: "2020-03-13", value: 7 }
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
      // let cd = {
      //   columns: this.cols,
      //   rows: this.heroes
      // };
      // return cd;
      return this.initData;
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
