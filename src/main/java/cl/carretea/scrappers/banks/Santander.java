package cl.carretea.scrappers.banks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.carretea.scrappers.Promotion;

public class Santander {
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://banco.santander.cl/landing/beneficios-descuentos";
		List<Promotion> santander_promotions = new ArrayList();
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.addArguments("--disable-gpu", "--blink-settings=imagesEnabled=false"); // "--headless",
		// optChrome.addExtensions(new File ("BlockImage.crx"));
		optChrome.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		WebDriver driver = new ChromeDriver(chromeDriverService, optChrome);
		driver.get(urlBase);
		boolean hasNextPage = true;

			try {
				Thread.sleep(1000);
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("scroll(0, 1250)");
				System.out.println("scroll");
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(By.id("logos-holder")));
			} catch (Exception ex) {
				System.out.println("Ex " + ex);
			}
			
			WebElement container = driver.findElement(By.id("logos-holder"));;
			List<WebElement> resultsList = container.findElements(By.xpath("//div[@id='logos-holder']/div"));
			System.out.println("find " + resultsList.size());
			if(resultsList.isEmpty())
				return null;
			for (WebElement ele : resultsList) {
				Promotion  promo = new Promotion();
				promo.setUrl(urlBase);
				promo.setInstitution_name("Santander");
				ele.click();
				Thread.sleep(1000);
				WebElement name = driver.findElement(By.className("detail-title"));
				System.out.println(name.getText());
				promo.setName_store(name.getText());
				List<WebElement> dsct = driver.findElements(By.className("detail-content"));
				if(!dsct.isEmpty()) {
					String[] dsctarr = dsct.get(0).getText().split("%");
					promo.setDiscount(Integer.parseInt(dsctarr[0]));
				}
				List<WebElement> description = driver.findElements(By.xpath("/html/body/div[3]/div/div/div/div/div/div[2]/div/div[3]/div[1]/p[1]")); ///div[@class='col-12']
				
				System.out.println(description.get(0).getText());
				promo.setDescription(description.get(0).getText());
				
				WebElement long_description = driver.findElement(By.className("detail-content"));
				promo.setLarge_description(long_description.getText());
				
				
				WebElement close = driver.findElement(By.className("str-close"));
				close.click();
				Thread.sleep(1000);
				//System.out.println(promo.toString());
				santander_promotions.add(promo);
			}		
			
	
			

		try {
			//driver.close();
			System.out.println("clouse2");
			driver.quit();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return santander_promotions;

	}
}
