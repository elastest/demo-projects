#/!bin/bash
# Add sut prefix for the current execution 
sed -i -e "s/name: demo-rest-java-sut/name: ${ET_SUT_CONTAINER_NAME}-demo-rest-java-sut/g" -e 's/sut_/sut-/g' rest-app-deployment.yaml
