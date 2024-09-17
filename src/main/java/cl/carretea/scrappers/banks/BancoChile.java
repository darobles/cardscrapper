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

public class BancoChile {
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://portales.bancochile.cl/personas/beneficios/los-mejores-sabores/categoria?tipo=restaurantes-y-bares";
		List<Promotion> bchile_promotions = new ArrayList();
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
		while(hasNextPage)
		{
			try {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(By.className("cercanos--beneficios__arrows")));
			} catch (Exception ex) {
				System.out.println("Ex " + ex);
			}
			
			WebElement container = driver.findElement(By.className("sabores--beneficios__row"));;
			List<WebElement> resultsList = container.findElements(By.className("sabores--beneficios__card"));
			System.out.println("find " + resultsList.size());
			if(resultsList.isEmpty())
				return null;
			for (WebElement ele : resultsList) {
				List<WebElement> place = ele.findElements(By.className("sabores--card__title_beneficio"));
				if (!place.isEmpty()) {
					Promotion promo = new Promotion();
					String link = ele.getAttribute("href");
					promo.setName_store(place.get(0).getText());
					promo.setUrl(link);
					List<WebElement> discount_list = ele.findElements(By.className("sabores--card__excerpt"));
					if (!discount_list.isEmpty()) {
						String[] disc= discount_list.get(0).getText().split("%");
						int discount = 0;
						if(disc.length >= 1)
						{
							discount = Integer.parseInt(disc[0]);
						}
						promo.setDiscount(discount);
					}
					WebElement description = ele.findElement(By.className("sabores--card__description"));
					promo.setDescription(description.getText());
					promo.setInstitution_name("Banco de Chile");
					bchile_promotions.add(promo);
				}

			}		
			List<WebElement> pag = driver.findElements(By.className("cercanos--beneficios__arrows"));
			if(pag.get(1).getAttribute("class").toString().contains("active"))
			{
				JavascriptExecutor executor = (JavascriptExecutor)driver;
				executor.executeScript("arguments[0].click();", pag.get(1));
			}
			else {
				break;
			}
			
		}
			
		bchile_promotions.forEach(promo -> {
			driver.get(promo.getUrl());
			WebElement extra_info = driver.findElement(By.className("detail__description"));			
			WebElement expiration = driver.findElement(By.className("vigencia"));
			System.out.println("exp " + expiration.getText());
			promo.setLarge_description( expiration.getText() + " " + extra_info.getText());
		});

		try {
			//driver.close();
			System.out.println("clouse2");
			driver.quit();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return bchile_promotions;

	}
}
