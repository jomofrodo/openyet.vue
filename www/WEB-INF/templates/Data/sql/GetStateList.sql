{{#if countrycode}}
SELECT ansi as statecode, name, 0 as idx,countrycode
FROM state
WHERE countrycode = '{{countrycode}}'
AND population >= 0
{{/if}}
{{#unless countrycode}}
SELECT ansi as statecode, name, 0 as idx, countrycode
FROM state
WHERE countrycode = 'USA'
UNION
SELECT ansi as statecode, name, 1 as idx, countrycode 
FROM state 
WHERE countrycode != 'USA'
{{/unless}}
ORDER BY idx,countrycode,name,statecode