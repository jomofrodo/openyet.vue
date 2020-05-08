<template>
  <div>
    <table class="data-detail">
      <tr>
        <th></th>
        <th colspan="12">
          Trend (value/day) During This Period / Change from baseline value
          <div class="explainer">
            (
            <a
              @click="toggleRateAndPercent(flgDeltaRate)"
              :class="{active:flgDeltaRate, inactive:flgDeltaPercent}"
              title="Change in growth rate vs prior day"
            >
              <i class="fas fa-caret-up large" :class="{inactive:flgDeltaPercent}" /> growth rate
            </a> /
            <a
              @click="toggleRateAndPercent(flgDeltaPercent)"
              :class="{active:flgDeltaPercent, inactive:flgDeltaRate}"
              title="Percent change in value of this metric"
            >
              <i class="fas fa-percent small" :class="{inactive:flgDeltaRate}" /> change in value
            </a>)
          </div>
          <div class="legend">
            <label>legend:</label>
            <div class="delta OPEN">&nbsp;</div>declining value vs baseline
            <div class="delta CLOSED">&nbsp;</div>increasing value vs baseline
          </div>
        </th>
        <th colspan="3">Overall Trend</th>
      </tr>
      <tr>
        <th>Measure</th>
        <th colspan="4">Baseline <div class="explainer">({{blStart}} - {{blEnd}})</div></th>
        <th colspan="4">Week 1 <div class="explainer">({{w1Start}} - {{w1End}})</div></th>
        <th colspan="4">Week 2 <div class="explainer">({{w2Start}} - {{w2End}})</div></th>
        <th></th>
      </tr>
      <tr>
        <th>
          confirmed
          <i class="fas fa-info-circle" :title="help.confirmed" />
        </th>
        <td colspan="4" class="status change-status">{{oy.confT_bl.trend}}</td>
        <td colspan="4" class="status change-status">
          {{oy.confT_w1.trend}} /
          <div class="delta" :class="calcChangeStatus(oy.confT_w1.trend,oy.confT_bl.trend)">
            <span v-if="flgDeltaRate">{{ oy.confT_w1.trendDelta }}</span>
            <span v-if="flgDeltaPercent">{{ oy.confT_w1.trendPerc }}%</span>
          </div>
        </td>
        <td colspan="4" class="status change-status">
          {{oy.confT_w2.trend}} /
          <div class="delta" :class="calcChangeStatus(oy.confT_w2.trend,oy.confT_bl.trend)">
            <span v-if="flgDeltaRate">{{ oy.confT_w2.trendDelta }}</span>
            <span v-if="flgDeltaPercent">{{ oy.confT_w2.trendPerc }} %</span>
          </div>
        </td>
        <td v-if="flgDeltaRate" class="status" :class="calcStatus(oy.confTrend)">
          <span>{{formatTrend(oy.confTrend)}}</span>
        </td>
        <td v-if="flgDeltaPercent" class="status" :class="calcStatus(oy.confTrend)">
          <span>{{formatTrend(oy.confTrendPerc,true,true)}}</span>
        </td>
      </tr>
      <tr v-if="!hasTesting">
        <th>
          % positive&nbsp;
          <i v-if="flgDebug" class="fas fa-info-circle" :title="help.ppositive" />
        </th>
        <td colspan="4">n/a</td>
        <td colspan="4">n/a</td>
        <td colspan="4">n/a</td>
        <td>n/a</td>
      </tr>
      <tr v-if="hasTesting">
        <th>
          % positive&nbsp;
          <i v-if="flgDebug" class="fas fa-info-circle" :title="help.ppositive" />
        </th>
        <td colspan="4" class="status change-status">{{oy.pposT_bl.yAvg}}</td>
        <td colspan="4" class="status change-status">
          {{oy.pposT_w1.yAvg}} /
          <div class="delta" :class="calcChangeStatus(oy.pposT_w1.yAvgDelta)">
            <span v-if="flgDeltaRate">{{ oy.pposT_w1.yAvgDelta }}%</span>
            <span v-if="flgDeltaPercent">{{ oy.pposT_w1.yAvgDPerc}}%</span>
          </div>
        </td>
        <td colspan="4" class="status change-status">
          {{oy.pposT_w2.yAvg}} /
          <div class="delta" :class="calcChangeStatus(oy.pposT_w2.yAvgDelta)">
            <span v-if="flgDeltaRate">{{ oy.pposT_w2.yAvgDelta }}%</span>
            <span v-if="flgDeltaPercent">{{ oy.pposT_w2.yAvgDPerc}}%</span>
          </div>
        </td>
        <td v-if="flgDeltaRate" class="status" :class="calcStatus(oy.pposTrend)">
          <span>{{oy.pposTrend}}%</span>
        </td>
        <td v-if="flgDeltaPercent" class="status" :class="calcStatus(oy.pposTrend)">
          <span>{{formatTrend(oy.pposTrendPerc,true,true)}}</span>
        </td>
      </tr>
      <tr>
        <th>
          deaths
          <i class="fas fa-info-circle" :title="help.deaths" />
        </th>
        <td colspan="4" class="status change-status">{{oy.deathsT_bl.trend}}</td>
        <td colspan="4" class="status change-status">
          {{oy.deathsT_w1.trend}} /
          <div class="delta" :class="calcChangeStatus(oy.deathsT_w1.trend,oy.deathsT_bl.trend)">
            <span v-if="flgDeltaRate">{{ oy.deathsT_w1.trendDelta }}</span>
            <span v-if="flgDeltaPercent">{{ oy.deathsT_w1.trendPerc }}%</span>
          </div>
        </td>
        <td colspan="4" class="status change-status">
          {{oy.deathsT_w2.trend}} /
          <div class="delta" :class="calcChangeStatus(oy.deathsT_w2.trend,oy.deathsT_bl.trend)">
            <span v-if="flgDeltaRate">{{ oy.deathsT_w2.trendDelta }}</span>
            <span v-if="flgDeltaPercent">{{ oy.deathsT_w2.trendPerc }}%</span>
          </div>
        </td>
        <td v-if="flgDeltaRate" class="status" :class="calcStatus(oy.deathsTrend)">
          <span>{{formatTrend(oy.deathsTrend)}}</span>
        </td>
        <td v-if="flgDeltaPercent" class="status" :class="calcStatus(oy.deathsTrendPerc)">
          <span>{{oy.deathsTrendPerc}}%</span>
        </td>
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
      flgDebug: true,
      flgDeltaRate: true,
      flgDeltaPercent: false,
      help: {
        ppositive:
          "Average % of tests for Covid19 that returned a positive result during this period (i.e., test shows that person being tested DOES HAVE the Covid19 disease)",
        confirmed: "Number of cases of Covid19 confirmed per day during this period",
        deaths: "Number of deaths per day attributed to Covid19 during this period"
      }
    };
  },
  computed: {
    oy() {
      return this.oyRec;
    },
    hasTesting() {
      if (!this.oy.recs) return false;
      const rec = this.oy.recs[0];
      if (rec.countrycode == "USA" && rec.county == null) return true;
      else return false;
    },

    blStart() {
      return this.startDate(this.oy.confT_bl);
    },
    blEnd() {
      return this.endDate(this.oy.confT_bl);
    },
    w1Start() {
      return this.startDate(this.oy.confT_w1);
    },
    w1End() {
      return this.endDate(this.oy.confT_w1);
    },
    w2Start() {
      return this.startDate(this.oy.confT_w2);
    },
    w2End() {
      return this.endDate(this.oy.confT_w2);
    }
  },
  created() {},
  watch: {
    oy(newVal) {}
  },
  methods: {
    calcPercChangeStatus(d0) {
      if (d0 == 0) return "STATIC";
      else if (d0 < 0) return "OPEN";
      else return "CLOSED";
    },
    calcChangeStatus(d0, d1) {
      if (d0 == d1) return "STATIC";
      let compVal = d0;
      if(d1!=null) compVal = d0-d1;
      if (compVal < 0) return "OPEN";
      else return "CLOSED";
    },
    calcStatus(ct) {
      if (ct == 0) return "STATIC";
      else if (ct > 0) return "CLOSED";
      else return "OPEN";
    },
    getDeltaRate(c0, d0, d1) {
      //Get the change in rate of increase of deltas, yesterday vs today
      if (d0 == null) return null;
      if (d0 == 0) return "0%";
      let delta = (d0 - d1) / d0;
      delta = Math.round(delta * 100, 0);
      const sign = delta > 0 ? "+" : "";
      return sign + delta + "%";
    },
    getDeltaPerc(c0, d0, d1) {
      // compare deltas.  d0 is today, d1 is yesterday
      if (c0 == null) return null;
      if (d0 == null) return null;
      if (d0 == 0) return "0%";
      let perc = d0 / (c0 - d0);

      // if(d0-d1==0) return "0%";
      // let perc = (d0 - d1) / d1;
      perc = Math.round(perc * 100, 0);
      let status = this.calcChangeStatus(d0, d1);
      const sign = perc > 0 ? "+" : "";
      return sign + perc + "%";
    },
    formatChangeDate(dateStr) {
      return moment(dateStr, "MMM DD, YYYY").format("MM/DD");
    },
    formatTrend(trend, isPerc, isDecimal) {
      if (trend === "n/a") return trend;
      // if((isDecimal)) trend = Math.round(trend*100,0);
      let trendStr = "";
      if (trend > 0) trendStr += "+";
      trendStr += trend;
      if (isPerc) {
        trendStr += "%";
      }
      return trendStr;
    },
    getJSON(rec) {
      return JSON.stringify(rec);
    },
    endDate(trend) {
      const oy = this.oy;
      if (!trend.recs) return "n/a";
      const idx = trend.recs.length - 1;
      return trend.recs[idx].date;
    },
    startDate(trend) {
      const oy = this.oy;
      if (!trend.recs) return "n/a";
      return trend.recs[0].date;
    },
    toggleRateAndPercent(flg) {
      if (flg) return; //flg already set;
      this.flgDeltaRate = !this.flgDeltaRate;
      this.flgDeltaPercent = !this.flgDeltaPercent;
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
  color: white;
}
.delta {
  display: inline-block;
  padding: 3px;
  font-size: 0.7em;
}
.delta.large {
  font-size: 0.9em;
}
div.explainer {
  display: inline-block;
  font-size: 0.7em;
  padding: 3px;
}
div.legend {
  font-size: 0.7em;
  padding: 4px;
}
div.legend > div.delta {
  padding: 5px;
  margin: 3px;
}
a:hover {
  cursor: pointer;
}
a.active {
  font-weight: bold;
  color: black;
  text-decoration: underline;
}
a.inactive {
  color: rgb(90, 106, 151);
}
i.inactive {
  opacity: 0.6;
}
i.large {
  font-size: 1.5em;
}
i.small {
  font-size: 0.7em;
}
</style>