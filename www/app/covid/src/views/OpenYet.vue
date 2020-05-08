<template>
  <div id="app">
    <div id="asyncticator-mount"></div>
    <div>
      <h3>Open Yet?</h3>
      <p>
        Are we ready to open yet? The U.S. Federal government has come out with
        <a
          href="https://www.whitehouse.gov/openingamerica/#criteria"
        >guidelines</a>
        for determining when a region is ready to re-open. This site attempts to interpret those guidelines with available data in order to answer the question for given regions.
        See our
        <router-link :to="{name: 'docs-methodology'}">methodology and rationale here.</router-link>
      </p>
    </div>
    <div id="div-controls">
      <div class="control" style="width:20rem" id="cc">
        <label class="control-label">Country:</label>
        <vue-select
          :options="countries"
          v-model="country"
          placeholder="-- Country --"
          label="name"
          class="vs__search"
          style="width:100%"
        ></vue-select>
      </div>
      <div
        v-if="flgStates"
        class="control"
        style="width:20rem"
        id="sc"
        :class="{disabled:!flgStates}"
      >
        <label class="control-label">State:</label>
        <vue-select
          name="State-Selector"
          :disabled="!flgStates"
          :options="states"
          v-model="state"
          placeholder="-- State --"
          label="name"
          class="vs__search"
          :class="{disabled:!flgStates}"
          style="width:100%"
        ></vue-select>
      </div>
      <div
        v-if="state && state.statecode"
        class="control"
        style="width:20rem"
        id="sc"
        :class="{disabled:!flgCounties}"
      >
        <label class="control-label">County:</label>
        <vue-select
          name="County-Selector"
          :disabled="!flgCounties"
          :options="counties"
          v-model="county"
          placeholder="-- County --"
          label="county"
          class="vs__search"
          :class="{disabled:!flgCounties}"
          style="width:100%"
        ></vue-select>
      </div>
    </div>
    <div id="open-yet-main" v-if="oyStatus.overallStatus">
      <div id="open-yet-breakdown" class="flex-main">
        <h4>
          2 Week Trend
          <div
            class="status status-h4"
            :class="oyStatus.overallStatus.code"
          >{{oyStatus.overallStatus.name}}</div>
        </h4>
        <b-tabs content-class="mt-3">
          <b-tab title="summary" active>
            <open-yet-summary :oyRec="oyRec" :oyStatus="oyStatus" />
          </b-tab>
          <b-tab title="detail">
            <open-yet-detail2 :oyRec="oyRec" />
          </b-tab>
          <b-tab title="graph">
            <open-yet-graph :oyRec="oyRec" />
          </b-tab>
        </b-tabs>
        <div class="explanatory">Data last updated: {{oyRec.date}}</div>
      </div>
      <div id="main-status" class="flex-main">
        <div class="explanatory bold">Based on these metrics, the status for this country/region is:</div>
        <div
          id="status-report"
          class="status"
          :class="oyStatus.overallStatus.code"
        >{{oyStatus.overallStatus.name}}</div>

        <MainStats :oyRec="oyRec" />
      </div>
    </div>

    <!-- Alert Dialog -->
    <jo-modal
      :id="dlgs.dlgAlert.id"
      :flgShow="dlgs.dlgAlert.flg"
      flgCancel="false"
      @ok="toggleDialog(dlgs.dlgAlert)"
      @cancel="toggleDialog(dlgs.dlgAlert)"
    >
      <div v-html="alertMsg"></div>
    </jo-modal>
  </div>
</template>

<script>
import Vue from "vue";
import joModal from "../components/joModal.vue";
import openYetDetail2 from "../components/OpenYet/openYetDetail2.vue";
import openYetGraph from "../components/OpenYet/openYetGraph.vue";
import openYetSummary from "../components/OpenYet/openYetSummary.vue";
import MainStats from "../components/OpenYet/MainStats.vue";
import * as mathLib from "../lib/mathLib";
import * as lt from "../lib/libTrend";
import vueSelect from "vue-select";
import * as commonOptions from "../grid/common-options";
import * as util from "../lib/util.js";
import * as math from "../lib/mathLib.js";

// Font Awesome
// Use a standard CDN load on the page that mounts the grid
// CSS
import "../css/modal.css";
import "@fortawesome/fontawesome-free/css/all.min.css";
const defaultCell = { width: 200, type: "text" };

const STATUS = {
  OPEN: { code: "OPEN", name: "Open" },
  CLOSED: { code: "CLOSED", name: "Closed" },
  TOPEN: { code: "TOPEN", name: "Trending Open" },
  STATIC: { code: "STATIC", name: "Static" },
  NA: { code: "NA", name: "Not Applicable" },
  UNKNOWN: { code: "UNKNOWN", name: "Unknown" }
};

const USA = { countrycode: "USA", name: "United States" };

