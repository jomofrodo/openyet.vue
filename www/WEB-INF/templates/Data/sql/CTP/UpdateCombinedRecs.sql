-- UPDATE US rollup records

UPDATE combined
SET positive = v1.positive,
	positiveincrease = v1.positiveincrease,
	negative = v1.negative,
	negativeincrease = v1.negativeincrease,
	totaltestresults = v1.totaltestresults,
	ppositive = v1.positive/v1.totaltestresults,
	totaltestresultsincrease = v1.totaltestresultsincrease,
	hospitalized = v1.hospitalized,
	hospitalizedincrease = v1.hospitalizedincrease

FROM( SELECT country,
	'USA' as countrycode,
	sum(positive) as positive,
	sum(positiveincrease) as positiveincrease,
	sum(negative) as negative,
	sum(negativeincrease) as negativeincrease,
	sum(hospitalized) as hospitalized,
	sum(hospitalizedincrease) as hospitalizedincrease,
	sum(totaltestresults) as totaltestresults,
	sum(totaltestresultsincrease) as totaltestresultsincrease,
	sum(death) as death,
	sum(deathincrease) as deathincrease,
	to_date(substring(datechecked from 1 for 10),'YYYY-MM-DD') as date
	
	FROM ctp_statesdaily
	WHERE 1=1 -- countrycode = 'USA'
	AND state is not null AND state != ''
	GROUP BY country, date, countrycode

) AS v1
WHERE ((combined.state is null))		-- rollup records
AND combined.countrycode = v1.countrycode
AND combined.date = v1.date;

-- Updates State records in combined

UPDATE combined
SET positive = v1.positive,
	positiveincrease = v1.positiveincrease,
	negative = v1.negative,
	negativeincrease = v1.negativeincrease,
	totaltestresults = v1.totaltestresults,
	ppositive = v1.positive/v1.totaltestresults,
	totaltestresultsincrease = v1.totaltestresultsincrease,
	hospitalized = v1.hospitalized,
	hospitalizedincrease = v1.hospitalizedincrease
FROM (SELECT  *,
	to_date(substring(ctp.datechecked from 1 for 10),'YYYY-MM-DD') as date
	FROM ctp_statesdaily ctp) v1
	WHERE  v1.state = combined.statecode
	AND combined.county is null
	AND v1.date = combined.date;
	
