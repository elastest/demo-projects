Info
=====

Webapp example using [ELK stack](https://www.elastic.co/elk-stack) to save the application logs and Jenkins Logstash plugin to save the Job logs

Steps
=====

1. Start [ELK stack](https://www.elastic.co/elk-stack) (Elasticsearch, Logstash and Kibana) running `docker-compose -f elk.yml up` command.
	- **Elasticsearch** will be available at `9200` port (both internally and binded)
	- **Logstash** will be available at `5000` tcp port to send logs (both internally and binded). All logs will be sent to the same Elasticsearch index (`webapp`).
	- **Kibana** will be available at `5601` port (both internally and binded)
2. Install and configure Logstash plugin in your Jenkins (insert Elasticsearch uri, like http://172.18.0.1:9200/MYINDEX/_doc)
3. Create a Jenkins pipeline with the code of **Jenkinsfile**
4. Run Job

