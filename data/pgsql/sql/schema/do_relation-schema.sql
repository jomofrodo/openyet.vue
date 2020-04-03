
-- Table: do_relation
CREATE TABLE do_relation ( 
    drcode VARCHAR( 10 )   PRIMARY KEY,
    drdesc VARCHAR( 128 ) 
);

INSERT INTO [do_relation] ([drcode], [drdesc]) VALUES ('PARENT', 'Parent');
INSERT INTO [do_relation] ([drcode], [drdesc]) VALUES ('SELF', 'Self pointer');
INSERT INTO [do_relation] ([drcode], [drdesc]) VALUES ('CHILD', 'Child pointer');
