import unittest
import os
from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities


driver = None


class ElasTestBase(unittest.TestCase):
    def setUp(self):
        global driver

        testName = self._testMethodName
        print '##### Start test: ' + testName

        if('ET_EUS_API' in os.environ):
            capabilities = DesiredCapabilities.CHROME
            if('BROWSER' in os.environ):
                if(os.environ['BROWSER'] == 'firefox'):
                    capabilities = DesiredCapabilities.FIREFOX

            capabilities['testName'] = testName

            driver = webdriver.Remote(
                command_executor=os.environ['ET_EUS_API'],
                desired_capabilities=capabilities)
        else:
            driver = webdriver.Chrome('/usr/lib/chromium-browser/chromedriver')

        sutIp = '172.17.0.3'
        if('ET_SUT_HOST' in os.environ):
            sutIp = os.environ['ET_SUT_HOST']

        sutUrl = 'http://' + sutIp + ':8080'
        driver.get(sutUrl)
        return driver

    def tearDown(self):
        global driver
        testName = self._testMethodName
        print '##### Finish test: ' + testName
        driver.close()
