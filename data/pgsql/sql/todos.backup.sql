--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.2
-- Dumped by pg_dump version 9.3.2
-- Started on 2016-12-18 22:34:02

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 7 (class 2615 OID 60890)
-- Name: todos; Type: SCHEMA; Schema: -; Owner: kanban
--

CREATE SCHEMA todos;


ALTER SCHEMA todos OWNER TO kanban;

SET search_path = todos, pg_catalog;

--
-- TOC entry 207 (class 1255 OID 60891)
-- Name: trf_create_do_do_0(); Type: FUNCTION; Schema: todos; Owner: postgres
--

CREATE FUNCTION trf_create_do_do_0() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
    RETURN new;
END;
$$;


ALTER FUNCTION todos.trf_create_do_do_0() OWNER TO postgres;

--
-- TOC entry 209 (class 1255 OID 60892)
-- Name: trf_create_post_do(); Type: FUNCTION; Schema: todos; Owner: postgres
--

CREATE FUNCTION trf_create_post_do() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO public.do ( 
        doid,
        dorecid,
        dorecuuid,
        dccode 
    ) 
    SELECT max( doid ) + 1,
           NEW.id,
           NEW.uuid,
           'DO'
      FROM public.do;
END;
$$;


ALTER FUNCTION todos.trf_create_post_do() OWNER TO postgres;

--
-- TOC entry 211 (class 1255 OID 60893)
-- Name: trf_delete_do_dos(); Type: FUNCTION; Schema: todos; Owner: postgres
--

CREATE FUNCTION trf_delete_do_dos() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE
      FROM todos.do_do
     WHERE reldoid = old.doid;

    DELETE
      FROM todos.do_do
     WHERE doid = old.doid;

     RETURN old;
END;
$$;


ALTER FUNCTION todos.trf_delete_do_dos() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 189 (class 1259 OID 68869)
-- Name: Channel; Type: TABLE; Schema: todos; Owner: kanban; Tablespace: 
--

CREATE TABLE "Channel" (
    "laneCode" character varying(20) NOT NULL,
    "lnName" character varying(80),
    "lnDesc" character varying(256)
);


ALTER TABLE todos."Channel" OWNER TO kanban;

--
-- TOC entry 187 (class 1259 OID 68851)
-- Name: Project; Type: TABLE; Schema: todos; Owner: kanban; Tablespace: 
--

CREATE TABLE "Project" (
    "projectCode" character varying(20),
    "prjjName" character varying(80) DEFAULT NULL::character varying,
    "prjDesc" character varying(256) DEFAULT NULL::character varying,
    "clientCode" character varying(80) DEFAULT NULL::character varying
);


ALTER TABLE todos."Project" OWNER TO kanban;

--
-- TOC entry 188 (class 1259 OID 68866)
-- Name: ToDo; Type: TABLE; Schema: todos; Owner: kanban; Tablespace: 
--

CREATE TABLE "ToDo" (
    "todoID" bigint NOT NULL,
    "tdName" character varying(80),
    "tdDesc" character varying(256),
    "tdComplete" numeric(1,0)
);


ALTER TABLE todos."ToDo" OWNER TO kanban;

--
-- TOC entry 182 (class 1259 OID 60894)
-- Name: seqdo; Type: SEQUENCE; Schema: todos; Owner: postgres
--

CREATE SEQUENCE seqdo
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE todos.seqdo OWNER TO postgres;

--
-- TOC entry 183 (class 1259 OID 60896)
-- Name: do; Type: TABLE; Schema: todos; Owner: postgres; Tablespace: 
--

CREATE TABLE "do" (
    doid numeric(12,0) DEFAULT nextval('seqdo'::regclass) NOT NULL,
    dccode character varying(10) DEFAULT 'DO'::character varying,
    dorecid numeric(12,0),
    dorecuuid character varying(36),
    dotitle text
);


ALTER TABLE todos."do" OWNER TO postgres;

--
-- TOC entry 184 (class 1259 OID 60904)
-- Name: do_class; Type: TABLE; Schema: todos; Owner: postgres; Tablespace: 
--

CREATE TABLE do_class (
    dccode character varying(10) NOT NULL,
    dcname character varying(25),
    dcdesc character varying(128),
    dcdb character varying(25),
    dctable character varying(25),
    dc_pk_fld character varying(25),
    dc_uuid_fld character varying(25)
);


ALTER TABLE todos.do_class OWNER TO postgres;

--
-- TOC entry 185 (class 1259 OID 60907)
-- Name: do_do; Type: TABLE; Schema: todos; Owner: postgres; Tablespace: 
--

CREATE TABLE do_do (
    doid numeric(12,0) NOT NULL,
    reldoid numeric(12,0) NOT NULL,
    drcode character varying(10) DEFAULT 'PARENT'::character varying NOT NULL,
    ddsort numeric(12,0),
    ddleft numeric(12,0),
    ddright numeric(12,0)
);


ALTER TABLE todos.do_do OWNER TO postgres;

--
-- TOC entry 186 (class 1259 OID 60911)
-- Name: do_relation; Type: TABLE; Schema: todos; Owner: postgres; Tablespace: 
--

CREATE TABLE do_relation (
    drcode character varying(10) NOT NULL,
    drdesc character varying(128)
);


ALTER TABLE todos.do_relation OWNER TO postgres;

--
-- TOC entry 2023 (class 0 OID 68869)
-- Dependencies: 189
-- Data for Name: Channel; Type: TABLE DATA; Schema: todos; Owner: kanban
--

