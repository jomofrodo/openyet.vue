<template>
  <div>
    <label>Country:</label>
    <select name="Country" v-model="filters.country">
      <option value>-- Select a country --</option>
      <option v-for="country in countries" :key="country.countrycode" :value="country.countrycode">{{country.name}}</option>
    </select>
    <ve-line :data="chartData"></ve-line>
  </div>
</template>

<script>
import VeLine from "v-charts/lib/line.common";
import * as csvLib from "../lib/csvLib";
import moment from "moment";
import * as util from '../lib/util';
var _ = require("lodash");
export default {
  components: { VeLine },
  data() {
    return {
      urlCombined: "cvd/getData/combined",
      urlCountries: "cvd/getData/countries",
      urlCountriesCSV: "/app/covid/data/countries.csv",
      csvData: [],
      cols: ["date", "positive", "death", "recovered"],
      sortOrders: {},
      filters: { country: "" },
      combined: [],
      countries: []
    };
  },
  computed: {
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
      if (!vm.filters.country) return hrecs;
      //Filter for Country/Region
      hrecs = this.combined.filter(function(rec, idx) {
        return rec["countrycode"] == vm.filters.country;
      });
      // Sort in ascending chrono
      hrecs = _.sortBy(hrecs, function(o) {
        return new moment(o.datechecked);
      }); //.reverse();
      return hrecs;
    },
    tag() {
      return this.$route.params.tag;
    }
  },
  created() {
    const vm = this;
    vm.cols.forEach(function(key) {
      vm.sortOrders[key] = 1;
    });
    // this.getData(vm.urlCountriesCSV).then(recs =>{
    //   let json = csvLib.csvToJSON(recs,1);
    //   vm.countries = json;
    // });

    const jsonStr = window.localStorage.getItem("countries.json");
    if (jsonStr) vm.countries = JSON.parse(jsonStr);
    else {
      this.getData(vm.urlCountries)
        .then(recs => {
          // Write data to local storage'
          window.localStorage.setItem("countries.json", JSON.stringify(recs));
          vm.countries = recs;
        })
        .catch(err => alert(err));
    }
    this.getData(this.urlCombined).then(recs => {
      const data = recs.filter(function(rec) {
        return rec.state == null || rec.state === "";
      });
      vm.combined = data;
    });
  },
  methods: {
    getData(url, recStore) {
      const vm = this;
      return this.$http.get(url).then(function(ret) {
        const recs = ret.data;
        return recs;
      });
    },
    clearCountryTable(){
      window.localStorage.clear("countries.json");
    }
  }
};
</script>
