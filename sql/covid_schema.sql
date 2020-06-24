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
    date date,
    sourcecode character varying(25),
    confd0 double precision,
    ppositived0 double precision,
    deathd0 double precision,
    confd1 double precision,
    ppositived1 double precision,
    deathd1 double precision,
    ppositive double precision
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
-- Name: COLUMN combined.confd0; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.confd0 IS 'confirmed delta (confirmed increase) current day';


--
-- Name: COLUMN combined.ppositived0; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.ppositived0 IS 'percent positive tests delta %';


--
-- Name: COLUMN combined.deathd0; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.deathd0 IS 'death delta %';


--
-- Name: COLUMN combined.confd1; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.confd1 IS 'conf delta % trend (daily)';


--
-- Name: COLUMN combined.ppositived1; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.ppositived1 IS 'perc positive delta % trend (daily)';


--
-- Name: COLUMN combined.deathd1; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.deathd1 IS 'death delta % trend';


--
-- Name: COLUMN combined.ppositive; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN combined.ppositive IS 'percent of tests that result in a positive';


--
-- Name: combined_bak; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE combined_bak (
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
    date date,
    sourcecode character varying(25)
);


ALTER TABLE covid.combined_bak OWNER TO covid;

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
-- Name: county; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE county (
    countrycode character varying(3),
    statecode character varying(5),
    county character varying(128) NOT NULL,
    population numeric(12,0),
    state character varying(127)
);


ALTER TABLE covid.county OWNER TO covid;

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
    date date
);


ALTER TABLE covid.ctp_statesdaily OWNER TO covid;

--
-- Name: state; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE state (
    name character varying(129) NOT NULL,
    status character varying(56),
    iso character varying(5),
    ansi character varying(2),
    usps character varying(10),
    uscg character varying(4),
    gpo character varying(22),
    ap character varying(22),
    other character varying(128),
    countrycode character varying(5) NOT NULL,
    population numeric(12,0)
);


ALTER TABLE covid.state OWNER TO covid;

--
-- Name: COLUMN state.countrycode; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN state.countrycode IS 'ansi country code';


--
-- Name: COLUMN state.population; Type: COMMENT; Schema: covid; Owner: covid
--

COMMENT ON COLUMN state.population IS 'Estimated 2019';


--
-- Name: cv_county_current_summary; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_county_current_summary AS
 SELECT DISTINCT combined.date,
    combined.county,
    combined.statecode,
    combined.state,
    combined.country,
    combined.countrycode,
    combined.confirmed,
    combined.confirmedincrease,
        CASE
            WHEN (combined.totaltestresults IS NULL) THEN NULL::numeric
            WHEN (combined.totaltestresults = (0)::numeric) THEN (0)::numeric
            WHEN (combined.totaltestresults > (0)::numeric) THEN (round((combined.positive / combined.totaltestresults), 2) * (100)::numeric)
            ELSE NULL::numeric
        END AS ppositive,
        CASE
            WHEN (combined.totaltestresultsincrease IS NULL) THEN NULL::numeric
            WHEN (combined.totaltestresultsincrease = (0)::numeric) THEN (0)::numeric
            WHEN (combined.totaltestresultsincrease > (0)::numeric) THEN (round((combined.positiveincrease / combined.totaltestresultsincrease), 2) * (100)::numeric)
            ELSE NULL::numeric
        END AS ppositiveincrease,
    combined.totaltestresults,
    combined.totaltestresultsincrease,
    combined.death,
    combined.deathincrease,
    round(((combined.death / county.population) * (1000)::numeric), 0) AS deaths_per_1k,
    round(((combined.confirmed / county.population) * (1000)::numeric), 0) AS confirmed_per_1k,
    round(((combined.totaltestresults / county.population) * (1000)::numeric), 0) AS tests_per_1k,
    county.population
   FROM (((combined
   JOIN country USING (countrycode))
   JOIN state ON (((combined.statecode)::text = (state.ansi)::text)))
   JOIN county USING (county, statecode))
  WHERE (((combined.state IS NOT NULL) AND (combined.county IS NOT NULL)) AND (combined.date = ( SELECT max(combined_1.date) AS max
   FROM combined combined_1)))
  ORDER BY combined.countrycode, combined.statecode, combined.county;


