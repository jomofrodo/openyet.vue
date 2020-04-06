
const optsPoolCode = [{ value: 'TEST_POOL_A', label: 'TEST_POOL_A' },
{ value: 'TEST_POOL_B', label: 'TEST_POOL_B' }];

export const colDefs = [
    { colName: "state", header: "State" , hidden:true },
    { colName: "countrycode", header: "CountryCode" },
    { colName: "country", header: "Country"  },
    { colName: "positive", header: "Confirmed", type:"integer" },
    { colName: "positiveincrease", header: "+ increase", type:"integer", hidden: true },
    { colName: "negative", header: "Negatives" , type:"integer"},
    { colName: "negativeincrease",header: "- increase", type: "integer", hidden: true},
    { colName: "totaltestresults", header: "Total # Tests", type:"integer", hidden:true },
    { colName: "totaltestresultsincrease", header: "Test # increase", type:"integer", hidden: true },
    { colName: "hospitalized", header: "Hospitalized", type:"integer", hidden: true },
    { colName: "hospitalizedincrease", header: "Hospitalized increase", type:"integer", hidden: true },
    { colName: "death", header: "Dead", type:"integer" },
    { colName: "deathincrease", header: "Death increase", type:"integer" },
    { colName: "recovered", header: "Recovered", type:"integer" },
    { colName: "recoveredincrease", header: "Recovered increase", type:"integer" },
    { colName: "datechecked", header: "String date"},
    { colName: "date", header: "Date"},
    { colName: "sourcecode", header: "Source" }

]

export const urlData = "cvd/getData/combined";