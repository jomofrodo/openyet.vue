
-- Table: do_do
CREATE TABLE do_do ( 
    doid    NUMERIC( 12, 0 )  REFERENCES do,
    dorelid NUMERIC( 12, 0 )  REFERENCES do,
    drcode  VARCHAR( 10 )     DEFAULT ( 'PARENT' ) 
                              REFERENCES do_relation ( drcode ),
    ddsort  NUMERIC( 12, 0 ),
    ddleft  NUMERIC( 12, 0 ),
    ddright NUMERIC( 12, 0 ),
    PRIMARY KEY ( doid, dorelid, drcode ) 
);

INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 1, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 2, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 3, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 4, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 5, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 6, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 7, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 8, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 9, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 10, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 11, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 12, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 13, 'PARENT', null, null, null);
INSERT INTO [do_do] ([doid], [dorelid], [drcode], [ddsort], [ddleft], [ddright]) VALUES (0, 14, 'PARENT', null, null, null);
