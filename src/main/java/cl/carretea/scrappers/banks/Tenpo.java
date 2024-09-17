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

public class Tenpo {
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://www.tenpo.cl/beneficios?categoria=Restobares";
		List<Promotion> tenpo_promotions = new ArrayList();
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
				Thread.sleep(1000);
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("scroll(0, 1250)");
				System.out.println("scroll");
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
				wait.until(ExpectedConditions.elementToBeClickable(By.id("w-dyn-list")));
			} catch (Exception ex) {
				System.out.println("Ex " + ex);
			}
			
			List<WebElement> resultsList = driver.findElements(By.className("b-card-beneficios"));
			System.out.println("find " + resultsList.size());
			if(resultsList.isEmpty())
				return null;
			for (WebElement ele : resultsList) {
				Promotion  promo = new Promotion();
				promo.setInstitution_name("Tenpo");
				WebElement hidden = ele.findElement(By.className("categor-as-hidden"));
				List<WebElement> elements = hidden.findElements(By.className("display-none"));
				promo.setName_store(hidden.findElements(By.className("display-none")).get(2).getAttribute("textContent"));
				WebElement description = ele.findElement(By.className("titulo-beneficio-all"));
				promo.setDescription(description.getText());		
				String[] descArr = description.getText().split("%");
				if(descArr.length > 1)
				{
					String[] aux = descArr[0].split(", ");
					promo.setDiscount(Integer.parseInt(aux[aux.length-1]));
				}
				List<WebElement> cards = ele.findElements(By.className("categor-as-beneficios-bullet"));
				System.out.println(cards.get(0).getText());
				String cardsTxt = cards.get(0).getText();
				List<String> cardsPromo = new ArrayList();
				if(cardsTxt.contains(" y ")) {
					String[] cardsArr = cardsTxt.split(" y ");
					cardsPromo.add(cardsArr[0]);
					cardsPromo.add(cardsArr[1]);
				}
				else {
					cardsPromo.add(cardsTxt);
				}
				promo.setCards_name(cardsPromo);
								
				WebElement long_description = driver.findElement(By.className("p-text-beneficio"));
				promo.setLarge_description(description.getText() + " " + long_description.getText());
				WebElement url = ele.findElement(By.className("cta-beneficio"));
				promo.setUrl(url.getAttribute("href"));
				System.out.println(promo.toString());
				tenpo_promotions.add(promo);
			}	
			List<WebElement> nextPage = driver.findElements(By.className("w-pagination-next"));
			if(!nextPage.isEmpty()) {
				try {
					nextPage.get(0).click();
				}
				catch(Exception ex)
				{
					break;
				}
			}
			else {
				hasNextPage = false;
			}
		}
	
			

		try {
			//driver.close();
			System.out.println("clouse2");
			driver.quit();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return tenpo_promotions;

	}
}
