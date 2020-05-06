<template>
  <div id="app">
    <div id="asyncticator-mount"></div>
    <div>
      <h3>Open Yet?</h3>
      <p>Are we ready to open yet? The U.S. Federal government has come out with <a href="https://www.whitehouse.gov/openingamerica/#criteria">guidelines</a> 
      for determining when a region is ready to re-open. This site attempts to interpret those guidelines with available data in order to answer the question for given regions.
      See our <router-link :to="{name: 'docs-methodology'}">methodology and rationale here.</router-link></p>
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
   

    <div id="summary-grid">
      <njs-grid
        :gridCode="gridCode"
        :gridID="gridCode"
        :colDefs="colDefs"
        :dataURL="urlSummaryData"
        :pDefaultRec="defaultRec"
        :p-filter="searchQuery"
        :readOnly="flgReadOnly"
      />
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
import njsGrid from "../njsGrid/src/njsGrid.vue"; // local src
import joModal from "../components/joModal.vue";
import ColumnSelector from "../components/ColumnSelector.vue";
import * as mathLib from "../lib/mathLib";
import vueSelect from "vue-select";
import * as commonOptions from "../grid/common-options";
import * as util from "../lib/util.js";

// Font Awesome
// Use a standard CDN load on the page that mounts the grid
// CSS
import "../css/modal.css";
import "../css/grid.css";
import "@fortawesome/fontawesome-free/css/all.min.css";
const defaultCell = { width: 200, type: "text" };

const STATUS = {
  OPEN: { code: "OPEN", name: "Open" },
  CLOSED: { code: "CLOSED", name: "Closed" },
  TOPEN: { code: "TOPEN", name: "Trending Open" },
  STATIC: { code: "STATIC", name: "Static" },
  NA: { code: "NA", name: "Not Applicable" }
};

const USA = { countrycode: "USA", name: "United States" };

