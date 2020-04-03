#!/bin/bash 

if [ ! $1 ];
then 
  echo "Usage: dump_it <output_file>"
  echo "output_file will be in sql format"
  echo "e.g., dump_it.sh kanban.sql"
  exit;
fi

pg_dump -d kanbandev -U kanban > $1
