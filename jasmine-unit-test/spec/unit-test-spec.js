require('./elastest-conf.js');
var calc = require('../calc.js');

leftOperand = 3;
rightOperand = 2;

describe('Unit Test', function() {
    it('Sum', function() {
        expectedResult = 5;
        console.log('Checking if ' + leftOperand + ' + ' + rightOperand + ' = ' + expectedResult);
        expect(calc.sum(leftOperand, rightOperand)).toEqual(expectedResult);
    });

    it('Sub', function() {
        expectedResult = 1;
        console.log('Checking if ' + leftOperand + ' - ' + rightOperand + ' = ' + expectedResult);
        expect(calc.sub(leftOperand, rightOperand)).toEqual(expectedResult);
    });
});
