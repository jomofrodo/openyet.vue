SELECT * , 1 as idx
FROM cv_national_current_summary
WHERE countrycode = '{{countrycode}}'

UNION

SELECT * , 2 as idx
FROM cv_national_current_summary
WHERE countrycode != '{{countrycode}}'

ORDER BY idx, country;