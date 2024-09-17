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

public class Itau {

	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://itaubeneficios.cl/ruta-gourmet/";
		List<Promotion> itau_promotions = new ArrayList<Promotion>();
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
			wait.until(ExpectedConditions.elementToBeClickable(By.className("page-beneficios-list-default__grid")));
		} catch (Exception ex) {
			System.out.println("Ex " + ex);
		}
		
		List<WebElement> resultList = driver.findElements(By.className("page-beneficios-list-default__grid__item"));
		System.out.println("find " + resultList.size());
		if (resultList.isEmpty())
			return null;
		for (WebElement ele : resultList) {
				Promotion promo = new Promotion();
				promo.setInstitution_name("Itau");
				System.out.println(ele.getAttribute("title"));
				promo.setName_store(ele.getAttribute("title"));
				promo.setUrl(ele.getAttribute("href"));
				List<WebElement> descAux = ele.findElements(By.className("beneficio__item__info-discount-only"));
				System.out.println(descAux.size());
				if (descAux.isEmpty()) {
					descAux = ele.findElements(By.className("beneficio__item__info-discount-pb__discount"));
				}
				System.out.println(descAux.get(0).getText().replace("%", ""));
				
				promo.setDiscount(Integer.parseInt(descAux.get(0).getText().replace("%", "")));
				itau_promotions.add(promo);
			}

			itau_promotions.forEach(promo -> {
				driver.get(promo.getUrl());
				WebElement largeDescription = driver
						.findElement(By.className("beneficio__information__texto__block-1"));
				promo.setLarge_description(largeDescription.getText());
				WebElement we = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/div[3]/div[2]/div[1]/p[1]/strong"));
				promo.setDescription(we.getText());
				//html/body/div[3]/div[2]/div[2]/div[3]/div[2]/div[1]/p[1]/strong
				System.out.println(promo.toString());
			});

			try {
				// driver.close();

				System.out.println("clouse2");
				driver.quit();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		
		return itau_promotions;

	}
}
