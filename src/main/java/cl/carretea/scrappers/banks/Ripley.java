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

public class Ripley {
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://www.bancoripley.cl/beneficios-y-promociones";
		List<Promotion> ripley_promotions = new ArrayList<Promotion>();
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.addArguments("--disable-gpu", "--blink-settings=imagesEnabled=false"); // "--headless",
		// optChrome.addExtensions(new File ("BlockImage.crx"));
		optChrome.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		WebDriver driver = new ChromeDriver(chromeDriverService, optChrome);
		driver.get(urlBase);

		try {
			Thread.sleep(1000);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("scroll(0, 1250)");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.elementToBeClickable(By.className("classContentItemsBoxes")));
		} catch (Exception ex) {
			System.out.println("Ex " + ex);
		}
		
		List<WebElement> resultList = driver.findElements(By.className("new-card_beneficios"));
		System.out.println("find " + resultList.size());
		if (resultList.isEmpty())
			return null;
		for (WebElement ele : resultList) {
				Promotion promo = new Promotion();
				promo.setInstitution_name("Banco Ripley");
				WebElement title = ele.findElement(By.className("title"));
				promo.setName_store(title.getText());
				WebElement url = ele.findElement(By.className("linkDetalle"));
				promo.setUrl(url.getAttribute("href"));
				WebElement dscto = ele.findElement(By.className("dcto"));
				String[] dsctoArr = dscto.getText().split("%");
				promo.setDiscount(Integer.parseInt(dsctoArr[0]));
				ripley_promotions.add(promo);
			}

			ripley_promotions.forEach(promo -> {
				driver.get(promo.getUrl());
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(By.className("dcto")));
				WebElement cont = driver.findElement(By.id("containerBenefecios-details"));
				WebElement desc = cont.findElement(By.className("dcto"));
				WebElement until = cont.findElement(By.xpath("//*[@id=\"containerBenefecios-details\"]/div/div[4]/div[1]/div[1]/div/p"));
				promo.setDescription(desc.getAttribute("textContent") + " " + until.getAttribute("textContent"));
				WebElement largeDescription = cont
						.findElement(By.id("legalDetalle"));
				promo.setLarge_description(promo.getDescription() + " " + largeDescription.getAttribute("textContent").trim());				
				System.out.println(promo.toString());
			});

			try {
				// driver.close();

				System.out.println("clouse2");
				driver.quit();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		
		return ripley_promotions;

	}
}
