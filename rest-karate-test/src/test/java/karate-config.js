function() {   

  // Log start and finish every scenario
  karate.log('##### Start test:',karate.info.scenarioName);
  karate.configure("afterScenario",function(){
      karate.log('##### Finish test:',karate.info.scenarioName);
  });
  
  var host = java.lang.System.getenv('ET_SUT_HOST');
  if(!host) {
	  host = "localhost"
  }
  var url = "http://" + host + ":8080";
  var config = {
    appUrl: url
  };
  
  return config;
}

