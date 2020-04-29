SELECT *
FROM (

SELECT *, 1 as idx
FROM cv_national_weekly_delta
WHERE countrycode = '{{countrycode}}'

{{#if statecode}}
UNION
SELECT *, 2 as idx
FROM cv_state_weekly_delta
WHERE statecode = '{{statecode}}'
{{/if}}

{{#if county}}
UNION
SELECT  *, 3 as idx 
FROM cv_county_weekly_delta
WHERE county = '{{county}}'
{{/if}}

)vweekly

ORDER BY idx
