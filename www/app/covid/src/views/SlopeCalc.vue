<template>
  <div>
    <h1>Slope Calculator</h1>
    <div class="container">
      <div>
        <textarea
          class="slope-data"
          v-model="y_values_input"
          placeholder="Input y values, one per line or CSV"
        ></textarea>
      </div>
      <div>
        <button @click="calcSlope">Calc Slope</button>
        <button @click="calcLogSlope">Calc Log Slope</button>
        <button @click="calcSlopePerc">Calc Slope %</button>
        <div>
          <label>Slope:</label>
          {{slope}}
        </div>
        <div>
          <label>Ln Slope:</label>
          {{lnSlope}}
        </div>
        <div>
          <label>Slope %:</label>
          {{slopePerc}}
        </div>
        <div>
          <label>Slope Check:</label>
          {{slopeCheck}}
        </div>
        <div>
          <label>Intercept:</label>
          {{intercept}}
        </div>
        <div>
          <label>Correlation (r^2):</label>
          {{correlation}}
        </div>
        <div>
          <label>x-vals:</label>
          {{xVals}}
        </div>
        <div>
          <label>y-vals:</label>
          {{yVals}}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import * as mathLib from "../lib/mathLib";
export default {
  data() {
    return {
      xVals: [],
      yVals: [],
      y_values_input: "",
      slope: "Enter y values",
      slopeCheck: 0,
      intercept: 0,
      correlation: 0,
      significance: 0,
      lnSlope: 0,
      slopePerc: 0
    };
  },
  methods: {
    calcSlope() {
      const M = Math;
      let yValsInput = this.y_values_input.trim();
      //Strip quotes
      yValsInput = yValsInput.replace(/['"]/g, "");
      let yVals = yValsInput.split("\n");

      let slope = Math.slope(null, yVals);
      let xVals = [];
      Object.keys(yVals).forEach(idx => xVals.push(idx - 0 + 1));

      let rVals = mathLib.getCorrelation(xVals,yVals);
            /*
          ret.correlation = cor;
          ret.intercept = inter;
          ret.slope = slo;
          ret.xMax = xmax;
          ret.xMin = xmin;
          ret.yMax = ymax;
          ret.yMin = ymin;
          ret.ctY = ctY;
      */
      let r2 = rVals.correlation;
      let m = rVals.slope;
      let b = rVals.intercept;
      let yValsConv = [];
      for (var i = 0; i < yVals.length; i++) {
        let x = xVals[i];
        let y = x * m + b;
        yValsConv.push(y);
      }
      // [xVals, yValsConv] = mathLib.findLineByLeastSquares(xVals, yVals);

      this.xVals = xVals;
      this.yVals = yValsConv;
      this.slope = M.round(slope*1000)/1000;
      this.slopeCheck = M.round(m*1000)/1000;
      this.intercept = M.round(b*1000)/1000;
      this.correlation = M.round(r2*1000)/1000;
    },
    calcLogSlope() {
      let yValsInput = this.y_values_input.trim();
      //Strip quotes
      yValsInput = yValsInput.replace(/['"]/g, "");
      let yVals = yValsInput.split("\n");
      for (let i = 0; i < yVals.length; i++) yVals[i] = new Number(yVals[i]);
      let yValsLog = [];
      //Convert to log values
      // Find the min value and raise all y values by enough to make min 1
      const min = Math.min(...yVals);
      if (min < 1) yVals.forEach((val, idx) => (yVals[idx] = val + (1 - min)));
      Object.keys(yVals).forEach(idx => {
        yValsLog[idx] = Math.log(yVals[idx]);
      });
      let lnSlope = Math.slope(null,yValsLog);
      let xVals = [];
      Object.keys(yVals).forEach(idx => xVals.push(idx - 0));
      let yValsConv = [];
      [xVals, yValsConv] = mathLib.findLineByLeastSquares(xVals, yValsLog);
      
      this.lnSlope = Math.round(lnSlope*1000)/1000;
      this.xVals = xVals;
      this.yVals = yValsConv;
    },
    calcSlopePerc() {
      this.calcSlope();
      let perc1 = (this.yVals[1] - this.yVals[0]) / this.yVals[0];
      const ct = this.yVals.length;
      const first = this.yVals[0];
      const last = this.yVals[ct - 1];
      let perc2 = (last - first) / first / ct;
      this.slopePerc = Math.round(perc2*1000)/1000 * 100;
    }
  }
};
</script>
<style scoped>
div.container {
  display: flex;
}
textarea.slope-data {
  width: 20em;
  height: 30em;
}
</style>