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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.carretea.scrappers.Promotion;

public class Bice {

	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://banco.bice.cl/personas/beneficios?region=&tarjeta=&subcategorias=&tiposTarjeta=&categorySelected=restaurante&page=2&sorting=M%C3%A1s+reciente";
		List<Promotion> bice_promotions = new ArrayList();
		ChromeOptions optChrome = new ChromeOptions();
		optChrome.addArguments("--disable-gpu", "--blink-settings=imagesEnabled=false"); // 
		// optChrome.addExtensions(new File ("BlockImage.crx"));
		optChrome.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		WebDriver driver = new ChromeDriver(chromeDriverService, optChrome);
		driver.get(urlBase);

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.elementToBeClickable(By.id("lista-beneficios")));
		} catch (Exception ex) {
			System.out.println("Ex " + ex);
		}
		WebElement lista_beneficios = driver.findElements(By.id("lista-beneficios")).get(0);
		while (true) {
			List<WebElement> ver_mas = lista_beneficios.findElements(By.className("my-5"));
			JavascriptExecutor jse = (JavascriptExecutor) driver;

			if (ver_mas.isEmpty()) {
				System.out.println("break");
				break;
			} else {
				//List<WebElement> cardcta = driver.findElements(By.id("cta-expand"));

				jse.executeScript("scroll(0, document.body.scrollHeight)");
				Thread.sleep(1000);
				try {
					jse.executeScript("return document.getElementsByClassName('accordion-cta')[0].remove();");
				}
				catch(Exception ex)
				{
					System.out.println("No element");
				}
				Actions actions = new Actions(driver);
				actions.moveToElement(ver_mas.get(0)).click().perform();
				jse.executeScript("scroll(0, document.body.scrollHeight)");
			}

		}
		
		List<WebElement> resultsList = driver.findElements(By.className("beneficio"));
		System.out.println("find " + resultsList.size());
		
		for (WebElement ele : resultsList) {
			List<WebElement> place = ele.findElements(By.className("marca"));
			if (place.isEmpty()) {
				place = ele.findElements(By.className("card-link__title"));
			}
			if (!place.isEmpty()) {
				Promotion promo = new Promotion();
				String link = ele.findElement(By.tagName("a")).getAttribute("href");
				promo.setName_store(place.get(0).getText());
				promo.setUrl(link);
				List<WebElement> discount_list = ele.findElements(By.className("card-top__desc"));
				if (!discount_list.isEmpty()) {
					String[] disc= discount_list.get(0).getText().split("%");
					int discount = 0;
					if(disc.length >= 1)
					{
						discount = Integer.parseInt(disc[0]);
					}
					promo.setDiscount(discount);
				}
				WebElement description = ele.findElement(By.className("bajada"));
				promo.setDescription(description.getText());
				List<WebElement> duration = ele.findElements(By.className("pl-2"));
				// promo.set(duration.get(0).getText());
				List<WebElement> cards_pop = ele.findElements(By.className("popover-body"));
				if (!cards_pop.isEmpty()) {
					List<WebElement> cards_pop1 = cards_pop.get(0).findElements(By.className("mt-2"));
					List<String> cards_name = new ArrayList();
					for (WebElement card : cards_pop1) {
						WebElement card1 = card.findElement(By.xpath(".//div"));
						cards_name.add(card1.findElements(By.className("m-0")).get(0).getAttribute("textContent"));
					}
					promo.setCards_name(cards_name);
				}
				promo.setInstitution_name("Banco BICE");
				System.out.println(promo.getName_store());
				bice_promotions.add(promo);
			}

		}

		bice_promotions.forEach(promo -> {
			driver.get(promo.getUrl());
			WebElement extra_info = driver.findElement(By.className("aditional-info"));
			promo.setLarge_description(extra_info.getText());
		});

		try {
			//driver.close();
			System.out.println("clouse2");
			driver.quit();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return bice_promotions;

	}
}