ALTER TABLE covid.cv_county_current_summary OWNER TO covid;

--
-- Name: cv_county_weekly; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_county_weekly AS
 SELECT combined.countrycode,
    combined.statecode,
    combined.county,
    date_trunc('week'::text, (combined.date)::timestamp with time zone) AS week,
    max(combined.confirmed) AS confirmed,
    sum(combined.confirmedincrease) AS confirmedincrease,
    max(combined.positive) AS positive,
    sum(combined.positiveincrease) AS positiveincrease,
    max(combined.negative) AS negative,
    sum(combined.negativeincrease) AS negativeincrease,
        CASE
            WHEN (sum(combined.positiveincrease) < (1)::numeric) THEN (0)::numeric
            ELSE (round((sum(combined.positiveincrease) / (sum(combined.positiveincrease) + sum(combined.negativeincrease))), 2) * (100)::numeric)
        END AS perc_positive,
    max(combined.death) AS death,
    sum(combined.deathincrease) AS deathincrease,
    count(combined.date) AS datect,
    combined.sourcecode
   FROM combined
  WHERE (((1 = 1) AND (combined.state IS NOT NULL)) AND (combined.county IS NOT NULL))
  GROUP BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone)
  ORDER BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone);


ALTER TABLE covid.cv_county_weekly OWNER TO covid;

--
-- Name: cv_county_weekly_delta; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_county_weekly_delta AS
 SELECT w3.countrycode,
    w3.statecode,
    w3.county,
    w4.confirmed AS conf_base,
    w4.death AS death_base,
    w4.perc_positive AS perc_positive_base,
    w1.confirmed AS conf_now,
    w1.death AS death_now,
    w1.perc_positive AS perc_positive_now,
    v_weeks.thisweek AS week0,
    w1.week AS week1,
    w2.week AS week2,
    w3.week AS week3,
        CASE
            WHEN (w0.datect = 7) THEN ((w0.confirmedincrease - w1.confirmedincrease))::numeric
            ELSE (round((((w0.confirmedincrease / w0.datect) * 7))::numeric, 0) - (w1.confirmedincrease)::numeric)
        END AS confd0,
    (w1.confirmedincrease - w2.confirmedincrease) AS confd1,
    (w2.confirmedincrease - w3.confirmedincrease) AS confd2,
    (w3.confirmedincrease - w4.confirmedincrease) AS confd3,
    (((w1.confirmedincrease)::numeric / w2.confirmed) - ((w2.confirmedincrease)::numeric / w3.confirmed)) AS confd1p,
    (((w2.confirmedincrease)::numeric / w3.confirmed) - ((w3.confirmedincrease)::numeric / w4.confirmed)) AS confd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.deathincrease - w1.deathincrease)
            ELSE (round(((w0.deathincrease / (w0.datect)::numeric) * (7)::numeric), 0) - w1.deathincrease)
        END AS deathd0,
    (w1.deathincrease - w2.deathincrease) AS deathd1,
    (w2.deathincrease - w3.deathincrease) AS deathd2,
    (w3.deathincrease - w4.deathincrease) AS deathd3,
    ((w1.deathincrease / w2.death) - (w2.deathincrease / w3.death)) AS deathd1p,
    ((w2.deathincrease / w3.death) - (w3.deathincrease / w4.death)) AS deathd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.perc_positive - w1.perc_positive)
            ELSE (round(((w0.perc_positive / (w0.datect)::numeric) * (7)::numeric), 0) - w1.perc_positive)
        END AS perc_positived0,
    (w1.perc_positive - w2.perc_positive) AS perc_positived1,
    (w2.perc_positive - w3.perc_positive) AS perc_positived2,
    (w3.perc_positive - w4.perc_positive) AS perc_positived3,
    v_weeks.thisweek,
    v_weeks.lastweek,
    v_weeks.twoweeks,
    COALESCE(w0.datect, (0)::bigint) AS datect
   FROM (((((( SELECT date_trunc('week'::text, now()) AS thisweek,
            date_trunc('week'::text, (now() - '7 days'::interval)) AS lastweek,
            date_trunc('week'::text, (now() - '14 days'::interval)) AS twoweeks,
            date_trunc('week'::text, (now() - '21 days'::interval)) AS threeweeks,
            date_trunc('week'::text, (now() - '28 days'::interval)) AS fourweeks) v_weeks
   LEFT JOIN cv_county_weekly w4 ON ((w4.week = v_weeks.fourweeks)))
   LEFT JOIN cv_county_weekly w3 ON (((((w3.week = v_weeks.threeweeks) AND ((w3.countrycode)::text = (w4.countrycode)::text)) AND ((w3.statecode)::text = (w4.statecode)::text)) AND ((w3.county)::text = (w4.county)::text))))
   LEFT JOIN cv_county_weekly w2 ON (((((w2.week = v_weeks.twoweeks) AND ((w2.countrycode)::text = (w3.countrycode)::text)) AND ((w2.statecode)::text = (w3.statecode)::text)) AND ((w2.county)::text = (w3.county)::text))))
   LEFT JOIN cv_county_weekly w1 ON (((((w1.week = v_weeks.lastweek) AND ((w1.countrycode)::text = (w3.countrycode)::text)) AND ((w1.statecode)::text = (w3.statecode)::text)) AND ((w1.county)::text = (w3.county)::text))))
   LEFT JOIN cv_county_weekly w0 ON (((((w0.week = v_weeks.thisweek) AND ((w0.countrycode)::text = (w3.countrycode)::text)) AND ((w0.statecode)::text = (w3.statecode)::text)) AND ((w0.county)::text = (w3.county)::text))))
  WHERE (1 = 1);


