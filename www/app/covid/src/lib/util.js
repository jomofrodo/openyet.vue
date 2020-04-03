import * as csvLib from './csvLib';

export function  getCSVData(url,vm) {
    return vm.$http.get(url).then(function(ret) {
      const csv = ret.data;
      const json = csvLib.csvToJSON(csv);
      return json;
    });
  }

  export function  getData(url, vm) {
    return vm.$http.get(url).then(function(ret) {
      const recs = ret.data;
      return recs;
    });
  }