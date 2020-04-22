
const optsPoolCode = [{ value: 'TEST_POOL_A', label: 'TEST_POOL_A' },
{ value: 'TEST_POOL_B', label: 'TEST_POOL_B' }];

export const colDefs = [
    { colName: "county", header: "County" , hidden:true },
    { colName: "state", header: "State" , hidden:true },
    { colName: "countrycode", header: "CountryCode", hidden:false },
    { colName: "country", header: "Country", hidden:false  },
    { colName: "confirmed", header: "Confirmed", type:"integer", hidden:false },
    { colName: "confirmedincrease", header: "Confirmed increase", type:"integer", hidden:false },
    { colName: "positive", header: "positive", title: "positive test", 
        help: "a test for covid19 was administered and came back with a positive (meaning confirming) result", type:"integer", hidden:true },
    { colName: "positiveincrease", header: "+ increase", type:"integer", hidden: true },
    { colName: "negative", header: "Negatives" , type:"integer", hidden:false},
    { colName: "negativeincrease",header: "- increase", type: "integer", hidden: true},
    { colName: "totaltestresults", header: "Total # Tests", type:"integer", hidden:true },
    { colName: "totaltestresultsincrease", header: "Test # increase", type:"integer", hidden: true },
    { colName: "hospitalized", header: "Hospitalized", type:"integer", hidden: true },
    { colName: "hospitalizedincrease", header: "Hospitalized increase", type:"integer", hidden: true },
    { colName: "death", header: "Deaths", type:"integer", hidden:false },
    { colName: "deathincrease", header: "Death increase", type:"integer", hidden:false },
    { colName: "recovered", header: "Recovered", type:"integer", hidden:false },
    { colName: "recoveredincrease", header: "Recovered increase", type:"integer", hidden:false },
    { colName: "datechecked", header: "String date", hidden: true},
    { colName: "date", header: "Date", hidden:false},
    { colName: "sourcecode", header: "Source", hidden:false }

]

export const urlData = "cvd/getData/combined";