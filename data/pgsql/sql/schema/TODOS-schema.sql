-- SQL Manager for PostgreSQL 5.2.0.3
-- ---------------------------------------
-- Host      : localhost
-- Database  : kanbandev
-- Version   : PostgreSQL 9.3.2, compiled by Visual C++ build 1600, 64-bit

begin transaction;

CREATE SCHEMA todos AUTHORIZATION kanban;
SET check_function_bodies = false;
--
-- Definition for function tf_create_do (OID = 24977) : 
--
SET search_path = todos, pg_catalog;
CREATE FUNCTION todos.tf_create_do (
)
RETURNS trigger
AS 
$body$
    DECLARE
        -- local variables
    BEGIN
        -- id and timestamp new records
        IF NEW.doID IS NULL
        THEN NEW.doID := nextval('todos.seqdo');
        END IF;
        RETURN NEW;
    END;
$body$
LANGUAGE plpgsql;
--
-- Definition for function trf_create_do_do_0 (OID = 24979) : 
--
CREATE FUNCTION todos.trf_create_do_do_0 (
)
RETURNS trigger
AS 
$body$
BEGIN
  INSERT INTO todos.do_do ( 
        doID,
        relDoID,
        drCode,
        ddSort 
    ) 
    VALUES ( 
        new.doID,
        1,
        'CHILD',
        new.doID 
    );
    RETURN NEW;
END;
$body$
LANGUAGE plpgsql;
--
-- Definition for function trf_delete_do_dos (OID = 24980) : 
--
CREATE FUNCTION todos.trf_delete_do_dos (
)
RETURNS trigger
AS 
$body$
BEGIN
    DELETE
      FROM todos.do_do
     WHERE reldoid = old.doid;

    DELETE
      FROM todos.do_do
     WHERE doid = old.doid;
     
    RETURN old;
END;
$body$
LANGUAGE plpgsql;
--
-- Definition for function trf_create_post_do (OID = 24981) : 
--
CREATE FUNCTION todos.trf_create_post_do (
)
RETURNS trigger
AS 
$body$
BEGIN
    INSERT INTO todos.do ( 
        dorecid,
        dorecuuid,
        dccode 
    ) 
    SELECT   
           NEW.id,
           NEW.uuid,
           'DO'
      FROM todos.do;
    RETURN NEW;
END;
$body$
LANGUAGE plpgsql;
--
-- Structure for table do_class (OID = 24925) : 
--
CREATE TABLE todos.do_class (
    dccode varchar(10) NOT NULL,
    dcname varchar(25),
    dcdesc varchar(128),
    dcdb varchar(25),
    dctable varchar(25),
    dc_pk_fld varchar(25),
    dc_uuid_fld varchar(25)
)
WITH (oids = false);
--
-- Structure for table do (OID = 24930) : 
--
CREATE TABLE todos."do" (
    doid numeric(12,0) NOT NULL,
    dccode varchar(10) DEFAULT 'DO'::character varying,
    dorecid numeric(12,0),
    dorecuuid varchar(36)
)
WITH (oids = false);
--
-- Structure for table do_relation (OID = 24941) : 
--
CREATE TABLE todos.do_relation (
    drcode varchar(10) NOT NULL,
    drdesc varchar(128)
)
WITH (oids = false);
--
-- Structure for table do_do (OID = 24946) : 
--
CREATE TABLE todos.do_do (
    doid numeric(12,0) NOT NULL,
    reldoid numeric(12,0) NOT NULL,
    drcode varchar(10) DEFAULT 'PARENT'::character varying NOT NULL,
    ddsort numeric(12,0),
    ddleft numeric(12,0),
    ddright numeric(12,0)
)
WITH (oids = false);
--
-- Definition for sequence seqdo (OID = 24973) : 
--
CREATE SEQUENCE todos.seqdo
    START WITH 10000
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;
--
-- Data for table todos.do_class (OID = 24925) (LIMIT 0,3)
--
INSERT INTO do_class (dccode, dcname, dcdesc, dcdb, dctable, dc_pk_fld, dc_uuid_fld)
VALUES ('EO', 'EO', 'EO', NULL, NULL, NULL, NULL);

INSERT INTO do_class (dccode, dcname, dcdesc, dcdb, dctable, dc_pk_fld, dc_uuid_fld)
VALUES ('DO', 'DO', 'Base DO', NULL, 'posts', 'id', 'uuid');

