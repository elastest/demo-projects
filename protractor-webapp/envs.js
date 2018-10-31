console.log(JSON.stringify(process.env));

module.exports = {
    // The address of a running selenium server.
    seleniumAddress: process.env.ET_EUS_API || 'http://localhost:4444/wd/hub',
    sutUrl: process.env.ET_SUT_HOST ? 'http://' + process.env.ET_SUT_HOST + ':8080' : 'http://172.17.0.2:8080',

    // Capabilities to be passed to the webdriver instance.
    capabilities: {
        browserName: process.env.BROWSER || 'chrome',
        version: process.env.BROWSER_VERSION || 'latest',
    },
};
