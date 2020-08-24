SELECT 
	'JH_GLOBAL' as srccode,
	count(tsid) as ct,
	max (date) as date
	FROM jh_timeseries
UNION

SELECT 
	'JH_US' as srccode,
	count(tsid) as ctUS,
	max (date) as date
	FROM jh_us_timeseries

UNION

SELECT 
	'CTP_STATES_DAILY' as srcCode,
	count(date) as ct,
	max (date) as date
	FROM ctp_statesdaily
	
UNION
	
SELECT
	'COMBINED' as srcCode,
	count(date) as ct,
	max(date) as date
	FROM combined;