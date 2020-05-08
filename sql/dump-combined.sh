#!/bin/bash
#  Use -x to strip ownership data
# Use -s to only export schema

pg_dump -t country -t state -t county -t datasrc -t uspopulation -t combined -d covid19dev -U covid -h localhost  -f covid_db_combined.sql

tar -czf covid_db_combined.tgz covid_db_combined.sql

rm covid_db_combined.sql
