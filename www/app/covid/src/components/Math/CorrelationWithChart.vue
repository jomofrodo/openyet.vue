<template>
  <div class="main-container">
    <div class="chart-area flex-item">
      CHART HERE
      <div
        v-for="i in iObj"
        :key="i"
        :id="'f'+i"
        :style="'position:absolute; top:3px; left:' + getX(i)+'px; width:2px; height:2px; font-size=1; background-color:#ff0000'"
      ></div>
      <div
        v-for="i in iObj"
        :key="i"
        :id="'da'+i"
        :style="'position:absolute; top:1px; left:' + getX(i)+'px; width:2px; height:2px; font-size=1; background-color:#00ff00'"
      ></div>
      <div
        v-for="i in iObj"
        :key="i"
        :id="'db'+i"
        :style="'position:absolute; top:5px; left:' + getX(i)+'px; width:2px; height:2px; font-size=1; background-color:#00ff00'"
      ></div>
      <div
        v-for="i in i2Obj"
        :key="i"
        :id="'d'+i"
        :style="'position:absolute; top:15px; left:'+getX2(i)+'px; width:7px; height:1px; font-size=1; background-color:#0000ff'"
      ></div>
      <div
        v-for="i in i2Obj"
        :key="'e'+i"
        :id="'e'+i"
        :style="'position:absolute; top:20px; left:'+getX2(i)+'px; width:7px; height:1px; font-size=1; background-color:#0000ff'"
      ></div>
    </div>
    <div class="chart-inputs flex-item">
      <textarea
        name="yVals"
        v-model="yVals"
        placeholder="14 values for y, one per line or csv"
        rows="15"
        cols="7"
      ></textarea>
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
        <label>X</label>
        <input name="newX" v-model="xPred" />
      </div>
      <div class="input">
        <label>Y upper</label>
        <input name="yupper" v-model="yUpper" />
      </div>
      <div class="input">
        <label>Y mean</label>
        <input name="ymean" v-model="yMean" />
      </div>
      <div class="input">
        <label>Y lower</label>
        <input name="ylower" v-model="yLower" />
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
        <button @click="rePlot">rePlot!</button>
        <button @click="getCorrelation">get Correlation</button>
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
      xPred: null,
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
  created() {
    // this.initGraph();
  },
  methods: {
    rePlot() {
      this.getCorrelation();
      const vm = this;
      const ctY = vm.ctY;

      var inter, slo, cor;
      inter = this.intercept;
      slo = this.slope;
      cor = this.slope;

      let xmax = this.xMax,
        xmin = this.xMin,
        ymax = this.yMax,
        ymin = this.yMin;

      var xd = xmax - xmin,
        yd = ymax - ymin;

      xmin = xmin - 0.1 * xd;
      xmax = xmax + 0.1 * xd;
      ymin = ymin - 0.1 * yd;
      ymax = ymax + 0.1 * yd;

      var screenx = 500,
        screeny = 300,
        offset = 10;
      var xf = screenx / (xmax - xmin),
        yf = screeny / (ymax - ymin);
      var x1, y1, x2, y2, y3, y4;

      for (i = 1; i <= ctY; i++) {
        x1 = (x[i] - xmin) * xf + offset;

        y1 = y[i];
        y1 = screeny - (y1 - ymin) * yf + offset;

        ptr = "d" + i;
        obj = document.getElementById(ptr);
        obj.style.top = y1;
        obj.style.left = x1 - 3;
        obj.style.visibility = "visible";

        ptr = "e" + i;
        obj = document.getElementById(ptr);
        obj.style.top = y1 - 3;
        obj.style.left = x1;
        obj.style.visibility = "visible";
      }

      for (i = ctY + 1; i <= 12; i++) {
        ptr = "d" + i;
        obj = document.getElementById(ptr);
        obj.style.visibility = "hidden";

        ptr = "e" + i;
        obj = document.getElementById(ptr);
        obj.style.visibility = "hidden";
      }

      for (var i = 0; i < screenx; i = i + 5) {
        x1 = i + offset;
        x2 = i / xf + xmin;

        y2 = slope * x2 + intercept;
        y1 = screeny - (y2 - ymin) * yf + offset;

        ptr = "f" + i;
        obj = document.getElementById(ptr);
        obj.style.top = y1;
        y2 > ymax || y2 < ymin
          ? (obj.style.visibility = "hidden")
          : (obj.style.visibility = "visible");
      }

      var s2x,
        s2y,
        s2,
        s,
        xbar = xs / ctY;

      s2x = (ctY * x2s - xs * xs) / ctY / (ctY - 1);
      s2y = (ctY * y2s - ys * ys) / ctY / (ctY - 1);
      s2 = ((ctY - 1) / (ctY - 2)) * (s2y - slope * slope * s2x);
      s2 = m.abs(s2);
      s = m.sqrt(s2);

      for (var i = 0; i < screenx; i = i + 5) {
        x1 = i + offset;
        x2 = i / xf + xmin;

        y3 = slope * x2 + intercept;
        y2 =
          y3 -
          t95[ctY - 2] *
            s *
            m.sqrt(1 + 1 / ctY + ((x2 - xbar) * (x2 - xbar)) / (ctY - 1) / s2x);

        y3 = y3 + y3 - y2;

        y1 = screeny - (y2 - ymin) * yf + offset;
        y4 = screeny - (y3 - ymin) * yf + offset;

        ptr = "da" + i;
        obj = document.getElementById(ptr);
        obj.style.top = y1;
        y2 > ymax || y2 < ymin
          ? (obj.style.visibility = "hidden")
          : (obj.style.visibility = "visible");

        ptr = "db" + i;
        obj = document.getElementById(ptr);
        obj.style.top = y4;
        y3 > ymax || y3 < ymin
          ? (obj.style.visibility = "hidden")
          : (obj.style.visibility = "visible");
      }

      var xpred, ylower, ymean, yupper;

      xpred = 1.0 * this.xPred;

      ymean = slope * xpred + intercept;
      ylower =
        ymean -
        t95[ctY - 2] *
          s *
          m.sqrt(
            1 + 1 / ctY + ((xpred - xbar) * (xpred - xbar)) / (ctY - 1) / s2x
          );
      yupper = 2 * ymean - ylower;

      ymean = m.round(ymean * 100) / 100;
      yupper = m.round(yupper * 100) / 100;
      ylower = m.round(ylower * 100) / 100;

      var yms = ymean.toString();
      var yus = yupper.toString();
      var yls = ylower.toString();

      this.yMean = yms;
      this.yUpper = yus;
      this.yLower = yls;
    },
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
    },
    getSignificance(){
        const m = Math;
        const r = this.correlation;
        const n = this.ctY;
        let t = r* m.sqrt((n-2)/(1-r*r));
        //compare to critical t value
        const critT = t95[n-2];
        this.significance = t;
        this.critT = critT;
        this.flgSignificant = (t-critT) > 0;
    },
    getX(i) {
      const offset = 10;
      return i + offset + 1;
    },
    getX2(i){
        return i*10;
    },
    initGraph() {
        const vm = this;
      let screenx = 500;
      vm.iObj = new Array();
      vm.i2Obj = new Array();
      for (var i = 0; i < screenx; i = i + 5) {
        vm.iObj.push(i);
      }
      for (var i = 0; i < 12; i++) {
        vm.i2Obj.push(i);
      }
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
input.significant{
    background-color: green;
    color:white;
}
</style>
	