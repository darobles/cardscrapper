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

public class Security {
//
	public List<Promotion> runScrapper() throws InterruptedException {
		String urlBase = "https://personas.bancosecurity.cl/beneficios/gourmet";
		List<Promotion> security_promotions = new ArrayList<Promotion>();
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
			jse.executeScript("scroll(0, 150)");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.elementToBeClickable(By.className("view-id-beneficios")));
		} catch (Exception ex) {
			System.out.println("Ex " + ex);
		}
		driver.findElement(By.id("hs-eu-close-button")).click();
		Thread.sleep(2000);
		boolean pager = true;
		while(pager)
		{
			try {
				List<WebElement> more = driver.findElements(By.className("pager"));
				if(more.isEmpty()) {
					break;
				}
				more.get(0).click();
				Thread.sleep(2000);
				
			}
			catch(Exception ex) {
				System.out.println(ex);
				break;
			}
			
			
		}
		Thread.sleep(2000);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scroll(0, 150)");
		WebElement aux = driver.findElement(By.className("views-infinite-scroll-content-wrapper"));
		List<WebElement> resultList = aux.findElements(By.className("coh-column"));
		
		System.out.println("find " + resultList.size());
		if (resultList.isEmpty())
			return null;
		for (WebElement ele : resultList) {
				Promotion promo = new Promotion();
				promo.setInstitution_name("Banco Security");
				WebElement title = ele.findElement(By.className("data-container")).findElement(By.xpath("div"));
				System.out.println(title.getText());
				promo.setName_store(title.getText());
				WebElement url = ele.findElement(By.className("item-card"));
				promo.setUrl(url.getAttribute("href"));
				WebElement dscto = ele.findElement(By.xpath("//*[@id=\"block-bancosecurity-theme-views-block-beneficios-block-2\"]/div/div/div/div/div[2]/div/div[2]/div[2]/div[1]/div/div[10]/div/span/article/a/div[1]/div[3]"));
				String[] dsctoArr = dscto.getText().split("%");
				promo.setDiscount(Integer.parseInt(dsctoArr[0]));
				security_promotions.add(promo);
			}

		security_promotions.forEach(promo -> {
				driver.get(promo.getUrl());
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(By.className("benefit")));
				WebElement ele = driver.findElement(By.className("benefit"));
				WebElement desc = ele.findElement(By.xpath("div/div/div[1]/div[1]/div/p[1]")); // /html/body/div[2]/main/div/article/div[4]/div/text()
				System.out.println(desc.getAttribute("textContent"));
				
				promo.setDescription(desc.getAttribute("textContent"));
				WebElement large = driver.findElement(By.className("acquia-cms-toolbar"));
				WebElement largeDescription = driver
						.findElement(By.className("dialog-off-canvas-main-canvas")); // .findElements(By.xpath("coh-style-contenedor-base-boxed"));
				promo.setLarge_description(promo.getDescription() + ". " + largeDescription.findElement(By.xpath("main/div/article/div[4]/div")).getText().trim());				
				System.out.println(promo.toString());
			});

			try {
				// driver.close();

				System.out.println("clouse2");
				driver.quit();
			} catch (Exception ex) {
				System.out.println(ex);
			}

		
		return security_promotions;

	}
}
