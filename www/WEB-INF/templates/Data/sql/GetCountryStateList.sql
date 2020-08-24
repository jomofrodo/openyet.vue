SELECT ansi as statecode, name, 0 as idx,countrycode
FROM state
WHERE countrycode = '{{countryCode}}'
AND population >= 0
ORDER BY idx,countrycode,name,statecode