# How to configure this test in ElasTest
## 1. Create a new project
## 2. Create a Sut into project:
- **Name**: whatever you want
- Select **Deployed outside ElasTest** and **No Instrumentation**
- **Sut IP**: amibehindaproxy.com
## 3. Create a TJob:
- **Name**: whatever you want
- **Test Results Path**: /demo-projects/proxytest/target/surefire-reports
- Select your Sut
- **Environment Docker image**: elastest/test-etm-alpinegitjava
- **Commands**
  ```
  git clone https://github.com/elastest/demo-projects;
  cd /demo-projects/proxytest;
  mvn -B test
  ```
- Parameters:
  - **Name**: browserVersion |  **Value**: 71
  - **Name**: PROXY_URL |  **Value**: PROXYIP:PROXYPORT or PROXYURL
