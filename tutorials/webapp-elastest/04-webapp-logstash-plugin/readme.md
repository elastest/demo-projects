Info
=====

Webapp example using [ELK stack](https://www.elastic.co/elk-stack) to save the application logs and Jenkins Logstash plugin to save the Job logs

Steps
=====

1. Start [ELK stack](https://www.elastic.co/elk-stack) (Elasticsearch, Logstash and Kibana) running `docker-compose -f elk.yml up` command.
	- **Elasticsearch** will be available at `9200` port (both internally and binded)
	- **Logstash** will be available at `5000` tcp port to send logs (both internally and binded). All logs will be sent to the same Elasticsearch index (`webapp`).
	- **Kibana** will be available at `5601` port (both internally and binded)
2. Install and configure [Logstash plugin](https://wiki.jenkins.io/display/JENKINS/Logstash+Plugin) in your Jenkins:
	- Go to Manage Jenkins -> Manage Plugins -> Available, search logstash and install.
	- Go to Manage Jenkins -> Configure System and do scroll to Logstash section to configure:
		- Check **Enable sending logs to an Indexer**
		- Select **Elastic Search** as Indexer Type
		- Insert Elasticsearch uri, like http://IP:9200/MYINDEX/_doc. If you are using [Jenkins instance of ElasTest](https://elastest.io/docs/jenkins/) the IP will be the host ip of the ElasTest network (like 172.18.0.1). The index (MYINDEX) can be whatever you want. If you use Jenkins installed on your machine, you can use localhost as ip, as the ports are binded.
		- Save configuration and restart Jenkins if is necessary
3. Create a Jenkins pipeline with the code of **Jenkinsfile**. Note that this pipeline uses the wrapper `logstash {}`
4. Run Job

