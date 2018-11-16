var jasmineReporters = require('jasmine-reporters');

jasmine.getEnv().addReporter(
    new jasmineReporters.JUnitXmlReporter({
        consolidateAll: true,
        savePath: 'testresults',
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
