package using.browsers.in.elastest.demo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UseBrowser extends ElastestBaseTest{
	
    protected static final Logger logger = LoggerFactory
            .getLogger(UseBrowser.class);

	@Test
	public void searchInGoogle() {
		String URL = "https://google.com/";
		
		this.driver.get(URL);		
		logger.info("Go to http://google.es/...");
		
		sleep(2000);
		
		logger.info("Searching 'test' word...");
		WebElement searchInput = driver.findElement(By.xpath("//input[@class='gLFyf gsfi']"));
		searchInput.sendKeys("test");
		searchInput.sendKeys(Keys.ENTER);
		
		sleep(2000);
		
		logger.info("Finish test correctly");
	}
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
