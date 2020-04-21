SELECT 
	'JH_GLOBAL' as srccode,
	count(tsid) as ct,
	max (to_date(date,'mm/dd/yy')) as date
	FROM jh_timeseries
UNION

SELECT 
	'JH_US' as srccode,
	count(tsid) as ctUS,
	max (to_date(date,'mm/dd/yy')) as date
	FROM jh_us_timeseries

UNION

SELECT 
	'CTP_STATES_DAILY' as srcCode,
	count(datechecked) as ct,
	max (to_date(substring(datechecked from 1 for 10),'YYYY-MM-DD')) as date
	FROM ctp_statesdaily;