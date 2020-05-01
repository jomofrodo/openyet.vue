/*
date
country
countrycode
region
confirmed
confirmedincrease
ppositive
ppositiveincrease
totaltestresults
totaltestresultsincrease
death
deathincrease
deaths_per_1m
confirmed_per_1m
tests_per_1m
population

Column sizes: 104px,200px,85px,61px,103px,116px,76px,59px,72px,75px,78px,89px,89px,87px,84px,200px
*/
export const colDefs = [
    { colName: "countrycode", header: "CountryCode", hidden:false, width:104 },
    { colName: "country", header: "Country", hidden:false, width: 200  },
    { colName: "confirmed", header: "Confirmed", type:"integer", hidden:false, width: 85 },
    { colName: "confirmedincrease", header: "Confirmed increase", type:"integer", hidden:false, width: 61 },
    { colName: "confirmed_per_1m", header: "Tot Confirmed/ 1M pop", type:"integer", width:75},
    { colName: "death", header: "Deaths", type:"integer", hidden:false, width: 72 },
    { colName: "deathincrease", header: "Death increase", type:"integer", hidden:false, width:75 },
    { colName: "deaths_per_1m", header: "Tot Deaths/ 1M pop", type:"integer", hidden: false, width: 90},
    { colName: "ppositive", header: "% positive", title: "% positive tests", width:75, 
        help: "% of tests with a positive (meaning confirming) result", type:"integer", hidden:false },
    { colName: "ppositiveincrease", header: "% positive increase", type:"integer", hidden: false, width:116 },
    { colName: "totaltestresults", header: "Total # Tests", type:"integer", hidden:false, width:76 },
    { colName: "totaltestresultsincrease", header: "Test # increase", type:"integer", hidden: false, width:59 },
    { colName: "tests_per_1m", header: "Tot Tests/ 1M pop", type:"integer", hidden: false, width:90}, 
    { colName: "population", header: "Population (thousands)", type:"integer", hidden: false, width:90},
    { colName: "date", header: "Date", hidden:false, width:200},
    { colName: "sourcecode", header: "Source", hidden:true }

]

export const urlData = "cvd/getData/";

