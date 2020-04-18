SELECT city,
		state,
		state.ansi as statecode,
		country,
		confirmed,
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
		combined.countrycode
 from combined
 LEFT JOIN state ON (combined.state = state.name AND combined.countrycode = state.countrycode)
 WHERE 1 =1
{{#unless states}}
AND (state is null)
{{/unless}}
{{#unless cities}}
AND (city is null)
{{/unless}}

{{#if countrycode}}
AND combined.countrycode = '{{countrycode}}'
{{/if}}
{{#if statecode}}
AND state.ansi = '{{statecode}}'
{{/if}}
{{#if filterKey}}
AND ((combined.countrycode LIKE '{{filterKey}}')
	 OR (country LIKE '%{{filterKey}}%')
	 OR (sourcecode LIKE '{{filterKey}}')
	 {{#if states}}
	 OR (state LIKE '{{filterKey}}')
	 OR (state.ansi LIKE '{{filterKey}}')
	 {{/if}}
	 {{#if cities}}
	 OR (city LIKE '{{filterKey}}%')
	 {{/if}}
	 )
{{/if}}
ORDER BY country, state, dateSort DESC, city, sourcecode
{{#if limit}}
LIMIT {{limit}}
{{/if}}