function() {   
  var host = java.lang.System.getenv('ET_SUT_HOST');
  var url = "http://" + host + ":8080";
  karate.log('Sut Url:', url);
  var config = { // base config JSON,
    appUrl: url
  };
  
  return config;
}