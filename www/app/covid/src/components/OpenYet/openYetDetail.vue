<template>
  <div>
    <table class="data-detail">
      <tr>
        <th></th>
        <th colspan="1">Value as of</th>
        <th colspan="14">
          Change on This Day / Delta
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
            <div class="delta OPEN">&nbsp;</div>declining value vs prior day
            <div class="delta CLOSED">&nbsp;</div>increasing value vs prior day
          </div>
        </th>
        <th colspan="1">Value as of</th>
        <th colspan="3">2 Week Trend</th>
      </tr>
      <tr>
        <th>Measure</th>
        <th>{{openingDate}}</th>
        <th v-for="rec in oy.recs" :key="rec.date">
          {{formatChangeDate(rec.date)}}
          <i class="fas fa-info-circle" :title="getJSON(rec)" />
        </th>
        <th>{{endingDate}}</th>
        <th></th>
      </tr>
      <tr>
        <th>
          confirmed
          <i class="fas fa-info-circle" :title="help.confirmed" />
        </th>
        <td>{{oy.recs[0].confirmed}}</td>
        <td v-for="rec in oy.recs" :key="rec.date" class="status change-status">
          {{rec.confd0}} /
          <div class="delta" :class="calcChangeStatus(rec.confd0,rec.confd1)">
            <span v-if="flgDeltaRate">{{ getDeltaRate(rec.confirmed,rec.confd0,rec.confd1)}}</span>
            <span v-if="flgDeltaPercent">{{ getDeltaPerc(rec.confirmed,rec.confd0,rec.confd1)}}</span>
          </div>
        </td>
        <td>{{oy.recs[13].confirmed}}</td>
        <td v-if="flgDeltaRate" class="status" :class="calcStatus(oy.confdTrend)">
          <span>{{formatTrend(oy.confdTrend,true,true)}}</span>
        </td>
        <td v-if="flgDeltaPercent" class="status" :class="calcStatus(oy.confTrend)">
          <span>{{formatTrend(oy.confTrend,true,true)}}</span>
        </td>
      </tr>
      <tr v-if="!hasTesting">
        <th>
          % positive&nbsp;
          <i v-if="flgDebug" class="fas fa-info-circle" :title="help.ppositive" />
        </th>
        <td>n/a</td>
        <td v-for="rec in oy.recs" :key="rec.date">n/a</td>
        <td>n/a</td>
        <td>n/a</td>
      </tr>
      <tr v-if="hasTesting">
        <th>
          % positive&nbsp;
          <i v-if="flgDebug" class="fas fa-info-circle" :title="help.ppositive" />
        </th>
        <td>{{oy.recs[0].ppositive}}%</td>
        <td v-for="rec in oy.recs" :key="rec.date">
          <div
            class="status change-status delta large"
            :class="calcPercChangeStatus(rec.ppositived0)"
          >{{rec.ppositived0}}%</div>
        </td>
        <td>{{oy.recs[13].ppositive}}%</td>
        <td
          class="status"
          :class="calcStatus(oyRec.ppositiveTrend)"
        >{{formatTrend(oyRec.ppositiveTrend,true)}}</td>
      </tr>
      <tr>
        <th>
          deaths
          <i class="fas fa-info-circle" :title="help.deaths" />
        </th>
        <td>{{oy.recs[0].death}}</td>
        <td v-for="rec in oy.recs" :key="rec.date" class="status change-status">
          {{rec.deathd0}} /
          <div class="delta" :class="calcChangeStatus(rec.deathd0,rec.deathd1)">
            <span v-if="flgDeltaRate">{{ getDeltaRate(rec.death,rec.deathd0,rec.deathd1)}}</span>
            <span v-if="flgDeltaPercent">{{ getDeltaPerc(rec.death,rec.deathd0,rec.deathd1)}}</span>
          </div>
        </td>
        <td>{{oy.recs[13].death}}</td>
        <td v-if="flgDeltaRate" class="status" :class="calcStatus(oy.deathsdTrend)">
          <span>{{formatTrend(oy.deathsdTrend,true,true)}}</span>
        </td>
        <td v-if="flgDeltaPercent" class="status" :class="calcStatus(oy.deathsTrend)">
          <span>{{formatTrend(oy.deathsTrend,true,true)}}</span>
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
          "% of tests for Covid19 that returned a positive result (i.e., test shows that person being tested DOES HAVE the Covid19 disease)",
        confirmed: "Number of cases of Covid19 confirmed during this period",
        deaths: "Number of deaths attributed to Covid19 during this period"
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
    endingDate() {
      const oy = this.oy;
      if (!oy.recs) return "n/a";
      const idx = oy.recs.length - 1;
      return oy.recs[idx].date;
    },
    openingDate() {
      const oy = this.oy;
      if (!oy.recs) return "n/a";
      return oy.recs[0].date;
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
      else if (d0 - d1 < 0) return "OPEN";
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
.delta.large{
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