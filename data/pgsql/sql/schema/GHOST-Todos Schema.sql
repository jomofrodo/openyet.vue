
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


-- Table: posts
CREATE TABLE posts ( 
    id               INTEGER         NOT NULL
                                     PRIMARY KEY AUTOINCREMENT,
    uuid             VARCHAR( 36 )   NOT NULL,
    title            VARCHAR( 150 )  NOT NULL,
    slug             VARCHAR( 150 )  NOT NULL,
    markdown         TEXT            NULL,
    html             TEXT            NULL,
    image            TEXT            NULL,
    featured         BOOLEAN         NOT NULL
                                     DEFAULT '0',
    page             BOOLEAN         NOT NULL
                                     DEFAULT '0',
    status           VARCHAR( 150 )  NOT NULL
                                     DEFAULT 'draft',
    language         VARCHAR( 6 )    NOT NULL
                                     DEFAULT 'en_US',
    meta_title       VARCHAR( 150 )  NULL,
    meta_description VARCHAR( 200 )  NULL,
    author_id        INTEGER         NOT NULL,
    created_at       DATETIME        NOT NULL,
    created_by       INTEGER         NOT NULL,
    updated_at       DATETIME        NULL,
    updated_by       INTEGER         NULL,
    published_at     DATETIME        NULL,
    published_by     INTEGER         NULL 
);


-- Table: users
CREATE TABLE users ( 
    id               INTEGER         NOT NULL
                                     PRIMARY KEY AUTOINCREMENT,
    uuid             VARCHAR( 36 )   NOT NULL,
    name             VARCHAR( 150 )  NOT NULL,
    slug             VARCHAR( 150 )  NOT NULL,
    password         VARCHAR( 60 )   NOT NULL,
    email            VARCHAR( 254 )  NOT NULL,
    image            TEXT            NULL,
    cover            TEXT            NULL,
    bio              VARCHAR( 200 )  NULL,
    website          TEXT            NULL,
    location         TEXT            NULL,
    accessibility    TEXT            NULL,
    status           VARCHAR( 150 )  NOT NULL
                                     DEFAULT 'active',
    language         VARCHAR( 6 )    NOT NULL
                                     DEFAULT 'en_US',
    meta_title       VARCHAR( 150 )  NULL,
    meta_description VARCHAR( 200 )  NULL,
    last_login       DATETIME        NULL,
    created_at       DATETIME        NOT NULL,
    created_by       INTEGER         NOT NULL,
    updated_at       DATETIME        NULL,
    updated_by       INTEGER         NULL 
);


-- Table: roles
CREATE TABLE roles ( 
    id          INTEGER         NOT NULL
                                PRIMARY KEY AUTOINCREMENT,
    uuid        VARCHAR( 36 )   NOT NULL,
    name        VARCHAR( 150 )  NOT NULL,
    description VARCHAR( 200 )  NULL,
    created_at  DATETIME        NOT NULL,
    created_by  INTEGER         NOT NULL,
    updated_at  DATETIME        NULL,
    updated_by  INTEGER         NULL 
);


-- Table: roles_users
CREATE TABLE roles_users ( 
    id      INTEGER NOT NULL
                    PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL 
);


-- Table: permissions
CREATE TABLE permissions ( 
    id          INTEGER         NOT NULL
                                PRIMARY KEY AUTOINCREMENT,
    uuid        VARCHAR( 36 )   NOT NULL,
    name        VARCHAR( 150 )  NOT NULL,
    object_type VARCHAR( 150 )  NOT NULL,
    action_type VARCHAR( 150 )  NOT NULL,
    object_id   INTEGER         NULL,
    created_at  DATETIME        NOT NULL,
    created_by  INTEGER         NOT NULL,
    updated_at  DATETIME        NULL,
    updated_by  INTEGER         NULL 
);


-- Table: permissions_users
CREATE TABLE permissions_users ( 
    id            INTEGER NOT NULL
                          PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL,
    permission_id INTEGER NOT NULL 
);


-- Table: permissions_roles
CREATE TABLE permissions_roles ( 
    id            INTEGER NOT NULL
                          PRIMARY KEY AUTOINCREMENT,
    role_id       INTEGER NOT NULL,
    permission_id INTEGER NOT NULL 
);


