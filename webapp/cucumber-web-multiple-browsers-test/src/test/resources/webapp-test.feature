Feature: Test WebApp Application
Scenario: Check that the title and body are not empty
    Given app url
    When i add an empty title and body
    Then row with empty title and body added
    
    
Scenario: Find title and body
    Given app url
    When i add a row with title and body
    Then row with the same title and body added
