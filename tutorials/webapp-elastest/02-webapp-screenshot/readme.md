Info
=====

Webapp example using *docker logs* command to **save application logs into a file** and taking a **screenshot** of failed tests. The application logs are displayed just before finishing the job in the job console and will also be attached as a file to the build.
The screenshots are shown in the job console as base64, so you only have to copy and paste it in a new tab of the browser to view it.

Steps
=====

1. Create a Jenkins pipeline with the code of **Jenkinsfile**
2. Run Job

Docker Images
=============

- elastest/etm-check-service-up
- elastest/ci-docker-e2e-compose:latest
- elastest/demo-web-java-test-sut-offline