-- Table: permissions_apps
CREATE TABLE permissions_apps ( 
    id            INTEGER NOT NULL
                          PRIMARY KEY AUTOINCREMENT,
    app_id        INTEGER NOT NULL,
    permission_id INTEGER NOT NULL 
);


-- Table: settings
CREATE TABLE settings ( 
    id         INTEGER         NOT NULL
                               PRIMARY KEY AUTOINCREMENT,
    uuid       VARCHAR( 36 )   NOT NULL,
    [key]      VARCHAR( 150 )  NOT NULL,
    value      TEXT            NULL,
    type       VARCHAR( 150 )  NOT NULL
                               DEFAULT 'core',
    created_at DATETIME        NOT NULL,
    created_by INTEGER         NOT NULL,
    updated_at DATETIME        NULL,
    updated_by INTEGER         NULL 
);


-- Table: tags
CREATE TABLE tags ( 
    id               INTEGER         NOT NULL
                                     PRIMARY KEY AUTOINCREMENT,
    uuid             VARCHAR( 36 )   NOT NULL,
    name             VARCHAR( 150 )  NOT NULL,
    slug             VARCHAR( 150 )  NOT NULL,
    description      VARCHAR( 200 )  NULL,
    image            TEXT            NULL,
    hidden           BOOLEAN         NOT NULL
                                     DEFAULT '0',
    parent_id        INTEGER         NULL,
    meta_title       VARCHAR( 150 )  NULL,
    meta_description VARCHAR( 200 )  NULL,
    created_at       DATETIME        NOT NULL,
    created_by       INTEGER         NOT NULL,
    updated_at       DATETIME        NULL,
    updated_by       INTEGER         NULL 
);


-- Table: posts_tags
CREATE TABLE posts_tags ( 
    id      INTEGER NOT NULL
                    PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    tag_id  INTEGER NOT NULL,
    FOREIGN KEY ( post_id ) REFERENCES posts ( id ),
    FOREIGN KEY ( tag_id ) REFERENCES tags ( id ) 
);


-- Table: apps
CREATE TABLE apps ( 
    id         INTEGER         NOT NULL
                               PRIMARY KEY AUTOINCREMENT,
    uuid       VARCHAR( 36 )   NOT NULL,
    name       VARCHAR( 150 )  NOT NULL,
    slug       VARCHAR( 150 )  NOT NULL,
    version    VARCHAR( 150 )  NOT NULL,
    status     VARCHAR( 150 )  NOT NULL
                               DEFAULT 'inactive',
    created_at DATETIME        NOT NULL,
    created_by INTEGER         NOT NULL,
    updated_at DATETIME        NULL,
    updated_by INTEGER         NULL 
);


-- Table: app_settings
CREATE TABLE app_settings ( 
    id         INTEGER         NOT NULL
                               PRIMARY KEY AUTOINCREMENT,
    uuid       VARCHAR( 36 )   NOT NULL,
    [key]      VARCHAR( 150 )  NOT NULL,
    value      TEXT            NULL,
    app_id     INTEGER         NOT NULL,
    created_at DATETIME        NOT NULL,
    created_by INTEGER         NOT NULL,
    updated_at DATETIME        NULL,
    updated_by INTEGER         NULL,
    FOREIGN KEY ( app_id ) REFERENCES apps ( id ) 
);


-- Table: app_fields
CREATE TABLE app_fields ( 
    id             INTEGER         NOT NULL
                                   PRIMARY KEY AUTOINCREMENT,
    uuid           VARCHAR( 36 )   NOT NULL,
    [key]          VARCHAR( 150 )  NOT NULL,
    value          TEXT            NULL,
    type           VARCHAR( 150 )  NOT NULL
                                   DEFAULT 'html',
    app_id         INTEGER         NOT NULL,
    relatable_id   INTEGER         NOT NULL,
    relatable_type VARCHAR( 150 )  NOT NULL
                                   DEFAULT 'posts',
    active         BOOLEAN         NOT NULL
                                   DEFAULT '1',
    created_at     DATETIME        NOT NULL,
    created_by     INTEGER         NOT NULL,
    updated_at     DATETIME        NULL,
    updated_by     INTEGER         NULL,
    FOREIGN KEY ( app_id ) REFERENCES apps ( id ) 
);


