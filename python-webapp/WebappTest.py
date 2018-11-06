import unittest
import os
import sys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import xmlrunner
import ElasTestBase


def getElementById(driver, id, timeout=10):
    wait = WebDriverWait(driver, timeout)
    return wait.until(EC.presence_of_element_located((By.ID, id)))


def addRow(driver, title, body):
    getElementById(driver, 'title-input').send_keys(title)
    getElementById(driver, 'body-input').send_keys(body)
    print 'Adding Message...'
    getElementById(driver, 'submit').click()


def clearData(driver):
    print 'Clearing Messages...'
    getElementById(driver, 'clearSubmit').click()


class TestWebApp(ElasTestBase.ElasTestBase):
    def test_check_title_and_body_not_empty(self):
        driver = ElasTestBase.driver
        try:
            time.sleep(1)
            addRow(driver, '', '')
            time.sleep(2)

            title = getElementById(driver, 'title').text
            body = getElementById(driver, 'body').text
            print 'Checking Message...'

            self.assertNotEqual('', title)
            self.assertNotEqual('', body)
        except Exception as e:
            clearData(driver)
            sys.exit(1)
        clearData(driver)


    def test_find_title_and_body(self):
        driver = ElasTestBase.driver
        try:
            time.sleep(1)
            addRow(driver, 'MessageTitle', 'MessageBody')
            time.sleep(2)

            title = getElementById(driver, 'title').text
            body = getElementById(driver, 'body').text
            print 'Checking Message...'

            self.assertEqual('MessageTitle', title)
            self.assertEqual('MessageBody', body)
        except Exception as e:
            clearData(driver)
            sys.exit(1)
        clearData(driver)

if __name__ == '__main__':
    file_path = './testresults'
    if not os.path.exists(file_path):
        os.makedirs(file_path)
    file_name = file_path + '/results.xml'
    with open(file_name, 'wb') as output:
        unittest.main(
            testRunner=xmlrunner.XMLTestRunner(output=output),
            failfast=False, buffer=False, catchbreak=False)
