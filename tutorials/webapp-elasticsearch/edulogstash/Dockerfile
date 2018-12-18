FROM docker.elastic.co/logstash/logstash:6.5.3

COPY logstash.conf /usr/share/logstash/pipeline/logstash.conf
COPY logstash.yml /usr/share/logstash/config/logstash.yml


CMD cd /usr/share/logstash/pipeline/; sed -i 's/ESHOST/'"$ESHOST"'/g' logstash.conf; sed -i 's/ESINDEX/'"$ESINDEX"'/g' logstash.conf; exec logstash -f logstash.conf --log.level=error;
