SELECT *
FROM (
{{#unless statecode}}
SELECT *, 1 as idx
FROM oyv_open_yet 
WHERE county is null
AND state is null
AND countrycode = '{{countrycode}}'
{{/unless}}

{{#if statecode}}{{#unless county}}
SELECT *, 2 as idx
FROM oyv_open_yet 
WHERE county is null
AND state is not null
AND statecode = '{{statecode}}'
AND countrycode = '{{countrycode}}'
{{/unless}}{{/if}}

{{#if county}}
SELECT  *, 3 as idx 
FROM oyv_open_yet 
WHERE county = '{{county}}'
AND statecode = '{{statecode}}'
{{/if}}

)voy

WHERE 1 = 1
AND date > (SELECT max(date) FROM combined) - interval '14 days'
--AND date > to_date('{{lastUpdate}}','mm/dd/yyyy') - interval '14 days'
ORDER BY idx, date
