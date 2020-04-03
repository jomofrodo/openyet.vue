--Create a set of DO records for the primary ghost.posts table
/*
 * Todos Canonical records are from 0 - 999
 * TAG records start at 1000
 * DO records start at 10000
 */
/*
DELETE FROM do WHERE doid is null OR doid > 10000;
DELETE FROM do_do WHERE doRelID > 10000;
SELECT * FROM do;
SELECT * FROM do_do;
*/
-- rollback transaction;

begin transaction;

INSERT INTO DO (doID,doRecID,doRecUUID,dcCode)
SELECT id + 10000 as doID,id,uuid, 'DO' as dcCode
FROM posts;


-- Tags
INSERT INTO DO(doID,doRecID,doRecUUID,dcCode)
SELECT id + 1000 as doID, id,uuid,'TAG' as dcCode
FROM tags;

-- Page Tags

INSERT INTO do_do(doID,doRelID,drCode,ddSort)
SELECT d1.doID,d2.doID,'CHILD' as drCode,  
(SELECT count(doID) + 1 as ct
FROM do_do WHERE doRelID = d2.doID) as ddSort
FROM do d1, do d2,
posts_tags pt
WHERE pt.post_id = d1.doRecID AND d1.dcCode = 'DO'
AND pt.tag_id = d2.doRecID AND d2.dcCode = 'TAG'
ORDER BY d2.doID;

commit transaction;

