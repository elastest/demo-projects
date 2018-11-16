import unittest
import os

sutUrl = None
class ElasTestBase(unittest.TestCase):
    def setUp(self):
        global sutUrl
        testName = self._testMethodName
        print '##### Start test: ' + testName
        sutIp = '172.17.0.3'
        if('ET_SUT_HOST' in os.environ):
            sutIp = os.environ['ET_SUT_HOST']

        sutUrl = 'http://' + sutIp + ':8080'

    def tearDown(self):
        testName = self._testMethodName
        print '##### Finish test: ' + testName
