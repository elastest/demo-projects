import unittest
import os
from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities


driver = None
sutUrl = None


class ElasTestBaseBrowserForEach(unittest.TestCase):
    def setUp(self):
        global driver
        global sutUrl
        testName = self._testMethodName
        print '##### Start test: ' + testName

        # os.environ['ET_EUS_API'] = 'http://172.18.0.1:8091/eus/v1/'
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

        sutIp = '172.18.0.8'
        if('ET_SUT_HOST' in os.environ):
            sutIp = os.environ['ET_SUT_HOST']

        sutUrl = 'http://' + sutIp + ':8080'
        driver.get(sutUrl)

    def tearDown(self):
        global driver
        testName = self._testMethodName
        print '##### Finish test: ' + testName
        driver.close()


class ElasTestBaseBrowserForAll(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        global driver
        global sutUrl

        if('ET_EUS_API' in os.environ):
            capabilities = DesiredCapabilities.CHROME
            if('BROWSER' in os.environ):
                if(os.environ['BROWSER'] == 'firefox'):
                    capabilities = DesiredCapabilities.FIREFOX

            driver = webdriver.Remote(
                command_executor=os.environ['ET_EUS_API'],
                desired_capabilities=capabilities)
        else:
            driver = webdriver.Chrome('/usr/lib/chromium-browser/chromedriver')

        sutIp = '172.18.0.8'
        if('ET_SUT_HOST' in os.environ):
            sutIp = os.environ['ET_SUT_HOST']

        sutUrl = 'http://' + sutIp + ':8080'

    @classmethod
    def tearDownClass(cls):
        driver.close()

    def setUp(self):
        global driver
        global sutUrl
        testName = self._testMethodName
        etScript = '\'<<##et => {"command": "startTest", "args": {"testName": "' + testName + '"}}>>\''
        driver.execute_script(etScript)
        print '##### Start test: ' + testName

        driver.get(sutUrl)

    def tearDown(self):
        global driver
        testName = self._testMethodName
        print '##### Finish test: ' + testName