export default {
  name: "OpenYet",
  components: {
    joModal,
    openYetDetail2,
    openYetGraph,
    openYetSummary,
    MainStats,
    vueSelect
  },
  props: [],
  data: function() {
    return {
      appName: "OpenYet",
      country: USA,
      state: { name: "-- select a state --" },
      county: { county: "-- select a county --" },
      searchQuery: "",
      colDefs: [],
      countries: [],
      states: [],
      counties: [],

      flgReadOnly: true,
      gridDefaults: [],
      gridOptions: [],
      flgDebug: false,
      flgDirty: false,
      dlgs: {
        dlgAlert: { id: "dlgAlert", flg: false }
      },
      oyRec: {}, //Main 'open yet' data for selection
      urlCountries: "cvd/getData/countries",
      urlStates: "cvd/getData/states",
      urlCounties: "cvd/getData/counties",
      // Grid Data
      urlOpenYetData: "cvd/getData/getOpenYet?limit=1000",
      urlSummaryNational: "/cvd/getData/nationalSummary",
      urlSummaryState: "/cvd/getData/stateSummary",
      urlSummaryCounty: "/cvd/getData/countySummary",
      // Misc
      reloadIdx: 1,
      alertMsg: "",
      infoMsg: "msg"
    };
  },
  mounted() {},
  created() {
    const vm = this;
    this.getCountries();
    this.getStates();
    this.getOpenYet();
  },
  computed: {
    dataURL() {
      let url = this.urlOpenYetData;
      // Add filters
      if (this.country && this.country.countrycode) {
        url += "&countrycode=" + this.country.countrycode;
      }
      if (this.state && this.state.statecode) {
        url += "&statecode=" + this.state.statecode;
      }
      if (this.county && this.county.statecode) {
        url += "&county=" + this.county.county;
      }
      return url;
    },
    filter() {
      const vm = this;
      return {
        country: vm.country,
        state: vm.state,
        county: vm.county
      };
    },
    statesURL() {
      if (!this.country.countrycode) return null;
      return this.urlStates + "?countrycode=" + this.country.countrycode;
    },
    countiesURL() {
      if (this.state && this.state.statecode) {
        return this.urlCounties + "?statecode=" + this.state.statecode;
      } else return null;
    },
    flgCounty() {
      return this.county && this.county.statecode != null;
    },
    flgCounties() {
      const colCounty = this.colDefs.find(function(col) {
        return col.colName == "county";
      });
      return this.counties && this.counties.length > 0;
    },
    flgState() {
      //Do we have a state selected?
      return this.state && this.state.statecode != null;
    },
    flgStates() {
      const colState = this.colDefs.find(function(col) {
        return col.colName == "statecode";
      });
      return this.states.length > 0;
    },
    gridCode() {
      let code;
      // default is national summary
      if (this.flgCounty) code = "SUMMARY_COUNTY";
      else if (this.flgState) code = "SUMMARY_STATE";
      else code = "SUMMARY_NATIONAL";
      return code;
    },
    oyStatus() {
      // compute status based on data in the oyRec
      const oy = this.oyRec;
      if (!oy || Object.keys(oy).length == 0) return {};
      let oyStat = {};
      let status;
      //Need at least two of three trends to be less than zero for status open
      // Or one positive and two zeroes

      // const confTrend = oyRec.confdTrend;
      // const posTrend = oyRec.ppositiveTrend;
      // const deathsTrend = oyRec.deathsdTrend;
      let overallScore = oy.confTrendPerc - 0 + (oy.deathsTrendPerc - 0);
      if (!isNaN(oy.pposTrendPerc-0)) overallScore += +(oy.pposTrendPerc - 0);
      if (isNaN(overallScore)) status = STATUS.UNKNOWN;
      else if (overallScore < -3) status = STATUS.OPEN;
      else if (overallScore >= -3 && overallScore < -1) status = STATUS.TOPEN;
      else if (overallScore >= -1 && overallScore < 1) status = STATUS.STATIC;
      else status = STATUS.CLOSED;

      oyStat.overallStatus = status;
      return oyStat;
    },
    urlSummaryData() {
      let url;
      // default is national summary
      if (this.flgCounty)
        url =
          this.urlSummaryCounty +
          "?countrycode=" +
          this.country.countrycode +
          "&statecode=" +
          this.state.statecode;
      else if (this.flgState)
        url = this.urlSummaryState + "?countrycode=" + this.country.countrycode;
      else
        url =
          this.urlSummaryNational + "?countrycode=" + this.country.countrycode;
      return url;
    }
  },
  watch: {
    country(newVal) {
      this.state = { name: null };
      this.county = { county: null };
    },
    countries(newVal) {
      this.getStates();
    },
    state(newVal) {
      this.county = { county: null };
    },
    statesURL(newVal) {
      if (!newVal) this.state = { name: null };
      else {
        this.getStates();
      }
    },
    countiesURL(newVal) {
      if (!newVal) this.county = { county: null };
      else this.getCounties();
    },
    dataURL(newVal) {
      if (newVal) {
        this.getOpenYet(newVal);
      }
    },
    oyRec(newVal) {
      const vm = this;
      const oy = this.oyRec;
      // this.convertToNumbers(newVal); // Convert strings to numbers
      let oyRec = newVal;
      // Calculate 3 sets of trends -
      //  baseline: 28 - 15 days ago
      //  week 1: 14-8 days ago
      //  week 2: 7 days ago through now
      // For each set, calculate:
      //  confirmed trend
      //  ppositive trend
      //  deaths trend
      // oyRec.confdTrend = this.calcTrend("confd0", oyRec.recs);
      // oyRec.confTrend = this.calcTrend("confirmed", oyRec.recs);
      // oyRec.ppositiveTrend = this.calcSum("ppositived0", oyRec.recs);
      // oyRec.deathsdTrend = this.calcTrend("deathd0", oyRec.recs);
      // oyRec.deathsTrend = this.calcTrend("death", oyRec.recs);
      const recsBL = oy.recs.slice(0, 14);
      const recsWeek1 = oy.recs.slice(14, 21);
      const recsWeek2 = oy.recs.slice(21);
      oy.confT_bl = lt.calcTrend("confirmed", recsBL);
      oy.confT_w1 = lt.calcTrend("confirmed", recsWeek1, oy.confT_bl);
      oy.confT_w2 = lt.calcTrend("confirmed", recsWeek2, oy.confT_bl);

      oy.pposT_bl = lt.calcTrend("ppositive", recsBL);
      oy.pposT_w1 = lt.calcTrend("ppositive", recsWeek1, oy.pposT_bl);
      oy.pposT_w2 = lt.calcTrend("ppositive", recsWeek2, oy.pposT_bl);

      oy.deathsT_bl = lt.calcTrend("death", recsBL);
      oy.deathsT_w1 = lt.calcTrend("death", recsWeek1, oy.deathsT_bl);
      oy.deathsT_w2 = lt.calcTrend("death", recsWeek2, oy.deathsT_bl);

      let temp = new Number();
      temp = (oy.confT_w1.trendDelta + oy.confT_w2.trendDelta) / 2;
      oy.confTrend = temp.toFixed(0);
      temp = (oy.confT_w1.trendPerc + oy.confT_w2.trendPerc) / 2;
      oy.confTrendPerc = temp.toFixed(2);
      temp = (oy.pposT_w1.yAvgDelta + oy.pposT_w2.yAvgDelta) / 2;
      oy.pposTrend = temp.toFixed(2);
      temp = (oy.pposT_w1.yAvgDPerc + oy.pposT_w2.yAvgDPerc) / 2;
      oy.pposTrendPerc = temp.toFixed(2);
      temp = (oy.deathsT_w1.trendDelta + oy.deathsT_w2.trendDelta) / 2;
      oy.deathsTrend = temp.toFixed(0);
      temp = (oy.deathsT_w1.trendPerc + oy.deathsT_w2.trendPerc) / 2;
      oy.deathsTrendPerc = temp.toFixed(2);

      if (vm.flgDebug) console.log(oyRec);
      // main stats from last record in sequence
      const lastRec = oyRec.recs[oyRec.recs.length - 1];
      Object.keys(lastRec).forEach(key => {
        oyRec[key] = lastRec[key];
      });
    }
  },

  methods: {
    initOpts() {
      const vm = this;
      //NOT IN USE
    },
    calcSum(key, recs) {
      let sum = 0;
      recs.forEach((rec, idx) => {
        sum += rec[key] - 0;
      });
      sum = Math.round(sum, 0);
      return sum;
    },

    convertToNumbers() {
      //Convert oyRec strings to number vals
      const oyRec = this.oyRec;
      if (!oyRec) return;
      Object.keys(oyRec).forEach(k => {
        let numVal = oyRec[k] - 0;
        if (numVal != NaN) oyRec[k] = numVal;
      });
    },
    getCountries() {
      const vm = this;
      const countries = this.getLocalStorage("countries.json", this.appName);
      if (countries && countries.length) vm.countries = countries;
      else {
        util
          .getData(vm.urlCountries, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            this.writeLocalStorage("countries.json", recs, vm.appName);
            vm.countries = recs;
          })
          .catch(err => alert(err));
      }
    },
    getCounties() {
      const vm = this;
      if (!this.statesURL) return null;
      let counties = this.getLocalStorage(
        this.country.countrycode +
          "-" +
          this.state.statecode +
          "-counties.json",
        this.appName
      );
      if (counties) vm.counties = counties;
      else {
        util
          .getData(this.countiesURL, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            vm.writeLocalStorage(
              this.country.countrycode +
                "-" +
                vm.state.statecode +
                "-counties.json",
              recs,
              vm.appName
            );
            vm.counties = recs;
          })
          .catch(err => alert(err));
      }
    },
    getStates() {
      const vm = this;
      if (!this.statesURL) return null;
      if (!this.country.countrycode) return null;
      const states = this.getLocalStorage(
        this.country.countrycode + "-states.json",
        this.appName
      );
      if (states && states.length) vm.states = states;
      else {
        util
          .getData(this.statesURL, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            vm.writeLocalStorage(
              vm.country.countrycode + "-states.json",
              recs,
              vm.appName
            );
            vm.states = recs;
          })
          .catch(err => alert(err));
      }
    },
    getOpenYet() {
      const vm = this;
      util.getData(this.dataURL, vm).then(rso => {
        // items will be country,[state],[county]
        if (!rso.items) {
          if (vm.flgDebug)
            console.log(
              "Error while retrieving OpenYet data: no items returned: " +
                vm.dataURL
            );
          return;
        }
        // Sanity check
        const ct = rso.items.length;
        if (ct != 28) {
          console.log(
            "Error condition for data returned by url: " + this.dataURL
          );
          rso.recs = [];
          for (let idx = 0; idx < 28; idx++) {
            rso.recs.push({ confirmed: "n/a", death: "n/a" });
          }
        } else rso.recs = rso.items; //alias
        vm.oyRec = rso;
        vm.oyRec.json = JSON.stringify(rso);
      });
    },
    niceNumber(num) {
      return math.niceNumber(num);
    },
    showAlert(msg) {
      if (this.flgDebug) console.log(msg);
      msg = msg.replace(/\n/g, "<br />");
      msg = msg.replace(/\\r\\n/g, "<br />");
      msg = msg.replace(/\\n/g, "<br />");
      this.alertMsg = msg;
      this.dlgs.dlgAlert.flg = true;
      //this.dlgs[this.dlgAlert] = true;
    },
    toggleDialog(dlg) {
      dlg.flg = !dlg.flg;
      //    this.dlgs[dlgID] = !this.dlgs[dlgID];
    },
    clearLocalStorage(appName) {
      window.localStorage.setItem(appName, "{}");
    },
    writeLocalStorage(key, val, appName) {
      let storageRec = this.getLocalStorage(null, appName);
      storageRec[key] = val;
      window.localStorage.setItem(appName, JSON.stringify(storageRec));
    },
    getLocalStorage(key, appName) {
      const storageJSON = window.localStorage.getItem(this.appName);
      if (storageJSON == null) return {};
      const storageRec = JSON.parse(storageJSON);
      if (key) return storageRec[key];
      else return storageRec;
    }
  }
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: left;
  color: #2c3e50;
  margin-top: 10px;
  margin-left: 30px;
}
div#open-yet-main {
  min-height: 50vh;
}
div#summary-grid {
  min-height: 20vh;
  max-height: 80vh;
}
div#div-controls {
  display: flex;
}
table#njs-grid {
  max-height: 40vh;
  overflow: auto;
}
div.main-status-stats label {
  font-weight: bold;
  min-width: 150px;
}
div.control {
  flex: 0 1 auto;
  padding: 5px;
}
label.control-label {
  font-size: 1.1em;
  font-weight: bold;
}
div.status-h4 {
  display: inline-block;
  padding: 7px;
}
i.alert-el {
  color: red;
}
div#upload-form {
  margin: 20px;
}
div#upload-form label,
div#upload-form input,
div#upload-form select {
  margin-top: 20px;
  float: left;
}
div#upload-form label {
  clear: left;
  min-width: 15em;
  font-weight: bold;
}

div#upload-form div.div-buttons {
  float: left;
  clear: left;
  margin-top: 30px;
}
.fade2-enter-active,
.fade2-leave-active {
  transition: opacity 0.5s;
}
.fade2-enter, .fade2-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}

/* open-yet-main */
div#open-yet-main {
  display: flex;
}
div.flex-main {
  margin: 30px;
  padding: 30px;
}
div#status-report {
  font-size: 2em;
  padding: 20px;
  margin: 20px;
  font-weight: bold;
}
table.status-detail {
  border-spacing: 10px;
  border-collapse: separate;
}
td.status-detail,
th.status-detail {
  padding: 10px;
}
.explanatory {
  font-size: 0.9em;
}
.explanatory.bold {
  font-weight: bold;
}
/*   STATUS classes */
.OPEN {
  background-color: green;
  color: white;
}
.CLOSED {
  background-color: red;
  color: white;
}
.TOPEN {
  background-color: yellow;
  color: black;
}
.NA {
  background-color: grey;
  color: white;
}
</style>