ALTER TABLE covid.cv_county_weekly_delta OWNER TO covid;

--
-- Name: cv_national_current_summary; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_national_current_summary AS
 SELECT combined.date,
    combined.country,
    combined.countrycode,
    country.region,
    combined.confirmed,
    combined.confirmedincrease,
    (round((combined.positive / (combined.negative + combined.positive)), 2) * (100)::numeric) AS ppositive,
    (round((combined.positiveincrease / (combined.positiveincrease + combined.negativeincrease)), 2) * (100)::numeric) AS ppositiveincrease,
    (combined.positive + combined.negative) AS totaltestresults,
    combined.totaltestresultsincrease,
    combined.death,
    combined.deathincrease,
    round(((combined.death * (1000)::numeric) / country.population), 0) AS deaths_per_1m,
    round(((combined.confirmed * (1000)::numeric) / country.population), 0) AS confirmed_per_1m,
    round(((combined.totaltestresults / country.population) * (1000)::numeric), 0) AS tests_per_1m,
    country.population
   FROM (combined
   JOIN country USING (countrycode))
  WHERE ((combined.state IS NULL) AND (combined.date = ( SELECT max(combined_1.date) AS max
      FROM combined combined_1)))
  ORDER BY combined.countrycode;


ALTER TABLE covid.cv_national_current_summary OWNER TO covid;

--
-- Name: cv_national_weekly; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_national_weekly AS
 SELECT combined.countrycode,
    combined.statecode,
    combined.county,
    date_trunc('week'::text, (combined.date)::timestamp with time zone) AS week,
    max(combined.confirmed) AS confirmed,
    sum(combined.confirmedincrease) AS confirmedincrease,
    max(combined.positive) AS positive,
    sum(combined.positiveincrease) AS positiveincrease,
    max(combined.negative) AS negative,
    sum(combined.negativeincrease) AS negativeincrease,
        CASE
            WHEN (sum(combined.positiveincrease) < (1)::numeric) THEN (0)::numeric
            ELSE (round((sum(combined.positiveincrease) / (sum(combined.positiveincrease) + sum(combined.negativeincrease))), 2) * (100)::numeric)
        END AS perc_positive,
    max(combined.death) AS death,
    sum(combined.deathincrease) AS deathincrease,
    count(combined.date) AS datect,
    combined.sourcecode
   FROM combined
  WHERE (((1 = 1) AND (combined.county IS NULL)) AND (combined.state IS NULL))
  GROUP BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone)
  ORDER BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone);


ALTER TABLE covid.cv_national_weekly OWNER TO covid;

