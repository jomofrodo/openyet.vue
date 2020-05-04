
-- Create new confirmed increase numbers

-- First, set percent positive ppositive
UPDATE combined SET totaltestresults = null WHERE totaltestresults = 0;
/*UPDATE combined SET ppositive = ROUND(positive/totaltestresults,2);*/

DROP TABLE IF EXISTS tt_increase CASCADE;
CREATE TABLE tt_increase AS
SELECT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, combined.ppositive, combined.death,
 c2.confirmed as confv1, c2.death as deathv1, c2.positive as positivev1, c2.ppositive as ppositivev1,c2.totaltestresults as totaltestsv1,
 c3.confirmed as confv2, c3.death as deathv2, c3.positive as positivev2, c3.ppositive ppositivev2,c3.totaltestresults as totaltestsv2,
(combined.confirmed-c2.confirmed) as confd0,
(combined.death-c2.death) as deathd0,
(combined.positive-c2.positive) as positived0,
 combined.ppositive - c2.ppositive as ppositived0,
c2.confirmed-c3.confirmed as confd1,
c2.death - c3.death as deathd1,
c2.positive - c3.positive as positived1,
c2.ppositive - c3.ppositive as ppositived1
FROM combined
JOIN combined c2 USING (countrycode)
JOIN combined c3 USING (countrycode)
WHERE c2.date = combined.date - interval '1 day'
AND c3.date = combined.date - interval '2 days'
AND (c2.state is null AND combined.state is null AND c3.state is null);  -- National rollups
--
UPDATE combined SET totaltestresults = null WHERE totaltestresults = 0;

-- State rollups
INSERT INTO tt_increase
SELECT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, combined.ppositive, combined.death,
 c2.confirmed as confv1, c2.death as deathv1, c2.positive as positivev1, c2.ppositive as ppositivev1,c2.totaltestresults as totaltestsv1,
 c3.confirmed as confv2, c3.death as deathv2, c3.positive as positivev2, c3.ppositive ppositivev2,c3.totaltestresults as totaltestsv2,
(combined.confirmed-c2.confirmed) as confd0,
(combined.death-c2.death) as deathd0,
(combined.positive-c2.positive) as positived0,
 combined.ppositive - c2.ppositive as ppositived0,
c2.confirmed-c3.confirmed as confd1,
c2.death - c3.death as deathd1,
c2.positive - c3.positive as positived1,
c2.ppositive - c3.ppositive as ppositived1
FROM combined
JOIN combined c2 USING (countrycode,state)
JOIN combined c3 USING (countrycode,state)
WHERE c2.date = combined.date - interval '1 day'
AND c3.date = combined.date - interval '2 days'
AND (c2.county is null AND c3.county is null AND combined.county is null);

-- county records
INSERT INTO tt_increase
SELECT countrycode,combined.state,combined.county, 
 combined.date, 
 combined.confirmed, combined.ppositive, combined.death,
 c2.confirmed as confv1, c2.death as deathv1, c2.positive as positivev1, c2.ppositive as ppositivev1,c2.totaltestresults as totaltestsv1,
 c3.confirmed as confv2, c3.death as deathv2, c3.positive as positivev2, c3.ppositive ppositivev2,c3.totaltestresults as totaltestsv2,
(combined.confirmed-c2.confirmed) as confd0,
(combined.death-c2.death) as deathd0,
(combined.positive-c2.positive) as positived0,
 combined.ppositive - c2.ppositive as ppositived0,
c2.confirmed-c3.confirmed as confd1,
c2.death - c3.death as deathd1,
c2.positive - c3.positive as positived1,
c2.ppositive - c3.ppositive as ppositived1
FROM combined
JOIN combined c2 USING (countrycode,state,county)
JOIN combined c3 USING (countrycode,state,county)
WHERE c2.date = combined.date - interval '1 day'
AND c3.date = combined.date - interval '2 days'
AND county is not null;
--AND combined.date > '{{lastUpdate}}';

--SELECT count(countrycode) as ct from tt_increase;

UPDATE tt_increase SET confv1 = null WHERE confv1 = 0;
UPDATE tt_increase SET deathv1 = null WHERE deathv1 = 0;
UPDATE tt_increase SET positivev1 = null WHERE positivev1 = 0;
UPDATE tt_increase SET confv2 = null WHERE confv2 = 0;
UPDATE tt_increase SET deathv2 = null WHERE deathv2 = 0;
UPDATE tt_increase SET positivev2 = null WHERE positivev2 = 0;

---------------------------------------------------
-- Set basic increase values into combined
---------------------------------------------------

UPDATE combined
SET confirmedincrease = vi.confd0, confd0 = vi.confd0,
    positiveincrease = vi.positived0, ppositived0 = vi.ppositived0,
    deathincrease = vi.deathd0, deathd0 = vi.deathd0,
    confd1 = vi.confd1, ppositived1 = vi.ppositived1, deathd1 = vi.deathd1
FROM tt_increase vi
WHERE 1=1
/*AND combined.confirmedincrease is null*/
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state is null AND vi.state is null);  -- National rollup

UPDATE combined
SET confirmedincrease = vi.confd0, confd0 = vi.confd0,
    positiveincrease = vi.positived0, ppositived0 = vi.ppositived0,
    deathincrease = vi.deathd0, deathd0 = vi.deathd0,
    confd1 = vi.confd1, ppositived1 = vi.ppositived1, deathd1 = vi.deathd1
FROM tt_increase vi
WHERE 1=1
/*AND combined.confirmedincrease is null*/
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state = vi.state) AND (combined.county is null AND vi.county is null); -- State rollups

UPDATE combined
SET confirmedincrease = vi.confd0, confd0 = vi.confd0,
    positiveincrease = vi.positived0, ppositived0 = vi.ppositived0,
    deathincrease = vi.deathd0, deathd0 = vi.deathd0,
    confd1 = vi.confd1, ppositived1 = vi.ppositived1, deathd1 = vi.deathd1
FROM tt_increase vi
WHERE 1=1
/*AND combined.confirmedincrease is null*/
AND combined.countrycode = vi.countrycode
AND combined.date = vi.date
AND (combined.state = vi.state) AND (combined.county = vi.county);-- Counties
