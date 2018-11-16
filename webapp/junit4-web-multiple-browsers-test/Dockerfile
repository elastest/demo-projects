FROM elastest/test-etm-alpinegitjava

CMD git clone https://github.com/elastest/demo-projects; cd demo-projects/web-java-test; mvn clean package -DskipTests; cd target; exec java -jar $(ls | grep ".*\.jar$");
