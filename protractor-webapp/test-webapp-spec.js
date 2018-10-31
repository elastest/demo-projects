describe('Test WebApp Application', function() {
    var elastestStartPrefix = '##### Start test: ';
    var elastestFinishPrefix = '##### Finish test: ';
    var firstTime = true;

    var sutUrl = 'http://172.17.0.2:8080';

    // console.log('aglia', process.env);
    if (typeof process.env.ET_SUT_HOST !== 'undefined') {
        sutUrl = process.env.ET_SUT_HOST;
        console.log('Using ET_SUT_HOST env value', sutUrl);
    }

    beforeEach(async () => {
        if (!firstTime) {
            await browser.restart();
        }
        await browser.waitForAngularEnabled(false);
        firstTime = false;
    });

    var spec1 = it('Check that the title and body are not empty', async () => {
        console.log(elastestStartPrefix + spec1.description);
        browser.get(sutUrl);

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
        console.log(elastestFinishPrefix + spec1.description);
    });

    var spec2 = it('Find title and body', async () => {
        console.log(elastestStartPrefix + spec2.description);
        browser.get(sutUrl);

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
        console.log(elastestFinishPrefix + spec2.description);
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
