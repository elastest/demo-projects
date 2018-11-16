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

module.exports = {
    sutUrl: process.env.ET_SUT_HOST ? 'http://' + process.env.ET_SUT_HOST + ':8080' : 'http://172.17.0.3:8080',
};
