exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    specs: ['test-webapp-spec.js'],
    sutUrl: 'http://172.17.0.2:8080',
    onPrepare: function() {
        console.log(JSON.stringify(process.env));
        if (typeof process.env.ET_EUS_API !== 'undefined') {
            this.seleniumAddress = process.env.ET_EUS_API;
        }
        if (typeof process.env.ET_SUT_HOST !== 'undefined') {
            this.sutUrl = process.env.ET_SUT_HOST;
        }

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
