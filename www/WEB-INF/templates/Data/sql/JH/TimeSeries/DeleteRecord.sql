DELETE FROM confirmed
WHERE 
state = ${state}
country = ${country}
AND date = $(date}