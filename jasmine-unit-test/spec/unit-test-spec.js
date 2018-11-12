require('./elastest-conf.js');

leftOperand = 3;
rightOperand = 2;

describe('Unit Test', function() {
    it('Sum', function() {
        expectedResult = 5;
        console.log('Checking if ' + leftOperand + ' + ' + rightOperand + ' = ' + expectedResult);
        expect(sum(leftOperand, rightOperand)).toEqual(expectedResult);
    });

    it('Sub', function() {
        expectedResult = 1;
        console.log('Checking if ' + leftOperand + ' - ' + rightOperand + ' = ' + expectedResult);
        expect(sub(leftOperand, rightOperand)).toEqual(expectedResult);
    });
});

function sum(a, b) {
    return Number(a) + Number(b);
}

function sub(a, b) {
    return Number(a) - Number(b);
}
