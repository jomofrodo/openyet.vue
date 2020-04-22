#!/bin/bash
#  Use -x to strip ownership data

pg_dump -d covid19dev -U covid -h localhost -s -f covid_schema.sql
