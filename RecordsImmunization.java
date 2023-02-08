package Stepdefinition.records;

import Actions.HomePage;
import Actions.ImmunizationsPage;
import Actions.LoginPage;
import Actions.RecordsPage;
import Reusable_Functions.Generic_functions;
import io.appium.java_client.MobileElement;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class RecordsImmunization extends Generic_functions {
    LoginPage login;
    HomePage home;
    ImmunizationsPage immunizationsPage;
    RecordsPage recordsPage;
    public static String immunizationCode, str;
    public List<MobileElement> immunizationList, historicalRecordsList;
    public ArrayList<String> immunizationData;

    public enum Timeout {SIMPLE, MEDIUM, COMPLEX}

    //Rohan logs in where no immunization data is available
    @Given("{string} logs into his account")
    public void logsIn(String username) {
        appLaunch();
        login = new LoginPage(driver);
        home = new HomePage(driver);
        immunizationsPage = new ImmunizationsPage(driver);
        recordsPage = new RecordsPage(driver);
        waitTillElementClickable("welcome_login", Timeout.COMPLEX);
        click("welcome_login");
        if (username.contains("Rohan"))
            login.login("login_email_id", 4, 0);
        else if (username.contains("Joe")) {
            login.login("login_email_id", 6, 0);
        } else {
            login.login("login_email_id", 5, 0);
        }
    }

    @And("he navigates to {string} screen")
    public void heNavigatesToScreen(String pageName) throws InterruptedException {
        if (pageName.contains("Records")) {
            home.navigateToRecords();
            Thread.sleep(3000);
        } else if (pageName.contains("Immunization")) {
            click("services_immunizations_tile");
            waitTillElementClickable("immunizationAddData", Timeout.COMPLEX);
        } else {
            click("immunizationAddData");
        }
    }

    @Then("he sees no Immunizations data")
    public void heSeesNoImmunizationsData() {
        waitTillElementVisible("services_immunizationsPage_title", Timeout.COMPLEX);
        validateText(getText("services_immunizationsPage_title"), tdReader("services_immunizationsPage_title", 0));
        Assert.assertTrue(isElementDisplayed("immunizationAddData"));
        if (platformName.equals("Android")) {
            Assert.assertTrue(isElementDisplayed("immunizationSearchIcon"));
        }
        Assert.assertTrue(isElementDisplayed("immunizationSearchBox"));
        if (platformName.equals("Android")) {
            validateText(getText("immunizationSearchBox"), tdReader("immunizationSearchBox", 1));
            String text = tdReader("immunizationDateFilter", 0) + " ";
            validateText(getText("immunizationDateFilter"), text);
            text = tdReader("immunizationStatusFilter", 0) + " ";
            validateText(getText("immunizationStatusFilter"), text);
            Assert.assertTrue(isElementDisplayed("immunizationNoDataImage"));
        } else {
            validateText(getText("immunizationSearchBox"), tdReader("immunizationSearchBox", 0));
            validateText(getText("immunizationDateFilter"), tdReader("immunizationDateFilter", 0));
            validateText(getText("immunizationStatusFilter"), tdReader("immunizationStatusFilter", 0));
            click("immunizationNoDataImage");
        }
        validateText(getText("immunizationNoDataText"), tdReader("immunizationNoDataText", 0));

        //Logging out
        login.logout();
    }

    //Joe logs in and verifies data
    @Then("he sees Immunizations data")
    public void heSeesImmunizationsData() {
        immunizationList = driver.findElements(By.xpath(ORReader("immunizationDataList")));
        immunizationData = new ArrayList<>();
        for (int i = 0; i < immunizationList.size(); i++) {
            immunizationData.add(immunizationList.get(i).getText());
        }
    }

    @And("he verifies number of immunization records")
    public void heVerifiesNumberOfImmunizationRecords() {
        Assert.assertEquals(immunizationData.size(), 2);
        //Logging out
        login.logout();
    }

    //Joe searches for existing immunization code
    @And("he searches for {string} immunization code")
    public void heSearchesForImmunizationCode(String code) {
        if (code.contains("non")) {
            enterValue("immunizationSearchBox", "immunizationCode", 1);
            pageImplicitWait(5000);
        } else {
            enterValue("immunizationSearchBox", "immunizationCode", 0);
            pageImplicitWait(5000);
        }
    }

    @Then("he should see the immunization records having the code")
    public void heShouldSeeTheImmunizationRecordsHavingTheCode() {
        waitTillElementVisible("conditionDataList", RecordsConditions.Timeout.COMPLEX);
        immunizationList = driver.findElements(By.xpath(ORReader("immunizationDataList")));
        immunizationData = new ArrayList<>();
        for (int i = 0; i < immunizationList.size(); i++) {
            immunizationData.add(immunizationList.get(i).getText());
        }
        Assert.assertEquals(immunizationData.size(), 2);
        if (platformName.equals("Android")) {
            for(String text: immunizationData){
                Assert.assertEquals(text,tdReader("immunizationSearchData", 0));
                Assert.assertTrue(text.contains(tdReader("immunizationCode", 0)));}
        } else {
            pageImplicitWait(2000);
            Assert.assertTrue(immunizationData.get(0).contains(tdReader("immunizationSearchData", 0)));
            Assert.assertTrue(immunizationData.get(0).contains(tdReader("immunizationCode", 0)));
        }

        if (platformName.equals("iOS")) {
            hideKeyboard("Search");
        }
        //Logging out
        login.logout();
    }

    //Joe searches for non existing immunization code
    @Then("she should not see any immunization data")
    public void sheShouldNotSeeAnyImmunizationData() {
        if (platformName.equals("Android")) {
            Assert.assertTrue(isElementDisplayed("immunizationNoDataImage"));
        }
        validateText(getText("immunizationNoDataText"), tdReader("immunizationNoDataText", 0));

        if (platformName.equals("iOS")) {
            hideKeyboard("Search");
        }
        //Logging out
        login.logout();
    }
}
