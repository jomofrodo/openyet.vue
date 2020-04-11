SELECT countrycode, name, 0 as idx
FROM country
WHERE countrycode = 'USA'
UNION
SELECT countrycode, name, 1 as idx 
FROM country 
WHERE region != '' AND region is not null 
ORDER BY idx,name