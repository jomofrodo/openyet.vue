SELECT comb1.confirmed as cc, comb1.confirmedincrease as ci, comb2.death as d, comb2.deathincrease as di, comb2.date
FROM combined comb1
JOIN combined comb2 ON (comb1.countrycode = comb2.countrycode AND comb1.statecode = comb2.statecode AND comb1.county = comb2.county)
WHERE 1 =1
AND comb1.date = comb2.date - interval({{x}} days)

AND countrycode = '{{countrycode}}'

{{#unless statecode}}
AND county is null
AND state is null
{{/if}}
{{#if statecode}}{{#unless county}}
AND county is null
AND statecode = '{{statecode}}'
{{/unless}}{{/if}}
{{#if county}}
AND county = '{{county}}'
AND statecode = '{{statecode}}'
{{/if}}

ORDER BY countrycode,statecode,county,date