INSERT INTO do_class (dccode, dcname, dcdesc, dcdb, dctable, dc_pk_fld, dc_uuid_fld)
VALUES ('TAG', 'TAG', 'Tag DO', NULL, 'tags', 'id', 'uuid');

--
-- Data for table todos."do" (OID = 24930) (LIMIT 0,3)
--
INSERT INTO "do" (doid, dccode, dorecid, dorecuuid)
VALUES (0, 'EO', 0, '0');

INSERT INTO "do" (doid, dccode, dorecid, dorecuuid)
VALUES (1, 'EO', 0, '0');

INSERT INTO "do" (doid, dccode, dorecid, dorecuuid)
VALUES (2, 'DO', 0, '0');

--
-- Data for table todos.do_relation (OID = 24941) (LIMIT 0,3)
--
INSERT INTO do_relation (drcode, drdesc)
VALUES ('PARENT', 'Parent');

INSERT INTO do_relation (drcode, drdesc)
VALUES ('SELF', 'Self pointer');

INSERT INTO do_relation (drcode, drdesc)
VALUES ('CHILD', 'Child pointer');

--
-- Data for table todos.do_do (OID = 24946) (LIMIT 0,7)
--
INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (0, 0, 'SELF', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (0, 1, 'PARENT', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (0, 2, 'PARENT', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (1, 0, 'SELF', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (2, 0, 'SELF', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (1, 0, 'CHILD', NULL, NULL, NULL);

INSERT INTO do_do (doid, reldoid, drcode, ddsort, ddleft, ddright)
VALUES (2, 0, 'CHILD', NULL, NULL, NULL);

--
-- Definition for index do_class_pkey (OID = 24928) : 
--
ALTER TABLE ONLY do_class
    ADD CONSTRAINT do_class_pkey
    PRIMARY KEY (dccode);
--
-- Definition for index do_pkey (OID = 24934) : 
--
ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_pkey
    PRIMARY KEY (doid);
--
-- Definition for index do_dccode_fkey (OID = 24936) : 
--
ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_dccode_fkey
    FOREIGN KEY (dccode) REFERENCES do_class(dccode);
--
-- Definition for index do_relation_pkey (OID = 24944) : 
--
ALTER TABLE ONLY do_relation
    ADD CONSTRAINT do_relation_pkey
    PRIMARY KEY (drcode);
--
-- Definition for index do_do_pkey (OID = 24950) : 
--
ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_pkey
    PRIMARY KEY (doid, reldoid, drcode);
--
-- Definition for index do_do_doid_fkey (OID = 24952) : 
--
ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_doid_fkey
    FOREIGN KEY (doid) REFERENCES "do"(doid);
--
-- Definition for index do_do_reldoid_fkey (OID = 24957) : 
--
ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_reldoid_fkey
    FOREIGN KEY (reldoid) REFERENCES "do"(doid);
--
-- Definition for index do_do_drcode_fkey (OID = 24962) : 
--
ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_drcode_fkey
    FOREIGN KEY (drcode) REFERENCES do_relation(drcode);
--
-- Definition for trigger do_tr (OID = 24978) : 
--
CREATE TRIGGER do_tr
    BEFORE INSERT ON "do"
    FOR EACH ROW
    EXECUTE PROCEDURE tf_create_do ();
--
-- Definition for trigger tr_do_after_insert (OID = 24983) : 
--
CREATE TRIGGER tr_do_after_insert
    AFTER INSERT ON "do"
    FOR EACH ROW
    EXECUTE PROCEDURE trf_create_do_do_0 ();
--
-- Definition for trigger tr_do_after_delete (OID = 24985) : 
--
CREATE TRIGGER tr_do_after_delete
    BEFORE DELETE ON "do"
    FOR EACH ROW
    EXECUTE PROCEDURE trf_delete_do_dos ();
--
-- Data for sequence todos.seqdo (OID = 24973)
--
SELECT pg_catalog.setval('seqdo', 10003, true);
--
-- Comments
--
COMMENT ON SCHEMA public IS 'standard public schema';


-- Don't hook up triggers until after loading EO records 0 and 1
--CREATE TRIGGER tr_do_after_insert AFTER INSERT ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_create_do_do_0();
--CREATE TRIGGER tr_do_after_delete AFTER DELETE ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_delete_do_dos();
--

commit;

    
    