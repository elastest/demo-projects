var envs = {
    // The address of a running selenium server.
    seleniumAddress: process.env.ET_EUS_API || 'http://localhost:4444/wd/hub',
    sutUrl: process.env.ET_SUT_HOST ? 'http://' + process.env.ET_SUT_HOST + ':8080' : 'http://172.18.0.8:8080',

    // Capabilities to be passed to the webdriver instance.
    capabilities: {
        browserName: process.env.BROWSER || 'chrome',
        version: process.env.ET_EUS_API ? (process.env.BROWSER_VERSION ? process.env.BROWSER_VERSION : '') : 'ANY',
    },
};


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
        await browser.executeScript('{"elastestCommand": "startTest", "args": {"testName": "' + result.description + '"} }');
        console.log('##### Start test: ' + result.description);
        browser.get(envs.sutUrl);
    }

    specDone(result) {
        console.log('##### Finish test: ' + result.description);
    }

    jasmineDone(result) {
        browser.close();
    }
}

exports.config = {
    seleniumAddress: envs.seleniumAddress,
    specs: ['test-webapp-spec.js'],
    sutUrl: envs.sutUrl,
    capabilities: envs.capabilities,
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
