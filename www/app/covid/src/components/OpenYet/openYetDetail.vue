<template>
  <div>
    <table class="data-detail">
      <tr>
        <th></th>
        <th colspan="2">Value as of end of week</th>
        <th colspan="3">Trend during week of</th>
      </tr>
      <tr>
        <th>Measure</th>
        <th>{{week3}}</th>
        <th>{{week1}}</th>
        <th>{{week2}}</th>
        <th>{{week1}}</th>
        <th>2 Week Trend<br/>(% change in growth)</th>
      </tr>
      <tr>
        <th>confirmed <i class="fas fa-info-circle" :title="help.confirmed"/></th>
        <td>{{conf_base + confd3}}</td>
        <td>{{conf_now}}</td>
        <td class="status" :class="calcStatus(oy.confd2p)">{{formatTrend(oy.confd2p,true,true)}}</td>
        <td class="status" :class="calcStatus(oy.confd1p)">{{formatTrend(oy.confd1p,true,true)}}</td>
        <td class="status" :class="calcStatus(oyRec.confTrend)">{{formatTrend(oyRec.confTrend,true)}}</td>
      </tr>
      <tr>
        <th>% positive&nbsp;<i class="fas fa-info-circle" :title="help.ppositive"/></th>
        <td>{{perc_positive_base + perc_positived3}}%</td>
        <td>{{perc_positive_now}}%</td>
        <td class="status" :class="calcStatus(perc_positived2)">{{formatTrend(perc_positived2,true)}}</td>
        <td class="status" :class="calcStatus(perc_positived1)">{{formatTrend(perc_positived1,true)}}</td>
        <td class="status" :class="calcStatus(oyRec.ppositiveTrend)">{{formatTrend(oyRec.ppositiveTrend,true)}}</td>
      </tr>
      <tr>
        <th>deaths <i class="fas fa-info-circle" :title="help.deaths"/></th>
        <td>{{death_base + deathd3}}</td>
        <td>{{death_now}}</td>
        <td class="status" :class="calcStatus(deathd2p)">{{formatTrend(deathd2p,true,true)}}</td>
        <td class="status" :class="calcStatus(deathd1p)">{{formatTrend(deathd1p,true,true)}}</td>
        <td class="status" :class="calcStatus(oyRec.deathsTrend)">{{formatTrend(oyRec.deathsTrend,true)}}</td>
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
      if((isDecimal)) trend = Math.round(trend*100,0);
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