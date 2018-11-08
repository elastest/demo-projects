require('./elastest-conf.js');

leftOperand = 3;
rightOperand = 2;
etMonitorMarkPrefix = '##elastest-monitor-mark:';

describe('Unit Test', function() {
    it('Sum', async () => {
        expectedResult = 5;
        printCommonLogs();
        console.log('Expected Result: ' + expectedResult);
        console.log(etMonitorMarkPrefix + ' id=action, value=Sum');

        expect(sum(leftOperand, rightOperand)).toEqual(expectedResult);
    });

    it('Sub', async () => {
        expectedResult = 1;
        printCommonLogs();
        console.log('Expected Result: ' + expectedResult);
        console.log(etMonitorMarkPrefix + ' id=action, value=Sub');

        expect(sub(leftOperand, rightOperand)).toEqual(expectedResult);
    });
});

function printCommonLogs() {
    console.log('Left operand: ' + leftOperand);
    console.log('Right operand: ' + rightOperand);
}

function sum(a, b) {
    return Number(a) + Number(b);
}

function sub(a, b) {
    return Number(a) - Number(b);
}
