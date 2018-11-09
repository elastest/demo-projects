package io.elastest.demo.web;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/multiple-webapp-tests.feature", plugin = {
        "html:target/surefire-reports/cucumber-html-report",
        "json:target/surefire-reports/cucumber.json", "pretty" })

public class MultipleWebAppTestsRunner {

}
