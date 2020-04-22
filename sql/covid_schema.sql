--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: covid; Type: SCHEMA; Schema: -; Owner: covid
--

CREATE SCHEMA covid;


ALTER SCHEMA covid OWNER TO covid;

--
-- Name: todos; Type: SCHEMA; Schema: -; Owner: covid
--

CREATE SCHEMA todos;


ALTER SCHEMA todos OWNER TO covid;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = todos, pg_catalog;

--
-- Name: tf_create_do(); Type: FUNCTION; Schema: todos; Owner: covid
--

CREATE FUNCTION tf_create_do() RETURNS trigger
    LANGUAGE plpgsql
    AS $$    DECLARE        -- local variables    BEGIN        -- id and timestamp new records        IF NEW.doID IS NULL        THEN NEW.doID := nextval('todos.seqdo');        END IF;        RETURN NEW;    END;$$;


ALTER FUNCTION todos.tf_create_do() OWNER TO covid;

--
-- Name: trf_create_do_do_0(); Type: FUNCTION; Schema: todos; Owner: covid
--

CREATE FUNCTION trf_create_do_do_0() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN  INSERT INTO todos.do_do (         doID,        relDoID,        drCode,        ddSort     )     VALUES (         new.doID,        1,        'CHILD',        new.doID     );    RETURN NEW;END;$$;


ALTER FUNCTION todos.trf_create_do_do_0() OWNER TO covid;

--
-- Name: trf_create_post_do(); Type: FUNCTION; Schema: todos; Owner: covid
--

CREATE FUNCTION trf_create_post_do() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN    INSERT INTO todos.do (         dorecid,        dorecuuid,        dccode     )     SELECT              NEW.id,           NEW.uuid,           'DO'      FROM todos.do;    RETURN NEW;END;$$;


ALTER FUNCTION todos.trf_create_post_do() OWNER TO covid;

--
-- Name: trf_delete_do_dos(); Type: FUNCTION; Schema: todos; Owner: covid
--

CREATE FUNCTION trf_delete_do_dos() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN    DELETE      FROM todos.do_do     WHERE reldoid = old.doid;    DELETE      FROM todos.do_do     WHERE doid = old.doid;         RETURN old;END;$$;


ALTER FUNCTION todos.trf_delete_do_dos() OWNER TO covid;

SET search_path = covid, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: combined; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE combined (
    statecode character varying(5),
    state character varying(128),
    countrycode character varying(3),
    country character varying(50),
    county character varying(128),
    confirmed numeric(12,0),
    confirmedincrease integer,
    positive numeric(12,0),
    positiveincrease numeric(12,0),
    negative numeric(12,0),
    negativeincrease numeric(12,0),
    totaltestresults numeric(12,0),
    totaltestresultsincrease numeric(12,0),
    hospitalized numeric(12,0),
    hospitalizedincrease numeric(12,0),
    death numeric(12,0),
    deathincrease numeric(12,0),
    recovered numeric(12,0),
    recoveredincrease numeric(12,0),
    datechecked character varying(40),
    date date,
    sourcecode character varying(25)
);


ALTER TABLE covid.combined OWNER TO covid;

--
-- Name: COLUMN combined.confirmed; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.confirmed IS 'confirmed cases';


--
-- Name: COLUMN combined.positive; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.positive IS 'positive test results';


--
-- Name: country; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE country (
    name character varying(128),
    countrycode character varying(3),
    region character varying(128),
    incomelevel character varying(128),
    ctp_name character varying(128),
    "numeric" numeric(12,0),
    population numeric(12,0)
);


ALTER TABLE covid.country OWNER TO covid;

--
-- Name: COLUMN country.ctp_name; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN country.ctp_name IS 'Name of country as used by Covid Tracking Project';


--
-- Name: COLUMN country."numeric"; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN country."numeric" IS 'ISO numeric code';


--
-- Name: COLUMN country.population; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN country.population IS '2020 UN, in thousands';


--
-- Name: ctp_statesdaily; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE ctp_statesdaily (
    state character varying(128) NOT NULL,
    country character varying(50) DEFAULT 'USA'::character varying NOT NULL,
    positive numeric(12,0),
    positiveincrease numeric(12,0),
    negative numeric(12,0),
    negativeincrease numeric(12,0),
    pending numeric(12,0),
    totaltestresults numeric(12,0),
    totaltestresultsincrease numeric(12,0),
    hospitalized numeric(12,0),
    hospitalizedincrease numeric(12,0),
    death numeric(12,0),
    deathincrease numeric(12,0),
    datechecked character varying(40) NOT NULL
);


