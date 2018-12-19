Info
=====

Webapp example using [ELK stack](https://www.elastic.co/elk-stack) to save the application logs and Jenkins Logstash plugin to save the Job logs

Steps
=====

1. Start [ELK stack](https://www.elastic.co/elk-stack) (Elasticsearch, Logstash and Kibana) running `docker-compose -f elk.yml up` command.
	- **Elasticsearch** will be available at `9200` port (both internally and binded). Note: if you have trouble starting Elasticsearch, run the command `sysctl -w vm.max_map_count=262144`
	- **Logstash** will be available at `5000` tcp port to send logs (both internally and binded). All logs will be sent to the same Elasticsearch index (`webapp`).
	- **Kibana** will be available at `5601` port (both internally and binded)
2. Install and configure [Logstash plugin](https://wiki.jenkins.io/display/JENKINS/Logstash+Plugin) in your Jenkins:
	- Go to Manage Jenkins -> Manage Plugins -> Available, search logstash and install.
	- Go to Manage Jenkins -> Configure System and do scroll to Logstash section to configure:
		- Check **Enable sending logs to an Indexer**
		- Select **Elastic Search** as Indexer Type
		- Insert Elasticsearch uri, like http://IP:9200/MYINDEX/_doc. If you are using [Jenkins instance of ElasTest](https://elastest.io/docs/jenkins/) the IP will be the host ip of the ElasTest network (like 172.18.0.1). If you use Jenkins installed on your machine, you can use localhost as ip, as the ports are binded. The index (MYINDEX) can be whatever you want. You can use **webapp**, which is the index to which the application's logs are sent, but there will be no way to differentiate the logs from each other. This can be solved by configuring different [filters and inputs in Logstash](https://www.elastic.co/guide/en/logstash/6.5/filter-plugins.html), but we have not done it for this example.
		- Save configuration and restart Jenkins if is necessary
3. Create a Jenkins pipeline with the code of **Jenkinsfile**. Note that this pipeline uses the wrapper `logstash {}`
4. Run Job

Docker Images
=============

- elastest/etm-check-service-up
- elastest/ci-docker-e2e-compose:latest
- elastest/demo-web-java-test-sut-offline
- docker.elastic.co/elasticsearch/elasticsearch:6.5.3
- docker.elastic.co/kibana/kibana:6.5.3
- edujgurjc/eslogstash

