<template>
  <section style="z-index:999">
    <div v-if="flgDebug">
      <btn @click="country=countryCodes[0]" type="primary">Set to Austria</btn>
      <btn @click="country=null">Clear</btn>
    </div>
    <label v-if="flgShowLabel" :for="pName">country</label>
    <input id="countryCode" name="countrycode" autocomplete="off" @update="setValue(evt,val)" class="form-control" 
    style="min-width:200px" :autofocus="flgAutoFocus" type="text" :ref="pRef" placeholder="Select location">
    <ds-typeahead v-model="country" target="#countryCode" :async-srcX="srcURL" async-keyX="data" :data="countryCodes" item-key="name" />
    <slot name="buttons"></slot>
    <br/>
    <alert v-if="flgDebug" v-show="country">You selected {{country}}</alert>
  </section>
</template>

<script>
//import "bootstrap/dist/css/bootstrap.min.css";
import Vue from 'vue';
import dsTypeahead from './dsTypeahead';
import { Btn, Alert } from 'uiv';

export default {
  name: 'loc-selector',
  components: { Btn, dsTypeahead, Alert },
  props: ['webuserID', 'pname', 'id', 'pRef', 'flgAutoFocus'],
  data() {
    return {
      flgDebug: false,
      flgShowLabel: false,
      country: '',
      countryCodes: [],
      srcURL: "cvd/getData/countries",
      value: null

    };
  },

  created: function() {
    const vm = this;
    this.getListData(this.countryCodes).then(function(data) {
      vm.countryCodes = data;
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
      const src = this.srcURL;
      return vm.$http
        .get(src)
        .then(function(ret) {
          const data = ret.data.items;
          arr = Object.keys(data).map(function(key) {
            return data[key];
          });
          //vm.countryCodes = arr;
          return arr;
        })
        .catch(function(err) {
          alert(err);
        });
    },
    setValue(evt,newVal){
      debugger;
      this.value = newVal;
    }
  }
};
</script>
