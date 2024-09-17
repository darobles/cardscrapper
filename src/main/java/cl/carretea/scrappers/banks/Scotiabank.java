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

public class Scotiabank {
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://beneficios.scotiabank.cl/scclubfront/categoria/platosycomida/rutagourmet";
		List<Promotion> scotia_promotions = new ArrayList<Promotion>();
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
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
				wait.until(ExpectedConditions.elementToBeClickable(By.id("panel-1")));
			} catch (Exception ex) {
				System.out.println("Ex " + ex);
			}
			
			List<WebElement> resultsList = driver.findElements(By.className("marketing-card"));
			System.out.println("find " + resultsList.size());
			if(resultsList.isEmpty())
				return null;
			for (WebElement ele : resultsList) {
				Promotion  promo = new Promotion();
				promo.setInstitution_name("Scotiabank");
				System.out.println(ele.getAttribute("data-nombre"));
				promo.setName_store(ele.getAttribute("data-nombre"));
				WebElement descAux = ele.findElement(By.className("headline-small"));
				String[] descArr = descAux.getText().split("%");
				if(descArr.length > 1)
				{
					promo.setDiscount(Integer.parseInt(descArr[0]));
				}
				//Ver detalle
				promo.setUrl("https://beneficios.scotiabank.cl/scclubfront/categoria/platosycomida/rutagourmet");
				ele.findElement(By.className("clickfollow")).click();
				Thread.sleep(1000);
				//WebElement accordeon = driver.findElement(By.className("sc-card-content"));
				WebElement large_descriptionWeb = driver.findElement(By.id("detalle-comousar"));
				promo.setLarge_description(large_descriptionWeb.getAttribute("textContent"));
				WebElement description = driver.findElement(By.id("detalle-descuento"));
				WebElement days = driver.findElement(By.id("detalle-dias"));
				WebElement end = driver.findElement(By.id("detalle-antetitulo"));
				promo.setDescription(days.getAttribute("textContent") + " " + description.getAttribute("textContent")+ " " + end.getAttribute("textContent"));
				System.out.println(promo.toString());
				scotia_promotions.add(promo);
				((JavascriptExecutor)driver).executeScript("volver();");
			}	

		
	
			

		try {
			//driver.close();
			
			System.out.println("clouse2");
			driver.quit();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return scotia_promotions;

	}
}
