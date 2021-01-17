package testing;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import java.io.IOException;
public class CountriesApiTest {
	static ExtentHtmlReporter htmlReporter;
	static ExtentReports extent;
	static ExtentTest logger;
	
	static RequestSpecification httpRequest = RestAssured.given().log().everything();
	static JsonPath jsonPathEvaluator;
	static Response response;
	static String capital =null;
	
	@BeforeClass
	public void setupReport() throws IOException {

		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/target/apiTesting.html");
		// Create an object of Extent Reports
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		htmlReporter.config().setDocumentTitle("Api testing");
		// Name of the report
		htmlReporter.config().setReportName("Api testing results");
		// Dark Theme
		htmlReporter.config().setTheme(Theme.DARK);
		
	}
	
	
	@Test	
	public static void verifyResponseWithCorrectCapital(){
		logger = extent.createTest("Test response code with valid capital");
		response = httpRequest.get("https://restcountries.eu/rest/v2/all?fields=name;capital;currencies;latlng");
		jsonPathEvaluator = response.jsonPath();
	    capital =jsonPathEvaluator.getString("capital[67]");
		RestAssured.baseURI = "https://restcountries.eu/rest/v2/capital";
		RequestSpecification httpRequest = RestAssured.given().log().everything();
		response = httpRequest.get("/"+capital+"?fields=name;capital;currencies;latlng;regionalBlocs");
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
		
	}
	
	@Test
	public static void verifyResponseWithNullCapital(){
		logger = extent.createTest("Test response code with invalid capital");
		response = httpRequest.get("https://restcountries.eu/rest/v2/all?fields=name;capital;currencies;latlng");
		jsonPathEvaluator = response.jsonPath();
		int capitalSize = jsonPathEvaluator.getList("capital").size();
	    capital =jsonPathEvaluator.getString("capital["+capitalSize + 1 +"]");
		RestAssured.baseURI = "https://restcountries.eu/rest/v2/capital";
		RequestSpecification httpRequest = RestAssured.given().log().everything();
		response = httpRequest.get("/"+capital+"?fields=name;capital;currencies;latlng;regionalBlocs");
		jsonPathEvaluator = response.jsonPath();
	    System.out.println(response.asString());  
	    int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 404);
	}
	
	@AfterMethod
	public void finalizeTest(ITestResult result){
		if (result.getStatus() == ITestResult.FAILURE) {
			logger.log(Status.FAIL,
					MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
			logger.log(Status.FAIL,
					MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));	
		} else if (result.getStatus() == ITestResult.SKIP) {
			logger.log(Status.SKIP,
					MarkupHelper.createLabel(result.getName() + " - Test Case Skipped", ExtentColor.ORANGE));
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			logger.log(Status.PASS,
					MarkupHelper.createLabel(result.getName() + " Test Case PASSED", ExtentColor.GREEN));
		}
		extent.flush();
	}
	
	
}
