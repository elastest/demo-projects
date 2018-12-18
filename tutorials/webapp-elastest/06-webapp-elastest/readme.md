Info
====

Webapp example using [ElasTest](https://elastest.io/docs/).



How to create in ElasTest
=========================

You can see in more detail how to create tests in ElasTest [here](https://elastest.io/docs/web-browsers/e2e-browser/).

1. Create a project with the name you want but we will call it **`WebApp`**.
2. Navigate to the project.
3. Create a Sut with the following information:
	-   **SuT Name**: can be called as you want, but we will call it **`WebApp`**
	-   Select **`With Docker Image`** option
	-   **Docker Image**: Use **`elastest/demo-web-java-test-sut`** as Sut image 
	-   **Protocol**: **`http`**
	-   **Wait for http port**: which port of the SuT should ElasTest wait to be available before starting the TJob (**`8080`**)
4. Create a TJob into the project with the following configuration:
	-   **TJob Name**: can be called as you want, but we will call it **`JUnit5 Single Browser Test`**
	-   **Test Results Path**: **`/demo-projects/tutorials/webapp-elastest/junit5-web-single-browser-test/target/surefire-reports`**. This is the complete path where the xml reports of the execution in the container will be saved. We explain this in more detail [here](https://elastest.io/docs/testing/unit#xmlAndtestResultsPath).

	-   **Select a SuT**: already created SuT to be tested through to the TJob (**`WebApp`**)

	-   **Environment docker image**:  [**`elastest/test-etm-alpinegitjava`**](https://github.com/elastest/elastest-torm/blob/master/docker/services/examples/test-etm-alpinegitjava/Dockerfile) (image that contains Git, Maven and Java).
	-   **Commands**:
	```
		git clone https://github.com/elastest/demo-projects;
		cd /demo-projects/webapp/junit5-web-multiple-browsers-test;
		mvn -B -Dbrowser=chrome test;
	```
	-   **Test Support Services**: Check **`EUS`**
5. Run the TJob
6. Enjoy



