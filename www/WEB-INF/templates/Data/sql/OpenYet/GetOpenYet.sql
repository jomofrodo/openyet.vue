SELECT *
FROM (
{{#unless statecode}}
SELECT *, 1 as idx
FROM cv_national_weekly_delta
JOIN cv_national_weekly USING(countrycode)
WHERE countrycode = '{{countrycode}}'
{{/unless}}

{{#if statecode}}{{#unless county}}
SELECT *, 2 as idx
FROM cv_state_weekly_delta
JOIN cv_state_weekly USING(countrycode,statecode)
WHERE statecode = '{{statecode}}'
AND countrycode = '{{countrycode}}'
{{/unless}}{{/if}}

{{#if county}}
SELECT  *, 3 as idx 
FROM cv_county_weekly_delta
JOIN cv_county_weekly USING(countrycode,statecode,county)
WHERE county = '{{county}}'
AND statecode = '{{statecode}}'
{{/if}}

)vweekly

ORDER BY idx
