
-- Create new confirmed increase numbers

DROP TABLE IF EXISTS tt_increase;
CREATE TABLE tt_increase AS
-- National rollups
SELECT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, c2.confirmed as confd1, 
(combined.confirmed-c2.confirmed) as confirmedincrease,
(combined.death-c2.death) as deathincrease,
(combined.positive-c2.positive) as positiveincrease,
(combined.negative-c2.negative) as negativeincrease 
FROM combined
JOIN combined c2 USING (countrycode)
WHERE c2.date = combined.date - interval '1 day'
AND (c2.state is null AND combined.state is null);


-- State rollups
INSERT INTO tt_increase
SELECT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, c2.confirmed as confd1, 
(combined.confirmed-c2.confirmed) as confirmedincrease,
(combined.death-c2.death) as deathincrease,
(combined.positive-c2.positive) as positiveincrease,
(combined.negative-c2.negative) as negativeincrease 
FROM combined
JOIN combined c2 USING (countrycode,state)
WHERE c2.date = combined.date - interval '1 day'
AND (c2.county is null AND combined.county is null);

-- county records
INSERT INTO tt_increase
SELECT DISTINCT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, c2.confirmed as confd1, 
(combined.confirmed-c2.confirmed) as confirmedincrease,
(combined.death-c2.death) as deathincrease,
(combined.positive-c2.positive) as positiveincrease,
(combined.negative-c2.negative) as negativeincrease 
FROM combined
JOIN combined c2 USING (countrycode,state,county)
WHERE c2.date = combined.date - interval '1 day'
AND c2.state = combined.state
AND c2.county = combined.county
AND countrycode is not null
AND county is not null;

SELECT count(countrycode) as ct from tt_increase;

---------------------------------------------------
-- Set values into combined
---------------------------------------------------

UPDATE combined
SET confirmedincrease = vi.confirmedincrease,
    positiveincrease = vi.positiveincrease,
    negativeincrease = vi.negativeincrease,
    deathincrease = vi.deathincrease 
FROM tt_increase vi
WHERE 1=1
AND combined.confirmedincrease is null
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state is null AND vi.state is null);  -- National rollup

UPDATE combined
SET confirmedincrease = vi.confirmedincrease,
    positiveincrease = vi.positiveincrease,
    negativeincrease = vi.negativeincrease,
    deathincrease = vi.deathincrease 
FROM tt_increase vi
WHERE 1=1
AND combined.confirmedincrease is null
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state = vi.state) AND (combined.county is null AND vi.county is null); -- State rollups



UPDATE combined
SET confirmedincrease = vi.confirmedincrease,
    positiveincrease = vi.positiveincrease,
    negativeincrease = vi.negativeincrease,
    deathincrease = vi.deathincrease 
FROM tt_increase vi
WHERE 1=1
AND combined.confirmedincrease is null
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state = vi.state) AND (combined.county = vi.county);-- Counties



