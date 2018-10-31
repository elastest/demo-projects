var env = require('./envs.js');

exports.config = {
    seleniumAddress: env.seleniumAddress,
    specs: ['test-webapp-spec.js'],
    sutUrl: 'http://172.17.0.2:8080',
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
                console.log('##### Start test: ' + result.description);
            },
            specDone: function(result) {
                console.log('##### Finish test: ' + result.description);
            },
        };

        jasmine.getEnv().addReporter(reporterCurrentSpec);

        browser.waitForAngularEnabled(false);
    },
};