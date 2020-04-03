#!/bin/sh 

cat $1 | psql -U covid covid19dev
