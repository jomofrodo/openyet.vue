<template>
  <div>
    <label>Country:</label>
    <select name="Country" v-model="filters.Country">
      <option value>-- Select a country --</option>
      <option v-for="country in countries" :key="country" :value="country">{{country}}</option>
    </select>
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
  data() {
    return {
      urlConfirmed: "app/covid/data/time_series-ncov-Confirmed.csv",
      urlDeaths: "app/covid/data/time_series-ncov-Deaths.csv",
      urlRecovered: "app/covid/data/time_series-ncov-Recovered.csv",
      csvData: [],
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
      sortOrders: {},
      filters: { Country: "" },
      confirmed: [],
      deaths: [],
      recovered: [],
      combined: []
    };
  },
  computed: {
    countries: function() {
      let cA = [];
      const recs = _.uniqBy(this.confirmed, function(e) {
        return e["Country/Region"];
      });
      recs.forEach(function(rec) {
        cA.push(rec["Country/Region"]);
      });
      return cA;
    },
    chartData: function(newVal) {
      let cd = {
        columns: this.cols,
        rows: this.heroes
      };
      return cd;
    },
    heroes: function() {
      let hrecs = [];
      const vm = this;
      //Filter for Country/Region
      hrecs = this.confirmed.filter(function(rec, idx) {
        return rec["Country/Region"] == vm.filters.Country;
      });
      // Sort in ascending chrono
      hrecs = _.sortBy(hrecs, function(o) {
        return new moment(o.Date);
      }); //.reverse();
      return hrecs;
    }
  },
  created() {
    const vm = this;
    this.getData(this.urlConfirmed).then(function(recs) {
      vm.confirmed = recs;
      vm.cols.forEach(function(key) {
        vm.sortOrders[key] = 1;
      });
    });
    this.getData(this.urlDeaths).then((recs)=> vm.deaths = recs);
    this.getData(this.urlRecovered).then(recs => vm.recovered = recs);

  },
  methods: {
    getData(url) {
      const vm = this;
      return this.$http.get(url).then(function(ret) {
        const csv = ret.data;
        const dataA = csv.split("\n");
        const hdr = dataA[0];
        const cols = hdr.split(",");
        const json = csvLib.csvToJSON(csv, vm);
        const recs = json.slice(2);
        // vm.cols = cols;
        return recs;
      });
    }
  },
  getCombinedStats() {
    // For each country, calculate the active cases
    // Active cases are confirmed, minus recovered, minus deaths
    let confCt, deathCt, recoverCt,activeCt;
    this.countries.forEach(function(c, idx) {
      confCt = 0; deatchCt = 0; recoverCt = 0;
      confCt = this.confirmed.find(function(rec) {
        if (rec["Country/Region"] === c) return rec.Value;
        else return false;
      });
      deathCt = this.deaths.find(function(rec) {
        if (rec["Country/Region"] === c) return rec.Value;
        else return false;
      });
      recoverCt = this.recovered.find(function(rec) {
        if (rec["Country/Region"] === c) return rec.Value;
        else return false;
      });
      activeCt = confCt + deathCt + recoverCt;
      let combRec = {}
    });
  }
};
</script>
