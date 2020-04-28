<template>
  <div id="app">
    <div id="asyncticator-mount"></div>
    <div>
      <h3>Open Yet?</h3>
      <p>Are we open yet? The U.S. Federal government has come out with guidelines for determining when a region (State)</p>
    </div>
    <div id="div-controls">
      <div class="control" style="width:20rem" id="cc">
        <vue-select
          :options="countries"
          v-model="filter.country"
          placeholder="-- Country --"
          label="name"
          class="vs__search"
          style="width:100%"
        ></vue-select>
      </div>
      <div
        v-if="flgState"
        class="control"
        style="width:20rem"
        id="sc"
        :class="{disabled:!states.length}"
      >
        <vue-select
          name="State-Selector"
          :disabled="states.length == 0"
          :options="states"
          v-model="state"
          placeholder="-- State --"
          label="name"
          class="vs__search"
          :class="{disabled:!flgState}"
          style="width:100%"
        ></vue-select>
      </div>
      <div
        v-if="filter.state"
        class="control"
        style="width:20rem"
        id="sc"
        :class="{disabled:!counties.length}"
      >
        <vue-select
          name="County-Selector"
          :disabled="counties.length == 0"
          :options="counties"
          v-model="county"
          placeholder="-- County --"
          label="name"
          class="vs__search"
          :class="{disabled:!counties.length}"
          style="width:100%"
        ></vue-select>
      </div>
    </div>
    <div id="open-yet-main" v-if="oyStatus.overallStatus">
      <div id="main-status" class="flex-main">
        <div>
          <label>Country:</label>
          {{ country.name }}
        </div>
        <div v-if="state.statecode">
          <label>State:</label>
          {{ state.name }}
        </div>
        <div v-if="county.value">
          <label>County:</label>
          {{ county.name }}
        </div>
        <div>
          <label>Status:</label>
          <div
            id="status-report"
            class="status"
            :class="oyStatus.overallStatus.code"
          >{{oyStatus.overallStatus.name}}</div>
        </div>
      </div>
      <div id="open-yet-breakdown" class="flex-main">
        <h4>2 Week Trend</h4>
        <b-tabs content-class="mt-3">
          <b-tab title="summary" active>
            <div>
              <table class="status-detail">
                <tr>
                  <th class="status-detail">Confirmed</th>
                  <td  class="status-detail" :class="oyStatus.confStatus.code">{{oyStatus.confTrend}}%</td>
                </tr>
                <tr>
                  <th class='status-detail'>% Positive</th>
                  <td class="status-detail" :class="oyStatus.ppositiveStatus.code">{{oyStatus.ppositiveTrend}}%</td>
                </tr>
                <tr>
                  <th class="status-detail">Deaths</th>
                  <td class="status-detail" :class="oyStatus.deathsStatus.code">{{oyStatus.deathsTrend}}%</td>
                </tr>
              </table>
            </div>
          </b-tab>
          <b-tab title="detail">
            <open-yet-detail :oyRec="oyRec"/>
          </b-tab>
          <b-tab title="graph">
            <open-yet-graph :oyRec="oyRec"/>
          </b-tab>
        </b-tabs>
      </div>
    </div>
 
    <njs-grid
      :gridCode="gridCode"
      :gridID="gridCode"
      :colDefs="colDefs"
      :dataURL="dataURL"
      :pDefaultRec="defaultRec"
      :p-filter="searchQuery"
      :readOnly="flgReadOnly"
    />

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
import openYetDetail from "../components/openYetDetail.vue";
import openYetGraph from "../components/openYetGraph.vue";
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
  TOPEN: { code: "TOPEN", name: "Trending Open" }
};

const USA = { countrycode: "USA", name: "United States" };

