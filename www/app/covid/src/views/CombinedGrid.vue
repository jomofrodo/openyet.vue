<template>
  <div id="app">
    <div id="asyncticator-mount"></div>
    <div>
      <h3>Combined Data Grid</h3>
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

      <div class="control" style="width:20rem" id="sc" :class="{disabled:!states.length}">
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
      <div class="control">
        <column-selector :colDefs="colDefs" label="Column Selector" title="Select columns that will display in the grid" />
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
      @update="onUpdateGridVal"
      @saveGrid="flgDirty=false"
      @noteUpdate="flgDirty=true"
      @noteDelete="flgDirty=true"
      @noteAdd="flgDirty=true"
      @reset="flgDirty=false"
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
// import { njsGrid } from "@netazoic/njs-grid";  // as an npm package
// import { njsGrid } from "./njsGrid";  // locally compiled module
// import { njsGrid } from "./njsGrid/src/index.js"; // as a git subModule
import njsGrid from "../njsGrid/src/njsGrid.vue"; // local src
import joModal from "../components/joModal.vue";
import ColumnSelector from "../components/ColumnSelector.vue";
import vueSelect from "vue-select";
import * as commonOptions from "../grid/common-options";
import * as util from "../lib/util.js";

// Font Awesome
// Use a standard CDN load on the page that mounts the grid
// CSS
import "../css/modal.css";
import "../css/grid.css";
import "@fortawesome/fontawesome-free/css/all.min.css";
let rateCodes = [];
const defaultCell = { width: 200, type: "text" };

export default {
  name: "CombinedGrid",
  components: {
    "njs-grid": njsGrid,
    joModal,
    "column-selector": ColumnSelector,
    "vue-select": vueSelect
  },
  props: [],
  data: function() {
    return {
      country: "",
      searchQuery: "",
      colDefs: [],
      countries: [],
      state: {},
      states: [],
      filter: {
        country: null,
        state: null
      },
      flgReadOnly: true,
      gridDefaults: [],
      gridOptions: [],
      gridCode: "COMBINED",
      flgDebug: true,
      flgDirty: false,
      dlgs: {
        dlgAlert: { id: "dlgAlert", flg: false }
      },
      urlCountries: "cvd/getData/countries",
      urlStates: "cvd/getData/states",
      urlData: "cvd/getData/combined?limit=1000",
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
  },
  computed: {
    dataURL() {
      let url = this.urlData;
      // check if state column is displayed

      if (this.flgState) url += "&states=true";
      if (this.flgCity) url += "&cities=true";
      // Add a country if selected
      if (this.filter.country) {
        url += "&countrycode=" + this.filter.country.countrycode;
      }
      if (this.filter.state) {
        url += "&statecode=" + this.filter.state.statecode;
      }
      return url;
    },
    statesURL() {
      if (!this.filter.country) return null;
      return this.urlStates + "?countrycode=" + this.filter.country.countrycode;
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
    flgCity() {
      const colCity = this.colDefs.find(function(col) {
        return col.colName == "county";
      });
      return !colCity.hidden;
    },
    flgState() {
      const colState = this.colDefs.find(function(col) {
        return col.colName == "state";
      });
      return !colState.hidden;
    }
  },
  watch: {
    statesURL(newVal) {
      if (!newVal) this.filter.state = null;
      else {
        this.getStates();
      }
    },
    state(newVal){
      const colState = this.colDefs.find(function(col) {
        return col.colName == "state";
      });
      colState.hidden = newVal == null;
      this.filter.state = newVal;
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
        if (!c.required && !c.headerClasses && c.editor !== null)
          Vue.set(c, "headerClasses", "optional");
        //Set 'isVisible' flags
        Vue.set(c, "isVisible", !c.hidden);
      });
    },
    initOpts() {
      const vm = this;
      //NOT IN USE
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
    onUpdateGridVal(row, col, idx) {
      if (!col.onUpdate) return null;
      col.onUpdate(row, col, this.gridDefaults);
      return 1;
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
    },
    toggleState() {
      let idx=0;
      const colState = this.colDefs.find(function(col,idx) {
        return col.colName == "state";
      });
      colState.hidden = (this.filter.state == null);
      // Vue.set(this.colDefs,idx,colState);
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
</style>
