SELECT state,
		country,
		positive,
		positiveincrease,
		negative,
		negativeincrease,
		totaltestresults,
		totaltestresultsincrease,
		hospitalized,
		hospitalizedincrease,
		death,
		deathincrease,
		recovered,
		recoveredincrease,
		datechecked,
		TO_CHAR(date, 'yyyy-mm-dd') as date,
		TO_CHAR(date, 'yyyy-mm-dd') as dateSort,
		sourcecode,
		countrycode
 from combined
 WHERE 1 =1
{{#unless states}}
AND (state is null OR state = '')
{{/unless}}
{{#if filterKey}}
AND ((countrycode LIKE '{{filterKey}}')
	 OR (country LIKE '%{{filterKey}}%')
	 OR (sourcecode LIKE '{{filterKey}}')
	 )
{{/if}}
ORDER BY country, state, dateSort, sourcecode
{{#if limit}}
LIMIT {{limit}}
{{/if}}