export default {
  name: "OpenYet",
  components: {
    joModal,
    njsGrid,
    openYetDetail,
    openYetGraph,
    vueSelect
  },
  props: [],
  data: function() {
    return {
      country: USA,
      state: { name: "-- select a state --" },
      county: { name: "-- select county --" },
      searchQuery: "",
      colDefs: [],
      countries: [],
      states: [],
      counties: [],
      filter: {
        country: USA,
        state: null,
        county: null
      },
      flgReadOnly: true,
      gridDefaults: [],
      gridOptions: [],
      gridCode: "OPEN_YET",
      flgDebug: true,
      flgDirty: false,
      dlgs: {
        dlgAlert: { id: "dlgAlert", flg: false }
      },
      oyRec: {}, //Main 'open yet' data for selection
      urlCountries: "cvd/getData/countries",
      urlStates: "cvd/getData/states",
      urlCounties: "cvd/getData/counties",
      urlData: "cvd/getData/getOpenYet?limit=1000",
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
      let url = this.urlData;
      // Add filters
      if (this.filter.country) {
        url += "&countrycode=" + this.filter.country.countrycode;
      }
      if (this.filter.state) {
        url += "&statecode=" + this.filter.state.statecode;
      }
      if (this.filter.county) {
        url += "&county=" + this.filter.county.name;
      }
      return url;
    },
    statesURL() {
      if (!this.filter.country) return null;
      return this.urlStates + "?countrycode=" + this.filter.country.countrycode;
    },
    countiesURL() {
      if (!this.filter.state) return null;
      return this.urlCounties + "?county=" + this.filter.state.statecode;
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
      const colCounty = this.colDefs.find(function(col) {
        return col.colName == "county";
      });
      return !colCounty.hidden;
    },
    flgState() {
      const colState = this.colDefs.find(function(col) {
        return col.colName == "statecode";
      });
      return this.states.length > 0;
    },
    oyStatus() {
      // compute status based on data in the oyRec
      let oyRec = this.oyRec;
      if (!oyRec || Object.keys(oyRec).length == 0) return {};
      let oyStat = {};
      oyStat.confTrend = this.calcTrend(
        oyRec.confd1,
        oyRec.confd2,
        oyRec.confd3,
        oyRec.conf_base
      );
      oyStat.ppositiveTrend = this.calcTrend(
        oyRec.perc_positived1,
        oyRec.perc_positived2,
        oyRec.perc_positived3,
        oyRec.perc_positive_base
      );
      oyStat.deathsTrend = this.calcTrend(
        oyRec.deathd1,
        oyRec.deathd2,
        oyRec.deathd3,
        oyRec.death_base
      );

      oyStat.confStatus = oyStat.confTrend < 0 ? STATUS.OPEN : STATUS.CLOSED;
      oyStat.ppositiveStatus =
        oyStat.ppositiveTrend < 0 ? STATUS.OPEN : STATUS.CLOSED;
      oyStat.deathsStatus =
        oyStat.deathsTrend < 0 ? STATUS.OPEN : STATUS.CLOSED;
      let status;
      if (oyStat.confTrend < 0) status = STATUS.OPEN;
      else if (oyStat.ppositiveTrend < 0) status = STATUS.OPEN;
      else if (oyStat.deathsTrend < 0) status = STATUS.OPEN;
      else status = STATUS.CLOSED;

      oyStat.overallStatus = status;
      return oyStat;
    }
  },
  watch: {
    statesURL(newVal) {
      if (!newVal) this.filter.state = null;
      else {
        this.getStates();
      }
    },
    countiesURL(newVAl) {
      if (!newVal) this.filter.countrycode = null;
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
            console.log(col.default);
            console.log(err);
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
    oyRec(newVal) {
      this.convertToNumbers(newVal); // Convert strings to numbers
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
    calcTrend(d1, d2, d3, dbase) {
      let trend;
      // if (d3 <= d2 || d3 <= d1) trend = 1;
      // positive or flat, no evidence of a two week decreasing trend
      if(false) 1;
      else {
        let d3Base = dbase - d3;
        if (d3Base == 0) {
          //whatever;
          d3Base = 1;
        }
        let dAvg = (d2 + d1) / 2;
        trend = dAvg / d3Base; // Average of d2 and d1 divided by dbase - d3 (d3_base)
      }
      trend = Math.round(trend * 100)/100;
      trend = trend * 100; //Convert to %
     return trend;
    },
    convertToNumbers() {
      //Convert oyRec strings to number vals
      const oyRec = this.oyRec;
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
      const jsonStr = window.localStorage.getItem("countries.json");
      //DEBUG
      if (false) vm.countries = JSON.parse(jsonStr);
      else {
        util
          .getData(vm.urlCountries, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            window.localStorage.setItem("countries.json", JSON.stringify(recs));
            vm.countries = recs;
          })
          .catch(err => alert(err));
      }
    },
    getCounties() {
      const vm = this;
      if (!this.statesURL) return null;
      const jsonStr = window.localStorage.getItem(
        this.state.statecode + "-counties.json"
      );
      //DEBUG
      if (false) vm.counties = JSON.parse(jsonStr);
      else {
        util
          .getData(this.countiesURL, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            window.localStorage.setItem(
              vm.state.statecode + "-counties.json",
              JSON.stringify(recs)
            );
            vm.counties = recs;
          })
          .catch(err => alert(err));
      }
    },
    getStates() {
      const vm = this;
      if (!this.statesURL) return null;
      const jsonStr = window.localStorage.getItem("states.json");
      //DEBUG
      if (false) vm.states = JSON.parse(jsonStr);
      else {
        util
          .getData(this.statesURL, vm)
          .then(rso => {
            // Write data to local storage'
            const recs = rso.items;
            window.localStorage.setItem("states.json", JSON.stringify(recs));
            vm.states = recs;
          })
          .catch(err => alert(err));
      }
    },
    getOpenYet() {
      const vm = this;
      util.getData(this.dataURL, vm).then(rso => {
        vm.oyRec = rso.items[0];
      });
    },
    showAlert(msg) {
      console.log(msg);
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
div#div-controls {
  display: flex;
}
div.control {
  flex: 0 1 auto;
  padding: 5px;
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
table.status-detail{
  border-spacing:10px;
  border-collapse: separate;
}
td.status-detail, th.status-detail {
  padding:10px;

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
</style>
