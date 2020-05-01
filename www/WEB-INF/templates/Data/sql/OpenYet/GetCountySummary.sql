SELECT *
FROM cv_county_current_summary
WHERE statecode = '{{statecode}}'
AND countrycode = '{{countrycode}}'
ORDER BY county;