export default {
  name: "OpenYet",
  components: {
    joModal,
    njsGrid,
    vueSelect
  },
  props: [],
  data: function() {
    return {
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
    this.initGrid();
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
    defaultRec() {
      let rec = {};
      const vm = this;
      Object.keys(this.colDefs).forEach(function(idx) {
        let col = vm.colDefs[idx];
        rec[col.colName] = col.default;
      });

      return rec;
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
      let oyRec = this.oyRec;
      if (!oyRec || Object.keys(oyRec).length == 0) return {};
      let oyStat = {};
      let status;
      //Need at least two of three trends to be less than zero for status open
      // Or one positive and two zeroes

      const confTrend = oyRec.confdTrend;
      const posTrend = oyRec.ppositiveTrend;
      const deathsTrend = oyRec.deathsdTrend;
      const overallScore = confTrend + posTrend + deathsTrend;

      if(overallScore < -3 ) status = STATUS.OPEN;
      else if(overallScore >=-3 && overallScore < -1) status = STATUS.TOPEN;
      else if(overallScore >= -1 && overallScore < 1) status = STATUS.STATIC;
      else status = STATUS.CLOSED;

      oyStat.overallStatus = status;
      return oyStat;
    },
    urlSummaryData() {
      let url;
      // default is national summary
      if (this.flgCounty) url = this.urlSummaryCounty + "?countrycode=" + this.country.countrycode 
        + "&statecode=" + this.state.statecode;
      else if (this.flgState) url = this.urlSummaryState + "?countrycode=" +this.country.countrycode;
      else url = this.urlSummaryNational + "?countrycode=" + this.country.countrycode;
      return url;
    }
  },
  watch: {
    country(newVal){
      this.state = {name:null};
      this.county = {county:null};
    },
    state(newVal) {
      this.county = {county:null};
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
    colDefs(newVal) {
      if (!newVal) return;
      const vm = this;
      const comopts = commonOptions || [];
      newVal.forEach(function(col, idx) {
        if (col.default) {
          // parse defaults that start with a : -- these should be global variables in the scope of the grid
          try {
            if (col.default.substr && col.default.substr(0, 1) === ":") {
              let strDefault = eval(col.default.slice(1));
              if (strDefault) col.default = strDefault;
            }
          } catch (err) {
            if(vm.flgDebug) console.log(col.default);
            if(vm.flgDebug) console.log(err);
          }
        }
        if (col.options) {
          let optString = col.options;
          if (vm.gridOptions) col.options = vm.gridOptions[optString];
          if (typeof col.options !== "object") {
            col.options = comopts[optString];
          }
        }
      });
    },
    dataURL(newVal) {
      if (newVal) {
        this.getOpenYet(newVal);
      }
    },
    gridCode(newVal){
      this.initGrid();
    },
    oyRec(newVal) {
      const vm = this;
      // this.convertToNumbers(newVal); // Convert strings to numbers
      let oyRec = newVal;
      oyRec.confdTrend = this.calcTrend(
        "confd0",
        oyRec.recs
      );
      oyRec.ppositiveTrend = this.calcSum(
        "ppositived0",
        oyRec.recs
      );
      oyRec.deathsdTrend = this.calcTrend(
        "deathd0",
        oyRec.recs
      );
      if(vm.flgDebug) console.log(oyRec);
    }
  },

  methods: {
    async initGrid() {
      const vm = this;
      try {
        this.gridOptions = require("../grid/coldefs/" +
          this.gridCode +
          "_options");
      } catch (err) {
        this.gridOptions = [];
        // no options for this grid
      }
      // const defaultCell = gridDef.defaultCell;

      await this.getColDefs(vm);
      this.colDefs.forEach((c, i) => {
        if (defaultCell) {
          if (!c.width) Vue.set(c, "width", defaultCell.width);
          if (!c.type) Vue.set(c, "type", defaultCell.type);
        }
        if (!c.hidden) Vue.set(c, "hidden", false);
        Vue.set(c, "isVisible", !c.hidden);
      });
    },
    initOpts() {
      const vm = this;
      //NOT IN USE
    },
    calcSum(key,recs){
      let sum = 0;
      recs.forEach((rec,idx)=>{
        sum += rec[key] - 0;
      })
      sum = Math.round(sum,0);
      return sum;
    },
    calcTrend(key,recs){
      // Get the LR slope of recs[key]
      let yVals = [], yValsConv = [];
      let xVals = [];
      let sY = 0;
      recs.forEach((rec,idx)=>{
        let val = rec[key];
        yVals.push(val);
        sY += val-0;
        xVals.push(idx);
      });
      if(sY==0) return 0;
      [xVals, yValsConv] = mathLib.findLineByLeastSquares(xVals, yVals);
      const perc1 = (yValsConv[1] - yValsConv[0]) / yValsConv[0];
      let trend =  perc1;
      if(trend % 1 != 0){
      // if(trend != 0 && (trend < 1 && trend > -1)){
        //decimal
        trend = Math.round(trend*100,0);
      }
      return trend;
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
    getColDefs: async vm => {
      // by default, get the columnDefs from local static definition file in /gridcoldefs/<gridCode>.js
      // Can override with a the db grid.gridcolumns table
      // by setting srcColDefs prop to "api"
      const src = vm.srcColDefs ? vm.srcColDefs : "local";
      if (src == "local") {
        let gridDef = require("../grid/coldefs/" + vm.gridCode);
        //let gridDef = [];
        vm.colDefs = gridDef.colDefs;
        return;
      } else {
        // src is from the api
        const url = vm.urlColDefs + "&gridCode=" + vm.gridCode;
        return vm.$http.get(url).then(function(resp) {
          // Do something here to convert response into colDefs;
        });
      }
    },
    getCountries() {
      const vm = this;
      const countries = this.getLocalStorage("countries.json");
      if (countries) vm.countries = countries;
      else {
        util
          .getData(vm.urlCountries, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            this.writeLocalStorage("countries.json", recs);
            vm.countries = recs;
          })
          .catch(err => alert(err));
      }
    },
    getCounties() {
      const vm = this;
      if (!this.statesURL) return null;
      let counties = this.getLocalStorage(
        this.country.countrycode + "-" + this.state.statecode + "-counties.json"
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
              recs
            );
            vm.counties = recs;
          })
          .catch(err => alert(err));
      }
    },
    getStates() {
      const vm = this;
      if (!this.statesURL) return null;
      const states = this.getLocalStorage(
        this.country.countrycode + "-states.json"
      );
      if (states) vm.states = states;
      else {
        util
          .getData(this.statesURL, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            vm.writeLocalStorage(vm.country.countrycode + "-states.json", recs);
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
          if(vm.flgDebug) console.log(
            "Error while retrieving OpenYet data: no items returned: " +
              vm.dataURL
          );
          return;
        }
        rso.recs = rso.items;  //alias
        vm.oyRec = rso;
        vm.oyRec.json = JSON.stringify(rso);
      });
    },
    showAlert(msg) {
      if(this.flgDebug) console.log(msg);
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
    clearLocalStorage() {
      window.localStorage.setItem("openYet", "{}");
    },
    writeLocalStorage(key, val) {
      let oyStorageRec = this.getLocalStorage();
      oyStorageRec[key] = val;
      window.localStorage.setItem("openYet", JSON.stringify(oyStorageRec));
    },
    getLocalStorage(key) {
      const oyStorageJSON = window.localStorage.getItem("openYet");
      if (oyStorageJSON == null) return {};
      const oyStorageRec = JSON.parse(oyStorageJSON);
      if (key) return oyStorageRec[key];
      else return oyStorageRec;
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
div#open-yet-main{
  min-height: 50vh;
}
div#summary-grid{
  min-height: 20vh;
  max-height: 80vh;
}
div#div-controls {
  display: flex;
}
table#njs-grid{
  max-height: 40vh;
  overflow:auto;
}
div.control {
  flex: 0 1 auto;
  padding: 5px;
}
label.control-label {
  font-size: 1.1em;
  font-weight: bold;
}
div.status-h4{
  display:inline-block;
  padding:7px;
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
