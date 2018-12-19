Info
=====

Webapp example using [ElasTest Jenkins Plugin](https://elastest.io/docs/jenkins/try-jenkins/) which allows the following information to be stored in ElasTest:
- **Application logs** and **metrics**
- **Job logs**
- **Test results**
- **Browser console logs** (in case there are any)
- **Test recordings**


Steps
=====

1. If you are using the [Jenkins instance of ElasTest](https://elastest.io/docs/jenkins/) you don't need to install the [ElasTest Jenkins Plugin](https://elastest.io/docs/jenkins/try-jenkins/) because it's already installed. Else, follow the steps in [ElasTest docs](https://elastest.io/docs/jenkins/).
2. Create a Jenkins pipeline with the code of **Jenkinsfile**.
3. Run Job


Docker Images
=============

- elastest/demo-web-java-test-sut-offline
- elastest/etm-check-service-up
- elastest/etm-dockbeat:latest
- Elastest-containers
	- elastest/platform:1.0.0-beta4
	- elastest/platform-services:1.0.0-beta4
	- elastest/edm-mysql:1.0.0-beta4
	- elastest/etm:1.0.0-beta4
	- elastest/etm-proxy:1.0.0-beta4
	- elastestbrowsers/chrome:latest-2.0.1
