/*
 * w0 is current week
 * w1 is 1 week ago
 * w2 is 2 weeks ago
 * w3 is 3 weeks ago
 */
DROP VIEW IF EXISTS cv_national_weekly_delta;
CREATE VIEW cv_national_weekly_delta AS

SELECT w0.countrycode, w0.statecode, w0.county,

CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
thisweek,lastweek,twoweeks,
w0.datect

FROM (
  SELECT *,
  w0.week as thisweek,
  w0.week - interval '1 week' as lastweek,
  w0.week - interval '2 weeks' as twoweeks,
  w0.week - interval '3 weeks' as threeweeks
  -- w1.confirmed - w2.confirmed as confdelta
  FROM cv_national_weekly w0
  WHERE w0.week = date_trunc('week',now())
) w0
LEFT JOIN cv_national_weekly w1 USING(countrycode)
LEFT JOIN cv_national_weekly w2 USING(countrycode)
LEFT JOIN cv_national_weekly w3 USING(countrycode)
WHERE 1=1
AND w0.week = thisweek
AND w1.week = lastweek
AND w2.week = twoweeks
AND w3.week = threeweeks
AND countrycode = 'USA';

-- SELECT * FROM cv_national_weekly_delta;


DROP VIEW IF EXISTS cv_state_weekly_delta;
CREATE VIEW cv_state_weekly_delta AS

SELECT w0.countrycode, w0.statecode, w0.county,
CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
thisweek,lastweek,twoweeks,
w0.datect
	FROM (
	  SELECT *,
	  w0.week as thisweek,
	  w0.week - interval '1 week' as lastweek,
	  w0.week - interval '2 weeks' as twoweeks,
	  w0.week - interval '3 weeks' as threeweeks
	  -- w1.confirmed - w2.confirmed as confdelta
	  FROM cv_state_weekly w0
	  WHERE w0.week = date_trunc('week',now())
	) w0
	LEFT JOIN cv_state_weekly w1 USING(countrycode,statecode)
	LEFT JOIN cv_state_weekly w2 USING(countrycode,statecode)
	LEFT JOIN cv_state_weekly w3 USING(countrycode,statecode)
	WHERE 1=1
	AND w0.week = thisweek
	AND w1.week = lastweek
	AND w2.week = twoweeks
	AND w3.week = threeweeks;

-- SELECT * FROM cv_state_weekly_delta;



DROP VIEW IF EXISTS cv_county_weekly_delta;
CREATE VIEW cv_county_weekly_delta AS

SELECT w0.countrycode, w0.statecode, w0.county,
CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
thisweek,lastweek,twoweeks,
w0.datect

FROM (
  SELECT *,
  w0.week as thisweek,
  w0.week - interval '1 week' as lastweek,
  w0.week - interval '2 weeks' as twoweeks,
  w0.week - interval '3 weeks' as threeweeks
  -- w1.confirmed - w2.confirmed as confdelta
  FROM cv_county_weekly w0
  WHERE w0.week = date_trunc('week',now())
) w0
LEFT JOIN cv_county_weekly w1 USING(countrycode,statecode,county)
LEFT JOIN cv_county_weekly w2 USING(countrycode,statecode,county)
LEFT JOIN cv_county_weekly w3 USING(countrycode,statecode,county)
WHERE 1=1
AND w0.week = thisweek
AND w1.week = lastweek
AND w2.week = twoweeks
AND w3.week = threeweeks;

-- SELECT * FROM cv_county_weekly_delta;

/*SELECT * FROM cv_national_weekly WHERE positive is not null;
SELECT * FROM cv_state_weekly WHERE positive is not null;

SELECT * FROM cv_county_weekly_delta WHERE county = 'Alameda';
SELECT * FROM cv_state_weekly WHERE statecode = 'CA';


SELECT * FROM jh_us_timeseries WHERE population is not null;*/
