var env = require('./envs.js');

class ElasTestBrowserManager {
    
    constructor() {
        this.firstTime = true;
        /* `_asyncFlow` is a promise.
        * It is a "flow" that we create in `specStarted`.
        * function will wait for the flow to finish before running the next spec.
        * This is not needed since Jasmine 3.0.
         * See https://github.com/jasmine/jasmine/issues/842#issuecomment-336077418
         */
        this._asyncFlow = null;
    }

    jasmineStarted() {
        /* Wait for async tasks triggered by `specStarted`. */
        beforeEach(async () => {
            await this._asyncFlow;
            this._asyncFlow = null;
        });
    }

    specStarted(result) {
        /* Convert async method to promise */
        this._asyncFlow = this.asyncSpecStarted(result);
    }

    async asyncSpecStarted(result) {
        if (!this.firstTime) {
            await browser.restart();
        }
        this.firstTime = false;
        browser.waitForAngularEnabled(false);
        await browser.executeScript('<<##et => {"command": "startTest", "args": {"testName": "' + result.description + '"} }>>');
        console.log('##### Start test: ' + result.description);
        browser.get(env.sutUrl);
    }

    specDone(result) {
        console.log('##### Finish test: ' + result.description);
    }

    jasmineDone(result) {
        browser.close();
    }
}

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
        jasmine.getEnv().addReporter(new ElasTestBrowserManager());

        browser.waitForAngularEnabled(false);
    },
};