--
-- Name: cv_national_weekly_delta; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_national_weekly_delta AS
 SELECT w3.countrycode,
    w3.statecode,
    w3.county,
    w4.confirmed AS conf_base,
    w4.death AS death_base,
    w4.perc_positive AS perc_positive_base,
    w1.confirmed AS conf_now,
    w1.death AS death_now,
    w1.perc_positive AS perc_positive_now,
    v_weeks.thisweek AS week0,
    w1.week AS week1,
    w2.week AS week2,
    w3.week AS week3,
        CASE
            WHEN (w0.datect = 7) THEN ((w0.confirmedincrease - w1.confirmedincrease))::numeric
            ELSE (round((((w0.confirmedincrease / w0.datect) * 7))::numeric, 0) - (w1.confirmedincrease)::numeric)
        END AS confd0,
    (w1.confirmedincrease - w2.confirmedincrease) AS confd1,
    (w2.confirmedincrease - w3.confirmedincrease) AS confd2,
    (w3.confirmedincrease - w4.confirmedincrease) AS confd3,
    ((w1.confirmedincrease)::numeric / w2.confirmed) AS confd1rate,
    ((w2.confirmedincrease)::numeric / w3.confirmed) AS confd2rate,
    ((w3.confirmedincrease)::numeric / w4.confirmed) AS confd3rate,
    (((w1.confirmedincrease)::numeric / w2.confirmed) - ((w2.confirmedincrease)::numeric / w3.confirmed)) AS confd1p,
    (((w2.confirmedincrease)::numeric / w3.confirmed) - ((w3.confirmedincrease)::numeric / w4.confirmed)) AS confd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.deathincrease - w1.deathincrease)
            ELSE (round(((w0.deathincrease / (w0.datect)::numeric) * (7)::numeric), 0) - w1.deathincrease)
        END AS deathd0,
    (w1.deathincrease - w2.deathincrease) AS deathd1,
    (w2.deathincrease - w3.deathincrease) AS deathd2,
    (w3.deathincrease - w4.deathincrease) AS deathd3,
    (w1.deathincrease / w2.death) AS deathd1rate,
    (w2.deathincrease / w3.death) AS deathd2rate,
    (w3.deathincrease / w4.death) AS deathd3rate,
    ((w1.deathincrease / w2.death) - (w2.deathincrease / w3.death)) AS deathd1p,
    ((w2.deathincrease / w3.death) - (w3.deathincrease / w4.death)) AS deathd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.perc_positive - w1.perc_positive)
            ELSE (round(((w0.perc_positive / (w0.datect)::numeric) * (7)::numeric), 0) - w1.perc_positive)
        END AS perc_positived0,
    (w1.perc_positive - w2.perc_positive) AS perc_positived1,
    (w2.perc_positive - w3.perc_positive) AS perc_positived2,
    (w3.perc_positive - w4.perc_positive) AS perc_positived3,
    v_weeks.thisweek,
    v_weeks.lastweek,
    v_weeks.twoweeks,
    COALESCE(w0.datect, (0)::bigint) AS datect
   FROM (((((( SELECT date_trunc('week'::text, now()) AS thisweek,
            date_trunc('week'::text, (now() - '7 days'::interval)) AS lastweek,
            date_trunc('week'::text, (now() - '14 days'::interval)) AS twoweeks,
            date_trunc('week'::text, (now() - '21 days'::interval)) AS threeweeks,
            date_trunc('week'::text, (now() - '28 days'::interval)) AS fourweeks) v_weeks
   LEFT JOIN cv_national_weekly w4 ON ((w4.week = v_weeks.fourweeks)))
   LEFT JOIN cv_national_weekly w3 ON (((w3.week = v_weeks.threeweeks) AND ((w3.countrycode)::text = (w4.countrycode)::text))))
   LEFT JOIN cv_national_weekly w2 ON (((w2.week = v_weeks.twoweeks) AND ((w2.countrycode)::text = (w3.countrycode)::text))))
   LEFT JOIN cv_national_weekly w1 ON (((w1.week = v_weeks.lastweek) AND ((w1.countrycode)::text = (w3.countrycode)::text))))
   LEFT JOIN cv_national_weekly w0 ON (((w0.week = v_weeks.thisweek) AND ((w0.countrycode)::text = (w3.countrycode)::text))))
  WHERE (1 = 1);


