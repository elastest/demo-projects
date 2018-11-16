Feature: Test a Sut 

Scenario: Navigate to url and obtain 200 response 
    Given  url appUrl 
    When   method get 
    Then   status 200 
Scenario: Navigate to url and obtain 500 response 
    Given  url appUrl 
    When   method get 
    Then   status 500 