ALTER TABLE covid.ctp_statesdaily OWNER TO covid;

--
-- Name: datasrc; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE datasrc (
    datasrccode character varying(20) NOT NULL,
    dsrcname character varying(128),
    dsrcurl character varying(516),
    sourcecode character varying(20)
);
ALTER TABLE ONLY datasrc ALTER COLUMN datasrccode SET STATISTICS 0;
ALTER TABLE ONLY datasrc ALTER COLUMN dsrcname SET STATISTICS 0;
ALTER TABLE ONLY datasrc ALTER COLUMN dsrcurl SET STATISTICS 0;
ALTER TABLE ONLY datasrc ALTER COLUMN sourcecode SET STATISTICS 0;


ALTER TABLE covid.datasrc OWNER TO covid;

--
-- Name: COLUMN datasrc.sourcecode; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN datasrc.sourcecode IS 'Code of the organization behind the source, e.g., JH';


--
-- Name: jh_timeseries; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE jh_timeseries (
    tsid integer NOT NULL,
    state character varying(128) DEFAULT NULL::character varying,
    country character varying(128) NOT NULL,
    ct numeric(12,0) NOT NULL,
    type character varying(5) NOT NULL,
    date character varying(30) NOT NULL
);


ALTER TABLE covid.jh_timeseries OWNER TO covid;

--
-- Name: jh_timeseries_tsid_seq; Type: SEQUENCE; Schema: covid; Owner: covid
--

CREATE SEQUENCE jh_timeseries_tsid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE covid.jh_timeseries_tsid_seq OWNER TO covid;

--
-- Name: jh_timeseries_tsid_seq; Type: SEQUENCE OWNED BY; Schema: covid; Owner: covid
--

ALTER SEQUENCE jh_timeseries_tsid_seq OWNED BY jh_timeseries.tsid;


--
-- Name: jh_us_timeseries; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE jh_us_timeseries (
    tsid integer NOT NULL,
    uid numeric(12,0) NOT NULL,
    iso3 character varying(3),
    fips double precision,
    county character varying(56),
    state character varying(128),
    country character varying(128),
    lat double precision,
    lon double precision,
    population numeric(12,0),
    date character varying(30),
    ct numeric(12,0),
    type character varying(5)
);


ALTER TABLE covid.jh_us_timeseries OWNER TO covid;

--
-- Name: jh_us_timeseries_tsid_seq; Type: SEQUENCE; Schema: covid; Owner: covid
--

CREATE SEQUENCE jh_us_timeseries_tsid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE covid.jh_us_timeseries_tsid_seq OWNER TO covid;

--
-- Name: jh_us_timeseries_tsid_seq; Type: SEQUENCE OWNED BY; Schema: covid; Owner: covid
--

ALTER SEQUENCE jh_us_timeseries_tsid_seq OWNED BY jh_us_timeseries.tsid;


--
-- Name: seqmisc; Type: SEQUENCE; Schema: covid; Owner: covid
--

CREATE SEQUENCE seqmisc
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE covid.seqmisc OWNER TO covid;

--
-- Name: seqtodo; Type: SEQUENCE; Schema: covid; Owner: covid
--

CREATE SEQUENCE seqtodo
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE covid.seqtodo OWNER TO covid;

--
-- Name: state; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE state (
    name character varying(129),
    status character varying(56),
    iso character varying(5) NOT NULL,
    ansi character varying(2),
    usps character varying(10),
    uscg character varying(4),
    gpo character varying(22),
    ap character varying(22),
    other character varying(128),
    countrycode character varying(5)
);


ALTER TABLE covid.state OWNER TO covid;

--
-- Name: COLUMN state.countrycode; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN state.countrycode IS 'ansi country code';


--
-- Name: tt_increase; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE tt_increase (
    countrycode character varying(3),
    state character varying(128),
    county character varying(128),
    date date,
    confirmed numeric(12,0),
    confd1 numeric(12,0),
    confirmedincrease numeric,
    deathincrease numeric,
    positiveincrease numeric,
    negativeincrease numeric
);


