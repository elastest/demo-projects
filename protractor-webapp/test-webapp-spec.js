var env = require('./envs.js');

describe('Test WebApp Application', function() {
    var firstTime = true;

    beforeEach(async () => {
        if (!firstTime) {
            await browser.restart();
        }
        await browser.waitForAngularEnabled(false);
        firstTime = false;
        browser.get(env.sutUrl);
    });

    it('Check that the title and body are not empty', async () => {
        // Add row
        addRow('', '');
        await sleep(2000);

        // Check row
        var title = element(by.id('title')).getText();
        var body = element(by.id('body')).getText();

        console.log('Checking Message...');
        expect(title).not.toEqual('');
        expect(body).not.toEqual('');

        clearMessages();
    });

    it('Find title and body', async () => {
        // Add row
        addRow('MessageTitle', 'MessageBody');
        await sleep(2000);

        // Check row
        var title = element(by.id('title')).getText();
        var body = element(by.id('body')).getText();

        console.log('Checking Message...');

        expect(title).toEqual('MessageTitle');
        expect(body).toEqual('MessageBody');

        clearMessages();
    });
});

function addRow(title, body) {
    element(by.id('title-input')).sendKeys(title);
    element(by.id('body-input')).sendKeys(body);
    console.log('Adding Message...');
    element(by.id('submit')).click();
}

function clearMessages() {
    console.log('Clearing Messages...');
    element(by.id('clearSubmit')).click();
}

function sleep(millis) {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(true);
        }, millis);
    });
}
