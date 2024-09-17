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

public class Bci {
//https://www.bci.cl/beneficios/beneficios-bci/sabores?tags=subcategoria-restaurantes,subcategoria-gourmet-y-delicatessen-comida-saludable,subcategoria-vinos-y-licores
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://www.bci.cl/beneficios/beneficios-bci/sabores?tags=subcategoria-restaurantes,subcategoria-gourmet-y-delicatessen-comida-saludable,subcategoria-vinos-y-licores";
		List<Promotion> bci_promotions = new ArrayList<Promotion>();
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
			wait.until(ExpectedConditions.elementToBeClickable(By.className("carrousel__item")));
		} catch (Exception ex) {
			System.out.println("Ex " + ex);
		}
		boolean hasNextPage = true;
		while(hasNextPage)
		{
		List<WebElement> resultList = driver.findElements(By.className("carrousel__item"));
		System.out.println("find " + resultList.size());
		if (resultList.isEmpty())
			return null;
		for (WebElement ele : resultList) {
				Promotion promo = new Promotion();
				promo.setInstitution_name("BCI");
				promo.setDescription(ele.findElement(By.className("card__title")).getText());
				System.out.println(ele.findElement(By.className("card__title")).getText());
				String[] nameArr = ele.findElement(By.className("card__title")).getText().split(" en ");
				if(nameArr.length > 1)
				{
					promo.setName_store(nameArr[1].replace(" Vivo Los Trapenses", ""));
				}
				else {
					String[] nameArr2 = ele.findElement(By.className("card__title")).getText().split(".en ");
					promo.setName_store(nameArr2[1].replace(" Vivo Los Trapenses", ""));
				}
				promo.setUrl(ele.findElement(By.className("btn-link-light")).getAttribute("href"));

				String[] descArr = promo.getDescription().split("%");
				if(descArr.length > 1)
				{
					String[] aux = descArr[0].split(" ");
					if(aux.length > 1)
					{
						promo.setDiscount(Integer.parseInt(aux[aux.length-1]));
					}
					else {
						promo.setDiscount(Integer.parseInt(descArr[0]));
					}
				}
				
				System.out.println(promo.toString());
				bci_promotions.add(promo);
			}
			//next
			try {
				WebElement next = driver.findElement(By.className("paginator__button--right"));
				if(next.getAttribute("disabled") != null)
				{
					System.out.println("break");
					break;
				}
				next.click();
				Thread.sleep(1000);
			}
			catch(Exception ex) {
				System.out.println("error " + ex.toString());
				break;
			}
		
		}
		bci_promotions.forEach(promo -> {
				driver.get(promo.getUrl());
				List<WebElement> largeDescription = driver
						.findElements(By.xpath("//*[@id=\"benefit-show\"]/div[3]/div[3]/div[1]/div/p"));
				String desc = "";
				for(WebElement web: largeDescription)
				{
					desc += web.getAttribute("textContent");
				}
				if(desc.equals(""))
				{
					List<WebElement> more = driver.findElements(By.xpath("//*[@id=\"benefit-show\"]/div[3]/div[3]/div[1]/div/ul/li"));
					for(WebElement web: more)
					{
						desc += web.getAttribute("textContent");
					} 
				}
				WebElement extra = driver.findElement(By.xpath("//*[@id=\"benefit-show\"]/div[3]/div[2]/div[2]/div/p"));
				promo.setLarge_description(desc + " " + extra.getAttribute("textContent"));
				//*[@id="benefit-show"]/div[3]/div[3]/div[1]/div/p[1]
				System.out.println(promo.toString());
			});

			try {
				// driver.close();//*[@id="benefit-show"]/div[3]/div[3]/div[1]/div/p[1]

				System.out.println("clouse2");
				driver.quit();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		
		//return bci_promotions;
		return bci_promotions;
	}
}
