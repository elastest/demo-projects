import unittest
import os
import sys
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
import time
import xmlrunner


def openBrowser(test):
    testName = test._testMethodName
    print '##### Start test: ' + testName

    if('ET_EUS_API' in os.environ):
        capabilities = DesiredCapabilities.CHROME
        if('BROWSER' in os.environ):
            if(os.environ['BROWSER'] == 'firefox'):
                capabilities = DesiredCapabilities.FIREFOX

        capabilities.browserId = testName

        driver = webdriver.Remote(
            command_executor=os.environ['ET_EUS_API'],
            desired_capabilities=capabilities)
    else:
        driver = webdriver.Chrome('/usr/lib/chromium-browser/chromedriver')

    sutIp = '172.17.0.3'
    if('ET_SUT_HOST' in os.environ):
        sutIp = os.environ['ET_SUT_HOST']

    sutUrl = 'http://' + sutIp + ':8080'
    # driver.get('http://172.17.0.3:8080')
    driver.get(sutUrl)

    return driver


def endTest(test, driver):
    print 'Clearing Messages...'
    getElementById(driver, 'clearSubmit').click()

    testName = test._testMethodName
    print '##### Finish test: ' + testName
    driver.close()


def getElementById(driver, id, timeout=10):
    wait = WebDriverWait(driver, timeout)
    return wait.until(EC.presence_of_element_located((By.ID, id)))


def addRow(driver, title, body):
    getElementById(driver, 'title-input').send_keys(title)
    getElementById(driver, 'body-input').send_keys(body)
    print 'Adding Message...'
    getElementById(driver, 'submit').click()


class TestWebApp(unittest.TestCase):

    def test_check_title_and_body_not_empty(self):
        driver = openBrowser(self)
        try:

            addRow(driver, '', '')
            time.sleep(2)

            title = getElementById(driver, 'title').text
            body = getElementById(driver, 'body').text
            print 'Checking Message...'

            self.assertNotEqual('', title)
            self.assertNotEqual('', body)
        except Exception as e:
            endTest(self, driver)
            sys.exit(1)
        endTest(self, driver)

    def test_find_title_and_body(self):
        driver = openBrowser(self)
        try:
            addRow(driver, 'MessageTitle', 'MessageBody')
            time.sleep(2)

            title = getElementById(driver, 'title').text
            body = getElementById(driver, 'body').text
            print 'Checking Message...'

            self.assertEqual('MessageTitle', title)
            self.assertEqual('MessageBody', body)
        except Exception as e:
            endTest(self, driver)
            sys.exit(1)
        endTest(self, driver)


if __name__ == '__main__':
    file_path = './testresults'
    if not os.path.exists(file_path):
        os.makedirs(file_path)
    file_name = file_path + '/results.xml'
    with open(file_name, 'wb') as output:
        unittest.main(
            testRunner=xmlrunner.XMLTestRunner(output=output),
            failfast=False, buffer=False, catchbreak=False)