ALTER TABLE covid.tt_increase OWNER TO covid;

--
-- Name: tt_national_increase; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW tt_national_increase AS
 SELECT combined.countrycode,
    combined.state,
    combined.county,
    combined.date,
    combined.confirmed,
    c2.confirmed AS confd1,
    (combined.confirmed - c2.confirmed) AS confirmedincrease,
    (combined.death - c2.death) AS deathincrease,
    (combined.positive - c2.positive) AS positiveincrease,
    (combined.negative - c2.negative) AS negativeincrease
   FROM (combined
   JOIN combined c2 USING (countrycode))
  WHERE ((c2.date = (combined.date - '1 day'::interval)) AND ((c2.state IS NULL) AND (combined.state IS NULL)));


ALTER TABLE covid.tt_national_increase OWNER TO covid;

SET search_path = todos, pg_catalog;

--
-- Name: do; Type: TABLE; Schema: todos; Owner: covid; Tablespace: 
--

CREATE TABLE "do" (
    doid numeric(12,0) NOT NULL,
    dccode character varying(10) DEFAULT 'DO'::character varying,
    dorecid numeric(12,0),
    dorecuuid character varying(36)
);


ALTER TABLE todos."do" OWNER TO covid;

--
-- Name: do_class; Type: TABLE; Schema: todos; Owner: covid; Tablespace: 
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


ALTER TABLE todos.do_class OWNER TO covid;

--
-- Name: do_net; Type: TABLE; Schema: todos; Owner: covid; Tablespace: 
--

CREATE TABLE do_net (
    doid numeric(12,0) NOT NULL,
    reldoid numeric(12,0) NOT NULL,
    drcode character varying(10) DEFAULT 'PARENT'::character varying NOT NULL,
    ddsort numeric(12,0),
    ddleft numeric(12,0),
    ddright numeric(12,0)
);


ALTER TABLE todos.do_net OWNER TO covid;

--
-- Name: TABLE do_net; Type: COMMENT; Schema: todos; Owner: covid
--

COMMENT ON TABLE do_net IS 'Relationship net of dos to dos';


--
-- Name: do_relation; Type: TABLE; Schema: todos; Owner: covid; Tablespace: 
--

CREATE TABLE do_relation (
    drcode character varying(10) NOT NULL,
    drdesc character varying(128)
);


ALTER TABLE todos.do_relation OWNER TO covid;

--
-- Name: seqdo; Type: SEQUENCE; Schema: todos; Owner: covid
--

CREATE SEQUENCE seqdo
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE todos.seqdo OWNER TO covid;

SET search_path = covid, pg_catalog;

--
-- Name: tsid; Type: DEFAULT; Schema: covid; Owner: covid
--

ALTER TABLE ONLY jh_timeseries ALTER COLUMN tsid SET DEFAULT nextval('jh_timeseries_tsid_seq'::regclass);


--
-- Name: tsid; Type: DEFAULT; Schema: covid; Owner: covid
--

ALTER TABLE ONLY jh_us_timeseries ALTER COLUMN tsid SET DEFAULT nextval('jh_us_timeseries_tsid_seq'::regclass);


--
-- Name: ctp_statesdaily_pkey; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY ctp_statesdaily
    ADD CONSTRAINT ctp_statesdaily_pkey PRIMARY KEY (state, datechecked);


--
-- Name: datasrc_pkey; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY datasrc
    ADD CONSTRAINT datasrc_pkey PRIMARY KEY (datasrccode);


--
-- Name: jh_timeseries_idx; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY jh_timeseries
    ADD CONSTRAINT jh_timeseries_idx UNIQUE (country, state, type, date);


--
-- Name: jh_timeseries_pkey; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY jh_timeseries
    ADD CONSTRAINT jh_timeseries_pkey PRIMARY KEY (tsid);


--
-- Name: jh_us_timeseries_pkey; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY jh_us_timeseries
    ADD CONSTRAINT jh_us_timeseries_pkey PRIMARY KEY (tsid);


--
-- Name: state_pkey; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY state
    ADD CONSTRAINT state_pkey PRIMARY KEY (iso);


SET search_path = todos, pg_catalog;

--
-- Name: do_class_pkey; Type: CONSTRAINT; Schema: todos; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY do_class
    ADD CONSTRAINT do_class_pkey PRIMARY KEY (dccode);


