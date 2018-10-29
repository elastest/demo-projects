Feature: Test a Sut 

Background: 
    * eval karate.log('##### Start test:',karate.info.scenarioName)
    * configure afterScenario = 
    """
    function(){
      karate.log('##### Finish test:',karate.info.scenarioName);
    }
    """
Scenario: Navigate to url and obtain 200 response 
    Given  url appUrl 
    When   method get 
    Then   status 200 
Scenario: Navigate to url and obtain 500 response 
    Given  url appUrl 
    When   method get 
    Then   status 500    