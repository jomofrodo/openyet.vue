
SELECT * FROM county
WHERE 1=1
{{#if statecode}}
AND statecode = {{statecode}}
{{/if}}
ORDER BY statecode,county