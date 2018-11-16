import unittest
import os
import sys
import xmlrunner
import ElasTestBase
import requests


class TestRest(ElasTestBase.ElasTestBase):
    def test_root_service(self):
        sutUrl = ElasTestBase.sutUrl
        print 'Send GET request to ' + sutUrl
        response = requests.get(sutUrl)
        self.assertEqual(response.content, 'Hello World!')


if __name__ == '__main__':
    file_path = './testresults'
    if not os.path.exists(file_path):
        os.makedirs(file_path)
    file_name = file_path + '/results.xml'
    with open(file_name, 'wb') as output:
        unittest.main(
            testRunner=xmlrunner.XMLTestRunner(output=output),
            failfast=False, buffer=False, catchbreak=False)
