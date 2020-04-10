<template>
<div >
  <v-layout align-center justify-center row>
    
    <v-autocomplete
      v-model="country"
      :items="countries"
      :loading="flgLoading"
      :attach="attachPoint"
      hide-no-data
      item-text="name"
      item-value="countrycode"
      label="Country"
      :hint="hint"
      return-object
    >
    </v-autocomplete>
  </v-layout>
</div>
</template>
<script>
// We use https://github.com/robsontenorio/vue-api-query
// and create a custom model
// import Person from "@/model/Person";
import debounce from "debounce";

export default {
  props: [ 'attachPoint', 'placeHolder' ],
  data() {
    return {
      country: {},
      countries: [],
      hint: this.placeHolder || 'Start typing to search',
      srcURL: "cvd/getData/countries",
      search: "",
      flgLoading: false
    };
  },
    created: function() {
    const vm = this;
    this.getListData().then(function(data) {
      vm.countries = data;
    });
  },
  methods: {
    getListData: function(arr) {
      const vm = this;
      const src = this.srcURL;
      return vm.$http
        .get(src)
        .then(function(ret) {
          const data = ret.data.items;
          return data;
        })
        .catch(function(err) {
          alert(err);
        });
    }
   
  }
};
</script>