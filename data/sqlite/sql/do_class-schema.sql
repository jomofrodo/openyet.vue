
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

INSERT INTO [do_class] ([dccode], [dcname], [dcdesc], [dcdb], [dctable], [dc_pk_fld], [dc_uuid_fld]) VALUES ('EO', 'EO', 'EO', null, null, null, null);
INSERT INTO [do_class] ([dccode], [dcname], [dcdesc], [dcdb], [dctable], [dc_pk_fld], [dc_uuid_fld]) VALUES ('DO', 'DO', 'Base DO', null, 'posts', 'id', 'uuid');
