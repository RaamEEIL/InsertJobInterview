

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FindInsertTest {
    
    public void outputTimeStamp(String outputText){
        System.out.println("==> "+LocalTime.now()+": "+outputText);
    }

    @Test
    public void findMobileEngagementPlatformElements(){
        List<WebElement> foundElements;
        WebElement foundElement;
        int foundElementsSize=0;
        int currentElement=0;

        //You will need to download geckodriver from https://github.com/mozilla/geckodriver/releases
        //for a different OS.
        //Also change the path to the geckodriver.
            System.setProperty("webdriver.gecko.driver","/Users/shlomi/Downloads/geckodriver");
            WebDriver driver = new FirefoxDriver();
            outputTimeStamp("Working with Firefox geckodriver");

        driver.get("https://www.google.com");

        //Open Google search results page and allow 30 seconds for page to fully load
        assertTrue(driver.getTitle().startsWith("Google"));        
        outputTimeStamp("Currnt URL: "+driver.getCurrentUrl());
        driver.findElement(By.xpath("//input[@title='Search']"))
                .sendKeys("Insert.io"+Keys.RETURN);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        outputTimeStamp("Currnt URL: "+driver.getCurrentUrl()+"\n"+"Currnt Title: "+driver.getTitle());
        
        
        /** Investigating the results for a specific link title and clicking it requires handling
         * 1. The click opens a google link before opening the actual page we are interested in
         * 2. Navigating back doesn't retrieve the previous found elements. The list of elements
         * is stale and needs to be acquired again.
         * 
         * Solution approach: 
         * 1. Investigate the search results
         * 2. Navigate to target page defined by the link's title
         * 3. Navigate back to search results page.
         * 4. Repeat the steps above until iterating through all the found elements.
        **/
        do{
            //Wait until search results load into page.
            try {
                WebElement expectedElement = (new WebDriverWait(driver, 30))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//a[contains(text(),'Mobile Engagement Platform')]")));  
            } catch (TimeoutException e) {
                outputTimeStamp("Failed to load page titled '*Mobile Engagement Platform*'");
            }

            //investigate available links with titles containing "Mobile Engagement Platform"
            foundElements=driver.findElements(
                    By.xpath("//a[contains(text(),'Mobile Engagement Platform')]"));
            
            if(foundElementsSize==0){
                foundElementsSize=foundElements.size();
                outputTimeStamp("Number of matching links found="+foundElementsSize);
            }
       

            //Click the found element by position 
            outputTimeStamp("Handling link #"+currentElement);
            foundElements.get(currentElement).click();

            //Make sure the link's page is loaded
            try {
                boolean loadedLink = (new WebDriverWait(driver, 30))
                        .until(ExpectedConditions.titleContains("Mobile Engagement Platform"));
            } catch (TimeoutException e) {
                outputTimeStamp("Failed to leave Google search results page");
            }
            
            
            
            //Investigate the loaded link, navigate back to search results and repeat the process
            //for the next link.
            outputTimeStamp("Currnt URL: "+driver.getCurrentUrl()+"\n"+"Currnt Title: "+driver.getTitle());
            driver.navigate().back();
            outputTimeStamp("Navigating back to search results");

            currentElement++;
        }while(currentElement<foundElementsSize);

        driver.close();
        //Comment out the quit method to leave window open for post test run debug
        driver.quit();
    }
}
