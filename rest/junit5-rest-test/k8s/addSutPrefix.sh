#/!bin/bash
# Add sut prefix for the current execution 
sed -i -e "s/name: demo-rest-java-sut/name: ${ET_SUT_CONTAINER_NAME}-demo-rest-java-sut/g" -e 's/sut_/sut-/g' -e "s/io.elastest.service: demo-rest-java-sut/io.elastest.service: ${ET_SUT_CONTAINER_NAME}-demo-rest-java-sut/g" rest-app-deployment.yaml
