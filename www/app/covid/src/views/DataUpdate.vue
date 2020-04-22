<template>
  <div>
    <ul class="data-stats">
      <li v-for="rec in dataStats" :rec="rec" :key="rec.srccode">DataSrc: {{rec.srccode}} : {{ rec.ct }} records. Last retrieved: {{rec.date || 'never'}}</li>
    </ul>
    <ul>
      <li>
        <div>
          <label>Retrieve remote data</label>
          <select name="dataSrc" v-model="dataSrc">
            <option value>-- Select Data Source --</option>
            <option value="JH_GLOBAL_CONF:JH_GLOBAL_DEATHS:JH_GLOBAL_RECOVER">All JH Global Data (Confirmed, Deaths, Recovered)</option>
            <option value="JH_US_CONF:JH_US_DEATHS">All JH US Data (Confirmed, Deaths)</option>
            <option value="CTP_STATES_DAILY">Get Covid Tracking Project US states/daily</option>
            <option value="JH_US_CONF">Get JH US confirmed cases time series</option>
            <option value="JH_US_DEATHS">Get JH US new deaths time series</option>
            <option value="JH_GLOBAL_CONF">Get JH Global confirmed cases time series</option>
            <option value="JH_GLOBAL_DEATHS">Get JH Global new deaths time series</option>
            <option value="JH_GLOBAL_RECOVER">Get JH Global recovered cases time series</option>

          </select>
          <br/>
          <input type="checkbox" name="expireAll" value="1" v-model="expireExistingRemote" />Expire existing remote records for this source
          <div class="center"><button id="go-getData" @click="getData()">GO</button></div>
        </div>
      </li>
      <li>
        <label>Retrieve ALL remote data:</label>
        <button @click="getALLData()">GO</button>
      </li>

      <li>
        Create Combined data:
        <br />
        <select name="dataSrc" v-model="dataSrcCombined">
          <option value>-- Select Data Source --</option>
          <option value="JH_GLOBAL_CONF:JH_US_CONF:CTP_STATES_DAILY">Create for ALL data sources</option>
          <option value="JH_US_CONF">JH US confirmed cases time series</option>
          <option value="JH_US_DEATHS">JH US new deaths time series</option>
          <option value="JH_GLOBAL_CONF">JH Global confirmed cases time series</option>
          <option value="JH_GLOBAL_DEATHS">JH Global new deaths time series</option>
          <option value="JH_GLOBAL_RECOVER">JH Global recovered cases time series</option>
          <option value="CTP_STATES_DAILY">Covid Tracking Project US states/daily</option>
        </select>
        <br />
        <button @click="createCombined()">GO</button>
        <input type="checkbox" name="expireAll" value="1" v-model="expireAllCombined" />Expire all existing
      </li>
    </ul>
  </div>
</template>
<script>
import * as asUtil from '../lib/libAsync';

export default {
  name: "DataUpdate",
  data() {
    return {
      dataSrc: "",
      dataSrcCombined: "",
      dataStats: [],
      expireAllCombined: false,
      expireExistingRemote: false,
      urlDataStats: '/cvd/remoteDataStats',
      flgIsFetching: false
    };
  },
  created(){
    this.getRemoteDataStats();
  },
  methods: {
    getData() {
      const vm = this;
      if (!this.dataSrc) return;
      let url = "/cvd/retrieveData?dataSrc=" + this.dataSrc;
      if(this.expireExistingRemote) url += "&expireExisting=true";
      this.flgIsFetching = true;
      this.$http.get(url)
        .then(function(data) {
          vm.flgIsFetching = false;
          let msg;
          msg = data.src + "\r\n";
          msg += data.srccode + "\r\n";
          msg += "Reords: " + data.cts.ctTotalRecords.value + "\r\n";
          msg += "Created: " + data.cts.ctNewRecordsCreated.value + "\r\n";
          msg += "Errors: " + data.cts.ctBadRecords.value;

          alert(msg);
        })
        .catch(function(err) {
          vm.flgIsFetching = false;
          alert(err.responseText);
        });
    },
    getRemoteDataStats(){
      const vm = this;
      const url = this.urlDataStats;
      this.$http.get(url)
      .then(ret=>{
        const rso = ret.data;
        const recs = rso.items;
        vm.dataStats = recs;
      })
    },

    getALLData() {
      const vm = this;
      this.flgIsFetching = true;
      let url = "/cvd/retrieveALLData";
      $.get(url)
        .then(function(data) {
          let msg;
          msg = data;
          vm.flgIsFetching = false;
          alert(msg);
        })
        .catch(function(err) {
          vm.flgIsFetching = false;
          alert(err.responseText);
        });
    },

    createCombined() {
      const vm = this;
      this.flgIsFetching = true;
      let fd = new FormData();
      let url = "/cvd/createCombinedData";
      fd.append("dataSrc", this.dataSrcCombined);
      let expireAll = this.expireAllCombined;
      if (this.expireAllCombined) fd.append("expireAll", true);
      asUtil.jqSubmit(url,fd)
      // this.$http.post(url,fd)
        .then(function(data) {
          let msg;
          msg = data;
          vm.flgIsFetching = false;
          alert(msg);
        })
        .catch(function(err) {
          vm.flgIsFetching = false;
          alert(err.responseText);
        });
    }
  }
};
</script>

<style scoped>
div.center{
  width:100%;
  margin-left: auto;
  margin-right: auto;
}
</style>