-- State totals -- create state rollups where state entries are broken out by county


-- SELECT * FROM ctp_statesdaily;


/*
DELETE FROM combined;
SELECT * FROM combined WHERE countrycode = 'USA' ORDER BY country,state,date;
SELECT * FROM combined;
DELETE FROM combined WHERE countrycode = 'USA' AND state is null;
*/

BEGIN TRANSACTION;
-- commit;
INSERT INTO combined (
state,
country,
confirmed,
positive,
positiveincrease,
negative,
negativeincrease,
hospitalized,
hospitalizedincrease,
death,
deathincrease,
recovered,
recoveredincrease,
datechecked,
date,
sourcecode,
countrycode
)
SELECT state,
country,
sum(confirmed) as confirmed,
sum(positive) as positive,
sum(positiveincrease) as positiveincrease,
sum(negative) as negative,
sum(negativeincrease) as negativeincrease,
sum(hospitalized) as hospitalized,
sum(hospitalizedincrease) as hospitalizedincrease,
sum(death) as death,
sum(deathincrease) as deathincrease,
sum(recovered) as recovered,
sum(recoveredincrease) as recoveredincrease,
datechecked,
date,
sourcecode,
countrycode
FROM combined
WHERE 1=1 -- countrycode = 'USA'
AND state is not null
AND county is not null
GROUP BY country, state, date, countrycode, datechecked,sourcecode;

commit;