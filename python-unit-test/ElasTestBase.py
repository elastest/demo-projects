import unittest

class ElasTestBase(unittest.TestCase):
    def setUp(self):
        testName = self._testMethodName
        print '##### Start test: ' + testName

    def tearDown(self):
        testName = self._testMethodName
        print '##### Finish test: ' + testName
