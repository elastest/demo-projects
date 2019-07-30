#/!bin/bash
# Add sut prefix for the current execution 
cd ${PWD}/k8s
sed -i -e "s/name: demo-web-java-sut/name: ${ET_SUT_CONTAINER_NAME}-demo-web-java-sut/g" -e 's/sut_/sut-/g' webapp-deployment.yaml