ALTER TABLE covid.cv_national_weekly_delta OWNER TO covid;

--
-- Name: cv_state_current_summary; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_state_current_summary AS
 SELECT DISTINCT combined.date,
    combined.statecode,
    combined.state,
    combined.country,
    combined.countrycode,
    combined.confirmed,
    combined.confirmedincrease,
        CASE
            WHEN (combined.totaltestresults IS NULL) THEN NULL::numeric
            WHEN (combined.totaltestresults = (0)::numeric) THEN (0)::numeric
            WHEN (combined.totaltestresults > (0)::numeric) THEN (round((combined.positive / combined.totaltestresults), 2) * (100)::numeric)
            ELSE NULL::numeric
        END AS ppositive,
        CASE
            WHEN (combined.totaltestresultsincrease IS NULL) THEN NULL::numeric
            WHEN (combined.totaltestresultsincrease = (0)::numeric) THEN (0)::numeric
            WHEN (combined.totaltestresultsincrease > (0)::numeric) THEN (round((combined.positiveincrease / combined.totaltestresultsincrease), 2) * (100)::numeric)
            ELSE NULL::numeric
        END AS ppositiveincrease,
    combined.totaltestresults,
    combined.totaltestresultsincrease,
    combined.death,
    combined.deathincrease,
    round(((combined.death * (1000000)::numeric) / state.population), 0) AS deaths_per_1m,
    round(((combined.confirmed * (1000000)::numeric) / state.population), 0) AS confirmed_per_1m,
    round(((combined.totaltestresults / state.population) * (1000000)::numeric), 0) AS tests_per_1m,
    state.population
   FROM ((combined
   JOIN country USING (countrycode))
   JOIN state ON ((((combined.statecode)::text = (state.ansi)::text) AND ((combined.countrycode)::text = (state.countrycode)::text))))
  WHERE (((combined.state IS NOT NULL) AND (combined.county IS NULL)) AND (combined.date = ( SELECT max(combined_1.date) AS max
   FROM combined combined_1)))
  ORDER BY combined.countrycode, combined.statecode;


ALTER TABLE covid.cv_state_current_summary OWNER TO covid;

--
-- Name: cv_state_weekly; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_state_weekly AS
 SELECT combined.countrycode,
    combined.statecode,
    combined.county,
    date_trunc('week'::text, (combined.date)::timestamp with time zone) AS week,
    max(combined.confirmed) AS confirmed,
    sum(combined.confirmedincrease) AS confirmedincrease,
    max(combined.positive) AS positive,
    sum(combined.positiveincrease) AS positiveincrease,
    max(combined.negative) AS negative,
    sum(combined.negativeincrease) AS negativeincrease,
        CASE
            WHEN (sum(combined.positiveincrease) < (1)::numeric) THEN (0)::numeric
            ELSE (round((sum(combined.positiveincrease) / (sum(combined.positiveincrease) + sum(combined.negativeincrease))), 2) * (100)::numeric)
        END AS perc_positive,
    max(combined.death) AS death,
    sum(combined.deathincrease) AS deathincrease,
    count(combined.date) AS datect,
    combined.sourcecode
   FROM combined
  WHERE (((1 = 1) AND (combined.state IS NOT NULL)) AND (combined.county IS NULL))
  GROUP BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone)
  ORDER BY combined.countrycode, combined.statecode, combined.county, combined.sourcecode, date_trunc('week'::text, (combined.date)::timestamp with time zone);


ALTER TABLE covid.cv_state_weekly OWNER TO covid;

