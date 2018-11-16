import unittest

class ElasTestBase(unittest.TestCase):
    def setUp(self):
        print '##### Start test: ' + self._testMethodName

    def tearDown(self):
        print '##### Finish test: ' + self._testMethodName
