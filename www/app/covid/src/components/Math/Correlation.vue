<template>
  <div>
    <h1>Correlation Calculator</h1>
    <div class="main-container">
      <div class="data-inputs flex-item">
        <div class="input">
          <label>Y Vals:</label>
          <textarea
            name="yVals"
            v-model="yVals"
            placeholder="14 values for y, one per line or csv"
            rows="15"
            cols="7"
          ></textarea>
        </div>
        <div class="inputs">
          <div class="input">
            <label>Slope</label>
            <input name="slope" v-model="slope" />
          </div>
          <div class="input">
            <label>Intercept</label>
            <input name="yhat" v-model="intercept" />
          </div>
          <div class="input">
            <label>r^2</label>
            <input name="correlation" v-model="correlation" />
          </div>
          <div class="input">
            <label>Significance (t):</label>
            <input name="significance" v-model="significance" :class="{significant:flgSignificant}" />
          </div>
          <div class="input">
            <label>critical T:</label>
            <input name="critT" v-model="critT" />
          </div>
          <div class="input">
            <button @click="getCorrelation">get Correlation</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
const t95 = new Array(
  0,
  6.314,
  2.92,
  2.353,
  2.132,
  2.015,
  1.943,
  1.895,
  1.86,
  1.833,
  1.812,
  1.796,
  1.782
);
export default {
  data() {
    return {
      intercept: 0,
      slope: 0,
      correlation: 0,
      yVals: [],
      ctY: 0,
      yUpper: null,
      yMean: null,
      yLower: null,
      xMax: null,
      xMin: null,
      yMax: null,
      yMin: null,
      //Significance,
      significance: 0,
      critT: 0,
      flgSignificant: false,
      //chart
      iObj: [],
      i2Obj: []
    };
  },
  created() {},
  methods: {
    getCorrelation() {
      const m = Math;
      var x = new Array(),
        y = new Array();
      var xfield, yfield, ctY;
      this.yVals = this.yVals.trim();
      y = this.yVals.split("\n");
      Object.keys(y).forEach((val, idx) => {
        x[idx] = idx + 1;
      });
      // x vals are just integers 1..14
      //Make sure we are working with numbers
      for (var i = 0; i < y.length; i++) {
        y[i] = y[i] - 0;
      }

      ctY = y.length;

      var xmin = x[1],
        xmax = x[1];
      var ymin = y[1],
        ymax = y[1];
      var xs = 0,
        ys = 0,
        x2s = 0,
        y2s = 0,
        xys = 0;
      for (let i = 0; i < ctY; i++) {
        xmin = m.min(x[i], xmin);
        xmax = m.max(x[i], xmax);
        ymin = m.min(y[i], ymin);
        ymax = m.max(y[i], ymax);
        xs += x[i];
        ys += y[i];
        x2s += x[i] * x[i];
        y2s += y[i] * y[i];
        xys += x[i] * y[i];
      }

      var slope, intercept, correlation;

      intercept = (ys * x2s - xys * xs) / (ctY * x2s - xs * xs);
      slope = (ctY * xys - xs * ys) / (ctY * x2s - xs * xs);
      correlation =
        (ctY * xys - xs * ys) /
        m.sqrt((ctY * x2s - xs * xs) * (ctY * y2s - ys * ys));
      correlation = correlation * correlation;

      let inter, slo, cor; //rounded versions
      inter = m.round(10000 * intercept) / 10000;
      slo = m.round(10000 * slope) / 10000;
      cor = m.round(10000 * correlation) / 10000;
      this.correlation = cor;
      this.intercept = inter;
      this.slope = slo;
      this.xMax = xmax;
      this.xMin = xmin;
      this.yMax = ymax;
      this.yMin = ymin;
      this.ctY = ctY;

      this.getSignificance();
    },
    getSignificance() {
      const m = Math;
      const r = m.sqrt(this.correlation);
      const n = this.ctY;
      let t = r * m.sqrt((n - 2) / (1 - r * r));
      //compare to critical t value
      const critT = t95[n - 2];
      this.significance = t;
      this.critT = critT;
      this.flgSignificant = t - critT > 0;
    }
  }
};
</script>
<style scoped>
div.main-container {
  display: flex;
}
div.flex-item {
  padding: 40px;
}
input.significant {
  background-color: green;
  color: white;
}
textarea {
  width: 20em;
  height: 30em;
}
</style>
	