--
-- Name: cv_state_weekly_delta; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW cv_state_weekly_delta AS
 SELECT w3.countrycode,
    w3.statecode,
    w3.county,
    w4.confirmed AS conf_base,
    w4.death AS death_base,
    w4.perc_positive AS perc_positive_base,
    w1.confirmed AS conf_now,
    w1.death AS death_now,
    w1.perc_positive AS perc_positive_now,
    v_weeks.thisweek AS week0,
    w1.week AS week1,
    w2.week AS week2,
    w3.week AS week3,
        CASE
            WHEN (w0.datect = 7) THEN ((w0.confirmedincrease - w1.confirmedincrease))::numeric
            ELSE (round((((w0.confirmedincrease / w0.datect) * 7))::numeric, 0) - (w1.confirmedincrease)::numeric)
        END AS confd0,
    (w1.confirmedincrease - w2.confirmedincrease) AS confd1,
    (w2.confirmedincrease - w3.confirmedincrease) AS confd2,
    (w3.confirmedincrease - w4.confirmedincrease) AS confd3,
    (((w1.confirmedincrease)::numeric / w2.confirmed) - ((w2.confirmedincrease)::numeric / w3.confirmed)) AS confd1p,
    (((w2.confirmedincrease)::numeric / w3.confirmed) - ((w3.confirmedincrease)::numeric / w4.confirmed)) AS confd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.deathincrease - w1.deathincrease)
            ELSE (round(((w0.deathincrease / (w0.datect)::numeric) * (7)::numeric), 0) - w1.deathincrease)
        END AS deathd0,
    (w1.deathincrease - w2.deathincrease) AS deathd1,
    (w2.deathincrease - w3.deathincrease) AS deathd2,
    (w3.deathincrease - w4.deathincrease) AS deathd3,
    ((w1.deathincrease / w2.death) - (w2.deathincrease / w3.death)) AS deathd1p,
    ((w2.deathincrease / w3.death) - (w3.deathincrease / w4.death)) AS deathd2p,
        CASE
            WHEN (w0.datect = 7) THEN (w0.perc_positive - w1.perc_positive)
            ELSE (round(((w0.perc_positive / (w0.datect)::numeric) * (7)::numeric), 0) - w1.perc_positive)
        END AS perc_positived0,
    (w1.perc_positive - w2.perc_positive) AS perc_positived1,
    (w2.perc_positive - w3.perc_positive) AS perc_positived2,
    (w3.perc_positive - w4.perc_positive) AS perc_positived3,
    v_weeks.thisweek,
    v_weeks.lastweek,
    v_weeks.twoweeks,
    COALESCE(w0.datect, (0)::bigint) AS datect
   FROM (((((( SELECT date_trunc('week'::text, now()) AS thisweek,
            date_trunc('week'::text, (now() - '7 days'::interval)) AS lastweek,
            date_trunc('week'::text, (now() - '14 days'::interval)) AS twoweeks,
            date_trunc('week'::text, (now() - '21 days'::interval)) AS threeweeks,
            date_trunc('week'::text, (now() - '28 days'::interval)) AS fourweeks) v_weeks
   LEFT JOIN cv_state_weekly w4 ON ((w4.week = v_weeks.fourweeks)))
   LEFT JOIN cv_state_weekly w3 ON ((((w3.week = v_weeks.threeweeks) AND ((w3.countrycode)::text = (w4.countrycode)::text)) AND ((w3.statecode)::text = (w4.statecode)::text))))
   LEFT JOIN cv_state_weekly w2 ON ((((w2.week = v_weeks.twoweeks) AND ((w2.countrycode)::text = (w3.countrycode)::text)) AND ((w2.statecode)::text = (w3.statecode)::text))))
   LEFT JOIN cv_state_weekly w1 ON ((((w1.week = v_weeks.lastweek) AND ((w1.countrycode)::text = (w3.countrycode)::text)) AND ((w1.statecode)::text = (w3.statecode)::text))))
   LEFT JOIN cv_state_weekly w0 ON ((((w0.week = v_weeks.thisweek) AND ((w0.countrycode)::text = (w3.countrycode)::text)) AND ((w0.statecode)::text = (w3.statecode)::text))))
  WHERE (1 = 1);


ALTER TABLE covid.cv_state_weekly_delta OWNER TO covid;

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
-- Name: oyv_open_yet; Type: VIEW; Schema: covid; Owner: covid
--