--
-- Name: do_do_pkey; Type: CONSTRAINT; Schema: todos; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY do_net
    ADD CONSTRAINT do_do_pkey PRIMARY KEY (doid, reldoid, drcode);


--
-- Name: do_pkey; Type: CONSTRAINT; Schema: todos; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_pkey PRIMARY KEY (doid);


--
-- Name: do_relation_pkey; Type: CONSTRAINT; Schema: todos; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY do_relation
    ADD CONSTRAINT do_relation_pkey PRIMARY KEY (drcode);


SET search_path = covid, pg_catalog;

--
-- Name: combined_idx; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx ON combined USING btree (country);


--
-- Name: combined_idx1; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx1 ON combined USING btree (date);


--
-- Name: combined_idx2; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx2 ON combined USING btree (state, statecode);


--
-- Name: combined_idx3; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx3 ON combined USING btree (county);


--
-- Name: combined_idx4; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE UNIQUE INDEX combined_idx4 ON combined USING btree (state, date, county, sourcecode, country);


--
-- Name: jh_timeseries_idx_state; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX jh_timeseries_idx_state ON jh_timeseries USING btree (state);


--
-- Name: jh_us_timeseries_idx; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX jh_us_timeseries_idx ON jh_us_timeseries USING btree (date);


--
-- Name: jh_us_timeseries_idx1; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX jh_us_timeseries_idx1 ON jh_us_timeseries USING btree (country);


--
-- Name: jh_us_timeseries_idx2; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX jh_us_timeseries_idx2 ON jh_us_timeseries USING btree (county);


--
-- Name: jh_us_timeseries_idx3; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE UNIQUE INDEX jh_us_timeseries_idx3 ON jh_us_timeseries USING btree (county, state, date, type);


--
-- Name: jh_us_timeseries_idx_type; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX jh_us_timeseries_idx_type ON jh_us_timeseries USING btree (type);


--
-- Name: tt_increase_idx; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX tt_increase_idx ON tt_increase USING btree (countrycode);


--
-- Name: tt_increase_idx1; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX tt_increase_idx1 ON tt_increase USING btree (state);


--
-- Name: tt_increase_idx2; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX tt_increase_idx2 ON tt_increase USING btree (county);


--
-- Name: tt_increase_idx3; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX tt_increase_idx3 ON tt_increase USING btree (date);


--
-- Name: tt_increase_idx4; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE UNIQUE INDEX tt_increase_idx4 ON tt_increase USING btree (date, county, state, countrycode);


SET search_path = todos, pg_catalog;

--
-- Name: do_tr; Type: TRIGGER; Schema: todos; Owner: covid
--

CREATE TRIGGER do_tr BEFORE INSERT ON "do" FOR EACH ROW EXECUTE PROCEDURE tf_create_do();


--
-- Name: tr_do_after_delete; Type: TRIGGER; Schema: todos; Owner: covid
--

CREATE TRIGGER tr_do_after_delete AFTER DELETE ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_delete_do_dos();


--
-- Name: tr_do_after_insert; Type: TRIGGER; Schema: todos; Owner: covid
--

CREATE TRIGGER tr_do_after_insert AFTER INSERT ON "do" FOR EACH ROW EXECUTE PROCEDURE trf_create_do_do_0();


--
-- Name: do_dccode_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: covid
--

ALTER TABLE ONLY "do"
    ADD CONSTRAINT do_dccode_fkey FOREIGN KEY (dccode) REFERENCES do_class(dccode);


--
-- Name: do_do_doid_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: covid
--

ALTER TABLE ONLY do_net
    ADD CONSTRAINT do_do_doid_fkey FOREIGN KEY (doid) REFERENCES "do"(doid);


--
-- Name: do_do_drcode_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: covid
--

ALTER TABLE ONLY do_net
    ADD CONSTRAINT do_do_drcode_fkey FOREIGN KEY (drcode) REFERENCES do_relation(drcode);


--
-- Name: do_do_reldoid_fkey; Type: FK CONSTRAINT; Schema: todos; Owner: covid
--

ALTER TABLE ONLY do_net
    ADD CONSTRAINT do_do_reldoid_fkey FOREIGN KEY (reldoid) REFERENCES "do"(doid);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

