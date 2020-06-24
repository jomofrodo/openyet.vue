SELECT county,
		state,
		state.ansi as statecode,
		country,
		confirmed,
		confirmedincrease,
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
AND (county is null)
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
	 OR (county LIKE '{{filterKey}}%')
	 {{/if}}
	 )
{{/if}}
ORDER BY country, state, dateSort DESC, county, sourcecode
{{#if limit}}
LIMIT {{limit}}
{{/if}}