
const optsPoolCode = [{ value: 'TEST_POOL_A', label: 'TEST_POOL_A' },
{ value: 'TEST_POOL_B', label: 'TEST_POOL_B' }];

export const colDefs = [
    { colName: "county", header: "County" , hidden:true },
    { colName: "statecode", header: "State" , hidden:true },
    { colName: "countrycode", header: "CountryCode", hidden:false },
    { colName: "country", header: "Country", hidden:true  },
    { colName: "week", header: "Week of", hidden: true},
    { colName: "conf1", header: "Conf change this week", type:"integer", hidden:false },
    { colName: "conf2", header: "Confirmed change last week", type:"integer", hidden:false },
    { colName: "conf3", header: "Confirmed change 2 weeks ago", type:"integer", hidden:false },
    { colName: "death1", header: "Deaths change this week", type:"integer", hidden:false },
    { colName: "death2", header: "Deaths last week", type:"integer", hidden:false },
    { colName: "death3", header: "Deaths change 2 weeks ago", type:"integer", hidden:false },
    { colName: "positive1", header: "Positive tests change this week", type:"integer", hidden:false },
    { colName: "positive2", header: "Positive tests change last week", type:"integer", hidden:false },
    { colName: "positive3", header: "Positive tests change 2 weeks ago", type:"integer", hidden:false },
    { colName: "conf1_avg", header: "Conf change per/day this week", type:"integer", hidden:false },
    { colName: "conf2_avg", header: "Conf change per/day last week", type:"integer", hidden:false },
    { colName: "conf3_avg", header: "Conf change per/day 2 weeks ago", type:"integer", hidden:false },
    { colName: "sourcecode", header: "Source", hidden:false }

]

export const urlData = "cvd/getData/combined";