COPY "Channel" ("laneCode", "lnName", "lnDesc") FROM stdin;
\.


--
-- TOC entry 2021 (class 0 OID 68851)
-- Dependencies: 187
-- Data for Name: Project; Type: TABLE DATA; Schema: todos; Owner: kanban
--

COPY "Project" ("projectCode", "prjjName", "prjDesc", "clientCode") FROM stdin;
\.


--
-- TOC entry 2022 (class 0 OID 68866)
-- Dependencies: 188
-- Data for Name: ToDo; Type: TABLE DATA; Schema: todos; Owner: kanban
--

COPY "ToDo" ("todoID", "tdName", "tdDesc", "tdComplete") FROM stdin;
\.


--
-- TOC entry 2017 (class 0 OID 60896)
-- Dependencies: 183
-- Data for Name: do; Type: TABLE DATA; Schema: todos; Owner: postgres
--

COPY "do" (doid, dccode, dorecid, dorecuuid, dotitle) FROM stdin;
0	EO	0	0	\N
1	EO	0	0	\N
2	DO	0	0	\N
3	DO	0	0	\N
\.


--
-- TOC entry 2018 (class 0 OID 60904)
-- Dependencies: 184
-- Data for Name: do_class; Type: TABLE DATA; Schema: todos; Owner: postgres
--

COPY do_class (dccode, dcname, dcdesc, dcdb, dctable, dc_pk_fld, dc_uuid_fld) FROM stdin;
EO	EO	EO	\N	\N	\N	\N
DO	DO	Base DO	\N	posts	id	uuid
TAG	TAG	Tag DO	\N	tags	id	uuid
SELF	SELF	SELF id	\N	\N	\N	\N
PARENT	PARENT	Parent id	\N	\N	\N	\N
\.


--
-- TOC entry 2019 (class 0 OID 60907)
-- Dependencies: 185
-- Data for Name: do_do; Type: TABLE DATA; Schema: todos; Owner: postgres
--

COPY do_do (doid, reldoid, drcode, ddsort, ddleft, ddright) FROM stdin;
0	0	SELF	\N	\N	\N
0	1	PARENT	\N	\N	\N
0	2	PARENT	\N	\N	\N
1	0	SELF	\N	\N	\N
2	0	SELF	\N	\N	\N
1	0	CHILD	\N	\N	\N
2	0	CHILD	\N	\N	\N
\.


--
-- TOC entry 2020 (class 0 OID 60911)
-- Dependencies: 186
-- Data for Name: do_relation; Type: TABLE DATA; Schema: todos; Owner: postgres
--

COPY do_relation (drcode, drdesc) FROM stdin;
PARENT	Parent
SELF	Self pointer
CHILD	Child pointer
\.


--
-- TOC entry 2028 (class 0 OID 0)
-- Dependencies: 182
-- Name: seqdo; Type: SEQUENCE SET; Schema: todos; Owner: postgres
--

SELECT pg_catalog.setval('seqdo', 10002, true);


--
-- TOC entry 1898 (class 2606 OID 60915)
-- Name: do_class_pkey; Type: CONSTRAINT; Schema: todos; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY do_class
    ADD CONSTRAINT do_class_pkey PRIMARY KEY (dccode);


--
-- TOC entry 1900 (class 2606 OID 60917)
-- Name: do_do_pkey; Type: CONSTRAINT; Schema: todos; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_pkey PRIMARY KEY (doid, reldoid, drcode);


--
-- TOC entry 1896 (class 2606 OID 60919)
-- Name: do_pkey; Type: CONSTRAINT; Schema: todos; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_pkey PRIMARY KEY (doid);


--
-- TOC entry 1902 (class 2606 OID 60921)
-- Name: do_relation_pkey; Type: CONSTRAINT; Schema: todos; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY do_relation
    ADD CONSTRAINT do_relation_pkey PRIMARY KEY (drcode);


--
-- TOC entry 1907 (class 2620 OID 60922)
-- Name: do-after-insert; Type: TRIGGER; Schema: todos; Owner: postgres
--

CREATE TRIGGER "do-after-insert" AFTER INSERT ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_create_do_do_0();


--
-- TOC entry 1908 (class 2620 OID 60923)
-- Name: do-before-delete; Type: TRIGGER; Schema: todos; Owner: postgres
--

CREATE TRIGGER "do-before-delete" BEFORE DELETE ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_delete_do_dos();


--
-- TOC entry 1903 (class 2606 OID 60924)
-- Name: do_dccode_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: postgres
--

ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_dccode_fkey FOREIGN KEY (dccode) REFERENCES do_class(dccode);


--
-- TOC entry 1904 (class 2606 OID 60929)
-- Name: do_do_doid_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: postgres
--

ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_doid_fkey FOREIGN KEY (doid) REFERENCES "do"(doid);


--
-- TOC entry 1905 (class 2606 OID 60934)
-- Name: do_do_drcode_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: postgres
--

ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_drcode_fkey FOREIGN KEY (drcode) REFERENCES do_relation(drcode);


--
-- TOC entry 1906 (class 2606 OID 60939)
-- Name: do_do_reldoid_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: postgres
--

ALTER TABLE ONLY do_do
    ADD CONSTRAINT do_do_reldoid_fkey FOREIGN KEY (reldoid) REFERENCES "do"(doid);


-- Completed on 2016-12-18 22:34:02

--
-- PostgreSQL database dump complete
--

