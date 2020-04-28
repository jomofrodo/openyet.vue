
-- Create week views at the national,state,county levels

DROP VIEW IF EXISTS cv_national_weekly CASCADE;
CREATE VIEW cv_national_weekly AS
 SELECT combined.countrycode,
         combined.statecode,
         combined.county,
         date_trunc('week', date) AS week,
         max(combined.confirmed) AS confirmed,
         sum(combined.confirmedincrease) AS confirmedincrease,
         max(combined.positive) AS positive,
         sum(combined.positiveincrease) AS positiveincrease,
         max(combined.negative) as negative,
         sum(combined.negativeincrease) as negativeincrease,  
         CASE
         	WHEN sum(positiveincrease) <1 THEN 0
            ELSE ROUND((sum(positiveincrease)/(sum(positiveincrease) + sum(negativeincrease))),2)*100
         END as perc_positive,
         max(combined.death) AS death,
         sum(combined.deathincrease) AS deathincrease,
         count(combined.date) AS datect,
         combined.sourcecode
  FROM combined
  WHERE 1 = 1 
  AND combined.county IS NULL 
  AND combined.state IS NULL
  GROUP BY combined.countrycode,
           combined.statecode,
           combined.county,
           combined.sourcecode,
           week 
  ORDER BY combined.countrycode,
           combined.statecode,
           combined.county,
           combined.sourcecode,
           week;


DROP VIEW IF EXISTS cv_state_weekly CASCADE;
CREATE VIEW cv_state_weekly AS
SELECT
	countrycode,statecode,county,
		date_trunc('week', date) AS week,
		max(confirmed) as confirmed,
		sum(confirmedincrease) as confirmedincrease,
        max(positive) as positive,
        sum(positiveincrease) as positiveincrease,
        max(combined.negative) as negative,
        sum(combined.negativeincrease) as negativeincrease,  
        CASE
         	WHEN sum(positiveincrease) <1 THEN 0
            ELSE ROUND((sum(positiveincrease)/(sum(positiveincrease) + sum(negativeincrease))),2)*100
         END as perc_positive,
        max(death) as death,
        sum(deathincrease) as deathincrease,
        count(date) as datect,
    sourcecode
FROM combined
WHERE 1 = 1
AND state is not null
AND county is null
GROUP BY countrycode,statecode,county,sourcecode, week
ORDER BY countrycode,statecode,county,sourcecode,week;


DROP VIEW IF EXISTS cv_county_weekly CASCADE;
CREATE VIEW cv_county_weekly AS
SELECT
	countrycode,statecode,county,
		date_trunc('week', date) AS week,
		max(confirmed) as confirmed,
		sum(confirmedincrease) as confirmedincrease,
        max(positive) as positive,
        sum(positiveincrease) as positiveincrease,
        max(combined.negative) as negative,
        sum(combined.negativeincrease) as negativeincrease,  
        CASE
         	WHEN sum(positiveincrease) <1 THEN 0
            ELSE ROUND((sum(positiveincrease)/(sum(positiveincrease) + sum(negativeincrease))),2)*100
         END as perc_positive,
        max(death) as death,
        sum(deathincrease) as deathincrease,
        count(date) as datect,
        
    sourcecode
FROM combined
WHERE 1 = 1
AND state is not null
AND county is not null
GROUP BY countrycode,statecode,county,sourcecode, week
ORDER BY countrycode,statecode,county,sourcecode,week;














