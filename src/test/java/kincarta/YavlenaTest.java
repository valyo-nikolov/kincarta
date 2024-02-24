package kincarta;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.Broker;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class YavlenaTest {

    WebDriver driver;
    Actions actions;
    WebDriverWait wait;
    FluentWait fluentWait;
    JavascriptExecutor js;


    @BeforeTest
    void setup() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920x1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");

        WebDriverManager.chromedriver().clearDriverCache().setup();
        WebDriverManager.chromedriver().clearResolutionCache().setup();

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

        // explicit wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // fluent wait
        fluentWait = new FluentWait<>(driver);
        fluentWait.withTimeout(Duration.ofSeconds(15));
        fluentWait.pollingEvery(Duration.ofMillis(250));
        fluentWait.ignoring(StaleElementReferenceException.class);
        fluentWait.ignoring(NoSuchElementException.class);
        fluentWait.ignoring(ElementClickInterceptedException.class);

        driver.manage().window().maximize();

        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
    }

    @AfterTest
    void afterTest() {
        driver.quit();
    }

    @Test
    public void testYavlenaSiteNamesAdressesAndNumberOfProperties() {
        driver.get("https://www.yavlena.com/broker/");

        WebElement hideCookiesBtn = driver.findElement(By.className("hide-cookies-message"));
        WebElement loadMoreBtn = driver.findElement(By.className("load-more-results-list"));


        fluentWait.until(ExpectedConditions.elementToBeClickable(hideCookiesBtn));
        hideCookiesBtn.click();

        fluentWait.until(ExpectedConditions.elementToBeClickable(loadMoreBtn));
        js.executeScript("arguments[0].click();", loadMoreBtn);


        fluentWait.until(ExpectedConditions.invisibilityOf(loadMoreBtn));

        ArrayList<WebElement> articlesList = (ArrayList<WebElement>) driver.findElements(By.tagName("article"));
        ArrayList<Broker> brokersList = new ArrayList<>();
        final String[] brokerNames = new String[1];

        articlesList.forEach(webElement -> {
            Broker broker = new Broker();
            brokerNames[0] = webElement.findElement(By.cssSelector(".name a")).getAttribute("title");
            broker.setName(brokerNames[0]);
            broker.setAddress(webElement.findElement(By.className("office")).getText());
            broker.setNumProperties(webElement.findElement(By.cssSelector(".position a[title]")).getText());
            brokersList.add(broker);
        });

        ArrayList<Broker> brokersListOuter = new ArrayList<>(brokersList);
        final WebElement[] searchInput = new WebElement[1];

        brokersListOuter.forEach(brokerOuter -> {
            searchInput[0] = (driver.findElement(By.className("input-search")));

            fluentWait.until(ExpectedConditions.visibilityOf(searchInput[0]));

            String brokerName = brokerOuter.getName();
            searchInput[0].sendKeys(brokerName);

            AtomicReference<WebElement> actualName =
                    new AtomicReference<>(driver.findElement(
                            By.xpath("(//a[@title='" + brokerName + "'])[1]")));
            fluentWait.until(ExpectedConditions.visibilityOf(actualName.get()));

            final ArrayList<WebElement>[] actualAddresses = new ArrayList[]{(ArrayList<WebElement>) driver.findElements(
                    By.xpath("//article//a[@title='" + brokerName + "']/" +
                            "parent::h3/following-sibling::div[@class='office']"))};
            actualAddresses[0].forEach(actAddress -> {
                fluentWait.until(ExpectedConditions.visibilityOf(actAddress));
            });

            AtomicReference<ArrayList<WebElement>> actualNumPropertiesList =
                    new AtomicReference<>((ArrayList<WebElement>) driver.findElements(
                    By.xpath("//article//a[@title='" + brokerName + "']/" +
                            "parent::h3/parent::div/following-sibling::div[@class='position']/a")));
            actualNumPropertiesList.get().forEach(actualNumProperties -> {
                fluentWait.until(ExpectedConditions.visibilityOf(actualNumProperties));
            });

            ArrayList<WebElement> numberOfArticlesSameName =
                    (ArrayList<WebElement>) driver.findElements(
                            By.xpath("//article//h3/a[@title='" + brokerName + "']"));

            brokersList.forEach(broker -> {
                if(brokerName.equals(broker.getName())) {
                    Assert.assertEquals(actualName.get().getAttribute("title"), broker.getName());
                    final boolean[] flagForAddressMatch = new boolean[1];
                    actualAddresses[0].forEach(actAddress -> {
                        try {
                            if (actAddress.getText().equals(broker.getAddress())) {
                                flagForAddressMatch[0] = true;
                            }
                        } catch (StaleElementReferenceException sere) {
                            actualAddresses[0] = (ArrayList<WebElement>) driver.findElements(
                                    By.xpath("//article//a[@title='" + brokerName + "']" +
                                            "/parent::h3/following-sibling::div[@class='office']"));
                            actualAddresses[0].forEach(actAddressRetry -> {
                                fluentWait.until(ExpectedConditions.visibilityOf(actAddressRetry));
                                if (actAddressRetry.getText().equals(broker.getAddress())) {
                                    flagForAddressMatch[0] = true;
                                }
                            });
                        }
                    });
                    Assert.assertTrue(flagForAddressMatch[0]);

                    final boolean[] flagForNumPropertiesMatch = new boolean[1];
                    actualNumPropertiesList.get().forEach(actualNumProperties -> {
                        try {
                            if (actualNumProperties.getText().equals(broker.getNumProperties())) {
                                flagForNumPropertiesMatch[0] = true;
                            }
                        } catch (StaleElementReferenceException sere) {
                            actualNumPropertiesList.set((ArrayList<WebElement>) driver.findElements(
                                    By.xpath("//article//a[@title='" + brokerName + "']" +
                                            "/parent::h3/parent::div/following-sibling::div[@class='position']/a")));
                            actualNumPropertiesList.get().forEach(actualNumPropertiesRetry -> {
                                fluentWait.until(ExpectedConditions.visibilityOf(actualNumPropertiesRetry));
                                if (actualNumPropertiesRetry.getText().equals(broker.getNumProperties())) {
                                    flagForNumPropertiesMatch[0] = true;
                                }
                            });
                        }
                    });
                    Assert.assertTrue(flagForNumPropertiesMatch[0]);
                }
            });

            //check the listed articles are equal to the searched by single name
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ArrayList<WebElement> numberOfArticles =
                    (ArrayList<WebElement>) driver.findElements(By.xpath("//article//h3/a[@title]"));
            Assert.assertTrue(numberOfArticles.size() == numberOfArticlesSameName.size());

            WebElement clearBtn = driver.findElement(By.className("clear-btn"));

            fluentWait.until(ExpectedConditions.elementToBeClickable(clearBtn));
            clearBtn.click();

            WebElement brokersLoading = driver.findElement(By.cssSelector(".brokers-loading[style='display: block;']"));
            fluentWait.until((ExpectedConditions.invisibilityOf(brokersLoading)));
        });
    }


    @Test
    public void testYavlenaSiteLandlinePhones() {
        driver.get("https://www.yavlena.com/broker/");

        WebElement hideCookiesBtn = driver.findElement(By.className("hide-cookies-message"));
        WebElement loadMoreBtn = driver.findElement(By.className("load-more-results-list"));

        fluentWait.until(ExpectedConditions.elementToBeClickable(hideCookiesBtn));
        hideCookiesBtn.click();

        fluentWait.until(ExpectedConditions.elementToBeClickable(loadMoreBtn));
        js.executeScript("arguments[0].click();", loadMoreBtn);

        fluentWait.until(ExpectedConditions.invisibilityOf(loadMoreBtn));

        ArrayList<WebElement> articlesList = (ArrayList<WebElement>) driver.findElements(By.tagName("article"));
        ArrayList<Broker> brokersList = new ArrayList<>();
        final String[] brokerNames = new String[1];
        final String[] mobilePhones = new String[1];

        articlesList.forEach(webElement -> {
            Broker broker = new Broker();
            brokerNames[0] = webElement.findElement(By.cssSelector(".name a")).getAttribute("title");
            broker.setName(brokerNames[0]);
            broker.setAddress(webElement.findElement(By.className("office")).getText());
            broker.setLandlinePhone(webElement.findElement(
                    By.xpath("(//article//a[@title='" + brokerNames[0] + "']/parent::h3/parent::div/" +
                            "following-sibling::div[@class='tel-group']/span[@class='tel']/a)[1]")).getText());
//            try {
//                mobilePhones[0] = Arrays.toString(new String[]{webElement.findElement(
//                        By.xpath("(//article//a[@title='" + brokerNames[0] + "']/parent::h3/parent::div/" +
//                                "following-sibling::div[@class='tel-group']/span[@class='tel']/a)[2]")).getText()});
//            } catch (org.openqa.selenium.NoSuchElementException noSuchElementException) {
//                mobilePhones[0] = "";
//            }
            //     broker.setMobilePhone(mobilePhones[0]);
            broker.setNumProperties(webElement.findElement(By.cssSelector(".position a[title]")).getText());
            brokersList.add(broker);
        });

        ArrayList<Broker> brokersListOuter = new ArrayList<>(brokersList);
        final WebElement[] searchInput = new WebElement[1];

        brokersListOuter.forEach(brokerOuter -> {
            searchInput[0] = (driver.findElement(By.className("input-search")));

            fluentWait.until(ExpectedConditions.visibilityOf(searchInput[0]));

            String brokerName = brokerOuter.getName();
            searchInput[0].sendKeys(brokerName);

            AtomicReference<WebElement> actualName =
                    new AtomicReference<>(driver.findElement(
                            By.xpath("(//a[@title='" + brokerName + "'])[1]")));
            fluentWait.until(ExpectedConditions.visibilityOf(actualName.get()));

            AtomicReference<ArrayList<WebElement>> actualLandlinePhoneList =
                    new AtomicReference<>((ArrayList<WebElement>) driver.findElements(
                            By.xpath("(//article[1]//a[@title='" + brokerName + "']/parent::h3/" +
                                    "parent::div/following-sibling::div[@class='tel-group']/span[@class='tel']/a)[1]")));
            actualLandlinePhoneList.get().forEach(actualLandlinePhone -> {
                fluentWait.until(ExpectedConditions.visibilityOf(actualLandlinePhone));
            });

            brokersList.forEach(broker -> {
                if(brokerName.equals(broker.getName())) {

                    final boolean[] flagForLandlinePhoneMatch = new boolean[1];
                    actualLandlinePhoneList.get().forEach(actualLandlinePhone -> {
                        try {
                            if (actualLandlinePhone.getText().equals(broker.getLandlinePhone())) {
                                flagForLandlinePhoneMatch[0] = true;
                            }
                        } catch (StaleElementReferenceException sere) {
                            actualLandlinePhoneList.set((ArrayList<WebElement>) driver.findElements(
                                    By.xpath("(//article[1]//a[@title='" + brokerName + "']/parent::h3/" +
                                            "parent::div/following-sibling::div[@class='tel-group']/span[@class='tel']/a)[1]")));
                            actualLandlinePhoneList.get().forEach(actualLandlinePhoneRetry -> {
                                fluentWait.until(ExpectedConditions.visibilityOf(actualLandlinePhoneRetry));
                                if (actualLandlinePhoneRetry.getText().equals(broker.getLandlinePhone())) {
                                    flagForLandlinePhoneMatch[0] = true;
                                }
                            });
                        }
                    });
                    Assert.assertTrue(flagForLandlinePhoneMatch[0]);
                }
            });

            WebElement clearBtn = driver.findElement(By.className("clear-btn"));

            fluentWait.until(ExpectedConditions.elementToBeClickable(clearBtn));
            clearBtn.click();

            WebElement brokersLoading = driver.findElement(By.cssSelector(".brokers-loading[style='display: block;']"));
            fluentWait.until((ExpectedConditions.invisibilityOf(brokersLoading)));
        });

    }
}
