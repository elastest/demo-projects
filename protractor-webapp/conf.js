var env = require('./envs.js');

exports.config = {
    seleniumAddress: env.seleniumAddress,
    specs: ['test-webapp-spec.js'],
    sutUrl: env.sutUrl,
    capabilities: env.capabilities,
    onPrepare: function() {
        var jasmineReporters = require('jasmine-reporters');
        jasmine.getEnv().addReporter(
            new jasmineReporters.JUnitXmlReporter({
                consolidateAll: true,
                savePath: 'testresults',
                // this will produce distinct xml files for each capability
                filePrefix: 'xml-report',
            }),
        );

        var reporterCurrentSpec = {
            specStarted: function(result) {
                browser.waitForAngularEnabled(false);
                browser.executeScript(
                    '<<##et => {"command": "startTest", "args": {"testName": "' + result.description + '"} }>>',
                );
                console.log('##### Start test: ' + result.description);
                browser.get(env.sutUrl);
            },
            specDone: function(result) {
                console.log('##### Finish test: ' + result.description);
            },
            suiteDone: function(result) {
                browser.close();
            },
        };

        jasmine.getEnv().addReporter(reporterCurrentSpec);

        browser.waitForAngularEnabled(false);
    },
};
