/*select * from combined
WHERE state is null
AND date = (SELECT max(date) FROM combined)
ORDER BY countrycode;*/

-- Nation latest summary
DROP VIEW IF EXISTS cv_national_current_summary;
CREATE VIEW cv_national_current_summary AS
SELECT date, country, countrycode, region, confirmed, confirmedincrease, 
	ROUND(positive/(negative+positive),2)*100 as ppositive,
    ROUND(positiveincrease/(positiveincrease + negativeincrease),2)*100 AS ppositiveincrease,
	(positive + negative) as totaltestresults,
    totaltestresultsincrease,
    death,deathincrease, 
    ROUND((death*1000/(population)),0) as deaths_per_1M,
    ROUND((confirmed*1000/population),0) as confirmed_per_1M,
    ROUND((totaltestresults/population)*1000,0) as tests_per_1M,
    population
FROM combined
JOIN country USING (countrycode)
WHERE state is null
AND date = (SELECT max(date) FROM combined)
ORDER BY countrycode;


-- State latest summary
DROP VIEW IF EXISTS cv_state_current_summary;
CREATE VIEW cv_state_current_summary AS
SELECT DISTINCT
date,combined.statecode,combined.state, country, combined.countrycode, confirmed, confirmedincrease, 
	CASE
		WHEN totaltestresults is null THEN null
		WHEN totaltestresults = 0 THEN 0
		WHEN totaltestresults > 0 THEN ROUND(positive/(totaltestresults),2)*100
		ELSE null
	END as ppositive,
	CASE
		WHEN totaltestresultsincrease is null THEN null
		WHEN totaltestresultsincrease = 0 THEN 0
		WHEN totaltestresultsincrease > 0 THEN
		    ROUND(positiveincrease/(totaltestresultsincrease),2)*100
		ELSE null
	END AS ppositiveincrease,
	totaltestresults,
    totaltestresultsincrease,
    death,deathincrease, 
    ROUND((death*1000000/(state.population)),0) as deaths_per_1M,
    ROUND((confirmed*1000000/state.population),0) as confirmed_per_1M,
    ROUND((totaltestresults/state.population)*1000000,0) as tests_per_1M,
    state.population
FROM combined
JOIN country USING (countrycode)
JOIN state ON (combined.statecode = state.ansi AND combined.countrycode = state.countrycode)
WHERE state is not null
AND county is null
AND date = (SELECT max(date) FROM combined)
ORDER BY countrycode, combined.statecode;

-- Counties latest summary

DROP VIEW IF EXISTS cv_county_current_summary;
CREATE VIEW cv_county_current_summary AS
SELECT DISTINCT 
date,combined.county, combined.statecode,combined.state, country, combined.countrycode, confirmed, confirmedincrease, 
	CASE
		WHEN totaltestresults is null THEN null
		WHEN totaltestresults = 0 THEN 0
		WHEN totaltestresults > 0 THEN ROUND(positive/(totaltestresults),2)*100
		ELSE null
	END as ppositive,
	CASE
		WHEN totaltestresultsincrease is null THEN null
		WHEN totaltestresultsincrease = 0 THEN 0
		WHEN totaltestresultsincrease > 0 THEN
		    ROUND(positiveincrease/(totaltestresultsincrease),2)*100
		ELSE null
	END AS ppositiveincrease,
	totaltestresults,
    totaltestresultsincrease,
    death,deathincrease,  
    ROUND((death/(county.population)*(1000)),0) as deaths_per_1K,
    ROUND((confirmed/county.population)*(1000),0) as confirmed_per_1K,
    ROUND((totaltestresults/county.population)*(1000),0) as tests_per_1K,
    county.population
FROM combined
JOIN country USING (countrycode)
JOIN state ON (combined.statecode = state.ansi)
JOIN county USING (county,statecode)
WHERE combined.state is not null
AND combined.county is NOT NULL
AND date = (SELECT max(date) FROM combined)
ORDER BY countrycode, combined.statecode, combined.county;




