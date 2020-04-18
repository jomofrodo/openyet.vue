UPDATE combined SET country = 'Taiwan' WHERE country = 'Taiwan*';
UPDATE combined SET country = 'South Korea' WHERE country = 'Korea, South';
UPDATE combined SET country = 'United States' WHERE country = 'US';

UPDATE jh_timeseries SET country = 'Taiwan' WHERE country = 'Taiwan*';
UPDATE jh_timeseries SET country = 'South Korea' WHERE country = 'Korea, South'; 
UPDATE jh_timeseries SET country = 'United States' WHERE country = 'US';

UPDATE jh_us_timeseries SET country = 'Taiwan' WHERE country = 'Taiwan*'; 
UPDATE jh_us_timeseries SET country = 'South Korea' WHERE country = 'Korea, South'; 
UPDATE jh_us_timeseries SET country = 'United States' WHERE country = 'US';

UPDATE combined
SET countrycode = v1.countrycode
FROM( SELECT * FROM country ) as v1
WHERE combined.country = v1.name;