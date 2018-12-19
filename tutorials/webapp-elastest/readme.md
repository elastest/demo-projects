Info
====

This tutorial is formed by several projects that develop functionalities incrementally. It will make use of the application we call Webapp, starting it as a docker container.

The tutorial relies heavily on Docker. The minimum requirements areas follows:

- Ubuntu 18.04 environment. If you use Windows, you can still do the tutorial in a VM within your machine. However, bear in mind that at least 8Gb of memory for the vm are required.
- Docker (https://docs.docker.com/install/linux/docker-ce/ubuntu/)
- Docker compose (https://docs.docker.com/compose/install/)
- ElasTest (https://elastest.io/docs/try-elastest/)
  - It should be started with Jenkins enabled:
  
        docker run --rm -v ~/.elastest:/data -v /var/run/docker.sock:/var/run/docker.sock elastest/platform start --jenkins
        
  - If you're using ElasTest from within a VM, then you need to provide the ip address of the vm on startup:

        docker run --rm -v ~/.elastest:/data -v /var/run/docker.sock:/var/run/docker.sock elastest/platform start --jenkins --server-address=<vm ip>

- Cloning the samples repository:

      git clone https://github.com/elastest/demo-projects.git

For examples 4 and 5, it is required that the max map count is increased for ElasticSearch, otherwise it might exit abruptly. Therefore, issue this command before starting any  of the tools:

    sudo sysctl -w vm.max_map_count=262144

In addition, it is recommended to download in advance the following containers to avoid issues due to network congestion:

- elastest/etm-check-service-up
- elastest/ci-docker-e2e-compose:latest
- elastest/demo-web-java-test-sut-offline
- docker.elastic.co/elasticsearch/elasticsearch:6.5.3
- docker.elastic.co/kibana/kibana:6.5.3
- edujgurjc/eslogstas
- elastest/etm-dockbeat:latest
- Elastest-containers
    - elastest/platform:1.0.0-beta4
    - elastest/platform-services:1.0.0-beta4
    - elastest/edm-mysql:1.0.0-beta4
    - elastest/etm:1.0.0-beta4
    - elastest/etm-proxy:1.0.0-beta4
    - elastestbrowsers/chrome:latest-2.0.1
    - elastest/etm-dockbeat:1.0.0-beta4
    - elastest/test-etm-alpinegitjava
