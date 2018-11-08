var request = require('request');
config = require('./elastest-conf.js');

describe('Rest Test', function() {
    it('Root Service Test', function(done) {
        console.log('Send GET request to ' + config.sutUrl);

        request.get(config.sutUrl, function(error, response, body) {
            expect(body).toEqual('Hello World!');
            done();
        });
    });
});
