package com.ExtramarksWebsite_TestCases;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.ExtramarksWebsite_Pages.ChildSubscriptionPage;
import com.ExtramarksWebsite_Pages.DashBoardPage;
import com.ExtramarksWebsite_Pages.LoginPage;
import com.ExtramarksWebsite_Pages.ProfilePage;
import com.ExtramarksWebsite_Utils.Constants;
import com.ExtramarksWebsite_Utils.DataUtil;
import com.ExtramarksWebsite_Utils.ExtentManager;
import com.ExtramarksWebsite_Utils.Xls_Reader;
import com.relevantcodes.extentreports.LogStatus;

public class ParentChildSubscriptionTest extends BaseTest {
	@BeforeMethod(alwaysRun = true)
	public void init(Method method) {
		rep = ExtentManager.getInstance();
		String testMethodName = method.getName();
		test = rep.startTest(testMethodName);
	}

	@AfterMethod(alwaysRun = true)
	public void logOut(ITestResult itr) throws InterruptedException {

		try {
			logStatus(itr);
			DashBoardPage dp = new DashBoardPage(driver, test);
			Thread.sleep(1000);
			// JavascriptExecutor js = (JavascriptExecutor) driver;
			// js.executeScript("arguments[0].click();", dp.SettingsIcon.get(0));
			click(dp.SettingsIcon, 0, "Settingicon");
			click(dp.LogOut, 0, "Logout");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rep.endTest(test);
			rep.flush();
			if (driver != null) {
				driver.quit();
				driver = null;
			}
		}
	}

	@AfterTest(alwaysRun = true)
	public void quit() throws InterruptedException {
     System.out.println("Test Execution finished");
     test.log(LogStatus.INFO, "Test Execution finished");
     Reporter.log("Test Execution finished");
	}

	@DataProvider
	public Object[][] getData() {
		Xls_Reader xls = new Xls_Reader(Constants.XLS_FILE_PATH);
		Object[][] data = DataUtil.getData(xls, "ParentChildSubscriptionTest");
		return data;
	}

	@Test(dataProvider = "getData")
	public void parentChildSubscription(Hashtable<String, String> data) throws InterruptedException {
		SoftAssert sAssert=new SoftAssert();
		defaultLogin(data.get("Browser"),data.get("Username"),data.get("Password"));
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		Thread.sleep(5000);
		String expectedResult = "ParentChildSubscriptionTest_PASS";
		String actualResult = "";
		DashBoardPage dp = new DashBoardPage(driver, test);
		if (dp.ParentTab() == 0) {
			actualResult = "Login_Fail";
			test.log(LogStatus.FAIL, "Got actual result as " + actualResult);
			Assert.fail("got actual result as= " + actualResult);
		}else {
			
			System.out.println("Logged in but not on parent page");
			test.log(LogStatus.INFO, "Logged in but not on parent page");
		}

		Object resultPage = dp.openChildSubs();
		if (resultPage instanceof ChildSubscriptionPage) {
			test.log(LogStatus.INFO, "ChildSubscriptionPage opens");
			actualResult = "ParentChildSubscriptionTest_PASS";
			System.out.println("ChildSubscriptionPage opens");
			ChildSubscriptionPage csp = (ChildSubscriptionPage)resultPage;
					csp.openProgSubs();
		} else {
			actualResult = "ParentChildSubscriptionTest_Child_SubscriptionPage_not_Open_Fail";
			dp.takeScreenShot();
			test.log(LogStatus.INFO, "ChildSubscriptionPage not open");
			System.out.println("ChildSubscriptionPage not opens");
		}
		if (!expectedResult.equals(actualResult)) {
			// take screenshot
			dp.takeScreenShot();
			test.log(LogStatus.FAIL, "Got actual result as " + actualResult);
			Assert.fail("Got actual result as " + actualResult);
		}else {
			test.log(LogStatus.PASS, "ChildSubscription Test passed");
		}
		
		sAssert.assertAll();
	}
}
