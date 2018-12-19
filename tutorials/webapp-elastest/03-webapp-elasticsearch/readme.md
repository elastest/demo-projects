Info
=====

Webapp example using [ELK stack](https://www.elastic.co/elk-stack) to save the application logs.

Steps
=====

1. Start [ELK stack](https://www.elastic.co/elk-stack) (Elasticsearch, Logstash and Kibana) running `docker-compose -f elk.yml up` command.
	- **Elasticsearch** will be available at `9200` port (both internally and binded). Note: if you have trouble starting Elasticsearch, run the command `sysctl -w vm.max_map_count=262144`
	- **Logstash** will be available at `5000` tcp port to send logs (both internally and binded). All logs will be sent to the same Elasticsearch index (`webapp`).
	- **Kibana** will be available at `5601` port (both internally and binded)
2. Create a Jenkins pipeline with the code of **Jenkinsfile**.
3. Run Job.

Docker Images
=============

- elastest/etm-check-service-up
- elastest/ci-docker-e2e-compose:latest
- elastest/demo-web-java-test-sut-offline
- docker.elastic.co/elasticsearch/elasticsearch:6.5.3
- docker.elastic.co/kibana/kibana:6.5.3
- edujgurjc/eslogstash