-- Table: clients
CREATE TABLE clients ( 
    id         INTEGER         NOT NULL
                               PRIMARY KEY AUTOINCREMENT,
    uuid       VARCHAR( 36 )   NOT NULL,
    name       VARCHAR( 150 )  NOT NULL,
    slug       VARCHAR( 150 )  NOT NULL,
    secret     VARCHAR( 150 )  NOT NULL,
    created_at DATETIME        NOT NULL,
    created_by INTEGER         NOT NULL,
    updated_at DATETIME        NULL,
    updated_by INTEGER         NULL 
);


-- Table: accesstokens
CREATE TABLE accesstokens ( 
    id        INTEGER         NOT NULL
                              PRIMARY KEY AUTOINCREMENT,
    token     VARCHAR( 255 )  NOT NULL,
    user_id   INTEGER         NOT NULL,
    client_id INTEGER         NOT NULL,
    expires   BIGINT          NOT NULL,
    FOREIGN KEY ( user_id ) REFERENCES users ( id ),
    FOREIGN KEY ( client_id ) REFERENCES clients ( id ) 
);


-- Table: refreshtokens
CREATE TABLE refreshtokens ( 
    id        INTEGER         NOT NULL
                              PRIMARY KEY AUTOINCREMENT,
    token     VARCHAR( 255 )  NOT NULL,
    user_id   INTEGER         NOT NULL,
    client_id INTEGER         NOT NULL,
    expires   BIGINT          NOT NULL,
    FOREIGN KEY ( user_id ) REFERENCES users ( id ),
    FOREIGN KEY ( client_id ) REFERENCES clients ( id ) 
);


-- Table: do_relation
CREATE TABLE do_relation ( 
    drcode VARCHAR( 10 )   PRIMARY KEY,
    drdesc VARCHAR( 128 ) 
);


-- Table: do
CREATE TABLE do ( 
    doid      NUMERIC( 12, 0 )  PRIMARY KEY,
    dccode    VARCHAR( 10 )     DEFAULT ( 'DO' ) 
                                REFERENCES do_class ( dccode ),
    dorecid   NUMERIC( 12, 0 ),
    dorecuuid VARCHAR( 36 ) 
);


-- Table: do_do
CREATE TABLE do_do ( 
    doid    NUMERIC( 12, 0 )  REFERENCES do ( doid ),
    dorelid NUMERIC( 12, 0 )  REFERENCES do,
    drcode  VARCHAR( 10 )     DEFAULT ( 'CHILD' ) 
                              REFERENCES do_relation ( drcode ),
    ddsort  NUMERIC( 12, 0 ),
    ddleft  NUMERIC( 12, 0 ),
    ddright NUMERIC( 12, 0 ),
    PRIMARY KEY ( doid, dorelid, drcode ) 
);


-- Index: posts_slug_unique
CREATE UNIQUE INDEX posts_slug_unique ON posts ( 
    slug 
);


-- Index: users_slug_unique
CREATE UNIQUE INDEX users_slug_unique ON users ( 
    slug 
);


-- Index: users_email_unique
CREATE UNIQUE INDEX users_email_unique ON users ( 
    email 
);


-- Index: settings_key_unique
CREATE UNIQUE INDEX settings_key_unique ON settings ( 
    [key] 
);


-- Index: tags_slug_unique
CREATE UNIQUE INDEX tags_slug_unique ON tags ( 
    slug 
);


-- Index: apps_name_unique
CREATE UNIQUE INDEX apps_name_unique ON apps ( 
    name 
);


-- Index: apps_slug_unique
CREATE UNIQUE INDEX apps_slug_unique ON apps ( 
    slug 
);


-- Index: app_settings_key_unique
CREATE UNIQUE INDEX app_settings_key_unique ON app_settings ( 
    [key] 
);


