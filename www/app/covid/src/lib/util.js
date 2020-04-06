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

  export function getQueryParam(name) {
    // eslint-disable-next-line no-useless-escape
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(window.location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}
export function getWindowVal(name) {
    if (window[name]) return window[name];
    else return this.getQueryParam(name);
}