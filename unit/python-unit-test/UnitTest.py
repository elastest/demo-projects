import unittest
import os
import sys
import xmlrunner
import ElasTestBase
from Calc import *

leftOperand = 3
rightOperand = 2


class TestUnit(ElasTestBase.ElasTestBase):

    def test_sum(self):
        global leftOperand
        global rightOperand
        expected = 5

        print 'Checking if ' + str(leftOperand) + ' + ' + \
            str(rightOperand) + ' = '+str(expected)

        self.assertEqual(sum(leftOperand, rightOperand), expected)

    def test_sub(self):
        global leftOperand
        global rightOperand
        expected = 1

        print 'Checking if ' + str(leftOperand) + ' - ' + \
            str(rightOperand) + ' = '+str(expected)

        self.assertEqual(sub(leftOperand, rightOperand), expected)


if __name__ == '__main__':
    file_path = './testresults'
    if not os.path.exists(file_path):
        os.makedirs(file_path)
    file_name = file_path + '/results.xml'
    with open(file_name, 'wb') as output:
        unittest.main(
            testRunner=xmlrunner.XMLTestRunner(output=output),
            failfast=False, buffer=False, catchbreak=False)
