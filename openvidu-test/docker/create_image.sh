# openvidu-server
git clone https://github.com/OpenVidu/openvidu.git
cd openvidu
mvn -DskipTests=true install
mvn -DskipTests=true compile
cd openvidu-server
mvn clean compile package
cp ./target/openvidu-server-1.1.1.jar ../../openvidu-server.jar

# openvidu-testapp
cd ../openvidu-testapp
npm install
ng build
cp -r ./dist ../../openvidu-testapp

# Build docker image
cd ../../
docker build -t elastest/demo-openvidu-test-sut2 .

# Delete unwanted files
rm -rf ./openvidu-testapp
rm openvidu-server.jar
rm -rf ./openvidu
