<template>
  <div>
    <table class="data-detail">
      <tr>
        <th colspan="2"></th>
        <th colspan="3">Change versus prior week during week of</th>
      </tr>
      <tr>
        <th>Measure</th>
        <th>Value as of {{week3}}</th>
        <th>{{week3}}</th>
        <th>{{week2}}</th>
        <th>{{week1}}</th>
      </tr>
      <tr>
        <th>confirmed</th>
        <td>{{conf_base}}</td>
        <td>{{confd3}}</td>
        <td class="status" :class="calcStatus(confd2)">{{confd2}}</td>
        <td class="status" :class="calcStatus(confd1)">{{confd1}}</td>
      </tr>
      <tr>
        <th>% positive</th>
        <td>{{perc_positive_base}}</td>
        <td>{{perc_positived3}}</td>
        <td class="status" :class="calcStatus(perc_positived2)">{{perc_positived2}}</td>
        <td class="status" :class="calcStatus(perc_positived1)">{{perc_positived1}}</td>
      </tr>
      <tr>
        <th>deaths</th>
        <td>{{death_base}}</td>
        <td>{{deathd3}}</td>
        <td class="status" :class="calcStatus(deathd2)">{{deathd2}}</td>
        <td class="status" :class="calcStatus(deathd1)">{{deathd1}}</td>
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
      perc_positived1: null,
      perc_positived2: null,
      perc_positived3: null,
      perc_positive_base: null,
      deathd1: null,
      deathd2: null,
      deathd3: null,
      death_base: null,
      week1: null,
      week2: null,
      week3: null
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
      if (ct >= 0) return "CLOSED";
      else if (ct < 0) return "OPEN";
    },
    initVars() {
      const oy = this.oy;
      const vm = this;
      Object.keys(oy).forEach(k => {
        if (vm[k] !== undefined) {
          vm[k] = oy[k];
        }
      });
      //Convert timestamps to dates
      vm.week1 = moment(vm.week1, "x").format("MM/DD/YY");
      vm.week2 = moment(vm.week2, "x").format("MM/DD/YY");
      vm.week3 = moment(vm.week3, "x").format("MM/DD/YY");
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