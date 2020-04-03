#!/bin/sh 

cat $1 | psql -U kanban kanbandev
