#!/bin/bash
#  Use -x to strip ownership data
# Use -s to only export schema

pg_dump -d covid19dev -U covid -h localhost  -f covid_db.sql

tar -czf covid_db.tgz covid_db.sql

rm covid_db.sql
