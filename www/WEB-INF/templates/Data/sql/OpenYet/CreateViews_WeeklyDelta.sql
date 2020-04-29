/*
 * w0 is current week
 * w1 is 1 week ago
 * w2 is 2 weeks ago
 * w3 is 3 weeks ago
 */
DROP VIEW IF EXISTS cv_national_weekly_delta;
CREATE VIEW cv_national_weekly_delta AS

SELECT w3.countrycode, w3.statecode, w3.county, 
w4.confirmed as conf_base, w4.death as death_base, w4.perc_positive as perc_positive_base,
thisweek as week0, w1.week as week1, w2.week as week2, w3.week as week3,
CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
w3.confirmedincrease - w4.confirmedincrease as confd3,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
w3.deathincrease - w4.deathincrease as deathd3,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
w3.perc_positive - w4.perc_positive as perc_positived3,
thisweek,lastweek,twoweeks,
coalesce(w0.datect,0) as datect 

FROM (
  SELECT date_trunc('week',now()) as thisweek,
  date_trunc('week',now() - interval '1 week') as lastweek,
  date_trunc('week', now() - interval '2 weeks') as twoweeks,
  date_trunc('week',now() - interval '3 weeks') as threeweeks,
  date_trunc('week',now() - interval '4 weeks') as fourweeks
  )v_weeks
 	LEFT JOIN cv_national_weekly w4 ON(w4.week = fourweeks)
 	LEFT JOIN cv_national_weekly w3 ON(w3.week = threeweeks AND w3.countrycode = w4.countrycode)
	LEFT JOIN cv_national_weekly w2 ON(w2.week = twoweeks AND w2.countrycode = w3.countrycode)
	LEFT JOIN cv_national_weekly w1 ON(w1.week = lastweek AND w1.countrycode = w3.countrycode)
	LEFT JOIN cv_national_weekly w0 ON(w0.week = thisweek AND w0.countrycode = w3.countrycode)

WHERE 1=1;

-- SELECT * FROM cv_national_weekly_delta;


DROP VIEW IF EXISTS cv_state_weekly_delta;
CREATE VIEW cv_state_weekly_delta AS

SELECT w3.countrycode, w3.statecode, w3.county,
w4.confirmed as conf_base, w4.death as death_base, w4.perc_positive as perc_positive_base,
thisweek as week0, w1.week as week1, w2.week as week2, w3.week as week3,
CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
w3.confirmedincrease - w4.confirmedincrease as confd3,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
w3.deathincrease - w4.deathincrease as deathd3,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
w3.perc_positive - w4.perc_positive as perc_positived3,
thisweek,lastweek,twoweeks,
coalesce(w0.datect,0) as datect 

FROM (
  SELECT date_trunc('week',now()) as thisweek,
  date_trunc('week',now() - interval '1 week') as lastweek,
  date_trunc('week', now() - interval '2 weeks') as twoweeks,
  date_trunc('week',now() - interval '3 weeks') as threeweeks,
  date_trunc('week',now() - interval '4 weeks') as fourweeks
  )v_weeks
 	LEFT JOIN cv_state_weekly w4 ON(w4.week = fourweeks)
	LEFT JOIN cv_state_weekly w3 ON(w3.week = threeweeks AND w3.countrycode = w4.countrycode AND w3.statecode = w4.statecode)
	LEFT JOIN cv_state_weekly w2 ON(w2.week = twoweeks AND w2.countrycode = w3.countrycode AND w2.statecode = w3.statecode)
	LEFT JOIN cv_state_weekly w1 ON(w1.week = lastweek AND w1.countrycode = w3.countrycode AND w1.statecode = w3.statecode)
	LEFT JOIN cv_state_weekly w0 ON(w0.week = thisweek AND w0.countrycode = w3.countrycode AND w0.statecode = w3.statecode)

WHERE 1=1;

-- SELECT * FROM cv_state_weekly_delta;



DROP VIEW IF EXISTS cv_county_weekly_delta;
CREATE VIEW cv_county_weekly_delta AS

SELECT w3.countrycode, w3.statecode, w3.county, 
w4.confirmed as conf_base, w4.death as death_base, w4.perc_positive as perc_positive_base,
thisweek as week0, w1.week as week1, w2.week as week2, w3.week as week3,
CASE
	WHEN w0.datect=7 THEN w0.confirmedincrease - w1.confirmedincrease
	ELSE ROUND(((w0.confirmedincrease/w0.datect)*7),0) - w1.confirmedincrease
END as confd0,
w1.confirmedincrease - w2.confirmedincrease as confd1,
w2.confirmedincrease - w3.confirmedincrease as confd2,
w3.confirmedincrease - w4.confirmedincrease as confd3,
CASE
	WHEN w0.datect=7 THEN w0.deathincrease - w1.deathincrease
	ELSE ROUND(((w0.deathincrease/w0.datect)*7),0) - w1.deathincrease
END as deathd0,
w1.deathincrease - w2.deathincrease as deathd1,
w2.deathincrease - w3.deathincrease as deathd2,
w3.deathincrease - w4.deathincrease as deathd3,
CASE
	WHEN w0.datect=7 THEN w0.perc_positive - w1.perc_positive
	ELSE round(((w0.perc_positive/w0.datect)*7),0) - w1.perc_positive
END as perc_positived0,
w1.perc_positive - w2.perc_positive as perc_positived1,
w2.perc_positive - w3.perc_positive as perc_positived2,
w3.perc_positive - w4.perc_positive as perc_positived3,
thisweek,lastweek,twoweeks,
coalesce(w0.datect,0) as datect 

FROM (
  SELECT date_trunc('week',now()) as thisweek,
  date_trunc('week',now() - interval '1 week') as lastweek,
  date_trunc('week', now() - interval '2 weeks') as twoweeks,
  date_trunc('week',now() - interval '3 weeks') as threeweeks,
  date_trunc('week', now() - interval '4 weeks') as fourweeks
  )v_weeks
 	LEFT JOIN cv_county_weekly w4 ON(w4.week = fourweeks)
 	LEFT JOIN cv_county_weekly w3 ON(w3.week = threeweeks AND w3.countrycode = w4.countrycode AND w3.statecode = w4.statecode AND w3.county = w4.county)
	LEFT JOIN cv_county_weekly w2 ON(w2.week = twoweeks AND w2.countrycode = w3.countrycode AND w2.statecode = w3.statecode AND w2.county = w3.county)
	LEFT JOIN cv_county_weekly w1 ON(w1.week = lastweek AND w1.countrycode = w3.countrycode AND w1.statecode = w3.statecode AND w1.county = w3.county)
	LEFT JOIN cv_county_weekly w0 ON(w0.week = thisweek AND w0.countrycode = w3.countrycode AND w0.statecode = w3.statecode AND w0.county = w3.county)

WHERE 1=1;


-- SELECT * FROM cv_county_weekly_delta;

/*SELECT * FROM cv_national_weekly WHERE positive is not null;
SELECT * FROM cv_state_weekly WHERE positive is not null;

SELECT * FROM cv_county_weekly_delta WHERE county = 'Alameda';
SELECT * FROM cv_state_weekly WHERE statecode = 'CA';


SELECT * FROM jh_us_timeseries WHERE population is not null;*/
