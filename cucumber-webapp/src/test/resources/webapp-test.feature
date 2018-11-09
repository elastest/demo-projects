@feature
Feature: Test WebApp Application 
Scenario: Check that the title and body are not empty 
    Given app url 2 
    When i add an empty title and body 2
    Then row with empty title and body added 2
    
    
Scenario: Find title and body 
    Given app url 2
    When i add a row with title and body 2
    Then row with the same title and body added 2
