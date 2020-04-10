<template>
  <v-layout align-center justify-center row>
    ...
    <v-autocomplete
      v-model="country"
      :items="countries"
      :loading="loadingCountries"
      :search-input.sync="search"
      hide-no-data
      item-text="name"
      item-value="countrycode"
      label="Country"
      prepend-icon="country"
      append-icon
      placeholder="Start typing to Search"
      clearable
      return-object
    ></v-autocomplete>...
  </v-layout>
</template>
<script>
// We use https://github.com/robsontenorio/vue-api-query
// and create a custom model
// import Person from "@/model/Person";
import debounce from "debounce";

export default {
  data() {
    return {
      country: '',
      countryCodes: [],
      srcURL: "cvd/getData/countries",
      search: "",
      flgLoading: false
    };
  },
    created: function() {
    const vm = this;
    this.getListData().then(function(data) {
      vm.countryCodes = data;
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
    makeSearch: async (value, self) => {
      // Handle empty value
      if (!value) {
        self.countries = [];
        self.country = "";
      }
      // Items have already been requested
      if (self.flgLoading) {
        return;
      }

      self.lflgLoading = true;

      // YOUR AJAX Methods go here
      // if you prefer not to use vue-api-query
     this.getListData().then(function(data) {
      vm.countryCodes = data;
    })
        .catch(error => {
          self.error = "Unknown Error. Please check details and try again.";
          self.failed();
        })
        .finally(() => (self.flgLoading = false));
    }
  },
  watch: {
    search(value) {
      if (!value) {
        return;
      }
      // Debounce the input and wait for a pause of at
      // least 200 milliseconds. This can be changed to
      // suit your needs.
      debounce(this.makeSearch, 200)(value, this);
    }
  }
};
</script>