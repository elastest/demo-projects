package io.elastest.demo.web.unique;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/webapp-test.feature", plugin = {
        "html:target/surefire-reports/cucumber-html-report",
        "json:target/surefire-reports/cucumber.json",
        "pretty" }, glue = { "io.elastest.demo.web.unique" })

public class WebAppTestRunner {

}
