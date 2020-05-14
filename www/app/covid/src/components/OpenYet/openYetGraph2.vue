<template>
  <div class="graph-container">
    <input type="checkbox" v-model="trends" value="confirmed"/> confirmed
    <input type="checkbox" v-model="trends" value="ppositive"/> % positive
    <input type="checkbox" v-model="trends" value="death"/> deaths
    <line-chart :chart-data="cData" :options="options" />
  </div>
</template>

<script>
import LineChart from "../charts/LineChart";

export default {
  name: "openYetGraph2",
  components: { LineChart },
  props: ["oyRec"],
  data: function() {
    return {
      oy: this.oyRec,
      options2: {
        responsive: true,
        maintainAspectRatio: false
      },
      options: {},
      trends: ['confirmed']
    };
  },
  computed: {
    cData() {
      const oy = this.oy;
      if (!oy) return {};
      const recsBL = oy.recs.slice(0, 14);
      const recsWeek1 = oy.recs.slice(14, 21);
      const recsWeek2 = oy.recs.slice(21);

      const recs = oy.recs;
      let cd = {
        labels: [],
        datasets: [
          //   {
          //     label: "",
          //     data: [],
          //     backgroundColor: [],
          //     borderColor: [],
          //     borderWidth: 1
          //   }
        ]
      };
      for (let idx in recs) {
        let rec = recs[idx];
        cd.labels.push(rec.date);
      }

      let confSet = { data: [], label: "confirmed", borderColor: "black" };
      let dSet = { data: [], label: "deaths", borderColor: "red" };
      let pposSet = {
        data: [],
        label: "% positive",
        borderColor: "purple"
      };

      this.getDataValArray("confirmed", recs, confSet.data);
      this.getDataValArray("death", recs, dSet.data);
      this.getDataValArray("ppositive", recs, pposSet.data);

      if (this.trends.includes("confirmed")) cd.datasets.push(confSet);
      if(this.trends.includes("death")) cd.datasets.push(dSet);
      if(this.trends.includes("ppositive")) cd.datasets.push(pposSet);
      return cd;
    }
  },
  methods: {
    getDataValArray(key, recs, data) {
      for (let idx in recs) {
        let rec = recs[idx];
        data.push(rec[key]);
      }
    }
  }
};
</script>
<style scoped>
div.graph-container {
  width: 500px;
  height: 600px;
}
</style>
