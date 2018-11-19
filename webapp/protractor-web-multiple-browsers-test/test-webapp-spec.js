describe('Test WebApp Application', function() {
    it('Check that the title and body are not empty', async () => {
        // Add row
        await addRow('', '');
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
        await addRow('MessageTitle', 'MessageBody');
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

async function addRow(title, body) {
    console.log('Inserting data...');
    element(by.id('title-input')).sendKeys(title);
    element(by.id('body-input')).sendKeys(body);
    await sleep(2000);
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