CREATE VIEW oyv_open_yet AS
 SELECT vc.date,
    vc.countrycode,
    vc.statecode,
    vc.state,
    vc.county,
    vc.confirmed,
    vc.death,
    vc.totaltestresults,
    vc.ppositive,
    vc.confd0,
    vc.confd1,
    vc.ppositived0,
    vc.ppositived1,
    vc.deathd0,
    vc.deathd1,
    vc.country_population,
    vc.population,
    round((vc.death / (vc.population / (1000000)::numeric))) AS deaths_1m,
    round((vc.confirmed / (vc.population / (1000000)::numeric))) AS confirmed_1m,
    round((vc.totaltestresults / (vc.population / (1000000)::numeric))) AS tests_1m
   FROM ( SELECT c.date,
            c.countrycode,
            c.statecode,
            c.state,
            c.county,
            c.confirmed,
            c.death,
            c.totaltestresults,
            round(((c.ppositive * (100)::double precision))::numeric, 2) AS ppositive,
            c.confd0,
            c.confd1,
            round(((c.ppositived0 * (100)::double precision))::numeric, 2) AS ppositived0,
            round(((c.ppositived1 * (100)::double precision))::numeric, 2) AS ppositived1,
            c.deathd0,
            c.deathd1,
            (country.population * (1000)::numeric) AS country_population,
                CASE
                    WHEN (county.population IS NOT NULL) THEN county.population
                    WHEN (state.population IS NOT NULL) THEN state.population
                    WHEN (country.population IS NOT NULL) THEN (country.population * (1000)::numeric)
                    ELSE NULL::numeric
                END AS population
           FROM (((combined c
      JOIN country USING (countrycode))
   LEFT JOIN state ON ((((c.statecode)::text = (state.ansi)::text) AND ((state.countrycode)::text = (c.countrycode)::text))))
   LEFT JOIN county ON ((((county.county)::text = (c.county)::text) AND ((county.statecode)::text = (c.statecode)::text))))) vc;


ALTER TABLE covid.oyv_open_yet OWNER TO covid;

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
-- Name: tt_increase; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE tt_increase (
    countrycode character varying(3),
    state character varying(128),
    county character varying(128),
    date date,
    confirmed numeric(12,0),
    ppositive double precision,
    death numeric(12,0),
    confv1 numeric(12,0),
    deathv1 numeric(12,0),
    positivev1 numeric(12,0),
    ppositivev1 double precision,
    totaltestsv1 numeric(12,0),
    confv2 numeric(12,0),
    deathv2 numeric(12,0),
    positivev2 numeric(12,0),
    ppositivev2 double precision,
    totaltestsv2 numeric(12,0),
    confd0 numeric,
    deathd0 numeric,
    positived0 numeric,
    ppositived0 double precision,
    confd1 numeric,
    deathd1 numeric,
    positived1 numeric,
    ppositived1 double precision
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

--
-- Name: uspopulation; Type: TABLE; Schema: covid; Owner: covid; Tablespace: 
--

CREATE TABLE uspopulation (
    sumlev numeric(2,0),
    region numeric(1,0),
    division numeric(2,0),
    state numeric(2,0),
    statename character varying(40),
    est2019 numeric(12,0),
    est2018 numeric(12,0),
    perc2018 double precision
);
ALTER TABLE ONLY uspopulation ALTER COLUMN sumlev SET STATISTICS 0;
ALTER TABLE ONLY uspopulation ALTER COLUMN region SET STATISTICS 0;
ALTER TABLE ONLY uspopulation ALTER COLUMN division SET STATISTICS 0;


ALTER TABLE covid.uspopulation OWNER TO covid;

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
    ADD CONSTRAINT ctp_statesdaily_pkey PRIMARY KEY (state, date);


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
-- Name: state_idx; Type: CONSTRAINT; Schema: covid; Owner: covid; Tablespace: 
--

ALTER TABLE ONLY state
    ADD CONSTRAINT state_idx PRIMARY KEY (name, countrycode);


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
-- Name: combined_idx5; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx5 ON combined USING btree (positive);


--
-- Name: combined_idx6; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX combined_idx6 ON combined USING btree (totaltestresults);


--
-- Name: county_idx; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX county_idx ON county USING btree (statecode);


--
-- Name: county_idx1; Type: INDEX; Schema: covid; Owner: covid; Tablespace: 
--

CREATE INDEX county_idx1 ON county USING btree (county);


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

