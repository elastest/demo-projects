import unittest
import os
import sys
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time


def openBrowser(test):
    testName = test._testMethodName
    print '##### Start test: ' + testName
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


class TestCustomTestSuite(unittest.TestCase):

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


if __name__ == '__main__':
    unittest.main()