-- Index: clients_name_unique
CREATE UNIQUE INDEX clients_name_unique ON clients ( 
    name 
);


-- Index: clients_slug_unique
CREATE UNIQUE INDEX clients_slug_unique ON clients ( 
    slug 
);


-- Index: clients_secret_unique
CREATE UNIQUE INDEX clients_secret_unique ON clients ( 
    secret 
);


-- Index: accesstokens_token_unique
CREATE UNIQUE INDEX accesstokens_token_unique ON accesstokens ( 
    token 
);


-- Index: refreshtokens_token_unique
CREATE UNIQUE INDEX refreshtokens_token_unique ON refreshtokens ( 
    token 
);


-- Trigger: create_do
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
;


-- Trigger: delete_do_dos
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
;


-- Trigger: create_do_do_0
CREATE TRIGGER create_do_do_0
       AFTER INSERT ON do
       WHEN new.doID > 1000
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
;


-- Trigger: dot_create_post_tag_do_do
CREATE TRIGGER dot_create_post_tag_do_do
       AFTER INSERT ON posts_tags
BEGIN
    INSERT INTO do_do ( 
        doID,
        doRelID,
        drCode,
        ddSort 
    ) 
    SELECT d1.doID AS doID,
           d2.doID AS doRelID,
           'CHILD' AS drCode,
           coalesce( ( 
               SELECT max( dd.ddSort ) + 1
                 FROM do_do
                WHERE dd.doRelID = d2.doID 
           ) 
           , 1 ) AS ddSort
      FROM do d1, 
           do d2, 
           do_do dd
     WHERE d1.doRecUUID =( 
               SELECT doRecUUID
                 FROM DO
                WHERE doRecID = new.post_ID 
                      AND
                      dcCode = 'DO' 
           ) 
           
           AND
           d2.doRecUUID =( 
               SELECT doRecUUID
                 FROM do
                WHERE doRecID = new.tag_id 
                      AND
                      dcCode = 'TAG' 
           );
END;
;


-- Trigger: dot_delete_post_tag
CREATE TRIGGER dot_delete_post_tag
       AFTER DELETE ON posts_tags
BEGIN
    DELETE
      FROM do_do
     WHERE doID =( 
               SELECT doID
                 FROM do
                WHERE doRecID = old.post_id 
                      AND
                      dcCode = 'DO' 
           ) 
           
           AND
           doRelID =( 
               SELECT doID
                 FROM do
                WHERE doRecID = old.tag_id 
                      AND
                      dcCode = 'TAG' 
           );
END;
;


-- Trigger: dot_delete_tag
CREATE TRIGGER dot_delete_tag
       AFTER DELETE ON tags
BEGIN
    DELETE
      FROM do_do
     WHERE doRelID =( 
               SELECT doID
                 FROM do
                WHERE doRecUUID = old.uuid 
                      AND
                      dcCode = 'TAG' 
           ) 
           
           AND
           drCode = 'CHILD';

    DELETE
      FROM do
     WHERE doRecUUID = old.uuid 
           AND
           dcCode = 'TAG';
END;
;


-- Trigger: dot_create_tag_do
CREATE TRIGGER dot_create_tag_do
       AFTER INSERT ON tags
BEGIN
    INSERT INTO do ( 
        doid,
        dorecid,
        dorecuuid,
        dccode 
    ) 
    SELECT max( doID ) + 1 AS doid,
           new.id,
           new.uuid,
           'TAG'
      FROM do
     WHERE doID < 10000;
END;
;


-- View: dv_relct
CREATE VIEW dv_relct AS
       SELECT do.doid,
              count( do.doid ) AS relct
         FROM do
              JOIN do_do dd
                   USING ( doid ) 
              JOIN do d2
                ON ( dd.doRelID = d2.doID ) 
        WHERE d2.dcCode != 'TAG'
        GROUP BY do.doid;
;


-- View: dv_inbox
CREATE VIEW dv_inbox AS
       SELECT doid,
              id,
              title,
              uuid
         FROM dv_relct
              JOIN do
                   USING ( doid ) 
              JOIN posts
                ON ( dorecid = id ) 
        WHERE relct = 1
        ORDER BY doid;
;

