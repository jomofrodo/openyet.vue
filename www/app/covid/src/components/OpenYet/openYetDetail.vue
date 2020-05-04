<template>
  <div>
    <table class="data-detail">
      <tr>
        <th></th>
        <th colspan="1">Value as of</th>
        <th colspan="14">Change vs Previous Day</th>
        <th colspan="1">Value as of</th>
        <th colspan="3">Trend</th>
      </tr>
      <tr>
        <th>Measure</th>
        <th>{{oy.recs[0].date}}</th>
        <th v-for="rec in oy.recs" :key="rec.date">{{rec.date}}</th>
        <th>{{oy.recs[13].date}}</th>
        <th>2 Week Trend<br/>*Either change in growth rate or absolute trend</th>
      </tr>
      <tr>
        <th>confirmed <i class="fas fa-info-circle" :title="help.confirmed"/></th>
        <td>{{oy.recs[0].confirmed}}</td>
        <td v-for="rec in oy.recs" :key="rec.date">{{rec.confd0}}</td>
        <td>{{oy.recs[13].confirmed}}</td>
        <td class="status" :class="calcStatus(oy.confdTrend)">{{formatTrend(oy.confdTrend,true,true)}}</td>
      </tr>
      <tr>
        <th>% positive&nbsp;<i class="fas fa-info-circle" :title="help.ppositive"/></th>
        <td>{{oy.recs[0].ppositive}}%</td>
        <td v-for="rec in oy.recs" :key="rec.date">{{rec.ppositived0}}%</td>
        <td>{{oy.recs[13].ppositive}}</td>
        <td class="status" :class="calcStatus(oyRec.ppositiveTrend)">{{formatTrend(oyRec.ppositiveTrend,true)}}</td>
      </tr>
      <tr>
        <th>deaths <i class="fas fa-info-circle" :title="help.deaths"/></th>
        <td>{{oy.recs[0].death}}</td>
        <td v-for="rec in oy.recs" :key="rec.date">{{rec.deathd0}}</td>
        <td>{{oy.recs[13].death}}</td>
        <td class="status" :class="calcStatus(oy.deathsdTrend)">{{formatTrend(oy.deathsdTrend,true,true)}}</td>
      </tr>
    </table>
  </div>
</template>
<script>
import moment from "moment";
import Vue from "vue";

export default {
  props: ["oyRec"],
  data() {
    return {
      confd1: null,
      confd2: null,
      confd3: null,
      conf_base: null,
      conf_now: null,
      perc_positived1: null,
      perc_positived2: null,
      perc_positived3: null,
      perc_positive_base: null,
      perc_positive_now: null,
      deathd1: null,
      deathd2: null,
      deathd3: null,
      death_base: null,
      death_now: null,
      week1: null,
      week2: null,
      week3: null,
      help:{
        ppositive: "% of tests for Covid19 that returned a positive result (i.e., test shows that person being tested DOES HAVE the Covid19 disease)",
        confirmed: "Number of cases of Covid19 confirmed during this period",
        deaths: "Number of deaths attributed to Covid19 during this period"
      }
    };
  },
  computed: {
    oy() {
      return this.oyRec;
    }
  },
  created() {
    this.initVars();
  },
  watch: {
    oy(newVal) {
      this.initVars();
    }
  },
  methods: {
    calcStatus(ct) {
      if(ct == 0) return 'STATIC';
      else if (ct > 0) return "CLOSED";
      else return "OPEN";
    },
    initVars() {
      const oy = this.oy;
      const vm = this;
      //first clear all vars
      Object.keys(oy).forEach(k => {
        vm[k] = null;
      });
      Object.keys(oy).forEach(k => {
        if (vm[k] !== undefined) {
          vm[k] = oy[k];
        }
      });
      //Convert timestamps to dates
      vm.week1 = moment(vm.week1, "x").format("MM/DD/YY");
      vm.week2 = moment(vm.week2, "x").format("MM/DD/YY");
      vm.week3 = moment(vm.week3, "x").format("MM/DD/YY");
    },
    formatTrend(trend,isPerc,isDecimal) {
      if (trend === "n/a") return trend;
      // if((isDecimal)) trend = Math.round(trend*100,0);
      let trendStr = "";
      if (trend > 0) trendStr += "+";
      trendStr += trend;
      if(isPerc){
        trendStr += "%";
      }
      return trendStr;
    }
  }
};
</script>
<style scoped>
table.data-detail {
  padding: 10px;
  border-collapse: collapse;
  border: 1px solid black;
}
table.data-detail th,
table.data-detail td {
  border: 1px solid black;
  padding: 10px;
}
td.status.OPEN {
  background-color: lightgreen;
  color: black;
}
td.status.CLOSED {
  background-color: red;
  color: grey;
}
</style>