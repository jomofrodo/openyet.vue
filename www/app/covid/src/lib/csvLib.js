export function csvToJSON(csv, hdrCt) {
  hdrCt = (hdrCt != undefined)? hdrCt: 0;
  var lines = csv.split("\n");
  var result = [];
  var headers = lines[0].split(",");
  headers.map(function(header,idx){
    headers[idx] = header.trim();
  })

  lines.map(function(line, indexLine) {
    if (indexLine < hdrCt) return; // Jump header lines
    var obj = {};
    var currentline = line.split(",");
    headers.map(function(header, indexHeader) {
      obj[header] = currentline[indexHeader];
    });
    result.push(obj);
  });

  result.pop(); // remove the last item because undefined values

  return result; // JavaScript object
}

export function readCSV(url) {
  var vm = this;
  if (window.FileReader) {
    var reader = new FileReader();
    reader.readAsText(url);
    // Handle errors load
    reader.onload = function(event) {
      var csv = event.target.result;
      const jsonRecs = vm.csvJSON(csv);
      return jsonRecs;
    };
    reader.onerror = function(evt) {
      if (evt.target.error.name == "NotReadableError") {
        alert("Cannot read file!");
      }
    };
  } else {
    alert("FileReader are not supported in this browser.");
  }
}

export function loadCSV(url) {
  var vm = this;
  if (window.FileReader) {
    var reader = new FileReader();
    reader.readAsText(url);
    // Handle errors load
    reader.onload = function(event) {
      var csv = event.target.result;
      const jsonRecs = vm.csvJSON(csv);
      return jsonRecs;
    };
    reader.onerror = function(evt) {
      if (evt.target.error.name == "NotReadableError") {
        alert("Cannot read file!");
      }
    };
  } else {
    alert("FileReader are not supported in this browser.");
  }
}
