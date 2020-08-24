#!/bin/sh
cd /usr/local/webapps/openyet/openyet.vue
echo Starting data retrieval task
java -cp "www/WEB-INF/lib/*" com.netazoic.covid.task.RetrieveDataTask >> logs/RetrieveData.log 
echo all done
