<template>
  <section style="z-index:999">
    <div v-if="flgDebug">
      <btn @click="marsha=marshaCodes[0]" type="primary">Set to AAEBR</btn>
      <btn @click="marsha=null">Clear</btn>
    </div>
    <label v-if="flgShowLabel" :for="pName">Marsha</label>
    <input :id="ID" :name="pName" autocomplete="off" :value="value" class="form-control" style="min-width:200px" :autofocus="flgAutoFocus" type="text" :ref="pRef" placeholder="Select location">
    <ds-typeahead v-model="marsha" :target="'#' + ID" :async-srcX="srcURL" async-keyX="data" :data="marshaCodes" item-key="label" />
    <slot name="buttons"></slot>
    <br/>
    <alert v-if="flgDebug" v-show="marsha">You selected {{marsha}}</alert>
  </section>
</template>

<script>
import _ls from './_ls';
//import "bootstrap/dist/css/bootstrap.min.css";
import Vue from 'vue';
import dsTypeahead from './dsTypeahead';
import { Btn, Alert } from 'uiv';
import axios from 'axios';
Vue.prototype.$http = axios;

const srcURLBase = '/ds?pAction=getACRecords&q=sql_GET_MARSHA_CODE_CANDIDATES&pWebuserID=';
const srcURLBase2 = '/ds?pAction=getACRecords&q=sql_GET_ACTIVE_MARSHA_CODES';

export default {
  name: 'loc-selector',
  components: { Btn, dsTypeahead, Alert },
  mixins: [_ls],
  props: ['webuserID', 'pname', 'id', 'value', 'pRef', 'flgAutoFocus'],
  data() {
    return {
      flgDebug: false,
      flgShowLabel: false,
      marsha: '',
      marshaCodes: [],
      srcURL: srcURLBase + this.webuserID,
      srcURL2: srcURLBase2,
      pName: this.pname || 'phMarshaCode',
      ID: this.id || this.pName || 'phMarshaCode'
    };
  },

  created: function() {
    const vm = this;
    this.getListData(this.marshaCodes).then(function(data) {
      vm.marshaCodes = data;
    });
  },
  mounted() {
    if (this.pRef) {
      // this.$refs[this.pRef].focus();
    }
  },
  methods: {
    getListData: function(arr) {
      const vm = this;
      const src = this.webuserID ? this.srcURL : this.srcURL2;
      return vm.$http
        .get(src)
        .then(function(ret) {
          const data = ret.data;
          arr = Object.keys(data).map(function(key) {
            return data[key];
          });
          //vm.marshaCodes = arr;
          return arr;
        })
        .catch(function(err) {
          alert(err);
        });
    }
  }
};
</script>
