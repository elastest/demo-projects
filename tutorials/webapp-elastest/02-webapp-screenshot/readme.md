Info
=====

Webapp example using docker logs command to **save application logs into a file** and taking a **screenshot** of failed tests

Steps
=====

1. Start [ELK stack](https://www.elastic.co/elk-stack) (Elasticsearch, Logstash and Kibana) running `docker-compose -f elk.yml up` command
2. Create a Jenkins pipeline with the code of **Jenkinsfile**
3. Run Job

