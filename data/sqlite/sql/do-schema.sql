
-- Table: do_class
CREATE TABLE do_class ( 
    dccode      VARCHAR( 10 )   PRIMARY KEY,
    dcname      VARCHAR( 25 ),
    dcdesc      VARCHAR( 128 ),
    dcdb        VARCHAR( 25 ),
    dctable     VARCHAR( 25 ),
    dc_pk_fld   VARCHAR( 25 ),
    dc_uuid_fld VARCHAR( 25 ) 
);

-- Table: do
CREATE TABLE do ( 
    doid      NUMERIC( 12, 0 )  PRIMARY KEY,
    dccode    VARCHAR( 10 )     DEFAULT ( 'DO' ) 
                                REFERENCES do_class ( dccode ),
    dorecid   NUMERIC( 12, 0 ),
    dorecuuid VARCHAR( 36 ) 
);

-- Table: do_relation
CREATE TABLE do_relation ( 
    drcode VARCHAR( 10 )   PRIMARY KEY,
    drdesc VARCHAR( 128 ) 
);

-- Table: do_do
CREATE TABLE do_do ( 
    doid    NUMERIC( 12, 0 )  REFERENCES do,
    reldoid NUMERIC( 12, 0 )  REFERENCES do,
    drcode  VARCHAR( 10 )     DEFAULT ( 'PARENT' ) 
                              REFERENCES do_relation ( drcode ),
    ddsort  NUMERIC( 12, 0 ),
    ddleft  NUMERIC( 12, 0 ),
    ddright NUMERIC( 12, 0 ),
    PRIMARY KEY ( doid, reldoid, drcode ) 
);


-- Triggers
CREATE TRIGGER create_do_do_0
       AFTER INSERT ON do
BEGIN
    INSERT INTO do_do ( 
        doID,
        doRelID,
        drCode,
        ddSort 
    ) 
    VALUES ( 
        new.doID,
        1,
        'CHILD',
        new.doID 
    );
END;

CREATE TRIGGER delete_do_dos
       AFTER DELETE ON do
BEGIN
    DELETE
      FROM do_do
     WHERE dorelid = old.doid;

    DELETE
      FROM do_do
     WHERE doid = old.doid;
END;


-- And now for a trigger on table posts
-- GHOST Specific

CREATE TRIGGER create_do
       AFTER INSERT ON posts
BEGIN
    INSERT INTO do ( 
        doid,
        dorecid,
        dorecuuid,
        dccode 
    ) 
    SELECT max( doid ) + 1,
           NEW.id,
           NEW.uuid,
           'DO'
      FROM do;
END;
