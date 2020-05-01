/*
date
statecode
state
country
countrycode
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
*/
export const colDefs = [
    { colName: "statecode", header: "StateCode", hidden: false, width: 104 },
    { colName: "state", header: "State", hidden: false, width: 200 },
    { colName: "countrycode", header: "CountryCode", hidden: false, width: 104 },
    {
        colName: "confirmed", header: "Confirmed", type: "integer", hidden: false, width: 85,
        help: "Confirmed cases of Covid19"
    },
    {
        colName: "confirmedincrease", header: "Confirmed increase", type: "integer", hidden: false, width: 61,
        help: "Increase in the number of confirmed cases reported this day"
    },
    {
        colName: "confirmed_per_1m", header: "Confirmed/ 1M pop", type: "integer", width: 75,
        help: "Number of confirmed cases per 1 million individuals in the population"
    },
    { colName: "death", header: "Deaths", type: "integer", hidden: false, width: 72 },
    { colName: "deathincrease", header: "Death increase", type: "integer", hidden: false, width: 75 },
    { colName: "deaths_per_1m", header: "Deaths/ 1M pop", type: "integer", hidden: false, width: 90 },
    {
        colName: "ppositive", header: "% positive", title: "% positive tests", width: 75,
        help: "% of tests with a positive (meaning confirming) result", type: "integer", hidden: false
    },
    { colName: "ppositiveincrease", header: "% positive increase", type: "integer", hidden: false, width: 116 },
    { colName: "totaltestresults", header: "Total # Tests", type: "integer", hidden: false, width: 76 },
    { colName: "totaltestresultsincrease", header: "Test # increase", type: "integer", hidden: false, width: 59 },
    { colName: "tests_per_1m", header: "Tests/ 1M pop", type: "integer", hidden: false, width: 90 },
    { colName: "population", header: "Population (thousands)", type: "integer", hidden: false, width: 90 },
    { colName: "date", header: "Date", hidden: false, width: 200 }

]

export const urlData = "cvd/getData/";

